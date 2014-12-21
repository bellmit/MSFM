package com.cboe.internalPresentation.product;

import com.cboe.interfaces.internalPresentation.product.ProductClassGroupModel;
import com.cboe.interfaces.internalPresentation.product.GroupModel;

//
// ------------------------------------------------------------------------
// Source file: ProductClassGroupModelFactory.java
// 
// PACKAGE: com.cboe.internalPresentation.product
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//package com.cboe.internalPresentation.product;

public class ProductClassGroupModelFactory
{
    public static ProductClassGroupModel createProductClassGroupModel(GroupModel groupModel)
    {
        return new ProductClassGroupModelImpl(groupModel);
    }
    public static ProductClassGroupModel createProductClassGroupModel()
    {
        return new ProductClassGroupModelImpl(GroupModelFactory.createGroupModel());
    }
}