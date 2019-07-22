package codexe.han.order.entity;

import codexe.han.common.dto.BaseEntity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue
    private Long orderItemId;

    private long customerId;

    private Long productId;

    private Long inventoryId;

    private Integer amount;

}
