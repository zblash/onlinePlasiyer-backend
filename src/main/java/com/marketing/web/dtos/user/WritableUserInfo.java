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

    @NotBlank
    @Size(min = 3,max = 20)
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private WritableAddress address;
}
