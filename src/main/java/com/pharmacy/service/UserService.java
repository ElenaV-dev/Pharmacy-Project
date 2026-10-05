package com.pharmacy.service;

import com.pharmacy.dto.user_dto.*;
import com.pharmacy.exception.EntityNotFoundException;
import com.pharmacy.mapper.UserMapper;
import com.pharmacy.model.User;
import com.pharmacy.model.UserRole;
import com.pharmacy.repository.UserRepository;
import com.pharmacy.security.JwtService;
import com.pharmacy.validator.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserService(UserValidator userValidator, UserMapper userMapper, UserRepository userRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userValidator = userValidator;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserLoginResponseDto registration(UserRegisterDto dto) {
        userValidator.validateRegisterDto(dto);
        User user = userMapper.UserRegisterDtoToEntity(dto);
        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        String token = jwtService.generateToken(authentication);

        log.info("New user registered: id={}, email={}, role={}",
                user.getId(),
                user.getEmail(),
                user.getRole());

        return userMapper.toUserLoginResponseDto(token, user.getRole());
    }

    public UserLoginResponseDto login(UserLoginRequestDto dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        String token = jwtService.generateToken(authentication);

        log.info("User logged in: email={}", dto.getEmail());

        UserRole role = UserRole.valueOf(authentication.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
                        .replace("ROLE_", "")
        );

        return userMapper.toUserLoginResponseDto(token, role);
    }

    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Retrieved {} users", users.size());
        return userMapper.toUserResponseDtoList(users);
    }

    public UserResponseDto createUser(UserCreateDto dto) {
        userValidator.validateCreateDto(dto);
        User user = userMapper.userCreateDtoToEntity(dto);
        userRepository.save(user);
        log.info("New user created: email={}, role={}", user.getEmail(), user.getRole());

        return userMapper.toUserResponseDto(user);
    }

    public UserResponseDto updateUser(Long id, UserUpdateDto dto) {
        userValidator.validateUpdateDto(dto, id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден с данным id: " + id));

        userMapper.updateEntityFromDto(user, dto);
        userRepository.save(user);
        log.info("User updated: id={}, email={}", user.getId(), user.getEmail());

        return userMapper.toUserResponseDto(user);
    }

    @Transactional
    public void deleteById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден с данным id: " + id));

        userRepository.delete(user);
        log.info("User deleted: id={}, email={}", user.getId(), user.getEmail());
    }
}
