package com.sspdev.auth.presentation.controller;

import com.sspdev.auth.domain.port.in.RegisterUserUseCase;
import com.sspdev.auth.domain.port.out.MessageResolver;
import com.sspdev.auth.presentation.dto.UserIdentityRequestDto;
import com.sspdev.auth.presentation.dto.UserIdentityResponseDto;
import com.sspdev.auth.presentation.mapper.UserIdentityRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String USER_SUCCESSFULLY_CREATED_KEY = "register.user.success";

    private final UserIdentityRequestMapper requestMapper;
    private final RegisterUserUseCase registerUserUseCase;
    private final MessageResolver messageResolver;

    @PostMapping("/register")
    public ResponseEntity<UserIdentityResponseDto> register(@Validated @RequestBody UserIdentityRequestDto requestDto) {
        var registerCommand = requestMapper.toRegisterUserCommand(requestDto);
        var registered = registerUserUseCase.register(registerCommand);

        var locale = LocaleContextHolder.getLocale();
        var message = messageResolver.resolve(
                USER_SUCCESSFULLY_CREATED_KEY,
                new Object[]{registered.id()},
                locale);

        var response = UserIdentityResponseDto.builder()
                .id(registered.id())
                .message(message)
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}