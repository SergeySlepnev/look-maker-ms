package com.sspdev.auth.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sspdev.auth.domain.exception.DomainExceptionCode;
import com.sspdev.auth.domain.exception.UserByEmailAlreadyExistsException;
import com.sspdev.auth.domain.exception.UserByPhoneAlreadyExistsException;
import com.sspdev.auth.domain.port.in.RegisterUserUseCase;
import com.sspdev.auth.domain.port.out.MessageResolver;
import com.sspdev.auth.presentation.exception.ApiExceptionHandler;
import com.sspdev.auth.presentation.mapper.UserIdentityRequestMapper;
import com.sspdev.auth.testutil.TestDataUtil;

@ExtendWith(MockitoExtension.class)
class RegisterControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserIdentityRequestMapper requestMapper;
    @Mock
    private RegisterUserUseCase registerUserUseCase;
    @Mock
    private MessageResolver messageResolver;
    @InjectMocks
    private RegisterController registerController;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(registerController)
                .setControllerAdvice(new ApiExceptionHandler(messageResolver))
                .alwaysDo(print())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void post_register_shouldReturnUserIdentityResponseDto_whenRequestDtoValid() throws Exception {
        var userCommand = TestDataUtil.getValidRegisterUserCommand();
        var userResponse = TestDataUtil.getRegisterUserResponse();
        var requestDto = TestDataUtil.getValidUserIdentityRequestDto();

        when(requestMapper.toRegisterUserCommand(requestDto)).thenReturn(userCommand);
        when(registerUserUseCase.register(userCommand)).thenReturn(userResponse);
        when(messageResolver.resolve(any(String.class), any(Object[].class), any(Locale.class))).thenReturn("Some message");

        var requestDtoAsString = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDtoAsString)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userResponse.id().toString()))
                .andExpect(jsonPath("$.message").value("Some message"));
    }

    @Test
    void post_register_shouldReturnBadRequest_whenRequestDtoNotValid() throws Exception {
        var requestDto = TestDataUtil.getUserIdentityRequestDtoWithNullFields();

        var requestDtoAsString = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDtoAsString)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void post_register_shouldThrowUserByEmailExist_whenEmailInDb() throws Exception {
        var requestDto = TestDataUtil.getValidUserIdentityRequestDto();
        var userCommand = TestDataUtil.getValidRegisterUserCommand();

        when(requestMapper.toRegisterUserCommand(requestDto)).thenReturn(userCommand);
        when(registerUserUseCase.register(userCommand)).thenThrow(new UserByEmailAlreadyExistsException(requestDto.email()));

        var requestAsString = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAsString))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value(DomainExceptionCode.USER_BY_EMAIL_ALREADY_EXISTS.name()));
    }

    @Test
    void post_register_shouldThrowUserByPhoneExist_whenPhoneInDb() throws Exception {
        var requestDto = TestDataUtil.getValidUserIdentityRequestDto();
        var userCommand = TestDataUtil.getValidRegisterUserCommand();

        when(requestMapper.toRegisterUserCommand(requestDto)).thenReturn(userCommand);
        when(registerUserUseCase.register(userCommand)).thenThrow(new UserByPhoneAlreadyExistsException(requestDto.email()));

        var requestAsString = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAsString))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value(DomainExceptionCode.USER_BY_PHONE_ALREADY_EXISTS.name()));
    }
}