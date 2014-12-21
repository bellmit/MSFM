package com.cboe.application.cas;

/**
 * This type was created in VisualAge.
 */
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.application.shared.*;
import com.cboe.util.event.*;

public class TestUserAccessFactory {
    static private UserAccess userAccess = null;
  /**
   * SBTAccessFactory constructor comment.
   */
  public TestUserAccessFactory() {
    super();
  }
  /**
   * This method was created in VisualAge.
   */
  public static UserAccess find() {
    if (userAccess == null) {
        try {
          Object obj = RemoteConnectionFactory.find().find_initial_object();
          userAccess =
              UserAccessHelper.narrow((org.omg.CORBA.Object) obj);
        } catch (Throwable e) {
            System.out.println("UserAccess remote object connection exception : " + e.toString());
            e.printStackTrace();
        }
    }
    return userAccess;
  }
}
