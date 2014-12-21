package com.cboe.infrastructureServices.traderService;

import java.util.*;

public class DirectoryQueryResult {

	private org.omg.CORBA.Object reference;
	private org.omg.CORBA.ORB orb;
	private org.omg.CosTrading.Property[] properties;
	private Properties props;

	public DirectoryQueryResult() {
	}

	protected DirectoryQueryResult( org.omg.CORBA.Object ref,
							  org.omg.CosTrading.Property[] cosProps ) {

		//orb = com.cboe.ORBInfra.ORB.Orb.init();
		orb = com.cboe.infrastructureServices.foundationFramework.FoundationFramework.getInstance().getOrbService().getOrb();
		props = new Properties();
		reference = ref;
		properties = cosProps;
		for ( int i = 0; i < cosProps.length; i++ ){
			props.put( cosProps[i].name,cosProps[i].value.extract_string());
		}
	}

	public org.omg.CORBA.Object getObjectReference() {
		return reference;
	}


	public org.omg.CosTrading.Property[] getCosProperties() {
		
		return properties;
	}

	public Properties getProperties() {
		return props;
	}

	public Properties getMultiplePropertyValues() {
		
		Properties retVal = new Properties();
		for (int i = 0; i < properties.length ; i++){
			String propName = properties[i].name;
			org.omg.CORBA.Any tempAny = orb.create_any();
			tempAny = properties[i].value;
			String value = tempAny.extract_string();
			
			ArrayList temp = (ArrayList)retVal.get(propName);	

			if (temp == null){
				temp = new ArrayList();
				retVal.put(propName, temp);
			}  
			temp.add(value);	
			
		}

		return retVal;
	}

}

