package com.ttl.shopping_common.service;

import com.ttl.shopping_common.pojo.Payment;

/**
 * 支付服务
 */
public interface PaymentService {
    // 模拟支付，生成支付记录
    Payment pay(String orderId, Long userId, Integer paymentType);
}
