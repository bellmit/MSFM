package com.cboe.cfix.cas.casLogin;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.cfix.CfixCasLogin;
import com.cboe.interfaces.cfix.CfixCasLoginHome;

/**
 * Created by IntelliJ IDEA.
 * User: lip
 * Date: May 6, 2010
 * Time: 10:57:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class CfixCasLoginHomeImpl extends ClientBOHome implements CfixCasLoginHome {
    private CfixCasLogin theCasLogin = null;

    public CfixCasLogin create()
    {
        if (theCasLogin == null)
        {
            if(Log.isDebugOn()){
                Log.debug(this, "CAS Login is Null. Creating CfixCasLogin");
            }
            CfixCasLoginImpl bo = new CfixCasLoginImpl();
            bo.create(String.valueOf(bo.hashCode()));
            addToContainer(bo);
            theCasLogin = bo;
            //CfixCasLoginSubscriptionTest.TestSubscription(bo.theRootPOA);
        }
        return theCasLogin;
    }

    public CfixCasLogin find() {
        if(Log.isDebugOn()){
            Log.debug(this, "In CfixCasLoginHome:find() ");
        }
        return create();
    }

    @Override
    public void clientStart()
            throws Exception
    {
        create();
    }

    @Override
    public void clientInitialize()
            throws Exception
    {
        create();
    }
}
