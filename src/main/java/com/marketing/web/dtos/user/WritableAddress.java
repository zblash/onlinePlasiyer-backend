package com.marketing.web.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WritableAddress implements Serializable {

    @NotBlank
    private String cityId;

    @NotBlank
    private String stateId;

    @NotBlank
    private String details;
}
