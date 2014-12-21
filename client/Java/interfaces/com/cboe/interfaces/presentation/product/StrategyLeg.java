//
// -----------------------------------------------------------------------------------
// Source file: SessionStrategyLeg.java
//
// PACKAGE: com.cboe.interfaces.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.product;

/**
 * Defines the interface exposed by a strategy leg.
 */
public interface StrategyLeg extends Cloneable
{
    /**
     * Gets the product that is used by this leg
     * @return Product
     */
    public Product getProduct();
    /**
     * Gets the ratio quantity
     * @return int
     */
    public int getRatioQuantity();
    /**
     * Gets the side of this strategy leg.
     * @see com.cboe.idl.cmiConstants.Sides
     * @return char
     */
    public char getSide();

    /**
     * gets the product key that is used by this leg
     * @return int
     */
     public int getProductKey();
}

