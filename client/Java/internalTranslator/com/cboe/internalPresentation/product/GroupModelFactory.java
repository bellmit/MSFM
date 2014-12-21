//
// ------------------------------------------------------------------------
// Source file: {FILE_NAME}
// 
// PACKAGE: com.cboe.internalPresentation.product;
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2002 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
package com.cboe.internalPresentation.product;

import com.cboe.idl.product.GroupStruct;
import com.cboe.interfaces.internalPresentation.product.GroupModel;

public class GroupModelFactory
{
    public static GroupModel createGroupModel(GroupStruct groupStruct)
    {
        return new GroupModelImpl(groupStruct);
    }

    public static GroupModel createGroupModel( )
    {
        return new GroupModelImpl();
    }
    
    public static GroupModel[] createGroupModels(GroupStruct[] groupStructs)
    {
        GroupModel[] groups = new GroupModel[groupStructs.length];
        for (int i = 0; i < groupStructs.length; i++)
        {
            groups[i] = GroupModelFactory.createGroupModel(groupStructs[i]);
        }
        
        return groups;
    }
}