/**
 * @author Jing Chen
 */
package com.cboe.cfix.cas.shared;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.interfaces.cfix.*;
import com.cboe.interfaces.domain.session.*;
import com.cboe.util.channel.*;

public class CfixServicesHelper {

    private static boolean isMDXEnabled = false;
    private static boolean isSessionC2 = false;
    /**
     * Returns a reference to session manager home .
     */
    public static CfixSessionManagerHome getCfixSessionManagerHome()
    {
        try
        {
            CfixSessionManagerHome home = (CfixSessionManagerHome) HomeFactory.getInstance().findHome(CfixSessionManagerHome.HOME_NAME);
            return home;
        } catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find Cfix Session Manager Home.");
        }
    }

    public static CfixSessionManager createCfixSessionManager(SessionProfileUserStructV2 userStruct, String sessionId, CfixUserSessionAdminConsumer clientListener, short sessionType, boolean gmdTextMessaging)
            throws com.cboe.exceptions.DataValidationException, com.cboe.exceptions.SystemException
    {
        try
        {
            CfixSessionManagerHome home = (CfixSessionManagerHome) HomeFactory.getInstance().findHome(CfixSessionManagerHome.HOME_NAME);
            return home.createCfixSession(userStruct, sessionId, clientListener, sessionType, gmdTextMessaging);
        } catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find Cfix Session Manager Home.");
        }
    }

    public static CfixMarketDataQueryProxyHome getCfixMarketDataQueryHome()
    {
        try
        {
            return (CfixMarketDataQueryProxyHome) HomeFactory.getInstance().findHome(CfixMarketDataQueryProxyHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CfixMarketDataQueryProxyHome");
        }
    }

    public static CfixMDXMarketDataQueryProxyHome getCfixMDXMarketDataQueryHome()
    {
        try
        {
            return (CfixMDXMarketDataQueryProxyHome) HomeFactory.getInstance().findHome(CfixMDXMarketDataQueryProxyHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CfixMDXMarketDataQueryProxyHome");
        }
    }

    public static ChannelListener getCfixSessionAdminConsumerProxy(CfixUserSessionAdminConsumer consumer, BaseSessionManager sessionManager, boolean gmd)
            throws DataValidationException
    {
        ChannelListener listener = null;
        try
        {
            com.cboe.interfaces.cfix.CfixSessionAdminConsumerProxyHome home =
                    (com.cboe.interfaces.cfix.CfixSessionAdminConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.cfix.CfixSessionAdminConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager, gmd);
        } catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find SessionAdminConsumerProxyHome.");
        }
        return listener;
    }

    public static CfixStrikePriceHelperHome getCfixStrikePriceHelperHome()
    {
        try
        {
            return (CfixStrikePriceHelperHome) HomeFactory.getInstance().findHome(CfixStrikePriceHelperHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CfixStrikePriceHelperHome");
        }
    }

    public static CfixHome getCfixHome()
    {
        try
        {
            return (CfixHome) HomeFactory.getInstance().findHome(CfixHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CfixHome");
        }
    }

    public static CfixProductConfigurationService getCfixProductConfigurationService()
            throws DataValidationException
    {
        CfixProductConfigurationService cfixPcs = null;
        try
        {
            com.cboe.interfaces.cfix.CfixProductConfigurationServiceHome home =
                    (com.cboe.interfaces.cfix.CfixProductConfigurationServiceHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.cfix.CfixProductConfigurationServiceHome.HOME_NAME);
            cfixPcs = home.create();
        } catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CfixProductConfigurationService.");
        }
        return cfixPcs;
    }

    public static CfixCasLoginHome getCfixCasLoginHome()
    {
        try
        {
            return (CfixCasLoginHome) HomeFactory.getInstance().findHome(CfixCasLoginHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CfixCasLoginHome");
        }
    }

    public static CfixCasLogin getCfixCasLogin()
    {
        try
        {
            CfixCasLoginHome home = (CfixCasLoginHome) HomeFactory.getInstance().findHome(CfixCasLoginHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CfixCasLogin");
        }
    }

    public static CfixCasExternalLoginHome getCfixCasexternalLoginHome()
    {
        try
        {
            return (CfixCasExternalLoginHome) HomeFactory.getInstance().findHome(CfixCasExternalLoginHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CfixCasExternalLoginHome");
        }
    }

    public static CfixCasExternalLogin getCfixCasExternalLogin()
    {
        try
        {
            CfixCasExternalLoginHome home = (CfixCasExternalLoginHome) HomeFactory.getInstance().findHome(CfixCasExternalLoginHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CfixCasExternalLogin");
        }
    }

    public static void setMDXEnabled(boolean MDXEnabled)
    {
        isMDXEnabled = MDXEnabled;
    }

    public static boolean getMDXEnabled()
    {
        return isMDXEnabled;
    }

    public static void setSessionC2(boolean C2Session)
    {
        isSessionC2 = C2Session;
    }

    public static boolean getSessionC2()
    {
        return isSessionC2;
    }
}

