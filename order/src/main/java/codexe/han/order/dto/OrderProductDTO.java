package codexe.han.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductDTO {

    private long customerId;

    private long productId;

    private long cartItemId;//shopping bag item id

    private long inventoryId;

    private int quantity;
}
