package codexe.han.inventory.repository;

import codexe.han.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Modifying
    @Query("update Inventory inventory set inventory.amount = inventory.amount - ?2 where inventory.amount> ?2 and inventory.inventoryId =?1")
    int blockInventory(long inventoryId, long amount);

}
