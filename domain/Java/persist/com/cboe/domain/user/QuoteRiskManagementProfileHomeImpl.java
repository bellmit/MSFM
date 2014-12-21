package com.cboe.domain.user;

// Source file: d:/sources/com/cboe/interfaces/domain/quoteRiskManagementProfile/QuoteRiskManagementProfileHome.java

import java.util.Vector;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.interfaces.domain.user.QRMUser;
import com.cboe.interfaces.domain.user.QuoteRiskManagementProfile;
import com.cboe.interfaces.domain.user.QuoteRiskManagementProfileHome;
import com.cboe.interfaces.domain.user.AcronymUser;
import com.cboe.util.ExceptionBuilder;

/**
 * A manager for <code>QuoteRiskManagementProfile</code> instances.
 *
 * @author Steven Sinclair
 */
public class QuoteRiskManagementProfileHomeImpl extends BOHome implements QuoteRiskManagementProfileHome
{
	/**
	 * Create/update a quoteRiskManagementProfile.
	 *
	 * @param user instance of User that the profile struct's information is to be associated with.
	 * @param quoteRiskManagementProfile CORBA struct containing quoteRiskManagementProfile information
	 * @return created/updated quoteRiskManagementProfile
	 */
	public QuoteRiskManagementProfile update(QRMUser user, QuoteRiskManagementProfileStruct profile)
	{
		QuoteRiskManagementProfile impl;
		try
		{
			impl = findByUserAndClassKey(user, profile.classKey);
			impl.fromStruct(profile);
			Log.debug(this, "Updated QRM profile for user " + user.getAcronym() + " and classKey " + profile.classKey);
		}
		catch (NotFoundException ex)
		{
			impl = new QuoteRiskManagementProfileImpl();
			addToContainer((QuoteRiskManagementProfileImpl)impl);
			impl.setUser(user);
			impl.fromStruct(profile);
			Log.debug(this, "Created QRM profile for user " + user.getAcronym() + " and classKey " + profile.classKey);
		}
		return impl;
	}
	/**
	 * Creates a new quoteRiskManagementProfile.
	 *
	 * @param user instance of User that the profile struct's information is to be associated with.
	 * @param newQuoteRiskManagementProfile CORBA struct containing quoteRiskManagementProfile information
	 * @return created quoteRiskManagementProfile
	 * @exception AlreadyExistsException if quoteRiskManagementProfile already exists
	 */
	public QuoteRiskManagementProfile create(QRMUser user, QuoteRiskManagementProfileStruct newProfile) throws AlreadyExistsException
	{
		// Validate "doesn't already exists"
		//
		try
		{
			QuoteRiskManagementProfile oldImpl = findByUserAndClassKey(user, newProfile.classKey);
			throw ExceptionBuilder.alreadyExistsException("cannot create profile: profile with given user/classKey already exists", 0);
		}
		catch (NotFoundException ex)
		{
		}

		QuoteRiskManagementProfileImpl newImpl = new QuoteRiskManagementProfileImpl();
		newImpl.setUser(user);
		newImpl.fromStruct(newProfile);
		addToContainer(newImpl);
		return newImpl;
	}

	/**
	 *  Perform the given query for QuoteRiskManagementProfileStruct's.
	 *
	 *  @param ObjectQuery - the query to perform
	 *  @return QuoteRiskManagementProfile[] - the list of structs found by the query.
	 */
	private QuoteRiskManagementProfileImpl[] doQuery(ObjectQuery query)
	{
		QuoteRiskManagementProfileImpl[] result;
		try
		{
			Vector queryResult = query.find();
			result = new QuoteRiskManagementProfileImpl[queryResult.size()];
			queryResult.copyInto(result);
			for (int i=0; i < result.length; i++)
			{
				addToContainer(result[i]);
			}
		}
		catch (PersistenceException e)
		{
			Log.exception(this, "Query for quote risk management profiles failed", e);
			result = new QuoteRiskManagementProfileImpl[0];
		}
		return result;
	}


	/**
	 * Searches for all defined quoteRiskManagementProfiles.
	 *
	 * @return all defined quoteRiskManagementProfiles
	 */
	public QuoteRiskManagementProfile[] findAll()
	{
		QuoteRiskManagementProfileImpl queryByExample = new QuoteRiskManagementProfileImpl();
		ObjectQuery query = new ObjectQuery(queryByExample);
		return doQuery(query);
	}

	/**
	 * Searches for requested quoteRiskManagementProfiles.
	 *
	 * @param user instance of User to use for location.
	 * @return found QuoteRiskManagementProfiles
	 */
	public QuoteRiskManagementProfile[] findByUser(QRMUser user)
	{
		QuoteRiskManagementProfileImpl queryByExample = new QuoteRiskManagementProfileImpl();
		ObjectQuery query = new ObjectQuery(queryByExample);
		queryByExample.setUser(user);
		return doQuery(query);
	}

	/**
	 * Searches for requested quoteRiskManagementProfiles.
	 *
	 * @param classKey key of the requested QuoteRiskManagementProfile
	 * @return found QuoteRiskManagementProfiles
	 */
	public QuoteRiskManagementProfile[] findByClassKey(int classKey)
	{
		QuoteRiskManagementProfileImpl queryByExample = new QuoteRiskManagementProfileImpl();
		ObjectQuery query = new ObjectQuery(queryByExample);
		queryByExample.setClassKey(classKey);
		return doQuery(query);
	}

	/**
	 * Searches for requested quoteRiskManagementProfile
	 * User & classKey should uniquely identify a profile.
	 *
	 * @param user - user to find profile for
	 * @param classKey - classKey to find profile for.
	 * @exception NotFoundException - thrown if the profile cannot be found.
	 */
	public QuoteRiskManagementProfile findByUserAndClassKey(QRMUser user, int classKey) throws NotFoundException
	{
		Vector profiles = user.getQuoteRiskManagementProfileVector();
		for (int i=0; i < profiles.size(); i++)
		{
			QuoteRiskManagementProfile profile = (QuoteRiskManagementProfile)profiles.elementAt(i);
			if (profile.getClassKey() == classKey)
			{
				return profile;
			}
		}
		throw ExceptionBuilder.notFoundException("Quote risk profile not found for (user,classKey)=(" + user + "," + classKey + ")", 0);
	}

	/**
	 * Updates a quoteRiskManagementProfile with values from the given struct.
	 * Updates the profile identified by user and classkey.
	 *
	 * @param user user to locate the profile for
	 * @param updatedProfile quoteRiskManagementProfile to be updated
	 * @exception NotFoundException thrown if old copy (user/classKey) not found
	 */
	public void updateProfile(QRMUser user, QuoteRiskManagementProfileStruct updatedProfile) throws NotFoundException
	{
		QuoteRiskManagementProfile profile = findByUserAndClassKey(user, updatedProfile.classKey);
		profile.fromStruct(updatedProfile);
	}

	/**
	 *  Remove a profile.
	 *
	 *  @param profile - the profile to remove.
	 */
	public void removeProfile(QuoteRiskManagementProfile profile)
	{
			if (profile instanceof QuoteRiskManagementProfileImpl)
			{
				try
				{
					((QuoteRiskManagementProfileImpl)profile).markForDelete();
				}
				catch (PersistenceException ex)
				{
					Log.exception(this, "Error marking profile for deletion.",  ex);
				}
			}
	}
}
