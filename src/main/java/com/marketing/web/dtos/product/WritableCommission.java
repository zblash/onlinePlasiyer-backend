package com.marketing.web.dtos.product;

import com.marketing.web.enums.CommissionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableCommission implements Serializable {

    @NotNull(message = "{validation.notNull}")
    private CommissionType commissionType;

    private String id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "{validation.notNull}")
    private double commission;

}
