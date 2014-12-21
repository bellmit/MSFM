//
// -----------------------------------------------------------------------------------
// Source file: TestUserAccessV4Factory.java
//
// PACKAGE: com.cboe.application.cas
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.cas;

import com.cboe.idl.cmiV4.UserAccessV4Helper;
import com.cboe.idl.cmiV4.UserAccessV4;

import com.cboe.application.shared.RemoteConnectionFactory;

public class TestUserAccessV4Factory
{
    static private UserAccessV4 userAccess = null;

    public TestUserAccessV4Factory()
    {
        super();
    }

    public static UserAccessV4 find()
    {
        if(userAccess == null)
        {
            try
            {
                Object obj = RemoteConnectionFactory.find().find_initial_V4_object();
                userAccess = UserAccessV4Helper.narrow((org.omg.CORBA.Object) obj);
            }
            catch(Throwable e)
            {
                System.out.println("com.cboe.idl.cmiV4.UserAccessV4 remote object connection exception : " + e.toString());
                e.printStackTrace();
            }
        }
        return userAccess;
    }
}
