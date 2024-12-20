package com.inghubs.creditmodule.service;

import com.inghubs.creditmodule.dto.LoanInstallmentDTO;

import java.math.BigDecimal;
import java.util.List;

public interface LoanInstallmentService {
    List<LoanInstallmentDTO> getInstallmentsByLoanId(Long loanId);

    void payInstallments(String username, Long loanId, BigDecimal paymentAmount);
}
