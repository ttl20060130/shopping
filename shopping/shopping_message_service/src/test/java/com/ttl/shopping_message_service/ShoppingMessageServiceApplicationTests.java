package com.ttl.shopping_message_service;

import com.ttl.shopping_common.result.BaseResult;
import com.ttl.shopping_common.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShoppingMessageServiceApplicationTests {

    @Autowired
    private MessageService messageService;

    @Test
    void contextLoads() {

        BaseResult baseResult = messageService.sendMessage("13116333753");
        System.out.println(baseResult.getMessage());
    }


}
