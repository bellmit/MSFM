package com.cboe.domain.util;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.uuidService.IdService;
import com.cboe.interfaces.domain.HistoryServiceIdGenerator;

public class HistoryServiceIdGeneratorOldImpl implements HistoryServiceIdGenerator
{
	private IdService idService;
	
	public HistoryServiceIdGeneratorOldImpl()
	{
		getIdService();
	}

	private final IdService getIdService()
	{
		if(idService == null)
		{
			idService =  FoundationFramework.getInstance().getIdService();
		}
		return idService;
	}
	
	public Object getId()
	{
		try
		{
			return new Long (getIdService().getNextUUID());
		}
		catch (Exception e)
		{
			Log.exception(Thread.currentThread().getName() + " caught exception while getting next unique id.", e);

			throw new RuntimeException (e);
		}
	}

}
