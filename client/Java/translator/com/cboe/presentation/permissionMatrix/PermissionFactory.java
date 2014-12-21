//
// -----------------------------------------------------------------------------------
// Source file: PermissionFactory.java
//
// PACKAGE: com.cboe.presentation.permissionMatrix;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.permissionMatrix;

import com.cboe.interfaces.presentation.permissionMatrix.Permission;

public class PermissionFactory
{
    private PermissionFactory()
    {}

    public static Permission getByName(String name)
    {
        Permission foundPermission = null;
        for(Permission permission : Permission.values())
        {
            if(permission.name().equalsIgnoreCase(name))
            {
                foundPermission = permission;
                break;
            }
        }
        return foundPermission;
    }
}
