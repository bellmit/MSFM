// -----------------------------------------------------------------------------------
// Source file: AdjustmentTypeFactory.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.interfaces.presentation.product.ProductType;
import com.cboe.interfaces.presentation.product.AdjustmentType;
import com.cboe.presentation.common.formatters.PriceAdjustmentTypes;

/**
 *  Factory for creating instances of ProductType
 */
public class AdjustmentTypeFactory
{
    private static AdjustmentType[] adjustmentTypes = {
        create(com.cboe.idl.cmiConstants.PriceAdjustmentTypes.COMMON_DISTRIBUTION),
        create(com.cboe.idl.cmiConstants.PriceAdjustmentTypes.DIVIDEND_CASH),
        create(com.cboe.idl.cmiConstants.PriceAdjustmentTypes.DIVIDEND_PERCENT),
        create(com.cboe.idl.cmiConstants.PriceAdjustmentTypes.DIVIDEND_STOCK),
        create(com.cboe.idl.cmiConstants.PriceAdjustmentTypes.LEAP_ROLLOVER),
        create(com.cboe.idl.cmiConstants.PriceAdjustmentTypes.MERGER),
        create(com.cboe.idl.cmiConstants.PriceAdjustmentTypes.SPLIT),
        create(com.cboe.idl.cmiConstants.PriceAdjustmentTypes.SYMBOL_CHANGE) };

    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private AdjustmentTypeFactory()
    {}

    /**
     * Creates an instance of a AdjustmentType using passed in type.
     * @param type short to use
     * @return AdjustmentType
     */
    public static AdjustmentType create(short type)
    {
        AdjustmentType adjustmentType = new AdjustmentTypeImpl(PriceAdjustmentTypes.toString(type), type);

        return adjustmentType;
    }

    /**
     * Creates an instance of a AdjustmentType using passed in type.
     * @param type short to use
     * @return AdjustmentType
     */
    public static AdjustmentType[] getAllAdjustmentTypes()
    {
        return adjustmentTypes;
    }

}
