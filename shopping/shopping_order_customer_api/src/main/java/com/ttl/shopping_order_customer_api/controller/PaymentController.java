package com.ttl.shopping_order_customer_api.controller;

import com.alibaba.fastjson2.JSON;
import com.ttl.shopping_common.pojo.Orders;
import com.ttl.shopping_common.pojo.Payment;
import com.ttl.shopping_common.result.BaseResult;
import com.ttl.shopping_common.service.OrdersService;
import com.ttl.shopping_common.service.ZfbPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝支付
 */
@RestController
@RequestMapping("/user/payment")
public class PaymentController {
    @DubboReference
    private ZfbPayService zfbPayService;
    @DubboReference
    private OrdersService ordersService;

    /**
     * 生成支付二维码
     * @param orderId 订单id
     * @return 二维码字符串
     */
    @PostMapping("/pcPay")
    public BaseResult<String> pcPay(String orderId){
        Orders orders = ordersService.findById(orderId);
        String codeUrl = zfbPayService.pcPay(orders);
        return BaseResult.ok(codeUrl);
    }

    /**
     * 支付成功回调，用户扫码支付后支付宝调用
     */
    @PostMapping("/success/notify")
    @Transactional
    public BaseResult successNotify(HttpServletRequest request){
        // 1.验签
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String[]> newMap = new HashMap<>();
        newMap.putAll(parameterMap);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("requestParameterMap", newMap);
        zfbPayService.checkSign(paramMap);

        String trade_status = request.getParameter("trade_status");
        String out_trade_no = request.getParameter("out_trade_no");
        // 2.支付成功，修改订单状态
        if ("TRADE_SUCCESS".equals(trade_status)){
            Orders orders = ordersService.findById(out_trade_no);
            orders.setStatus(2); // 已付款
            orders.setPaymentType(2); // 支付宝支付
            orders.setPaymentTime(new Date());
            ordersService.update(orders);

            // 3.添加交易记录
            Payment payment = new Payment();
            payment.setOrderId(out_trade_no);
            payment.setTransactionId(out_trade_no);
            payment.setTradeType("支付宝支付");
            payment.setTradeState(trade_status);
            payment.setPayerTotal(orders.getPayment());
            payment.setContent(JSON.toJSONString(request.getParameterMap()));
            payment.setCreateTime(new Date());
            zfbPayService.addPayment(payment);
        }
        return BaseResult.ok();
    }
}
