package com.inghubs.creditmodule.repository;

import com.inghubs.creditmodule.entity.Loan;
import com.inghubs.creditmodule.entity.LoanInstallment;
import com.inghubs.creditmodule.entity.Users;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class LoanInstallmentRepositoryTest {

    @Autowired
    private LoanInstallmentRepository installmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindByLoanIdAndIsPaidFalseOrderByDueDateAscSuccessfully() {
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

        Loan loan = new Loan();
        loan.setUsers(user);
        loan.setLoanAmount(BigDecimal.valueOf(5000));
        loan.setNumberOfInstallments(12);
        loan.setInterestRate(BigDecimal.valueOf(0.2));
        loan.setIsPaid(false);
        loan.setCreateDate(LocalDate.now());
        entityManager.persist(loan);

        LoanInstallment installment1 = new LoanInstallment();
        installment1.setLoan(loan);
        installment1.setAmount(BigDecimal.valueOf(500));
        installment1.setDueDate(LocalDate.now().plusMonths(1));
        installment1.setIsPaid(false);
        entityManager.persist(installment1);

        LoanInstallment installment2 = new LoanInstallment();
        installment2.setLoan(loan);
        installment2.setAmount(BigDecimal.valueOf(500));
        installment2.setDueDate(LocalDate.now().plusMonths(2));
        installment2.setIsPaid(false);
        entityManager.persist(installment2);

        LoanInstallment installment3 = new LoanInstallment();
        installment3.setLoan(loan);
        installment3.setAmount(BigDecimal.valueOf(500));
        installment3.setDueDate(LocalDate.now().plusMonths(3));
        installment3.setIsPaid(true);
        entityManager.persist(installment3);

        entityManager.flush();

        // when
        List<LoanInstallment> unpaidInstallments = installmentRepository
                .findByLoanIdAndIsPaidFalseOrderByDueDateAsc(loan.getId());

        // then
        assertThat(unpaidInstallments).isNotNull();
        assertThat(unpaidInstallments.size()).isEqualTo(2);
        assertThat(unpaidInstallments.get(0).getDueDate()).isBefore(unpaidInstallments.get(1).getDueDate());
        assertThat(unpaidInstallments.get(0).getIsPaid()).isFalse();
        assertThat(unpaidInstallments.get(1).getIsPaid()).isFalse();
    }

    @Test
    void shouldReturnEmptyListWhenNoUnpaidInstallmentsExist() {
        // given
        Users user = new Users();
        user.setName("Jane");
        user.setSurname("Smith");
        user.setUsername("jane_smith");
        user.setPassword("password");
        user.setRole("CUSTOMER");
        user.setCreditLimit(BigDecimal.valueOf(10000));
        user.setUsedCreditLimit(BigDecimal.ZERO);

        entityManager.persist(user);

        Loan loan = new Loan();
        loan.setUsers(user);
        loan.setLoanAmount(BigDecimal.valueOf(3000));
        loan.setNumberOfInstallments(6);
        loan.setInterestRate(BigDecimal.valueOf(0.15));
        loan.setIsPaid(false);
        loan.setCreateDate(LocalDate.now());
        entityManager.persist(loan);

        LoanInstallment installment = new LoanInstallment();
        installment.setLoan(loan);
        installment.setAmount(BigDecimal.valueOf(500));
        installment.setDueDate(LocalDate.now().plusMonths(1));
        installment.setIsPaid(true); // This installment is paid
        entityManager.persist(installment);

        entityManager.flush();

        // when
        List<LoanInstallment> unpaidInstallments = installmentRepository
                .findByLoanIdAndIsPaidFalseOrderByDueDateAsc(loan.getId());

        // then
        assertThat(unpaidInstallments).isNotNull();
        assertThat(unpaidInstallments).isEmpty();
    }
}
