package com.pharmacy.controller;

import com.pharmacy.constants.ApiEndpointConstants;
import com.pharmacy.dto.user_dto.UserCreateDto;
import com.pharmacy.dto.user_dto.UserResponseDto;
import com.pharmacy.dto.user_dto.UserUpdateDto;
import com.pharmacy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiEndpointConstants.USER)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserResponseDto createUser(@Valid @RequestBody UserCreateDto dto) {
        return userService.createUser(dto);
    }

    @PutMapping(ApiEndpointConstants.USER_BY_ID)
    public UserResponseDto updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto dto) {

        return userService.updateUser(id, dto);
    }

    @DeleteMapping(ApiEndpointConstants.USER_BY_ID)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }
}
