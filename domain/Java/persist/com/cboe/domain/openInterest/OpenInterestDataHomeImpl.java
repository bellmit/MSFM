package com.cboe.domain.openInterest;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiErrorCodes.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.infrastructureServices.systemsManagementService.*;

import com.cboe.interfaces.domain.OpenInterestDataHome;
import com.cboe.interfaces.domain.OpenInterestData;
import com.cboe.util.*;
import java.util.*;
/**
 *  A persistentable implementation of OpenInterestDataHome.
 *
 *@author  David Hoag
 *@created  September 19, 2001
 */
public class OpenInterestDataHomeImpl extends BOHome implements OpenInterestDataHome
{
	/**
	 *  MarketDataHomeImpl constructor comment.
	 */
	public OpenInterestDataHomeImpl()
	{
		setSmaType( "GlobalOpenInterestData.OpenInterestDataHomeImpl" );
	}
	/**
	 *
	 */
	public OpenInterestData[] findAll( ) throws SystemException
	{
		try
		{
			OpenInterestDataImpl search = new OpenInterestDataImpl();
			ObjectQuery query = new ObjectQuery( search );
			addToContainer( search );
			Vector list = query.find( );
			OpenInterestDataImpl[] result = new OpenInterestDataImpl[ list.size() ];
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
	 *@param  sessionName
	 *@return  The marketData value
	 *@exception  PersistenceException
	 */
	private OpenInterestData getOpenInterestData( String productName ) throws PersistenceException, DataValidationException
	{
		if( productName == null || productName.trim().equals("") )
		{
			throw ExceptionBuilder.dataValidationException( "OpenInterestData must be requested for a specific product name!", 0);
		}
		OpenInterestDataImpl search = new OpenInterestDataImpl();
		ObjectQuery query = new ObjectQuery( search );
		addToContainer( search );
	//	search.setProductName( productName );

		Vector all = query.find();
		OpenInterestDataImpl result = null;
		if( all.size() != 0 )
		{
			result = ( OpenInterestDataImpl ) all.get( 0 );
			addToContainer( result );
		}
		return result;
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
