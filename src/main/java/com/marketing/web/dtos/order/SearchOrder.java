package com.marketing.web.dtos.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marketing.web.dtos.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchOrder extends DTO {

    @NotNull
    private Date startDate;

    private Date endDate;

}
