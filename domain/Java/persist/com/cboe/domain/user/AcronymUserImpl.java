package com.cboe.domain.user;

//----------------------------------------------------------------------
// Source file: Java/com/cboe/domain/user/UserImpl.java
//
// PACKAGE: com.cboe.domain.user
//----------------------------------------------------------------------
// Copyright (c) 1999 The Chicago Board Options Exchange. All Rights Reserved.
//----------------------------------------------------------------------

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.StructBuilder;
import com.cboe.domain.util.TimeServiceWrapper;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiConstants.SessionNameValues;
import com.cboe.idl.cmiConstants.UserRoles;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiQuote.UserQuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiUser.AccountStruct;
import com.cboe.idl.cmiUser.DpmStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.SessionProfileStruct;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.idl.constants.UserTypes;
import com.cboe.idl.user.AccountDefinitionStruct;
import com.cboe.idl.user.MarketMakerClassAssignmentStruct;
import com.cboe.idl.user.SessionClearingAcronymStruct;
import com.cboe.idl.user.SessionProfileUserDefinitionStruct;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.interfaces.domain.Firm;
import com.cboe.interfaces.domain.FirmHome;
import com.cboe.interfaces.domain.session.TradingSessionHome;
import com.cboe.interfaces.domain.user.AcronymUser;
import com.cboe.interfaces.domain.user.QRMUser;
import com.cboe.interfaces.domain.user.QuoteRiskManagementProfile;
import com.cboe.interfaces.domain.user.QuoteRiskManagementProfileHome;
import com.cboe.interfaces.domain.user.User;
import com.cboe.interfaces.domain.user.UserHome;
import com.cboe.util.ExceptionBuilder;



/**
 * A persistent implementation of <code>AcronymUser</code>.
 * 
 * <p><b>NOTE:</b>  This class used to be UserImpl, but is now an element
 *  of the composite class UserCombinedImpl.  This is to accomodate multiple
 *  userids per acronym.
 * 
 * <p><b>NOTE:</b>  The profiles assiciated with this class have two roles
 *		First, there is a default profile
 * 		Second, there are profiles by class
 *		We store both of these in the "profiles" list
 * 		The structs have them separeted out so we join and filter these two when converting
 *		to and from structs.
 *
 * @author John Wickberg
 * @author Brad Samuels
 */
public class AcronymUserImpl extends PersistentBObject implements AcronymUser, UserInternal, QRMUser
{
    //
    // TODO: After the rollout of the SingleAcronym project, the
    //       userId and active columns will be dropped from the sbt_user table.
    //       This code, and it's invocations within this class, can be deleted.
    //
    //       Look for other "TODO" and "DELETABLE" comments in this class.
    //
    // ----- BEGIN DELETABLE CODE ----- TODO
	private String userId;
	private boolean active;
	static Field _userId;
	static Field _active;
    static
    {
        try
        {
			_userId = AcronymUserImpl.class.getDeclaredField("userId");
            _userId.setAccessible(true);
		    _active = AcronymUserImpl.class.getDeclaredField("active");
		    _active.setAccessible(true);
		}
		catch (NoSuchFieldException ex) { System.out.println(ex); }
    }
    protected boolean getActive() 
    {
        return editor.get(_active, active);
    }
    private String getUserId()
    {
        return (String) editor.get(_userId, userId);
    }
    private void setUserId(String aValue)
    {
        editor.set(_userId, aValue, userId);
    }
    protected void setActive(boolean newState)
    {
        editor.set(_active, newState, active);
    }
    // ----- END DELETABLE CODE -----
    
    
	/**
  	 * Reference to the quote risk management home.
	 */
	private QuoteRiskManagementProfileHome quoteRiskManagementHome;

	/**
	 * Name of database table.
	 */
	public static final String TABLE_NAME = "sbt_user";
    private String userName; // kept for compatible to version 1.1
	private String fullName;
	/**
	 * Key for user's firm, if applicable.
	 */
	private int firmKey;
	/**
	 * Key for user's membership data, if applicable.
	 */
	private int membershipKey;
	/**
	 * Type of membership, if applicable.
	 */
	private short userType;
	/**
	 * User's trading acronym, if applicable.
	 */
	private String acronym;
        private String exchangeAcronym; //the exchange the user associated with
	/**
	 * User's role.
	 */
	private char role;
	/**
	 * Assigned classes for the user.
	 */
	private Vector assignedClasses;
	/**
	 * Executing giveup firms.
	 */
	private Vector executingGiveupFirms;
	/**
	 * accounts for the user.
	 */
	private Vector accounts;
	/**
	 * Profiles for the user.
	 */
	private Vector profiles;

    /**
     * clearingAcronyms for the user.
     */
    private Vector clearingAcrs;
	/**
	 *  Indicator used to fill in the UserEnablementStruct's testClassesOnly flag.
	 */
	private boolean testClassesOnly;
	/**
	 * Time that the user was last modified.  If user record is received from
	 * membership, the membership LMOD time will be used.
	 */
	private long lastModifiedTime;
	/**
	 * Version number of the user.
	 */
	private int versionNumber;
	/**
	 * firmHome
	 */
	private FirmHome firmHome;
    /**
     * tradingSessionHome
     */
    private TradingSessionHome tradingSessionHome;

	private static final int DEFAULT_PROFILE_CLASS_KEY = com.cboe.idl.cmiConstants.ProductClass.DEFAULT_CLASS_KEY;

	/**
	 *  Determines whether or not the quote risk management profile for this user is enabled
	 */
	private boolean quoteRiskManagementEnabled; // QRM enabled boolean 0==false, 1==true

	/**
	 *  The collection of quote risk profiles. (vector elements are of type QuoteRiskManagementProfileImpl)
	 *  This collection includes the default profile as one of its elements.
	 */
	private Vector quoteRiskManagementProfiles;

    /**
     * Temporary hashmap of profiles we hold onto whenever a user is updated.
     * Gets cleared on every user updated.
     * We hold onto the profiles provided by users and then use the
     * sub-account field that was provided by the user instead of getting it
     * out of the DPM's profile.
     * Key = classKey , value = profile.
     */
    // private HashMap tempProfilesProvided = new HashMap(101);
	// JavaGrinder variables
    static Field _userName; //keep for compatible to version 1.1
	static Field _fullName;
	static Field _firmKey;
	static Field _membershipKey;
	static Field _userType;
	static Field _acronym;
    static Field _exchangeAcronym;
	static Field _role;
	static Field _assignedClasses;
   	static Field _executingGiveupFirms;
	static Field _accounts;
	static Field _profiles;
    static Field _clearingAcrs;
	static Field _testClassesOnly;
	static Field _lastModifiedTime;
	static Field _versionNumber;
	static Field _quoteRiskManagementEnabled;
	static Field _quoteRiskManagementProfiles;

	static Vector classDescriptor;

    /**
	* This static block will be regenerated if persistence is regenerated.
	*/
	static { /*NAME:fieldDefinition:*/
		try{
		    _userName = AcronymUserImpl.class.getDeclaredField("userName");
			_fullName = AcronymUserImpl.class.getDeclaredField("fullName");
		    _firmKey = AcronymUserImpl.class.getDeclaredField("firmKey");
		    _membershipKey = AcronymUserImpl.class.getDeclaredField("membershipKey");
		    _userType = AcronymUserImpl.class.getDeclaredField("userType");
		    _acronym = AcronymUserImpl.class.getDeclaredField("acronym");
		    _exchangeAcronym = AcronymUserImpl.class.getDeclaredField("exchangeAcronym");
		    _role = AcronymUserImpl.class.getDeclaredField("role");
		    _assignedClasses = AcronymUserImpl.class.getDeclaredField("assignedClasses");
		    _executingGiveupFirms = AcronymUserImpl.class.getDeclaredField("executingGiveupFirms");
		    _accounts = AcronymUserImpl.class.getDeclaredField("accounts");
		    _profiles = AcronymUserImpl.class.getDeclaredField("profiles");
		    _clearingAcrs = AcronymUserImpl.class.getDeclaredField("clearingAcrs");
		    _testClassesOnly = AcronymUserImpl.class.getDeclaredField("testClassesOnly");
		    _lastModifiedTime = AcronymUserImpl.class.getDeclaredField("lastModifiedTime");
		    _versionNumber = AcronymUserImpl.class.getDeclaredField("versionNumber");
			_quoteRiskManagementEnabled = AcronymUserImpl.class.getDeclaredField("quoteRiskManagementEnabled");
			_quoteRiskManagementProfiles = AcronymUserImpl.class.getDeclaredField("quoteRiskManagementProfiles");
		    _userName.setAccessible(true);
            _fullName.setAccessible(true);
		    _firmKey.setAccessible(true);
		    _membershipKey.setAccessible(true);
		    _userType.setAccessible(true);
		    _acronym.setAccessible(true);
		    _exchangeAcronym.setAccessible(true);
		    _role.setAccessible(true);
		    _assignedClasses.setAccessible(true);
		    _executingGiveupFirms.setAccessible(true);
		    _accounts.setAccessible(true);
		    _profiles.setAccessible(true);
		    _clearingAcrs.setAccessible(true);
		    _testClassesOnly.setAccessible(true);
		    _lastModifiedTime.setAccessible(true);
		    _versionNumber.setAccessible(true);
			_quoteRiskManagementEnabled.setAccessible(true);
			_quoteRiskManagementProfiles.setAccessible(true);
		}
		catch (NoSuchFieldException ex) { System.out.println(ex); }
	}

	/**
	 * Creates a default user instance.
	 *
	 */
	public AcronymUserImpl()
	{
		super();
                setUsing32bitId(true);
	}

    protected QuoteRiskManagementProfileHome getQuoteRiskManagementProfileHome()
    {
        if (quoteRiskManagementHome == null)
        {
            synchronized (this)
            {
                if (quoteRiskManagementHome == null)
                {
					try
					{
						BOHome aHome = HomeFactory.getInstance().findHome(QuoteRiskManagementProfileHome.HOME_NAME);
						quoteRiskManagementHome = (QuoteRiskManagementProfileHome)aHome;
					}
					catch (com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException ex)
					{
						throw new Error("Could not find QuoteRiskManagementProfileHome instance: " + ex);
					}
                }
            }
        }
        return quoteRiskManagementHome;
	}

	/**
	 * Checks version number to make sure update isn't done with stale data.
	 *
	 * @param updatedUserKey key of user being updated
	 * @param versionNumber version number before update
	 * @exception DataValidationException if version number is old
	 */
    protected void checkVersion(int updatedUserKey, int versionNumber) throws DataValidationException {
		// Membership adapter will update without first doing query, so ignore version check if key is 0
		if (updatedUserKey > 0 && versionNumber != getVersionNumber()) {
			throw ExceptionBuilder.dataValidationException("User version used in update is not current", 0);
		}
	}
	/**
	 * Sets all values for the user from the definition struct.
	 *
	 * @param userValues struct containing values for user
	 * @param membershipDefined if true, then the user info came from membership.
	 * @exception DataValidationException if validation checks fail
	 */
	public synchronized void fromStruct(SessionProfileUserDefinitionStruct userValues, boolean membershipDefined) throws DataValidationException
	{
        // force updates from this point on.
		try
		{
			insert();
		}
		catch (Exception e)
		{
			Log.exception(this,e);
			throw ExceptionBuilder.dataValidationException("Unable to save user to database: " + e, 0);
		}

		setExecutingGiveupFirms( userValues.executingGiveupFirms );

		// If a new non-DPM user is created, we must create a default profile.
		// (any membership-defined DPM account has had its default profile created && added via the updateBase() call, above.)
		//
        // update the base information
        updateBase(userValues, membershipDefined);
	}

	public boolean getQuoteRiskManagementEnabled()
	{
		boolean enabled = editor.get(_quoteRiskManagementEnabled, quoteRiskManagementEnabled);//.equals("T");
		return enabled;
	}

	public Vector getQuoteRiskManagementProfileVector()
	{
		Vector result = (Vector)editor.get(_quoteRiskManagementProfiles, quoteRiskManagementProfiles);
		if( result == null )
		{
			editor.set(_quoteRiskManagementProfiles, new Vector(), quoteRiskManagementProfiles);
			result = (Vector)editor.get(_quoteRiskManagementProfiles, quoteRiskManagementProfiles);
		}
		return result;
	}

	public QuoteRiskManagementProfile[] getQuoteRiskManagementProfiles()
	{
		Vector profiles = getQuoteRiskManagementProfileVector();
		QuoteRiskManagementProfile[] result = new QuoteRiskManagementProfile[profiles.size()];
		for (int i=0; i < result.length; i++)
		{
			result[i] = (QuoteRiskManagementProfile)profiles.elementAt(i);
		}
		return result;
	}

	/**
	 * Get the acronym through the object editor.
	 * @return String
	 */
	public String getAcronym()
	{
		return (String) editor.get(_acronym, acronym);
	}

	public String getExchangeAcronym()
	{
		return (String) editor.get(_exchangeAcronym, exchangeAcronym);
	}

	public boolean getTestClassesOnly() {
		return editor.get(_testClassesOnly, testClassesOnly);
	}
	/**
	 * Gets the assigned classes for the user
	 *
	 * @see User#getAssignedClasses
	 */
	public int[] getAssignedClasses() {
        Vector assignments = getAssignedVector();
		int[] result = new int[assignments.size()];
		Enumeration assignedClassesEnum = assignments.elements();
		AssignedClass assignment;
		for (int i = 0; assignedClassesEnum.hasMoreElements(); i++) {
			assignment = (AssignedClass) assignedClassesEnum.nextElement();
			result[i] = assignment.getAssignedClass();
		}
		return result;
	}

    protected MarketMakerClassAssignmentStruct[] getAssignedClassesV2()
    {
        Vector assignments = getAssignedVector();
        MarketMakerClassAssignmentStruct[] result = new MarketMakerClassAssignmentStruct[assignments.size()];
        Enumeration assignedClassesEnum = assignments.elements();
        AssignedClass assignment;
        MarketMakerClassAssignmentStruct mmClassAssignment;
        for(int i=0; assignedClassesEnum.hasMoreElements(); i++)
        {
            assignment = (AssignedClass) assignedClassesEnum.nextElement();
            mmClassAssignment = new MarketMakerClassAssignmentStruct();
            mmClassAssignment.classKey = assignment.getAssignedClass();
            mmClassAssignment.sessionName = assignment.getSessionName();
            mmClassAssignment.assignmentType = assignment.getAssignmentType();

            result[i] = mmClassAssignment;
        }

        return result;
    }
	/**
	 * Gets the accounts for the user
	 *
	 */
	public AccountStruct[] getAccounts() {
		Vector accountsVector = getAccountsVector();

		AccountStruct[] result = new AccountStruct[accountsVector.size()];
		Enumeration accountsEnum = accountsVector.elements();
		UserAccountRelation accountRelation;
		for (int i = 0; accountsEnum.hasMoreElements(); i++) {
			accountRelation = (UserAccountRelation) accountsEnum.nextElement();
			result[i] = accountRelation.toStruct();
		}

		return result;
	}
	/**
	 * Gets the user account
	 * @return UserAccountRelation  will be null if account is not found
	 *
	 */
	protected UserAccountRelation getUserAccount() {
		Vector accountsVector = getAccountsVector();
		Enumeration accountsEnum = accountsVector.elements();
		while (accountsEnum.hasMoreElements() ){
			UserAccountRelation account = (UserAccountRelation)accountsEnum.nextElement();
			if( account.isUserAccount() ){
				return account;
			}
		}
		return null;
	}
	/**
	 * Gets the accounts definitions for the user
	 *
	 */
	protected AccountDefinitionStruct[] getAccountDefinitions() {
		Vector accountsVector = getAccountsVector();
		AccountDefinitionStruct[] result = new AccountDefinitionStruct[accountsVector.size()];
		Enumeration accountsEnum = accountsVector.elements();
		UserAccountRelation accountRelation;
		for (int i = 0; accountsEnum.hasMoreElements(); i++) {
			accountRelation = (UserAccountRelation) accountsEnum.nextElement();
			result[i] = accountRelation.toDefinitionStruct();
		}

		return result;
	}
	/**
	 * Gets the collection of accounts
	 */
	protected Vector getAccountsVector() {
		Vector result = (Vector) editor.get(_accounts, accounts);

		if( result == null ){
			// create empty  accounts
			editor.set(_accounts, new Vector(), accounts);

			result = (Vector) editor.get(_accounts, accounts);
		}

		return result;
	}

	/**
	 * Gets the user account with the name and clearing firm specified
	 * @param accountName
	 * @param executingGiveupFirm
	 * @retuUserAccountRelation the account relation found or null if not found
	 */
    protected UserAccountRelation getAccount( String accountName, ExchangeFirmStruct executingGiveupFirm  )
    {
        if(executingGiveupFirm == null || executingGiveupFirm.firmNumber == null)
        {
            Log.alarm(this, "Error trying to get Account for a null executingGiveupFirm ");
            return null;
        }

        try
        {
            Firm firm = (Firm)getFirmHome().findFirmByNumber( executingGiveupFirm );

            return getAccount(accountName, firm.getFirmKey());
        }
        catch(Exception e)
        {
            Log.exception( this, e );
            Log.information(this, "Unable to getAccount for user:executingGiveupFirmNumber: " + accountName + ":" + executingGiveupFirm.firmNumber);

            return null;
        }
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
	/**
	 * Gets the user account with the name and firm key specified
	 * @param accountName
	 * @param firmKey
	 * @return UserAccountRelation the account relation found or null if not found
	 */
	public UserAccountRelation getAccount( String accountName, int firmKey ) {
		Vector accountsVector = getAccountsVector();
		Enumeration accountsEnum = accountsVector.elements();
		while (accountsEnum.hasMoreElements()) {
			UserAccountRelation accountRelation = (UserAccountRelation) accountsEnum.nextElement();
			if( accountRelation.getAccount().getAcronym().equals( accountName )
                && accountRelation.getAccount().getFirmKey() == firmKey)
            {
				return accountRelation;
			}
		}

        Log.information(this, "Unable to getAccount for user:firmKey: " + accountName + ":" + firmKey);
		return null;
	}
    /**
     * Gets the sessionProfiles sequence defined for classes for the user
     * excluding the default profiles for all classes.
     **/
    protected SessionProfileStruct[] getSessionProfilesByClass()
    {
        Vector profilesVector = getProfilesVector();
        //The result list is less than the profile list since the default profile and default session profiles will be filtered out
        SessionProfileStruct[] result = null;
        if ( profilesVector.size() == 0 )
        {
            Log.alarm(this, "DB error!  Default profile not found for user...");
            result = new SessionProfileStruct[0];
        }
        Enumeration profilesEnum = profilesVector.elements();
        Profile profile;
        ArrayList arrayList = new ArrayList();
        while (profilesEnum.hasMoreElements())
        {
            profile  = (Profile) profilesEnum.nextElement();
            if( profile.getClassKey() != DEFAULT_PROFILE_CLASS_KEY )
            {
                arrayList.add(profile.toStruct());
            }
        }
        result = new SessionProfileStruct[arrayList.size()];
        arrayList.toArray(result);
        return result;
    }

	/**
	 * Gets the collection of profiles
	 */
    protected Vector getProfilesVector() {
		Vector result = (Vector) editor.get(_profiles, profiles);

		if( result == null ){
			// create empty accounts
			editor.set(_profiles, new Vector(), profiles);

			result = (Vector) editor.get(_profiles, profiles);
		}

		return result;
	}


    /**
     * Gets the collection of clearingAcrs
     */
    protected Vector getClearingAcrsVector()
    {
        Vector result = (Vector) editor.get(_clearingAcrs, clearingAcrs);

        if (result == null)
        {
            // create empty clearingAcrs vector
            editor.set(_clearingAcrs, new Vector(), clearingAcrs);
            result = (Vector) editor.get(_clearingAcrs, clearingAcrs);
        }
        return result;
    }

	/**
	 * Gets the default sesssion profiles for the user
	 * @return Profile this will be null when the default has not yet been created
	 *
	 */
    public Profile getDefaultProfile() {
		Vector profilesVector = getProfilesVector();
		Enumeration profilesEnum = profilesVector.elements();
		while (profilesEnum.hasMoreElements() ){
            Profile profile = (Profile)profilesEnum.nextElement();
			if (profile.getClassKey() == DEFAULT_PROFILE_CLASS_KEY
                && com.cboe.idl.cmiConstants.SessionNameValues.ALL_SESSION_NAME.equals(profile.getSessionName()))
            {
				return profile;
			}
		}
        Log.information(this, "getDefaultProfile returning null, no default profile created yet at this moment for userId:" + loggableName());
        return null;
	}

    /**
     * Gets the default session profiles for the user
     * @return SessionProfileStruct[]
     *
     */
    protected SessionProfileStruct[] getDefaultSessionProfiles()
    {
        Vector profilesVector = getProfilesVector();
        Enumeration profilesEnum = profilesVector.elements();
        ArrayList arrayList = new ArrayList();
        while (profilesEnum.hasMoreElements())
        {
            Profile profile = (Profile) profilesEnum.nextElement();
            if ((profile.getClassKey() == DEFAULT_PROFILE_CLASS_KEY)
                && !com.cboe.idl.cmiConstants.SessionNameValues.ALL_SESSION_NAME.equals( profile.getSessionName()))
            {
                arrayList.add(profile.toStruct());
            }
        }
        SessionProfileStruct[] result = new SessionProfileStruct[arrayList.size()];
        arrayList.toArray(result);
        return result;
    }
    /**
     * Gets the default clearingAcronyms for the user by sessionName
     * @return SessionClearingAcronymStruct[]
     *
     */
    protected SessionClearingAcronymStruct[] getSessionClearingAcrs()
    {
        Vector clearingAcrsVector = getClearingAcrsVector();
        ArrayList clearingAcrsList = new ArrayList();
        for (int i = 0; i < clearingAcrsVector.size(); i++)
        {
            ClearingAcronym clearAcr = (ClearingAcronym) clearingAcrsVector.elementAt(i);
            if (getRole() == UserRoles.BROKER_DEALER || getRole() == UserRoles.CUSTOMER_BROKER_DEALER)
            {
                clearingAcrsList.add(clearAcr.toStruct());
            }
        }
        SessionClearingAcronymStruct[] result = new SessionClearingAcronymStruct[clearingAcrsList.size()];
        clearingAcrsList.toArray(result);
        return result;
    }
	/**
	 *  Return the subset of assigned classes which are assigned to a DPM account that is in the user's account list.
	 */
	protected UserAccountRelation[] getDpmAccounts()
	{
		Vector accountsVector = getAccountsVector();
		ArrayList dpmList = new ArrayList();

		// Search for DPM accounts
		//
		for (int i=0; i < accountsVector.size(); i++)
		{
			UserAccountRelation uac = (UserAccountRelation)accountsVector.elementAt(i);
			AcronymUserImpl account = uac.getAccount();
			if (account.getUserType() == UserTypes.DPM_ACCOUNT)
			{
				dpmList.add(uac);
			}
		}

		// Assemble the results.
		//
		UserAccountRelation[] result = new UserAccountRelation[dpmList.size()];
		dpmList.toArray(result);
		return result;
	}

	/**
	 * Gets the executing giveup firm numbers
	 *
	 */
    protected ExchangeFirmStruct[] getExecutingGiveupFirmNumbers() {
		ExchangeFirmStruct[] firmNumbers = null;
		try{
			int[] executingFirmKeys = getExecutingGiveupFirmKeys();
			firmNumbers = new ExchangeFirmStruct[ executingFirmKeys.length ];
			for (int i = 0; i < executingFirmKeys.length; ++i) {
				Firm firm = getFirmHome().findFirmByKey( executingFirmKeys[i] );
				firmNumbers[i] = new ExchangeFirmStruct(firm.getExchangeAcr(), firm.getFirmNumber());
			}
		} catch( Exception e ){
			Log.exception( this, e );
		}
		return firmNumbers;
	}

	/**
	 * Gets the executing giveup firms
	 *
	 * @see User#getExecutingGiveupFirms
	 */
	protected int[] getExecutingGiveupFirmKeys() {
		Vector relations = getExecutingGiveupFirmsVector();
		int[] result = new int[relations.size()];
		Enumeration firmRelations = relations.elements();
		UserFirmRelation relation;
		for (int i = 0; firmRelations.hasMoreElements(); i++) {
			relation = (UserFirmRelation) firmRelations.nextElement();
			result[i] = relation.getFirmKey();
		}
		return result;
	}

	/**
	 * Gets the collection of assigned classes.
	 */
	protected Vector getAssignedVector() {
		Vector result = (Vector) editor.get(_assignedClasses, assignedClasses);

		if( result == null ){
			// create empty assigned classes
			editor.set(_assignedClasses, new Vector(), assignedClasses);

			result = (Vector) editor.get(_assignedClasses, assignedClasses);
		}

		return result;
	}
	/**
	 * Gets the collection of executing giveup firms.
	 */
	protected Vector getExecutingGiveupFirmsVector() {
		Vector result = (Vector) editor.get(_executingGiveupFirms, executingGiveupFirms);

		if( result == null ){
			// create empty executing given firms
			editor.set(_executingGiveupFirms, new Vector(), executingGiveupFirms);

			result = (Vector) editor.get(_executingGiveupFirms, executingGiveupFirms);
		}

		return result;
	}

	/**
	 * Get the firm key through the object editor.
	 * @return user's firm key
	 */
	protected int getFirmKey()
	{
		return editor.get(_firmKey, firmKey);
	}
	/**
	 * Gets the last modified time of the user.
	 */
	protected long getLastModifiedTime()
	{
		return editor.get(_lastModifiedTime, lastModifiedTime);
	}
	/**
	 * Get the member key through the object editor.
	 * @return user's membership key
	 */
	protected int getMembershipKey()
	{
		return editor.get(_membershipKey, membershipKey);
	}
	/**
	 * Get the role through the object editor.
	 * @return user role
	 */
	public char getRole()
	{
		return editor.get(_role, role);
	}
	/**
	 * @see User#getUserKey
	 */
	public int getAcronymUserKey()
	{
		return getObjectIdentifierAsInt();
	}
        /**
         * @see User#getUserName
	 */
	public String getUserName()
	{
		return (String) editor.get(_userName, userName);
	}

    public String getFullName()
    {
        return (String) editor.get(_fullName, fullName);
    }

	/**
	 * Get the user type through the object editor.
	 * @return user's type
	 */
	public short getUserType()
	{
		return editor.get(_userType, userType);
	}
	/**
	 * Gets current version number.
	 */
	public int getVersionNumber()
	{
		return editor.get(_versionNumber, versionNumber);
	}
	/**
	 * Describe how this class relates to the relational database.
	 */
	public void initDescriptor()
	{
		synchronized (AcronymUserImpl.class)
		{
			if (classDescriptor != null)
				return;
			Vector tempDescriptor = getSuperDescriptor();
            // ----- BEGIN DELETABLE CODE ----- TODO
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("userId", _userId));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("active", _active));
            // ----- END DELETABLE CODE -----
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("full_name", _fullName));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("user_name", _userName));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("firm_key", _firmKey));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("mbr_key", _membershipKey));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("user_type", _userType));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("acr", _acronym));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("exchange_acronym", _exchangeAcronym));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("role", _role));
			tempDescriptor.addElement(AttributeDefinition.getCollectionRelation(AssignedClass.class, _assignedClasses));
			tempDescriptor.addElement(AttributeDefinition.getCollectionRelation(UserFirmRelation.class, _executingGiveupFirms));
			tempDescriptor.addElement(AttributeDefinition.getCollectionRelation(UserAccountRelation.class, _accounts));
			tempDescriptor.addElement(AttributeDefinition.getCollectionRelation(Profile.class, _profiles));
            tempDescriptor.addElement(AttributeDefinition.getCollectionRelation(ClearingAcronym.class, _clearingAcrs));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("testClassesOnly", _testClassesOnly));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("lastModifiedTime", _lastModifiedTime));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("versionNumber", _versionNumber));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("quoteRiskManagementEnabled", _quoteRiskManagementEnabled));
			tempDescriptor.addElement(AttributeDefinition.getCollectionRelation(QuoteRiskManagementProfileImpl.class, _quoteRiskManagementProfiles));
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
	 * Updates version number as last step of saving object.
	 */
	public void save() throws PersistenceException {
		// update version number directly, transaction is in progress by during this call and
		// transaction log has already been applied to this object.
		versionNumber += 1;
		super.save();
	}
	public void setQuoteRiskManagementEnabled(boolean aValue)
	{
		editor.set(_quoteRiskManagementEnabled, aValue/*?"T":"F"*/, quoteRiskManagementEnabled);
	}

	/**
	 * Add a profile to the list: update it if one with the given class key already exists.
	 */
	public void addQuoteRiskManagementProfile(QuoteRiskManagementProfileStruct newProfileStruct)
	{
		QuoteRiskManagementProfile profile = getQuoteRiskManagementProfileForClass(newProfileStruct.classKey);
		if (profile != null)
		{
			Log.debug(this, "QRM Profile for class key " + newProfileStruct.classKey + " for user " + getAcronym() + " already exists.");
			profile.fromStruct(newProfileStruct);
			profile.createQRMLogMessage("Updated");
		}
		else
		{
			Log.debug(this, "Creating new QRM profile for class key " + newProfileStruct.classKey + " for user " + getAcronym());
			QuoteRiskManagementProfile newProfile = getQuoteRiskManagementProfileHome().update(this, newProfileStruct);
			Vector currentProfiles = getQuoteRiskManagementProfileVector();
			currentProfiles.addElement(newProfile);
			editor.set(_quoteRiskManagementProfiles, currentProfiles, quoteRiskManagementProfiles);
			newProfile.createQRMLogMessage("Created");
			Log.debug(this, "Created new QRM profile for class key " + newProfileStruct.classKey + " for user " + getAcronym());
		}

	}

	/**
	 *  This method will remove all old profile list elements and replace them with new ones.
	 */
	public void setQuoteRiskManagementProfileVector(Vector newProfiles) throws TransactionFailedException
	{
		Vector currentProfiles = getQuoteRiskManagementProfileVector();
		for (int i=0; i < currentProfiles.size(); ++i)
		{
			QuoteRiskManagementProfile profile = (QuoteRiskManagementProfile)currentProfiles.elementAt(i);
			int j;
			for (j=0; j < newProfiles.size(); ++j)
			{
				if (newProfiles.elementAt(j) == profile) // is the new object the same instance as an old one?
				{
					break; // don't mark for delete
				}
			}
			if (j == newProfiles.size())
			{
					getQuoteRiskManagementProfileHome().removeProfile(profile);
			}
		}
		editor.set(_quoteRiskManagementProfiles, newProfiles, quoteRiskManagementProfiles);
	}

	/**
	 *  Returns null if classKey is not found in the profile list.
	 */
	public QuoteRiskManagementProfile getQuoteRiskManagementProfileForClass(int classKey)
	{
		Vector currentProfiles = getQuoteRiskManagementProfileVector();
		for (int i=0; i < currentProfiles.size(); ++i)
		{
			QuoteRiskManagementProfile profile = (QuoteRiskManagementProfile)currentProfiles.elementAt(i);
			if (profile.getClassKey() == classKey)
			{
				return profile;
			}
		}
		return null;
	}

	/**
	 *  Remove the profile for the given class key if it exists.
	 */
	public void removeQuoteRiskManagementProfile(int classKey) throws TransactionFailedException
	{
		Vector currentProfiles = getQuoteRiskManagementProfileVector();
		for (int i=0; i < currentProfiles.size(); ++i)
		{
			QuoteRiskManagementProfile profile = (QuoteRiskManagementProfile)currentProfiles.elementAt(i);
			if (profile.getClassKey() == classKey)
			{
				getQuoteRiskManagementProfileHome().removeProfile(profile);
				currentProfiles.removeElementAt(i);
				profile.createQRMLogMessage("Removed");
				editor.set(_quoteRiskManagementProfiles, currentProfiles, quoteRiskManagementProfiles);
				break;
			}
		}
	}

	/**
	 *  This method will update the profile list using the information supplied in the struct array being passed in.
	 *  Database elements will be deleted/added/updated as needed to reflect the structs passed in.
	 */
	public void updateQuoteRiskManagementProfiles(QuoteRiskManagementProfileStruct[] profileStructs) throws TransactionFailedException
	{
		Vector currentProfiles = getQuoteRiskManagementProfileVector();
		boolean[] updated = new boolean[profileStructs.length];  // knowledge of which structs were updated in the first for() loop.

		// Update or delete the current profile list.
		//
		for (int i=0; i < currentProfiles.size(); ++i)
		{
			QuoteRiskManagementProfile profile = (QuoteRiskManagementProfile)currentProfiles.elementAt(i);
			int classKey = profile.getClassKey();
			boolean found = false;
			for (int j=0; j < profileStructs.length; ++j)
			{
				if (classKey == profileStructs[i].classKey)
				{
					found = true;
					updated[j] = true;
					profile.fromStruct(profileStructs[i]);
					break;
				}
			}
			if (!found)
			{
				getQuoteRiskManagementProfileHome().removeProfile(profile);
				currentProfiles.removeElementAt(i);
				--i;
			}
		}

		// Create new profile obejcts for any structs that didn't update an existing profile object.
		//
		for (int i=0; i < updated.length; ++i)
		{
			if (!updated[i])
			{
				QuoteRiskManagementProfile newProfile = getQuoteRiskManagementProfileHome().update(this, profileStructs[i]);
				currentProfiles.addElement(newProfile);
			}
		}

		editor.set(_quoteRiskManagementProfiles, currentProfiles, quoteRiskManagementProfiles);
	}

	/**
	 * Set the acronym through an object editor.
	 * @param aValue The acronym.
	 */
	public void setAcronym(String aValue)
	{
		editor.set(_acronym, aValue, acronym);
	}

	public void setExchangeAcronym(String aValue)
	{
		editor.set(_exchangeAcronym, aValue, exchangeAcronym);
	}

	public void setTestClassesOnly(boolean newState) {
		editor.set(_testClassesOnly, newState, testClassesOnly);
	}
	/**
	 * Sets the assigned classes for the user.
	 *
	 * @see User#setAssignedClasses
	 */
	protected void setAssignedClasses(MarketMakerClassAssignmentStruct[] assignedClasses) throws DataValidationException {
		Vector currentAssignments = getAssignedVector();
        addNewClassAssignments(currentAssignments, assignedClasses);
        removeOldClassAssignments(currentAssignments, assignedClasses);
    }

    protected void addNewClassAssignments(Vector assignments, MarketMakerClassAssignmentStruct[] toBeAssignedClasses) throws DataValidationException
    {
        boolean addClass, updateClass;
        MarketMakerClassAssignmentStruct toBeAssignedClass;
        for(int i = 0; i < toBeAssignedClasses.length; i++)
        {
            addClass = true;
            updateClass = true;
            toBeAssignedClass = toBeAssignedClasses[i];
/*
            if(! toBeAssignedClass.sessionName.equals(SessionNameValues.ALL_SESSION_NAME))
            {
                Log.alarm(this, "Session for class assignment is invalid userId/class/sessionName:" + loggableName() + "/" + toBeAssignedClass.classKey + "/" + toBeAssignedClass.sessionName + " It must be " + SessionNameValues.ALL_SESSION_NAME);
                throw ExceptionBuilder.dataValidationException("Invalid session name for class assignment :" + toBeAssignedClass.sessionName, DataValidationCodes.INVALID_SESSION);
            }
*/            
            AssignedClass currentAssignment=null;
            for(int j = 0; addClass && updateClass && j < assignments.size(); j++)
            {
                currentAssignment = (AssignedClass) assignments.elementAt(j);
                if(toBeAssignedClass.classKey == currentAssignment.getAssignedClass() &&
                        toBeAssignedClass.sessionName.equals(currentAssignment.getSessionName()))
                {
                    addClass = false;
                    if(toBeAssignedClass.assignmentType == currentAssignment.getAssignmentType())
                    {
                        updateClass = false;
                    }
                }
            }
            if(addClass)
            {
                AssignedClass newAssignment = new AssignedClass(this,toBeAssignedClass.classKey, toBeAssignedClass.assignmentType, toBeAssignedClass.sessionName);
                getBOHome().addToContainer(newAssignment);
                assignments.addElement(newAssignment);
                AssignedClassHistory histElement = new AssignedClassHistory(AssignedClassHistory.ADD, getMembershipKey(), getAcronym(), getExchangeAcronym(),
                        toBeAssignedClass.classKey, toBeAssignedClass.assignmentType, toBeAssignedClass.sessionName);
                getBOHome().addToContainer(histElement);
                if(Log.isDebugOn())
                {
                    Log.debug(this,"Added class assignment for User - assignmentType/classKey/SessionName: " + loggableName() + " - " + toBeAssignedClass.assignmentType + "/"
                            + toBeAssignedClass.classKey + "/" + toBeAssignedClass.sessionName);
                }
            }
            else if(updateClass)
            {
                if(currentAssignment != null)
                {
                    currentAssignment.setAssignmentType(toBeAssignedClass.assignmentType);
                    AssignedClassHistory histElement = new AssignedClassHistory(AssignedClassHistory.UPDATE, getMembershipKey(), getAcronym(), getExchangeAcronym(),
                            toBeAssignedClass.classKey, toBeAssignedClass.assignmentType, toBeAssignedClass.sessionName);
                    getBOHome().addToContainer(histElement);
                    if(Log.isDebugOn())
                    {
                        Log.debug(this,"Updated class assignment for User - assignmentType/classKey/SessionName: " + loggableName() + " - " + toBeAssignedClass.assignmentType + "/"
                                + toBeAssignedClass.classKey + "/" + toBeAssignedClass.sessionName);
                    }
                }
                else
                {
                    Log.alarm(this, "Could not update the current assignment. This should never happen.");
                }
            }
        }
    }



    protected void removeOldClassAssignments(Vector assignments, MarketMakerClassAssignmentStruct[] toBeAssignedClasses) {
		AssignedClass assignment;
		boolean removeClass;
		for (int i=0; i < assignments.size(); i++)
		{
			assignment = (AssignedClass) assignments.elementAt(i);
			removeClass = true;
			for (int j = 0; removeClass && j < toBeAssignedClasses.length; j++) {
				if (assignment.getAssignedClass() == toBeAssignedClasses[j].classKey &&
                        assignment.getSessionName().equals(toBeAssignedClasses[j].sessionName)) {
					removeClass = false;
				}
			}
			if (removeClass) {
				try {
                    int productKey = assignment.getAssignedClass();
                    short assignType = assignment.getAssignmentType();
                    String session_name = assignment.getSessionName();
					assignment.markForDelete();
					assignments.removeElementAt(i--);
					AssignedClassHistory histElement = new AssignedClassHistory(AssignedClassHistory.REMOVE, getMembershipKey(), getAcronym(), getExchangeAcronym(), productKey,
                            assignType, session_name);
					getBOHome().addToContainer(histElement);
                    if(Log.isDebugOn())
                    {
                        Log.debug(this,"Removed class assignment for User - assignmentType/classKey/SessionName: " + loggableName() + " - " + assignType + "/" + productKey + "/" + session_name);
                    }
				}
				catch (Exception e) {
					Log.exception(this, "Unable to remove class assignment: " + assignment, e);
				}
			}
		}
	}
	/**
	 * Sets the executing giveup firms
	 *
	 * @see User#setExecutingGiveupFirms
	 */
    protected void setExecutingGiveupFirms(int[] executingGiveupFirms) {
		Vector firms = getExecutingGiveupFirmsVector();

		// find new executing giveup firms
		int[] currentFirms = getExecutingGiveupFirmKeys();
		boolean addFirm;
		for (int i = 0; i < executingGiveupFirms.length; i++) {
			addFirm = true;
			for (int j = 0; addFirm && j < currentFirms.length; j++) {
				if (executingGiveupFirms[i] == currentFirms[j]) {
					addFirm = false;
				}
			}
			if (addFirm) {
				UserFirmRelation newRelation = new UserFirmRelation(this, executingGiveupFirms[i]);
				getBOHome().addToContainer(newRelation);
				firms.addElement( newRelation );
			}
		}


		// remove old class assignments
		UserFirmRelation relation;
		boolean removeFirm;
		for (int i=0; i < firms.size(); i++)
		{
			relation = (UserFirmRelation)firms.elementAt(i);
			removeFirm = true;
			for (int j = 0; removeFirm && j < executingGiveupFirms.length; j++) {
				if (relation.getFirmKey() == executingGiveupFirms[j]) {
					removeFirm = false;
				}
			}
			if (removeFirm) {
				try {
					relation.markForDelete();
					firms.removeElementAt(i--);
				}
				catch (Exception e) {
					Log.exception(this, "Unable to remove independent firm relationship : " + relation, e);
				}
			}
		}
	}
	/**
	 * Sets the accounts for the user.  This method will also check to make sure that at least
     * one of the accounts is the user's account.  If it does not exist it will be created
	 * once created it will not be deleted.
	 *
	 * @see User#setAccounts
	 */
    protected void setAccounts(AccountDefinitionStruct[] accounts,
	    boolean membershipDefined) throws DataValidationException {

		Vector accountsVector = getAccountsVector();

		// find new class assignments
		AccountDefinitionStruct[] currentAccounts = getAccountDefinitions();
		boolean addAccount;

		for (int i = 0; i < accounts.length; i++) {
            // skip inactive accounts - not used for trading so don't add them
            if (!accounts[i].isActive) {
                continue;
            }
			addAccount = true;
			for (int j = 0; addAccount && j < currentAccounts.length; j++) {
				if (accounts[i].account.acronym.equals(currentAccounts[j].account.acronym)
                    && accounts[i].account.exchange.equals(currentAccounts[j].account.exchange)) {
					addAccount = false;
				}
			}
			if ( addAccount ) {
				UserAccountRelation newAccount = new UserAccountRelation( this );
				getBOHome().addToContainer( newAccount );
				newAccount.fromStruct( accounts[i] );
				accountsVector.addElement( newAccount );
			}
		}


		// remove old accounts, update existing accounts, and handle the user account
		// I am doing updates here which are actually updating the profiles I just added above.
 		//This is because I convieniently have the profile object here and would have do an extra
		//seach through the vector to get it elsewhere.
		Enumeration accountsEnum = accountsVector.elements();
		UserAccountRelation accountRelation, existingAccountRelation = null;
		AccountDefinitionStruct accountDefStruct=null;
		for (int i=0; i < accountsVector.size(); i++)
		{
			accountRelation = (UserAccountRelation)accountsVector.elementAt(i);
		    boolean removeAccount = true;

            // remove account if no matching active account is given
			for (int j = 0; removeAccount && j < accounts.length; j++)
			{
				if (accounts[j].isActive)
				{
					AcronymUserImpl currAcct = accountRelation.getAccount();
					if (currAcct.getAcronym().equals(accounts[j].account.acronym)
                        && currAcct.getExchangeAcronym().equals(accounts[j].account.exchange) )
					{
						removeAccount = false;
						existingAccountRelation = accountRelation;
						accountDefStruct = accounts[j];
					}
				}
			}
			//Only remove if it is not the user account and is not a joint account
			// OR if membership defined, force remove
			if (removeAccount && !accountRelation.isUserAccount() ) {
				try {
				    if(membershipDefined)
				    {
				        // if this is coming from the membership adapter remove profiles for the account.
				        removeAllUserProfilesForAccount(accountRelation.getAccount().getAcronym(), accountRelation.getAccount().getExchangeAcronym());
				    }
                    boolean reallyDelete = membershipDefined;
                    String reasonForNotDeleting = "{no reason determined}"; // this default string should never be able to get to the Log.alarm() 
                    if (!reallyDelete)
                    {
                       // If this is not a membership update, then DO NOT delete any JOINT_ACCOUNT.
                       //
                       if (accountRelation.getAccount().getUserType() == UserTypes.JOINT_ACCOUNT)
                       {
                           reasonForNotDeleting = "Unable to remove account relation for  " + this + " since it is a JOINT_ACCOUNT: " + accountRelation.getAccount();  
                       }
                       else
                       {
                           reallyDelete = !hasProfile( accountRelation.getAccount().getAcronym(), accountRelation.getAccount().getExchangeAcronym());
                           if (!reallyDelete)
                           {
                               reasonForNotDeleting = "Unable to remove account relation for " + this + " since account is still in use : " + accountRelation.getAccount();
                           }
                       }
                    }
                        
                    if (reallyDelete)
                    {
						accountRelation.markForDelete();
						accountsVector.removeElementAt(i--);
					}
					else 
                    {
                        Log.alarm(this, reasonForNotDeleting);
					}
				}
				catch (Exception e) {
					Log.exception(this, "Unable to remove account relation: " + accountRelation, e);
				}
			}
			//If we were not suppose to remove it, then we should update it
			else if( !removeAccount ){
				//Update the existing accountRelation
				existingAccountRelation.fromStruct( accountDefStruct );
			}
			//If we get here it means were were supposed to remove it but it is the user account
			//We do not remove the user account, it is just not sent from membership
			//else {
				//Do nothing
			//}
		}

//		editor.set(_accounts, accountsVector, this.accounts);
	}

    /**
     * Add the default session profiles and class associated profiles, this method has to not remove the generic default
     * when removing the rest since the default will not be in the list passed in but it will be in our vector.
     * @param defaultSessionProfiles - only contains those default profiles defined for sessions
     * @param profiles -only contains those profiles defined for specified classKeys
     */
    protected void addProfilesByClassBySession(SessionProfileStruct[] defaultSessionProfiles, SessionProfileStruct[] profiles, boolean membershipDefined) throws DataValidationException
    {

        Vector profilesVector = getProfilesVector();

        // find new profiles
        SessionProfileStruct[] currentDefaultSessionProfiles = getDefaultSessionProfiles();
        SessionProfileStruct[] currentProfiles = getSessionProfilesByClass();
        boolean addProfile;
        for (int i = 0; i < defaultSessionProfiles.length + profiles.length; i++)
        {
            addProfile = true;
            if (i < defaultSessionProfiles.length)
            {
                // validate default session profile
                if ((defaultSessionProfiles[i].classKey != DEFAULT_PROFILE_CLASS_KEY) ||
                    (defaultSessionProfiles[i].classKey == DEFAULT_PROFILE_CLASS_KEY && defaultSessionProfiles[i].sessionName.equals(SessionNameValues.ALL_SESSION_NAME)))
                {
                    String msg = "In valid defaultSession Profile,  classKey: " + defaultSessionProfiles[i].classKey
                            + ",sessionName:" + defaultSessionProfiles[i].sessionName;
                    Log.alarm (this, msg );
                    throw ExceptionBuilder.dataValidationException(msg, 0);
                }
                // check if it is in the current default sesion profiles list
                for (int j = 0; addProfile && j < currentDefaultSessionProfiles.length; j++)
                {
                    if ((defaultSessionProfiles[i].classKey == currentDefaultSessionProfiles[j].classKey) && (defaultSessionProfiles[i].sessionName.equals(currentDefaultSessionProfiles[j].sessionName)))
                    {
                        addProfile = false;
                        break;
                    }
                }
            }
            else
            {
                // validate sepcified profile for class and session
                int index = i - defaultSessionProfiles.length;
                if (profiles[index].classKey == DEFAULT_PROFILE_CLASS_KEY)
                {
                    String msg = "In valid session Profile defined for a specific class,  classKey: " + profiles[index].classKey
                            + ", sessionName:" + profiles[index].sessionName;
                    Log.alarm(this, msg);
                    throw ExceptionBuilder.dataValidationException(msg, 0);
                }
                // check if it in the current profile for class list
                for (int j = 0; addProfile && j < currentProfiles.length; j++)
                {
                    if ((profiles[index].classKey == currentProfiles[j].classKey) && (profiles[index].sessionName.equals(currentProfiles[j].sessionName)))
                    {
                        addProfile = false;
                        break;
                    }
                }
            }
            // if it is a new profile,  create it and add it into the vector profile list.
            if (addProfile)
            {
                    Profile newProfile = new Profile(this);
                    getBOHome().addToContainer(newProfile);
                    if ( i < defaultSessionProfiles.length)
                    {
                        newProfile.fromStruct(defaultSessionProfiles[i]);
                    }
                    else
                    {
                        newProfile.fromStruct(profiles[i- defaultSessionProfiles.length]);
                    }
                    newProfile.setMembershipDefined(membershipDefined);
                    profilesVector.addElement(newProfile);
            }
        }

        // remove old profiles and update existing profile.
        // I am doing updates here which are actually updating the profiles I just added above.
        //This is because I convieniently have the profile object here and would have do an extra
        //seach through the vector to get it elsewhere.

        Profile profile, existingProfile = null;
        SessionProfileStruct profileStruct = null;
        boolean removeProfile, isDefault;
        for (int i = 0; i < profilesVector.size(); i++)
        {
            profile = (Profile) profilesVector.elementAt(i);
            removeProfile = true;
            isDefault = false;

            //See if we have the default profiles
            if ((profile.getClassKey() == DEFAULT_PROFILE_CLASS_KEY) &&
                 SessionNameValues.ALL_SESSION_NAME.equals(profile.getSessionName()) )

            {
                removeProfile = false;
                isDefault = true;
            }
            else
            {
                for (int j = 0; removeProfile && j < defaultSessionProfiles.length + profiles.length; j++)
                {
                    profileStruct = null;
                    if (j < defaultSessionProfiles.length)
                    {
                        if (profile.getClassKey() == defaultSessionProfiles[j].classKey
                                && profile.getSessionName().equals(defaultSessionProfiles[j].sessionName))
                        {
                            removeProfile = false;
                            existingProfile = profile;
                            profileStruct = defaultSessionProfiles[j];
                            break;
                        }
                    }
                    else
                    {
                        int index = j- defaultSessionProfiles.length;
                        if (profile.getClassKey() == profiles[index].classKey
                                && profile.getSessionName().equals(profiles[index].sessionName))
                        {
                            removeProfile = false;
                            existingProfile = profile;
                            profileStruct = profiles[index];
                            break;
                        }
                    }
                }
            }

            if (removeProfile)
            {
                // Don't remove manually defined profiles during a membership update
                if (!membershipDefined || profile.isMembershipDefined())
                {
                    try
                    {
                        profile.markForDelete();
                        profilesVector.removeElementAt(i--);
                    }
                    catch (Exception e)
                    {
                        Log.exception(this, "Unable to remove profile : " + profile, e);
                    }
                }
            }
            else if (!isDefault)
            {
                // Update the existing profile if it is not the default, default is handled on call to addDefault
                existingProfile.fromStruct(profileStruct);
                // Change status if membership updates an manually created profile
                if (membershipDefined && !existingProfile.isMembershipDefined())
                {
                    existingProfile.setMembershipDefined(true);
                }
            }
        }

    }

    /**
     * Add the default session associated clearing acronym for the user
     * @param newClearingAcrs
     */
    protected void addClearingAcrs(SessionClearingAcronymStruct[] newClearingAcrs)
            throws DataValidationException
    {

        Vector clearingAcrsVector = getClearingAcrsVector();

        // find new clearingAcrs

        SessionClearingAcronymStruct[] currentSessionClearingAcronymStructs = getSessionClearingAcrs();
        boolean addClearingAcr;
        for (int i = 0; i < newClearingAcrs.length; i++)
        {
            addClearingAcr = true;
            for (int j = 0; addClearingAcr && j < currentSessionClearingAcronymStructs.length; j++)
            {
                if (newClearingAcrs[i].sessionName.equals(currentSessionClearingAcronymStructs[j].sessionName))
                {
                    addClearingAcr = false;
                    break;
                }
            }
            if (addClearingAcr)
            {
                ClearingAcronym newClearingAcr = new ClearingAcronym(this);
                getBOHome().addToContainer(newClearingAcr);
                newClearingAcr.fromStruct(newClearingAcrs[i]);
                clearingAcrsVector.addElement(newClearingAcr);
            }
        }
        // remove old clearingAcrs and update existing clearingAcrs
        ClearingAcronym clearingAcr, existingClearingAcr = null;
        SessionClearingAcronymStruct clearingAcronymStruct = null;
        boolean removeClearingAcr = true;
        for (int i = 0; i <  clearingAcrsVector.size(); i++)
        {
            clearingAcr = (ClearingAcronym) clearingAcrsVector.elementAt(i);
            removeClearingAcr = true;
            //see if in the update list
            {
                for (int j = 0; removeClearingAcr && j < newClearingAcrs.length; j++)
                {
                    if (clearingAcr.getSessionName().equals(newClearingAcrs[j].sessionName))
                    {
                        removeClearingAcr = false;
                        existingClearingAcr = clearingAcr;
                        clearingAcronymStruct  = newClearingAcrs[j];
                        break;
                    }
                }
            }

            if (removeClearingAcr)
            {
                // remove the old entry
                try
                {
                    clearingAcr.markForDelete();
                    clearingAcrsVector.removeElementAt(i--);
                }
                catch (Exception e)
                {
                    Log.exception(this, "Unable to remove clearingAcr : " + clearingAcr, e);
                }
            }
            else
            {
                // update the exsiting/new entry
                existingClearingAcr.fromStruct(clearingAcronymStruct);
                if ( Log.isDebugOn())
                {
                    Log.debug(this, "ClearingAcronym Updated: " + existingClearingAcr);
                }
            }
        }
    }
	/**
	 * Sets the executing giveup firms
	 *
	 * @see User# setExecutingGiveupFirms
	 */
     void setExecutingGiveupFirms(ExchangeFirmStruct[] executingGiveupFirms) throws DataValidationException {
        int idx=0;
		try{
			int[] firmKeys = new int[ executingGiveupFirms.length ];
			for (; idx < executingGiveupFirms.length; idx++) {
				Firm firm = getFirmHome().findFirmByNumber( executingGiveupFirms[idx] );
				firmKeys[idx]=firm.getFirmKey();
			}
			setExecutingGiveupFirms( firmKeys );
		} 
        catch( NotFoundException e )
        {
			final String msg = "Executing firm " + executingGiveupFirms[idx].exchange+":"+executingGiveupFirms[idx].firmNumber 
                + " not found for update of user " + getExchangeAcronym()+":"+getAcronym();
			Log.exception( this, msg, e );
			throw ExceptionBuilder.dataValidationException(msg, DataValidationCodes.INVALID_USER);
		}
        catch(Exception e )
        {
			final String msg = "Error " + e + " finding Executing firm " + executingGiveupFirms[idx].exchange+":"+executingGiveupFirms[idx].firmNumber 
                + " for update of user " + getExchangeAcronym()+":"+getAcronym();
			Log.exception( this, msg, e );
			throw ExceptionBuilder.dataValidationException(msg, DataValidationCodes.INVALID_USER);
		}
	}

	/**
	 * add the default profile to the users list of profiles
	 * @param aNewDefaultProfile  the new default profile
	 */
	 void addDefaultProfile(SessionProfileStruct aNewDefaultProfile) throws DataValidationException
	{
		//Check the class key

        if (aNewDefaultProfile.classKey != DEFAULT_PROFILE_CLASS_KEY)
        {
            Log.alarm (this, "Default profile class key is incorrect");
            throw ExceptionBuilder.dataValidationException("Default profile class key is incorrect", 0);
        }
        if (! SessionNameValues.ALL_SESSION_NAME.equals(aNewDefaultProfile.sessionName))
        {
            Log.alarm (this, "Default profile's sessionName is incorrect: " + aNewDefaultProfile.sessionName);
            throw ExceptionBuilder.dataValidationException("Default profile sessionName is incorrect: " + aNewDefaultProfile.sessionName, 0);
        }
		//see if we already have a default
		Profile existingDefault = getDefaultProfile();
		if( existingDefault == null ){
			Profile newProfile = new Profile( this );
			getBOHome().addToContainer( newProfile );

			newProfile.fromStruct( aNewDefaultProfile );

			Vector profilesVector = getProfilesVector();
			profilesVector.addElement( newProfile );
			Log.information("Add default profile.  There are now " + getProfilesVector().size() + " elements. for userId:" + loggableName());
		}
        else if (getActive()) {
            existingDefault.fromStruct( aNewDefaultProfile );
        }
	}

	/**
	 * add the user account to the list of accounts only if it does not exist.
	 */
	 void handleUserAccount() throws DataValidationException
	{
		UserAccountRelation userAccount = getUserAccount();
		if( userAccount == null ){
			userAccount = new UserAccountRelation(this);
			getBOHome().addToContainer( userAccount );

			userAccount.setAccount(this); // the account name is the user's
			userAccount.setIsActive( true ); // the account is active by default
			userAccount.setLastModifiedTime( getLastModifiedTime() ); // the accounts last modified time will be the users

			getAccountsVector().addElement( userAccount );
		}
		//Otherwise just update the timestamp field
		else {
			//userAccount.setIsActive( getActive() );
			userAccount.setLastModifiedTime( getLastModifiedTime() );
		}

	}
	/**
	 * Check to see if the user has at least one profile for the account specified
	 */
	 boolean hasProfile( String accountName, String exchange )
	{
		Vector profiles = getProfilesVector();

		Enumeration profilesEnum = profiles.elements();
		Profile profile;
		boolean found=false;
		while (profilesEnum.hasMoreElements()) {
			profile  = (Profile) profilesEnum.nextElement();
			if( profile.getUserAccount().getAccount().getAcronym().equals( accountName )
            && profile.getUserAccount().getAccount().getExchangeAcronym().equals(exchange)){
				found = true;
				break;
			}
		}
		return found;

	}

	/**
	 * Remove all profiles for user account
	 */
	// NOTE: Be sure that this method is called from within a transaction!
     void removeAllUserProfilesForAccount(String accountName, String exchange) throws PersistenceException
    {
        Vector profiles = getProfilesVector();
        Enumeration profilesEnum = profiles.elements();

        ArrayList profilesToRemove = new ArrayList();
        Profile profile;
        while(profilesEnum.hasMoreElements())
        {
            profile  = (Profile) profilesEnum.nextElement();
            if(profile.getUserAccount().getAccount().getAcronym().equals(accountName)
            && profile.getUserAccount().getAccount().getExchangeAcronym().equals(exchange)
            )
            {
                profile.markForDelete();
                profilesToRemove.add(profile);
            }
        }

        for(int i = 0; i < profilesToRemove.size(); i++)
        {
            profiles.remove(profilesToRemove.get(i));
        }
    }

    /**
     * Removes all profiles created for the associations between a market maker and a DPM from
     * the incoming struct.  These profiles will be added back in later processing.  Removing is
     * required so that changes in the classes assigned to the DPM will be updated correctly.

    private void removeDpmProfilesFromStruct(UserDefinitionStruct newValues)
    {
        // Updates from membership won't have any profiles, so no need to check.
        if (newValues.profilesByClass.length == 0)
        {
            return;
        }

        UserAccountRelation[] dpmAccounts = getDpmAccounts();
        if (dpmAccounts.length > 0)
        {
            ArrayList newProfiles = new ArrayList(newValues.profilesByClass.length);
            for (int i = 0; i < newValues.profilesByClass.length; i++)
            {
                boolean includeProfile = true;
                for (int j = 0; includeProfile && j < dpmAccounts.length; j++)
                {
                    // A very ugly sequence to get from this users relation to it's DPM to the joint account
                    // used by the DPM
                    String dpmJointAccount = dpmAccounts[j].getAccount().getDefaultProfile().getUserAccount().getAccount().getAcronym();
                    // profile can be included if it is not for this joint account
                    includeProfile = !newValues.profilesByClass[i].account.equals(dpmJointAccount);
                }
                if (includeProfile)
                {
                    newProfiles.add(newValues.profilesByClass[i]);
                }
            }
            newValues.profilesByClass = new ProfileStruct[newProfiles.size()];
            newProfiles.toArray(newValues.profilesByClass);
        }
    }
    */

	/**
	 * Set the firm key through an object editor.
	 * @param aValue The firm key.
	 */
	void setFirmKey(int aValue)
	{
		editor.set(_firmKey, aValue, firmKey);
	}
	/**
	 * Sets the last modified time of the user.
	 * @param modTime time user was modified
	 */
	 void setLastModifiedTime(long modTime)
	{
		editor.set(_lastModifiedTime, modTime, lastModifiedTime);
	}
	/**
	 * Set the member key through an object editor.
	 * @param aValue The member key.
	 */
	 void setMembershipKey(int aValue)
	{
		editor.set(_membershipKey, aValue, membershipKey);
	}
	/**
	 * Set the priveleges through an object editor.
	 * @param aValue array The priveleges.
	 */
	 void setRole(char aValue)
	{
		editor.set(_role, aValue, role);
	}
	/**
	 * Set the user name through an object editor.
	 * @param aValue The user name.
	 */
	 void setUserName(String aValue)
	{
		editor.set(_userName, aValue, userName);
	}
     
    public void setFullName(String aValue)
    {
        editor.set(_fullName, aValue, fullName);
    }

	/**
	 * Set the user type through an object editor.
	 * param aValue The user's type
	 */
	public void setUserType(short aValue)
	{
		editor.set(_userType, aValue, userType);
	}

	public UserQuoteRiskManagementProfileStruct toQuoteRiskStruct()
	{
		UserQuoteRiskManagementProfileStruct quoteRiskStruct = new UserQuoteRiskManagementProfileStruct();

		Vector profiles = getQuoteRiskManagementProfileVector();

		int arrayLen = profiles.size();
		if (profiles.size() > 0)
		{
			arrayLen -= 1;
		}

		// We expect one of the profiles to be the default profile, so we optimistically create array length-1
		//
		QuoteRiskManagementProfileStruct[] profileStructs = new QuoteRiskManagementProfileStruct[arrayLen];
		int arrayIdx=0;
		for (int i=0; i < profiles.size(); i++)
		{
			QuoteRiskManagementProfile profile = (QuoteRiskManagementProfile)profiles.elementAt(i);
			if (profile.getClassKey() == com.cboe.idl.cmiConstants.ProductClass.DEFAULT_CLASS_KEY &&
				quoteRiskStruct.defaultQuoteRiskProfile == null)
			{
				quoteRiskStruct.defaultQuoteRiskProfile = profile.toStruct();
			}
			else
			{
				if (arrayIdx < profileStructs.length)
				{
					profileStructs[arrayIdx++] = profile.toStruct();
				}
			}
		}

		// This would be unusual: no default found, so recopy the array and paste in the last element of the vector.
		//
		if (quoteRiskStruct.defaultQuoteRiskProfile == null && profiles.size() > 0)
		{
			QuoteRiskManagementProfileStruct[] allProfiles = new QuoteRiskManagementProfileStruct[profileStructs.length+1];
			System.arraycopy(profileStructs, 0, allProfiles, 0, profileStructs.length);
			allProfiles[profileStructs.length] = ((QuoteRiskManagementProfile)(profiles.elementAt(profiles.size()-1))).toStruct();
			profileStructs = allProfiles;
		}

		quoteRiskStruct.globalQuoteRiskManagementEnabled = getQuoteRiskManagementEnabled();
		quoteRiskStruct.quoteRiskProfiles = profileStructs;

		return quoteRiskStruct;
	}

	/**
	 *  Return the subset of assigned classes which are assigned to a DPM account that is in the user's account list.
	 */
	 DpmStruct[] createDpmStructs()
	{
		Vector accountsVector = getAccountsVector();
		ArrayList dpmVector = new ArrayList();

		// Search for DPM accounts
		//
		for (int i=0; i < accountsVector.size(); i++)
		{
			UserAccountRelation uac = (UserAccountRelation)accountsVector.elementAt(i);
			AcronymUserImpl account = uac.getAccount();
			if (account.getUserType() == UserTypes.DPM_ACCOUNT)
			{
				int[] accountClasses = account.getAssignedClasses();
				DpmStruct dpmStruct = new DpmStruct();
				dpmStruct.dpmUserId = account.getAcronym();
				dpmStruct.dpmAssignedClasses = accountClasses;
				dpmVector.add(dpmStruct);
			}
		}

		// Assemble the results.
		//
		DpmStruct[] dpmStructs = new DpmStruct[dpmVector.size()];
		dpmVector.toArray(dpmStructs);
		return dpmStructs;
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
	 * Sets only the common values that are available thru all the sources.
	 * i.e. membership updates only a few values, The gui updates only those
	 * values not updated by membership,
	 * @param userValues struct containing values for user
	 * @param membershipDefined if true, then the user info came from membership.
	 * @exception DataValidationException if validation checks fail
	 */
	public void updateBase(SessionProfileUserDefinitionStruct userValues, boolean membershipDefined) throws DataValidationException
	{
		checkVersion(userValues.userKey, userValues.versionNumber);
        checkUserRole(userValues);
        setUserName(userValues.userId); //kept for compatibility to version 1.1
        setUserId(userValues.userId); // value needs to be defined because of constraint.
        
        if (membershipDefined)
		{
            setActive(userValues.isActive);
		}
        
        if (getAcronym() != null && !getAcronym().equals(userValues.userAcronym.acronym))
        {
            throw ExceptionBuilder.dataValidationException("A user acronym cannot be changed (old="+ getAcronym() + ", new="+userValues.userAcronym.acronym, DataValidationCodes.INVALID_USER);
        }
        if (getExchangeAcronym() != null && !getExchangeAcronym().equals(userValues.userAcronym.exchange))
        {
            throw ExceptionBuilder.dataValidationException("A user exchange cannot be changed (old="+ getExchangeAcronym() + ", new=" + userValues.userAcronym.exchange, DataValidationCodes.INVALID_EXCHANGE);
        }
        
		setFullName(userValues.fullName);
		setAcronym(userValues.userAcronym.acronym);
        setExchangeAcronym(userValues.userAcronym.exchange);
		setFirmKey(userValues.firmKey);
		setMembershipKey(userValues.membershipKey);
		setUserType(userValues.userType);
		setRole(userValues.role);
		if (userValues.membershipKey > 0 && !StructBuilder.isDefault(userValues.lastModifiedTime)) {
			setLastModifiedTime(DateWrapper.convertToMillis(userValues.lastModifiedTime));
		}
		else {
			setLastModifiedTime(TimeServiceWrapper.getCurrentDateTimeInMillis());
		}

		//This will create or update the user account.
		handleUserAccount();
		setAccounts(userValues.accounts, membershipDefined);


		// set the assigned classes
		setAssignedClasses(userValues.assignedClasses);

		// Update the profiles for the classes that this market maker is a DPM participant.
		//
        if (Log.isDebugOn())
        {
            Log.debug(this, "Updating user profile or Clearing Acronyms for userId:" + loggableName());
        }
        createDefaultProfile(membershipDefined, userValues);
		addProfilesByClassBySession(userValues.defaultSessionProfiles, userValues.sessionProfilesByClass, membershipDefined);
        addClearingAcrs(userValues.sessionClearingAcronyms);
	}

	/**
	 *  Reject profiles which include DPM-assigned classes but which don't clear through the DPM.
	 *
	 *  @param profiles - an array of profiles supplied by the user.
	 *  @exception DataValidationException - thrown if a profile is invalid
	 */
	public void validateDPMProfiles(SessionProfileUserDefinitionStruct userValues) throws DataValidationException
	{
		// This validation only applies to market makers.
		//
		if (userValues.role != UserRoles.MARKET_MAKER)
		{
			return;
		}


		// Find DPM accounts.
		//
		ArrayList dpmUsers = new ArrayList();
		for (int i=0; i < userValues.accounts.length; i++)
		{
			try
			{
				UserHome userHome = (UserHome)getBOHome();
				User user = userHome.findByFirstExchangeAcronym(userValues.accounts[i].account);
				if (user.getUserType() == UserTypes.DPM_ACCOUNT)
				{
					dpmUsers.add(user);
				}
			}
			catch (NotFoundException ex)
			{
				throw ExceptionBuilder.dataValidationException("Error validating profile for DPM account " +  toString(userValues.accounts[i].account) + " - no such user.", 0);
			}
		}


		// For each DPM account, find a profile which lists it's values.
		//
		for (int i=0; i < dpmUsers.size(); i++)
		{
			UserInternal dpmUser      = (UserInternal)dpmUsers.get(i);
			int[]    dpmClassKeys = dpmUser.getAssignedClasses();
			String   dpmDefaultId = dpmUser.getDefaultProfile().getUserAccount().getAccount().getAcronym();

			// Examine each class key for the DPM.
			//
			for (int j=0; j < dpmClassKeys.length; j++)
			{
				// Find a profile for the class key.
				//
				for (int k=0; k < userValues.sessionProfilesByClass.length; k++)
				{
					// Verify that the profile clears through the DPM's default profile account.
					//
					if (userValues.sessionProfilesByClass[k].classKey == dpmClassKeys[j] &&
					    !dpmDefaultId.equals(userValues.sessionProfilesByClass[k].account))
					{
						String errMsg = "profile for class " + userValues.sessionProfilesByClass[k].classKey + " must clear through DPM " + dpmDefaultId;
						Log.alarm(this, errMsg);
						throw ExceptionBuilder.dataValidationException(errMsg, 0/*XXX - no code!*/);
					}
				}
			}
		}
	}

    void createDefaultProfile(boolean membershipDefined, SessionProfileUserDefinitionStruct userValues)
        throws DataValidationException
    {
        if (membershipDefined && userValues.userType == UserTypes.DPM_ACCOUNT)
		{
			AccountDefinitionStruct[] accounts = userValues.accounts;

			// DPM_ACCOUNT users are expected to list only their joint account ('Q' account)
			// All DPM accounts must list a joint account.
			//
			if (accounts.length != 1)
			{
                String msg = "DPM account must list it's joint account in the accounts field for user: "
                        + toString(userValues.userAcronym);
                Log.alarm(this, msg);
                throw ExceptionBuilder.dataValidationException(msg, 0/*XXX no code!*/);
			}
			SessionProfileStruct profileStruct = new SessionProfileStruct();
			profileStruct.account    = accounts[0].account.acronym;
			profileStruct.classKey   = DEFAULT_PROFILE_CLASS_KEY;
            profileStruct.sessionName = SessionNameValues.ALL_SESSION_NAME;
            profileStruct.isAccountBlanked = false;
            profileStruct.originCode = ' ';

            try
            {
                Firm firm = (Firm)getFirmHome().findFirmByKey(userValues.firmKey);
                profileStruct.executingGiveupFirm = new ExchangeFirmStruct(firm.getExchangeAcr(), firm.getFirmNumber());
            }
            catch(Exception e)
            {
                throw ExceptionBuilder.dataValidationException("Unable to find firm key for default profile for firm: " + userValues.firmKey + " and user: "
                                                            + toString(userValues.userAcronym), 0/*XXX no code!*/);
            }

			addDefaultProfile(profileStruct);
		}
		else
		{
			if (userValues.userKey == 0) // If comming from membershup update or if it is a new user, need to create it.
			{
				SessionProfileStruct ps = new SessionProfileStruct();
				ps.account = userValues.userAcronym.acronym;
				ps.classKey = DEFAULT_PROFILE_CLASS_KEY;
                ps.sessionName = SessionNameValues.ALL_SESSION_NAME;
                ps.isAccountBlanked = false;
                ps.originCode = ' ';
                // Try and leave the old sub account if it exists.
		        Profile existingDefault = getDefaultProfile();
                if (existingDefault != null)
                {
                    if (membershipDefined)
                    {
                        // If there is an existing default profile and this update is from membership
                        // (ie, not a new user), then do not update the default profile.
                        //
                        return;
                    }
                    String subAccount = existingDefault.getSubAccount();
                    if (subAccount != null)
                    {
                        ps.subAccount = subAccount;
                    }
                }

				try{
					Firm firm = getFirmHome().findFirmByKey( userValues.firmKey );
					ps.executingGiveupFirm = new ExchangeFirmStruct(firm.getExchangeAcr(), firm.getFirmNumber());
				} catch( Exception e ){
					Log.exception( this, e );
					throw ExceptionBuilder.dataValidationException("Unable to obtain firm for user", 0);
				}

				userValues.defaultProfile = ps;
			}
            // else, from SAGUI update user
			addDefaultProfile(userValues.defaultProfile);
		}
    }
     TradingSessionHome getTradingSessionHome()
    {
        if (tradingSessionHome == null)
        {
            synchronized (this)
            {
                if (tradingSessionHome == null)
                {
                    try
                    {
                        BOHome aHome = HomeFactory.getInstance().findHome(TradingSessionHome.HOME_NAME);
                        tradingSessionHome = (TradingSessionHome) aHome;
                    }
                    catch (com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException ex)
                    {
                        Log.exception(this, "Can NOT find TradingSessionHome instance" + ex, ex );
                        throw new Error("Could not find QuoteRiskManagementProfileHome instance: " + ex);
                    }
                }
            }
        }
        return tradingSessionHome;
    }

    public boolean isValidSessionName(String sessionName)
    {
        if (SessionNameValues.ALL_SESSION_NAME.equals(sessionName))
        {
            return true;
        }
        try
        {
            getTradingSessionHome().find(sessionName);
            return true;
        }
        catch (NotFoundException e)
        {
            Log.exception(this, e.details.message + e, e);
            return false;
        }
    }
    void checkUserRole(SessionProfileUserDefinitionStruct userValues) throws DataValidationException
    {
        if (userValues.sessionClearingAcronyms != null&& userValues.sessionClearingAcronyms.length > 0 )
        {
            if (userValues.role == UserRoles.MARKET_MAKER )
            {
                String msg = "You can NOT add session clearing Acronyms for Market Marker";
                Log.information(this,msg);
                throw ExceptionBuilder.dataValidationException(msg ,0);
            }
        }
    }
    
    private String toString(ExchangeAcronymStruct exchAcr)
    {
        return exchAcr.exchange + ':' + exchAcr.acronym;
    }
    
    public String toString()
    {
        return loggableName();
    }
    
    protected String loggableName()
    {
        final String id = (super.objectIdentifier==null) 
            ? "{newAcrUser}" 
            : Integer.toString(getAcronymUserKey());
        return '\''+ getAcronym() + '@' + getExchangeAcronym() + ';' + id + '\'';
    }
    
    /**
     * Sometimes this object may not have been associated with the UserHome.
     * Since this is only needed for updates, we'll associate with the home on
     * an as-neede basis
     * 
     * @see com.cboe.infrastructureServices.foundationFramework.BObject#getBOHome()
     */
    public BOHome getBOHome()
    {
        BOHome home = super.getBOHome();
        if (home == null)
        {
            try
            {
                home = HomeFactory.getInstance().findHome(UserHome.HOME_NAME);
                home.addToContainer(this);
                return super.getBOHome();
            }
            catch (CBOELoggableException ex)
            {
            }
        }
        return home;
    }
}
