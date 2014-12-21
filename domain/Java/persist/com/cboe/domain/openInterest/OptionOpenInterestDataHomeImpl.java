package com.cboe.domain.openInterest;

import com.cboe.exceptions.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.persistenceService.*;

import com.cboe.interfaces.domain.OptionOpenInterestDataHome;
import com.cboe.interfaces.domain.OptionOpenInterestData;
import com.cboe.util.*;
import java.util.*;

/**
 *  A persistentable implementation of OpenInterestDataHome.
 *  UD 10/06/05
 */
public class OptionOpenInterestDataHomeImpl extends BOHome implements OptionOpenInterestDataHome
{
	/**
	 *  OptionOpenInterestDataHomeImpl constructor.
	 */
	public OptionOpenInterestDataHomeImpl()
	{
		setSmaType( "GlobalOpenInterestData.OptionOpenInterestDataHomeImpl" );
	}

	/**
	 *  Find all the Open Interest Data for Options.
	 */
	public OptionOpenInterestData[] findAll( ) throws SystemException
	{
		try
		{
			OptionOpenInterestDataImpl search = new OptionOpenInterestDataImpl();
			ObjectQuery query = new ObjectQuery( search );
			addToContainer( search );
			Vector list = query.find( );
			OptionOpenInterestDataImpl[] result = new OptionOpenInterestDataImpl[ list.size() ];
			list.copyInto( result );
			for (int i = 0; i < result.length; i++) {
				addToContainer( result[i] );
			}

			return result;
		}
		catch( PersistenceException pe )
		{
			Log.exception( this, "Failed to find the Open Interest.", pe);
			throw ExceptionBuilder.systemException( "Persistence problem looking for OpenInterestData by classes",0 );
		}
	}

	/**
	 *  Prepares to begin processing as master.
	 *
	 *@param  failover
	 */
	public void goMaster( boolean failover ) { }
	/**
	 *  Prepares to begin processing as slave.
	 */
	public void goSlave() { }
}
