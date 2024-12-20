package com.inghubs.creditmodule.mapper;

import com.inghubs.creditmodule.dto.UserDTO;
import com.inghubs.creditmodule.entity.Users;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapper userMapper;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void shouldMapUserEntityToUserDTO() {
        // given
        Users user = new Users();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");
        user.setUsername("johndoe");
        user.setRole("CUSTOMER");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("John");
        userDTO.setSurname("Doe");
        userDTO.setUsername("johndoe");
        userDTO.setRole("CUSTOMER");

        Mockito.when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        // when
        UserDTO result = userMapper.toDTO(user);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getName()).isEqualTo(user.getName());
        assertThat(result.getSurname()).isEqualTo(user.getSurname());
        assertThat(result.getUsername()).isEqualTo(user.getUsername());
        assertThat(result.getRole()).isEqualTo(user.getRole());

        Mockito.verify(modelMapper).map(user, UserDTO.class);
    }

    @Test
    void shouldMapUserDTOToUserEntity() {
        // given
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Jane");
        userDTO.setSurname("Smith");
        userDTO.setUsername("janesmith");
        userDTO.setRole("ADMIN");

        Users user = new Users();
        user.setId(1L);
        user.setName("Jane");
        user.setSurname("Smith");
        user.setUsername("janesmith");
        user.setRole("ADMIN");

        Mockito.when(modelMapper.map(userDTO, Users.class)).thenReturn(user);

        // when
        Users result = userMapper.toEntity(userDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userDTO.getId());
        assertThat(result.getName()).isEqualTo(userDTO.getName());
        assertThat(result.getSurname()).isEqualTo(userDTO.getSurname());
        assertThat(result.getUsername()).isEqualTo(userDTO.getUsername());
        assertThat(result.getRole()).isEqualTo(userDTO.getRole());

        Mockito.verify(modelMapper).map(userDTO, Users.class);
    }
}
