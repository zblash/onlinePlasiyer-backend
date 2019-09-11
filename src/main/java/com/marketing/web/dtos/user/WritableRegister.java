package com.marketing.web.dtos.user;

import com.marketing.web.dtos.DTO;
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

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableRegister extends DTO {

    @NotBlank
    @Size(min = 3,max = 20)
    private String username;

    @NotBlank
    @Size(min = 3,max = 20)
    private String name;

    @NotBlank
    @Size(min = 5, max = 90)
    private String password;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String taxNumber;

    @NotBlank
    private String cityId;

    @NotBlank
    private String stateId;

    @NotBlank
    private String details;

    @NotNull
    private RoleType roleType;

}
