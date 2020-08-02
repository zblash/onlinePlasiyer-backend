package com.marketing.web.dtos.user.writable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WritableActivation implements Serializable {

    @NotBlank(message = "{validation.notBlank}")
    private String activationToken;
}
