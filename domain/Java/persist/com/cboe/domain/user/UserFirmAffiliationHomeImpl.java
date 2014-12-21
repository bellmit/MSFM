package com.cboe.domain.user;


import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.user.UserFirmAffiliationStruct;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.interfaces.domain.user.UserFirmAffiliation;
import com.cboe.util.ExceptionBuilder;

import java.util.Enumeration;
import java.util.Vector;

public class UserFirmAffiliationHomeImpl extends BOHome implements com.cboe.interfaces.domain.user.UserFirmAffiliationHome {

	/**
	 * Creates a home instance.
	 *
	 */
	public UserFirmAffiliationHomeImpl() {
		setSmaType("GlobalProperty.UserFirmAffiliationHomeImpl");
	}

	/**
	 * Adds each object in the vector the container
	 *
	 * @param vector	vector of objects
	 */
	private void addObjectsToContainer(Vector vector) {

		Enumeration firmsEnum = vector.elements();
		while( firmsEnum.hasMoreElements() ){
			addToContainer( (UserFirmAffiliationImpl)firmsEnum.nextElement() );
		}

	}

	public com.cboe.interfaces.domain.user.UserFirmAffiliation create(UserFirmAffiliationStruct newUserFirmAffiliationStruct) throws DataValidationException {
		UserFirmAffiliationImpl userFirmAffiliation = new UserFirmAffiliationImpl();
		addToContainer(userFirmAffiliation);

        userFirmAffiliation.fromStruct( newUserFirmAffiliationStruct );
		return userFirmAffiliation;
	}


	/**
	 * Performs queries that return multiple properties.
	 *
	 * @param query query for a set of properties
	 * @return properties found by query
	 */
	private com.cboe.interfaces.domain.user.UserFirmAffiliation[] doQuery(ObjectQuery query) {
		com.cboe.interfaces.domain.user.UserFirmAffiliation[] result;
		try
		{
			Vector queryResult = query.find();
			result = new com.cboe.interfaces.domain.user.UserFirmAffiliation[queryResult.size()];
			queryResult.copyInto(result);
			addObjectsToContainer( queryResult );
		}
		catch (PersistenceException e)
		{
			Log.exception(this, "Query for UserFirmAffiliation failed", e);
			result = new com.cboe.interfaces.domain.user.UserFirmAffiliation[0];
		}
		return result;
	}



	public UserFirmAffiliation[] findAll() {
		UserFirmAffiliationImpl example = new UserFirmAffiliationImpl();
		addToContainer(example);
		ObjectQuery query = new ObjectQuery(example);
		return doQuery(query);
	}


	public UserFirmAffiliation[] findByAffiliatedFirm(String affiliatedFirm) throws NotFoundException {
		UserFirmAffiliationImpl example = new UserFirmAffiliationImpl();
		addToContainer(example);
		ObjectQuery query = new ObjectQuery(example);
        example.setAffiliatedFirm(affiliatedFirm);
		return doQuery(query);
	}

    public UserFirmAffiliation findByUserExchange(ExchangeAcronymStruct exchangeAcronymStruct) throws NotFoundException {
        UserFirmAffiliationImpl example = new UserFirmAffiliationImpl();
        addToContainer(example);
        ObjectQuery query = new ObjectQuery(example);
        example.setExchangeAcronym(exchangeAcronymStruct.exchange);
        example.setUserAcronym(exchangeAcronymStruct.acronym);
        return doUniqueQuery(query);
    }


	private UserFirmAffiliation doUniqueQuery(ObjectQuery query) throws NotFoundException {
		UserFirmAffiliationImpl userFirmAffiliation;
		try
		{
			userFirmAffiliation = (UserFirmAffiliationImpl) query.findUnique();
			if (userFirmAffiliation == null)
			{
				throw ExceptionBuilder.notFoundException("Could not find userFirmAffiliation with name = " + name, 0);
			}
			addToContainer( userFirmAffiliation );
		}
		catch (PersistenceException e)
		{
			throw ExceptionBuilder.notFoundException("Query failed for userFirmAffiliation: " + e, 0);
		}
		return userFirmAffiliation;
	}



	/**
	 * Queries properties in database to prime object pool, if it is being
	 * used.
	 *
	 */
    public void goMaster(boolean failover)
    {
		Log.information(this, "Start of Initialization....");
		UserFirmAffiliationImpl example = new UserFirmAffiliationImpl();
		addToContainer(example);
		ObjectQuery query = new ObjectQuery(example);
		try
		{
			Vector userAffilatedFirm = query.find();
			Log.information(this, "Number of usersAffilatedFirms in database = " + userAffilatedFirm.size());
		}
		catch (PersistenceException e)
		{
			Log.exception(this, "Initial query for users failed", e);
		}
		Log.information(this, "Initialized!");
    }


	/**
	 * @see BOHome#shutdown
	 */
	public void shutdown()
	{
		Log.information(this, "Shutdown!");
	}
	/**
	 * @see BOHome#start
	 */
	public void start()
	{
		Log.information(this, "Started!");
	}



	public UserFirmAffiliationStruct toUserFirmAffiliationStruct(com.cboe.interfaces.domain.user.UserFirmAffiliation userFirmAffiliation) {
		return toUserFirmAffiliationStruct(userFirmAffiliation);
	}


	public void deleteUserFirmAffiliation(UserFirmAffiliationStruct newUserFirmAffiliationStruct)throws DataValidationException, SystemException
    {
        UserFirmAffiliation userFirmAffiliation;
		try
        {
            userFirmAffiliation = findByUserExchange(newUserFirmAffiliationStruct.userAcronym);
            ((UserFirmAffiliationImpl) userFirmAffiliation).markForDelete();
		}
		catch (PersistenceException e)
        {
			Log.exception(this, "Unable to delete property ", e);
		}
        catch(NotFoundException nfx)
        {
            Log.exception(this, "Unable to delete property ", nfx);
        }
	}



}
