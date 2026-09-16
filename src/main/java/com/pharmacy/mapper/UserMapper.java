package com.pharmacy.mapper;

import com.pharmacy.dto.user_dto.UserLoginResponseDto;
import com.pharmacy.dto.user_dto.UserRegisterDto;
import com.pharmacy.model.User;
import com.pharmacy.model.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
}
