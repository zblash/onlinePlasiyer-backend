package com.marketing.web.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WritableUserInfo implements Serializable {

    @NotBlank(message = "{validation.notBlank}")
    private String name;

    @NotBlank(message = "{validation.notBlank}")
    @Email(message = "{validation.email}")
    private String email;

    @NotNull(message = "{validation.notNull}")
    private WritableAddress address;
}
