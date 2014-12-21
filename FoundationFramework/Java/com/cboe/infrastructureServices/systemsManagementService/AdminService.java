package com.cboe.infrastructureServices.systemsManagementService;

public interface AdminService extends com.cboe.infrastructureServices.interfaces.adminService.AdminOperations
{
	public boolean initialize(ConfigurationService configService) ;
}