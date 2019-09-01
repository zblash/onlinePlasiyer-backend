package com.marketing.web.dtos.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marketing.web.dtos.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchOrder extends DTO {

    private Date startDate;

    private Date endDate;

    private String userName;

    @JsonIgnore
    private Long sellerId;

    @JsonIgnore
    private Long buyerId;
}
