package codexe.han.inventory.entity;

import codexe.han.common.dto.BaseEntity;
import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "inventory_id")
    private long inventoryId;

    private long amount;
}

