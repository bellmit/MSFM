// -----------------------------------------------------------------------------------
// Source file: UserPermissionMatrixImpl.java
//
// PACKAGE: com.cboe.presentation.permissionMatrix;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.permissionMatrix;

import com.cboe.interfaces.presentation.permissionMatrix.Permission;
import com.cboe.interfaces.presentation.permissionMatrix.PermissionMatrix;
import com.cboe.interfaces.presentation.permissionMatrix.UserPermissionMatrix;
import com.cboe.interfaces.presentation.user.Role;
import com.cboe.interfaces.presentation.user.UserModel;

public class UserPermissionMatrixImpl implements UserPermissionMatrix
{
    private Role userRole;
    private PermissionMatrix permissionMatrix;

    public UserPermissionMatrixImpl(PermissionMatrix permissionMatrix, UserModel user)
    {
        this(permissionMatrix, user.getRole());
    }

    public UserPermissionMatrixImpl(PermissionMatrix permissionMatrix, Role role)
    {
        this(permissionMatrix);
        setUserRole(role);
    }

    private UserPermissionMatrixImpl(PermissionMatrix permissionMatrix)
    {
        this();
        setPermissionMatrix(permissionMatrix);
    }

    private UserPermissionMatrixImpl()
    {
    }

    public boolean isAllowed(Permission permission)
    {
        return isAllowed(permission, getUserRole());
    }

    private Role getUserRole()
    {
        return userRole;
    }

    private void setUserRole(Role userRole)
    {
        this.userRole = userRole;
    }

    private PermissionMatrix getPermissionMatrix()
    {
        return permissionMatrix;
    }

    private void setPermissionMatrix(PermissionMatrix permissionMatrix)
    {
        this.permissionMatrix = permissionMatrix;
    }

    public boolean isAllowed(Permission permission, Role role)
    {
        return getPermissionMatrix().isAllowed(permission, role);
    }

    public Boolean get(Permission permission, Role role)
    {
        return getPermissionMatrix().get(permission, role);
    }

    public void set(Permission permission, Role role, Boolean value)
    {
        getPermissionMatrix().set(permission, role, value);
    }
}
