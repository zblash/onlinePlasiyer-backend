package com.marketing.web.dtos.websockets;

import com.marketing.web.dtos.product.ReadableProductSpecify;
import com.marketing.web.enums.WsStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrapperWsProductSpecify implements Serializable {

    private WsStatus status;

    private ReadableProductSpecify productSpecify;
}
