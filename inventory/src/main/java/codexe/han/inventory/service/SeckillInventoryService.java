package codexe.han.inventory.service;

public interface SeckillInventoryService {

    /**
     * 如果redis信息不存在，读取数据库信息
     * @param inventoryId
     * @param amount
     * @return
     */
    boolean isPurchasable(long inventoryId, int amount);

    /**
     * 秒杀系统中，因为流量太高，所以选择从redis中进行decr，判断返回值，如果是>0的话，就异步更新数据库，允许请求打到数据库上
     * 如何保证redis减库存成功，异步更新数据库同时成功，同时失败呢，数据库一致性。
     * 返回前端订单号，前端根据订单号，去redis轮序订单状态
     * @param inventoryId
     * @param amount
     * @return
     */
    boolean blockInventory(long inventoryId, int amount);

}
