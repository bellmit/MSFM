package com.cboe.infrastructureServices.systemsManagementService;


/**
 * @author Dave Hoag
 * @version 1.2
 */
public class AdminServiceNullImpl extends AdminServiceImpl
{
	/**
	 * Prevent corba registration.
	 */
	public boolean initialize(ConfigurationService configService)
	{
		return true;
	}
}