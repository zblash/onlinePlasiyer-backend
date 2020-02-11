package com.marketing.web.dtos.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableConfirmOrderItem implements Serializable {

    @NotBlank(message = "{validation.notBlank}")
    private String id;

    @NotNull(message = "{validation.notNull}")
    private int quantity;

    @NotNull(message = "{validation.notNull}")
    private boolean removed;

}
