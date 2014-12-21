package com.cboe.domain.util;

import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.product.ProductInformationStruct;

/**
 * Created by IntelliJ IDEA.
 * User: krueyay
 * Date: Dec 18, 2007
 * Time: 1:07:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProductStatusGroupUpdateProductContainer
{
    private ProductStruct product;
    private ProductInformationStruct productInformation;

    public ProductStatusGroupUpdateProductContainer(ProductStruct product, ProductInformationStruct productInformation)
    {
        this.product = product;
        this.productInformation = productInformation;
    }

    public ProductStruct getProductStruct()
    {
        return product;
    }

    public ProductInformationStruct getProductInformationStruct()
    {
        return productInformation;
    }
}
