package com.sspdev.auth.presentation.mapper;

import org.springframework.stereotype.Component;
import com.sspdev.auth.domain.port.in.RegisterUserCommand;
import com.sspdev.auth.presentation.dto.UserIdentityRequestDto;

@Component
public class UserIdentityRequestMapper {

    public RegisterUserCommand toRegisterUserCommand(UserIdentityRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        return RegisterUserCommand.builder()
                .email(requestDto.email())
                .phone(requestDto.phone())
                .rawPassword(requestDto.rawPassword())
                .roles(requestDto.roles())
                .build();
    }
}