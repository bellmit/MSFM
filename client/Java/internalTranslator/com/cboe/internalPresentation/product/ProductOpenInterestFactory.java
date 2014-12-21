package com.cboe.internalPresentation.product;

import com.cboe.idl.product.ProductOpenInterestStruct;
import com.cboe.interfaces.internalPresentation.product.ProductOpenInterest;
import com.cboe.interfaces.internalPresentation.product.ProductOpenInterestModel;

public class ProductOpenInterestFactory
{

    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private ProductOpenInterestFactory()
    {}

    /**
     * Creates an instance of a ProductOpenInterestImpl from a passed in ProductOpenInterestStruct.
     * @param ProductOpenInterestStruct to wrap in instance of ProductOpenInterest
     * @return ProductOpenInterest to represent the ProductOpenInterestStruct
     */
    public static ProductOpenInterest createProductOpenInterest(ProductOpenInterestStruct productOpenInterestStruct)
    {
        ProductOpenInterestModel productOpenInterestModel = null;
        
        productOpenInterestModel = new ProductOpenInterestModelImpl(productOpenInterestStruct);

        return (ProductOpenInterest)productOpenInterestModel;
    }

    /**
     * Creates an instance of a ProductOpenInterestModelImpl from a passed in ProductOpenInterestStruct.
     * @param ProductOpenInterestStruct to wrap in instance of ProductOpenInterestModel
     * @return ProductOpenInterestModel to represent the ProductOpenInterestStruct
     */
    public static ProductOpenInterestModel createProductOpenInterestModel(ProductOpenInterestStruct productOpenInterestStruct)
    {
        if ( productOpenInterestStruct == null )
        {
            throw new IllegalArgumentException("ProductOpenInterestStruct can not be null.");
        }
        
        ProductOpenInterestModel productOpenInterestModel = new ProductOpenInterestModelImpl(productOpenInterestStruct);
        
        return productOpenInterestModel;
    }

}
