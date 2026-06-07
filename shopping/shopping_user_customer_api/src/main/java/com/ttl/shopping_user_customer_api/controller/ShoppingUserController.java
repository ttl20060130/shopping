package com.ttl.shopping_user_customer_api.controller;

import com.ttl.shopping_common.pojo.ShoppingUser;
import com.ttl.shopping_common.result.BaseResult;
import com.ttl.shopping_common.service.ShoppingUserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * 商城用户
 */
@RestController
@RequestMapping("/user/shoppingUser")
public class ShoppingUserController {
  @DubboReference
  private ShoppingUserService shoppingUserService;

  /**
   * 发送注册短信验证码
   * @param phone 注册手机号
   * @return 操作结果
   */
  @GetMapping("/sendMessage")
  public BaseResult sendMessage(String phone){
    // 阿里云自动生成验证码并发送短信
    shoppingUserService.sendRegisterMessage(phone);
    return BaseResult.ok();
  }

  /**
   * 验证用户注册验证码
   * @param phone 手机号
   * @param checkCode 验证码
   * @return 200验证成功，605验证码不正确
   */
  @GetMapping("/registerCheckCode")
  public BaseResult registerCheckCode(String phone,String checkCode){
    shoppingUserService.registerCheckCode(phone,checkCode);
    return BaseResult.ok();
  }

  /**
   * 用户注册
   * @param shoppingUser 用户信息
   * @return 注册结果
   */
  @PostMapping("/register")
  public BaseResult register(@RequestBody ShoppingUser shoppingUser){
    shoppingUserService.register(shoppingUser);
    return BaseResult.ok();
  }

  /**
   * 用户名密码登录
   * @param shoppingUser 用户对象
   * @return 登录结果
   */
  @PostMapping("/loginPassword")
  public BaseResult loginPassword(@RequestBody ShoppingUser shoppingUser){
    String sign = shoppingUserService.loginPassword(shoppingUser.getUsername(), shoppingUser.getPassword());
    return BaseResult.ok(sign);
  }

  /**
   * 发送登录短信验证码
   * @param phone 手机号
   * @return 操作结果
   */
  @GetMapping("/sendLoginCheckCode")
  public BaseResult sendLoginCheckCode(String phone){
    // 检查手机号 + 发送短信（阿里云自动生成验证码）
    shoppingUserService.sendLoginMessage(phone);
    return BaseResult.ok();
  }

  /**
   * 手机号验证码登录
   * @param phone 手机号
   * @param checkCode 验证码
   * @return 登录结果
   */
  @PostMapping("/loginCheckCode")
  public BaseResult loginCheckCode(String phone, String checkCode){
    String sign = shoppingUserService.loginCheckCode(phone, checkCode);
    return BaseResult.ok(sign);
  }

  /**
   * 获取登录的用户名
   * @param authorization 令牌
   * @return 用户名
   */
  @GetMapping("/getName")
  public BaseResult<String> getName(@RequestHeader("Authorization") String authorization){
    String token = authorization.replace("Bearer ","");
    String name = shoppingUserService.getName(token);
    return BaseResult.ok(name);
  }
}
