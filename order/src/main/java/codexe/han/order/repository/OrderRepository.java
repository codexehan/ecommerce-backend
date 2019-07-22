package codexe.han.order.repository;

import codexe.han.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    @Modifying
    @Query(value = "insert into order(order_id, customer_id, status) values(?1,?2,?3)",nativeQuery = true)
    void insert(long orderId, long customerId, int status);

}
