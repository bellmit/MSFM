package com.cboe.internalPresentation.product;

import com.cboe.idl.product.ProductClassStruct;
import com.cboe.interfaces.internalPresentation.product.ProductClassDetail;

public class ProductClassDetailFactory
{

    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private ProductClassDetailFactory()
    {}

    /**
     * Creates an instance of a ProductClassDetail from a ProductClassStruct.
     * @param productClassStruct to wrap in instance of ProductClassDetail
     * @return ProductClassDetail to represent the ProductClassStruct
     */
    public static ProductClassDetail create(ProductClassStruct productClassStruct)
    {
        if (productClassStruct == null)
        {
            throw new IllegalArgumentException();
        }
        ProductClassDetail productClassDetail = new ProductClassDetailImpl(productClassStruct);

        return productClassDetail;
    }

}

