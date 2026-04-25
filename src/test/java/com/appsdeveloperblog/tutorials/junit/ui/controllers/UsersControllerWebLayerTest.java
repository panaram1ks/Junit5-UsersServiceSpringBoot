package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.ui.request.UserDetailsRequestModel;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(
        controllers = UsersController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
) // Говорит создать бины относящиеся только к web

//@AutoConfigureMockMvc(addFilters = false)
public class UsersControllerWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("user can be created")
    void testCreateUser_whenUserDetailsProvided_returnsCreatedUserDetails() throws Exception {
        // Arrange
        UserDetailsRequestModel user = new UserDetailsRequestModel();
        user.setFirstName("Sergey");
        user.setLastName("Ivanov");
        user.setEmail("test@email.com");
        user.setPassword("12345678");
        user.setRepeatPassword("12345678");

        ObjectMapper objectMapper = new ObjectMapper();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user));
        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        UserRest createUser = new ObjectMapper().readValue(responseBodyAsString, UserRest.class);
        // Assert
        Assertions.assertEquals(user.getFirstName(), createUser.getFirstName(), "The userFirst name is most likely incorrect");
        Assertions.assertEquals(user.getLastName(), createUser.getLastName(), "The userLast name is most likely incorrect");
        Assertions.assertEquals(user.getEmail(), createUser.getEmail(), "The user email is most likely incorrect");

        Assertions.assertFalse(createUser.getUserId().isEmpty(), "userId should not be empty");
    }
}