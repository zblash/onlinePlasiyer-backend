package com.marketing.web.utils.facade;

import com.marketing.web.dtos.product.ReadableProductSpecify;
import com.marketing.web.dtos.product.WritableProductSpecify;
import com.marketing.web.models.Merchant;
import com.marketing.web.models.User;

public interface ProductFacade {

    ReadableProductSpecify createProductSpecify(WritableProductSpecify writableProductSpecify, Merchant merchant);

    ReadableProductSpecify updateProductSpecify(String uuid, WritableProductSpecify writableProductSpecify, Merchant merchant);
}
