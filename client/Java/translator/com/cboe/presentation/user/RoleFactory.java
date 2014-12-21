//
// -----------------------------------------------------------------------------------
// Source file: RoleFactory.java
//
// PACKAGE: com.cboe.presentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.user;

import com.cboe.interfaces.presentation.user.Role;
import com.cboe.interfaces.presentation.user.RoleUtility;
import com.cboe.presentation.userSession.UserSessionFactory;

public class RoleFactory
{
    private RoleFactory()
    {}

    public static Role getByName(String name)
    {
        Role foundRole = null;
        for(Role role : Role.values())
        {
            if(role.getName().equals(name))
            {
                foundRole = role;
                break;
            }
        }
        return foundRole;
    }

    public static Role getByChar(char roleChar)
    {
        Role foundRole = null;
        for(Role role : Role.values())
        {
            if(role.getRoleChar() == roleChar)
            {
                foundRole = role;
                break;
            }
        }
        return foundRole;
    }

    public static RoleUtility getRoleUtility(Role role)
    {
    	switch (role)
    	{
    		case MARKET_MAKER:
    		case DPM:
    			return new MarketMakerRoleUtility();
    		default:
    			return new DefaultRoleUtility();
    	}
    }

    public static RoleUtility getRoleUtility()
    {
    	Role role = UserSessionFactory.findUserSession().getUserModel().getRole();
    	return getRoleUtility(role);
    }
}
