package com.marketing.web.dtos.user;

import com.marketing.web.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableRegister implements Serializable {

    @NotBlank(message = "{validation.notBlank}")
    private String username;

    @NotBlank(message = "{validation.notBlank}")
    private String name;

    @NotBlank(message = "{validation.notBlank}")
    @Size(min = 5,max = 90, message = "{validation.size}")
    private String password;

    @Email(message = "{validation.email}")
    @NotBlank(message = "{validation.notBlank}")
    private String email;

    @NotBlank(message = "{validation.notBlank}")
    private String taxNumber;

    @NotBlank(message = "{validation.notBlank}")
    private String cityId;

    @NotBlank(message = "{validation.notBlank}")
    private String stateId;

    @NotBlank(message = "{validation.notBlank}")
    private String details;

    @NotNull(message = "{validation.notNull}")
    private RoleType roleType;

    private boolean status;

}
