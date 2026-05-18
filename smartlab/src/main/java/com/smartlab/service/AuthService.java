package com.smartlab.service;

import com.smartlab.dto.RegisterDTO;
import com.smartlab.entity.User;
import com.smartlab.enums.Role;
import com.smartlab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public void register(RegisterDTO dto){

        if(userRepository.existsByUsername(dto.getUsername())){throw new RuntimeException("Tên người dùng đã tồn tại");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())

                // HASH PASSWORD
                .password(
                        passwordEncoder.encode(
                                dto.getPassword()
                        )
                )
                .role(Role.STUDENT)
                .build();
        userRepository.save(user);
    }
}