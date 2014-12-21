package com.cboe.infrastructureServices.traderService;

import java.util.*;

import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;

public interface TraderService {
	public boolean initialize(ConfigurationService configService) ;
	public String export(org.omg.CORBA.Object objectRef, String serviceTypeName, String propertyString);
	public String export(org.omg.CORBA.Object objectRef, String serviceTypeName, Properties props);
	public String export(org.omg.PortableServer.Servant objectRef, org.omg.PortableServer.POA poa, String serviceTypeName, Properties props);
	public String exportPropertiesWithMultipleValues(org.omg.PortableServer.Servant objectRef, org.omg.PortableServer.POA poa, String serviceTypeName, Properties props);
	public org.omg.CosTrading.OfferSeqHolder query(String serviceTypeName, String constraints);
	public DirectoryQueryResult[] queryDirectory(String serviceTypeName, String constraints);
	public DirectoryQueryResult[] queryDirectoryForEC(String serviceTypeName, String constraints);
	public void withdraw(String serviceTypeName, String constraints);
	public String describeType( String serviceTypeName );
	public String[] listTypes( );
}
