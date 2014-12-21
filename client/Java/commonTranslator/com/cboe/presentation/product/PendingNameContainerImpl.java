package com.cboe.presentation.product;

import com.cboe.interfaces.presentation.product.Product;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.interfaces.presentation.product.PendingNameContainer;
import com.cboe.idl.cmiProduct.PendingNameStruct;

public class PendingNameContainerImpl implements PendingNameContainer
{
    private short action;
    private Product product;
    private ProductNameStruct productName;

    public PendingNameContainerImpl(PendingNameStruct name) {
        action = name.action;
        product = ProductFactoryHome.find().create(name.productStruct);
        productName = name.pendingProductName;
    }

    public short getAction() {
        return action;
    }

    public Product getProduct() {
        return product;
    }

    public ProductNameStruct getProductNameStruct() {
        return productName;
    }
}