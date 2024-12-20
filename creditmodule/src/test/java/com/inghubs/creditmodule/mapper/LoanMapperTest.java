package com.inghubs.creditmodule.mapper;

import com.inghubs.creditmodule.dto.LoanDTO;
import com.inghubs.creditmodule.entity.Loan;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LoanMapperTest {

    @InjectMocks
    private LoanMapper loanMapper;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void shouldMapLoanEntityToLoanDTO() {
        // given
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setLoanAmount(BigDecimal.valueOf(5000));
        loan.setNumberOfInstallments(12);
        loan.setInterestRate(BigDecimal.valueOf(0.15));

        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setId(1L);
        loanDTO.setLoanAmount(BigDecimal.valueOf(5000));
        loanDTO.setNumberOfInstallments(12);
        loanDTO.setInterestRate(BigDecimal.valueOf(0.15));

        Mockito.when(modelMapper.map(loan, LoanDTO.class)).thenReturn(loanDTO);

        // when
        LoanDTO result = loanMapper.toDTO(loan);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(loan.getId());
        assertThat(result.getLoanAmount()).isEqualTo(loan.getLoanAmount());
        assertThat(result.getNumberOfInstallments()).isEqualTo(loan.getNumberOfInstallments());
        assertThat(result.getInterestRate()).isEqualTo(loan.getInterestRate());

        Mockito.verify(modelMapper).map(loan, LoanDTO.class);
    }

    @Test
    void shouldMapLoanDTOToLoanEntity() {
        // given
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setId(1L);
        loanDTO.setLoanAmount(BigDecimal.valueOf(10000));
        loanDTO.setNumberOfInstallments(24);
        loanDTO.setInterestRate(BigDecimal.valueOf(0.2));

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setLoanAmount(BigDecimal.valueOf(10000));
        loan.setNumberOfInstallments(24);
        loan.setInterestRate(BigDecimal.valueOf(0.2));

        Mockito.when(modelMapper.map(loanDTO, Loan.class)).thenReturn(loan);

        // when
        Loan result = loanMapper.toEntity(loanDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(loanDTO.getId());
        assertThat(result.getLoanAmount()).isEqualTo(loanDTO.getLoanAmount());
        assertThat(result.getNumberOfInstallments()).isEqualTo(loanDTO.getNumberOfInstallments());
        assertThat(result.getInterestRate()).isEqualTo(loanDTO.getInterestRate());

        Mockito.verify(modelMapper).map(loanDTO, Loan.class);
    }
}
