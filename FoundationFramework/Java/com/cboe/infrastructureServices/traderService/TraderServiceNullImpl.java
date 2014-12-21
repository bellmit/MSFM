package com.cboe.infrastructureServices.traderService;

import java.util.*;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
/**
 * @author Dave Hoag
 * @version 1.0
 */
public class TraderServiceNullImpl implements TraderService
{
	public TraderServiceNullImpl()
	{
	}
	public boolean initialize(ConfigurationService configService)
	{
		return true;
	}
	/** */
	public String export(org.omg.CORBA.Object objectRef, String serviceTypeName, String propertyString)
	{
		return "";
	}
	/** */
	public String export(org.omg.CORBA.Object objectRef, String serviceTypeName, Properties props)
	{
		return "";
	}
	/** 
	 * Facilitate testing of other services. 
	 * 
	 */
	public String export(org.omg.PortableServer.Servant objectRef, org.omg.PortableServer.POA poa, String serviceTypeName, Properties props)
	{
		return "";
	}
	public String exportPropertiesWithMultipleValues(org.omg.PortableServer. Servant objectRef, org.omg.PortableServer.POA poa, String serviceTypeName, Properties props)
	{
		return "";
	}
	public org.omg.CosTrading.OfferSeqHolder query(String serviceTypeName, String constraints)
	{
		return null;
	}
	public DirectoryQueryResult[] queryDirectory(String serviceTypeName, String constraints)
	{
		return null;
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
