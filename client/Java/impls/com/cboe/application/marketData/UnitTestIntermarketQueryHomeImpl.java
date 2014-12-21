/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Sep 20, 2002
 * Time: 10:37:14 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.marketData;

import com.cboe.application.shared.UnitTestHelper;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.cas.TestCallback;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.idl.cmiIntermarketMessages.CurrentIntermarketStruct;
import com.cboe.idl.cmiConstants.LoginSessionTypes;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiCallback.CMIUserSessionAdminHelper;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.delegates.callback.UserSessionAdminConsumerDelegate;

public class UnitTestIntermarketQueryHomeImpl extends IntermarketQueryHomeImpl {
    public static void main(String[] args)
    {
        try
        {
            UnitTestHelper.initFFEnv( args, 0 );
            if (Log.isDebugOn()) {
                Log.debug("---------------------------------> Testing IntermarketQuery");
            }

            ((UnitTestIntermarketQueryHomeImpl)ServicesHelper.getIntermarketQueryHome()).test();

        }
         catch (Exception e)
            {
               Log.exception("--------------------Testing failed" , e);

            }
      }

      public void test()
      {
          try {
              if (Log.isDebugOn()) {
                Log.debug(this, "test");
              }

              SessionProfileUserStructV2 validUserStruct = UnitTestHelper.createNewValidSessionProfileUserStructV2("CCC");
              UserSessionAdminConsumerDelegate sessionListener = new UserSessionAdminConsumerDelegate(new TestCallback());
              org.omg.CORBA.Object orbObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(sessionListener);
              CMIUserSessionAdmin userAdminListener = CMIUserSessionAdminHelper.narrow(orbObject);
              SessionManager session = ServicesHelper.createSessionManager(validUserStruct, "W_AM1", 1234, userAdminListener, LoginSessionTypes.PRIMARY, true);

              if ( session != null) {
                  if (Log.isDebugOn()) {
                    Log.debug("sesssion is not null");
                  }
              }

              CurrentIntermarketStruct struct[] = create(session).getIntermarketByClassForSession(917607, "W_AM1");

              CurrentIntermarketStruct struct1 = create(session).getIntermarketByProductForSession(917551, "W_AM1");

              if (Log.isDebugOn()) {
                Log.debug(" finished testing");
              }
          } catch (Exception e )
          {
              e.printStackTrace();
              Log.exception(this, e);
          }
      }

}
