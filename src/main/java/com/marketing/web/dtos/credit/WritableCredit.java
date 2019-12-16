package com.marketing.web.dtos.credit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WritableCredit implements Serializable {

    @NotBlank
    private String userId;

    @NotNull
    private double totalDebt;

    @NotNull
    private double creditLimit;

}
