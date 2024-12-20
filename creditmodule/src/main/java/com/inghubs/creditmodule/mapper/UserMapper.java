package com.inghubs.creditmodule.mapper;

import com.inghubs.creditmodule.dto.UserDTO;
import com.inghubs.creditmodule.entity.Users;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UserDTO toDTO(Users users) {
        return modelMapper.map(users, UserDTO.class);
    }

    public Users toEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, Users.class);
    }
}
