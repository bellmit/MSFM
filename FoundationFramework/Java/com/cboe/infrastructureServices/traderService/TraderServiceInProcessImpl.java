package com.cboe.infrastructureServices.traderService;

import java.util.*;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.infrastructureServices.orbService.*;
import org.omg.PortableServer.POAPackage.WrongPolicy;
/**
 * @author Dave Hoag
 * @version 1.2
 */
public class TraderServiceInProcessImpl implements TraderService
{
	Hashtable exportedPoas;
	public TraderServiceInProcessImpl()
	{
		exportedPoas = new Hashtable();
	}
	public boolean initialize(ConfigurationService configService)
	{
		return true;
	}
	/** */
	public String export(org.omg.CORBA.Object objectRef, String serviceTypeName, String propertyString)
	{
		return null;
	}
	/** */
	public String export(org.omg.CORBA.Object objectRef, String serviceTypeName, Properties props)
	{
		return null;
	}
	/** 
	 * Facilitate testing of other services. 
	 * 
	 */
	public String export(org.omg.PortableServer.Servant objectRef, org.omg.PortableServer.POA poa, String serviceTypeName, Properties props)
	{
		String constraints = new String();
		Enumeration e = props.propertyNames();
		
		while(e.hasMoreElements())
		{
			String propertyName = (String)e.nextElement();
			Object value = props.getProperty(propertyName);
			constraints += propertyName + "==" + value.toString();
		} 
		DirectoryQueryResult[] result =  queryDirectory(serviceTypeName, constraints);
		
		exportedPoas.put(serviceTypeName, objectRef);
		
		return null;
	}
	public String exportPropertiesWithMultipleValues(org.omg.PortableServer. Servant objectRef, org.omg.PortableServer.POA poa, String serviceTypeName, Properties props)
	{
		exportedPoas.put(serviceTypeName, objectRef);
		return null;
	}
	public org.omg.CosTrading.OfferSeqHolder query(String serviceTypeName, String constraints)
	{
		return null;
	}
	public DirectoryQueryResult[] queryDirectory(String serviceTypeName, String constraints)
	{
		org.omg.PortableServer.Servant poa = (org.omg.PortableServer.Servant )exportedPoas.get(serviceTypeName);
		DirectoryQueryResult [] result = null;
		if(poa == null) 
		{
			result = new DirectoryQueryResult[0];
		}
		else
		{
			try
			{
				org.omg.CORBA.Object ref = OrbServiceBaseImpl.getInstance().connect(OrbService.ROOT_POA, poa, null);
				result = new DirectoryQueryResult[1];
				result [0] = new DirectoryQueryResult( ref, new org.omg.CosTrading.Property[0]);
			}
			catch (NoSuchPOAException ex) { System.out.println(ex);  }
			catch (WrongPolicy ex) { System.out.println(ex);  }
		}
		return result;
	}
	public DirectoryQueryResult[] queryDirectoryForEC(String serviceTypeName, String constraints)
	{
		return null;
	}
	public void withdraw(String serviceTypeName, String constraints)
	{
	}
	public String describeType( String serviceTypeName )
	{
		return null;
	}
	public String[] listTypes( )
	{
		return null;
	}


}
