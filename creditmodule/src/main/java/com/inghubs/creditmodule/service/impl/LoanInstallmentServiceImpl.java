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
import com.inghubs.creditmodule.service.LoanInstallmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LoanInstallmentServiceImpl implements LoanInstallmentService {

    private final LoanInstallmentRepository installmentRepository;
    private final LoanInstallmentMapper installmentMapper;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    @Autowired
    public LoanInstallmentServiceImpl(LoanInstallmentRepository installmentRepository, LoanInstallmentMapper installmentMapper, LoanRepository loanRepository, UserRepository userRepository) {
        this.installmentRepository = installmentRepository;
        this.installmentMapper = installmentMapper;
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<LoanInstallmentDTO> getInstallmentsByLoanId(Long loanId) {
        List<LoanInstallment> installments = installmentRepository.findByLoanIdAndIsPaidFalseOrderByDueDateAsc(loanId);

        if (installments.isEmpty()) {
            throw new ResourceNotFoundException("loan.not.found", loanId);
        }

        return installments.stream()
                .map(installmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void payInstallments(String username, Long loanId, BigDecimal paymentAmount) {
        log.info("Processing payment of amount: {} for loan ID: {} by user: {}", paymentAmount, loanId, username);

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found", username));

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("loan.not.found", loanId));

        // Access control: Ensure customers can only pay their own loans
        if (user.getRole().equals("CUSTOMER") && !loan.getUsers().getId().equals(user.getId())) {
            log.warn("Access denied for user ID: {}. Cannot pay installments for loan ID: {}", user.getId(), loanId);
            throw new AccessDeniedException("You can only pay installments for your own loans.");
        }

        List<LoanInstallment> installments = installmentRepository.findByLoanIdAndIsPaidFalseOrderByDueDateAsc(loanId);

        if (installments.isEmpty()) {
            log.warn("No installments found for loan ID: {}", loanId);
            throw new ResourceNotFoundException("loan.not.found", loanId);
        }

        BigDecimal remainingAmount = paymentAmount;
        int paidInstallments = 0;
        BigDecimal totalPaid = BigDecimal.ZERO;

        for (LoanInstallment installment : installments) {
            // Stop if the due date is more than 3 months ahead
            if (installment.getDueDate().isAfter(LocalDate.now().plusMonths(3))) {
                log.info("Installment due date for ID: {} is more than 3 months ahead, stopping payments.", installment.getId());
                break;
            }

            BigDecimal effectiveAmount = installment.getAmount();
            long daysDifference;

            // Early payment discount
            if (LocalDate.now().isBefore(installment.getDueDate())) {
                daysDifference = ChronoUnit.DAYS.between(LocalDate.now(), installment.getDueDate());
                effectiveAmount = effectiveAmount.subtract(effectiveAmount.multiply(BigDecimal.valueOf(0.001 * daysDifference)));
            }
            // Late payment penalty
            else if (LocalDate.now().isAfter(installment.getDueDate())) {
                daysDifference = ChronoUnit.DAYS.between(installment.getDueDate(), LocalDate.now());
                effectiveAmount = effectiveAmount.add(effectiveAmount.multiply(BigDecimal.valueOf(0.001 * daysDifference)));
            }

            // Check if remaining amount is sufficient to cover the installment
            if (remainingAmount.compareTo(effectiveAmount) >= 0) {
                remainingAmount = remainingAmount.subtract(effectiveAmount);
                installment.setPaidAmount(effectiveAmount);
                installment.setPaymentDate(LocalDate.now());
                installment.setIsPaid(Boolean.TRUE);
                installmentRepository.save(installment);

                totalPaid = totalPaid.add(effectiveAmount);
                paidInstallments++;

                log.info("Installment with ID: {} paid. Effective amount: {}, Remaining amount: {}", installment.getId(), effectiveAmount, remainingAmount);
            } else {
                log.info("Remaining payment amount is less than the effective installment amount. Stopping payment.");
                break;
            }
        }

        // If no installments were paid, throw an exception
        if (paidInstallments == 0) {
            log.warn("Insufficient payment amount for loan ID: {}. No installments were paid.", loanId);
            throw new BusinessException("insufficient.payment.amount");
        }

        // Check if loan is fully paid
        boolean isLoanFullyPaid = installmentRepository.findByLoanIdAndIsPaidFalseOrderByDueDateAsc(loanId).isEmpty();
        if (isLoanFullyPaid) {
            loan.setIsPaid(Boolean.TRUE);
            loanRepository.save(loan);
            log.info("Loan ID: {} is fully paid. Updating loan status to 'Paid'.", loanId);
        }

        log.info("Installments Paid: {}", paidInstallments);
        log.info("Total Amount Paid: {}", totalPaid);
        log.info("Loan Fully Paid: {}", isLoanFullyPaid);
    }
}
