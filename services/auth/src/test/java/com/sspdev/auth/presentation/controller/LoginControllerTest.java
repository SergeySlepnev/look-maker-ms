package com.sspdev.auth.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sspdev.auth.application.service.LoginService;
import com.sspdev.auth.presentation.dto.LoginRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private LoginService loginService;
    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(loginController)
                .alwaysDo(print())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void post_login_shouldCreateTokenAndReturn200_whenUserInDb() throws Exception {
        var loginRequest = LoginRequestDto.builder()
                .email("test@gmail.com")
                .password("testRawPassword")
                .build();

        when(loginService.login(loginRequest.email(), loginRequest.password())).thenReturn("generatedValidJwtToken");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("generatedValidJwtToken"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        verify(loginService, times(1)).login(loginRequest.email(), loginRequest.password());
    }
}