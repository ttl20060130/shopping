package com.ttl.shopping_user_service.util;

import lombok.SneakyThrows;
import org.jose4j.json.JsonUtil;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
  // 私钥
  public final static String PRIVATE_JSON = "{\"kty\":\"RSA\",\"n\":\"lruaP84ajsTrIO6ZrEEivHYFHz2HN6OctHKGiPlL0cgCnnZnOlBNQhLN27oeBPw3uyrVM0wBsrX5xMotIVWcbdD4FZbKlhFP6Q4sIST3q5PAykYjGqgR8PTrMySvbW8u0fH6Nlb6gJhG04XK7omh-nZHXenek0yfW0qBYdQ8MuJgm2E-f4HSNAX2JBFlDyiOnbPbrjd-ZJ2a9W_TchPgA3KiLmQI77e5pkU2aAWFxz5K9fGQF5Gljs67Qa3OnGqTIjI2cG6YG-0ErBqHGHZyL4ieATDdsBB-3-G6Fcu1aJTBZa2q2v_618E82HBt8f6fLiFRrpLje8Rz-HmGP7oS8Q\",\"e\":\"AQAB\",\"d\":\"I-5jljsk1Iy3ehm2kzvRsng1v1006vv7An7ceDFpNABIM4sJD3sN96aw4OSMfGa9SEYtHXXMX9QGTrVdLbmRORJb2RWhhEVrNaSVu27rBUEK74zpmFShNj3EBWSE1wLud8iFe0efnmDuXUTi-5refGyOlaIG1zkoMaRglDf6A6zaT84_u9htfLdZ1ZcXIkszwfz2kbbloHZjB3eF4Js5Z_sfmfsBN0vd1RVeal8SOlxwHAJMi2CQYul7_o6BRvr7hox-XQAFjQR0KuKnx2rW51N2kvXYWp_w4l8a0lx8EoohHPljEqbVEfhHFtEAYCMPH-xjefEzow4NdeUiY2Mg8w\",\"p\":\"unj31fN1cpWhiS5JUZvT-JT0OzWmgHI6QVgLssmQbWnzApMB6ffJB8uvu7PdLGvhwAm0DKEk_w5QytEgauhgNk6SjTOOwCjjTriic61b6HAW-uohGFVPRPuPqABT5-0sX0kZJNunISr_vmGsOsrFJq_bv_ysz62yuxLT42_gGIs\",\"q\":\"zu88PweuyLhcCwSpbegUX87G-a1ELJGupY-eCEGnKX7aeDQhIvhXOQ-IsqCZs65ZTHEFyCC6kpnLhfE8iEt7O8psGh38zEHCWifki14EWoCi2x4Aro-rqURBmVK9oIyvI1-u8jKbIWsv8FlzAlfpBCo3Y11Q90vVXKQoBQqKNfM\",\"dp\":\"TXDJdDgMcY7ErCTl3Bd7-4NYvnI0eKl3mgWHrjNZYKccfdgxdkoqWat0it5Snw0ajtc4x_nwVGBMmz4oWV2x_zu4Tx23TgRtChdRl_rJ7YkHciLjpahgKETUzy97qRjtUZEY-R6jlPm3hggoL65gHAXynBPCbOk1Dc3u40hl56s\",\"dq\":\"HvwZZEKtPCbGuWugRvGqI1Jd3sK_44CIywd_HTNfaUoyQi9kCs1umf-mc0Cy4wGnI1gFCptD9HGM1OhzLOFIeYJsv1hgIuFxzXgNs4SHWFnMiMysQ3cFuBmdTOzjsed-zegk1OUY718SVYW_P8mT_Wio1HsDhZPYRb_asi6K1Vs\",\"qi\":\"O5kDBvrOB2cS9JICkpv7h5xTvFGysgX3q2sbacsRGU1q-QuKcjHvHoANNBwqrf-rhGHJKbvHbG6T26779WMbCGcsyNJrhnj2Dod1FHA77d6S5Avi4pBNyAgyMxAnm4l-h9uGebsqhSdsbXpThmW6zRsf1cmCEWCSQpX6d9KDem0\"}";
  // 公钥
  public final static String PUBLIC_JSON = "{\"kty\":\"RSA\",\"n\":\"lruaP84ajsTrIO6ZrEEivHYFHz2HN6OctHKGiPlL0cgCnnZnOlBNQhLN27oeBPw3uyrVM0wBsrX5xMotIVWcbdD4FZbKlhFP6Q4sIST3q5PAykYjGqgR8PTrMySvbW8u0fH6Nlb6gJhG04XK7omh-nZHXenek0yfW0qBYdQ8MuJgm2E-f4HSNAX2JBFlDyiOnbPbrjd-ZJ2a9W_TchPgA3KiLmQI77e5pkU2aAWFxz5K9fGQF5Gljs67Qa3OnGqTIjI2cG6YG-0ErBqHGHZyL4ieATDdsBB-3-G6Fcu1aJTBZa2q2v_618E82HBt8f6fLiFRrpLje8Rz-HmGP7oS8Q\",\"e\":\"AQAB\"}";


  /**
   * 生成token
   *
   * @param userId    用户id
   * @param username 用户名字
   * @return
   */
  @SneakyThrows
  public static String sign(Long userId, String username) throws JoseException {
    // 1、 创建jwtclaims  jwt内容载荷部分
    JwtClaims claims = new JwtClaims();
    // 是谁创建了令牌并且签署了它
    claims.setIssuer("ttl");
    // 令牌将被发送给谁
    claims.setAudience("audience");
    // 失效时间长 （分钟）
    claims.setExpirationTimeMinutesInTheFuture(60 * 24);
    // 令牌唯一标识符
    claims.setGeneratedJwtId();
    // 当令牌被发布或者创建现在
    claims.setIssuedAtToNow();
    // 再次之前令牌无效
    claims.setNotBeforeMinutesInThePast(2);
    // 主题
    claims.setSubject("shopping");
    // 可以添加关于这个主题得声明属性
    claims.setClaim("userId", userId);
    claims.setClaim("username", username);


    // 2、签名
    JsonWebSignature jws = new JsonWebSignature();
    //赋值载荷
    jws.setPayload(claims.toJson());


    // 3、jwt使用私钥签署
    PrivateKey privateKey = new RsaJsonWebKey(JsonUtil.parseJson(PRIVATE_JSON)).getPrivateKey();
    jws.setKey(privateKey);


    // 4、设置关键 kid
    jws.setKeyIdHeaderValue("keyId");


    // 5、设置签名算法
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
    // 6、生成jwt
    String jwt = jws.getCompactSerialization();
    return jwt;
   }




  /**
   * 解密token，获取token中的信息
   *
   * @param token
   */
  @SneakyThrows
  public static Map<String, Object> verify(String token) throws JoseException {
    // 1、引入公钥
    PublicKey publicKey = new RsaJsonWebKey(JsonUtil.parseJson(PUBLIC_JSON)).getPublicKey();
    // 2、使用jwtcoonsumer  验证和处理jwt
    JwtConsumer jwtConsumer = new JwtConsumerBuilder()
         .setRequireExpirationTime() //过期时间
         .setAllowedClockSkewInSeconds(30) //允许在验证得时候留有一些余地 计算时钟偏差  秒
         .setRequireSubject() // 主题生命
         .setExpectedIssuer("ttl") // jwt需要知道谁发布得 用来验证发布人
         .setExpectedAudience("audience") //jwt目的是谁 用来验证观众
         .setVerificationKey(publicKey) // 用公钥验证签名  验证密钥
         .setJwsAlgorithmConstraints(new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST, AlgorithmIdentifiers.RSA_USING_SHA256))
         .build();
    // 3、验证jwt 并将其处理为 claims
    try {
      JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
      return jwtClaims.getClaimsMap();
     }catch (Exception e){
      return new HashMap();
     }
   }


}
