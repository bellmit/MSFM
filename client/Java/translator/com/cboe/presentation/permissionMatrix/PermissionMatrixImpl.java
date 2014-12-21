//
// -----------------------------------------------------------------------------------
// Source file: PermissionMatrixImpl.java
//
// PACKAGE: com.cboe.presentation.permissionMatrix;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.permissionMatrix;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

public class PermissionMatrixImpl extends AbstractPermissionMatrix
{
    protected void initialize()
    {
        PMCSVInitializer initializer = new PMCSVInitializer();
        //noinspection CatchGenericClass
        try
        {
            initializer.initialize(this);
            printPermissions();
        }
        catch(Exception e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Permissions could not be loaded.");
        }
    }
}
