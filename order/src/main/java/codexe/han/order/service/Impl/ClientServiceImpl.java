package codexe.han.order.service.Impl;

import codexe.han.common.response.CodexeApi;
import codexe.han.order.client.InventoryClient;
import codexe.han.order.common.BlockInventoryStatus;
import codexe.han.order.service.ClientService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {

    private InventoryClient inventoryClient;

    @Override
    @HystrixCommand(fallbackMethod = "blockInventoryAsync")
    public BlockInventoryStatus blockInventorySync(long cartItemId, long orderId, long inventoryId, int amount) {
        ResponseEntity<CodexeApi> response = this.inventoryClient.blockInventory(cartItemId, orderId, inventoryId, amount);
        if((boolean)response.getBody().getData()){
            return BlockInventoryStatus.SUCCESS;//扣减成功
        }
        else{
            return BlockInventoryStatus.FAILED;//扣减失败
        }
    }

    public BlockInventoryStatus blockInventoryAsync(long cartItemId, long orderId, long inventoryId, int amount){
        /**
         * TODO：
         * mq异步减库存
         * 用户导向排队页面
         */
        try {
            log.info("send block inventory message to mq");
            return BlockInventoryStatus.ASYNC;//转换成异步或者需要扩容了。。。
        }catch(Exception e){
            return BlockInventoryStatus.SYSTEM_ISSUE;//MQ出现问题系统繁忙，请稍后重试，定时任务可以帮忙兜底
        }
    }
}
