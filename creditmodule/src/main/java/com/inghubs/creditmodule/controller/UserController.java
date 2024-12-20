package com.inghubs.creditmodule.controller;

import com.inghubs.creditmodule.dto.LoanDTO;
import com.inghubs.creditmodule.dto.LoanInstallmentDTO;
import com.inghubs.creditmodule.exception.ResourceNotFoundException;
import com.inghubs.creditmodule.repository.UserRepository;
import com.inghubs.creditmodule.service.LoanInstallmentService;
import com.inghubs.creditmodule.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final LoanInstallmentService installmentService;
    private final LoanService loanService;

    @Autowired
    public UserController(LoanInstallmentService installmentService, LoanService loanService) {
        this.installmentService = installmentService;
        this.loanService = loanService;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/loans/{loanId}/installments")
    public ResponseEntity<List<LoanInstallmentDTO>> getInstallmentsByLoanId(@PathVariable Long loanId) {
        List<LoanInstallmentDTO> installments = installmentService.getInstallmentsByLoanId(loanId);
        return ResponseEntity.ok(installments);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @PostMapping("/pay-loan")
    public ResponseEntity<String> payLoan(
            @RequestParam Long loanId,
            @RequestParam BigDecimal amount) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        installmentService.payInstallments(username, loanId, amount);

        return ResponseEntity.ok("Payment completed successfully.");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping("/loans")
    public ResponseEntity<Page<LoanDTO>> getLoansForUser(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<LoanDTO> loans = loanService.getLoansForUser(userId, page, size);
        return ResponseEntity.ok(loans);
    }
}
