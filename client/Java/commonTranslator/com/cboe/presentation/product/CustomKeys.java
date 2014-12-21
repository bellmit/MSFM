//
// -----------------------------------------------------------------------------------
// Source file:
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiConstants.ProductClass;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiConstants.StrategyTypes;

/**
 * Contains custom keys that do not exist in the server but that we are using in the GUI.
 */
public interface CustomKeys
{
    public static final int DEFAULT_CLASS_KEY = ProductClass.DEFAULT_CLASS_KEY;
    public static final int DEFAULT_PRODUCT_KEY = 3;
    public static final int DEFAULT_STRATEGY_PRODUCT_KEY = 5;
    public static final int DEFAULT_STRATEGY_TYPE = StrategyTypes.UNKNOWN;
    public static final short DEFAULT_PRODUCT_TYPE = ProductTypes.OPTION;
    public static final int ALL_SELECTED_CLASS_KEY = 2;
    public static final int ALL_SELECTED_PRODUCT_KEY = 4;
    public static final short ALL_SELECTED_PRODUCT_TYPE = ProductTypes.OPTION;
}
