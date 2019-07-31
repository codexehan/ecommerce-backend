package codexe.han.payment.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PaymentController {

    /**
     * 可靠消息最终一致性原则
     * 付款成功
     * 1.修改订单状态
     * 2.积分系统更新
     * 3.物流系统更新
     * @return
     */
    @PostMapping("/pay")
    public ResponseEntity pay(){
        return null;
    }
}
