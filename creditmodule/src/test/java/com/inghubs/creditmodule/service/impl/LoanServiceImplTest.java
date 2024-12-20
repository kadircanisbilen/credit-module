package com.inghubs.creditmodule.service.impl;

import com.inghubs.creditmodule.dto.LoanDTO;
import com.inghubs.creditmodule.entity.Loan;
import com.inghubs.creditmodule.entity.Users;
import com.inghubs.creditmodule.exception.ResourceNotFoundException;
import com.inghubs.creditmodule.mapper.LoanMapper;
import com.inghubs.creditmodule.repository.LoanInstallmentRepository;
import com.inghubs.creditmodule.repository.LoanRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @InjectMocks
    private LoanServiceImpl loanService;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanMapper loanMapper;

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    @Test
    void shouldCreateLoanSuccessfully() {
        // given
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setUserId(1L);
        loanDTO.setLoanAmount(new BigDecimal("10000"));
        loanDTO.setInterestRate(new BigDecimal("0.2"));
        loanDTO.setNumberOfInstallments(12);

        Users user = new Users();
        user.setId(1L);
        user.setCreditLimit(new BigDecimal("20000"));
        user.setUsedCreditLimit(new BigDecimal("0"));

        Loan loan = new Loan();
        Loan savedLoan = new Loan();
        savedLoan.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanMapper.toEntity(loanDTO)).thenReturn(loan);
        when(loanRepository.save(loan)).thenReturn(savedLoan);
        when(loanMapper.toDTO(savedLoan)).thenReturn(loanDTO);

        // when
        LoanDTO result = loanService.createLoan(loanDTO);

        // then
        assertNotNull(result);
        assertEquals(loanDTO, result);
        verify(userRepository).findById(1L);
        verify(loanMapper).toEntity(loanDTO);
        verify(loanRepository).save(loan);
        verify(loanMapper).toDTO(savedLoan);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForLoanCreation() {
        // given
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                loanService.createLoan(loanDTO)
        );

        assertEquals("user.not.found", exception.getMessage());
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldGetLoanByIdSuccessfully() {
        // given
        Loan loan = new Loan();
        loan.setId(1L);
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setId(1L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanMapper.toDTO(loan)).thenReturn(loanDTO);

        // when
        LoanDTO result = loanService.getLoanById(1L);

        // then
        assertNotNull(result);
        assertEquals(loanDTO, result);
        verify(loanRepository).findById(1L);
        verify(loanMapper).toDTO(loan);
    }

    @Test
    void shouldThrowExceptionWhenLoanNotFoundById() {
        // given
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                loanService.getLoanById(1L)
        );

        assertEquals("loan.not.found", exception.getMessage());
        verify(loanRepository).findById(1L);
    }

    @Test
    void shouldGetLoansForUserSuccessfully() {
        // given
        String username = "admin";
        Users adminUser = new Users();
        adminUser.setId(1L);
        adminUser.setUsername(username);
        adminUser.setRole("ADMIN");

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setUsers(adminUser);
        loan.setLoanAmount(new BigDecimal("12000"));
        loan.setNumberOfInstallments(12);
        loan.setInterestRate(new BigDecimal("0.1"));

        List<Loan> loans = List.of(loan);
        Page<Loan> pagedLoans = new PageImpl<>(loans);

        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setId(1L);
        loanDTO.setLoanAmount(new BigDecimal("12000"));
        loanDTO.setNumberOfInstallments(12);
        loanDTO.setInterestRate(new BigDecimal("0.1"));
        loanDTO.setIsPaid(false);

        // Mock security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);

        // Mock repository calls
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(adminUser));
        Mockito.when(loanRepository.findByUsersId(adminUser.getId(), PageRequest.of(0, 10)))
                .thenReturn(pagedLoans);
        Mockito.when(loanMapper.toDTO(loan)).thenReturn(loanDTO);

        // when
        Page<LoanDTO> result = loanService.getLoansForUser(adminUser.getId(), 0, 10);

        // then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals("12000", result.getContent().get(0).getLoanAmount().toPlainString());
        assertEquals(12, result.getContent().get(0).getNumberOfInstallments());
        assertFalse(result.getContent().get(0).getIsPaid());

        // Verify interactions
        Mockito.verify(userRepository).findById(1L);
        Mockito.verify(userRepository).findByUsername(username);
        Mockito.verify(loanRepository).findByUsersId(adminUser.getId(), PageRequest.of(0, 10));

        SecurityContextHolder.clearContext();
    }
}
