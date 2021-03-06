package com.marketing.web.dtos.websockets;

import com.marketing.web.dtos.product.ReadableProductSpecify;
import com.marketing.web.enums.WsStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrapperWsProductSpecify implements Serializable {

    private WsStatus status;

    private String productName;

    private String productId;

    private ReadableProductSpecify productSpecify;
}
