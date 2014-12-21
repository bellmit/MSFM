package com.cboe.infrastructureServices.uuidService;
import java.util.HashMap;

import com.cboe.exceptions.DataValidationException;
/**
 * Title:        Basically a null impl that returns a long value<p>
 * Description:  <p>
 * @author David Hoag
 * @version 1.3
 */
public class IdServiceSimpleImpl extends IdServiceBaseImpl
{
	HashMap map = new HashMap();
	long value = 1;
	long lowLimit, highLimit;
	int value2 = 1;
	boolean ranged = false;
	/**
	 * Define a new context with the defined bounds. If an id service with this same 
	 * context is already defined, just return the existing id service.
	 *
	 * @param context A context that will uniquely identify this service
	 * @param high The upper bounds
	 * @param low The starting range of this service
	 * @return Either the existing id service with the specified context, or a new one.
	 */
	public synchronized IdService defineIdService(long low, long high, String context)
	{
		IdService existingService = getIdService(context);
		if(existingService == null)
		{
			IdServiceSimpleImpl simpleExistingService = new IdServiceSimpleImpl();
			simpleExistingService.setRanged(true);
			simpleExistingService.setLowLimit( low );
			simpleExistingService.setHighLimit( high );
			map.put(context, existingService);
			existingService = simpleExistingService;
		}
		return existingService;
	}
	/**
	 */
	protected void setHighLimit(long high)
	{
		highLimit = high;
	}
	/**
	/**
	 */
	protected void setLowLimit(long low)
	{
		lowLimit = low;
	}
	/**
	 */
	protected void setRanged(boolean b)
	{
		ranged = b;
	}
	/**
	 */
	public IdService getIdService(String context)
	{
		return (IdService)map.get(context);
	}
	/**
	 */
	public IdServiceSimpleImpl()
	{
        //Improve the likely hood of successful runs one after each other
		value = System.currentTimeMillis();
        long rnd = new java.util.Random().nextInt();
        rnd = (rnd << 32) ; //move int to high bits
        rnd = rnd ^ value; //exclusive or rnd number with time
        value = value & 0x00000000FFFFFFFFl; //drop high bits from value
        rnd = rnd & 0xFFFFFFFF00000000l;  //drop low bits from rnd
        value = value | rnd; //merge together
	}

	/**
	 *
	 */
	public synchronized long getNextUUID()
	{
		return value++;
	}

	/**
	 *
	 */
	public synchronized long getNextUUIDFromBlock()
	{
		return value++;
	}

	/**
	 *
	 */
	public synchronized int getNextID()
	{
		return value2++;
	}
	
	/**
	 * IdService facade normally prefetch a certain amount of IDs from IdServer, and 
	 * then distribute the block of Ids to application. This method is provided so
	 * application can override IdService's prefetch quantity configuration if there is
	 * such a need.
	 */
	public void overridePrefetchQuantity(int aQuantity) throws DataValidationException
	{
		//do nothing. This implementation will never go the idserver
	}
}
