package com.sspdev.auth.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sspdev.auth.domain.port.in.RegisterUserUseCase;
import com.sspdev.auth.domain.port.out.MessageResolver;
import com.sspdev.auth.presentation.mapper.UserIdentityRequestMapper;
import com.sspdev.auth.testutil.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Locale;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserIdentityRequestMapper requestMapper;
    @Mock
    private RegisterUserUseCase registerUserUseCase;
    @Mock
    private MessageResolver messageResolver;
    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
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
                .andExpect(jsonPath("$.id").value(userResponse.id()))
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
}