package com.ttl.shopping_pay_service.service;

import com.ttl.shopping_common.pojo.Orders;
import com.ttl.shopping_common.pojo.Payment;
import com.ttl.shopping_common.service.OrdersService;
import com.ttl.shopping_common.service.PaymentService;
import com.ttl.shopping_pay_service.mapper.PaymentMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.UUID;

@DubboService
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentMapper paymentMapper;
    @DubboReference
    private OrdersService ordersService;

    @Override
    public Payment pay(String orderId, Long userId, Integer paymentType) {
        // 1.查询订单
        Orders orders = ordersService.findById(orderId);

        // 2.创建支付记录
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setTransactionId(UUID.randomUUID().toString().replace("-", ""));
        payment.setTradeType(paymentType == 1 ? "微信支付" : "支付宝支付");
        payment.setTradeState("SUCCESS");
        payment.setPayerTotal(orders.getPayment());
        payment.setContent("模拟支付");
        payment.setCreateTime(new Date());
        paymentMapper.insert(payment);

        // 3.修改订单状态为已付款
        orders.setStatus(2);
        orders.setPaymentTime(new Date());
        ordersService.update(orders);

        return payment;
    }
}
