//
// -----------------------------------------------------------------------------------
// Source file: AbstractPermissionMatrix.java
//
// PACKAGE: com.cboe.presentation.permissionMatrix;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.permissionMatrix;

import java.util.*;

import com.cboe.interfaces.presentation.permissionMatrix.Permission;
import com.cboe.interfaces.presentation.permissionMatrix.PermissionMatrix;
import com.cboe.interfaces.presentation.user.Role;

import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;

public abstract class AbstractPermissionMatrix implements PermissionMatrix
{
    private Map<Permission, Map<Role, Boolean>> data;

    protected AbstractPermissionMatrix()
    {
        //noinspection AbstractMethodCallInConstructor
        initialize();
    }

    protected abstract void initialize();

    @SuppressWarnings({"ReturnOfCollectionOrArrayField"})
    protected Map<Permission, Map<Role, Boolean>> getData()
    {
        if(data == null)
        {
            data = new EnumMap<Permission, Map<Role, Boolean>>(Permission.class);
        }
        return data;
    }

    public void set(Permission permission, Role role, Boolean value)
    {
        if(value != null)
        {
            Map<Permission, Map<Role, Boolean>> masterData = getData();
            synchronized(masterData)
            {
                Map<Role, Boolean> permByRole = masterData.get(permission);
                if(permByRole == null)
                {
                    permByRole = new EnumMap<Role, Boolean>(Role.class);
                    masterData.put(permission, permByRole);
                }
                permByRole.put(role, value);
            }
        }
    }

    public Boolean get(Permission permission, Role role)
    {
        Map<Permission, Map<Role, Boolean>> masterData = getData();
        Boolean retVal = Boolean.FALSE;
        synchronized(masterData)
        {
            Map<Role, Boolean> permByRole = masterData.get(permission);
            if (permByRole != null)
            {
                retVal = permByRole.get(role);
            }
            return retVal;
        }
    }

    public boolean isAllowed(Permission permission, Role role)
    {
        return get(permission, role);
    }

    protected void printPermissions()
    {
        if(GUILoggerHome.find().isDebugOn())
        {
            StringBuilder output = new StringBuilder(1000);

            for(Map.Entry<Permission, Map<Role, Boolean>> permissionEntry : getData().entrySet())
            {
                Permission permission = permissionEntry.getKey();
                output.append("Permission = ").append(permission);
                output.append("==============================================").append('\n');

                Map<Role, Boolean> rolePerms = permissionEntry.getValue();
                for(Map.Entry<Role, Boolean> roleEntry : rolePerms.entrySet())
                {
                    Role role = roleEntry.getKey();
                    Boolean value = roleEntry.getValue();
                    output.append(role.getName()).append('=').append(value).append('\n');
                }
            }

            GUILoggerHome.find().debug(getClass().getName(),
                                       GUILoggerBusinessProperty.PERMISSION_MATRIX,
                                       output.toString());
        }
    }
}
