//
// -----------------------------------------------------------------------------------
// Source file: TestUserAccessV6Factory.java
//
// PACKAGE: com.cboe.application.cas
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.cas;

import com.cboe.idl.cmiV6.UserAccessV6Helper;
import com.cboe.idl.cmiV6.UserAccessV6;

import com.cboe.application.shared.RemoteConnectionFactory;

public class TestUserAccessV6Factory
{
    static private UserAccessV6 userAccess = null;

    public TestUserAccessV6Factory()
    {
        super();
    }

    public static UserAccessV6 find()
    {
        if(userAccess == null)
        {
            try
            {
                Object obj = RemoteConnectionFactory.find().find_initial_V6_object();
                userAccess = UserAccessV6Helper.narrow((org.omg.CORBA.Object) obj);
            }
            catch(Throwable e)
            {
                System.out.println("com.cboe.idl.cmiV6.UserAccessV6 remote object connection exception : " + e.toString());
                e.printStackTrace();
            }
        }
        return userAccess;
    }
}
