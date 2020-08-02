package com.marketing.web.dtos.shippingDays;

import com.marketing.web.enums.DaysOfWeek;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableShippingDays implements Serializable {

    @NotBlank(message = "{validation.notBlank}")
    private String stateId;

    @NotNull(message = "{validation.notNull}")
    private List<DaysOfWeek> days;

}
