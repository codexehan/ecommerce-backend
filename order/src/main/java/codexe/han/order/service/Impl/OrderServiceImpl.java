package codexe.han.order.service.Impl;

import codexe.han.common.response.CodexeApi;
import codexe.han.order.client.InventoryClient;
import codexe.han.order.dto.OrderProductDTO;
import codexe.han.order.repository.OrderItemRepository;
import codexe.han.order.repository.OrderRepository;
import codexe.han.order.service.OrderService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.SocketTimeoutException;


@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private InventoryClient inventoryClient;

    private OrderRepository orderRepository;

    private OrderItemRepository orderItemRepository;

    @Override
    public Object proceedCheckout(OrderProductDTO orderProductDTO) {
        ResponseEntity<CodexeApi> validateResponse = this.inventoryClient.checkInventory(orderProductDTO.getInventoryId(), orderProductDTO.getQuantity());
        if((boolean)validateResponse.getBody().getData()){
            //return the check out page
            return "continue check out page";
        }
        else{
            return "no enough inventory";
        }
    }

    @Override
    @HystrixCommand()
    @Transactional(rollbackFor = {Exception.class})
    public Object continueCheckout(OrderProductDTO orderProductDTO) {
        /**
         * 1.先减库存 后生成订单，对账系统校验扣减库存日志 =》一旦订单生成失败，对账系统也很难去校验，除非是将order_id一起记录
         * 2.先生成订单，后减库存，对账系统校验订单日志 =》一旦订单生成失败，直接不需要进行库存扣减，
         *      A.如果扣减库存超时，对账系统提取扫描订单，检测库存是否扣减成功
         *      B.库存不足，这个时候需要更新cart商品状态，并检测其他商品状态
         *      C.库存足够，这个时候需要从cart移除（更新状态）
         *  缺点就是需要对订单表进行多次更新
         */
        int PROCESSING = 1000;
        int TO_BE_PAIED = 2000;
        int EXPIRED = 3000;
        int PAIED = 4000;
        int DELIVERYING = 5000;
        int DELIVERED = 6000;
        int REFUND = 7000;
        int COMPLETED = 8000;
        int orderStatus = PROCESSING;//待处理; state machine 有利于解耦
        long orderId = 123L;//分布式订单id的生成
        try {
            ResponseEntity<CodexeApi> blockResponse = this.inventoryClient.blockInventory(orderProductDTO.getCartItemId(), orderId, orderProductDTO.getInventoryId(), orderProductDTO.getQuantity());
            if(!(boolean)blockResponse.getBody().getData()){
                return "no enough inventory";
            }
            else{
                /**
                 * 库存足够，生成订单
                 * orderId需要用算法生成，有一定的讲究，比如 snowflake算法
                 * status=1000表示初始状态，等待处理
                 *
                 * 对账系统会查看
                 */
                orderStatus = TO_BE_PAIED;//库存足够，下单成功，待付款
                this.orderRepository.insert(orderId, orderProductDTO.getCustomerId(), orderStatus);
                this.orderItemRepository.insert(orderId,
                        orderProductDTO.getCustomerId(),
                        orderProductDTO.getProductId(),
                        orderProductDTO.getInventoryId(),
                        orderProductDTO.getCartItemId(),
                        orderProductDTO.getQuantity());
                return "check put page";
            }
        }catch(Exception e){
            log.error("block");
            this.orderRepository.insert(orderId, orderProductDTO.getCustomerId(), orderStatus);
            this.orderItemRepository.insert(orderId,
                    orderProductDTO.getCustomerId(),
                    orderProductDTO.getProductId(),
                    orderProductDTO.getInventoryId(),
                    orderProductDTO.getCartItemId(),
                    orderProductDTO.getQuantity());
            /**
             * 更改cart 商品状态（下单成功，购物车移除）
             */
        }
        return null;
    }
}
