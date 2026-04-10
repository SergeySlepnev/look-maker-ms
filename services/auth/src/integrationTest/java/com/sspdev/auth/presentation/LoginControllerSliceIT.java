package com.sspdev.auth.presentation;

import lombok.RequiredArgsConstructor;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sspdev.auth.application.service.LoginService;
import com.sspdev.auth.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.sspdev.auth.presentation.controller.LoginController;
import com.sspdev.auth.presentation.dto.LoginRequestDto;
import com.sspdev.auth.presentation.exception.ApiExceptionHandler;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor
public class LoginControllerSliceIT {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockitoBean
    private LoginService loginService;
    @MockitoBean
    private JwtAuthenticationFilter authenticationFilter;
    @MockitoBean
    private ApiExceptionHandler exceptionHandler;

    @Test
    void post_login_shouldReturnBadRequest_whenBodyInvalid() throws Exception {
        var invalidLoginDto = LoginRequestDto.builder()
                .build();

        var invalidLoginRequest = objectMapper.writeValueAsString(invalidLoginDto);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidLoginRequest))
                .andExpect(status().isBadRequest());
    }
}