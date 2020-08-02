package com.marketing.web.dtos.user.register;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableMerchantRegister extends BaseRegister {


    @NotBlank(message = "{validation.notBlank}")
    private String taxNumber;

    @NotNull
    private Set<String> activeStates;
}
