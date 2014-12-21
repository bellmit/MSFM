package com.cboe.domain.util;

import com.cboe.idl.cmiConstants.ActivityTypesOperations;

/**
 * This class is designed to provide the facility to define Spread leg normalization types
 */

public class SpreadNormalizationStrategyTypes {
    // Least denominator for stock leg 100, A 2:100 CPX will be 2:100 ratio, cannot be 1:50 since stock leg has to be 100
    public static final short STOCK_LEG_FULL_LOT_NORMALIZATION = (short) 1; 
    
   // Least denominator for stock leg is 1.  This will make the CPX strategy to be normalized like regular strategy
   // A 2:100 CPX will become 1:50 ratio 
    public static final short NO_STOCK_LEG_FULL_LOT_NORMALIZATION = (short) 2;  

    public SpreadNormalizationStrategyTypes() {
    }
}