package com.pharmacy.controller;

import com.pharmacy.dto.user_dto.UserLoginRequestDto;
import com.pharmacy.dto.user_dto.UserLoginResponseDto;
import com.pharmacy.dto.user_dto.UserRegisterDto;
import com.pharmacy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthorizationController {
    private final UserService userService;

    public AuthorizationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public UserLoginResponseDto registration(@Valid @RequestBody UserRegisterDto dto) {
        return userService.registration(dto);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@Valid @RequestBody UserLoginRequestDto dto) {
        return userService.login(dto);
    }
}
