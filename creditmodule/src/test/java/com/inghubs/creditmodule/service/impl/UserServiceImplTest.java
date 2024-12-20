package com.inghubs.creditmodule.service.impl;

import com.inghubs.creditmodule.dto.UserDTO;
import com.inghubs.creditmodule.entity.Users;
import com.inghubs.creditmodule.exception.ResourceNotFoundException;
import com.inghubs.creditmodule.mapper.UserMapper;
import com.inghubs.creditmodule.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldGetUserByIdWhenUserExists() {
        // given
        Long userId = 1L;
        Users mockUser = new Users();
        mockUser.setId(userId);
        mockUser.setName("Test");
        UserDTO mockUserDTO = new UserDTO();
        mockUserDTO.setId(userId);
        mockUserDTO.setName("Test");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        Mockito.when(userMapper.toDTO(mockUser)).thenReturn(mockUserDTO);

        // when
        UserDTO result = userService.getUserById(userId);

        // then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        Mockito.verify(userRepository, times(1)).findById(userId);
        Mockito.verify(userMapper, times(1)).toDTO(mockUser);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // given
        Long userId = 1L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
        Mockito.verify(userRepository, times(1)).findById(userId);
        Mockito.verifyNoInteractions(userMapper);
    }

    @Test
    void shouldGetAllUsers() {
        // given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Users> mockPage = new PageImpl<>(List.of(new Users()));
        UserDTO mockUserDTO = new UserDTO();

        Mockito.when(userRepository.findAll(pageable)).thenReturn(mockPage);
        Mockito.when(userMapper.toDTO(Mockito.any(Users.class))).thenReturn(mockUserDTO);

        // when
        Page<UserDTO> result = userService.getAllUsers(page, size);

        // then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        Mockito.verify(userRepository, times(1)).findAll(pageable);
        Mockito.verify(userMapper, times(1)).toDTO(Mockito.any(Users.class));
    }

    @Test
    void shouldCreateUserSuccessfully() {
        // given
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testUser");
        userDTO.setPassword("password");
        userDTO.setRole("CUSTOMER");

        Users users = new Users();
        users.setId(1L);
        users.setUsername("testUser");
        users.setPassword("hashedPassword");
        users.setRole("CUSTOMER");

        when(userMapper.toEntity(userDTO)).thenReturn(users);
        when(userRepository.save(any(Users.class))).thenReturn(users);
        when(userMapper.toDTO(any(Users.class))).thenReturn(userDTO);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        // when
        UserDTO createdUser = userService.createUser(userDTO);

        // then
        assertNotNull(createdUser);
        assertEquals(userDTO, createdUser);
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(Users.class));
        verify(userMapper).toEntity(userDTO);
        verify(userMapper).toDTO(any(Users.class));
    }

    @Test
    void shouldLogErrorWhenExceptionOccursWhileCreatingUser() {
        // given
        UserDTO userDTO = new UserDTO();
        userDTO.setPassword("password123");
        Mockito.when(passwordEncoder.encode(userDTO.getPassword())).thenThrow(new RuntimeException("Encoding failed"));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.createUser(userDTO));
        assertEquals("Encoding failed", exception.getMessage());
        Mockito.verify(passwordEncoder, times(1)).encode(userDTO.getPassword());
        Mockito.verifyNoInteractions(userMapper);
        Mockito.verifyNoInteractions(userRepository);
    }
}
