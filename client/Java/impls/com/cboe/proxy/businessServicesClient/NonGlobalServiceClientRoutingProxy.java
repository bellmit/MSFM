package com.cboe.proxy.businessServicesClient;

import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.idl.cmiErrorCodes.CommunicationFailureCodes;
import com.cboe.idl.cmiErrorCodes.NotAcceptedCodes;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.utilities.RouteNameHelper;
import com.cboe.infrastructureServices.traderService.DirectoryQueryResult;
import com.cboe.interfaces.internalBusinessServices.ProductConfigurationService;
import com.cboe.domain.util.TradingSessionNameHelper;
import com.cboe.interfaces.domain.userLoadManager.UserLoadManager;
import com.cboe.util.ExceptionBuilder;
import org.omg.CORBA.Object;
import java.util.*;

import com.cboe.ORBInfra.IOPImpl.IORImpl;
import com.cboe.ORBInfra.IOPImpl.ProfileNotPresent;
import com.cboe.ORBInfra.IOPImpl.ComponentNotPresent;
import com.cboe.ORBInfra.IOPImpl.MultipleComponentProfileImpl;
import com.cboe.ORBInfra.ORB.OrbNameComponent;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.interfaces.domain.userLoadManager.UserLoadManagerHome;

/**
 * This class is designed to be the common base for all non-global service routing proxy
 *
 * @date December 18, 2008
 * 
 */

public abstract class NonGlobalServiceClientRoutingProxy extends BusinessServiceClientRoutingProxy
{
    public static final String SUB_CONSTRAINT = "subroutename";
    private final static String USE_MANAGED_CONN_POOLS = "ORB.IIOPTransport.UseManagedConnPools";
    private String serviceTypeName;
    //protected HashMap<String, org.omg.CORBA.Object> activityHistoryServiceRouteMap;
    //protected ArrayList<org.omg.CORBA.Object> ahsRouteMap = new ArrayList<org.omg.CORBA.Object>();

    /**
     * Default constructor
     */
    public NonGlobalServiceClientRoutingProxy() 
    {
    }

    /**
     *
     * Initialize the non-global service routing proxy. This method queries the trader service and
     * builds a routing table upon an initialization request, based on whatever returned from the
     * getHelperClassName() method. Each subclass should implement getHelperClassName method to
     * return related business service helper.
     *
     **/
    public void initialize() {
        serviceTypeName = getServiceTypeName();
        Log.information(this, "Initializing NonGlobalServiceRoutingProxy: "+serviceTypeName);
        
        //Look for Activity History Service
        if (isActivityHistoryService())             
        {
            if( Log.isDebugOn())
            {
                Log.debug(this, "build Activity History Service Map");
            }
            // Since this service is not on BC. We will build separate routing map.
            buildActivityHistoryRouteMap(serviceTypeName);    
        }else
        {           
        DirectoryQueryResult[] svcList = getBusinessServicesFor(getServiceTypeName(), null);
        if (svcList == null) {
            Log.information(this, "No Services available - Initialization failure");
            return;
        }
        
        UserLoadManager userLoadManager = ServicesHelper.getUserLoadManagerHome().find();
        boolean useManagedConnPools = Boolean.parseBoolean(System.getProperty(USE_MANAGED_CONN_POOLS, "false"));
        if( Log.isDebugOn())
        {
        	Log.debug(this, "useManagedConnPools=" + useManagedConnPools);
        }
        
        String proxyRouteName = RouteNameHelper.getRouteName();
        ArrayList<String> cachedProcesses = new ArrayList<String>();
        String svcOrbName;
        String svcRouteKey;
        StringBuilder sb = new StringBuilder(100);
        for (int i = 0; i < svcList.length; i++) {
            String routeName = svcList[i].getProperties().getProperty(RouteName);
            sb.setLength(0);
            sb.append("Service properties = ").append(svcList[i].getProperties());
            Log.information(this, sb.toString());
            sb.setLength(0);
            sb.append(routeName).append(ProductConfigurationService.UNDERLYING_GROUP_SUFFIX);
            String underlyingRouteName = sb.toString();
            if (routeName == null || selfRoutingNotAllowed(routeName, proxyRouteName)) {
                continue;
            }
            ArrayList sessionNames = (ArrayList) svcList[i].getMultiplePropertyValues().get(SessionName);
            ArrayList subRouteNames = (ArrayList) svcList[i].getMultiplePropertyValues().get(SUB_CONSTRAINT);

            // If session names are not exported no need to route to them.
            if (sessionNames == null || sessionNames.size() == 0) {
                sb.setLength(0);
                sb.append("Ignoring service for route name = ").append(routeName);
                Log.information(this, sb.toString());
                continue;
            }
            Iterator sessionNameIterator = sessionNames.iterator();
            while (sessionNameIterator.hasNext()) {
                String sessionName = (String) sessionNameIterator.next();
                if (TradingSessionNameHelper.isUnderlyingSession(sessionName)) {
                    svcRouteKey = getRouteMapKey(sessionName, underlyingRouteName);
                } else {
                    svcRouteKey = getRouteMapKey(sessionName, routeName);
                }
                org.omg.CORBA.Object serviceReference = narrow(svcList[i].getObjectReference());
                             
                serviceReference = createDecoratorForOutboundCalls(serviceReference);
                org.omg.CORBA.Object decoratedSvcRef = createDecoratorForOutboundCalls(serviceReference);
                
                // Add the service reference to the map of service references that are
                // unique per process. With the introduction of multiple trade server, some
                // services that are exported by the OHServer process are exported multiple times
                // under different route names. Typically such multiply exported services are the side
                // effects of the OHServer process masquerading as a trade server. These exported
                // offers are identical services with route names corresponding to each trader server
                // process instance. This is necessary to allow for proper routing of calls to the correct
                // trade server based on the trading product. The side effect of this architecture is
                // that some calls that are really responded to by the OHServer are now invoked four times.
                // To prevent this, a collection of service offers unique by process (i.e. ORB name) is built.
                try {
                    org.omg.CORBA.portable.ObjectImpl svcRefImpl = (org.omg.CORBA.portable.ObjectImpl) serviceReference;
                    com.cboe.ORBInfra.ORB.DelegateImpl svcDelegateImpl = (com.cboe.ORBInfra.ORB.DelegateImpl) svcRefImpl._get_delegate();
                    IORImpl svcIor = svcDelegateImpl.getIOR();
                    MultipleComponentProfileImpl svcMcpProfile = (MultipleComponentProfileImpl) svcIor.getProfile(MultipleComponentProfileImpl.tag);
                    OrbNameComponent svcOrbNameComponent = (OrbNameComponent) svcMcpProfile.getComponent(OrbNameComponent.tag);
                    svcOrbName = svcOrbNameComponent.getOrbName();
                    if (svcOrbName != null) 
                    {
                        sb.setLength(0);
                        sb.append(svcOrbName).append(":").append(sessionName);
                        String svcRouteKeyWithOrb = sb.toString();
                        sb.setLength(0);                        

                        // This is a temporary fix for the SpreadTradeServer causing issues for cancelOrderByUsers,
                        // the workaround is to not add the SpreadTradeServer in the uniqueServiceRefByProcess map.
                        boolean isSpreadTradeServerKey = (null != svcRouteKey) && (svcRouteKey.contains("SpreadTradeServer"));
                        if(isSpreadTradeServerKey)
                        {
                            Log.information(this, "svcRouteKey contains SpreadTradeServer and will not be added to uniqueServiceRefByProcess map");
                        }

                        if (!cachedProcesses.contains(svcRouteKeyWithOrb) && !isSpreadTradeServerKey)
                        {
                            uniqueServiceRefByProcess.put(svcRouteKey, decoratedSvcRef);
                            cachedProcesses.add(svcRouteKeyWithOrb);
                            sb.append("Added ORB:Session ").append(svcRouteKeyWithOrb)
                              .append(" for the service to unique service references by process.");
                            Log.information(this, sb.toString());
                        } 
                        else 
                        {
                            sb.append("ORB:Session ").append(svcRouteKeyWithOrb)
                              .append(" for the service is already in unique service reference by process.");
                            Log.information(this, sb.toString());
                        }
                    }
                } catch (ProfileNotPresent e) {
                    Log.alarm(this, "Error while attempting to build unique service reference collection by process. Unable to find MultipleComponentProfile.");
                } catch (ComponentNotPresent e) {
                    Log.alarm(this, "Error while attempting to build unique service reference collection by process. Unable to find OrbNameComponent.");
                }
                routeMap.put(svcRouteKey, decoratedSvcRef);
                
                /*
                 * if useManagedConnPools==true Register the decoratedSvcRef to the UserLoadManager so that
                 *  this decoratedSvcRef can be managed to allow dynamic growth of the decoratedSvcRef's ConnectionPool
                 */            
				if (useManagedConnPools)
				{
					userLoadManager.registerServiceReference(svcRouteKey, decoratedSvcRef);
				} 
            }
        }
        serviceState = true;
        sb.setLength(0);
        sb.append("Initialization complete with route map as: ").append(routeMap.keySet().toString());
        Log.information(this, sb.toString());
        }
    }
    // Check type of service name.
    private boolean isActivityHistoryService()
    {
        int index = serviceTypeName.indexOf("ActivityHistoryService");
     
        if (index != -1)
            return true;          
        else
            return false;
    }
    
    private void buildActivityHistoryRouteMap(String serviceTypeName)
    {
        org.omg.CORBA.Object serviceProvider;
        
        String constraint = new String ("routename == " + RouteNameHelper.getRemoteRouteName() );
        if (Log.isDebugOn())
        {
            Log.debug(this, "Initializing ActivityHistoryService with Constraint:"+constraint );
        }       
        DirectoryQueryResult[] ahsSvcList = getBusinessServicesFor(serviceTypeName, null );
        
        if (ahsSvcList != null){
            
            for (int i=0; i < ahsSvcList.length;i++)
            {
                serviceProvider = narrow( ahsSvcList[i].getObjectReference() );
                if ( serviceProvider != null  ){
                    // add to activity service route map
                    ahsRouteMap.add(serviceProvider);       
                }else
                {
                    Log.debug( this, "Can't get object reference for ActivityHistory services - Initialization failure");
                }               
            }          
        }else
        {   
           Log.debug(this, "No ActivityHistory services available - Initialization failure");   
        }
    }

    // only build routemap for services based on the required sub route name
    private void locateServiceBySubRouteName(ArrayList subRouteNames, String sessionName, String routeName, Object serviceReference)
    {
        // check multiple subroutenames as well, here we will use
        // route name, session name and subroutename to form a unique offer constraint
        Iterator subRouteNameIterator = subRouteNames.iterator();
        StringBuilder sb = new StringBuilder(150);
        while (subRouteNameIterator.hasNext())
        {
            String subRouteName =  (String) subRouteNameIterator.next();
            String sf = getRouteMapKey(sessionName, routeName);
            sb.setLength(0);
            if (sf.indexOf(subRouteName) == -1)
            {
                sb.append("Ignoring service for route name = ").append(routeName)
                  .append(", sub Route Name = ").append(subRouteName)
                  .append(", session Name = ").append(sessionName);
                Log.information(this, sb.toString());
                continue;
            }
            else
            {
                sb.append("creating RouteMap for route name = ").append(routeName)
                  .append(", sub Route Name = ").append(subRouteName)
                  .append(", session Name = ").append(sessionName);
                Log.information(this, sb.toString());
                routeMap.put( getRouteMapKey(sessionName, routeName), serviceReference );
            }
        }
    }
    
    protected boolean selfRoutingNotAllowed(String myRouteName, String serviceRouteName)
    {
        return myRouteName.equals(serviceRouteName);
    }

    /** Single calls that can route to multiple servers ("federated" calls) may
     * enounter down servers. Log the exception and hide details from the caller.
     * @param e Exception indicating server is missing.
     * @param msg Detail text for our logs.
     * @return Exception to throw back to the caller.
     */
    protected NotAcceptedException convertToNotAcceptedException(org.omg.CORBA.OBJECT_NOT_EXIST e, String msg)
    {
//todo was:        Log.alarm(this, "Caught OBJECT_NOT_EXIST exception, will convert to NotAcceptedException. " + msg);
        Log.exception(this, "Converting OBJECT_NOT_EXIST to NotAcceptedException. " + msg, e);
        return ExceptionBuilder.notAcceptedException("Requested server is not up at this moment.", NotAcceptedCodes.SERVER_NOT_AVAILABLE);
    }

    /** Single calls that can route to multiple servers ("federated" calls) may
     * enounter down servers. Log the exception and hide details from the caller.
     * @param e Exception indicating server is missing.
     * @param msg Detail text for our logs.
     * @return Exception to throw back to the caller.
     */
    protected CommunicationException convertToCommunicationException(org.omg.CORBA.OBJECT_NOT_EXIST e, String msg)
        throws CommunicationException
    {
//todo was:        Log.alarm(this, "Caught OBJECT_NOT_EXIST exception, will convert to CommunicationException. " + msg);
        Log.exception(this, "Converting OBJECT_NOT_EXIST to CommunicationException. " + msg, e);
        return ExceptionBuilder.communicationException("Requested server is not up at this moment.", CommunicationFailureCodes.SERVER_NOT_AVAILABLE);
    }

}
