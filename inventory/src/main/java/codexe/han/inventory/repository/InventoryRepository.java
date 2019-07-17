package codexe.han.inventory.repository;

import codexe.han.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Modifying
    @Query("update Inventory inventory set inventory.amount = :amount where inventory = :version")
    int updateInventory(long amount, long version);

}
