package codexe.han.order.entity;

import codexe.han.common.dto.BaseEntity;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "order_t")
@Builder
@Data
public class Order extends BaseEntity {

    @Id
    private Long orderId;

    private Integer status;
    //private Long orderPrice;

    private Long customerId;

}
