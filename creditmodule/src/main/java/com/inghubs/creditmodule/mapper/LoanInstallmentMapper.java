package com.inghubs.creditmodule.mapper;

import com.inghubs.creditmodule.dto.LoanInstallmentDTO;
import com.inghubs.creditmodule.entity.LoanInstallment;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class LoanInstallmentMapper {

    private final ModelMapper modelMapper;

    public LoanInstallmentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public LoanInstallmentDTO toDTO(LoanInstallment loanInstallment) {
        return modelMapper.map(loanInstallment, LoanInstallmentDTO.class);
    }

    public LoanInstallment toEntity(LoanInstallmentDTO loanInstallmentDTO) {
        return modelMapper.map(loanInstallmentDTO, LoanInstallment.class);
    }
}
