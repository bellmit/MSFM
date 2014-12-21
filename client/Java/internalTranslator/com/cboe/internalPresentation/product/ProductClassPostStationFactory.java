package com.cboe.internalPresentation.product;

import com.cboe.idl.product.ProductClassExtStruct;

import com.cboe.interfaces.internalPresentation.product.ProductClassPostStation;
// -----------------------------------------------------------------------------------
// Source file: ProductClassPostStationFactory
//
// PACKAGE: com.cboe.internalPresentation.product
// 
// Created: Sep 21, 2006 7:52:53 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------


public class ProductClassPostStationFactory
{
    private ProductClassPostStationFactory() { }
    
    public static ProductClassPostStation createPostStation(ProductClassExtStruct struct)
    {
        return new ProductClassPostStationImpl(struct);
    }
}
