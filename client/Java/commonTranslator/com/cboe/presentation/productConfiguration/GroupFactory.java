//
// -----------------------------------------------------------------------------------
// Source file: GroupFactory.java
//
// PACKAGE: com.cboe.presentation.productConfiguration
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.productConfiguration;

import com.cboe.idl.product.GroupStruct;
import com.cboe.idl.product.GroupTypeStruct;
import com.cboe.interfaces.presentation.productConfiguration.Group;

public class GroupFactory
{
    private GroupFactory()
    {
    }

    public static Group createGroup(GroupStruct struct)
    {
        return new GroupImpl(struct);
    }

    public static Group[] createGroups(GroupStruct[] structs)
    {
        Group[] groups = new Group[structs.length];
        for (int i = 0; i < structs.length; i++)
        {
            groups[i] = createGroup(structs[i]);
        }
        return groups;
    }

    public static Group createDefaultGroup()
    {
        GroupTypeStruct typeStruct = new GroupTypeStruct(40, "PostAssignmentsGroup", false);
        GroupStruct struct = new GroupStruct(0, "Post_0.Station_0", typeStruct);
        return new GroupImpl(struct);
    }

    public static Group createDefaultStationGroup(int post)
    {
        GroupTypeStruct typeStruct = new GroupTypeStruct(40, "PostAssignmentsGroup", false);
        GroupStruct struct = new GroupStruct(0 - post, "Post_"+String.valueOf(post)+".Station_0", typeStruct);
        return new GroupImpl(struct);
    }


}
