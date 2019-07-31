package codexe.han.order.service;

import codexe.han.order.common.BlockInventoryStatus;

public interface ClientService {
    BlockInventoryStatus blockInventorySync(long cartItemId, long orderId, long inventoryId, int amount);

    BlockInventoryStatus blockInventoryAsync(long cartItemId, long orderId, long inventoryId, int amount);
}
