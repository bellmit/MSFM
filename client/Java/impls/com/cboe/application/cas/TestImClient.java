/*
 * Created by IntelliJ IDEA.
 * User: HUANGE
 * Date: Sep 11, 2002
 * Time: 9:22:38 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.cas;

import com.cboe.idl.cmiIntermarket.IntermarketUserSessionManager;
import com.cboe.idl.cmiIntermarket.NBBOAgentSessionManager;
import com.cboe.idl.cmiIntermarket.*;
import com.cboe.idl.cmiIntermarketCallback.CMIIntermarketOrderStatusConsumer;
import com.cboe.idl.cmiIntermarketCallback.CMIIntermarketOrderStatusConsumerHelper;
import com.cboe.idl.cmiIntermarketCallback.CMINBBOAgentSessionAdminHelper;
import com.cboe.idl.cmiIntermarketCallback.CMINBBOAgentSessionAdmin;
import com.cboe.delegates.intermarketCallback.IntermarketOrderStatusConsumerDelegate;
import com.cboe.delegates.intermarketCallback.NBBOAgentSessionAdminConsumerDelegate;

public class TestImClient extends TestClient {
    public static void main(String[] args)
    {
        try
        {
            ////////// MUST BE CALLED /////////
            initORBConnection(args);

            initImUserSession();

            testGetNBBOAgent();
            // testGetSessions();

            try {
                java.lang.Object waiter = new java.lang.Object();
                synchronized(waiter)
                {
                    waiter.wait();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
         } catch (Throwable e)
         {
            e.printStackTrace();
         }
    }

    public static void testGetNBBOAgent()
    {
        try {
        System.out.println("version: "+ imSessionStruct.sessionManager.getVersion() );

        int classKey = 917647;   // from simulator
        String sessionName = "W_AM1";
        boolean forceOverride = false;


        initPOA();
        TestImCallback callbackConsumer = new TestImCallback();
        IntermarketOrderStatusConsumerDelegate classListener = new IntermarketOrderStatusConsumerDelegate(callbackConsumer);
        org.omg.CORBA.Object classObject = getPOA().servant_to_reference(classListener);
        CMIIntermarketOrderStatusConsumer imOrderStatusListener = CMIIntermarketOrderStatusConsumerHelper.narrow(classObject);

        NBBOAgentSessionAdminConsumerDelegate agentListener = new NBBOAgentSessionAdminConsumerDelegate(callbackConsumer);
        org.omg.CORBA.Object agentObject = getPOA().servant_to_reference(agentListener);
        CMINBBOAgentSessionAdmin nbboAgentSessionAdmin = CMINBBOAgentSessionAdminHelper.narrow(agentObject);

        NBBOAgentSessionManager nbboAgentManager = imSessionStruct.imSessionManager.getNBBOAgent().registerAgent(classKey, sessionName, forceOverride, imOrderStatusListener, nbboAgentSessionAdmin ) ;
        //nbboAgentManager.getIntermarketHeldOrderEntry();
        //IntermarketHeldOrderQuery imOrderQuery = nbboAgentManager.getIntermarketHeldOrderQuery();
        //IntermarketHeldOrderEntry imOrderEntry = nbboAgentManager.getIntermarketHeldOrderEntry();


        System.out.println("register done");
        } catch ( Exception e) {
            e.printStackTrace();
        }

    }

}
