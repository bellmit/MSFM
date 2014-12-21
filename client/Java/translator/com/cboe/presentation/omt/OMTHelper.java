//
// -----------------------------------------------------------------------------------
// Source file: OMTHelper.java
//
// PACKAGE: com.cboe.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import com.cboe.interfaces.presentation.user.Role;

/**
 * @author Thomas Morrow
 * @since May 14, 2008
 */
public class OMTHelper
{
   
    private OMTHelper()
    {
    }
    
   
    public static boolean isOMTRole(Role role)
    {
        return (role == Role.BOOTH_OMT || role == Role.CROWD_OMT || role == Role.DISPLAY_OMT ||
           role == Role.HELPDESK_OMT);
    }

}
