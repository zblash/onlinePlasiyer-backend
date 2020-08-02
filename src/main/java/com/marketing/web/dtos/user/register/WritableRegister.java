package com.marketing.web.dtos.user.register;

import com.marketing.web.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableRegister extends BaseRegister {

    @NotNull(message = "{validation.notNull}")
    private RoleType roleType;

    private String taxNumber;

    private boolean status;

    private Set<String> activeStates;

}
