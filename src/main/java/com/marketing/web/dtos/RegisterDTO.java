package com.marketing.web.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.marketing.web.models.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO implements Serializable {

    @NotBlank
    @Size(min = 3,max = 20)
    private String userName;

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
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String details;

    @NotNull
    private RoleType roleType;

}
