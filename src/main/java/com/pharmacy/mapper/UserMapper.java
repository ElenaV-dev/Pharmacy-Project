package com.pharmacy.mapper;

import com.pharmacy.dto.user_dto.*;
import com.pharmacy.model.User;
import com.pharmacy.model.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User UserRegisterDtoToEntity(UserRegisterDto dto) {
        User user = new User();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(UserRole.CLIENT);

        return user;
    }

    public UserLoginResponseDto toUserLoginResponseDto(String token, UserRole role) {
        return UserLoginResponseDto.builder()
                .token(token)
                .role(role)
                .build();
    }

    public UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    public List<UserResponseDto> toUserResponseDtoList(List<User> users) {
        return users.stream()
                .map(this::toUserResponseDto)
                .toList();
    }

    public User userCreateDtoToEntity(UserCreateDto dto) {
        User user = new User();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());

        return user;
    }

    public void updateEntityFromDto(User user, UserUpdateDto dto) {
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
    }
}
