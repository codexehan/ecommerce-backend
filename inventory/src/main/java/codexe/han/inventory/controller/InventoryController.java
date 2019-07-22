package codexe.han.inventory.controller;

import codexe.han.common.response.CodexeApiResponse;
import codexe.han.inventory.service.InventoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class InventoryController {

    private InventoryService inventoryService;
    @GetMapping("/check")
    public ResponseEntity checkInventory(@RequestParam(name = "inventory_id") long inventoryId,
                                         @RequestParam(name = "amount") int amount){
        boolean isPurchasable = inventoryService.isPurchasable(inventoryId, amount);
        return CodexeApiResponse.builder().data(isPurchasable).build();
    }

    @PostMapping("/block")
    public ResponseEntity blockInventory(@RequestParam(name = "cart_item_id") long cartItemId,
                                         @RequestParam(name = "order_id") long orderId,
                                         @RequestParam(name = "inventory_id") long inventoryId,
                                         @RequestParam(name = "amount") int amount){
        try {
            boolean res = this.inventoryService.blockInventoryWeakConsistent(cartItemId, orderId, inventoryId, amount);
            return CodexeApiResponse.builder().data(res).msg("block inventory successfully").build();
        }catch(Exception e){
            log.error("block inventory error", e);
        }
        return CodexeApiResponse.builder().data(false).msg("block inventory error").build();
    }


}
