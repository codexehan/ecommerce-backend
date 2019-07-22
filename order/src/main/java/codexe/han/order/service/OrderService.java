package codexe.han.order.service;

import codexe.han.order.dto.OrderProductDTO;

public interface OrderService {
    Object proceedCheckout(OrderProductDTO orderProductDTO);

    Object continueCheckout(OrderProductDTO orderProductDTO);

}

