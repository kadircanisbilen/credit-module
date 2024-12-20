package com.inghubs.creditmodule.mapper;

import com.inghubs.creditmodule.dto.LoanInstallmentDTO;
import com.inghubs.creditmodule.entity.LoanInstallment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LoanInstallmentMapperTest {

    @InjectMocks
    private LoanInstallmentMapper loanInstallmentMapper;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void shouldMapLoanInstallmentEntityToLoanInstallmentDTO() {
        // given
        LoanInstallment loanInstallment = new LoanInstallment();
        loanInstallment.setId(1L);
        loanInstallment.setAmount(BigDecimal.valueOf(1000));
        loanInstallment.setPaidAmount(BigDecimal.valueOf(500));
        loanInstallment.setDueDate(LocalDate.of(2024, 12, 31));
        loanInstallment.setPaymentDate(LocalDate.of(2024, 12, 20));
        loanInstallment.setIsPaid(true);

        LoanInstallmentDTO loanInstallmentDTO = new LoanInstallmentDTO();
        loanInstallmentDTO.setId(1L);
        loanInstallmentDTO.setAmount(BigDecimal.valueOf(1000));
        loanInstallmentDTO.setPaidAmount(BigDecimal.valueOf(500));
        loanInstallmentDTO.setDueDate(LocalDate.of(2024, 12, 31));
        loanInstallmentDTO.setPaymentDate(LocalDate.of(2024, 12, 20));
        loanInstallmentDTO.setIsPaid(true);

        Mockito.when(modelMapper.map(loanInstallment, LoanInstallmentDTO.class)).thenReturn(loanInstallmentDTO);

        // when
        LoanInstallmentDTO result = loanInstallmentMapper.toDTO(loanInstallment);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(loanInstallment.getId());
        assertThat(result.getAmount()).isEqualTo(loanInstallment.getAmount());
        assertThat(result.getPaidAmount()).isEqualTo(loanInstallment.getPaidAmount());
        assertThat(result.getDueDate()).isEqualTo(loanInstallment.getDueDate());
        assertThat(result.getPaymentDate()).isEqualTo(loanInstallment.getPaymentDate());
        assertThat(result.getIsPaid()).isEqualTo(loanInstallment.getIsPaid());

        Mockito.verify(modelMapper).map(loanInstallment, LoanInstallmentDTO.class);
    }

    @Test
    void shouldMapLoanInstallmentDTOToLoanInstallmentEntity() {
        // given
        LoanInstallmentDTO loanInstallmentDTO = new LoanInstallmentDTO();
        loanInstallmentDTO.setId(1L);
        loanInstallmentDTO.setAmount(BigDecimal.valueOf(1000));
        loanInstallmentDTO.setPaidAmount(BigDecimal.valueOf(500));
        loanInstallmentDTO.setDueDate(LocalDate.of(2024, 12, 31));
        loanInstallmentDTO.setPaymentDate(LocalDate.of(2024, 12, 20));
        loanInstallmentDTO.setIsPaid(true);

        LoanInstallment loanInstallment = new LoanInstallment();
        loanInstallment.setId(1L);
        loanInstallment.setAmount(BigDecimal.valueOf(1000));
        loanInstallment.setPaidAmount(BigDecimal.valueOf(500));
        loanInstallment.setDueDate(LocalDate.of(2024, 12, 31));
        loanInstallment.setPaymentDate(LocalDate.of(2024, 12, 20));
        loanInstallment.setIsPaid(true);

        Mockito.when(modelMapper.map(loanInstallmentDTO, LoanInstallment.class)).thenReturn(loanInstallment);

        // when
        LoanInstallment result = loanInstallmentMapper.toEntity(loanInstallmentDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(loanInstallmentDTO.getId());
        assertThat(result.getAmount()).isEqualTo(loanInstallmentDTO.getAmount());
        assertThat(result.getPaidAmount()).isEqualTo(loanInstallmentDTO.getPaidAmount());
        assertThat(result.getDueDate()).isEqualTo(loanInstallmentDTO.getDueDate());
        assertThat(result.getPaymentDate()).isEqualTo(loanInstallmentDTO.getPaymentDate());
        assertThat(result.getIsPaid()).isEqualTo(loanInstallmentDTO.getIsPaid());

        Mockito.verify(modelMapper).map(loanInstallmentDTO, LoanInstallment.class);
    }
}
