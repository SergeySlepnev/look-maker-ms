package com.sspdev.auth.domain.port.in;

public interface RegisterUserUseCase {

    RegisterUserResponse register(RegisterUserCommand command);
}