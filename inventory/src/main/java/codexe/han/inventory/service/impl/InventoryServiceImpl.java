package codexe.han.inventory.service.impl;

import codexe.han.inventory.service.InventoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private InventoryRepository inventoryRepository;
    private RedisTemplate redisTemplate;

    private static final String inventoryKey = "inventory:";

    @Override
    public boolean blockInventoryWeakConsistent(long inventoryId, int amount) {

    }

    @Override
    public boolean blockInventoryStrongConsistent(long inventoryId, int amount) {
        return false;
    }

    @Override
    public boolean isPurchasable(long inventoryId, int amount) {
        long remain = (long) redisTemplate.opsForValue().get(inventoryKey+inventoryId);
        log.info("inventory {} remain {}", inventoryId, remain);
        return remain>=amount;
    }

}
