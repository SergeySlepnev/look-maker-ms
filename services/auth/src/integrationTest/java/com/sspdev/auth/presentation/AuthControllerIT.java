package com.sspdev.auth.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sspdev.auth.domain.exception.DomainExceptionCode;
import com.sspdev.auth.presentation.dto.UserIdentityRequestDto;
import com.sspdev.auth.setup.IntegrationTestBase;
import com.sspdev.auth.testutil.IntegrationTestDataUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RequiredArgsConstructor
public class AuthControllerIT extends IntegrationTestBase {

    public static final String EXISTING_EMAIL = "user01@example.com";
    public static final String EXISTING_PHONE = "+7 900 111 11 01";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Test
    void post_register_shouldReturnUserIdentityResponseDto_whenRequestDtoValid() throws Exception {
        var requestDto = IntegrationTestDataUtil.getValidUserIdentityRequestDto();

        var requestDtoAsString = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDtoAsString)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void post_register_shouldThrowUserByEmailExist_whenEmailInDb() throws Exception {
        var requestDtoWithExistingEmail = UserIdentityRequestDto.builder()
                .email(EXISTING_EMAIL)
                .phone("8-925-869-96-98")
                .rawPassword("dummyPassword")
                .build();

        var requestAsString = objectMapper.writeValueAsString(requestDtoWithExistingEmail);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAsString))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value(DomainExceptionCode.USER_BY_EMAIL_ALREADY_EXISTS.name()));
    }

    @Test
    void post_register_shouldThrowUserByPhoneExist_whenPhoneInDb() throws Exception {
        var requestDtoWithExistingPhone = UserIdentityRequestDto.builder()
                .email("test@gmail.com")
                .phone(EXISTING_PHONE)
                .rawPassword("dummyPassword")
                .build();

        var requestAsString = objectMapper.writeValueAsString(requestDtoWithExistingPhone);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAsString))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value(DomainExceptionCode.USER_BY_PHONE_ALREADY_EXISTS.name()));
    }
}