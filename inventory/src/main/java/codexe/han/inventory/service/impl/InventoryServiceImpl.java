package codexe.han.inventory.service.impl;

import codexe.han.inventory.entity.Inventory;
import codexe.han.inventory.repository.InventoryRepository;
import codexe.han.inventory.service.InventoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private InventoryRepository inventoryRepository;
    private RedisTemplate redisTemplate;

    private static final String inventoryKey = "inventory:";

    @Override
    public boolean blockInventoryWeakConsistent(long inventoryId, int amount) {
        return false;
    }

    @Override
    public boolean blockInventoryStrongConsistent(long inventoryId, int amount) {
        return false;
    }


    @Override
    public boolean isPurchasable(long inventoryId, int amount) {
        /**
         * 这里的redis更新是要保证顺序的。
         * 但是，在这两步之间 就可能出现缓存不一致情况。A读数据库 C改了数据库 B读数据库；B先更新缓存，A后更新缓存
         * 保证顺序的基本思路，这种情况下，需要记录数据更新的时间戳，时间戳就是数据库中读取的时间戳。每次要做更新的时候需要对比时间戳。
         * 1.要么就是放在队列中，每次去时间戳最大的那个进行一次set
         * 2.要么就添加分布式锁，每次获得锁以后进行更新
         * 3.要么就是用redis事务进行更新
         */
        Long remain = (Long) redisTemplate.opsForValue().get(inventoryKey+inventoryId);
        log.info("redis inventory {} remain {}", inventoryId, remain);
        if(remain==null){
            log.info("inventory {} not in redis, load from db", inventoryId);
            Inventory inventoryDB = inventoryRepository.findOne(inventoryId);
            if(inventoryDB!=null){
                remain = inventoryDB.getAmount();
                redisTemplate.opsForValue().set(inventoryKey+inventoryId, remain, 1, TimeUnit.DAYS);
            }
        }
        return remain>=amount;
    }

}
