package com.ttl.shopping_common.service;

import com.ttl.shopping_common.pojo.ShoppingUser;

/**
 * 商城用户服务
 */
public interface ShoppingUserService {
  // 发送注册短信验证码
  void sendRegisterMessage(String phone);
  // 注册时校验验证码（阿里云验证）
  void registerCheckCode(String phone,String checkCode);
  // 用户注册
  void register(ShoppingUser shoppingUser);

  // 用户名密码登录
  String loginPassword(String username,String password);
  // 发送登录短信验证码
  void sendLoginMessage(String phone);
  // 手机号验证码登录
  String loginCheckCode(String phone, String checkCode);

  // 获取登录用户名
  String getName(String token);
  // 根据token获取用户
  ShoppingUser getLoginUser(String token);
  // 判断用户手机号是否存在，状态是否正常
    void checkPhone(String phone);
}
