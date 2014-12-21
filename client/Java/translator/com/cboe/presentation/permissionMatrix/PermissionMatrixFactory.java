// -----------------------------------------------------------------------------------
// Source file: PermissionMatrixFactory.java
//
// PACKAGE: com.cboe.presentation.permissionMatrix;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.permissionMatrix;

import com.cboe.interfaces.presentation.permissionMatrix.PermissionMatrix;
import com.cboe.interfaces.presentation.permissionMatrix.UserPermissionMatrix;
import com.cboe.interfaces.presentation.user.UserModel;

import com.cboe.presentation.userSession.UserSessionEvent;
import com.cboe.presentation.userSession.UserSessionFactory;
import com.cboe.presentation.userSession.UserSessionListener;

/**
 * Provides creation and access to a PermissionMatrix
 */
public class PermissionMatrixFactory
{
    private static PermissionMatrix aPermissionMatrix = null;
    private static UserPermissionMatrix userPermissionMatrix = null;

    private static final UserSessionListener userListener = new UserSessionListener()
    {
        public void userSessionChange(UserSessionEvent event)
        {
            if(event.getActionType() == UserSessionEvent.FORCED_LOGGED_OUT_EVENT ||
                event.getActionType() == UserSessionEvent.LOGGED_OUT_EVENT)
            {
                userPermissionMatrix = null;
                UserSessionFactory.findUserSession().removeUserSessionListener(this);
            }
        }
    };

    private PermissionMatrixFactory()
    {}

    /**
     * Finds the singleton instance of a PermissionMatrix
     */
    public static synchronized PermissionMatrix findPermissionMatrix()
    {
        if(aPermissionMatrix == null)
        {
            aPermissionMatrix = create();
        }
        return aPermissionMatrix;
    }

    /**
     * Finds the singleton instance of a PermissionMatrix for a current user
     */
    public static synchronized UserPermissionMatrix findUserPermissionMatrix()
    {
        if(userPermissionMatrix == null)
        {
            userPermissionMatrix =
                    createUserPermissonMatrix(UserSessionFactory.findUserSession().getUserModel());
            UserSessionFactory.findUserSession().addUserSessionListener(userListener);
        }
        return userPermissionMatrix;
    }

    /**
     * Finds the singleton instance of a PermissionMatrix for a current user
     */
    public static synchronized UserPermissionMatrix findUserPermissionMatrix(UserModel user)
    {
        UserPermissionMatrix matrix;
        if (user.equals(UserSessionFactory.findUserSession().getUserModel()))
        {
            matrix = findUserPermissionMatrix();
        }
        else
        {
            matrix = createUserPermissonMatrix(user);
        }
        return matrix;
    }

    private static UserPermissionMatrix createUserPermissonMatrix(UserModel user)
    {
        UserPermissionMatrix matrix;
        if(user != null)
        {
            matrix = new UserPermissionMatrixImpl(findPermissionMatrix(), user);
        }
        else
        {
            throw new IllegalArgumentException("PermissionMatrixFactory.createUserPermissionMatrix()" +
                                               " - UserModel can not be null.");
        }

        return matrix;
    }

    /**
     * Creates an instance of PermissinMatrix
     */
    protected static synchronized PermissionMatrix create()
    {
        return new PermissionMatrixImpl();
    }
}
