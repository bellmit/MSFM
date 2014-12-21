package com.cboe.infrastructureServices.systemsManagementService;

import java.util.Properties;
/**
 *
 */
public class ConfigurationServiceNullImpl extends ConfigurationServiceFileImpl
{
	public ConfigurationServiceNullImpl()
	{
		properties = System.getProperties();
	}
	public void setProperties(Properties props)
	{
		properties = props;
	}
	/**
	 * Override initialize.
	 */
	public boolean initialize(String[] parameters, int firstConfigurationParameter )
	{
		return true;
	}
}	
