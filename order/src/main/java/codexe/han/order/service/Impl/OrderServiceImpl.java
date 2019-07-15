package codexe.han.order.service.Impl;

import codexe.han.common.response.CodexeApi;
import codexe.han.order.client.InventoryClient;
import codexe.han.order.dto.OrderProductDTO;
import codexe.han.order.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@NoArgsConstructor
public class OrderServiceImpl implements OrderService {

    private InventoryClient inventoryClient;

    @Override
    public Object proceedCheckout(OrderProductDTO orderProductDTO) {
        ResponseEntity<CodexeApi> validateResponse = this.inventoryClient.checkInventory(orderProductDTO.getInventoryId(), orderProductDTO.getQuantity());
        if((boolean)validateResponse.getBody().getData()){
            //return the check out page
            return "check out page";
        }
        else{
            return "no enough inventory";
        }
    }
}
