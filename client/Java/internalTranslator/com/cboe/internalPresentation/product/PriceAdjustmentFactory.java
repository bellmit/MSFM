package com.cboe.internalPresentation.product;

import com.cboe.idl.product.PriceAdjustmentStruct;
import com.cboe.interfaces.internalPresentation.product.PriceAdjustment;
import com.cboe.interfaces.internalPresentation.product.PriceAdjustmentModel;
import com.cboe.domain.util.ProductStructBuilder;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Chicago Board Options Exchange
 * @author Joel Davisson
 * @version 1.0
 */

public class PriceAdjustmentFactory
{
    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private PriceAdjustmentFactory()
    {
    }

    /**
     * Creates an instance of a PriceAdjustmentImpl from a passed in PriceAdjustmentStruct.
     * @param PriceAdjustmentStruct to wrap in instance of PriceAdjustment
     * @return PriceAdjustment to represent the PriceAdjustmentStruct
     */
    public static PriceAdjustment createPriceAdjustment(PriceAdjustmentStruct priceAdjustmentStruct)
    {
        if (priceAdjustmentStruct == null)
        {
            throw new IllegalArgumentException("PriceAdjustmentStruct can not be null");
        }
        return (PriceAdjustment)createPriceAdjustmentModel(priceAdjustmentStruct);
    }


    /**
     * Creates an instance of a PriceAdjustmentModelImpl from a passed in PriceAdjustmentStruct.
     * @param PriceAdjustmentStruct to wrap in instance of PriceAdjustmentModel
     * @return PriceAdjustmentModel to represent the PriceAdjustmentStruct
     */
    public static PriceAdjustmentModel createPriceAdjustmentModel(PriceAdjustmentStruct priceAdjustmentStruct)
    {
        if (priceAdjustmentStruct == null)
        {
            throw new IllegalArgumentException("PriceAdjustmentStruct can not be null");
        }

        return new PriceAdjustmentModelImpl(priceAdjustmentStruct);
    }

    /**
     * Creates a new default instance of a PriceAdjustmentModelImpl.
     * @return PriceAdjustmentModel to new PriceAdjustmentModel
     */
    public static PriceAdjustmentModel createPriceAdjustmentModel()
    {
        PriceAdjustmentStruct struct = ProductStructBuilder.buildPriceAdjustmentStruct();
        return createPriceAdjustmentModel(struct);
    }

}