//
// -----------------------------------------------------------------------------------
// Source file: UserCollectionFactory.java
//
// PACKAGE: com.cboe.internalPresentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.user;

import java.util.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiUser.DpmStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.SessionProfileStruct;
import com.cboe.idl.constants.UserTypes;
import com.cboe.idl.user.AccountDefinitionStruct;
import com.cboe.idl.user.MarketMakerClassAssignmentStruct;
import com.cboe.idl.user.SessionClearingAcronymStruct;
import com.cboe.idl.user.SessionProfileUserDefinitionStruct;
import com.cboe.idl.user.UserSummaryStruct;

import com.cboe.interfaces.internalPresentation.firm.FirmModel;
import com.cboe.interfaces.internalPresentation.user.SortableUserCollection;
import com.cboe.interfaces.internalPresentation.user.UserAccountModel;
import com.cboe.interfaces.internalPresentation.user.UserCollection;
import com.cboe.interfaces.internalPresentation.user.UserCollectionListener;
import com.cboe.interfaces.presentation.common.exchange.Exchange;
import com.cboe.interfaces.presentation.permissionMatrix.Permission;
import com.cboe.interfaces.presentation.permissionMatrix.UserPermissionMatrix;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.user.Role;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.exchange.GUIExchangeHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.permissionMatrix.PermissionMatrixFactory;
import com.cboe.presentation.user.ExchangeAcronymFactory;
import com.cboe.presentation.user.ProfileFactory;
import com.cboe.presentation.userSession.UserSessionEvent;
import com.cboe.presentation.userSession.UserSessionFactory;
import com.cboe.presentation.userSession.UserSessionListener;

import com.cboe.internalPresentation.api.SystemAdminAPIFactory;
import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;

import com.cboe.domain.util.StructBuilder;

/**
 * Provides a central location for the master collection of users.
 */
public class UserCollectionFactory
{
    public static final String USER_MAINTENANCE_PROPERTY_SECTION_NAME = "UserMaintenance";
    public static final String USER_MAINTENANCE_PRELOAD_PROPERTY_NAME = "PreLoadCache";

    private static UserCollection masterCollection;
    private static final Object lockObject = new Object();

    private static final UserSessionListener sessionListener = new UserSessionListener()
    {
        public void userSessionChange(UserSessionEvent event)
        {
            if(event.getActionType() == UserSessionEvent.FORCED_LOGGED_OUT_EVENT ||
                event.getActionType() == UserSessionEvent.LOGGED_OUT_EVENT)
            {
                masterCollection = null;
            }
            else if(event.getActionType() == UserSessionEvent.LOGGED_IN_EVENT)
            {
                boolean preloadCollection = true;
                if(AppPropertiesFileFactory.isAppPropertiesAvailable())
                {
                    String preloadValueString =
                            AppPropertiesFileFactory.find().getValue(USER_MAINTENANCE_PROPERTY_SECTION_NAME,
                                                                     USER_MAINTENANCE_PRELOAD_PROPERTY_NAME);
                    preloadCollection = Boolean.valueOf(preloadValueString).booleanValue();
                }

                if(preloadCollection)
                {
                    UserPermissionMatrix permissionMatrix = PermissionMatrixFactory.findUserPermissionMatrix();
                    if ( permissionMatrix.isAllowed(Permission.USER_MANAGEMENT_ACCESS) )
                    {
                        Thread preLoadThread = new Thread("UserCachePreLoadThread")
                        {
                            public void run()
                            {
                                buildMasterCollection();
                            }
                        };

                        preLoadThread.start();
                    }
                }
            }
        }
    };

    private static boolean sessionListenerRegistered = false;

    private static UserCollection immutableQueriedUserCollection;

    /**
     * Private constructor. Should not instantiate one of these.
     */
    private UserCollectionFactory(){}

    public static synchronized UserCollection getImmutableQueryCollection()
    {
        if(immutableQueriedUserCollection == null)
        {
            immutableQueriedUserCollection = new ImmutableUserCollection(getUserCollection());
        }

        return immutableQueriedUserCollection;
    }

    /**
     * Gets a personal copy of a SortableUserCollection tied back to the master collection.
     */
    public static synchronized SortableUserCollection getUserCollection()
    {
        if(masterCollection == null)
        {
            buildMasterCollection();
        }

        return new DefaultSortableUserCollection(masterCollection);
    }

    public static Object getUpdateLockObject()
    {
        return lockObject;
    }

    public static void initialize()
    {
        registerUserSessionListener();
    }

    public static UserAccountModel createUserAccountModel(UserSummaryStruct struct)
    {
        UserAccountModel newModel = new UserAccountModelImpl(struct);
        return newModel;
    }

    public static UserAccountModel createUserAccountModel(SessionProfileUserDefinitionStruct struct, boolean isNew)
    {
        UserAccountModel newModel = new UserAccountModelImpl(struct, isNew);
        return newModel;
    }

    public static UserAccountModel createNewUserAccountModel(String newUserId, ExchangeAcronym exchangeAcronym)
    {
        SessionProfileUserDefinitionStruct newStruct = createNewDefaultStruct();

        UserAccountModel[] siblingUsers = masterCollection.getAllUsersForExchangeAcronym(exchangeAcronym);
        if(siblingUsers != null)
        {
            for(int i = 0; i < siblingUsers.length; i++)
            {
                UserAccountModel siblingUser = siblingUsers[i];
                if(!siblingUser.isNeverBeenSaved())
                {
                    newStruct = createNewStructBasedOnExistingUser(siblingUser);
                    break;
                }
            }
        }
        if(newStruct == null)
        {
            newStruct = createNewDefaultStruct();
        }

        newStruct.userAcronym = exchangeAcronym.getExchangeAcronymStruct();

        SessionProfileStruct profileStruct = newStruct.defaultProfile;
        profileStruct.account = exchangeAcronym.getAcronym();

        newStruct.userId = newUserId;

        UserAccountModel newModel = createUserAccountModel(newStruct, true);

        return newModel;
    }

    private static SessionProfileUserDefinitionStruct createNewStructBasedOnExistingUser(UserAccountModel existingUser)
    {
        SessionProfileUserDefinitionStruct newStruct = new SessionProfileUserDefinitionStruct();
        SessionProfileUserDefinitionStruct existingStruct = existingUser.getUserDefinitionStruct();

        newStruct.inactivationTime = StructBuilder.buildDateTimeStruct();
        newStruct.lastModifiedTime = StructBuilder.buildDateTimeStruct();
        newStruct.assignedClasses = new MarketMakerClassAssignmentStruct[0];
        newStruct.accounts = new AccountDefinitionStruct[0];
        newStruct.executingGiveupFirms = new ExchangeFirmStruct[0];
        newStruct.dpms = new DpmStruct[0];
        newStruct.sessionProfilesByClass = new SessionProfileStruct[0];
        newStruct.defaultSessionProfiles = new SessionProfileStruct[0];
        newStruct.sessionClearingAcronyms = new SessionClearingAcronymStruct[0];
        newStruct.isActive = false;
        newStruct.membershipKey = -1;
        newStruct.role = existingStruct.role;
        newStruct.userType = existingStruct.userType;
        newStruct.fullName = existingStruct.fullName;
        newStruct.userId = "";

        newStruct.firmKey = existingStruct.firmKey;

        ExchangeAcronymStruct existingExchAcro = existingStruct.userAcronym;
        ExchangeAcronymStruct newExchAcro = new ExchangeAcronymStruct(existingExchAcro.exchange,
                                                                      existingExchAcro.acronym);
        newStruct.userAcronym = newExchAcro;

        SessionProfileStruct defaultProfileStruct = ProfileFactory.createDefaultProfileStruct();
        defaultProfileStruct.account = newExchAcro.acronym;
        newStruct.defaultProfile = defaultProfileStruct;

        return newStruct;
    }

    public static SessionProfileUserDefinitionStruct createNewDefaultStruct()
    {
        SessionProfileUserDefinitionStruct newStruct = new SessionProfileUserDefinitionStruct();

        newStruct.inactivationTime = StructBuilder.buildDateTimeStruct();
        newStruct.lastModifiedTime = StructBuilder.buildDateTimeStruct();
        newStruct.assignedClasses = new MarketMakerClassAssignmentStruct[0];
        newStruct.accounts = new AccountDefinitionStruct[0];
        newStruct.executingGiveupFirms = new ExchangeFirmStruct[0];
        newStruct.dpms = new DpmStruct[0];
        newStruct.sessionProfilesByClass = new SessionProfileStruct[0];
        newStruct.defaultSessionProfiles = new SessionProfileStruct[0];
        newStruct.sessionClearingAcronyms = new SessionClearingAcronymStruct[0];
        newStruct.isActive = false;
        newStruct.membershipKey = -1;
        newStruct.role = Role.UNKNOWN.getRoleChar();
        newStruct.userType = UserTypes.INDIVIDUAL_ACCOUNT;
        newStruct.fullName = "";
        newStruct.userId = "";

        FirmModel[] allFirms = SystemAdminAPIFactory.find().getFirms();
        newStruct.firmKey = allFirms[0].getFirmKey();

        Exchange defaultExchange = GUIExchangeHome.find().getUnspecifiedExchange();
        ExchangeAcronym defaultExchangeAcronym =
                ExchangeAcronymFactory.createExchangeAcronym(defaultExchange.getExchange(), "");
        newStruct.userAcronym = defaultExchangeAcronym.getExchangeAcronymStruct();

        SessionProfileStruct defaultProfileStruct = ProfileFactory.createDefaultProfileStruct();
        defaultProfileStruct.account = defaultExchangeAcronym.getAcronym();
        newStruct.defaultProfile = defaultProfileStruct;

        return newStruct;
    }

    static UserSummaryStruct createUserSummaryStruct(SessionProfileUserDefinitionStruct fullUserStruct)
    {
        UserSummaryStruct newSummaryStruct = new UserSummaryStruct();
        newSummaryStruct.firmKey = fullUserStruct.firmKey;
        newSummaryStruct.fullName = fullUserStruct.fullName;
        newSummaryStruct.isActive = fullUserStruct.isActive;
        newSummaryStruct.role = fullUserStruct.role;
        newSummaryStruct.userAcronym = fullUserStruct.userAcronym;
        newSummaryStruct.userId = fullUserStruct.userId;
        newSummaryStruct.userKey = fullUserStruct.userKey;
        newSummaryStruct.userType = fullUserStruct.userType;
        return newSummaryStruct;
    }

    /**
     * Builds the master UserCollection
     */
    private static synchronized void buildMasterCollection()
    {
        if(!sessionListenerRegistered)
        {
            registerUserSessionListener();
        }

        if(masterCollection == null)
        {
            GUILoggerHome.find().debug("UserCollectionFactory",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT,
                                       "User collection loading begin.");

            masterCollection = new MasterUserCollection();

            //This synchronize block must remain with the MasterUserCollection.channelUpdate so that
            //we don't start processing events before the collection is loaded up.
            synchronized( masterCollection )
            {
                try
                {
                    SystemAdminAPIFactory.find().subscribeUserMaintenanceEvent(
                            ( MasterUserCollection ) masterCollection);
                    (( MasterUserCollection ) masterCollection).loadCollection(
                            SystemAdminAPIFactory.find().getAllUsers());
                }
                catch (UserException e)
                {
                    StringBuffer msg = new StringBuffer(60);
                    msg.append("Users could not be loaded from the system. ");
                    msg.append("You will NOT be able to view users in the system!");

                    DefaultExceptionHandlerHome.find().process(e, msg.toString());
                }
            }

            GUILoggerHome.find().debug("UserCollectionFactory",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT,
                                       "User collection loading done.");
        }
    }

    /**
     * Registers a user session listener if needed
     */
    private static synchronized void registerUserSessionListener()
    {
        if(!sessionListenerRegistered)
        {
            UserSessionFactory.findUserSession().addUserSessionListener(sessionListener);
            sessionListenerRegistered = true;
        }
    }
}

class ImmutableUserCollection implements UserCollection
{
    private UserCollection masterCollection;

    protected ImmutableUserCollection(UserCollection masterCollection)
    {
        this.masterCollection = masterCollection;
    }

    public boolean containsUser(UserAccountModel user)
    {
        return masterCollection.containsUser(user);
    }

    public UserAccountModel find(String userId)
    {
        return masterCollection.find(userId);
    }

    public ExchangeAcronym findExchangeAcronym(String exchange, String acronym)
    {
        return masterCollection.findExchangeAcronym(exchange, acronym);
    }

    public UserAccountModel findPartial(String partialUserId, Iterator userIdIterator)
    {
        return masterCollection.findPartial(partialUserId, userIdIterator);
    }

    public ExchangeAcronym[] getAllExchangeAcronyms()
    {
        return masterCollection.getAllExchangeAcronyms();
    }

    public UserAccountModel[] getAllUsersForExchangeAcronym(ExchangeAcronym exchangeAcronym)
    {
        return masterCollection.getAllUsersForExchangeAcronym(exchangeAcronym);
    }

    public ExchangeAcronym[] getExchangeAcronymsForExchange(String exchange)
    {
        return masterCollection.getExchangeAcronymsForExchange(exchange);
    }

    public Iterator getIteratorForUsers()
    {
        return masterCollection.getIteratorForUsers();
    }

    public List getListCollection()
    {
        return masterCollection.getListCollection();
    }

    public SortedSet getSortedUserIdSet()
    {
        return masterCollection.getSortedUserIdSet();
    }

    public int getSize()
    {
        return masterCollection.getSize();
    }

    public UserAccountModel getUserAccount(int index)
    {
        return masterCollection.getUserAccount(index);
    }

    public void addListener(UserCollectionListener listener)
    {
        throw new UnsupportedOperationException("This UserCollection is immutable.");
    }

    public void addListener(UserCollectionListener listener, UserAccountModel user)
    {
        throw new UnsupportedOperationException("This UserCollection is immutable.");
    }

    public UserAccountModel addUser(SessionProfileUserDefinitionStruct newUser)
    {
        throw new UnsupportedOperationException("This UserCollection is immutable.");
    }

    public void addUser(UserAccountModel newUser)
    {
        throw new UnsupportedOperationException("This UserCollection is immutable.");
    }

    public void removeAllListener(UserCollectionListener listener)
    {
        throw new UnsupportedOperationException("This UserCollection is immutable.");
    }

    public void removeListener(UserCollectionListener listener)
    {
        throw new UnsupportedOperationException("This UserCollection is immutable.");
    }

    public void removeListener(UserCollectionListener listener, UserAccountModel user)
    {
        throw new UnsupportedOperationException("This UserCollection is immutable.");
    }

    public void removeUser(UserAccountModel userModel)
    {
        throw new UnsupportedOperationException("This UserCollection is immutable.");
    }
}
