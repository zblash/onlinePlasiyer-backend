package com.marketing.web.dtos.user.writable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WritableLogin implements Serializable {

    @NotBlank(message = "{validation.notBlank}")
    private String username;

    @NotBlank(message = "{validation.notBlank}")
    private String password;
}
