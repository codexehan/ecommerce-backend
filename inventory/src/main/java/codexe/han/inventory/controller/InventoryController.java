package codexe.han.inventory.controller;

import codexe.han.common.response.CodexeApiResponse;
import codexe.han.inventory.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventoryController {

    private InventoryService inventoryService;
    @GetMapping("/check")
    public ResponseEntity checkInventory(@RequestParam(name = "inventory_id") long inventoryId,
                                         @RequestParam(name = "amount") int amount){
        boolean isPurchasable = inventoryService.isPurchasable(inventoryId, amount);
        return CodexeApiResponse.builder().data(isPurchasable).build();
    }


}
