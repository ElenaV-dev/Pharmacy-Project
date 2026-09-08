package com.pharmacy.service;

import com.pharmacy.dto.user_dto.UserRegisterDto;
import com.pharmacy.mapper.UserMapper;
import com.pharmacy.model.User;
import com.pharmacy.repository.UserRepository;
import com.pharmacy.validator.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserService(UserValidator userValidator, UserMapper userMapper, UserRepository userRepository) {
        this.userValidator = userValidator;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
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
}
