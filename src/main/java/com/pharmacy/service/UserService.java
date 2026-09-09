package com.pharmacy.service;

import com.pharmacy.dto.user_dto.UserLoginRequestDto;
import com.pharmacy.dto.user_dto.UserLoginResponseDto;
import com.pharmacy.dto.user_dto.UserRegisterDto;
import com.pharmacy.mapper.UserMapper;
import com.pharmacy.model.User;
import com.pharmacy.repository.UserRepository;
import com.pharmacy.security.JwtService;
import com.pharmacy.validator.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

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

    public void registration(UserRegisterDto dto) {
        userValidator.validateRegisterDto(dto);
        User user = userMapper.UserRegisterDtoToEntity(dto);
        userRepository.save(user);
        log.info("New user registered: id={}, email={}, role={}",
                user.getId(),
                user.getEmail(),
                user.getRole());
    }

    public UserLoginResponseDto login(UserLoginRequestDto dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        String token = jwtService.generateToken(authentication);

        return userMapper.toUserLoginResponseDto(token);
    }
}
