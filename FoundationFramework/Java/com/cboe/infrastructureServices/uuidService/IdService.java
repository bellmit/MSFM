package com.cboe.infrastructureServices.uuidService;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;

/**
 * A service to provide a Universally Unique Identifier
 * @author Dave Hoag
 * @version 1.6
 */
public interface IdService 
{
	/**
	 * The basic two methods to obtain unique ID:
	 * 1. getNextUUID() will return a "long" which is up to 64bits
	 * 2. getNextID() will return a "int" which is up to 32 bits.
	 * Both methods will "wrap around" after the max number is used.
	 * 
	 */
	public long getNextUUID() throws SystemException, NotFoundException;
	public int getNextID() throws SystemException, NotFoundException;
	
	/**
	 * getNextUUIDFromBlock() is similar as getNextUUID(). The difference is the way
	 * UUID is calculated. The calling thread will obtain a block of IDs at once in this 
	 * method, and use one at a time. 
	 * 
	 * It should be said that it is strange to expose that implementation detail through
	 * an interface.
	 */
	public long getPerThreadBlockSize();
	public void setPerThreadBlockSize(long blockSize);
	public long getNextUUIDFromBlock() throws SystemException, NotFoundException;	
	
	/** 
	 * Defining an IdService should not be a task of IdService Client, for which this interface
	 * is intended, instead, it is the task of an Administrator who will define the IdService.
	 * So the semantics of this method is redefined as the same of getIdService. And 
	 * SystemException will be thrown if no IdService context is found matching the context passed in. 
	 */
	public IdService defineIdService(long low, long high, String context) throws SystemException;
	
	/**
	 * Return an IdService implementation with given context. If there is no IdService context
	 * found matching the given context, NotFoundException is thrown.
	 */
	public IdService getIdService(String context) throws NotFoundException, SystemException;
	
	/**
	 * IdService facade normally prefetch a certain amount of IDs from IdServer, and 
	 * then distribute the block of Ids to application. This method is provided so
	 * application can override IdService's prefetch quantity configuration if there is
	 * such a need.
	 */
	public void overridePrefetchQuantity(int aQuantity) throws DataValidationException;
	/**
	 * FoundationFramework Framework methods. IdService is one of the FF core services
	 * which will have to provide these two methods.
	 */
	public boolean initialize(ConfigurationService con);
	public void goMaster();
}
