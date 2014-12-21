package com.cboe.domain.user;

//----------------------------------------------------------------------
// Source file: Java/com/cboe/domain/user/UserAccountRelation.java
//
// PACKAGE: com.cboe.domain.user
//----------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
//----------------------------------------------------------------------


import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.util.ExceptionBuilder;
import com.cboe.interfaces.domain.Firm;
import com.cboe.interfaces.domain.FirmHome;
import com.cboe.interfaces.domain.user.User;
import com.cboe.interfaces.domain.user.UserHome;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.idl.cmiUser.AccountStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.user.AccountDefinitionStruct;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Vector;

import com.cboe.domain.util.FirmExchangeContainer;
import com.cboe.domain.util.StructBuilder;
import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.TimeServiceWrapper;

/**
 * A user account relation
 *
 * @author Matt Sochacki
 */
public class UserAccountRelation extends PersistentBObject {

	/**
	 * Table name used for object mapping.
	 */
	public static final String TABLE_NAME = "user_account_relation";

	/**
	 * The user associated to the account
	 */
	private AcronymUserImpl user;

	/**
	 * Account implemted as a user
	 */
	private AcronymUserImpl account;

	/**
	 * isActive indicator
	 */
	private boolean isActive;

	/**
	 * The last modified time stamp
	 */
	private long lastModifiedTime;

	/**
	 * The userHome
	 */
	private UserHome userHome;

	/**
	 * The firmHome
	 */
	private FirmHome firmHome;

	/*
	 * Fields for JavaGrinder.
	 */
	private static Field _user;
	private static Field _account;
	private static Field _isActive;
	private static Field _lastModifiedTime;

	/*
	 * JavaGrinder attribute descriptions.
	 */
	private static Vector classDescriptor;

	/*
	 * Initialize fields
	 */
	static {
		try {
			_user = UserAccountRelation.class.getDeclaredField("user");
			_account = UserAccountRelation.class.getDeclaredField("account");
			_isActive = UserAccountRelation.class.getDeclaredField("isActive");
			_lastModifiedTime = UserAccountRelation.class.getDeclaredField("lastModifiedTime");
		}
		catch (Exception e) {
			System.out.println("Unable to initialize JavaGrinder fields for UserAccountRelation: " + e);
		}
	}

	/**
	 * Constructs a new user account relation.  This constructor is needed for queries.
	 */
	public UserAccountRelation() {
	}

	/**
	 * Constructs a new user account relation.
	 *
	 * @param user
	 * @param account
	 * @param active
	 * @param lastModTime
	 */
	public UserAccountRelation(AcronymUserImpl user, AcronymUserImpl account, boolean active, long lastModTime ) {
		super();
		setUser( user );
		setAccount( account );
		setIsActive(active);
		setLastModifiedTime(lastModTime);
	}

	/**
	 * Constructs a new user account relation.
	 *
	 * @param user
	 */
	public UserAccountRelation(AcronymUserImpl user ){
		super();
		setUser( user );
	}

	/**
	 * Gets user.
	 */
	public AcronymUserImpl getUser() {
		return (AcronymUserImpl) editor.get(_user, user);
	}

	/**
	 * Gets account.
	 */
	public AcronymUserImpl getAccount() {
		return (AcronymUserImpl) editor.get(_account, account);
	}

	/**
	 * Gets the isActive indicator
	 */
	public boolean isActive() {
		return editor.get(_isActive , isActive);
	}

	/**
	 * Determines if this relation is the user's account
	 */
	public boolean isUserAccount() {
		if( getAccount().getAcronym().equals( getUser().getAcronym() ) ){
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Gets the last modified timestamp
	 */
	private long getLastModifiedTime() {
		return editor.get(_lastModifiedTime, lastModifiedTime);
	}


	/**
	 * Describe how this class relates to the relational database.
	 */
	public void initDescriptor()
	{
		synchronized (UserAccountRelation.class)
		{
			if (classDescriptor != null)
				return;
			Vector tempDescriptor = getSuperDescriptor();
			tempDescriptor.addElement(AttributeDefinition.getForeignRelation(AcronymUserImpl.class, "user_key", _user));
			tempDescriptor.addElement(AttributeDefinition.getInstanceRelation(AcronymUserImpl.class, "account_key", _account));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("active", _isActive));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("lastModifiedTime", _lastModifiedTime));
			classDescriptor = tempDescriptor;
		}
	}

	/**
	* Needed to define table name and the description of this class.
	*/
	public ObjectChangesIF initializeObjectEditor()
	{
		final DBAdapter result = (DBAdapter) super.initializeObjectEditor();
		if (classDescriptor == null)
			initDescriptor();
		result.setTableName(TABLE_NAME);
		result.setClassDescription(classDescriptor);
		return result;
	}

	/**
	 * Sets the user.
	 *
	 * @param user associted with the account
	 */
	public void setUser(AcronymUserImpl newUser ) {
		editor.set(_user, newUser, user);
	}

	/**
	 * Sets the account name .
	 *
	 * @param accountName of the account name
	 */
	public void setAccount(AcronymUserImpl newAccount) {
		editor.set(_account, newAccount, account);
	}

	/**
	 * Sets the isActive indicator
	 *
	 * @param aIsActive
	 */
	public void setIsActive( boolean aIsActive ) {
		editor.set(_isActive, aIsActive , isActive);
	}

	/**
	 * Sets the last modeified timestamp
	 *
	 * @param lastModTimestamp
	 */
	public void setLastModifiedTime(long lastModTime) {
		editor.set(_lastModifiedTime, lastModTime, lastModifiedTime);
	}

	/**
	 * Formats this assignment as a string.
	 */
	public String toString() {
		return getUser().getAcronym() + " " + getAccount().getAcronym();
	}


	/**
	 * This method allows me to get arounds security problems with updating
	 * and object from a generic framework.
	 */
	public void update(boolean get, Object[] data, Field[] fields)
	{
		for (int i = 0; i < data.length; i++)
		{
			try
			{
				if (get)
					data[i] = fields[i].get(this);
				else
					fields[i].set(this, data[i]);
			}
			catch (IllegalAccessException ex)
			{
				System.out.println(ex);
			}
			catch (IllegalArgumentException ex)
			{
				System.out.println(ex);
			}
		}
	}

	/**
	 * Sets all values for the relation from the definition struct.
	 *
	 * @param accountStruct 	struct containing values for user
	 * @exception DataValidationException if validation checks fail
	 */
	public void fromStruct(AccountDefinitionStruct accountStruct) throws DataValidationException
	{
        Firm firm = null;
        try
        {
            firm = (Firm)getFirmHome().findFirmByNumber( accountStruct.executingGiveupFirm );
        }
        catch(NotFoundException nfe)
        {
            String msg = "Not found firm number " +toString(accountStruct.executingGiveupFirm) 
                + " for Account " + toString(accountStruct.account);
            Log.alarm(this, msg + nfe.details.message + nfe );
            throw ExceptionBuilder.dataValidationException(msg, 0);
        }
        catch (Exception e)
        {
            Log.exception(this, "Unable to find firm, with firm number " + toString(accountStruct.executingGiveupFirm) + " - " +  e, e);
            throw ExceptionBuilder.dataValidationException("Unable to find firm, with firm number " + toString(accountStruct.executingGiveupFirm), 0);
        }
        try
        {
            if (getUser().getAcronym().equals(accountStruct.account.acronym) &&
                            getUser().getExchangeAcronym().equals(accountStruct.account.exchange))
            {
                   setAccount(getUser()); //Use the user of this account if it has the same exchange acronym as the account.
                   // If firms don't match, there may be a data problem.  Log issue, but allow update to
                   // contintue.
                   if (getUser().getFirmKey() != firm.getFirmKey())
                   {
                        Log.alarm("Firm of account " + toString(accountStruct.account) + ", with firmKey ("+ getUser().getFirmKey() +
                                          "), does not match active clearing firm " + toString(accountStruct.executingGiveupFirm) + " with firmKey " + firm.getFirmKey());
                   }
            }
            else
            {
                    List usersForAcctAcr = getUserHome().findByExchangeAcronym(accountStruct.account);
                    if (usersForAcctAcr.isEmpty())
                    {
                        final String msg = "No user (account) found for " + toString(accountStruct.account) + " for update of user " + getUser();
                        throw ExceptionBuilder.notFoundException(msg, NotFoundCodes.RESOURCE_DOESNT_EXIST);
                    }
                    UserCombinedImpl accountDef = (UserCombinedImpl)usersForAcctAcr.get(0);
                    setAccount( accountDef.getAcronymUserImpl() );
                    // If firms don't match, there may be a data problem.  Log issue, but allow update to
                    // contintue.
                    if (accountDef.getAcronymUserImpl().getFirmKey() != firm.getFirmKey())
                    {
                        Log.alarm("Firm of account " + toString(accountStruct.account) + " with firmKey " + accountDef.getAcronymUserImpl().getFirmKey() +
                                                ", does not match active clearing firm " + toString(accountStruct.executingGiveupFirm) + " with firmKey " + firm.getFirmKey());
                    }
            }
		}
        catch (NotFoundException e )
        {
            Log.alarm("UserAccountRelation: failed to setup account information - NotFoundException: " + e.details.message);
			throw ExceptionBuilder.dataValidationException("Unable to find account - " + toString(accountStruct.account) + " (internal exception " + e + ")", 0);
		}
        catch (Exception e )
        {
            Log.exception("UserAccountRelation: failed to setup account information", e);
			throw ExceptionBuilder.dataValidationException("Unable to find account - " + toString(accountStruct.account) + " (internal exception " + e + ")", 0);
		}
		setIsActive( accountStruct.isActive );
		if (!StructBuilder.isDefault(accountStruct.lastModifiedTime))
        {
			setLastModifiedTime(DateWrapper.convertToMillis(accountStruct.lastModifiedTime));
		}
		else
        {
			setLastModifiedTime(TimeServiceWrapper.getCurrentDateTimeInMillis());
		}
	}

	/**
	 * Creates an AccountStruct
         * Note: account in AccountStruct is the acronym of the account
	 *
	 */
	public AccountStruct toStruct()
	{
		AccountStruct as = new AccountStruct();
		AccountDefinitionStruct ads = toDefinitionStruct();
		as.account = ads.account.acronym;
		as.executingGiveupFirm = ads.executingGiveupFirm;
		return as;
	}

	/**
	 * Creates an AccountDefinitionStruct
	 *
	 */
	public AccountDefinitionStruct toDefinitionStruct()
	{
		AccountDefinitionStruct ads = new AccountDefinitionStruct();
		ads.account = new ExchangeAcronymStruct(getAccount().getExchangeAcronym(), getAccount().getAcronym());
		try{
			Firm firm = getFirmHome().findFirmByKey( getAccount().getFirmKey() );
			ads.executingGiveupFirm = new ExchangeFirmStruct(firm.getExchangeAcr(), firm.getFirmNumber());
		} catch (Exception e ){
			//This should not happen since you should not be able to create an account without having a valid firm
			Log.exception( this, e );
		}

		ads.isActive = isActive();
		ads.lastModifiedTime = DateWrapper.convertToDateTime(getLastModifiedTime());

		return ads;

	}

	/**
	 * Get the UserHome
	 */
	public UserHome getUserHome() {

		try{

			if( userHome == null ){
				userHome = (UserHome)HomeFactory.getInstance().findHome( UserHome.HOME_NAME );
			}
		}
		catch( Exception e ){
			Log.exception(this, e );
		}
		return userHome;
	}

	/**
	 * Get the FirmHome
	 */
	public FirmHome getFirmHome() {

		try{

			if( firmHome == null ){
				firmHome = (FirmHome)HomeFactory.getInstance().findHome( FirmHome.HOME_NAME );
			}
		}
		catch( Exception e ){
			Log.exception(this, e );
		}
		return firmHome;
	}
    
    private String toString(ExchangeFirmStruct exchFirm)
    {
        return exchFirm.exchange + ":" + exchFirm.firmNumber;
    }
    private String toString(ExchangeAcronymStruct exchUser)
    {
        return exchUser.exchange + ":" + exchUser.acronym; 
    }
}

