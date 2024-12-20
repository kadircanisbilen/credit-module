package com.inghubs.creditmodule.repository;

import com.inghubs.creditmodule.entity.Users;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@ExtendWith(SpringExtension.class)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindAllUsersSuccessfully() {
        // given
        Users user1 = new Users();
        user1.setName("John");
        user1.setSurname("Doe");
        user1.setUsername("johndoe");
        user1.setPassword("password");
        user1.setRole("ADMIN");
        user1.setCreditLimit(BigDecimal.valueOf(10000));
        user1.setUsedCreditLimit(BigDecimal.ZERO);

        Users user2 = new Users();
        user2.setName("Jane");
        user2.setSurname("Doe");
        user2.setUsername("janedoe");
        user2.setPassword("password");
        user2.setRole("CUSTOMER");
        user2.setCreditLimit(BigDecimal.valueOf(5000));
        user2.setUsedCreditLimit(BigDecimal.valueOf(2000));

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Users> result = userRepository.findAll(pageable);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.getContent()).extracting(Users::getUsername)
                .containsExactlyInAnyOrder("johndoe", "janedoe");
    }

    @Test
    void shouldFindUserByUsernameSuccessfully() {
        // given
        Users user = new Users();
        user.setName("John");
        user.setSurname("Doe");
        user.setUsername("johndoe");
        user.setPassword("password");
        user.setRole("ADMIN");
        user.setCreditLimit(BigDecimal.valueOf(10000));
        user.setUsedCreditLimit(BigDecimal.ZERO);

        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<Users> result = userRepository.findByUsername("johndoe");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("John");
    }

    @Test
    void shouldReturnEmptyWhenUsernameNotFound() {
        // when
        Optional<Users> result = userRepository.findByUsername("nonexistent");

        // then
        assertThat(result).isNotPresent();
    }
}
