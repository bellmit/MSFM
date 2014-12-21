package com.cboe.delegates.application;

import com.cboe.interfaces.application.ProductDefinition;

public class ProductDefinitionDelegate extends com.cboe.idl.cmi.POA_ProductDefinition_tie {
    public ProductDefinitionDelegate(ProductDefinition delegate) {
        super(delegate);
    }
}
