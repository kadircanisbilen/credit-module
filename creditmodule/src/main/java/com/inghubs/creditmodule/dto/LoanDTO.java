package com.inghubs.creditmodule.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanDTO {

    private Long id;

    @NotNull(message = "user.id.not.null")
    private Long userId;

    @DecimalMin(value = "0.01", message = "loan.amount.min")
    @NotNull
    private BigDecimal loanAmount;

    @Pattern(regexp = "6|9|12|24", message = "Number of installments must be 6, 9, 12, or 24")
    private Integer numberOfInstallments;

    private LocalDate createDate;

    @NotNull
    private Boolean isPaid;

    @NotNull
    @DecimalMin(value = "0.1", message = "Interest rate must be at least 0.1")
    @DecimalMax(value = "0.5", message = "Interest rate must not exceed 0.5")
    private BigDecimal interestRate;
}
