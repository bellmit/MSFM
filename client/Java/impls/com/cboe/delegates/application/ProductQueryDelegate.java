package com.cboe.delegates.application;

import com.cboe.interfaces.application.ProductQueryManager;

public class ProductQueryDelegate extends com.cboe.idl.floorApplication.POA_ProductQueryV2_tie {
    public ProductQueryDelegate(ProductQueryManager delegate) {
        super(delegate);
    }
}
