package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.service.UsersService;
import com.appsdeveloperblog.tutorials.junit.shared.UserDto;
import com.appsdeveloperblog.tutorials.junit.ui.request.UserDetailsRequestModel;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(
        controllers = UsersController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
) // Говорит создать бины относящиеся только к web

//@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
//@MockBean({UsersServiceImpl.class})
public class UsersControllerWebLayerTest {

    @MockBean
    UsersService usersService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("user can be created")
    void testCreateUser_whenUserDetailsProvided_returnsCreatedUserDetails() throws Exception {
        // Arrange
        UserDetailsRequestModel userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("Sergey");
        userDetailsRequestModel.setLastName("Ivanov");
        userDetailsRequestModel.setEmail("test@email.com");
        userDetailsRequestModel.setPassword("12345678");
        userDetailsRequestModel.setRepeatPassword("12345678");

//        UserDto userDto = new UserDto();
//        userDto.setFirstName("Sergey");
//        userDto.setLastName("Sergeev");
//        userDto.setEmail("email@mail.ru");
//        userDto.setUserId(UUID.randomUUID().toString());

        UserDto userDto = new ModelMapper().map(userDetailsRequestModel, UserDto.class);
        userDto.setUserId(UUID.randomUUID().toString());

        ObjectMapper objectMapper = new ObjectMapper();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDetailsRequestModel));

        when(usersService.createUser(any(UserDto.class))).thenReturn(userDto);
        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        UserRest createUser = new ObjectMapper().readValue(responseBodyAsString, UserRest.class);
        // Assert
        Assertions.assertEquals(userDetailsRequestModel.getFirstName(), createUser.getFirstName(), "The userFirst name is most likely incorrect");
        Assertions.assertEquals(userDetailsRequestModel.getLastName(), createUser.getLastName(), "The userLast name is most likely incorrect");
        Assertions.assertEquals(userDetailsRequestModel.getEmail(), createUser.getEmail(), "The user email is most likely incorrect");

        Assertions.assertFalse(createUser.getUserId().isEmpty(), "userId should not be empty");
    }

    @Test
    @DisplayName("First name is not empty")
    void testCreateUser_whenFirstNameIsNotEmpty_returns400StatusCode() throws Exception {
        // Arrange
        UserDetailsRequestModel userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("");
        userDetailsRequestModel.setLastName("Ivanov");
        userDetailsRequestModel.setEmail("test@email.com");
        userDetailsRequestModel.setPassword("12345678");
        userDetailsRequestModel.setRepeatPassword("12345678");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));
        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        System.out.println(mvcResult.getResponse().getStatus());

        // Assertion
        Assertions.assertEquals(
                HttpStatus.BAD_REQUEST.value(),
                mvcResult.getResponse().getStatus(),
                "incorrect HTTP Status Code return"
        );
    }


}