//
// -----------------------------------------------------------------------------------
// Source file: SessionStrategyLeg.java
//
// PACKAGE: com.cboe.interfaces.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiStrategy.StrategyLegStruct;

import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.StrategyLeg;

import com.cboe.presentation.common.formatters.CommonFormatFactory;

/**
 * Defines the interface exposed by a strategy leg.
 */
class StrategyLegImpl implements StrategyLeg
{
      private StrategyLegStruct strategyLegStruct = null;
      private int ratioQuantity;
      private char side;
      private int productKey;
      private String formatString;

   /**
     * Constructor
     * @param strategyLegStruct StrategyLegStruct
     *
     */
    protected StrategyLegImpl(StrategyLegStruct strategyLegStruct)
    {
         this(strategyLegStruct.product, strategyLegStruct.ratioQuantity, strategyLegStruct.side);
    }
    /**
     * constructor
     * @param productKey int
     * @param ratioQuantity int
     * @param side char
     *
     */

    protected StrategyLegImpl(int productKey, int ratioQuantity, char side)
    {
          this();
          this.productKey = productKey;
          this.ratioQuantity = ratioQuantity;
          this.side = side;
    }


    /**
     *  Default constructor.
     */
    protected StrategyLegImpl()
    {
        super();
    }

    /**
     * Gets the product that is used by this leg
     * @return Product
     */
    public Product getProduct()
    {
        return ProductHelper.getProduct(productKey);
    }
    /**
     * Gets the ratio quantity
     * @return int
     */
    public int getRatioQuantity()
    {
        return ratioQuantity;
    }
    /**
     * Gets the side of this strategy leg.
     * @see com.cboe.idl.cmiConstants.Sides
     * @return char
     */
    public char getSide()
    {
        return side;
    }

    /**
     * Gets the productKey of this strategy leg.
     * @return int
     */
    public int getProductKey()
    {
        return productKey;
    }

    /**
     * Gets the String representation of this StrategyLeg
     * @return String
     */
    public String toString()
    {
        if (this.formatString == null)
        {
            this.formatString = CommonFormatFactory.getStrategyLegFormatStrategy().format(this);
        }
        return this.formatString;
    }

}

