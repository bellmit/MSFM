//
// -----------------------------------------------------------------------------------
// Source file: ProductClassAllSelectedImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

/**
 * ProductClass implementation for an All Selected ProductClass for the OPTION type.
 */
class ProductClassAllSelectedImpl extends ProductClassDefaultImpl implements CustomKeys
{
    protected static final String ALL_CLASSES_SELECTED_TEXT = "<All Classes>";

    /**
     *  Default constructor.
     */
    protected ProductClassAllSelectedImpl()
    {
        super();
    }

    public boolean isAllSelectedProductClass()
    {
        return true;
    }

    public boolean isDefaultProductClass()
    {
        return false;
    }

    /**
     * Get the class symbol for this ProductClass.
     * @return All Classes string for the ProductClassAllSelectedImpl is returned
     */
    public String getClassSymbol()
    {
        return ALL_CLASSES_SELECTED_TEXT;
    }

    /**
     * Get the class key this ProductClass.
     * @return Default class key for the ProductClassDefaultImpl is returned
     */
    public int getClassKey()
    {
        return ALL_SELECTED_CLASS_KEY;
    }

    /**
     * Clones this object by returning another instance of this class.
     */
    public Object clone() throws CloneNotSupportedException
    {
        return new ProductClassAllSelectedImpl();
    }

    /**
     * If <code>obj</code> is an instance of this class true is return,
     * false otherwise.
     * @param obj to compare
     * @return true if equal, false if not.
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if( isEqual )
        {
            if( obj instanceof ProductClassAllSelectedImpl )
            {
                isEqual = true;
            }
            else
            {
                isEqual = false;
            }
        }
        return isEqual;
    }
}
