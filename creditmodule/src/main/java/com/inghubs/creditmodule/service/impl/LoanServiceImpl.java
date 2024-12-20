package com.inghubs.creditmodule.service.impl;

import com.inghubs.creditmodule.dto.LoanDTO;
import com.inghubs.creditmodule.entity.Users;
import com.inghubs.creditmodule.entity.Loan;
import com.inghubs.creditmodule.entity.LoanInstallment;
import com.inghubs.creditmodule.exception.BusinessException;
import com.inghubs.creditmodule.exception.ResourceNotFoundException;
import com.inghubs.creditmodule.mapper.LoanMapper;
import com.inghubs.creditmodule.repository.UserRepository;
import com.inghubs.creditmodule.repository.LoanInstallmentRepository;
import com.inghubs.creditmodule.repository.LoanRepository;
import com.inghubs.creditmodule.service.LoanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final LoanMapper loanMapper;
    private final LoanInstallmentRepository loanInstallmentRepository;

    @Autowired
    public LoanServiceImpl(LoanRepository loanRepository, UserRepository userRepository,
                           LoanMapper loanMapper, LoanInstallmentRepository loanInstallmentRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.loanMapper = loanMapper;
        this.loanInstallmentRepository = loanInstallmentRepository;
    }

    @Transactional
    @Override
    public LoanDTO createLoan(LoanDTO loanDTO) {
        try {
            Users users = userRepository.findById(loanDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("user.not.found", loanDTO.getUserId()));

            if (loanDTO.getInterestRate().compareTo(BigDecimal.valueOf(0.1)) < 0 ||
                    loanDTO.getInterestRate().compareTo(BigDecimal.valueOf(0.5)) > 0) {
                log.warn("Invalid interest rate: {}", loanDTO.getInterestRate());
                throw new BusinessException("invalid.interest.rate");
            }

            List<Integer> validInstallments = Arrays.asList(6, 9, 12, 24);
            if (!validInstallments.contains(loanDTO.getNumberOfInstallments())) {
                log.warn("Invalid number of installments: {}", loanDTO.getNumberOfInstallments());
                throw new BusinessException("invalid.number.of.installments");
            }

            BigDecimal totalAmount = loanDTO.getLoanAmount()
                    .multiply(BigDecimal.ONE.add(loanDTO.getInterestRate())); // totalAmount = amount * (1 + interest rate)
            BigDecimal newCreditUsage = users.getUsedCreditLimit().add(totalAmount);

            if (newCreditUsage.compareTo(users.getCreditLimit()) > 0) {
                log.warn("Insufficient credit limit for user ID: {}", users.getId());
                throw new BusinessException("insufficient.credit.limit", users.getId());
            }

            Loan loan = loanMapper.toEntity(loanDTO);
            loan.setUsers(users);
            loan.setLoanAmount(totalAmount);
            Loan savedLoan = loanRepository.save(loan);

            users.setUsedCreditLimit(newCreditUsage);
            userRepository.save(users);

            BigDecimal installmentAmount = totalAmount.divide(BigDecimal.valueOf(loanDTO.getNumberOfInstallments()), RoundingMode.HALF_UP);
            LocalDate dueDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);

            for (int i = 0; i < loanDTO.getNumberOfInstallments(); i++) {
                LoanInstallment installment = new LoanInstallment();
                installment.setLoan(savedLoan);
                installment.setAmount(installmentAmount);
                installment.setDueDate(dueDate.plusMonths(i));
                installment.setIsPaid(false);
                loanInstallmentRepository.save(installment);
            }

            log.info("Loan created successfully for user ID: {}", users.getId());
            return loanMapper.toDTO(savedLoan);
        } catch (Exception ex) {
            log.error("Error occurred while creating loan: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public LoanDTO getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "loan.not.found", id));
        return loanMapper.toDTO(loan);
    }

    @Override
    public Page<LoanDTO> getLoansForUser(Long userId, int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Users currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found", username));

        Pageable pageable = PageRequest.of(page, size);

        if (Objects.equals(currentUser.getRole(), "ADMIN")) {
            if (userId != null) {
                Users user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("user.not.found", userId));
                return loanRepository.findByUsersId(user.getId(), pageable).map(loanMapper::toDTO);
            }
            return loanRepository.findAll(pageable).map(loanMapper::toDTO);
        }

        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("You can only access your own loans.");
        }

        return loanRepository.findByUsersId(currentUser.getId(), pageable).map(loanMapper::toDTO);
    }
}
