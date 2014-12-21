package com.cboe.internalPresentation.product;

import com.cboe.idl.product.ProductSettlementStruct;
import com.cboe.idl.product.ProductOpenInterestStruct;
import com.cboe.interfaces.internalPresentation.product.ProductSettlementModel;
import com.cboe.interfaces.internalPresentation.product.ProductOpenInterestModel;
import com.cboe.interfaces.internalPresentation.product.ProductSettlementOpenInterest;
import com.cboe.interfaces.internalPresentation.product.ProductSettlementOpenInterestModel;

public class ProductSettlementOpenInterestFactory
{

    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private ProductSettlementOpenInterestFactory()
    {}

    /**
     * Returns an instance of a ProductSettlementOpenInterestModel from a passed in 
     * ProductSettlementStruct and ProductOpenInterestStruct.
     * @param ProductSettlementStruct to wrap in instance of ProductSettlement
     * @param ProductOpenInterestStruct to wrap in instance of ProductOpenInterest
     * @return ProductSettlementOpenInterestModel to represent the ProductSettlementStruct
     */
    public static ProductSettlementOpenInterestModel createProductSettlementOpenInterestModel(ProductSettlementStruct productSettlementStruct, ProductOpenInterestStruct productOpenInterestStruct)
    {
        ProductSettlementOpenInterestModel productSettlement = null;
        
        if ( productSettlementStruct == null )
        {
            throw new IllegalArgumentException("ProductSettlementStruct can not be null.");
        }
        if ( productOpenInterestStruct == null )
        {
            throw new IllegalArgumentException("ProductOpenInterestStruct can not be null.");
        }
        
        productSettlement = new ProductSettlementOpenInterestModelImpl(productSettlementStruct, productOpenInterestStruct);

        return productSettlement;
    }

    /**
     * Returns an instance of a ProductSettlementOpenInterest from a passed in 
     * ProductSettlementStruct and ProductOpenInterestStruct.
     * @param ProductSettlementStruct to wrap in instance of ProductSettlement
     * @param ProductOpenInterestStruct to wrap in instance of ProductOpenInterest
     * @return ProductSettlementOpenInterest to represent the ProductSettlementStruct
     */
    public static ProductSettlementOpenInterest createProductSettlementOpenInterest(ProductSettlementStruct productSettlementStruct, ProductOpenInterestStruct productOpenInterestStruct)
    {
        ProductSettlementOpenInterestModel productSettlement = createProductSettlementOpenInterestModel(productSettlementStruct, productOpenInterestStruct);

        return (ProductSettlementOpenInterest)productSettlement;
    }

    /**
     * Creates an instance of a ProductSettlementOpenInterestModelImpl from a passed in ProductSettlementModel and ProductOpenInterestModel.
     * @param ProductSettlementStruct to wrap in instance of ProductSettlementModel
     * @return ProductSettlementOpenInterestModel to represent the ProductSettlementStruct
     */
    public static ProductSettlementOpenInterestModel createProductSettlementOpenInterestModel(ProductSettlementModel productSettlementModel, ProductOpenInterestModel productOpenInterestModel)
    {
        ProductSettlementOpenInterestModel productSettlementOpenInterestModel = new ProductSettlementOpenInterestModelImpl(productSettlementModel, productOpenInterestModel);
        
        return productSettlementOpenInterestModel;
    }

    /**
     * Creates an instance of a ProductSettlementOpenInterest from a passed in ProductSettlementModel and ProductOpenInterestModel.
     * @param ProductSettlementStruct to wrap in instance of ProductSettlementModel
     * @return ProductSettlementOpenInterestModel to represent the ProductSettlementStruct
     */
    public static ProductSettlementOpenInterest createProductSettlementOpenInterest(ProductSettlementModel productSettlementModel, ProductOpenInterestModel productOpenInterestModel)
    {
        ProductSettlementOpenInterestModel productSettlementOpenInterestModel = createProductSettlementOpenInterestModel(productSettlementModel, productOpenInterestModel);
        
        return (ProductSettlementOpenInterest)productSettlementOpenInterestModel;
    }

}
