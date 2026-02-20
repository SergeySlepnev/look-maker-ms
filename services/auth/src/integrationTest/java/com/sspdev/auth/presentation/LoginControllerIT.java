package com.sspdev.auth.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sspdev.auth.domain.port.in.RegisterUserUseCase;
import com.sspdev.auth.presentation.dto.LoginRequestDto;
import com.sspdev.auth.setup.IntegrationTestBase;
import com.sspdev.auth.testutil.IntegrationTestDataUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@AutoConfigureMockMvc
public class LoginControllerIT extends IntegrationTestBase {

    private final RegisterUserUseCase registerUserUseCase;
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    @Test
    void post_login_shouldCreateTokenAndReturn200_whenUserInDb() throws Exception {
        var userCommand = IntegrationTestDataUtil.getValidRegisterUserCommand();
        registerUserUseCase.register(userCommand);
        var loginRequest = LoginRequestDto.builder()
                .email(userCommand.email())
                .password(userCommand.rawPassword())
                .build();
        var loginJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }
}