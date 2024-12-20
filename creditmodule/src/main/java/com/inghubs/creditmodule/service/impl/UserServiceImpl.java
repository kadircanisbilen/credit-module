package com.inghubs.creditmodule.service.impl;

import com.inghubs.creditmodule.dto.UserDTO;
import com.inghubs.creditmodule.entity.Users;
import com.inghubs.creditmodule.exception.ResourceNotFoundException;
import com.inghubs.creditmodule.mapper.UserMapper;
import com.inghubs.creditmodule.repository.UserRepository;
import com.inghubs.creditmodule.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO getUserById(Long id) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "user.not.found", id));
        return userMapper.toDTO(users);
    }

    @Override
    public Page<UserDTO> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Users> users = userRepository.findAll(pageable);
        return users.map(userMapper::toDTO);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        try {
            String hashedPassword = passwordEncoder.encode(userDTO.getPassword());
            userDTO.setPassword(hashedPassword);
            Users users = userMapper.toEntity(userDTO);
            Users savedUsers = userRepository.save(users);
            log.info("User created successfully with ID: {}", savedUsers.getId());
            return userMapper.toDTO(savedUsers);
        } catch (Exception ex) {
            log.error("Error occurred while creating User: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
}
