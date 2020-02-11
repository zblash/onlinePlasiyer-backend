package com.marketing.web.dtos.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableConfirmOrder implements Serializable {

    @NotNull(message = "{validation.notNull}")
    private List<WritableConfirmOrderItem> items;

}
