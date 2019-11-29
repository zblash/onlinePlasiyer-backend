package com.marketing.web.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WritablePasswordReset implements Serializable {

    @NotBlank
    @Size(min = 5,max = 90)
    private String password;

    @NotBlank
    @Size(min = 5, max = 90)
    private String passwordConfirmation;
}
