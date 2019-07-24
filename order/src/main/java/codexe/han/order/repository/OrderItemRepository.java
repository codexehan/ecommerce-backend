package codexe.han.order.repository;

import codexe.han.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Modifying
    @Query(value = "insert into order_item(order_id, customer_id, product_id, inventory_id, cart_item_id, amount) values(?1,?2,?3,?4,?5,?6)",nativeQuery = true)
    void insert(long order_id, long customerId, long productId, long inventoryId, long cartItemId, long amount);

}
