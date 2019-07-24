package codexe.han.order.entity;

import codexe.han.common.dto.BaseEntity;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "order_t")
@Builder
@Data
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue
    private Long orderItemId;

    private long customerId;

    private Long productId;

    private Long inventoryId;

    private Integer amount;

}
