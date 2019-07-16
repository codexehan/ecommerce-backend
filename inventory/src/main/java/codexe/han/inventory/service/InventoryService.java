package codexe.han.inventory.service;

public interface InventoryService {
    /**
     * 日常业务，读多写少，我们可以先更新数据库库存，然后invalidate 缓存
     *  可能出现的不一致情况：缓存刚好失效，A读取数据，然后B更新数据，invaldiate缓存，A读到的旧数据刚到，写入
     *  解决方案：是否可以存储一个最后更新的时间？按照数据库的时间戳？
     * 写多读少的情况下，先invalidate 缓存，在更新数据库
     *  可能出现的不一会情况：更新没有完成，就有读请求，会读到脏数据
     *  解决方案：串行化读写请求
     * cache aside pattern 是先更新数据库 再删除缓存 facebook也是
     */
    boolean blockInventoryWeakConsistent(long inventoryId, int amount);

    /**
     * 流量不高的时候，为了保证缓存和数据库中库存的强一致性
     * 将对该inventoryId的读写路由到kafka topic的partition中，然后设置kafka参数max.in.flight.request.per.connection=1
     * 保证消息顺序。而且需要保证只有一个生产者
     */
    boolean blockInventoryStrongConsistent(long inventoryId, int amount);


    /**
     * 场景是 读多写少
     * 通过redis得到库存信息，有一个基本条件就是，redis库存只有可能比实际库存多，不可能少。
     * 而blockInventoryWeakConsistent如果可以保证 更新db+invalidate cache之间间隔足够小， 就可以保证库存最后是一致的
     * 这个也可以从前端操作流程保证，a更新库存的时间 < b用户在前端首先结算的时候会读取一遍库存，提交订单的时候，也会读去库存，校验库存。
     * @param inventoryId
     * @param amount
     * @return
     */
    boolean isPurchasable(long inventoryId, int amount);



}
