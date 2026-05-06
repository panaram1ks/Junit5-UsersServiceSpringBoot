package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;

//@SpringBootTest // позволяет использовать все слои spring application
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // используется по умолчанию (не поднимает тестовый сервер)

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, // запускает embedded server
//        properties = {"server.port=9999"}) // override value from default application.properties

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@TestPropertySource(locations = "/application-test.properties")

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsersControllersIntegrationTest {

    @Value("${server.port}")
    private int serverPort;

    @LocalServerPort // pickup actual server port when embedded server is running
    private int localServerPort;

    //    @Test
//    void contextLoads() {
//        System.out.println(localServerPort);
//        Assertions.assertEquals(2222, serverPort, "Порт не совпадает с ожидаемым!");
//    }

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("User can be created")
    void testCreateUser_whenValidDetailsProvided_returnsUserDetails() throws JSONException {
        // Arrange
        JSONObject userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("firstName", "Sergey");
        userDetailsRequestJson.put("lastName", "Kargopolov");
        userDetailsRequestJson.put("email", "test3@test.com");
        userDetailsRequestJson.put("password", "12345678");
        userDetailsRequestJson.put("repeatPassword", "12345678");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), httpHeaders);

        // Act
        ResponseEntity<UserRest> response = testRestTemplate.postForEntity(
                "/users",
                request,
                UserRest.class
        );
        UserRest  userRest = response.getBody();

        // Assert
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        Assertions.assertEquals(userDetailsRequestJson.getString("firstName"), userRest.getFirstName(), "firstName не совпадает!");

    }

}