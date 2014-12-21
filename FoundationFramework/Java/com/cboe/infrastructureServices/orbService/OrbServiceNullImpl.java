package com.cboe.infrastructureServices.orbService;

import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
/**
 * @author Dave Hoag
 */ 
public class OrbServiceNullImpl extends OrbServiceBaseImpl
{
	public boolean initialize(ConfigurationService configService) { return true; }
	public org.omg.CORBA.ORB getOrb()
	{
		return null;
	}
	public void connect(org.omg.CORBA.Object obj) throws org.omg.CORBA.SystemException
	{
	}
	public org.omg.PortableServer.POA getPOA(String poaName) throws NoSuchPOAException
	{
		return null;
	}
	public void disconnect(org.omg.CORBA.Object obj) throws org.omg.CORBA.SystemException
	{
	}
	public String object_to_string(org.omg.CORBA.Object obj) throws org.omg.CORBA.SystemException
	{
		return null;
	}
	public org.omg.CORBA.Object string_to_object(String iorString) throws org.omg.CORBA.SystemException
	{
		return null;
	}
	public void disconnect(String poaName, org.omg.PortableServer.Servant svnt)throws NoSuchPOAException
	{
	}
	
	/**
	 * This method is added to support MIOP channel
	 */
	public void setupMulticastChannelPOA(String multicastChannelName, org.omg.PortableServer.Servant servant)
	{
		String msg = "OrbServiceNullImpl >> setupMulticastChannelPOA is called for: ";
		msg = msg + multicastChannelName + " and servant: " + servant;
		System.out.println(msg);
	}
	
}
