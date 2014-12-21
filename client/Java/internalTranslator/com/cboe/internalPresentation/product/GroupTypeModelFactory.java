//
// ------------------------------------------------------------------------
// Source file: GroupTypeFactory.java
//
// PACKAGE: com.cboe.internalPresentation.product
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------

package com.cboe.internalPresentation.product;

import com.cboe.idl.product.GroupTypeStruct;
import com.cboe.interfaces.internalPresentation.product.GroupTypeModel;

public class GroupTypeModelFactory
{
    public static GroupTypeModel createGroupType(GroupTypeStruct groupTypeStruct)
    {
        return new GroupTypeModelImpl(groupTypeStruct);
    }

    public static GroupTypeModel createGroupType( )
    {
        return new GroupTypeModelImpl();
    }

}