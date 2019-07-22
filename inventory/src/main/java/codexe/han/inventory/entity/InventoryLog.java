package codexe.han.inventory.entity;

import codexe.han.common.dto.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryLog extends BaseEntity {
    @Id
    @Column(name = "order_id")
    private long orderId;

    private long inventoryId;

    private long amount;

    private long version;
}

