package codexe.han.order.service;

import codexe.han.order.dto.OrderProductDTO;
import org.springframework.web.context.request.async.DeferredResult;

public interface OrderService {
    Object proceedCheckout(OrderProductDTO orderProductDTO);

    Object continueCheckout(OrderProductDTO orderProductDTO);

    void testHystrixCommand(DeferredResult deferredResult);

}

