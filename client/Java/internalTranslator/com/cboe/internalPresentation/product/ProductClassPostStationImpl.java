package com.cboe.internalPresentation.product;

import com.cboe.idl.product.ProductClassExtStruct;

import com.cboe.interfaces.internalPresentation.product.ProductClassPostStation;

import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
// -----------------------------------------------------------------------------------
// Source file: ProductClassPostStationImpl
//
// PACKAGE: com.cboe.internalPresentation.product
// 
// Created: Sep 21, 2006 7:39:55 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------


public class ProductClassPostStationImpl extends AbstractBusinessModel implements ProductClassPostStation
{
    private int classKey;
    private String post;
    private String station;
    
    private String text;

    public ProductClassPostStationImpl(ProductClassExtStruct struct)
    {
        checkParam(struct, "struct");
        this.post = struct.post;
        this.station = struct.station;
        this.classKey = struct.productClass.info.classKey;
        this.text = this.post + " / " + this.station;
    }

    public int getClassKey()
    {
        return classKey; 
    }

    public String getStation()
    {
        return station;
    }

    public String getPost()
    {
        return post;
    }

    public int hashCode()
    {
        return getClassKey();
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);
        if (!isEqual)
        {
            if (obj instanceof ProductClassPostStation)
            {
                ProductClassPostStation tmp = (ProductClassPostStation) obj;
                isEqual = tmp.getClassKey() == this.getClassKey();
            }
        }
        
        return isEqual;
    }

    public String toString()
    {
        return text;
    }
}
