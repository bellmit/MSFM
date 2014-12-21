package com.cboe.domain.openInterest;

import java.util.Vector;

import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.interfaces.domain.AllOptionsOpenInterestDataHome;
import com.cboe.interfaces.domain.OptionOpenInterestData;
import com.cboe.util.ExceptionBuilder;

/**
 *A persistent implementation of <code>AlloptionsOpenInterestDataHome</code>.
 *@author  Cognizant Technology Solutions
 *@created  Aug 7, 2007
 */


public class AllOptionsOpenInterestDataHomeImpl extends BOHome implements AllOptionsOpenInterestDataHome 
{
	/**
	 *  AllOptionsOpenInterestDataHomeImpl constructor.
	 */
	public AllOptionsOpenInterestDataHomeImpl()
	{
		setSmaType( "GlobalOpenInterestData.AllOptionsOpenInterestDataHomeImpl" );
	}

	/**
	 *  Find all the Open Interest Data for Options.
	 */
	public OptionOpenInterestData[] findAll( ) throws SystemException
	{
		try
		{
			AllOptionsOpenInterestDataImpl search = new AllOptionsOpenInterestDataImpl();
			ObjectQuery query = new ObjectQuery( search );
			addToContainer( search );
			Vector list = query.find( );
			AllOptionsOpenInterestDataImpl[] result = new AllOptionsOpenInterestDataImpl[ list.size() ];
			list.copyInto( result );
			for ( int i = 0; i < result.length; i++ ) 
			{
				addToContainer( result[i] );
			}
			return result;
		}
		catch( PersistenceException pe )
		{
			Log.exception( this, "Failed to find the Open Interest.", pe);
			throw ExceptionBuilder.systemException( "Persistence problem looking for OptionOpenInterestData by classes",0 );
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
