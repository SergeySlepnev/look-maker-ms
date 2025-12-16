package com.sspdev.auth.unit.presentation.mapper;

import com.sspdev.auth.presentation.mapper.UserIdentityRequestMapper;
import com.sspdev.auth.TestDataUtil;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.Test;

class UserIdentityRequestMapperTest {

    private final UserIdentityRequestMapper requestMapper = new UserIdentityRequestMapper();

    @Test
    void toRegisterUserCommand_shouldMapFromRequestDtoToRegisterUserCommand_whenRequestValid() {
        var userIdentityRequestDto = TestDataUtil.getValidUserIdentityRequestDto();
        var registerUserCommand = TestDataUtil.getValidRegisterUserCommand();

        var actualResult = requestMapper.toRegisterUserCommand(userIdentityRequestDto);

        assertThat(actualResult).usingRecursiveComparison().isEqualTo(registerUserCommand);
    }

    @Test
    void toRegisterUserCommand_shouldMapFromRequestDto_withNullFields_ToRegisterUserCommand() {
        var userIdentityRequestDto = TestDataUtil.getUserIdentityRequestDtoWithNullFields();

        var actualResult = requestMapper.toRegisterUserCommand(userIdentityRequestDto);

        assertAll(() -> {
            assertThat(actualResult).isNotNull();
            assertThat(actualResult.email()).isNull();
            assertThat(actualResult.phone()).isNull();
            assertThat(actualResult.rawPassword()).isNull();
        });
    }

    @Test
    void toRegisterUserCommand_shouldReturnNull_whenRequestNull() {
        var actualResult = requestMapper.toRegisterUserCommand(null);

        assertThat(actualResult).isNull();
    }
}