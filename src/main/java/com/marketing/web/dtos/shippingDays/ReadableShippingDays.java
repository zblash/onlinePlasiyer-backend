package com.marketing.web.dtos.shippingDays;

import com.marketing.web.enums.DaysOfWeek;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableShippingDays implements Serializable {

    private String id;

    private String merchantId;

    private String merchantName;

    private List<DaysOfWeek> days;

    private String stateId;

    private String stateName;

    private String cityId;

    private String cityName;
}
