package com.cboe.internalPresentation.product;

import com.cboe.idl.product.ProductSettlementStruct;
import com.cboe.interfaces.internalPresentation.product.ProductSettlement;
import com.cboe.interfaces.internalPresentation.product.ProductSettlementModel;

public class ProductSettlementFactory
{

    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private ProductSettlementFactory()
    {}

    /**
     * Creates an instance of a ProductSettlementImpl from a passed in ProductSettlementStruct.
     * @param ProductSettlementStruct to wrap in instance of ProductSettlement
     * @return ProductSettlement to represent the ProductSettlementStruct
     */
    public static ProductSettlement createProductSettlement(ProductSettlementStruct productSettlementStruct)
    {
        ProductSettlementModel productSettlement = null;
        
        if ( productSettlementStruct == null )
        {
            throw new IllegalArgumentException("ProductSettlementStruct can not be null.");
        }
        
        productSettlement = new ProductSettlementModelImpl(productSettlementStruct);

        return (ProductSettlement)productSettlement;
    }

    /**
     * Creates an instance of a ProductSettlementModelImpl from a passed in ProductSettlementStruct.
     * @param ProductSettlementStruct to wrap in instance of ProductSettlementModel
     * @return ProductSettlementModel to represent the ProductSettlementStruct
     */
    public static ProductSettlementModel createProductSettlementModel(ProductSettlementStruct productSettlementStruct)
    {
        ProductSettlementModel productSettlementModel = new ProductSettlementModelImpl(productSettlementStruct);
        
        return productSettlementModel;
    }

}
