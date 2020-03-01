package com.marketing.web.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadableAddress implements Serializable {

    private String cityId;

    private String cityName;

    private String stateId;

    private String stateName;

    private String details;

}
