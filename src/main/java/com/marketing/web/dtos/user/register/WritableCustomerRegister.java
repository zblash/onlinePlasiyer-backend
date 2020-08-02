package com.marketing.web.dtos.user.register;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableCustomerRegister extends BaseRegister {

    @NotBlank(message = "{validation.notBlank}")
    private String taxNumber;

}
