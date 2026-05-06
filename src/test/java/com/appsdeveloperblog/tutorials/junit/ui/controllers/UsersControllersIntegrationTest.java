package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest // позволяет использовать все слои spring application
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // используется по умолчанию (не поднимает тестовый сервер)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, // запускает embedded server
        properties = {"server.port=9999"}) // override value from default application.properties
public class UsersControllersIntegrationTest {

    @Test
    void contextLoads() {

    }

}