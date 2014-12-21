package com.cboe.internalPresentation.product;

import com.cboe.idl.product.ProductClassStruct;
//import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.interfaces.internalPresentation.product.ProductClassModel;
import com.cboe.interfaces.internalPresentation.product.ProductClassDetail;
import com.cboe.presentation.product.ProductClassFactory;

public class ProductClassModelFactory
{

    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private ProductClassModelFactory()
    {}

    /**
     * Creates an instance of a ProductClassModelImpl from a passed in ProductClassStruct.
     * @param ProductClassStruct to wrap in instance of ProductClassModel
     * @return ProductClassModel to represent the ProductClassStruct
     */
    public static ProductClassModel create(ProductClassStruct productClassStruct)
    {
        if (productClassStruct == null)
        {
            throw new IllegalArgumentException("ProductClassStruct can not be null");
        }
        ProductClassModel productClassModel = new ProductClassModelImpl(productClassStruct);

        return productClassModel;
    }

    /**
     * Creates an instance of a ProductClassModelImpl from a passed in ProductClassStruct.
     * @param ProductClassStruct to wrap in instance of ProductClassModel
     * @return ProductClassModel to represent the ProductClassStruct
     */
    public static ProductClassModel create(ProductClassDetail productClassDetail)
    {
        if (productClassDetail == null)
        {
            throw new IllegalArgumentException("ProductClassDetail can not be null");
        }
        return create(productClassDetail.getProductClassStruct());
    }

}
