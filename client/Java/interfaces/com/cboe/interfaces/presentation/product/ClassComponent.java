//
// -----------------------------------------------------------------------------------
// Source file: ClassComponent.java
//
// PACKAGE: com.cboe.interfaces.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.product;

/**
 * Represents a contract for a component that represents an instance of a ProductClass
 */
public interface ClassComponent
{
    /**
     * Gets the ProductClass that this component represents.
     * @return ProductClass
     */
    public ProductClass getProductClass();

    /**
     * Sets the ProductClass that this component represents.
     * @param productClass to represent
     */
    public void setProductClass(ProductClass productClass);
}
