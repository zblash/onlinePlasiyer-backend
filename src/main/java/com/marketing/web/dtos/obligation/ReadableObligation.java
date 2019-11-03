package com.marketing.web.dtos.obligation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableObligation implements Serializable {

    private double debt;

    private double receivable;

}
