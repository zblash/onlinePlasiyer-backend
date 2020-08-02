package com.marketing.web.dtos.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableConfirmOrder implements Serializable {

    @NotNull(message = "{validation.notNull}")
    private List<WritableConfirmOrderItem> items;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate waybillDate;

}
