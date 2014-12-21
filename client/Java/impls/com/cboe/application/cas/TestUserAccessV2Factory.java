package com.cboe.application.cas;

/**
 * This type was created in VisualAge.
 */
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.idl.cmiV2.UserAccessV2;


public class TestUserAccessV2Factory {
    static private UserAccessV2 userAccess = null;
  /**
   * SBTAccessFactory constructor comment.
   */
  public TestUserAccessV2Factory() {
    super();
  }
  /**
   * This method was created in VisualAge.
   */
  public static UserAccessV2 find() {
    if (userAccess == null) {
        try {
          Object obj = RemoteConnectionFactory.find().find_initial_object();
          userAccess =
              com.cboe.idl.cmiV2.UserAccessV2Helper.narrow((org.omg.CORBA.Object) obj);
        } catch (Throwable e) {
            System.out.println("UserAccess remote object connection exception : " + e.toString());
            e.printStackTrace();
        }
    }
    return userAccess;
  }
}
