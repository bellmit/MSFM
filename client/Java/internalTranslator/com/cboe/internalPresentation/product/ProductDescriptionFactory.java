package com.cboe.internalPresentation.product;

import com.cboe.idl.cmiProduct.ProductDescriptionStruct;
import com.cboe.interfaces.internalPresentation.product.ProductDescription;
import com.cboe.interfaces.internalPresentation.product.ProductDescriptionModel;
import com.cboe.domain.util.ProductStructBuilder;

public class ProductDescriptionFactory
{
//    // this incremented 'key' is a work around because in ProductDescriptionModelImpl we have to implement the abstract hashcode() method in AbstractMutableBusinessModel; since it's abstract, we can't just use super.hashcode() (Object.hashcode())
//    // in another branch this has been
//    private static int lastAssignedKey = 0;

    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private ProductDescriptionFactory()
    {}

    /**
     * Creates an instance of a ProductDescription from a ProductDescriptionStruct.
     * @param productDescriptionStruct to wrap in instance of ProductDescription
     * @return ProductDescription to represent the ProductDescriptionStruct
     */
    public static ProductDescription createProductDescription(ProductDescriptionStruct productDescriptionStruct)
    {
        if (productDescriptionStruct == null)
        {
            throw new IllegalArgumentException("ProductDescriptionStruct can not be null");
        }

    return (ProductDescription)createProductDescriptionModel(productDescriptionStruct);
    }

    /**
     * Creates an instance of a ProductDescription from a ProductDescriptionStruct.
     * @param productDescriptionStruct to wrap in instance of ProductDescription
     * @return ProductDescription to represent the ProductDescriptionStruct
     */
    public static ProductDescriptionModel createProductDescriptionModel(ProductDescriptionStruct productDescriptionStruct)
    {
        if (productDescriptionStruct == null)
        {
            throw new IllegalArgumentException("ProductDescriptionStruct can not be null");
        }
        ProductDescriptionModel productDescriptionModel = null;
//        productDescriptionModel = new ProductDescriptionModelImpl(productDescriptionStruct, ++lastAssignedKey);
        productDescriptionModel = new ProductDescriptionModelImpl(productDescriptionStruct);

        return productDescriptionModel;
    }

    /**
     * Creates a new instance of a ProductDescription.
     * @return new ProductDescription
     */
    public static ProductDescriptionModel createProductDescriptionModel()
    {
        ProductDescriptionStruct struct = ProductStructBuilder.buildProductDescriptionStruct();
        return createProductDescriptionModel(struct);
    }

}

