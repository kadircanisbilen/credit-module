package com.inghubs.creditmodule.mapper;

import com.inghubs.creditmodule.dto.LoanDTO;
import com.inghubs.creditmodule.entity.Loan;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {

    private final ModelMapper modelMapper;

    public LoanMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public LoanDTO toDTO(Loan loan) {
        return modelMapper.map(loan, LoanDTO.class);
    }

    public Loan toEntity(LoanDTO loanDTO) {
        return modelMapper.map(loanDTO, Loan.class);
    }
}
