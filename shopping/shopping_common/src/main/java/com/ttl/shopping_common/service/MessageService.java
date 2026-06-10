package com.ttl.shopping_common.service;

import com.ttl.shopping_common.result.BaseResult;

/**
 * 短信服务
 */
public interface MessageService {
  /**
   * 发送短信验证码（阿里云自动生成验证码，无需传入code）
   * @param phoneNumber 手机号
   * @return 返回结果
   */
  BaseResult sendMessage(String phoneNumber);

  /**
   * 校验短信验证码
   * @param phoneNumber 手机号
   * @param code 用户输入的验证码
   * @return 返回结果
   */
  BaseResult checkCode(String phoneNumber, String code);
}
