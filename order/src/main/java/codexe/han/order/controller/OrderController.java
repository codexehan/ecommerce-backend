package codexe.han.order.controller;

import codexe.han.common.response.CodexeApiResponse;
import codexe.han.order.client.InventoryClient;
import codexe.han.order.command.CustomHystrixCommand;
import codexe.han.order.dto.OrderProductDTO;
import codexe.han.order.service.ClientService;
import codexe.han.order.service.OrderService;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.util.concurrent.*;

@RestController
@AllArgsConstructor
@Slf4j
public class OrderController {

    private OrderService orderService;

    private RedisTemplate redisTemplate;
    //proceed to checkout
    //need to login
    @PostMapping("/proceed/checkout")
    public ResponseEntity proceedCheckout(@RequestBody OrderProductDTO orderProductDTO){
        /**
         * 1.need to check login status
         * 2.need to check product validating time
         * 4.need to check whether can be delivered to the address
         * 3.need to check inventory
         *      enough: go to order page
         *      not enough: return error to remind
         */
        this.orderService.proceedCheckout(orderProductDTO);
        return CodexeApiResponse.builder().build();
    }

    @PostMapping("/continue/checkout")
    public ResponseEntity continueCheckout(@RequestBody OrderProductDTO orderProductDTO){
        /**
         * 1.check inventory
         * 2.pre block the inventory
         *
         * @Transactional
         *      update db
         *      invalidate the redis
         * return true -> create order
         * 先更新数据库，再invalidate缓存。不能保证缓存和数据库的强一致性
         * 先invalidate缓存，再更新数据库。需要将read缓存和update数据库异步串行执行，来保证数据库的强一致性
         *
         *  NOTE: during peak load ->
         *      mq - update db  异步更新db
         *      sub quantity in redis 扣减redis
         *      先发送db 再减redis 防止少卖情况的发生
         *
         *  导向付款页面
         */
        orderProductDTO = OrderProductDTO.builder()
                .cartItemId(1L)
                .productId(1L)
                .inventoryId(2L)
                .customerId(2L)
                .quantity(2)
                .build();
        this.orderService.continueCheckout(orderProductDTO);
        return CodexeApiResponse.builder().build();
    }

    /**
     * 排队页面从缓存中读取order状态
     * 有可能有某个商品缺货之类的信息，需要返回
     * 扣减成功，导向付款页面
     * @param orderId
     * @return
     */
    @GetMapping("/queue/order/status")
    public ResponseEntity queueCheckOrderStatus(@RequestParam(value = "orderId") long orderId){
        this.redisTemplate.opsForValue().get("");
        return null;
    }

}