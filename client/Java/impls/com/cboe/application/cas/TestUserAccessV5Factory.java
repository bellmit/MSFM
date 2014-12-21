//
// -----------------------------------------------------------------------------------
// Source file: TestUserAccessV5Factory.java
//
// PACKAGE: com.cboe.application.cas
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.cas;

import com.cboe.idl.cmiV5.UserAccessV5Helper;
import com.cboe.idl.cmiV5.UserAccessV5;

import com.cboe.application.shared.RemoteConnectionFactory;

public class TestUserAccessV5Factory
{
    static private UserAccessV5 userAccess = null;

    public TestUserAccessV5Factory()
    {
        super();
    }

    public static UserAccessV5 find()
    {
        if(userAccess == null)
        {
            try
            {
                Object obj = RemoteConnectionFactory.find().find_initial_V5_object();
                userAccess = UserAccessV5Helper.narrow((org.omg.CORBA.Object) obj);
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
