package com.inghubs.creditmodule.service;

import com.inghubs.creditmodule.dto.LoanDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoanService {
    LoanDTO createLoan(LoanDTO loanDTO);

    LoanDTO getLoanById(Long id);

    Page<LoanDTO> getLoansForUser(Long userId, int page, int size);
}
