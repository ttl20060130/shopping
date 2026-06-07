package com.ttl.shopping_user_service.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ttl.shopping_common.pojo.ShoppingUser;
import com.ttl.shopping_common.result.BaseResult;
import com.ttl.shopping_common.result.BusException;
import com.ttl.shopping_common.result.CodeEnum;
import com.ttl.shopping_common.service.MessageService;
import com.ttl.shopping_common.service.ShoppingUserService;
import com.ttl.shopping_common.util.Md5Util;
import com.ttl.shopping_user_service.mapper.ShoppingUserMapper;
import com.ttl.shopping_user_service.util.JwtUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@DubboService
public class ShoppingUserServiceImpl implements ShoppingUserService {
  @Autowired
  private ShoppingUserMapper shoppingUserMapper;
  @DubboReference
  private MessageService messageService;

  // 发送注册短信验证码
  @Override
  public void sendRegisterMessage(String phone) {
    BaseResult result = messageService.sendMessage(phone);
    if (200 != result.getCode()) {
      throw new BusException(CodeEnum.SYSTEM_ERROR);
    }
  }

  // 注册时校验验证码（阿里云验证）
  @Override
  public void registerCheckCode(String phone, String checkCode) {
    BaseResult result = messageService.checkCode(phone, checkCode);
    if (200 != result.getCode()) {
      throw new BusException(CodeEnum.REGISTER_CODE_ERROR);
    }
  }

  @Override
  public void register(ShoppingUser shoppingUser) {
    // 1.验证手机号是否存在
    String phone = shoppingUser.getPhone();
    QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
    queryWrapper.eq("phone", phone);
    List<ShoppingUser> shoppingUsers = shoppingUserMapper.selectList(queryWrapper);
    if (shoppingUsers != null && shoppingUsers.size() > 0) {
      throw new BusException(CodeEnum.REGISTER_REPEAT_PHONE_ERROR);
    }

    // 2.验证用户名是否存在
    String username = shoppingUser.getUsername();
    QueryWrapper<ShoppingUser> queryWrapper1 = new QueryWrapper();
    queryWrapper1.eq("username", username);
    List<ShoppingUser> shoppingUsers1 = shoppingUserMapper.selectList(queryWrapper1);
    if (shoppingUsers1 != null && shoppingUsers1.size() > 0) {
      throw new BusException(CodeEnum.REGISTER_REPEAT_NAME_ERROR);
    }

    // 3.新增用户
    shoppingUser.setStatus("Y");
    shoppingUser.setPassword(Md5Util.encode(shoppingUser.getPassword()));
    shoppingUserMapper.insert(shoppingUser);
  }

  @Override
  public String loginPassword(String username, String password) {
    // 1.验证用户名
    QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
    queryWrapper.eq("username",username);
    ShoppingUser shoppingUser = shoppingUserMapper.selectOne(queryWrapper);
    if (shoppingUser == null){
      throw new BusException(CodeEnum.LOGIN_NAME_PASSWORD_ERROR);
    }
    // 2.验证密码
    boolean verify = Md5Util.verify(password, shoppingUser.getPassword());
    if (!verify){
      throw new BusException(CodeEnum.LOGIN_NAME_PASSWORD_ERROR);
    }
    // 3.生成JWT令牌，返回令牌
    try {
      String sign = JwtUtils.sign(shoppingUser.getId(),shoppingUser.getUsername());
      return sign;
    } catch (Exception e) {
      throw new BusException(CodeEnum.SYSTEM_ERROR);
    }
  }

  // 发送登录短信验证码
  @Override
  public void sendLoginMessage(String phone) {
    // 1.判断该用户是否已经注册，状态是否正常
    checkPhone(phone);
    // 2.发送短信
    BaseResult result = messageService.sendMessage(phone);
    if (200 != result.getCode()) {
      throw new BusException(CodeEnum.SYSTEM_ERROR);
    }
  }

  // 手机号验证码登录
  @Override
  public String loginCheckCode(String phone, String checkCode) {
    // 1.阿里云校验验证码
    BaseResult result = messageService.checkCode(phone, checkCode);
    if (200 != result.getCode()) {
      throw new BusException(CodeEnum.LOGIN_CODE_ERROR);
    }
    // 2.登录成功，根据手机号查询用户
    QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
    queryWrapper.eq("phone",phone);
    ShoppingUser shoppingUser = shoppingUserMapper.selectOne(queryWrapper);
    // 3.生成JWT令牌，返回令牌
    try {
      String sign = JwtUtils.sign(shoppingUser.getId(),shoppingUser.getUsername());
      return sign;
    } catch (Exception e) {
      throw new BusException(CodeEnum.SYSTEM_ERROR);
    }
  }

  @Override
  public String getName(String token) {
    try {
      Map<String, Object> map = JwtUtils.verify(token);
      String username = (String) map.get("username");
      return username;
    } catch (Exception e) {
      throw new BusException(CodeEnum.SYSTEM_ERROR);
    }
  }

  @Override
  public ShoppingUser getLoginUser(String token) {
    try {
      // 从令牌中获取用户id
      Map<String, Object> map = JwtUtils.verify(token);
      Long userId = (Long) map.get("userId");
      // 根据id查询用户
      QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
      queryWrapper.eq("id", userId);
      ShoppingUser shoppingUser = shoppingUserMapper.selectOne(queryWrapper);
      return shoppingUser;
    } catch (Exception e) {
      throw new BusException(CodeEnum.SYSTEM_ERROR);
    }
  }

  @Override
  public void checkPhone(String phone) {
    // 1.判断手机号是否存在
    QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
    queryWrapper.eq("phone",phone);
    ShoppingUser shoppingUser = shoppingUserMapper.selectOne(queryWrapper);
    if (shoppingUser == null){
      throw new BusException(CodeEnum.LOGIN_NOPHONE_ERROR);
    }
    // 2.判断用户状态
    if(!"Y".equals(shoppingUser.getStatus())){
      throw new BusException(CodeEnum.LOGIN_NOPHONE_ERROR);
    }
  }
}
