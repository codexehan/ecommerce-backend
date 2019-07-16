package codexe.han.inventory.service.impl;

import codexe.han.inventory.service.InventoryService;
import codexe.han.inventory.service.SeckillInventoryService;

public class SeckillInventoryServiceImpl implements SeckillInventoryService {

    @Override
    public boolean isPurchasable(long inventoryId, int amount) {
        return false;
    }

    @Override
    public boolean blockInventory(long inventoryId, int amount) {
        return false;
    }
}
