package codexe.han.inventory.service.impl;

import codexe.han.inventory.entity.Inventory;
import codexe.han.inventory.entity.InventoryLog;
import codexe.han.inventory.repository.InventoryLogRepository;
import codexe.han.inventory.repository.InventoryRepository;
import codexe.han.inventory.service.InventoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private InventoryRepository inventoryRepository;
    private InventoryLogRepository inventoryLogRepository;
    private RedisTemplate<String, Long> redisTemplate;

    private static final String inventoryKey = "inventory:";
    private static final String inventoryUpdate = "inventoryupdate:";

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean blockInventoryWeakConsistent(long cartItemId, long orderId, long inventoryId, int amount) {
       /* this.inventoryLogRepository.save(InventoryLog.builder()
                .orderId(orderId).inventoryId(inventoryId).amount(amount).build());//先从数据库查找，存在则执行更新*/
        /**
         * 1.先检测本地缓存，是否标记为已经没有库存
         * 2.尝试去数据库扣减库存（这边可能会考虑是否要在redis进行一次校验，但是我们在之前的结算页面已经校验过，所以基本可以不用再次校验）
          */
        //假设本地校验商品，还有库存
       int res = this.inventoryRepository.blockInventory(inventoryId, amount);
        if(res == 1){
            /**
             * 扣减库存成功，进行记录
             * 如果是重复扣减，会出现cartItemId重复报错
             * 水平切分表的时候，要注意inventory和 inventorylog的路由策略
             */
            this.inventoryLogRepository.insert(cartItemId, orderId, inventoryId, amount);//用于对账单
            this.redisTemplate.delete(inventoryKey+inventoryId);
            return true;
        }
        else{
            /**
             * 库存不足，需要返回给前端
             */
        }
        //add a transaction record
        return false;
    }

    @Override
    public boolean blockInventoryStrongConsistent(long orderId, long inventoryId, int amount) {
        return false;
    }


    @Override
    public boolean isPurchasable(long inventoryId, int amount) {
        /**
         * 这里的redis更新是要保证顺序的。
         * 但是，在这两步之间 就可能出现缓存不一致情况。A读数据库 C改了数据库 B读数据库；B先更新缓存，A后更新缓存
         * 保证顺序的基本思路，这种情况下，需要记录数据更新的时间戳，时间戳就是数据库中读取的时间戳。每次要做更新的时候需要对比时间戳。
         * 1.要么就是将请求放在队列中，然后去执行
         * 2.要么就添加分布式锁，每次获得锁以后进行更新
         * 3.要么就是用redis事务进行更新
         */
        Long remain = redisTemplate.opsForValue().get(inventoryKey+inventoryId);
        log.info("redis inventory {} remain {}", inventoryId, remain);
        if(remain==null){
            remain = 0L;
            log.info("inventory {} not in redis, load from db", inventoryId);
            Inventory inventoryDB = inventoryRepository.findOne(inventoryId);
            if(inventoryDB!=null){
                remain = inventoryDB.getAmount();
                /**
                 * update by optimistic watch
                 * redis集群环境下，需要保证inventoryKey和inventoryUpdate在同一个redis上
                 */
                redisTemplate.execute(new SessionCallback<Boolean>() {
                    @Override
                    public Boolean execute(RedisOperations operations) throws DataAccessException {
                        int retryLimit = 3;
                        long delay = 10;
                        List<Object> results = null;
                        int i = 0;
                        while (results == null && i < retryLimit) {
                            try {
                                Thread.sleep(delay << i);//exponential backoffs delay*2^n
                            } catch (InterruptedException e) {
                                new RuntimeException(e);
                            }
                            operations.watch(Arrays.asList(inventoryKey+inventoryId, inventoryUpdate+inventoryId));
                            Long updateTime = (Long) operations.opsForValue().get(inventoryUpdate+inventoryId);
                            if(updateTime == null || updateTime<inventoryDB.getLastModifiedDate().getTime()){
                                operations.multi();
                                operations.opsForValue().set(inventoryUpdate+inventoryId,inventoryDB.getLastModifiedDate().getTime());//更新成最新时间
                                operations.opsForValue().set(inventoryKey+inventoryId,inventoryDB.getAmount());//更新成最新时间
                                results = operations.exec();
                                log.info("update inventory {} to {}, update time {}",inventoryId, inventoryDB.getAmount(),inventoryDB.getLastModifiedDate().getTime());
                            }
                            else{
                                log.info("old inventory data, discard");
                                return false;
                            }
                            i++;
                        }
                        return true;
                    }
                });
            }
        }
        return Long.compare(remain,amount)>=0;
    }

    /**
     * 异步扣减库存(一个order里面的多个商品库存扣减)
     * 1.保持幂等的前提下,扣减库存
     * 2.扣减成功，或者扣减失败，更改order状态，将order状态写入redis还有对应的order_item状态
     * 3.如果商品没货了，需要更新所有线上信息，已经用户购物车信息（这个可以百分百异步来做）还可以将商品下架状态更新到本地缓存
     */
    @Override
    public void blockInventoryAsync() {
        log.info("get inventory info from mq");
        //扣减库存

        //更新order状态，写入redis

        //没有货 mq购物车 以及线上状态
    }

}
