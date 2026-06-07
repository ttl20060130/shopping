package com.ttl.shopping_order_customer_api.controller;

import com.ttl.shopping_common.pojo.Payment;
import com.ttl.shopping_common.result.BaseResult;
import com.ttl.shopping_common.service.PaymentService;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * 支付
 */
@RestController
@RequestMapping("/user/payment")
public class PaymentController {
    @DubboReference
    private PaymentService paymentService;

    /**
     * 模拟支付
     * @param orderId 订单id
     * @param paymentType 支付方式：1.微信支付 2.支付宝支付
     * @param userId 用户id
     * @return 支付记录
     */
    @PostMapping("/pay")
    @GlobalTransactional
    public BaseResult<Payment> pay(String orderId, Integer paymentType, @RequestHeader Long userId) {
        Payment payment = paymentService.pay(orderId, userId, paymentType);
        return BaseResult.ok(payment);
    }
}
