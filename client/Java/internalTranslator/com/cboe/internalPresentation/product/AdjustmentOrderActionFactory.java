// -----------------------------------------------------------------------------------
// Source file: AdjustmentTypeFactory.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.product;

import com.cboe.interfaces.internalPresentation.product.AdjustmentOrderAction;
import com.cboe.internalPresentation.common.formatters.AdjustmentOrderActions;

/**
 *  Factory for creating instances of ProductType
 */
public class AdjustmentOrderActionFactory
{
    private static AdjustmentOrderAction[] orderActions = {
        create(com.cboe.idl.constants.AdjustmentOrderActions.NORMAL_ADJUSTMENT),
        create(com.cboe.idl.constants.AdjustmentOrderActions.CANCEL_ALL_ORDERS) };

    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private AdjustmentOrderActionFactory()
    {}

    /**
     * Creates an instance of a AdjustmentType using passed in type.
     * @param type short to use
     * @return AdjustmentType
     */
    public static AdjustmentOrderAction create(short action)
    {
        AdjustmentOrderAction adjustmentOrderAction = new AdjustmentOrderActionImpl(AdjustmentOrderActions.toString(action), action);

        return adjustmentOrderAction;
    }

    /**
     * Creates an instance of a AdjustmentType using passed in type.
     * @param type short to use
     * @return AdjustmentType
     */
    public static AdjustmentOrderAction[] getAllAdjustmentOrderActions()
    {
        return orderActions;
    }

}
