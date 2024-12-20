package com.inghubs.creditmodule.service;

import com.inghubs.creditmodule.dto.UserDTO;
import org.springframework.data.domain.Page;

public interface UserService {
    UserDTO getUserById(Long id);

    Page<UserDTO> getAllUsers(int page, int size);

    UserDTO createUser(UserDTO userDTO);
}
