package com.gzhennaxia.personal.integration.ib;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IBClientPortalApiClientTest {

    @Autowired
    private IBClientPortalApiClient ibClient;

    @Test
    void getAccountInfo() {
        Object accountInfo = ibClient.getAccountInfo();
        System.out.println(accountInfo);
    }
}