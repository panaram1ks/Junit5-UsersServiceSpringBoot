package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

//@SpringBootTest // позволяет использовать все слои spring application
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // используется по умолчанию (не поднимает тестовый сервер)

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, // запускает embedded server
//        properties = {"server.port=9999"}) // override value from default application.properties

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "/application-test.properties")
public class UsersControllersIntegrationTest {

    @Value("${server.port}")
    private int serverPort;

    @Test
    void contextLoads() {
        Assertions.assertEquals(2222, serverPort, "Порт не совпадает с ожидаемым!");
    }

}