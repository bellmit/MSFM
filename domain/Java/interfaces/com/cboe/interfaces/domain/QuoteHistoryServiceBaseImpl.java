package com.cboe.interfaces.domain;

import com.cboe.exceptions.DataValidationException;
import com.cboe.infrastructureServices.foundationFramework.BObject;

/**
 * Base class for both local and proxy implementation.
 * 
 * @author singh
 * 
 */
public abstract class QuoteHistoryServiceBaseImpl extends BObject implements QuoteHistoryService
{
	private final String PROPERTY_SERVICE_ID = "serviceidentifier";

	private final String DEFAULT_SERVICE_ID = "serviceidentifier_NOT_SET";

	private String serviceIdentifier;

	private String serverRouteName;

	public String getServerRouteName()
	{
		return serverRouteName;
	}

	public void setServerRouteName(String routeName)
	{
		this.serverRouteName = routeName;
	}

	public String getServiceIdentifier()
	{
		if (serviceIdentifier == null)
		{
			String defaultId = DEFAULT_SERVICE_ID + "_" + System.currentTimeMillis();

			serviceIdentifier = getBOHome().getProperty(PROPERTY_SERVICE_ID, defaultId);
		}

		return serviceIdentifier;
	}

	public void checkServiceIdentifier(String serviceId) throws DataValidationException
	{
		if (this.getServiceIdentifier().equals(serviceId) == false)
		{
			throw new DataValidationException("Service Id mismatch. Supported: " + getServiceIdentifier() + ". Received: "
					+ serviceId, null);
		}
	}
}
