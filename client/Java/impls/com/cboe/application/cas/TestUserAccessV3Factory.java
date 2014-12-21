package com.cboe.application.cas;

/**
 * This type was created in VisualAge.
 */
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiV3.UserAccessV3;
import com.cboe.idl.cmiV3.UserAccessV3Helper;
import com.cboe.application.shared.*;
import com.cboe.util.event.*;

public class TestUserAccessV3Factory {
    static private UserAccessV3 userAccess = null;
  /**
   * SBTAccessFactory constructor comment.
   */
  public TestUserAccessV3Factory() {
    super();
  }
  /**
   * This method was created in VisualAge.
   */
  public static UserAccessV3 find() {
    if (userAccess == null) {
        try {
          Object obj = RemoteConnectionFactory.find().find_initial_object();
          userAccess =
              UserAccessV3Helper.narrow((org.omg.CORBA.Object) obj);
        } catch (Throwable e) {
            System.out.println("UserAccessV3 remote object connection exception : " + e.toString());
            e.printStackTrace();
        }
    }
    return userAccess;
  }
}
