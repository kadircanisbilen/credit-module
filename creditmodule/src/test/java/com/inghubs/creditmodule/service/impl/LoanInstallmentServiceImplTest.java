package com.inghubs.creditmodule.service.impl;

import com.inghubs.creditmodule.dto.LoanInstallmentDTO;
import com.inghubs.creditmodule.entity.Loan;
import com.inghubs.creditmodule.entity.LoanInstallment;
import com.inghubs.creditmodule.entity.Users;
import com.inghubs.creditmodule.exception.BusinessException;
import com.inghubs.creditmodule.exception.ResourceNotFoundException;
import com.inghubs.creditmodule.mapper.LoanInstallmentMapper;
import com.inghubs.creditmodule.repository.LoanInstallmentRepository;
import com.inghubs.creditmodule.repository.LoanRepository;
import com.inghubs.creditmodule.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class LoanInstallmentServiceImplTest {

    @Mock
    private LoanInstallmentRepository installmentRepository;

    @Mock
    private LoanInstallmentMapper installmentMapper;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoanInstallmentServiceImpl installmentService;

    @Test
    void shouldGetInstallmentsByLoanIdSuccessfully() {
        // given
        Long loanId = 1L;
        LoanInstallment installment = new LoanInstallment();
        installment.setId(1L);
        installment.setAmount(BigDecimal.valueOf(1000));
        installment.setDueDate(LocalDate.now());

        LoanInstallmentDTO installmentDTO = new LoanInstallmentDTO();
        installmentDTO.setId(1L);
        installmentDTO.setAmount(BigDecimal.valueOf(1000));

        Mockito.when(installmentRepository.findByLoanIdAndIsPaidFalseOrderByDueDateAsc(loanId))
                .thenReturn(Collections.singletonList(installment));
        Mockito.when(installmentMapper.toDTO(installment)).thenReturn(installmentDTO);

        // when
        List<LoanInstallmentDTO> result = installmentService.getInstallmentsByLoanId(loanId);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        Mockito.verify(installmentRepository).findByLoanIdAndIsPaidFalseOrderByDueDateAsc(loanId);
        Mockito.verify(installmentMapper).toDTO(installment);
    }

    @Test
    void shouldThrowExceptionIfNoInstallmentsFound() {
        // given
        Long loanId = 1L;
        Mockito.when(installmentRepository.findByLoanIdAndIsPaidFalseOrderByDueDateAsc(loanId))
                .thenReturn(Collections.emptyList());

        // when & then
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> installmentService.getInstallmentsByLoanId(loanId));

        assertEquals("loan.not.found", exception.getMessage());
        Mockito.verify(installmentRepository).findByLoanIdAndIsPaidFalseOrderByDueDateAsc(loanId);
    }

    @Test
    void shouldPayInstallmentsSuccessfully() {
        // given
        String username = "user";
        Long loanId = 1L;
        BigDecimal paymentAmount = BigDecimal.valueOf(2000);

        Users user = new Users();
        user.setId(1L);
        user.setUsername(username);
        user.setRole("CUSTOMER");

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setUsers(user);
        loan.setIsPaid(false);

        LoanInstallment installment1 = new LoanInstallment();
        installment1.setId(1L);
        installment1.setAmount(BigDecimal.valueOf(1000));
        installment1.setDueDate(LocalDate.now());

        LoanInstallment installment2 = new LoanInstallment();
        installment2.setId(2L);
        installment2.setAmount(BigDecimal.valueOf(1000));
        installment2.setDueDate(LocalDate.now());

        List<LoanInstallment> installments = List.of(installment1, installment2);

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        Mockito.when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        Mockito.when(installmentRepository.findByLoanIdAndIsPaidFalseOrderByDueDateAsc(loanId))
                .thenReturn(installments)
                .thenReturn(Collections.emptyList());

        // when
        installmentService.payInstallments(username, loanId, paymentAmount);

        // then
        Mockito.verify(userRepository).findByUsername(username);
        Mockito.verify(loanRepository).findById(loanId);
        Mockito.verify(installmentRepository, Mockito.times(2)).save(Mockito.any(LoanInstallment.class));
        Mockito.verify(loanRepository).save(loan);
        assertTrue(loan.getIsPaid());
    }

    @Test
    void shouldThrowExceptionIfPaymentAmountIsInsufficient() {
        // given
        String username = "user";
        Long loanId = 1L;
        BigDecimal paymentAmount = BigDecimal.valueOf(500);

        Users user = new Users();
        user.setId(1L);
        user.setUsername(username);
        user.setRole("CUSTOMER");

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setUsers(user);

        LoanInstallment installment = new LoanInstallment();
        installment.setId(1L);
        installment.setAmount(BigDecimal.valueOf(1000));
        installment.setDueDate(LocalDate.now());

        List<LoanInstallment> installments = List.of(installment);

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        Mockito.when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        Mockito.when(installmentRepository.findByLoanIdAndIsPaidFalseOrderByDueDateAsc(loanId))
                .thenReturn(installments);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> installmentService.payInstallments(username, loanId, paymentAmount));

        assertEquals("insufficient.payment.amount", exception.getMessage());
        Mockito.verify(userRepository).findByUsername(username);
        Mockito.verify(loanRepository).findById(loanId);
        Mockito.verify(installmentRepository).findByLoanIdAndIsPaidFalseOrderByDueDateAsc(loanId);
    }
}
