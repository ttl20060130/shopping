package com.ttl.shopping_message_service.service;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dypnsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dypnsapi20170525.models.CheckSmsVerifyCodeRequest;
import com.aliyun.sdk.service.dypnsapi20170525.models.CheckSmsVerifyCodeResponse;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.ttl.shopping_common.result.BaseResult;
import com.ttl.shopping_common.service.MessageService;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@DubboService
@Service
public class MessageServiceImpl implements MessageService {
    @Value("${message.accessKeyId}")
    private String accessKeyId;
    @Value("${message.accessKeySecret}")
    private String accessKeySecret;

    @SneakyThrows
    @Override
    public BaseResult sendMessage(String phoneNumber) {
        // 配置 AK/SK
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                .build());

        // 创建 Client
        AsyncClient client = AsyncClient.builder()
                .region("ap-southeast-1")
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dypnsapi.aliyuncs.com")
                )
                .build();

        // 构建请求 — ##code## 会被阿里云自动替换为真实验证码
        SendSmsVerifyCodeRequest request = SendSmsVerifyCodeRequest.builder()
                .signName("速通互联验证码")
                .templateCode("100001")
                .phoneNumber(phoneNumber)
                .templateParam("{\"code\":\"##code##\",\"min\":\"5\"}")
                .build();

        // 同步获取返回结果
        CompletableFuture<SendSmsVerifyCodeResponse> response = client.sendSmsVerifyCode(request);
        SendSmsVerifyCodeResponse resp = response.get();
        client.close();

        if ("OK".equals(resp.getBody().getCode())) {
            return new BaseResult(200, resp.getBody().getCode(), resp.getBody().getMessage());
        } else {
            return new BaseResult(500, resp.getBody().getCode(), resp.getBody().getMessage());
        }
    }

    @SneakyThrows
    @Override
    public BaseResult checkCode(String phoneNumber, String code) {
        // 配置 AK/SK
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                .build());

        // 创建 Client
        AsyncClient client = AsyncClient.builder()
                .region("ap-southeast-1")
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dypnsapi.aliyuncs.com")
                )
                .build();

        // 构建校验请求
        CheckSmsVerifyCodeRequest request = CheckSmsVerifyCodeRequest.builder()
                .phoneNumber(phoneNumber)
                .verifyCode(code)
                .build();

        // 同步获取返回结果
        CompletableFuture<CheckSmsVerifyCodeResponse> response = client.checkSmsVerifyCode(request);
        CheckSmsVerifyCodeResponse resp = response.get();
        client.close();

        if ("OK".equals(resp.getBody().getCode())) {
            return new BaseResult(200, resp.getBody().getCode(), resp.getBody().getMessage());
        } else {
            return new BaseResult(500, resp.getBody().getCode(), resp.getBody().getMessage());
        }
    }
}
