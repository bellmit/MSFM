package com.cboe.infrastructureServices.foundationFramework;

import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.WrongAdapter;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.orbService.NoSuchPOAException;
import com.cboe.infrastructureServices.orbService.OrbService;
import com.cboe.infrastructureServices.traderService.TraderService;
/**
 * A utility class to assist the developers working with POA.
 *
 * @version 2.3
 * @author Dave Hoag
 */
public class POAHelper
{
    static boolean master = false;
    /**
     */
    public static org.omg.PortableServer.Servant reference_to_servant( org.omg.CORBA.Object object, BOHome boHome) throws com.cboe.infrastructureServices.orbService.NoSuchPOAException, WrongPolicy, ObjectNotActive, WrongAdapter
    {
		FoundationFramework ff = FoundationFramework.getInstance();
		OrbService orbSvc = ff.getOrbService();
		String poaName = orbSvc.USER_POA;
		if(boHome != null)
		{
			poaName = boHome.getFrameworkProperty("poaName", poaName);
		}
		org.omg.PortableServer.POA poa = orbSvc.getPOA(poaName);
        return poa.reference_to_servant(object);
    }
	/**
	 * Convenience method to help service developers.
	 * This method may be expanded to obtain a configurable POA name rather than
	 * defaulting to the root poa.
	 *
	 * @return The valid CORBA reference.
	 * @param svnt A Servant.
	 * @param boHome A BOHome. This is an optional parameter. Null is a legal value.
	 */
	public static org.omg.CORBA.Object connect(org.omg.PortableServer.Servant svnt, BOHome boHome) throws com.cboe.infrastructureServices.orbService.NoSuchPOAException
	{
		FoundationFramework ff = FoundationFramework.getInstance();
		OrbService orbSvc = ff.getOrbService();
		String poaName = orbSvc.USER_POA;
        String objectId = null;
		if(boHome != null)
		{
			poaName = boHome.getFrameworkProperty("poaName", poaName);
            objectId = boHome.getFrameworkProperty( "objectId", objectId );
		}
        try
        {
            return orbSvc.connect(poaName, svnt, objectId);
        }
        catch( WrongPolicy ex )
        {
            String message = "Exception when connecting servant " + svnt + " to POA " + poaName;
            if( objectId  == null)
            {
                message += ". Expected poa to be configured with Transient IORs.";
            }
            else
            {
                message += ". Expected poa to be configured with persistent IORs.";
            }
            Log.exception( message, ex);
            throw new IllegalStateException("The Servant and the POA do not appear to have the proper configuration. " + message );
        }
	}
	/**
	 * Convenience method to help service developers.
	 * This method may be expanded to obtain a configurable POA name rather than
	 * defaulting to the root poa.
	 *
	 * @param svnt A Servant.
	 * @param boHome A BOHome. This is an optional parameter. Null is a legal value.
	 */
	public static void disconnect(org.omg.PortableServer.Servant svnt, BOHome boHome) throws com.cboe.infrastructureServices.orbService.NoSuchPOAException
	{
		FoundationFramework ff = FoundationFramework.getInstance();
		OrbService orbSvc = ff.getOrbService();
		String poaName = orbSvc.USER_POA;
		if(boHome != null)
		{
			poaName = boHome.getFrameworkProperty("poaName", poaName);
		}
		orbSvc.disconnect(poaName, svnt);
	}
	/**
	 * Convenience method to help service developers.
	 * This method may be expanded to obtain a configurable POA name rather than
	 * defaulting to the root poa.
	 *
	 * @return The IOR of the exported object.
	 * @param servant A Servant to export.
	 * @param boHome A BOHome. This is an optional parameter. Null is a legal value.
	 */
	public static String exportWithMultipleValues(org.omg.PortableServer.Servant servant, String serviceTypeName, java.util.Properties props, BOHome boHome) throws NoSuchPOAException
	{
    		FoundationFramework ff = FoundationFramework.getInstance();
		TraderService traderSvc = ff.getTraderService();
        	org.omg.PortableServer.POA poa = getPoa(boHome);
		return traderSvc.exportPropertiesWithMultipleValues(servant, poa, serviceTypeName, props);
	}
	/**
	 * Convenience method to help service developers.
	 * This method may be expanded to obtain a configurable POA name rather than
	 * defaulting to the root poa.
	 *
	 * @return The IOR of the exported object.
	 * @param servant A Servant to export.
	 * @param boHome A BOHome. This is an optional parameter. Null is a legal value.
	 */
	public static String export(org.omg.PortableServer.Servant servant, String serviceTypeName, java.util.Properties props, BOHome boHome) throws NoSuchPOAException
	{
    		FoundationFramework ff = FoundationFramework.getInstance();
		TraderService traderSvc = ff.getTraderService();
        	org.omg.PortableServer.POA poa = getPoa(boHome);
		return traderSvc.export(servant, poa, serviceTypeName, props);
	}
	private static org.omg.PortableServer.POA getPoa(BOHome boHome) throws NoSuchPOAException
    	{
    		FoundationFramework ff = FoundationFramework.getInstance();
//For now, we don't care about the master state
//        if(! isMaster())
//        {
//            ff.getDefaultLogService().log(MsgPriority.medium, MsgCategory.information, "OrbService.export", "Export of " + serviceTypeName + " ignored. Not a Master service." );
//            return "";
//        }
		OrbService orbSvc = ff.getOrbService();
		String poaName = orbSvc.USER_POA;
		if(boHome != null)
		{
			poaName = boHome.getFrameworkProperty("poaName", poaName);
		}
		org.omg.PortableServer.POA poa = orbSvc.getPOA(poaName);
        	return poa;
    }
    /**
     * Invoked when the Process is going live.
     */
    public final static void setMaster(boolean value)
    {
        master = value;
    }
    /**
     *
     */
    public final static boolean isMaster()
    {
        return master;
    }
}
