//
// -----------------------------------------------------------------------------------
// Source file: TestUserAccessV9Factory.java
//
// PACKAGE: com.cboe.application.cas
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.cas;

import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.idl.cmiV9.UserAccessV9;
import com.cboe.idl.cmiV9.UserAccessV9Helper;

public class TestUserAccessV9Factory
{
	static private UserAccessV9 userAccess = null;

	public TestUserAccessV9Factory()
	{
		super();
	}

	public static UserAccessV9 find()
	{
		if (userAccess == null)
		{
			try
			{
				Object obj = RemoteConnectionFactory.find().find_initial_V9_object();
				userAccess = UserAccessV9Helper.narrow((org.omg.CORBA.Object) obj);
			}
			catch (Throwable e)
			{
				System.out.println("com.cboe.idl.cmiV9.UserAccessV9 remote object connection exception : " + e.toString());
				e.printStackTrace();
			}
		}
		return userAccess;
	}
}
