package com.marketing.web.dtos.user.register;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseRegister implements Serializable {

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

    @Pattern(regexp="(^$|[0-9]{10})")
    @NotBlank(message = "{validation.notBlank}")
    private String phoneNumber;

    @NotBlank(message = "{validation.notBlank}")
    private String cityId;

    @NotBlank(message = "{validation.notBlank}")
    private String stateId;

    @NotBlank(message = "{validation.notBlank}")
    private String details;

}
