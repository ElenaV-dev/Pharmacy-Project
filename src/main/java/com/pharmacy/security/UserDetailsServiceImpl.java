package com.pharmacy.security;

import com.pharmacy.constants.ErrorMessageConstants;
import com.pharmacy.constants.SecurityConstants;
import com.pharmacy.repository.UserRepository;
import com.pharmacy.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                       String.format(ErrorMessageConstants.USER_NOT_FOUND_BY_EMAIL, email)));

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(SecurityConstants.ROLE_PREFIX + user.getRole().name())
                .build();
    }
}
