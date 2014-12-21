package com.cboe.domain.util;

import com.cboe.idl.cmiOrder.CancelReportStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderCancelRequestStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;

public class HeldOrderCancelRequestContainer
{

    private HeldOrderCancelRequestStruct cancelRequestStruct;
    private ProductKeysStruct productKeysStruct;

    /**
      * Sets the internal fields to the passed values
      */
    public HeldOrderCancelRequestContainer(ProductKeysStruct productKeys, HeldOrderCancelRequestStruct cancelRequest)
    {
        this.productKeysStruct = productKeys;
        this.cancelRequestStruct = cancelRequest;
    }

    public ProductKeysStruct getProductKeyes()
    {
        return productKeysStruct;
    }

    public HeldOrderCancelRequestStruct getCancelRequest()
    {
        return cancelRequestStruct;
    }

}
