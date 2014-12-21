//
// -----------------------------------------------------------------------------------
// Source file: PermissionMatrix.java
//
// PACKAGE: com.cboe.interfaces.presentation.permissionMatrix;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.permissionMatrix;

import com.cboe.interfaces.presentation.user.Role;

public interface PermissionMatrix
{
    boolean isAllowed(Permission permission, Role role);
    Boolean get(Permission permission, Role role);
    void set(Permission permission, Role role, Boolean value);
}
