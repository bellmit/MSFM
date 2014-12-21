package com.cboe.internalPresentation.product;

import com.cboe.idl.product.PriceAdjustmentClassStruct;
import com.cboe.interfaces.internalPresentation.product.PriceAdjustmentClassModel;

public class PriceAdjustmentClassModelFactory
{

    private PriceAdjustmentClassModelFactory()
    {
    }

    /**
     * Creates an instance of a PriceAdjustmentClassModelImpl from a passed in PriceAdjustmentClassStruct.
     * @param PriceAdjustmentClassStruct to wrap in instance of PriceAdjustmentClassModel
     * @return PriceAdjustmentClassModel to represent the PriceAdjustmentClassStruct
     */
    public static PriceAdjustmentClassModel create(PriceAdjustmentClassStruct priceAdjustmentClassStruct)
    {
        if (priceAdjustmentClassStruct == null)
        {
            throw new IllegalArgumentException("PriceAdjustmentClassStruct can not be null");
        }
        PriceAdjustmentClassModel priceAdjustmentClassModel = new PriceAdjustmentClassModelImpl(priceAdjustmentClassStruct);

        return priceAdjustmentClassModel;
    }
}

