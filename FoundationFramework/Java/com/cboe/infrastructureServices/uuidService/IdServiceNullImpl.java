package com.cboe.infrastructureServices.uuidService;

import com.cboe.exceptions.DataValidationException;

/**
 * Delegate the request to the actual id service implementation.
 * @author Dave Hoag
 * @version 1.3
 */
public class IdServiceNullImpl extends IdServiceBaseImpl
{
	/** */
	public IdService defineIdService(long low, long high, String context)
	{
		return null;
	}
	public IdService getIdService(String context)
	{
		return null;
	}
	public int getNextID()
	{
        throw new RuntimeException("No ID service configured. Please configure uuidServiceImpl for either the SimpleImpl (for testing) or a real IdService");
	}
	public long getNextUUID()
	{
        throw new RuntimeException("No ID service configured. Please configure uuidServiceImpl for either the SimpleImpl (for testing) or a real IdService");
	}
	public long getNextUUIDFromBlock()
	{
        throw new RuntimeException("No ID service configured. Please configure uuidServiceImpl for either the SimpleImpl (for testing) or a real IdService");
	}
	public long getPerThreadBlockSize()
	{
        throw new RuntimeException("No ID service configured. Please configure uuidServiceImpl for either the SimpleImpl (for testing) or a real IdService");
	}
	public void setPerThreadBlockSize(long blockSize)
	{
        throw new RuntimeException("No ID service configured. Please configure uuidServiceImpl for either the SimpleImpl (for testing) or a real IdService");
	}
	
	/**
	 * IdService facade normally prefetch a certain amount of IDs from IdServer, and 
	 * then distribute the block of Ids to application. This method is provided so
	 * application can override IdService's prefetch quantity configuration if there is
	 * such a need.
	 */
	public void overridePrefetchQuantity(int aQuantity) throws DataValidationException{
		throw new RuntimeException("No ID service configured. Please configure uuidServiceImpl for either the SimpleImpl (for testing) or a real IdService");		
	}
}
