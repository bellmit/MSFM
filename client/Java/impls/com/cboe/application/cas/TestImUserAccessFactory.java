/*
 * Created by IntelliJ IDEA.
 * User: HUANGE
 * Date: Sep 10, 2002
 * Time: 2:00:55 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.cas;

import com.cboe.idl.cmiIntermarket.IntermarketUserAccess;
import com.cboe.idl.cmiIntermarket.IntermarketUserAccessHelper;


/**
 * This type was created in VisualAge.
 */
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.application.shared.*;
import com.cboe.util.event.*;

public class TestImUserAccessFactory {
    static private IntermarketUserAccess imUserAccess = null;

  public TestImUserAccessFactory() {
    super();
  }

  public static IntermarketUserAccess find() {
    if (imUserAccess == null) {
        try {
          Object obj = RemoteConnectionFactory.find().find_initial_object();
          imUserAccess =
              IntermarketUserAccessHelper.narrow((org.omg.CORBA.Object) obj);
        } catch (Throwable e) {
            System.out.println("UserAccess remote object connection exception : " + e.toString());
            e.printStackTrace();
        }
    }
    return imUserAccess;
  }
}