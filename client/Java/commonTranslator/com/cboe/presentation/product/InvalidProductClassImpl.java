package com.cboe.presentation.product;
// -----------------------------------------------------------------------------------
// Source file: InvalidProductClassImpl
//
// PACKAGE: com.cboe.presentation.product
// 
// Created: Mar 24, 2006 3:32:30 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import com.cboe.idl.cmiProduct.ClassStruct;

import com.cboe.domain.util.ClientProductStructBuilder;

class InvalidProductClassImpl extends ProductClassImpl
{
    private String toStringValue;
    
    /**
     * Constructor
     * @param classKey int  
     */
    InvalidProductClassImpl(int classKey)
    {
        ClassStruct aClassStruct = ClientProductStructBuilder.buildClassStruct();
        aClassStruct.classKey    = classKey;
        aClassStruct.classSymbol = "[" + classKey + "]";

        updateFromStruct(aClassStruct);
    }

    /**
     * Default constructor.
     */
    InvalidProductClassImpl()
    {
        super(ClientProductStructBuilder.buildClassStruct());
    }

    /**
     * Determines if this ProductClass is invalid, either it has been marked inactive or has been removed from the
     * system, but some data structures still reference the classkey
     */
    public boolean isValid()
    {
        return false;
    }

    /**
     * Returns a String representation of this ProductClass.
     */
    public String toString()
    {
        if (toStringValue == null)
        {        
            toStringValue = super.toString() + "(Invalid)";
        }
        
        return toStringValue;
    }
}
