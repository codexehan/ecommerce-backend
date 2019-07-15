package codexe.han.order.client;

import codexe.han.common.response.CodexeApi;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory")
public interface InventoryClient {
    @GetMapping("/check")
    ResponseEntity<CodexeApi> checkInventory(@RequestParam(name = "inventory_id") long inventoryId,
                                             @RequestParam(name = "amount") int amount);
}
