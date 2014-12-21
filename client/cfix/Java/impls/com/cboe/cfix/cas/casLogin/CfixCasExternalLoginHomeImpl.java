package com.cboe.cfix.cas.casLogin;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.cfix.CfixCasExternalLoginHome;
import com.cboe.interfaces.cfix.CfixCasExternalLogin;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Created by IntelliJ IDEA.
 * User: beniwalv
 * Date: Mar 21, 2011
 * Time: 4:29:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class CfixCasExternalLoginHomeImpl extends ClientBOHome implements CfixCasExternalLoginHome
{
    private CfixCasExternalLogin theCasLogin = null;

    public CfixCasExternalLogin create()
    {
        if (theCasLogin == null)
        {
            if(Log.isDebugOn()){
                Log.debug(this, "CAS Login is Null. Creating CfixCasLogin");
            }
            CfixCasExternalLoginImpl bo = new CfixCasExternalLoginImpl();
            bo.create(String.valueOf(bo.hashCode()));
            addToContainer(bo);
            theCasLogin = bo;
        }
        return theCasLogin;
    }

    public CfixCasExternalLogin find() {
        if(Log.isDebugOn()){
            Log.debug(this, "In CfixCasExternalLoginHome:find() ");
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
