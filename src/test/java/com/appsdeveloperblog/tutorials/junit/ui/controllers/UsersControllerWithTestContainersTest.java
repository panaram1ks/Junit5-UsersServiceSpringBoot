package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class UsersControllerWithTestContainersTest {

    @Container
    private static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.4.0")
            .withDatabaseName("photo_app")
            .withUsername("sergey")
            .withPassword("sergey");

    @DynamicPropertySource // allow dynamically override property files.
    private static void overrideProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }
}
