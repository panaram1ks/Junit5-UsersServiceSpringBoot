package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.security.SecurityConstants;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;

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

    @Test
    @DisplayName("GET /users requires JWT")
    void getUsers_whenMissingJWT_returns403(){
        //Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity requestEntity = new HttpEntity<>(null, headers);

        //Act
//        ResponseEntity<UserRest> response = testRestTemplate.getForEntity(
//                "/users",
//                UserRest.class);

        ResponseEntity<List<UserRest>> response = testRestTemplate.exchange("/users",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<UserRest>>() {
                }
        );

        //Assert
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatusCode().value(), "Status code is not 403!");
    }

    @Test
    @DisplayName("/login works")
    void testUserLogin_whenValidCredentialsProvided_returnsJWTinAuthorizationHeader() throws JSONException {
        //Arrange
        String loginCredentialsJson = " {\n" +
                "  \"email\":\"test3@test.com\",\n" +
                "  \"password\":\"12345678\",\n" +
                "}";
        JSONObject loginCredentials = new JSONObject();
        loginCredentials.put("email", "test3@test.com");
        loginCredentials.put("password", "12345678");

        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString());

        //Act
        ResponseEntity<Object> response = testRestTemplate.postForEntity("/users/login", request, null);

        //Assert
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), "HTTP Status code should be 200");
        Assertions.assertNotNull(
                response.getHeaders().getValuesAsList(SecurityConstants.HEADER_STRING).get(0),
                "Response should contain Authorization header with JWT"
        );
        Assertions.assertNotNull(
                response.getHeaders().getValuesAsList("UserID").get(0),
                "Response should contain UserID in response header"
        );
    }

}