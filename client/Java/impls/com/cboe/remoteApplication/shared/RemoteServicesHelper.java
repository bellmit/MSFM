package com.cboe.remoteApplication.shared;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.interfaces.events.*;
import com.cboe.interfaces.remoteApplication.RemoteCASMarketDataServiceHome;
import com.cboe.interfaces.remoteApplication.RemoteCASSessionManagerHome;
import com.cboe.interfaces.remoteApplication.RemoteCASConfigurationServiceHome;
import com.cboe.interfaces.remoteApplication.RemoteCASConfigurationService;

/**
 * @author Jing Chen
 */
public class RemoteServicesHelper extends ServicesHelper{

    public RemoteServicesHelper()
    {
        super();
    }

    public static RemoteCASMarketDataServiceHome getRemoteCASMarketDataServiceHome()
    {
         try
        {
            RemoteCASMarketDataServiceHome home = (RemoteCASMarketDataServiceHome) HomeFactory.getInstance().findHome(RemoteCASMarketDataServiceHome.HOME_NAME);
            return home;
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find RemoteCASMarketDataServiceHome Home.");
        }
    }
    public static RemoteCASConfigurationServiceHome getRemoteCASConfigurationServiceHome()
    {
         try
        {
            RemoteCASConfigurationServiceHome home = (RemoteCASConfigurationServiceHome) HomeFactory.getInstance().findHome(RemoteCASConfigurationServiceHome.HOME_NAME);
            return home;
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find RemoteCASMarketDataServiceHome Home.");
        }
    }

    public static RemoteCASConfigurationService getRemoteCASConfigurationService()
    {
         try
        {
            RemoteCASConfigurationServiceHome home = (RemoteCASConfigurationServiceHome) HomeFactory.getInstance().findHome(RemoteCASConfigurationServiceHome.HOME_NAME);
            return home.create();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find RemoteCASMarketDataService.");
        }
    }

    public static RemoteCASSessionManagerHome getRemoteCASSessionManagerHome()
    {
         try
        {
            RemoteCASSessionManagerHome home = (RemoteCASSessionManagerHome) HomeFactory.getInstance().findHome(RemoteCASSessionManagerHome.HOME_NAME);
            return home;
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find RemoteCASSessionManagerHome Home.");
        }
    }
    public static RemoteCASCallbackRemovalConsumer getRemoteCASCallbackRemovalPublisher()
    {
        try
        {
            RemoteCASCallbackRemovalConsumerHome home = (RemoteCASCallbackRemovalConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASCallbackRemovalConsumerHome.PUBLISHER_HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find RemoteCASCallbackRemovalPublisher ");
        }
    }

    public static RemoteCASRecoveryConsumer getRemoteCASRecoveryPublisher()
    {
        try
        {
            RemoteCASRecoveryConsumerHome home = (RemoteCASRecoveryConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASRecoveryConsumerHome.PUBLISHER_HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find RemoteCASRecoveryPublisher ");
        }
    }
    public static IECRemoteCASTickerConsumerHome getRemoteCASTickerConsumerHome()
    {
        try
        {
            return (IECRemoteCASTickerConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASTickerConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find TickerConsumerHome (UOQ)");
        }
    }

    public static IECRemoteCASBookDepthConsumerHome getRemoteCASBookDepthConsumerHome()
    {
        try
        {
            return (IECRemoteCASBookDepthConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASBookDepthConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find BookDepthConsumerHome (UOQ)");
        }
    }

    public static IECRemoteCASCurrentMarketConsumerHome getRemoteCASCurrentMarketConsumerHome()
    {
        try
        {
            return (IECRemoteCASCurrentMarketConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASCurrentMarketConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CurrentMarketConsumerHome (UOQ)");
        }
    }

    public static IECRemoteCASNBBOConsumerHome getRemoteCASNBBOConsumerHome()
    {
        try
        {
            return (IECRemoteCASNBBOConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASNBBOConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find NBBOConsumerHome (UOQ)");
        }
    }

    public static IECRemoteCASRecapConsumerHome getRemoteCASRecapConsumerHome()
    {
        try
        {
            return (IECRemoteCASRecapConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASRecapConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find RecapConsumerHome (UOQ)");
        }
    }

    public static IECRemoteCASExpectedOpeningPriceConsumerHome getRemoteCASExpectedOpeningPriceConsumerHome()
    {
        try
        {
            return (IECRemoteCASExpectedOpeningPriceConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASExpectedOpeningPriceConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ExpectedOpeningPriceConsumerHome (UOQ)");
        }
    }

    public static IECRemoteCASBookDepthUpdateConsumerHome getRemoteCASBookDepthUpdateConsumerHome()
    {
        try
        {
            return (IECRemoteCASBookDepthUpdateConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASBookDepthUpdateConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find BookDepthUpdateConsumerHome (UOQ)");
        }
    }

    public static IECRemoteCASSessionManagerConsumerHome getRemoteCASSessionManagerConsumerHome()
    {
        try
        {
            return (IECRemoteCASSessionManagerConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASSessionManagerConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ExpectedOpeningPriceConsumerHome (UOQ)");
        }
    }
}
