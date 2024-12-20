package com.inghubs.creditmodule.repository;

import com.inghubs.creditmodule.entity.Loan;
import com.inghubs.creditmodule.entity.Users;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindByUsersIdSuccessfully() {
        // given
        Users user = new Users();
        user.setName("John");
        user.setSurname("Doe");
        user.setUsername("john_doe");
        user.setPassword("password");
        user.setRole("CUSTOMER");
        user.setCreditLimit(BigDecimal.valueOf(10000));
        user.setUsedCreditLimit(BigDecimal.ZERO);

        entityManager.persist(user);

        Loan loan1 = new Loan();
        loan1.setUsers(user);
        loan1.setLoanAmount(BigDecimal.valueOf(5000));
        loan1.setNumberOfInstallments(12);
        loan1.setInterestRate(BigDecimal.valueOf(0.2));
        loan1.setIsPaid(false);
        loan1.setCreateDate(LocalDate.now());
        entityManager.persist(loan1);

        Loan loan2 = new Loan();
        loan2.setUsers(user);
        loan2.setLoanAmount(BigDecimal.valueOf(3000));
        loan2.setNumberOfInstallments(6);
        loan2.setInterestRate(BigDecimal.valueOf(0.15));
        loan2.setIsPaid(true);
        loan2.setCreateDate(LocalDate.now());
        entityManager.persist(loan2);

        entityManager.flush();

        // when
        Page<Loan> loans = loanRepository.findByUsersId(user.getId(), PageRequest.of(0, 10));

        // then
        assertThat(loans).isNotNull();
        assertThat(loans.getTotalElements()).isEqualTo(2);

        Loan retrievedLoan1 = loans.getContent().get(0);
        assertThat(retrievedLoan1.getUsers().getId()).isEqualTo(user.getId());
        assertThat(retrievedLoan1.getLoanAmount()).isEqualByComparingTo(BigDecimal.valueOf(5000));
    }

    @Test
    void shouldReturnEmptyPageWhenNoLoansExistForUser() {
        // given
        Users user = new Users();
        user.setName("Jane");
        user.setSurname("Smith");
        user.setUsername("jane_smith");
        user.setPassword("password");
        user.setRole("CUSTOMER");
        user.setCreditLimit(BigDecimal.valueOf(5000));
        user.setUsedCreditLimit(BigDecimal.ZERO);

        entityManager.persist(user);

        entityManager.flush();

        // when
        Page<Loan> loans = loanRepository.findByUsersId(user.getId(), PageRequest.of(0, 10));

        // then
        assertThat(loans).isNotNull();
        assertThat(loans.getTotalElements()).isEqualTo(0);
    }
}
