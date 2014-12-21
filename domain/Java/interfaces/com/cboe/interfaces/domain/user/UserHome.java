package com.cboe.interfaces.domain.user;

// Source file: d:/sources/com/cboe/interfaces/domain/user/UserHome.java

import java.util.List;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.idl.user.SessionProfileUserDefinitionStruct;
import com.cboe.idl.user.UserSummaryStruct;

/**
 * A manager for <code>User</code> instances.
 *
 * @author John Wickberg
 */
public interface UserHome
{
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "UserHome";

    /**
     * Creates a new session Profile user.
     *
     * @param newUser CORBA struct containing user information
     * @return created user
     * @exception AlreadyExistsException if user already exists
     * @exception DataValidationException if validation checks fail
     */
    public User create(SessionProfileUserDefinitionStruct newUser, boolean membershipDefined) throws AlreadyExistsException, DataValidationException, SystemException;
    
    /**
     * Create a new user, having all of the attribributes of any existing user for the given exchAcr.
     * 
     * @param userId - the new userId
     * @param exchAcr - the acronym to use to locate most of the new user's information
     * @param createAsActive - should the new user be activE?
     * @return User - the new user
     * 
     * @throws AlreadyExistsException
     * @throws NotFoundException - thrown if no user having exchAcr is found.
     * @throws DataValidationException
     * @throws SystemException
     */
    public User createUsingAcronym(String userId, ExchangeAcronymStruct exchAcr, boolean createAsActive) throws AlreadyExistsException, NotFoundException, DataValidationException, SystemException;
	/**
	 * Searches for all defined users.
	 *
	 * @return List&lt;User&gt; - all defined users
	 */
	public List findAll();
	/**
	 * Finds users having an assigned class.
	 *
	 * @param classKey requested assigned class
	 * @return List&lt;User&gt; - all users having requested class in their assignments
	 */
	public List findByAssignedClass(int classkey);
	/**
	 * Searches for requested user.  The externalKey is a unique identifier
     * for the userid.
	 *
	 * @param userKey key of the requested user
	 * @return found user
	 * @exception NotFoundException if user doesn't exist
	 */
	public User findByUserIdKey(int userKey) throws NotFoundException;
	/**
	 * Searches for users who have the given account userId listed in their accounts vector.
	 *
	 * @param account request account id
	 * @exception DataValidationException - thrown if the account is not found
	 * @exception SystemException - thrown if there was a persistence problem.
	 * @return List&lt;User&gt; - the users who have the given account in the accounts vector.
	 */
	public List findByAccount(String account) throws DataValidationException, SystemException;
 	/**
	 * Searches for all users for a firm.
	 *
	 * @param firmKey the firm key of the user's clearing firm
	 * @return List&lt;User&gt; - all users having requested clearing firm
	 */
	public List findByFirm(int firmKey );

	/**
	 * Searches for all users having the given acronym+exchange.
	 *
	 * @param ExchangeAcronymStruct exchangeAcronymStruct
	 * @return List&lt;User&gt; - the matching users 
	 * @exception NotFoundException if user doesn't exist
	 */
	public List findByExchangeAcronym(ExchangeAcronymStruct exchangeAcronymStruct);
    
    /**
     * Some operations have only an acronym available, and need to access acronym-level user information.  This
     * method will return the first active user found with the given acronym+exchange, otherwise any inactive
     * user.  A NotFoundException will be thrown if no users have this acronym+exchange.
     * 
     * @param exchangeAcronymStruct
     * @return A user having the exchangeAcronymStruct.  Returns an active user if one is available.
     * @throws NotFoundException
     */
    public User findByFirstExchangeAcronym(ExchangeAcronymStruct exchangeAcronymStruct) throws NotFoundException; 
    
	/*
	 * Searches for requested user.
	 *
	 * @param name requested user name
	 * @return found user
	 * @exception NotFoundException if user doesn't exist
	 */
	public User findByUserId(String userId) throws NotFoundException;

	/**
	 * Finds users having a requested type.
	 *
	 * @param type requested type
	 * @return List&lt;User&gt; - all users having requested the requested type
	 */
	public List findByType(short type);
	/**
	 * Finds users having a requested role.
	 *
	 * @param role requested role
	 * @return List&lt;User&gt; - all users having requested the requested role
	 */
	public List findByRole(char role);
	/**
	 * Finds by DPM assigned class: returns users who have a DPM account which is assigned the given class key.
	 *
	 * @param classKey - the class to find by.
	 * @param role - the user role for the results.  '\0' will return all.
     * @return List&lt;User&gt;
	 */
	public List findByDpmAssignedClass(int classKey, char role);

    /**
     * Find the users which have the given assignment type with the assigned classes.
     * @param classKey
     * @param assignmentType
     * @param sessionName
     * @return List&lt;User&gt;
     */ 
    public List findByClassAssignmentType(int classKey, short assignmentType, String sessionName);
    /**
     * Creates CORBA struct containing user definition.
     *
     * @param user user to be converted
     * @return CORBA definition struct
     */
    public SessionProfileUserDefinitionStruct toDefinitionStruct(User user);
    /**
     * Creates CORBA struct containing user information sent to clients.
     * @param user user to be converted
     * @return CORBA user struct
     */
    public SessionProfileUserStruct toUserStruct(User user);

    /**
     * Updates a user with values from the given struct.

     * @param updatedUser user to be updated
     * @param updatedValues new values for user
     * @param membershipDefined if true then the user update info came from membership.
     * @exception DataValidationException if validation checks fail
     */
    public void updateUser(User updatedUser, SessionProfileUserDefinitionStruct updatedValues, boolean membershipDefined) throws DataValidationException;

    /**
     * Updates a user with values from the given struct.
     * @param updatedUser user to be updated
     * @param updatedValues new values for user
     * @param membershipDefined if true then the user update info came from membership.
     * @exception DataValidationException if validation checks fail
     */
    public void updateUserBase(User updatedUser, SessionProfileUserDefinitionStruct updatedValues, boolean membershipDefined) throws DataValidationException;

    /**
     * Delete the userid information
     * <p><b>NOTE: this will not delete the acronym or %99 of the user config.  JUST the userid and enabled status</b>
     * 
     * @param userid
     * @throws NotFoundException
     * @throws PersistenceException
     */
    public void deleteUserid(String userid) throws NotFoundException, SystemException;
    
    /**
     * Update the AcronymUser information for all the given users.
     * This method will NOT update the UserIdentifier information.
     * 
     * @param usersToUpdate&lt;User&gt;
     * @param updatedValues
     */
    public void updateAcronymUserData(List usersToUpdate, SessionProfileUserDefinitionStruct updatedValues, boolean membershipDefined) throws DataValidationException;
    
    public SessionProfileUserDefinitionStruct[] getDpmsForClass(String userid, int classKey) throws NotFoundException;
    public SessionProfileUserDefinitionStruct getDpmJointAccountForClass(String userid, int classKey) throws DataValidationException, NotFoundException;
    
	public void setTestClassesOnly(String userId, boolean newTestClassesOnlyValue) throws DataValidationException;
	public boolean getTestClassesOnly(String userId) throws NotFoundException;
    public UserSummaryStruct toSummaryStruct(User user);
    
    public AcronymUser findAcronymUserForUserId(String userId) throws NotFoundException;
}
