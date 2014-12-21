package com.cboe.ffBusinessServices;

import com.cboe.infrastructureServices.foundationFramework.utilities.RouteNameHelper;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.POAHelper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException;

import com.cboe.infrastructureServices.traderService.TraderService;
import com.cboe.infrastructureServices.orbService.NoSuchPOAException;
import org.omg.PortableServer.Servant;

import java.util.ArrayList;
import java.util.Properties;

public class BusinessServiceHome extends BOHome
{
    /**
     *  POA Servant for the service associated with this home.
     */
    private Servant corbaService;

    /**
     * Callback object created when connecting to POA.
     */
    private org.omg.CORBA.Object callbackObject;

    /**
     *  Can be set to false by a subclass to avoid creating a route-name based constraint
     */
    private boolean exportWithRouteName = true;

    protected void setExportWithRouteName(boolean aValue)
    {
        exportWithRouteName = aValue;
    }

    protected boolean getExportWithRouteName()
    {
        return exportWithRouteName;
    }

    /**
     * Connects servant to poa.
     *
     * @param service service to be connected
     */
    protected void connectToPOA(Servant service)
    {
        try
        {
            corbaService = service;
            callbackObject = POAHelper.connect(corbaService, this);
        }
        catch (NoSuchPOAException e)
        {
            Log.alarm(this, "Unable to connect service to POA: " + service.getClass());
        }
    }

    /**
     * Exports service to the trader service.
     *
     * @param helperId identifier from "helper" for CORBA service, or a generic name
     * -MW
     */
      protected void export(String helperId)
      {
           /* try to export the offer of the generic name service  -MW */
           String genericServiceName = null;
           genericServiceName = com.cboe.domain.util.GenericName.getGenericName( helperId, ':');
           if ( null != genericServiceName)
           {
               export(genericServiceName, true);
           }
           else
           {
               export(helperId, true);
           }
      }
     /**
     * Exports service to the trader service.
     *
     * @param helperId identifier(extract the generic name between ':") from "helper" for CORBA service
     * @param boolean to overload the method export(), identify this method can be used inside this class only
     * -MW
     */
      private void export(String helperId, boolean internal)
      {

        Log.debug( this, " Exporting " + helperId  + " service: " + corbaService );
        if( corbaService == null )
        {
            Log.debug( this, "Export will probably not happen since service is null!" );
        }
        FoundationFramework ff = FoundationFramework.getInstance();
        TraderService ts = ff.getTraderService();
        String constraints = "";
        String routeName = null;
        if (getExportWithRouteName())
        {
            routeName = RouteNameHelper.getRouteName();
            constraints = "routename == " + routeName;
        }
        ts.withdraw(helperId, constraints);
        exportByRouteName( routeName, helperId );
    }
    private void exportByRouteName( String routeName, String helperId )
    {
    	Log.debug(this, "exportByRouteName(routeName='" + routeName + "',helperId='" + helperId + "')");
        Properties serviceProperties = new Properties();
        if (routeName != null)
        {
            serviceProperties.put("routename", routeName);
        }
        try
        {
            POAHelper.export(corbaService, helperId, serviceProperties, this);
        }
        catch (NoSuchPOAException e)
        {
             Log.alarm(this, "Unable to export service to TraderService: " + helperId);
        }
    }

    /**
     * Gets callback object for service of this home.
     */
    protected org.omg.CORBA.Object getCallbackObject()
    {
        return callbackObject;
    }

    /**
     * Gets servant for the service.
     *
     * @return POA servant
     */
    protected Servant getServant()
    {
        return corbaService;
    }
}
