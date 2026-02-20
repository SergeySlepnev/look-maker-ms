package com.sspdev.auth.presentation.controller;

import com.sspdev.auth.application.service.LoginService;
import com.sspdev.auth.presentation.dto.LoginRequestDto;
import com.sspdev.auth.presentation.dto.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Validated LoginRequestDto requestDto) {
        var token = loginService.login(requestDto.email(), requestDto.password());
        var responseDto = LoginResponseDto.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .build();

        return ResponseEntity.ok(responseDto);
    }
}