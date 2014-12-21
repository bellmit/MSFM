//
// -----------------------------------------------------------------------------------
// Source file: TestUserAccessV7Factory.java
//
// PACKAGE: com.cboe.application.cas
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.cas;

import com.cboe.idl.cmiV7.UserAccessV7Helper;
import com.cboe.idl.cmiV7.UserAccessV7;

import com.cboe.application.shared.RemoteConnectionFactory;

public class TestUserAccessV7Factory
{
    static private UserAccessV7 userAccess = null;

    public TestUserAccessV7Factory()
    {
        super();
    }

    public static UserAccessV7 find()
    {
        if(userAccess == null)
        {
            try
            {
                Object obj = RemoteConnectionFactory.find().find_initial_V7_object();
                userAccess = UserAccessV7Helper.narrow((org.omg.CORBA.Object) obj);
            }
            catch(Throwable e)
            {
                System.out.println("com.cboe.idl.cmiV7.UserAccessV7 remote object connection exception : " + e.toString());
                e.printStackTrace();
            }
        }
        return userAccess;
    }
}
