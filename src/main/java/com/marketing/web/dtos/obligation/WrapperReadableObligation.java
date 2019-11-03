package com.marketing.web.dtos.obligation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrapperReadableObligation implements Serializable {

    private List<ReadableObligation> values;

    private double totalDebts;

    private double totalReceivables;

}
