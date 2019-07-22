package codexe.han.inventory.repository;

import codexe.han.inventory.entity.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {

    @Modifying
    @Transactional(rollbackFor={Exception.class})
    @Query(value = "insert into inventory_log(cart_item_id, order_id, inventory_id, amount) values(?1,?2,?3)",nativeQuery = true)
    void insert(long cartItemId, long orderId, long inventoryId, long amount);
}
