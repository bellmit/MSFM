//
// -----------------------------------------------------------------------------------
// Source file: UserAccountModelImpl.java
//
// PACKAGE: com.cboe.internalPresentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.user;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiQuote.UserQuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.constants.MarketMakerClassAssignmentTypes;
import com.cboe.idl.constants.PropertyCategoryTypes;
import com.cboe.idl.constants.UserTypes;
import com.cboe.idl.property.PropertyGroupStruct;
import com.cboe.idl.user.AccountDefinitionStruct;
import com.cboe.idl.user.MarketMakerClassAssignmentStruct;
import com.cboe.idl.user.SessionClearingAcronymStruct;
import com.cboe.idl.user.SessionProfileUserDefinitionStruct;
import com.cboe.idl.user.UserSummaryStruct;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.internalPresentation.firm.FirmModel;
import com.cboe.interfaces.internalPresentation.user.*;
import com.cboe.interfaces.presentation.dpm.DPMModel;
import com.cboe.interfaces.presentation.marketMaker.MMClassAssignmentModel;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.qrm.UserQuoteRiskManagementProfile;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.user.ExchangeAcronymModel;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.user.Profile;
import com.cboe.interfaces.presentation.user.ProfileModel;
import com.cboe.interfaces.presentation.user.Role;
import com.cboe.interfaces.presentation.preferences.PreferenceConstants;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelListener;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.dpm.DPMStructModel;
import com.cboe.presentation.qrm.GUIUserTradingParametersAPIHome;
import com.cboe.presentation.qrm.UserQuoteRiskManagementProfileImpl;
import com.cboe.presentation.threading.APIWorkerImpl;
import com.cboe.presentation.threading.GUIWorkerImpl;
import com.cboe.presentation.threading.SwingEventThreadWorker;
import com.cboe.presentation.user.ExchangeAcronymFactory;
import com.cboe.presentation.user.ExchangeFirmFactory;
import com.cboe.presentation.user.ProfileModelFactory;
import com.cboe.presentation.user.RoleFactory;
import com.cboe.presentation.product.ProductHelper;

import com.cboe.internalPresentation.api.SystemAdminAPIFactory;
import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;
import com.cboe.internalPresentation.common.formatters.OperationTypes;
import com.cboe.internalPresentation.marketMaker.MMClassAssignmentModelImpl;
import com.cboe.internalPresentation.sessionManagement.SessionInfoManagerHome;
import com.cboe.internalPresentation.sessionManagement.SessionQueryException;
import com.cboe.internalPresentation.sessionManagement.UserSession;

import com.cboe.domain.property.PropertyFactory;
import com.cboe.domain.property.PropertyServiceFacadeHome;
import com.cboe.domain.util.DateWrapper;

/**
 * Encapsulates a UserDefinitionStruct, providing behaviour
 */
public class UserAccountModelImpl implements PropertyChangeListener, UserAccountModel, EventChannelListener
{
    private static final String SEQUENCE_PREF_DELIMITER = Character.toString(PreferenceConstants.DELIMITER);
    private UserSummaryStruct userSummaryStruct;
    private SessionProfileUserDefinitionStruct user;

    private PropertyChangeSupport propertyEventManager;
    private final DateWrapper dateWrapper = new DateWrapper();
    private UserQuoteRiskManagementProfile userQRMProfile;
    private UserSession smsSession;
    private ExchangeAcronym exchangeAcronym;
    private Map sessionClearingAcronyms;
    private boolean isModified;
    private boolean beingDeleted;
    private boolean isNew;

    private ProfileModel defaultProfile;
    private ProfileModel[] profileModels;

    private boolean isEnablementsLoaded;
    private boolean isEnablementsChanged;
    private List<Enablement> enablements;
    private Map<SessionProductClass, List<Enablement>> enablementsByClass;
    private Map<Integer, List<Enablement>> enablementsByOperation;
    private PropertyServicePropertyGroup testClassPropertyGroup;
    private PropertyServicePropertyGroup mdxPropertyGroup;
    private PropertyServicePropertyGroup tradingFirmPropertyGroup;
    private int enablementVersion;

    private boolean isUserFirmAffiliationLoaded;
    private UserFirmAffiliation userFirmAffiliation;

    //buffered structs for abort operations ONLY
    private SessionProfileUserDefinitionStruct newUser;
    private SessionProfileUserDefinitionStruct oldUser;

    private boolean userDetailsAvailable;
    private boolean isClassAssignmentsChanged;

    private short smsState;

    // map of the user's current PARBrokerProfiles, by version
    private Map<String, PARBrokerProfile> parProfilesByVersion = new HashMap<String, PARBrokerProfile>();
    // List of all PARBrokerProfiles that have been deleted since the last time the user was saved
    private List<PARBrokerProfile> deletedParProfilesByVersion = Collections.synchronizedList(new ArrayList<PARBrokerProfile>());
    private boolean parProfilesLoaded;

    UserAccountModelImpl(UserSummaryStruct userSummaryStruct)
    {
        if(userSummaryStruct == null)
        {
            throw new IllegalArgumentException("UserSummaryStruct may not be null.");
        }

        smsState = SMS_NOT_LOADED;
        this.userSummaryStruct = userSummaryStruct;
        user = null;
        userDetailsAvailable = false;

        initialize();

        //the only way this constructor can be used is from the initial get when all are existing.
        isNew = false;

        setModified(false);
    }

    /**
     * Instantiates a new UserAccountModel based on the passed UserDefinitionStruct.
     * @param user to based model on
     * @param isNeverBeenSaved Set to true if the UserDefinitionStruct this model is
     * based on has never been saved to the API. Reserved for functionality that creates
     * new users. False if this user was obtained from the API already.
     */
    UserAccountModelImpl(SessionProfileUserDefinitionStruct user, boolean isNeverBeenSaved)
    {
        if(user == null)
        {
            throw new IllegalArgumentException("SessionProfileUserDefinitionStruct may not be null.");
        }

        this.user = user;
        userSummaryStruct = null;
        userDetailsAvailable = true;

        initialize();

        if(user.assignedClasses == null)
        {
            user.assignedClasses = new MarketMakerClassAssignmentStruct[0];
        }

        isNew = isNeverBeenSaved;

        //if this is new then modified should be set to true, if based on existing
        //user set to false since we just instantiated and nothing could have been
        //changed yet
        setModified(isNeverBeenSaved);
    }

    UserAccountModelImpl(SessionProfileUserDefinitionStruct user)
    {
        this(user, false);
    }

    public int hashCode()
    {
        int result;
        result = getUserId().hashCode();
        result = 29 * result + getExchangeAcronym().hashCode();
        return result;
    }

    /**
     * Returns the String representation of the UserDefinitionStruct
     * @return String
     */
    public String toString()
    {
        StringBuffer string = new StringBuffer(60);
        string.append(getFullName()).append(" (").append(getUserId()).append(')');
        return string.toString();
    }

    public boolean equals(Object obj)
    {
        boolean isEqual;

        if(this == obj)
        {
            isEqual = true;
        }
        else if(obj == null)
        {
            isEqual = false;
        }
        else if(obj instanceof UserAccountModel)
        {
            UserAccountModel castedObj = (UserAccountModel)obj;

            isEqual = getUserId().equals(castedObj.getUserId());
            if(isEqual)
            {
                isEqual = getExchangeAcronym().equals(castedObj.getExchangeAcronym());
            }
        }
        else
        {
            isEqual = false;
        }

        return isEqual;
    }

    /**
     * Handle events that are sent to this listener.  Currently only the enablement update and remove event are
     * subscribed to.  Only the update event is relevant to the model.
     */
    public void channelUpdate(ChannelEvent event)
    {
        int channelType = ((ChannelKey) event.getChannel()).channelType;

        if(channelType == ChannelType.UPDATE_PROPERTY)
        {
            final PropertyGroupStruct propertyGroupStruct = (PropertyGroupStruct) event.getEventData();
            String propertyKey = propertyGroupStruct.propertyKey;

            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
            {

                GUILoggerHome.find().debug(getClass().getName() + ".channelUpdate() ChannelType.UPDATE_PROPERTY event received.",
                                           GUILoggerSABusinessProperty.USER_MANAGEMENT, propertyGroupStruct);
            }

            // Double check to make sure this is for the current user
            if(propertyKey.equals(EnablementFactory.getUserEnablementsKey(this)))
            {
                GUIWorkerImpl worker = new GUIWorkerImpl(UserCollectionFactory.getUpdateLockObject())
                {
                    private Enablement[] oldValue;
                    private Enablement[] newValue;

                    public boolean isCleanUpEnabled()
                    {
                        return false;
                    }

                    public boolean isInitializeEnabled()
                    {
                        return false;
                    }

                    public void execute()
                            throws Exception
                    {
                        oldValue = enablements.toArray(new Enablement[0]);

                        PropertyServicePropertyGroup propertyGroup = PropertyFactory.createPropertyGroup(propertyGroupStruct);
                        setEnablementCollections(propertyGroup);

                        newValue = enablements.toArray(new Enablement[0]);
                    }

                    public void processData()
                    {
                        firePropertyChange(ENABLEMENTS_CHANGE_EVENT, oldValue, newValue);
                    }

                    public void handleException(Exception e)
                    {
                        DefaultExceptionHandlerHome.find().process(e,
                                                                   "Error occurred processing a changed user Enablement.");
                    }
                };
                APIWorkerImpl.run(worker);
            }
            else if(propertyKey.equals(EnablementFactory.getTestClassOnlyKey(this)))
            {
                GUIWorkerImpl worker = new GUIWorkerImpl(UserCollectionFactory.getUpdateLockObject())
                {
                    private PropertyServicePropertyGroup oldValue;
                    private PropertyServicePropertyGroup newValue;

                    public boolean isCleanUpEnabled()
                    {
                        return false;
                    }

                    public boolean isInitializeEnabled()
                    {
                        return false;
                    }

                    public void execute()
                            throws Exception
                    {
                        oldValue = testClassPropertyGroup;
                        testClassPropertyGroup = PropertyFactory.createPropertyGroup(propertyGroupStruct);
                        newValue = testClassPropertyGroup;
                    }

                    public void processData()
                    {
                        firePropertyChange(TESTCLASSES_CHANGE_EVENT, oldValue, newValue);
                    }

                    public void handleException(Exception e)
                    {
                        DefaultExceptionHandlerHome.find().process(e,
                                                                   "Error occurred processing a changed user Enablement.");
                    }
                };
                APIWorkerImpl.run(worker);
            }
            else if(propertyKey.equals(EnablementFactory.getMDXKey(this)))
            {
                GUIWorkerImpl worker = new GUIWorkerImpl(UserCollectionFactory.getUpdateLockObject())
                {
                    private PropertyServicePropertyGroup oldValue;
                    private PropertyServicePropertyGroup newValue;

                    public boolean isCleanUpEnabled()
                    {
                        return false;
                    }

                    public boolean isInitializeEnabled()
                    {
                        return false;
                    }

                    public void execute()
                            throws Exception
                    {
                        oldValue = mdxPropertyGroup;
                        mdxPropertyGroup = PropertyFactory.createPropertyGroup(propertyGroupStruct);
                        newValue = mdxPropertyGroup;
                    }

                    public void processData()
                    {
                        firePropertyChange(MDX_ENABLEMENT_CHANGE_EVENT, oldValue, newValue);
                    }

                    public void handleException(Exception e)
                    {
                        DefaultExceptionHandlerHome.find().process(e, "Error occurred processing a changed MDX user enablement.");
                    }
                };
                APIWorkerImpl.run(worker);
            }
            else if(propertyKey.equals(EnablementFactory.getTradingFirmKey(this)))
            {
                GUIWorkerImpl worker = new GUIWorkerImpl(UserCollectionFactory.getUpdateLockObject())
                {
                    private PropertyServicePropertyGroup oldValue;
                    private PropertyServicePropertyGroup newValue;

                    public boolean isCleanUpEnabled()
                    {
                        return false;
                    }

                    public boolean isInitializeEnabled()
                    {
                        return false;
                    }

                    public void execute()
                            throws Exception
                    {
                        oldValue = tradingFirmPropertyGroup;
                        tradingFirmPropertyGroup = PropertyFactory.createPropertyGroup(propertyGroupStruct);
                        newValue = tradingFirmPropertyGroup;
                    }

                    public void processData()
                    {
                        firePropertyChange(TRADINGFIRM_ENABLEMENT_CHANGE_EVENT, oldValue, newValue);
                    }

                    public void handleException(Exception e)
                    {
                        DefaultExceptionHandlerHome.find().process(e, "Error occurred processing a changed MDX user enablement.");
                    }
                };
                APIWorkerImpl.run(worker);
            }
        }
    }

    /**
     * Clears the SMS session reference so further calls will obtain refreshed data
     */
    public void refreshSMSData()
    {
        smsSession = null;
    }

    public short getLoggedInStatus()
    {
        return getLoggedInStatus(true);
    }

    public short getLoggedInStatus(boolean refreshFromSMS)
    {
        //call findSMSUserSession() to lazily load the SMS session and set the smsState
        findSMSUserSession(refreshFromSMS);
        return smsState;
    }

    public String[] getCASList()
    {
        return getCASList(true);
    }

    /**
     * Gets the list of CAS's this user is logged into.
     * @return String array of CAS's this user is logged into
     */
    public String[] getCASList(boolean refreshFromSMS)
    {
        String[] casList = new String[0];
        UserSession session = findSMSUserSession(refreshFromSMS);

        if(session != null)
        {
            casList = session.getCASList();
        }

        return casList;
    }

    /**
     * Returns all the enablements set for this user
     * @return all enablements set for this user
     */
    public Enablement[] getAllEnablements()
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        loadEnablements();
        return enablements.toArray(new Enablement[0]);
    }

    public Enablement[] getAllEnablementsDirect()
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        isEnablementsLoaded = false;

        enablements.clear();
        enablementsByClass.clear();
        enablementsByOperation.clear();

        loadEnablements();
        return enablements.toArray(new Enablement[0]);
    }

    /**
     * Adds enablements for this user
     * @param enablementArray to add
     */
    public void addEnablements(Enablement[] enablementArray)
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        Enablement[] oldValue = getAllEnablements();
        if(enablementArray != null)
        {
            for( Enablement enablement : enablementArray )
            {
                addEnablement(enablement, false);
            }

            isEnablementsChanged = true;
            if( enablementArray.length > 0 )
            {
                setModified(true);
                firePropertyChange(ENABLEMENTS_CHANGE_EVENT, oldValue, getAllEnablements());
            }
        }
    }


    /**
     * Adds an enablement for this user. If an enablement already exists for the same SessionProductClass
     * and operation type, then it is removed before the passed on is added. This implementation will only
     * add the enablement if the isEnabled() == true.
     * @param enablement to add
     * @return if a previous Enablement already existed with the same SessionProductClass and operation type,
     * then it is returned, otherwise null is returned.
     */
    public Enablement addEnablement(Enablement enablement)
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        Enablement previous = addEnablement(enablement, true);
        return previous;
    }

    /**
     * Updates an existing enablement. If an enablement does not already exist for the same SessionProductClass
     * and operation type, then it is added.
     * @param enablement to update
     * @return previous enablement before it was updated, unless it did not already exist, then null
     * is returned.
     */
    public Enablement updateEnablement(Enablement enablement)
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        return addEnablement(enablement);
    }

    /**
     * this method updates the enablement for an existing session-product class. it does not create an entry for the
     * session-product class if it doesn't exists (which is what addEnablement method does).
     * I don't use it anymore (I decided to use addEnablement), but I'll keep it around in case we need it in the future.
     * Shawn Khosravani, April 5, 2006
     *
    public boolean updateEnablement(Enablement enablement, boolean fireEvent)
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        if(enablement != null)
        {
            List<Enablement> listByClass = enablementsByClass.get(enablement.getSessionProductClass());
            if (listByClass == null)
            {
                System.out.println("UserAccountModelImpl.updateEnablement: skipping, no enablements found for sessProdCls="
                                 + enablement.getSessionProductClass().getTradingSessionName() + " / " + enablement.getSessionProductClass().getProductDescription());
                return false;
            }
            // TODO: do we get an empty list if there is an enablements row for the session, but has all enablements disabled ??

            Integer          operation       = new Integer(enablement.getOperationType());
            List<Enablement> listByOperation = enablementsByOperation.get(operation);
            Enablement       previous        = null;
            Enablement       current         = null;

            if (enablement.isEnabled())
            {
                if (enablements.indexOf(enablement) >= 0)
                {
                    System.out.println(" UserAccountModelImpl.updateEnablement: enablement exists, don't bother adding");
                    // it already exists, so don't bother adding
                    return false;
                }
                previous = removeEnablement(enablement, false);
                System.out.println("UserAccountModelImpl.updateEnablement: added enablement...previous was " + previous);
                enablements.add(enablement);

                listByClass.add(enablement);
                enablementsByClass.put(enablement.getSessionProductClass(), listByClass);

                if (listByOperation == null)
                {
                    listByOperation = allocateEnablementList();
                }
                listByOperation.add(enablement);

                current = enablement;
            }
            else
            {
                Enablement inverted = EnablementFactory.create(enablement.getSessionProductClass(), enablement.getOperationType(), true);
                if (enablements.indexOf(inverted) < 0)
                {
                    // it doesn't exist, so don't bother removing
                    System.out.println("UserAccountModelImpl.updateEnablement: inverted enablement doesnt exist, don't bother removing");
                    return false;
                }
                previous = removeEnablement(inverted, false);
                System.out.println("UserAccountModelImpl.updateEnablement: removed invrse enablement...previous was " + previous);
                if (previous != null)
                {
                    enablements.remove(previous);
                    listByClass.remove(previous);
                    listByOperation.remove(previous);
                }
            }

            enablementsByOperation.put(operation, listByOperation);

            setModified(true);
            isEnablementsChanged = true;
            if( fireEvent )
            {
                firePropertyChange(ENABLEMENTS_CHANGE_EVENT, previous, current);
            }
            return true;
        }
        return false;
    }
    */

    /**
     * Updates an array of existing enablements. If an enablement element does not already exist for the
     * same SessionProductClass and operation type, then it is added.
     * @param enablementArray to update
     */
    public void updateEnablements(Enablement[] enablementArray)
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        addEnablements(enablementArray);
    }

    /**
     * Removes an enablement
     * @param enablement to remove
     * @return if enablement was found and removed, then it is returned, otherwise null is returned
     */
    public Enablement removeEnablement(Enablement enablement)
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        Enablement previous = removeEnablement(enablement, true);
        return previous;
    }

    /**
     * Removes all enablements for the passed SessionProductClass
     * @param sessionProductClass to remove enablements for
     */
    public void removeEnablements(SessionProductClass sessionProductClass)
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException,
                   NotFoundException
    {
        Enablement[] oldValue = getAllEnablements();
        Enablement[] spcEnablements = containsEnablements(sessionProductClass);

        if(spcEnablements != null)
        {
            enablementsByClass.remove(sessionProductClass);
            for (Enablement spcEnablement : spcEnablements)
            {
                removeEnablement(spcEnablement, false);
            }

            isEnablementsChanged = true;
            if( spcEnablements.length > 0 )
            {
                setModified(true);
                firePropertyChange(ENABLEMENTS_CHANGE_EVENT, oldValue, getAllEnablements());
            }
        }
    }

    /**
     * Sets the enablements for this user to be only what is passed in
     * @param newEnablements to override existing enablements with
     */
    public void setEnablements(Enablement[] newEnablements)
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        loadEnablements();
        if(newEnablements != null)
        {
            Enablement[] oldValue = enablements.toArray(new Enablement[0]);

            enablements.clear();
            enablementsByClass.clear();
            enablementsByOperation.clear();

            for( Enablement enablement : newEnablements )
            {
                addEnablement(enablement);
            }

            isEnablementsChanged = true;
            setModified(true);
            firePropertyChange(ENABLEMENTS_CHANGE_EVENT, oldValue, newEnablements);
        }
    }

    /**
     * Sets whether this user is enabled for MDX operations (e.g., CurrentMarketV4, RecapV4, etc)
     * @param mdxEnabled true if to be enabled for MDX
     */
    public void setMDXEnabled(boolean mdxEnabled)
            throws DataValidationException, CommunicationException, AuthorizationException, SystemException
    {
        boolean oldValue = isMDXEnabled();
        if(oldValue != mdxEnabled)
        {
            Boolean oldObject = Boolean.valueOf(oldValue);
            mdxPropertyGroup.setProperty(EnablementFactory.createMDXProperty(mdxEnabled));
            isEnablementsChanged = true;
            setModified(true);
            firePropertyChange(MDX_ENABLEMENT_CHANGE_EVENT, oldObject, Boolean.valueOf(mdxEnabled));
        }
    }
    
    public void setTradingFirmEnabled(boolean tradingFirmEnabled)
            throws DataValidationException, CommunicationException, AuthorizationException, SystemException, NotFoundException
    {
        boolean oldValue = isTradingFirmEnabled();
        if(oldValue != tradingFirmEnabled)
        {
            Boolean oldObject = Boolean.valueOf(oldValue);
            tradingFirmPropertyGroup.setProperty(EnablementFactory.createTradingFirmProperty(tradingFirmEnabled));
            isEnablementsChanged = true;
            setModified(true);
            firePropertyChange(TRADINGFIRM_ENABLEMENT_CHANGE_EVENT, oldObject, Boolean.valueOf(tradingFirmEnabled));
        }
    }

    /**
     * Sets whether this user is enabled only for test classes
     * @param testClassOnly true if to be enabled ONLY for test classes,
     * false if other enablements determine class access
     */
    public void setTestClassOnlyEnabled(boolean testClassOnly)
            throws DataValidationException, CommunicationException, AuthorizationException, SystemException
    {
        boolean oldValue = isTestClassOnlyEnabled();
        if( oldValue != testClassOnly )
        {
            Boolean oldObject = Boolean.valueOf(oldValue);

            testClassPropertyGroup.setProperty(EnablementFactory.createTestClassProperty(testClassOnly));
            isEnablementsChanged = true;
            setModified(true);
            firePropertyChange(TESTCLASSES_CHANGE_EVENT, oldObject, Boolean.valueOf(testClassOnly));
        }
    }

    /**
     * Determines if this user is enabled for MDX operations
     * @return true if enabled for MDX
     */
    public boolean isMDXEnabled()
            throws DataValidationException, CommunicationException, AuthorizationException, SystemException
    {
        boolean enabled = false;
        loadEnablements();
        if(isEnablementsLoaded)
        {
            enabled = Boolean.valueOf(EnablementFactory.getMDXPropertyValue(mdxPropertyGroup)).booleanValue();
        }
        return enabled;
    }
    
    public boolean isTradingFirmEnabled() throws DataValidationException, CommunicationException, AuthorizationException, SystemException, NotFoundException
    {
        boolean enabled = false;
        loadEnablements();
        if(isEnablementsLoaded)
        {
            enabled = Boolean.valueOf(EnablementFactory.getTradingFirmPropertyValue(tradingFirmPropertyGroup)).booleanValue();
        }
        return enabled;
    }

    /**
     * Determines if this user is enabled only for test classes
     * @return true if enabled ONLY for test classes, false if other enablements determine class access
     */
    public boolean isTestClassOnlyEnabled()
            throws DataValidationException, CommunicationException, AuthorizationException, SystemException
    {
        boolean enabled = false;
        loadEnablements();
        if(isEnablementsLoaded)
        {
            enabled = Boolean.valueOf(EnablementFactory.getTestClassPropertyValue(testClassPropertyGroup)).booleanValue();
        }
        return enabled;
    }

    /**
     * Determines if any enablements exist for passed operationType
     * @param operationType to look for
     * @return all enablements for all SessionProductClass'es for passed operationType
     */
    public Enablement[] containsEnablements(int operationType)
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        loadEnablements();
        Enablement[] elements = new Enablement[0];
        Integer operation = new Integer(operationType);
        List<Enablement> listByOperation = enablementsByOperation.get(operation);
        if( listByOperation != null )
        {
            elements = listByOperation.toArray(elements);
        }
        return elements;
    }

    /**
     * Determines if any enablements exist for passed sessionName and classKey
     * @param sessionName to look for
     * @param classKey to look for
     * @return all Enablements for all operation types for passed sessionName and classKey
     */
    public Enablement[] containsEnablements(String sessionName, int classKey)
            throws AuthorizationException, NotFoundException, CommunicationException, DataValidationException,
            SystemException
    {
        SessionProductClass spc = APIHome.findProductQueryAPI().getClassByKeyForSession(sessionName, classKey);
        return containsEnablements(spc);
    }

    /**
     * Determines if any enablements exist for passed sessionProductClass
     * @param sessionProductClass to look for
     * @return all Enablements for all operation types for passed sessionProductClass
     */
    public Enablement[] containsEnablements(SessionProductClass sessionProductClass)
            throws DataValidationException, SystemException, CommunicationException, NotFoundException,
            AuthorizationException
    {
        loadEnablements();
        Enablement[] elements = new Enablement[0];
        if(sessionProductClass != null)
        {
            List<Enablement> listByClass = enablementsByClass.get(sessionProductClass);
            if( listByClass != null )
            {
                elements = listByClass.toArray(elements);
            }
        }
        return elements;
    }

    public SessionClearingAcronym addSessionAcronym(SessionClearingAcronym sessionAcronym)
    {
        SessionClearingAcronym clonedAcronym;
        SessionClearingAcronym oldValue = null;
        if (sessionAcronym != null)
        {
            try
            {
                clonedAcronym = (SessionClearingAcronym) sessionAcronym.clone();
            }
            catch (CloneNotSupportedException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
                clonedAcronym = sessionAcronym;
            }
            oldValue = containsSessionAcronym(clonedAcronym.getSessionName());
            sessionClearingAcronyms.put(clonedAcronym.getSessionName(), clonedAcronym);
            if (oldValue != null)
            {
                if(!oldValue.equals(clonedAcronym))
                {
                    SessionClearingAcronymStruct[] structs = getUserDefinitionStruct().sessionClearingAcronyms;
                    for( SessionClearingAcronymStruct aSstruct : structs )
                    {
                        if(aSstruct.sessionName.equals(clonedAcronym.getSessionName()))
                        {
                            aSstruct.sessionClearingAcronym = clonedAcronym.getClearingAcronym();
                            break;
                        }
                    }
                    setModified(true);
                    firePropertyChange(SESSION_ACRONYM_CHANGE_EVENT, oldValue, sessionAcronym);
                }
            }
            else
            {
                SessionClearingAcronymStruct[] structs = getUserDefinitionStruct().sessionClearingAcronyms;
                SessionClearingAcronymStruct[] newStructs = new SessionClearingAcronymStruct[structs.length + 1];
                System.arraycopy(structs, 0, newStructs, 0, structs.length);
                newStructs[newStructs.length - 1] = new SessionClearingAcronymStruct(clonedAcronym.getSessionName(),
                                                                                     clonedAcronym.getClearingAcronym());
                getUserDefinitionStruct().sessionClearingAcronyms = newStructs;

                setModified(true);
                firePropertyChange(SESSION_ACRONYM_CHANGE_EVENT, oldValue, sessionAcronym);
            }
        }
        return oldValue;
    }

    public SessionClearingAcronym containsSessionAcronym(String sessionName)
    {
        SessionClearingAcronym scaToReturn = null;
        SessionClearingAcronym scaTemp = ( SessionClearingAcronym ) sessionClearingAcronyms.get(sessionName);
        if (scaTemp != null)
        {
            try
            {
                scaToReturn = (SessionClearingAcronym) scaTemp.clone();
            }
            catch (CloneNotSupportedException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
        }
        return scaToReturn;
    }

    public SessionClearingAcronym[] getAllSessionAcronyms()
    {
        if(sessionClearingAcronyms == null)
        {
            SessionClearingAcronymStruct[] structs = getUserDefinitionStruct().sessionClearingAcronyms;
            sessionClearingAcronyms = new HashMap(10);
            for (SessionClearingAcronymStruct aSstruct :  structs)
            {
                SessionClearingAcronym newObject = new SessionClearingAcronymImpl(aSstruct);
                sessionClearingAcronyms.put(newObject.getSessionName(), newObject);
            }
        }

        Collection values = sessionClearingAcronyms.values();

        SessionClearingAcronym[] returnValue = new SessionClearingAcronym[values.size()];
        Iterator acronymIt = values.iterator();
        for (int i = 0; acronymIt.hasNext(); ++i)
        {
            SessionClearingAcronym scaTemp = ( SessionClearingAcronym ) acronymIt.next();
            try
            {
                returnValue[i] = (SessionClearingAcronym) scaTemp.clone();
            }
            catch (CloneNotSupportedException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
                returnValue[i] = new SessionClearingAcronymImpl (scaTemp.getSessionName(), scaTemp.getClearingAcronym());
            }
        }

        return returnValue;
    }

    public SessionClearingAcronym removeSessionAcronym(SessionClearingAcronym sessionAcronym)
    {
        SessionClearingAcronym oldValue =
                ( SessionClearingAcronym ) sessionClearingAcronyms.remove(sessionAcronym.getSessionName());

        if(oldValue != null)
        {
            SessionClearingAcronymStruct[] structs = getUserDefinitionStruct().sessionClearingAcronyms;
            SessionClearingAcronymStruct[] newStructs = new SessionClearingAcronymStruct[structs.length - 1];
            int newStructsIndex = 0;
            for( SessionClearingAcronymStruct aStruct : structs )
            {
                if(!aStruct.sessionName.equals(sessionAcronym.getSessionName()))
                {
                    newStructs[newStructsIndex] = aStruct;
                    newStructsIndex++;
                }
            }
            getUserDefinitionStruct().sessionClearingAcronyms = newStructs;

            setModified(true);
            firePropertyChange(SESSION_ACRONYM_CHANGE_EVENT, oldValue, null);
        }

        return oldValue;
    }

    /**
     * remove all session acronym
     */
    public void removeAllSessionAcronym()
    {
        SessionClearingAcronym[] oldValue = getAllSessionAcronyms();
        
        getUserDefinitionStruct().sessionClearingAcronyms = new SessionClearingAcronymStruct[0];
        sessionClearingAcronyms = null;

        setModified(true);
        firePropertyChange(SESSION_ACRONYM_CHANGE_EVENT, oldValue, null);       
    }

    
    public SessionClearingAcronym updateSessionAcronym(SessionClearingAcronym sessionAcronym)
    {

        SessionClearingAcronym oldValue = addSessionAcronym(sessionAcronym);
        return oldValue;
    }

    public ExchangeAcronym getExchangeAcronym()
    {
        if (exchangeAcronym == null)
        {
            UserCollection userCollection = UserCollectionFactory.getImmutableQueryCollection();
            ExchangeAcronymStruct struct = getUserSummaryStruct().userAcronym;

            exchangeAcronym = userCollection.findExchangeAcronym(struct.exchange, struct.acronym);
            if(exchangeAcronym == null)
            {
                exchangeAcronym = ExchangeAcronymFactory.createExchangeAcronym(struct);
                if(isNeverBeenSaved())
                {
                    ((ExchangeAcronymModel) exchangeAcronym).setNeverBeenSaved(true);
                }
            }
        }
        return exchangeAcronym;
    }

    public ExchangeFirm getExchangeFirm()
    {
        ExchangeFirm exchangeFirm = null;
        FirmModel firmModel = getFirmModel();

        if(firmModel != null)
        {
            exchangeFirm = ExchangeFirmFactory.createExchangeFirm(firmModel.getFirmExchange(), firmModel.getFirmNumber());
        }
        return exchangeFirm;
    }

    public void setExchangeAcronym(ExchangeAcronym exchangeAcronym)
    {
        if (exchangeAcronym == null)
        {
            throw new IllegalArgumentException("ExchangeAcronym must not be null.");
        }
        ExchangeAcronym oldValue = getExchangeAcronym();
        if (!oldValue.equals(exchangeAcronym))
        {
            ExchangeAcronymStruct struct = exchangeAcronym.getExchangeAcronymStruct();
            getUserDefinitionStruct().userAcronym = struct;
            getUserSummaryStruct().userAcronym = struct;
            this.exchangeAcronym = exchangeAcronym;
            setModified(true);
            firePropertyChange(EXCHANGE_ACRONYM_CHANGE_EVENT, oldValue, exchangeAcronym);
        }
        else if(oldValue.isNeverBeenSaved() != exchangeAcronym.isNeverBeenSaved())
        {
            this.exchangeAcronym = exchangeAcronym;
            firePropertyChange(EXCHANGE_ACRONYM_CHANGE_EVENT, null, exchangeAcronym);
        }
    }

    /**
     * Gets the users default profile from the UserDefinitionStruct.
     * @return com.cboe.idl.cmiUser.Profile
     */
    public ProfileModel getDefaultProfile()
    {
        if(defaultProfile == null)
        {
            if(getUserDefinitionStruct().defaultProfile != null)
            {
                defaultProfile = ProfileModelFactory.createMutableProfile(getUserDefinitionStruct().defaultProfile);
            }
            else
            {
                GUILoggerHome.find().debug("UserAccountModelImpl.getDefaultProfile(): " +
                                           "UserDefinitionStruct.defaultProfile was null; creating new Profile",
                                           GUILoggerSABusinessProperty.USER_MANAGEMENT);
                defaultProfile =
                    ProfileModelFactory.createMutableProfile(ProfileModelFactory.createDefaultProfileStruct());
            }
        }

        ProfileModel clonedModel = null;
        try
        {
            clonedModel = ( ProfileModel ) defaultProfile.clone();
        }
        catch( CloneNotSupportedException e )
        {
            DefaultExceptionHandlerHome.find().process(e);
        }

        return clonedModel;
    }

    /**
     * Gets the default ProfileModel for the tradingSession, if it exists.  If the user doesn't have
     * a default for the tradingSession, then null is returned.
     * @param tradingSession
     * @return default ProfileModel for tradingSession
     */
    public ProfileModel getDefaultProfileForSession(String tradingSession)
    {
        ProfileModel retVal = null;

        int classKey = APIHome.findProductQueryAPI().getDefaultSessionProductClass(tradingSession).getClassKey();
        ProfileModel[] profiles = getProfiles();
        for(int i=0; i<profiles.length; i++)
        {
            if(profiles[i].getSessionName().equals(tradingSession) &&
                    profiles[i].getProductClass().getClassKey() == classKey)
            {
                try
                {
                    retVal = ( ProfileModel ) profiles[i].clone();
                }
                catch( CloneNotSupportedException e )
                {
                    DefaultExceptionHandlerHome.find().process(e);
                }

                if(GUILoggerHome.find().isDebugOn() &&
                   GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_SESSION))
                {
                    GUILoggerHome.find().debug(getClass().getName() + ".getDefaultProfileForSession('" +
                                               tradingSession + "')",
                                               GUILoggerSABusinessProperty.USER_SESSION,
                                               profiles[i].getProfileStruct());
                }
                break;
            }
        }
        if(retVal == null && GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(getClass().getName() + ".getDefaultProfileForSession()",
                                       GUILoggerSABusinessProperty.USER_SESSION,
                                       "No default profile found for session '" + tradingSession + '\'');
        }
        return retVal;
    }

    /**
     * Sets the users default profile for the UserDefinitionStruct.
     * @param profile Profile
     */
    public void setDefaultProfile(Profile profile)
    {
        if(profile.getProductClass().isDefaultProductClass() &&
           profile.getProductClass().isDefaultSession() &&
           profile.getAccount() != null &&
           profile.getSubAccount() != null &&
           profile.getExecutingGiveupFirm() != null)
        {
            SessionProfileStruct newStruct = profile.getProfileStruct();
            getUserDefinitionStruct().defaultProfile = newStruct;

            // setting the Profile to null will force it to be recreated from the struct the next time getDefaultProfile() is called
            defaultProfile = null;

            setModified(true);
            firePropertyChange(DEFAULT_PROFILE_CHANGE_EVENT, null, getDefaultProfile());
        }
        else
        {
            throw new IllegalArgumentException("Invalid Default Profile. ClassKey = " + profile.getClassKey() +
                                               ", Session = " + profile.getSessionName() +
                                                ", Account = " + profile.getAccount() +
                                                ", SubAccount = " + profile.getSubAccount() +
                                                ", ExecutingGiveupFirm = " + profile.getExecutingGiveupFirm().getFirm() +
                                                ", Exchange = " + profile.getExecutingGiveupFirm().getExchange() +
                                                ", OriginCode = " + profile.getOriginCode());
        }
    }

    /**
     * Gets the executing/give up firms from the UserDefinitionStruct.
     * @return com.cboe.interfaces.presentation.user.ExchangeFirm[]
     */
    public ExchangeFirm[] getExecutingGiveUpFirms()
    {
        ExchangeFirm[] firms = new ExchangeFirm[getUserDefinitionStruct().executingGiveupFirms.length];
        for(int i = 0; i < firms.length; i++)
        {
            firms[i] = ExchangeFirmFactory.createExchangeFirm(getUserDefinitionStruct().executingGiveupFirms[i]);
        }

        return firms;
    }

    /**
     * Sets the array of executing or give up firms for the UserDefinitionStruct.
     * @param firms com.cboe.interfaces.presentation.user.ExchangeFirm[]
     */
    public void setExecutingGiveUpFirms(ExchangeFirm[] firms)
    {
        if(!Arrays.equals(getExecutingGiveUpFirms(), firms))
        {
            ExchangeFirm[] oldFirms = getExecutingGiveUpFirms();

            ExchangeFirmStruct[] newStructs = new ExchangeFirmStruct[firms.length];
            for(int i=0; i<firms.length; i++)
            {
                newStructs[i] = cloneExchangeFirmStruct(firms[i].getExchangeFirmStruct());
            }
            getUserDefinitionStruct().executingGiveupFirms = newStructs;

            setModified(true);
            firePropertyChange(GIVEUP_FIRMS_CHANGE_EVENT, oldFirms, firms);
        }
    }
    
    /**
     * remove all executing or give up firms in the UserDefinitionStruct
     */
    public void removeAllExecutingGiveUpFirms()
    {
        ExchangeFirm[] oldFirms = getExecutingGiveUpFirms();
        if (oldFirms.length == 0)
            return;     // nothing to be removed
        
        getUserDefinitionStruct().executingGiveupFirms = new ExchangeFirmStruct[0];

        setModified(true);
        firePropertyChange(GIVEUP_FIRMS_CHANGE_EVENT, oldFirms, new ExchangeFirm[0]);       
    }

    public int getFirmKey()
    {
        return getUserSummaryStruct().firmKey;
    }

    public FirmModel getFirmModel()
    {
        FirmModel firm = null;

        try
        {
            firm = SystemAdminAPIFactory.find().getFirmByKey(getFirmKey());
        }
        catch(NotFoundException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Could not find Firm by Key:" + getFirmKey());
        }

        return firm;
    }

    public void setFirmKey(int firmKey)
    {
        if(getFirmKey() != firmKey)
        {
            Integer oldValue = new Integer(getFirmKey());
            getUserDefinitionStruct().firmKey = firmKey;
            getUserSummaryStruct().firmKey = firmKey;
            setModified(true);
            firePropertyChange(FIRM_KEY_CHANGE_EVENT, oldValue, new Integer(firmKey));

            // Update profiles affected by this firm change
            // Only profile with the account matching user acronym needs to be updated
            String acronym = getExchangeAcronym().getAcronym();
            ProfileModel [] profiles = getProfileModels();
            for (int i = 0; i < profiles.length; i++)
            {
                if (profiles[i].getAccount().equals(acronym))
                {
                    ExchangeFirm exchangeFirm =
                            ExchangeFirmFactory.createExchangeFirm(getFirmModel().getFirmExchange(),
                                                                   getFirmModel().getFirmNumber());
                    profiles[i].setExecutingGiveupFirm(exchangeFirm);
                    setProfile(profiles[i]);
                }
            }
            // Also need to perform this for a default profile
            SessionProfileStruct defaultProfile = getDefaultProfile().getProfileStruct();
            if (defaultProfile.account.equals(acronym))
            {
                defaultProfile.executingGiveupFirm.firmNumber = getFirmModel().getFirmNumber();
                defaultProfile.executingGiveupFirm.exchange = getFirmModel().getFirmExchange();
                setDefaultProfile(ProfileModelFactory.createMutableProfile(defaultProfile));
            }
        }
    }

    /**
     * Gets the user full name from the UserDefinitionStruct
     * @return user full name
     */
    public String getFullName()
    {
        return getUserSummaryStruct().fullName;
    }

    /**
     * Sets the user full name for the UserDefinitionStruct
     * @param name new user full name
     */
    public void setFullName(String name)
    {
        if(name == null)
        {
            throw new IllegalArgumentException("Full name must not be null.");
        }
        else
        {
            String oldValue = getFullName();

            if(!name.equals(oldValue))
            {
                getUserDefinitionStruct().fullName = name;
                getUserSummaryStruct().fullName = name;
                setModified(true);
                firePropertyChange(FULL_NAME_CHANGE_EVENT, oldValue, name);
            }
        }
    }

    /**
     * Gets the inactivation time for the struct
     * @return Calendar Calendar format of a <code>com.cboe.idl.cmiUtil.DateTimeStruct</code>
     */
    public Calendar getInactivationTime()
    {
        if(getUserDefinitionStruct().inactivationTime != null)
        {
            synchronized(dateWrapper)
            {
                dateWrapper.setDateTime(getUserDefinitionStruct().inactivationTime);
                return dateWrapper.getNewCalendar();
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the inactivation time for the struct.
     * @param newDateTime inactivation time
     */
    public void setInactivationTime(Calendar newDateTime)
    {
        if(newDateTime == null)
        {
            throw new IllegalArgumentException("Illegal calendar. Must be a non-null value.");
        }
        else
        {
            Calendar oldValue = getInactivationTime();

            if(!newDateTime.equals(oldValue))
            {
                synchronized(dateWrapper)
                {
                    dateWrapper.setDate(newDateTime);
                    getUserDefinitionStruct().inactivationTime = dateWrapper.toDateTimeStruct();
                }
                setModified(true);
                firePropertyChange(INACTIVATION_TIME_CHANGE_EVENT, oldValue, newDateTime);
            }
        }
    }

    /**
     * Gets the last modified time for the struct
     * @return Calendar Calendar format of a <code>com.cboe.idl.cmiUtil.DateTimeStruct</code>
     */
    public Calendar getLastModifiedTime()
    {
        if(getUserDefinitionStruct().lastModifiedTime != null)
        {
            synchronized(dateWrapper)
            {
                dateWrapper.setDateTime(getUserDefinitionStruct().lastModifiedTime);
                return dateWrapper.getNewCalendar();
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the last modified time for the struct.
     * @param newDateTime last modified time
     */
    public void setLastModifiedTime(Calendar newDateTime)
    {
        if(newDateTime == null)
        {
            throw new IllegalArgumentException("Illegal calendar. Must be a non-null value.");
        }
        else
        {
            Calendar oldValue = getLastModifiedTime();

            if(!newDateTime.equals(oldValue))
            {
                synchronized(dateWrapper)
                {
                    dateWrapper.setDate(newDateTime);
                    getUserDefinitionStruct().lastModifiedTime = dateWrapper.toDateTimeStruct();
                }
                setModified(true);
                firePropertyChange(LAST_MODIFIED_TIME_CHANGE_EVENT, oldValue, newDateTime);
            }
        }
    }

    /**
     * Gets the membership key from the UserDefinitionStruct
     * @return membership key
     */
    public int getMembershipKey()
    {
        return getUserDefinitionStruct().membershipKey;
    }

    /**
     * Sets the membership key for the UserDefinitionStruct
     * @param membershipKey new membership key
     */
    public void setMembershipKey(int membershipKey)
    {
        if(getMembershipKey() != membershipKey)
        {
            Integer oldValue = new Integer(getMembershipKey());
            getUserDefinitionStruct().membershipKey = membershipKey;
            setModified(true);
            firePropertyChange(MEMBERSHIP_KEY_CHANGE_EVENT, oldValue, new Integer(membershipKey));
        }
    }

    public String getUserId()
    {
        return getUserSummaryStruct().userId;
    }

    public void setUserId(String userId)
    {
        if(userId == null)
        {
            throw new IllegalArgumentException("UserId must not be null.");
        }
        else
        {
            String oldValue = getUserId();

            if(!userId.equals(oldValue))
            {
                getUserDefinitionStruct().userId = userId;
                getUserSummaryStruct().userId = userId;
                setModified(true);
                firePropertyChange(USER_ID_CHANGE_EVENT, oldValue, userId);
            }
        }
    }

    /**
     * Gets the Profiles for the UserDefinitionStruct
     * @return com.cboe.idl.cmiUser.Profile[]
     */
    public ProfileModel[] getProfileModels()
    {
        // lazily create ProfileModels
        if(profileModels == null)
        {
            SessionProfileUserDefinitionStruct user = getUserDefinitionStruct();
            profileModels = new ProfileModel[user.defaultSessionProfiles.length + user.sessionProfilesByClass.length];

            ProfileModel[] defaultSessionModels = null;

            if( user.defaultSessionProfiles.length > 0)
            {
                defaultSessionModels = ProfileModelFactory.createMutableProfiles(
                        getUserDefinitionStruct().defaultSessionProfiles);
                if(defaultSessionModels != null && defaultSessionModels.length > 0)
                {
                    System.arraycopy(defaultSessionModels, 0, profileModels, 0, defaultSessionModels.length);
                }
            }

            if( user.sessionProfilesByClass.length > 0 )
            {
                ProfileModel[] sessionClassModels = ProfileModelFactory.createMutableProfiles(
                        getUserDefinitionStruct().sessionProfilesByClass);
                if(sessionClassModels != null && sessionClassModels.length > 0)
                {
                    int destPos = defaultSessionModels == null ? 0 : defaultSessionModels.length;
                    System.arraycopy(sessionClassModels, 0, profileModels, destPos, sessionClassModels.length);
                }
            }
        }
        
        List<ProfileModel> clonedModels = new ArrayList<ProfileModel>(profileModels.length);
        for(ProfileModel profileModel : profileModels)
        {
            try
            {
                if(profileModel.getProductClass() != null)
                {
                    ProfileModel newProfileModel = (ProfileModel) profileModel.clone();
                    clonedModels.add(newProfileModel);
                }
            }
            catch(Exception e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
        }

        return clonedModels.toArray(new ProfileModel[0]);
    }

    public ProfileModel[] getProfiles()
    {
        return getProfileModels();
    }

    /**
     * Sets the Profiles for the UserDefinitionStruct
     * @param profiles com.cboe.idl.cmiUser.ProfileStruct[]
     */
    public void setProfiles(Profile[] profiles)
    {
        if(!Arrays.equals(getProfiles(), profiles))
        {
            setProfiles(profiles, true);
            setModified(true);
        }
    }

    /**
     * Updates or adds a profile.
     * @param profile Profile to add or update. If already exists for this user
     * it will be updated. If it does  not exist it will be added.
     */
    public void setProfile(Profile profile)
    {
        int profileIndex = getProfileIndex(profile.getProductClass());

        if ( profileIndex != -1 )
        {
            updateProfile(profile, true);
        }
        else
        {
            addProfile(profile);
        }
    }

    /**
     * Removes a profile.
     * @param profile Profile to remove.
     * @exception IllegalArgumentException thrown if profile does not exist.
     */
    public void removeProfile(Profile profile)
    {
        int profileIndex = getProfileIndex(profile.getProductClass());

        if ( profileIndex != -1 )
        {
            removeProfile(profileIndex);
        }
        else
        {
            throw new IllegalArgumentException("Unable to remove profile. Profile not fount in the list.");
        }
    }

    /**
     * Gets the user role code from the UserDefinitionStruct
     * @return user rol code
     */
    public Role getRole()
    {
        return RoleFactory.getByChar(getUserSummaryStruct().role);
    }

    /**
     * Sets the user role code for the UserDefinitionStruct
     * @param role new user role code
     */
    public void setRole(Role role)
    {
        if(getRole() != role)
        {
            // clean up unnecessary data for the new role
            switch (role)
            {
                case BROKER_DEALER:
                case CUSTOMER_BROKER_DEALER:
                case FIRM:
                case FIRM_DISPLAY:
                {
                    removeAllProfiles();
                    removeUserQRMProfile();
                    removeAllDPMs();
                    removeAllClasses();
                    break;
                }

                case MARKET_MAKER:
                case DPM:
                {
                    removeAllExecutingGiveUpFirms();
                    removeAllSessionAcronym();
                    break;
                }

                case TFL:
                case EXCHANGE_BROKER:
                case PRODUCT_MAINTENANCE:
                case CLASS_DISPLAY:
                case HELP_DESK:
                case HELPDESK_OMT:
                case BOOTH_OMT:
                case DISPLAY_OMT:
                case CROWD_OMT:
                case REPORTING:
                case OPRA:
                case UNKNOWN:
                {
                    removeAllProfiles();
                    removeUserQRMProfile();
                    removeAllDPMs();
                    removeAllExecutingGiveUpFirms();
                    removeAllSessionAcronym();
                    removeAllClasses();
                    break;
                }
            }

            Role oldValue = getRole();

            getUserDefinitionStruct().role = role.getRoleChar();
            getUserSummaryStruct().role = role.getRoleChar();
            setModified(true);
            firePropertyChange(ROLE_CHANGE_EVENT, oldValue, role);
        }
    }

    /**
     * Get the accounts assigned for this user.
     * @return JointAccount[]
     */
    public JointAccount[] getUserAccounts()
    {
        JointAccount[] models = new JointAccount[getUserDefinitionStruct().accounts.length];

        for(int i = 0; i < models.length; i++)
        {
            models[i] = new JointAccountImpl(getUserDefinitionStruct().accounts[i]);
        }

        return models;
    }

    /**
     * Adds the passed account to this users assigned accounts. See method
     * <code>addUserAccount(JointAccount, boolean)</code> for behaviour of
     * corresponding event fired.
     * @param account to add to the user. Guaranteed to only be added
     * to the current user once.
     */
    public void addUserAccount(JointAccount account)
    {
        addUserAccount(account, true);
    }

    /**
     * Determines if the passed account is assigned to this user.
     * @param account to find
     * @return Index into collection of found accounts. Will be >=0 if found, <0 if not found.
     */
    public int containsUserAccount(JointAccount account)
    {
        JointAccount[] accounts = getUserAccounts();
        for(int i = 0; i < accounts.length; i++)
        {
            if(accounts[i].equals(account))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Removes all accounts from this struct.
     */
    public void removeAllUserAccounts()
    {
        JointAccount[] oldAccounts = getUserAccounts();

        getUserDefinitionStruct().accounts = new AccountDefinitionStruct[0];
        setModified(true);

        firePropertyChange(JOINT_ACCOUNTS_CHANGE_EVENT, Arrays.asList(oldAccounts), Arrays.asList(new JointAccount[0]));
    }

    /**
     * Removes the passed account from this users assigned accounts.
     * @param account to remove from the user
     */
    public void removeUserAccount(JointAccount account)
    {
        int foundIndex = containsUserAccount(account);
        AccountDefinitionStruct[] newAccounts;

        if(foundIndex >= 0)
        {
            newAccounts = new AccountDefinitionStruct[getUserDefinitionStruct().accounts.length - 1];

            System.arraycopy(getUserDefinitionStruct().accounts, 0, newAccounts, 0, foundIndex);
            System.arraycopy(getUserDefinitionStruct().accounts, foundIndex + 1, newAccounts, foundIndex, getUserDefinitionStruct().accounts.length - foundIndex - 1);

            JointAccount[] oldAccounts = getUserAccounts();

            getUserDefinitionStruct().accounts = newAccounts;
            setModified(true);

            firePropertyChange(JOINT_ACCOUNTS_CHANGE_EVENT, Arrays.asList(oldAccounts), Arrays.asList(getUserAccounts()));
        }
    }

    public boolean isUserDetailsAvailable()
    {
        getUserDefinitionStruct();
        return userDetailsAvailable;
    }

    public UserSummaryStruct getUserSummaryStruct()
    {
        if(userSummaryStruct == null)
        {
            UserSummaryStruct newSummaryStruct;
            if(!isNeverBeenSaved())
            {
                try
                {
                    newSummaryStruct = SystemAdminAPIFactory.find().getUserSummaryStructByUserId(user.userId);

                    if(newSummaryStruct == null)
                    {
                        GUILoggerHome.find().alarm(getClass().getName() + ":Could not obtain summary struct from API. " +
                                                   "Creating manually.");
                        newSummaryStruct = UserCollectionFactory.createUserSummaryStruct(user);
                    }
                }
                catch(UserException e)
                {
                    DefaultExceptionHandlerHome.find().process(e, "User Summary was not available. " +
                                                                  "Default values will be displayed!");
                    newSummaryStruct = UserCollectionFactory.createUserSummaryStruct(user);
                }
            }
            else
            {
                newSummaryStruct = UserCollectionFactory.createUserSummaryStruct(user);
            }
            userSummaryStruct = newSummaryStruct;
        }
        return userSummaryStruct;
    }

    public SessionProfileUserDefinitionStruct getUserDefinitionStruct()
    {
        if(user == null || !userDetailsAvailable)
        {
            userDetailsAvailable = false;
            SessionProfileUserDefinitionStruct userDetails;
            try
            {
                userDetails = SystemAdminAPIFactory.find().getUserStructByKey(userSummaryStruct.userKey);
                userDetailsAvailable = true;
            }
            catch(UserException e)
            {
                DefaultExceptionHandlerHome.find().process(e, "User Details were not available. " +
                                                              "Default values will be displayed!");
                userDetails = UserCollectionFactory.createNewDefaultStruct();
            }
            user = userDetails;
        }
        return user;
    }

    /**
     * Sets the UserStruct that this model represents.
     * @param newUser New struct for this model to represent.
     * @param fireEvent whether or not to fire property change event
     */
    public void setUserDefinitionStruct(SessionProfileUserDefinitionStruct newUser, boolean fireEvent,
                                        boolean resetUserSummaryStruct)
    {
        SessionProfileUserDefinitionStruct oldUser = user;

        if(oldUser != newUser)
        {
            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
            {
                GUILoggerHome.find().debug(getClass().getName() +
                                           ":setUserDefinitionStruct() Old SessionProfileUserDefinitionStruct",
                                           GUILoggerSABusinessProperty.USER_MANAGEMENT, oldUser);
            }

            if(newUser == null)
            {
                throw new IllegalArgumentException("SessionProfileUserDefinitionStruct may not be null.");
            }

            user = newUser;

            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
            {
                GUILoggerHome.find().debug(getClass().getName() +
                                           ":setUserDefinitionStruct() New SessionProfileUserDefinitionStruct",
                                           GUILoggerSABusinessProperty.USER_MANAGEMENT, newUser);
            }

            defaultProfile = null;
            setUserQRMProfile(null);
            exchangeAcronym = null;
            profileModels = null;
            sessionClearingAcronyms = null;
            isClassAssignmentsChanged = false;

            if(resetUserSummaryStruct)
            {
                userSummaryStruct = null;
            }

            isNew = false;
            setModified(false);

            if(fireEvent)
            {
                firePropertyChange(STRUCT_CHANGE_EVENT, oldUser, user);
            }

            if(isEnablementsLoaded)
            {
                initializeEnablementStructures(false);
            }
        }
    }

    /**
     * Sets the UserStruct that this model represents.
     * @param newUser New struct for this model to represent.
     */
    public void setUserDefinitionStruct(SessionProfileUserDefinitionStruct newUser)
    {
        setUserDefinitionStruct(newUser, true, true);
    }

    public SessionProductClass[] getSessionProductClasses()
    {
        MarketMakerClassAssignmentStruct[] assignments = getUserDefinitionStruct().assignedClasses;
        List<SessionProductClass> tempList = new ArrayList<SessionProductClass>(assignments.length);

        for(MarketMakerClassAssignmentStruct assignment : assignments)
        {
            try
            {
                SessionProductClass spc = ProductHelper.getSessionProductClassCheckInvalid(assignment.sessionName,
                                                                                           assignment.classKey);
                tempList.add(spc);
            }
            catch(UserException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
        }

        return tempList.toArray(new SessionProductClass[0]);
    }

    /**
     * Gets the user key from the UserDefinitionStruct
     * @return user key
     */
    public int getUserKey()
    {
        return getUserSummaryStruct().userKey;
    }

    public UserQuoteRiskManagementProfile reloadUserQRMProfile()
    {
        userQRMProfile = null;
        return getUserQRMProfile();
    }

    /**
     * Gets the users Quote risk management profile.
     * @return com.cboe.presentation.commonBusiness.UserQuoteRiskManagementProfile
     */
    public UserQuoteRiskManagementProfile getUserQRMProfile()
    {
        if(userQRMProfile == null && !isNeverBeenSaved())
        {
//            String acronym = getAcronym();
            if(getUserId() != null)
            {
                try
                {
                    UserQuoteRiskManagementProfile newUserQRMProfile;

                    UserQuoteRiskManagementProfileStruct profileStruct = GUIUserTradingParametersAPIHome.find().getAllQuoteRiskProfiles(getUserId());
                    newUserQRMProfile = new UserQuoteRiskManagementProfileImpl(profileStruct);
                    newUserQRMProfile.setUserId(getUserId());

                    setUserQRMProfile(newUserQRMProfile);

                    //Add UserAccountModel as a PropertyChangeListener to the UserQRMProfile
                    newUserQRMProfile.addPropertyChangeListener(this);
                }
                catch(UserException e)
                {
                    DefaultExceptionHandlerHome.find().process(e);
                }
            }
        }

        return userQRMProfile;
    }

    /**
     * Sets the users Quote risk management profile.
     * @param newUserQRMProfile com.cboe.presentation.commonBusiness.UserQuoteRiskManagementProfile
     */
    public void setUserQRMProfile(UserQuoteRiskManagementProfile newUserQRMProfile)
    {
        userQRMProfile = newUserQRMProfile;
    }
    
    public void removeUserQRMProfile()
    {
        setUserQRMProfile(new UserQuoteRiskManagementProfileImpl(getUserId()));
        userQRMProfile.setModified(true);
    }

    /**
     * Gets the user type code from the UserDefinitionStruct
     * @return user type
     */
    public short getUserType()
    {
        return getUserSummaryStruct().userType;
    }

    /**
     * Sets the user type code for the UserDefinitionStruct
     * @param userType new user type code
     */
    public void setUserType(short userType)
    {
        switch(userType)
        {
            case UserTypes.DPM_ACCOUNT:
            case UserTypes.FIRM_LOGIN:
            case UserTypes.HELP_DESK:
            case UserTypes.INDIVIDUAL_ACCOUNT:
            case UserTypes.JOINT_ACCOUNT:
            case UserTypes.SYSTEMS_OPERATIONS:
                if(getUserType() != userType)
                {
                    Short oldValue = new Short(getUserType());
                    getUserDefinitionStruct().userType = userType;
                    getUserSummaryStruct().userType = userType;
                    setModified(true);
                    firePropertyChange(USER_TYPE_CHANGE_EVENT, oldValue, new Short(userType));
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid user type = " + userType);
        }
    }

    /**
     * Gets the assigned class keys for the struct.
     * @return int[] a sequence of user assigned class keys
     */
    public int[] getClassKeys()
    {
        int[] models = new int[getUserDefinitionStruct().assignedClasses.length];

        for(int i = 0; i < models.length; i++)
        {
            models[i] = getUserDefinitionStruct().assignedClasses[i].classKey;
        }

        return models;
    }

    public MMClassAssignmentModel[] getClassAssignments()
    {
        MMClassAssignmentModel[] models = new MMClassAssignmentModelImpl[getUserDefinitionStruct().assignedClasses.length];

        for(int i = 0; i < models.length; i++)
        {
            models[i] = new MMClassAssignmentModelImpl(getUserDefinitionStruct().assignedClasses[i]);
        }

        return models;
    }

    /**
     * Adds the passed class key to this users assign classes. See method
     * setClasses for behaviour of corresponding event fired.
     * @param spc SessionProductClass to add to the user. Guaranteed to only be added
     */
    public void addClass(SessionProductClass spc)
    {
        addClass(spc, true);
    }

    /* Gets the users assigned classes.
     * @return array of ProductClass's this user is assigned.
     */
    public ProductClass[] getProductClasses()
    {
        return this.getSessionProductClasses();
    }

    /**
     * Determines if the passed classKey is assigned to this user. As a side effect of calling this method
     * the class keys in the struct will be sorted.
     * @param classKey to find
     * @return Index into collection of found class. Will be >=0 if found, <0 if not found.
     */
    public int containsClassKey(int classKey)
    {
        MarketMakerClassAssignmentStruct[] assignment = getUserDefinitionStruct().assignedClasses;
        for(int i=0; i<assignment.length; i++)
        {
            if (assignment[i].classKey == classKey)

            {
                return i;
            }
        }

        return -1;
    }

    /**
     * Determines if the passed classKey is assigned to this user. As a side effect of calling this method
     * the class keys in the struct will be sorted.
     * @param spc SessionProductClass to find
     * @return Index into collection of found class. Will be >=0 if found, <0 if not found.
     */
    public int containsClassKey(SessionProductClass spc)
    {
        MarketMakerClassAssignmentStruct[] assignment = getUserDefinitionStruct().assignedClasses;
        for(int i=0; i<assignment.length; i++)
        {
            if (assignment[i].sessionName.equals(spc.getTradingSessionName()) &&
                assignment[i].classKey == spc.getClassKey())
            {
                return i;
            }
        }

        return -1;
    }

    public MMClassAssignmentModel assignmentTypeFor(SessionProductClass spc)
    {
        int index = containsClassKey(spc);

        if (index > -1)
        {
            return new MMClassAssignmentModelImpl(getUserDefinitionStruct().assignedClasses[index]);
        }

        return null;
    }

    /**
     * Removes all classes from this struct.
     */
    public void removeAllClasses()
    {
        MarketMakerClassAssignmentStruct[] oldClassKeys = getUserDefinitionStruct().assignedClasses;

        getUserDefinitionStruct().assignedClasses = new MarketMakerClassAssignmentStruct[0];
        isClassAssignmentsChanged = true;
        setModified(true);

        MMClassAssignmentModel[] oldClassKeyOBJs = new MMClassAssignmentModelImpl[oldClassKeys.length];
        for(int i = 0; i < oldClassKeys.length; i++)
        {
            oldClassKeyOBJs[i] = new MMClassAssignmentModelImpl(oldClassKeys[i]);
        }

        MMClassAssignmentModel[] emptyList = new MMClassAssignmentModelImpl[0];
        firePropertyChange(CLASS_KEYS_CHANGE_EVENT, Arrays.asList(oldClassKeyOBJs), Arrays.asList(emptyList));
    }

    /**
     * Removes the passed class key from this users assigned classes. See method
     * setClasses for behaviour of corresponding event fired.
     * @param spc SessionProductClass to remove from the user
     */
    public void removeClass(SessionProductClass spc)
    {
        int foundIndex = containsClassKey(spc);
        MarketMakerClassAssignmentStruct[] newClassKeys;

        if(foundIndex >= 0)
        {
            newClassKeys = new MarketMakerClassAssignmentStruct[getUserDefinitionStruct().assignedClasses.length - 1];

            System.arraycopy(getUserDefinitionStruct().assignedClasses, 0, newClassKeys, 0, foundIndex);
            System.arraycopy(getUserDefinitionStruct().assignedClasses, foundIndex + 1, newClassKeys, foundIndex,
                             getUserDefinitionStruct().assignedClasses.length - foundIndex - 1);

            MarketMakerClassAssignmentStruct[] oldClassKeys = getUserDefinitionStruct().assignedClasses;

            getUserDefinitionStruct().assignedClasses = newClassKeys;
            isClassAssignmentsChanged = true;
            setModified(true);

            MMClassAssignmentModel[] oldClassKeyOBJs = new MMClassAssignmentModelImpl[oldClassKeys.length];
            for(int i = 0; i < oldClassKeys.length; i++)
            {
                oldClassKeyOBJs[i] = new MMClassAssignmentModelImpl(oldClassKeys[i]);
            }

            MMClassAssignmentModel[] newClassKeyOBJs = new MMClassAssignmentModelImpl[newClassKeys.length];
            for(int i = 0; i < newClassKeys.length; i++)
            {
                newClassKeyOBJs[i] = new MMClassAssignmentModelImpl(newClassKeys[i]);
            }

            firePropertyChange(CLASS_KEYS_CHANGE_EVENT, Arrays.asList(oldClassKeyOBJs), Arrays.asList(newClassKeyOBJs));
        }
    }

    /**
     * Gets the DPM accounts that this user is assigned to.
     * @return DPMModel sequence
     */
    public DPMModel[] getAssignedDPMs()
    {
        DPMModel[] models = new DPMStructModel[getUserDefinitionStruct().dpms.length];

        for(int i = 0; i < models.length; i++)
        {
            models[i] = new DPMStructModel(getUserDefinitionStruct().dpms[i]);
        }

        return models;
    }

    /**
     * Adds the passed DPM to this users assigned DPM's. See method
     * <code>addDPM(DPMModel, boolean)</code> for behaviour of
     * corresponding event fired.
     * @param dpm to add to the user. Guaranteed to only be added
     * to the current user once.
     */
    public void addDPM(DPMModel dpm)
    {
        addDPM(dpm, true);
    }

    /**
     * Determines if the passed DPM is assigned to this user.
     * @param dpm to find
     * @return Index into collection of found accounts. Will be >=0 if found, <0 if not found.
     */
    public int containsDPM(DPMModel dpm)
    {
        DPMModel[] dpms = getAssignedDPMs();
        for(int i = 0; i < dpms.length; i++)
        {
            if(dpms[i].equals(dpm))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Removes all accounts from this struct.
     */
    public void removeAllDPMs()
    {
        DPMModel[] oldDPMs = getAssignedDPMs();

        getUserDefinitionStruct().dpms = new DpmStruct[0];
        setModified(true);

        firePropertyChange(DPM_ACCOUNTS_CHANGE_EVENT, Arrays.asList(oldDPMs), Arrays.asList(getAssignedDPMs()));
    }

    /**
     * Removes the passed DPM from this users assigned DPM's.
     * @param dpm to remove from the user
     */
    public void removeDPM(DPMModel dpm)
    {
        int foundIndex = containsDPM(dpm);
        DpmStruct[] newDPMs;

        if(foundIndex >= 0)
        {
            newDPMs = new DpmStruct[getUserDefinitionStruct().dpms.length - 1];

            System.arraycopy(getUserDefinitionStruct().dpms, 0, newDPMs, 0, foundIndex);
            System.arraycopy(getUserDefinitionStruct().dpms, foundIndex + 1, newDPMs, foundIndex,
                             getUserDefinitionStruct().dpms.length - foundIndex - 1);

            DPMModel[] oldDPMs = getAssignedDPMs();

            getUserDefinitionStruct().dpms = newDPMs;
            setModified(true);

            firePropertyChange(DPM_ACCOUNTS_CHANGE_EVENT, Arrays.asList(oldDPMs), Arrays.asList(getAssignedDPMs()));
        }
    }
    
    /**
     * Gets the Session isActive flag
     * @return boolean True if active, false otherwise.
     */
    public boolean isActive()
    {
        return getUserSummaryStruct().isActive;
    }

    /**
     * Sets the Session isActive flag
     * @param active True if template is enabled to become active, false otherwise.
     */
    public void setActive(boolean active)
    {
        if(active != isActive())
        {
            Boolean oldValue = Boolean.valueOf(isActive());
            getUserDefinitionStruct().isActive = active;
            getUserSummaryStruct().isActive = active;
            setModified(true);
            firePropertyChange(ACTIVE_CHANGE_EVENT, oldValue, Boolean.valueOf(active));
        }
    }

    /**
     * Determines if the underlying struct has been modified.
     * @return True if it has been modified, false otherwise.
     */
    public boolean isModified()
    {
        return isModified;
    }

    public void setBeingDeleted(boolean deleting)
    {
        beingDeleted = deleting;
    }

    public boolean isBeingDeleted()
    {
        return beingDeleted;
    }

    /**
     * Determines if the underlying struct has never been saved.
     * @return True if it has never been saved to the API, false otherwise.
     */
    public boolean isNeverBeenSaved()
    {
        return isNew;
    }

    /**
     * Fires abort event. Will only execute if newUser and oldUser are not null.
     * Also nulls these values out after completion.
     * This is done so that this can only be called after abortChanges, with
     * no changes to oldUser or newUser permitted other than from that method.
     */
    public void fireAbortPropertyChange()
    {
        if((oldUser != null) && (newUser != null))
        {
            firePropertyChange(ABORT_CHANGE_EVENT, oldUser, newUser);

            //ALWAYS null these out so they are not left in undefined state
            newUser = null;
            oldUser = null;
        }
    }

    /**
     * Aborts any changes in the underlying struct within this model to the API.
     */
    public void abortChanges()
            throws CommunicationException, DataValidationException, NotFoundException,
                   SystemException, AuthorizationException
    {
        if(isModified() && !isNeverBeenSaved())
        {
            newUser = SystemAdminAPIFactory.find().getUserStructByKey(getUserKey());
            oldUser = getUserDefinitionStruct();

            SwingEventThreadWorker worker = new SwingEventThreadWorker(true)
            {
                public Object process() throws Exception
                {
                    setUserDefinitionStruct(newUser, false, true);
                    return null;
                }
            };

            try
            {
                worker.doProcess();
            }
            catch( InvocationTargetException e )
            {
                DefaultExceptionHandlerHome.find().process( e );
            }
            catch( InterruptedException e )
            {
                DefaultExceptionHandlerHome.find().process( e );
            }

            if(isEnablementsLoaded)
            {
                initializeEnablementStructures(true);
            }

            if (parProfilesLoaded)
            {
                loadPARBrokerProfilesFromServer();
            }
        }
    }

    /**
     * Saves any changes in the underlying struct within this model to the API.
     *
     * @exception CommunicationException
     * @exception DataValidationException
     * @exception TransactionFailedException
     * @exception SystemException
     * @exception AuthorizationException
     * @exception NotFoundException
     * @exception AlreadyExistsException
     */
    public void saveChanges()
            throws CommunicationException, DataValidationException, TransactionFailedException, SystemException,
                   AuthorizationException, NotFoundException, AlreadyExistsException
    {
        if(isModified())
        {
            StringBuffer debugMessage = new StringBuffer(100);
            String windowTitle = null;
            if(GUILoggerHome.find().isDebugOn())
            {
                debugMessage.append("UserID: ").append(getUserId());
                debugMessage.append(" UserName: ").append(getFullName());
            }

            if(isNeverBeenSaved())
            {
                SessionProfileStruct defaultProfile = getDefaultProfile().getProfileStruct();
                defaultProfile.account = getUserId();

                setDefaultProfile(ProfileModelFactory.createMutableProfile(defaultProfile));

                SystemAdminAPIFactory.find().addUser(this);

                if(GUILoggerHome.find().isDebugOn())
                {
                    debugMessage.insert(0, "New user was added: ");
                    windowTitle = "User Created";
                }

                isNew = false;
            }
            else
            {
                SystemAdminAPIFactory.find().updateUser(this);
                isClassAssignmentsChanged = false;

                if(GUILoggerHome.find().isDebugOn())
                {
                    debugMessage.insert(0, "User was modified: ");
                    windowTitle = "User Modified";
                }
            }

            if(GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug(windowTitle + debugMessage.toString(),
                                           GUILoggerSABusinessProperty.USER_MANAGEMENT, getUserDefinitionStruct());
            }

            setModified(false);

            if (userQRMProfile != null)
            {
                userQRMProfile.save();
            }

            if (!isClassAssignmentsChanged())
            {
                saveEnablements();
            }

            saveUserFirmAffiliation();
            savePARBrokerProfiles();
        }
    }

    /**
     * Fires save event.
     * This should  be called after saveChanges.
     */
    public void fireSavePropertyChange()
    {
        firePropertyChange(SAVED_CHANGE_EVENT, Boolean.TRUE, Boolean.FALSE);
    }

    /**
     * Add the listener for property changes to the User attributes.
     * @param newListener PropertyChangeListener to receive a callback when a User
     * property is changed.
     */
    public void addPropertyChangeListener(PropertyChangeListener newListener)
    {
        // First check to make sure this listener is not already listening
        PropertyChangeListener[] listeners = propertyEventManager.getPropertyChangeListeners();
        for (int i = 0; i < listeners.length ;i++ )
        {
            PropertyChangeListener listener = listeners[i];
            if ( listener == newListener )
            {
                return;
            }
        }
        propertyEventManager.addPropertyChangeListener(newListener);
    }

    /**
     * Removes the listener for property changes to the User attributes.
     * @param listener PropertyChangeListener to remove from receiving callbacks when a User
     * property is changed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyEventManager.removePropertyChangeListener(listener);
    }

    /**
     * Handles propertyChanges for User's QRM profile. Propagates event to this
     * objects listeners.
     */
    public void propertyChange(PropertyChangeEvent event)
    {
        if(event.getSource() == getUserQRMProfile() || event.getSource() instanceof PARBrokerProfile)
        {
            // the PARProfilePreferenceNames.BrokerPassword.prefName is a special case which we can
            // ignore, because that preference will only be added/removed from the PARBrokerProfile
            // during the import from a PAR server (which will only happen during the rollout phase
            // of the PAR Server removal project).  The PARProfilePreferenceNames.BrokerPassword.prefName
            // preference will always be removed from the PARBrokerProfile during the initial save
            // of the profile (see PARBrokerProfileImpl.savePreferences())
            if (!event.getPropertyName().endsWith(PARProfilePreferenceNames.BrokerPassword.prefName))
            {
                setModified(true);
                firePropertyChange(event.getPropertyName(), event.getOldValue(), event.getNewValue());
            }
        }
    }

    public void setAssignmentType(SessionProductClass spc, short assignmentType)
    {
        int index = containsClassKey(spc);
        if (index > -1)
        {
            if (getUserDefinitionStruct().assignedClasses[index].assignmentType != assignmentType )
            {
                getUserDefinitionStruct().assignedClasses[index].assignmentType = assignmentType;
                isClassAssignmentsChanged = true;
                setModified(true);

                propertyEventManager.firePropertyChange(CLASS_KEYS_CHANGE_EVENT, null, null);
            }
        }
    }

    /**
     * Fires a property change to the event manager. Typically, do not need to
     * call this, as model updates do so. However, may be called if separation
     * between notified components and model updates is desired.
     *
     * @param property must be recognized property from UserAccountModel.
     * @param oldValue old value.
     * @param newValue new value.
     */
    public void firePropertyChange(String property, Object oldValue, Object newValue)
    {
        propertyEventManager.firePropertyChange(property, oldValue, newValue);
    }

    public boolean isClassAssignmentsChanged()
    {
        return isClassAssignmentsChanged;
    }

    public boolean isEnablementsChanged()
    {
        return isEnablementsChanged;
    }

    /**
     * Adds the passed class key to this users assign classes, optionally firing an event for the change.
     * @param spc SessionProductClass to add to the user. Guaranteed to only be added to the current session once.
     */
    private void addClass(SessionProductClass spc, boolean fireEvent)
    {
        MarketMakerClassAssignmentStruct[] oldClassKeys = new MarketMakerClassAssignmentStruct[0];

        int foundIndex = containsClassKey(spc);
        MarketMakerClassAssignmentStruct[] newClassKeys;

        if(foundIndex < 0)
        {
            newClassKeys = new MarketMakerClassAssignmentStruct[getUserDefinitionStruct().assignedClasses.length + 1];

            System.arraycopy(getUserDefinitionStruct().assignedClasses, 0, newClassKeys, 0,
                             getUserDefinitionStruct().assignedClasses.length);

            MarketMakerClassAssignmentStruct newClass = new MarketMakerClassAssignmentStruct();
            newClass.classKey = spc.getClassKey();
            newClass.assignmentType = MarketMakerClassAssignmentTypes.NOT_APPLICABLE;
            newClass.sessionName = spc.getTradingSessionName();

            newClassKeys[newClassKeys.length - 1] = newClass;

            if(fireEvent)
            {
                oldClassKeys = getUserDefinitionStruct().assignedClasses;
            }

            getUserDefinitionStruct().assignedClasses = newClassKeys;
            isClassAssignmentsChanged = true;
            setModified(true);

            if(fireEvent)
            {
                MMClassAssignmentModel[] oldClassKeyOBJs = new MMClassAssignmentModelImpl[oldClassKeys.length];
                for(int i = 0; i < oldClassKeys.length; i++)
                {
                    oldClassKeyOBJs[i] = new MMClassAssignmentModelImpl(oldClassKeys[i]);
                }

                MMClassAssignmentModel[] newClassKeyOBJs = new MMClassAssignmentModelImpl[newClassKeys.length];
                for(int i = 0; i < newClassKeys.length; i++)
                {
                    newClassKeyOBJs[i] = new MMClassAssignmentModelImpl(newClassKeys[i]);
                }

                firePropertyChange(CLASS_KEYS_CHANGE_EVENT, Arrays.asList(oldClassKeyOBJs), Arrays.asList(newClassKeyOBJs));
            }
        }
    }

    /**
     * Adds the passed AccountDefinitionStruct to this users assigned accounts, optionally firing an event for the change.
     * @param account to add to the user. Guaranteed to only be added to the current user once.
     * @param fireEvent True if propertyChangeEvent should be fired for this addition.
     */
    private void addUserAccount(JointAccount account, boolean fireEvent)
    {
        int foundIndex = containsUserAccount(account);

        if(foundIndex < 0)
        {
            JointAccount[] oldAccounts = new JointAccount[0];
            AccountDefinitionStruct[] newAccounts;

            newAccounts = new AccountDefinitionStruct[getUserDefinitionStruct().accounts.length + 1];

            System.arraycopy(getUserDefinitionStruct().accounts, 0, newAccounts, 0,
                             getUserDefinitionStruct().accounts.length);
            newAccounts[newAccounts.length - 1] = account.getAccountStruct();

            if(fireEvent)
            {
                oldAccounts = getUserAccounts();
            }

            getUserDefinitionStruct().accounts = newAccounts;
            setModified(true);

            if(fireEvent)
            {
                firePropertyChange(JOINT_ACCOUNTS_CHANGE_EVENT, Arrays.asList(oldAccounts),
                                   Arrays.asList(getUserAccounts()));
            }
        }
    }

    /**
     * Adds the passed DPMModel to this users assigned DPMs, optionally firing an event for the change.
     * @param dpm to add to the user. Guaranteed to only be added to the current user once.
     * @param fireEvent True if propertyChangeEvent should be fired for this addition.
     */
    private void addDPM(DPMModel dpm, boolean fireEvent)
    {
        int foundIndex = containsDPM(dpm);

        if(foundIndex < 0)
        {
            DPMModel[] oldDPMs = new DPMStructModel[0];
            DpmStruct[] newDPMs;

            newDPMs = new DpmStruct[getUserDefinitionStruct().dpms.length + 1];

            System.arraycopy(getUserDefinitionStruct().dpms, 0, newDPMs, 0, getUserDefinitionStruct().dpms.length);
            newDPMs[newDPMs.length - 1] = dpm.getDpmStruct();

            if(fireEvent)
            {
                oldDPMs = getAssignedDPMs();
            }

            getUserDefinitionStruct().dpms = newDPMs;
            setModified(true);

            if(fireEvent)
            {
                firePropertyChange(DPM_ACCOUNTS_CHANGE_EVENT, Arrays.asList(oldDPMs), Arrays.asList(getAssignedDPMs()));
            }
        }
    }

    /**
     * Clones an ExchangeFirmStruct returning the clone.
     * @param exchange com.cboe.idl.cmiUser.ExchangeFirmStruct
     * @return com.cboe.idl.cmiUser.ExchangeFirmStruct
     */
    private ExchangeFirmStruct cloneExchangeFirmStruct(ExchangeFirmStruct exchange)
    {
        ExchangeFirmStruct newExchange = null;

        if ( exchange != null )
        {
            newExchange = new ExchangeFirmStruct();
            newExchange.exchange = exchange.exchange;
            newExchange.firmNumber = exchange.firmNumber;
        }

        return newExchange;
    }

    /**
     * Gets the index of the profile for the matching class with the classKey passed.
     * @param productClass for profile
     * @return index of profile
     */
    private int getProfileIndex(SessionProductClass productClass)
    {
        int index = -1;
        Profile[] profiles = getProfiles();

        if (profiles != null && productClass != null)
        {
            for (int i = 0; i < profiles.length; i++)
            {
                if (profiles[i].getProductClass().equals(productClass))
                {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    private SessionProfileStruct[] getStructsForProfiles(Profile[] profiles)
    {
        SessionProfileStruct[] structs = new SessionProfileStruct[profiles.length];

        for(int i = 0; i < profiles.length; i++)
        {
            // Profile.getProfileStruct() returns a clone of the original struct, so
            //    don't have to clone the structs here; if the structs' fields are
            //    edited it won't affect the original Profile
            structs[i] = profiles[i].getProfileStruct();
        }
        return structs;
    }

    /**
     * Adds the passed profile to existing profiles.
     * @param newProfile com.cboe.interfaces.presentation.user.Profile
     */
    private void addProfile(Profile newProfile)
    {
        ProfileModel[] oldProfiles = getProfiles();
        int length = (oldProfiles != null) ? oldProfiles.length : 0;
        ProfileModel[] newProfiles = new ProfileModel[length + 1];

        if( length > 0 )
        {
            System.arraycopy(oldProfiles, 0, newProfiles, 0, length);
        }

        // add the newProfile to the end of the array
        newProfiles[length] = ProfileModelFactory.createMutableProfile(newProfile);

        SessionProductClass productClassAdded = newProfile.getProductClass();
        SessionProfileStruct[] oldStructs;

        if( productClassAdded.isDefaultProductClass() )
        {
            oldStructs = getUserDefinitionStruct().defaultSessionProfiles;
        }
        else
        {
            oldStructs = getUserDefinitionStruct().sessionProfilesByClass;
        }

        SessionProfileStruct[] newStructs = new SessionProfileStruct[oldStructs.length + 1];
        System.arraycopy(oldStructs, 0, newStructs, 0, oldStructs.length);
        newStructs[oldStructs.length] = newProfile.getProfileStruct();

        if( productClassAdded.isDefaultProductClass() )
        {
            getUserDefinitionStruct().defaultSessionProfiles = newStructs;
        }
        else
        {
            getUserDefinitionStruct().sessionProfilesByClass = newStructs;
        }

        profileModels = newProfiles;

        setModified(true);

        firePropertyChange(PROFILES_CHANGE_EVENT, oldProfiles, newProfiles);
    }

    /**
     * Removes the profile at index.
     * @param index of profile to remove.
     */
    private void removeProfile(int index)
    {
        ProfileModel[] oldModels = profileModels;

        ProfileModel modelRemoved = profileModels[index];
        SessionProductClass productClassRemoved = modelRemoved.getProductClass();

        ProfileModel[] newModels = new ProfileModel[oldModels.length - 1];

        System.arraycopy(oldModels, 0, newModels, 0, index);
        System.arraycopy(oldModels, index + 1, newModels, index, oldModels.length - index -1);

        SessionProfileStruct[] oldProfiles;

        if(productClassRemoved.isDefaultProductClass())
        {
            oldProfiles = getUserDefinitionStruct().defaultSessionProfiles;
        }
        else
        {
            oldProfiles = getUserDefinitionStruct().sessionProfilesByClass;
        }

        SessionProfileStruct[] newProfiles = new SessionProfileStruct[oldProfiles.length - 1];

        for( int i = 0; i < oldProfiles.length; i++ )
        {
            SessionProfileStruct oldProfile = oldProfiles[i];
            if( oldProfile.classKey == modelRemoved.getClassKey() &&
                    oldProfile.sessionName.equals(modelRemoved.getSessionName()))
            {
                System.arraycopy(oldProfiles, 0, newProfiles, 0, i);
                System.arraycopy(oldProfiles, i + 1, newProfiles, i, oldProfiles.length - i - 1);
                break;
            }
        }

        if( productClassRemoved.isDefaultProductClass() )
        {
            getUserDefinitionStruct().defaultSessionProfiles = newProfiles;
        }
        else
        {
            getUserDefinitionStruct().sessionProfilesByClass = newProfiles;
        }

        profileModels = newModels;

        setModified(true);

        firePropertyChange(PROFILES_CHANGE_EVENT, oldModels, newModels);
    }

    /**
     * remove all session profiles and default session profile
     */
    public void removeAllProfiles()
    {
        ProfileModel[] oldModels = profileModels;
        profileModels = null;
        
        getUserDefinitionStruct().sessionProfilesByClass = new SessionProfileStruct[0];
        getUserDefinitionStruct().defaultSessionProfiles = new SessionProfileStruct[0];


        setModified(true);
        firePropertyChange(PROFILES_CHANGE_EVENT, oldModels, profileModels);     // keep the first default model
    }

    /**
     * Sets this model as modified. If setting to false and this model
     * represents a new UserDefinitionStruct that was never saved to the API,
     * this setting is ignored and the state of this model remains unchanged
     * due to this call.
     * @param modified true if modified, false if unchanged
     */
    public void setModified(boolean modified)
    {
        if(!modified && isNeverBeenSaved())
        {
            return;
        }

        isModified = modified;
    }

    private void setProfiles(Profile[] profiles, boolean fireEvent)
    {
        Profile[] oldProfiles = new Profile[0];

        if(fireEvent)
        {
            oldProfiles = getProfiles();
        }

        ArrayList defaultSessionProfiles = new ArrayList(10);
        ArrayList classSessionProfiles = new ArrayList(10);

        for( int i = 0; i < profiles.length; i++ )
        {
            Profile profile = profiles[i];
            if(profile.getProductClass().isDefaultProductClass())
            {
                defaultSessionProfiles.add(profile);
            }
            else
            {
                classSessionProfiles.add(profile);
            }
        }

        getUserDefinitionStruct().defaultSessionProfiles =
                getStructsForProfiles((Profile[])defaultSessionProfiles.toArray(new Profile[0]));
        getUserDefinitionStruct().sessionProfilesByClass =
                getStructsForProfiles(( Profile[] ) classSessionProfiles.toArray(new Profile[0]));

        if(fireEvent)
        {
            firePropertyChange(PROFILES_CHANGE_EVENT, oldProfiles, profiles);
        }

        ProfileModel[] newModels = new ProfileModel[profiles.length];
        for( int i = 0; i < profiles.length; i++ )
        {
            newModels[i] = ProfileModelFactory.createMutableProfile(profiles[i]);
        }
        profileModels = newModels;
    }

    private void updateProfile(Profile profile, boolean fireEvent)
    {
        Profile[] profiles = getProfiles();
        Profile oldProfile = null;
        Profile newProfile;

        try
        {
            newProfile = (Profile)profile.clone();
        }
        catch(CloneNotSupportedException e)
        {
            GUILoggerHome.find().exception(e);
            newProfile = profile;
        }

        SessionProductClass productClass = newProfile.getProductClass();

        if( profiles != null && productClass != null )
        {
            for( int i = 0; i < profiles.length; i++ )
            {
                if( profiles[i].getProductClass().equals(productClass) )
                {
                    if( fireEvent )
                    {
                        oldProfile = profiles[i];
                    }
                    profiles[i] = newProfile;
                    setModified(true);
                    break;
                }
            }
        }

        SessionProfileStruct newStruct = newProfile.getProfileStruct();
        SessionProfileStruct[] structs;

        if(productClass != null && productClass.isDefaultProductClass())
        {
            structs = getUserDefinitionStruct().defaultSessionProfiles;
        }
        else
        {
            structs = getUserDefinitionStruct().sessionProfilesByClass;
        }

        if( structs != null && newStruct != null )
        {
            for( int i = 0; i < structs.length; i++ )
            {
                if( structs[i].classKey == newStruct.classKey &&
                        structs[i].sessionName.equals(newStruct.sessionName))
                {
                    structs[i] = newStruct;
                    setModified(true);
                    break;
                }
            }
        }

        if( fireEvent && isModified())
        {
            firePropertyChange(PROFILE_CHANGE_EVENT, oldProfile, newProfile);
        }
    }

    /**
     * Finds the SMS session for this user.
     */
    private synchronized UserSession findSMSUserSession(boolean refreshFromSMS)
    {
        if(smsSession == null && refreshFromSMS)
        {
            try
            {
                smsState = SMS_LOGGED_OUT;
                smsSession = SessionInfoManagerHome.find().getUserForName(getUserId());
                if(smsSession != null && smsSession.getUser().isLoggedIn())
                {
                    smsState = SMS_LOGGED_IN;
                }
            }
            catch(SessionQueryException e)
            {
                DefaultExceptionHandlerHome.find().process(e, "Could not determine SMS Session for user");
            }
        }

        return smsSession;
    }

    private void loadEnablements()
            throws DataValidationException, CommunicationException, AuthorizationException,
            SystemException
    {
        if( !isEnablementsLoaded && !isNeverBeenSaved() )
        {
            String userEnablementsKey = EnablementFactory.getUserEnablementsKey(this);
            try
            {
                PropertyServicePropertyGroup propertyGroup =
                        PropertyServiceFacadeHome.find().getPropertyGroup(PropertyCategoryTypes.USER_ENABLEMENT,
                                                                          userEnablementsKey);
                setEnablementCollections(propertyGroup);
            }
            catch (NotFoundException nfe)
            {
                if(GUILoggerHome.find().isDebugOn() &&
                   GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
                {
                    GUILoggerHome.find().debug("Enablements were not found for user=" + getUserId() + ", key=" +
                                               userEnablementsKey, GUILoggerSABusinessProperty.USER_MANAGEMENT);
                }
            }

            try
            {
                testClassPropertyGroup = EnablementFactory.getTestClassPropertyGroup(this);
            }
            catch(NotFoundException e)
            {
                if(GUILoggerHome.find().isDebugOn() &&
                   GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
                {
                    GUILoggerHome.find().debug("TestClass Enablement was not found for user=" + getUserId() + ", key=" +
                                               userEnablementsKey, GUILoggerSABusinessProperty.USER_MANAGEMENT);
                }
                testClassPropertyGroup = EnablementFactory.createTestClassPropertyGroup(this, false);
            }

            try
            {
                mdxPropertyGroup = EnablementFactory.getMDXPropertyGroup(this);
            }
            catch(NotFoundException e)
            {
                if(GUILoggerHome.find().isDebugOn() &&
                   GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
                {
                    GUILoggerHome.find().debug("MDX Enablement was not found for user=" + getUserId() + ", key=" +
                                               userEnablementsKey, GUILoggerSABusinessProperty.USER_MANAGEMENT);
                }
                mdxPropertyGroup = EnablementFactory.createMDXPropertyGroup(this, false);
            }
            
            try
            {
                tradingFirmPropertyGroup = EnablementFactory.getTradingFirmPropertyGroup(this);
            }
            catch(NotFoundException e)
            {
                if(GUILoggerHome.find().isDebugOn() &&
                   GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
                {
                    GUILoggerHome.find().debug("Trading Firm Enablement was not found for user=" + getUserId() + ", key=" +
                                               userEnablementsKey, GUILoggerSABusinessProperty.USER_MANAGEMENT);
                }
                tradingFirmPropertyGroup = EnablementFactory.createTradingFirmPropertyGroup(this, false);
            }

            isEnablementsLoaded = true;
            subscribeEnablementEvents();
        }
    }

    private void setEnablementCollections(PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, CommunicationException, AuthorizationException,
                   SystemException, NotFoundException
    {

        enablementsByClass.clear();
        enablementsByOperation.clear();

        // Set the version that was read
        enablementVersion = propertyGroup.getVersion();

        // Convert the properties to enablements
        enablements = EnablementFactory.createEnablementList(propertyGroup);

        // Add the enablements to the collections
        for (Enablement enablement : enablements)
        {
            List<Enablement> listByClass = enablementsByClass.get(enablement.getSessionProductClass());
            if (listByClass == null)
            {
                listByClass = allocateEnablementList();
                enablementsByClass.put(enablement.getSessionProductClass(), listByClass);
            }
            listByClass.add(enablement);

            Integer operation = new Integer(enablement.getOperationType());
            List<Enablement> listByOperation = enablementsByOperation.get(operation);
            if (listByOperation == null)
            {
                listByOperation = allocateEnablementList();
                enablementsByOperation.put(operation, listByOperation);
            }

            listByOperation.add(enablement);
        }

        isEnablementsChanged = false;
    }

    private void saveEnablements() throws SystemException, AuthorizationException, CommunicationException
    {
        if(isEnablementsLoaded && isEnablementsChanged)
        {
            // Take the list from of enablments and covert it to a PropertyGroup
            PropertyServicePropertyGroup propertyGroup =
                    EnablementFactory.createPropertyGroup(enablements, enablementVersion,
                                                          EnablementFactory.getUserEnablementsKey(this));
            // Save the PropertyGroups
            testClassPropertyGroup = PropertyServiceFacadeHome.find().savePropertyGroup(testClassPropertyGroup);
            mdxPropertyGroup = PropertyServiceFacadeHome.find().savePropertyGroup(mdxPropertyGroup);
            tradingFirmPropertyGroup = PropertyServiceFacadeHome.find().savePropertyGroup(tradingFirmPropertyGroup);
            
            propertyGroup = PropertyServiceFacadeHome.find().savePropertyGroup(propertyGroup);
            enablementVersion = propertyGroup.getVersion();

            isEnablementsChanged = false;
        }
    }

    /**
     * Adds an enablement for this user. If an enablement already exists for the same SessionProductClass
     * and operation type, then it is removed before the passed on is added. This implementation will only
     * add the enablement if the isEnabled() == true.
     * @param enablement to add
     * @return if a previous Enablement already existed with the same SessionProductClass and operation type,
     * then it is returned, otherwise null is returned.
     */
    public Enablement addEnablement(Enablement enablement, boolean fireEvent)
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        Enablement previous = null;

        if(enablement != null)
        {
            previous = removeEnablement(enablement, false);
            if( enablement.isEnabled() )
            {
                enablements.add(enablement);

                List<Enablement> listByClass = enablementsByClass.get(enablement.getSessionProductClass());
                if( listByClass == null )
                {
                    listByClass = allocateEnablementList();
                }
                listByClass.add(enablement);
                enablementsByClass.put(enablement.getSessionProductClass(), listByClass);

                Integer operation = new Integer(enablement.getOperationType());
                List<Enablement> listByOperation = enablementsByOperation.get(operation);
                if( listByOperation == null )
                {
                    listByOperation = allocateEnablementList();
                }
                listByOperation.add(enablement);
                enablementsByOperation.put(operation, listByOperation);

                setModified(true);
                isEnablementsChanged = true;
                if( fireEvent )
                {
                    firePropertyChange(ENABLEMENTS_CHANGE_EVENT, previous, enablement);
                }
            }
            else if(previous != null)
            {
                setModified(true);
                isEnablementsChanged = true;
                if( fireEvent )
                {
                    firePropertyChange(ENABLEMENTS_CHANGE_EVENT, previous, null);
                }
            }
        }
        return previous;
    }

    /**
     * Removes an enablement
     * @param enablement to remove
     * @return if enablement was found and removed, then it is returned, otherwise null is returned
     */
    public Enablement removeEnablement(Enablement enablement, boolean fireEvent)
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        loadEnablements();
        Enablement previous = null;
        if(enablement != null)
        {
            int index = enablements.indexOf(enablement);
            if( index > -1 )
            {
                previous = enablements.remove(index);

                List<Enablement> listByClass = enablementsByClass.get(previous.getSessionProductClass());
                if( listByClass != null )
                {
                    listByClass.remove(previous);
                    enablementsByClass.put(previous.getSessionProductClass(), listByClass);
                }

                Integer operation = new Integer(previous.getOperationType());
                List<Enablement> listByOperation = enablementsByOperation.get(operation);
                if( listByOperation != null )
                {
                    listByOperation.remove(previous);
                    enablementsByOperation.put(operation, listByOperation);
                }

                setModified(true);
                isEnablementsChanged = true;
                if(fireEvent)
                {
                    firePropertyChange(ENABLEMENTS_CHANGE_EVENT, previous, null);
                }
            }
        }

        return previous;
    }

    private void subscribeEnablementEvents()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        String key = EnablementFactory.getUserEnablementsKey(this);
        String keyTest = EnablementFactory.getTestClassOnlyKey  (this);
        String mdxKey = EnablementFactory.getMDXKey((this));
        String tradingFirmKey =  EnablementFactory.getTradingFirmKey(this);
        
        PropertyServiceFacadeHome.find().subscribe(PropertyCategoryTypes.USER_ENABLEMENT, key, this);
        PropertyServiceFacadeHome.find().subscribe(PropertyCategoryTypes.USER_ENABLEMENT, keyTest, this);
        PropertyServiceFacadeHome.find().subscribe(PropertyCategoryTypes.USER_ENABLEMENT, mdxKey, this);
        PropertyServiceFacadeHome.find().subscribe(PropertyCategoryTypes.USER_ENABLEMENT, tradingFirmKey, this);
        
    }

    private void unsubscribeEnablementEvents()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        String key = EnablementFactory.getUserEnablementsKey(this);
        String keyTest = EnablementFactory.getTestClassOnlyKey  (this);
        String mdxKey = EnablementFactory.getMDXKey((this));
        String tradingFirmKey =  EnablementFactory.getTradingFirmKey(this);
        
        PropertyServiceFacadeHome.find().unsubscribe(PropertyCategoryTypes.USER_ENABLEMENT, key, this);
        PropertyServiceFacadeHome.find().unsubscribe(PropertyCategoryTypes.USER_ENABLEMENT, keyTest, this);
        PropertyServiceFacadeHome.find().unsubscribe(PropertyCategoryTypes.USER_ENABLEMENT, mdxKey, this);
        PropertyServiceFacadeHome.find().unsubscribe(PropertyCategoryTypes.USER_ENABLEMENT, tradingFirmKey, this);
    }

    /**
     * Sets the AffiliatedFirm in the underlying UserFirmAffiliation Interface and if the UserFirmAffiliation Interface
     * is null it creates a new Underlying Interface and Struct
     *
     * @param firm
     */
    public void setUserFirmAffiliation(String firm)
    {
        String previous = null;

        if (isUserFirmAffiliationLoaded)
        {
            previous = userFirmAffiliation.getAffiliatedFirm();
            userFirmAffiliation.setAffiliatedFirm(firm);
        }
        else // then this user has no affiliation or its has been deleted
        {
            userFirmAffiliation = UserFirmAffiliationFactory.create(getExchangeAcronym(), firm);
            isUserFirmAffiliationLoaded   = true;
        }

        firePropertyChange(USERFIRM_AFFILIATION_CHANGE_EVENT, previous, firm);
    }

    /**
     * This method returns the value of the Affiliated Firm in the underlying UserFirmAffiliation Interface
     *
     * @return the Affiliated firm for this user
     */
    public String getUserFirmAffiliation()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
               NotFoundException
    {
        loadUserFirmAffiliation();
        if (userFirmAffiliation != null)
        {
            return userFirmAffiliation.getAffiliatedFirm();
        }

        return null;
    }

    /**
     * This methods removes the Affiliation for this user by calling SACAS and sets the
     * underlying UserFirmAffiliation Interface to null
     */
    public void removeUserFirmAffiliation()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
               NotFoundException, TransactionFailedException
    {
        if (userFirmAffiliation != null)
        {
            SystemAdminAPIFactory.find().deleteUserFirmAffiliation(userFirmAffiliation);

            userFirmAffiliation           = null;
            isUserFirmAffiliationLoaded   = false;
        }
    }

    public UserFirmAffiliation saveUserFirmAffiliation()
            throws TransactionFailedException, DataValidationException, SystemException, AuthorizationException,
                   CommunicationException
    {
        if (userFirmAffiliation != null && userFirmAffiliation.getAffiliatedFirm().length() > 0)
        {
            userFirmAffiliation = SystemAdminAPIFactory.find().setAffiliatedFirmForUser(userFirmAffiliation);
            isUserFirmAffiliationLoaded = true;
        }

        return userFirmAffiliation;
    }

    /**
     * sets the  underlying UserFirmAffiliation Interface to null and sets the load flag to false
     */
    public void resetUserFirmAffiliation()
    {
        userFirmAffiliation           = null;
        isUserFirmAffiliationLoaded   = false;
        firePropertyChange(USERFIRM_AFFILIATION_CHANGE_EVENT, null, null);
    }

    public PARBrokerProfile[] getPARBrokerProfiles()
    {
        return getPARBrokerProfiles(false);
    }

    /**
     * Get all PARBrokerProfiles for the user.  if forceReload is true, any cached profile data
     * will be removed from the model and all profile data will be relaoded from the server.
     */
    public synchronized PARBrokerProfile[] getPARBrokerProfiles(boolean forceReload)
    {
        if (!parProfilesLoaded || forceReload)
        {
            loadPARBrokerProfilesFromServer();
        }
        return parProfilesByVersion.values().toArray(new PARBrokerProfile[parProfilesByVersion.size()]);
    }

    /**
     * Get the user's PARBrokerProfile for a specific version.  If the user doesn't have
     * a PARBrokerProfile for the version, null will be returned.
     */
    public PARBrokerProfile getPARBrokerProfile(String version)
    {
        PARBrokerProfile[] parProfiles = getPARBrokerProfiles();
        PARBrokerProfile parProfile = null;
        for (PARBrokerProfile existingProfile : parProfiles)
        {
            if (existingProfile.getVersion().equals(version))
            {
                parProfile = existingProfile;
                break;
            }
        }
        return parProfile;
    }

    /**
     * Adds the PARBrokerProfile to the UserAccountModel.
     *
     * The UserAccountModel will be flagged as having been modified, and a PropertyChangeEvent will
     * be fired to registered listeners.
     */
    public void setPARBrokerProfile(String version, PARBrokerProfile parProfile)
    {
        setPARBrokerProfile(version, parProfile, true);
    }

    /**
     * Adds the PARBrokerProfile to the UserAccountModel.
     *
     * If fireModifiedEvent is true, the UserAccountModel will be flagged as having been modified,
     * and a PropertyChangeEvent will be fired to registered listeners.
     */
    public void setPARBrokerProfile(String version, PARBrokerProfile parProfile, boolean fireModifiedEvent)
    {
        PARBrokerProfile prevProfile = parProfilesByVersion.get(version);
        if(prevProfile != null)
        {
            cleanupAndRemovePARBrokerProfile(prevProfile);
            if (fireModifiedEvent)
            {
                setModified(true);
                firePropertyChange(PAR_BROKER_PROFILE_DELETED_EVENT, prevProfile, null);
            }
        }

        if(parProfile != null)
        {
            parProfile.addPropertyChangeListener(this);
            parProfilesByVersion.put(version, parProfile);
            if (fireModifiedEvent)
            {
                setModified(true);
                firePropertyChange(PAR_BROKER_PROFILE_ADDED_EVENT, null, parProfile);
            }
        }
    }

    public void removePARBrokerProfile(String version)
    {
        // remove all prefs from the PARBrokerProfile, but don't remove the profile from the map, so it will be saved as
        // an empty preference collection when this model is saved
        PARBrokerProfile parProfile = parProfilesByVersion.remove(version);
        if (parProfile != null)
        {
            deletedParProfilesByVersion.add(parProfile);
            parProfile.cleanup();
            setModified(true);
            firePropertyChange(PAR_BROKER_PROFILE_DELETED_EVENT, parProfile, null);
        }
    }

    public void savePARBrokerProfiles()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        PARBrokerProfile[] allProfiles = getPARBrokerProfiles();
        String allVersionsPrefVal = "";

        // only save the allVersionsPref if changes have been made...
        boolean parProfilesChanged = false;

        // delete prefs for any PARBrokerProfiles that the user has deleted since the last save
        List<PARBrokerProfile> deletedProfiles = new ArrayList<PARBrokerProfile>(deletedParProfilesByVersion);
        for(PARBrokerProfile deletedProfile : deletedProfiles)
        {
            String prefSection = deletedProfile.getPreferenceName();
            SystemAdminAPIFactory.find().removeUserPreferencesByPrefix(getUserId(), prefSection);
            parProfilesChanged = true;
        }
        deletedParProfilesByVersion.clear();

        for (PARBrokerProfile profile : allProfiles)
        {
            if (profile.savePreferences())
            {
                parProfilesChanged = true;
            }
            allVersionsPrefVal += profile.getVersion() + SEQUENCE_PREF_DELIMITER;
        }
        // only save if one or more profiles has changed
        if (parProfilesChanged)
        {
            if (allVersionsPrefVal.endsWith(SEQUENCE_PREF_DELIMITER))
            {
                allVersionsPrefVal = allVersionsPrefVal.substring(0, allVersionsPrefVal.length() - 1);
            }
            String prefName = getPARBrokerProfileVersionsPrefName();
            PreferenceStruct[] versionPref = new PreferenceStruct[]{new PreferenceStruct(prefName, allVersionsPrefVal)};
            SystemAdminAPIFactory.find().setUserPreferences(getUserId(), versionPref);
        }
    }

    private void cleanupAndRemovePARBrokerProfile(PARBrokerProfile parProfile)
    {
        parProfile.removePropertyChangeListener(this);
        parProfile.cleanup();
        parProfilesByVersion.remove(parProfile.getVersion());
        deletedParProfilesByVersion.remove(parProfile);
    }

    private static String getPARBrokerProfileVersionsPrefName()
    {
        return PARBrokerProfile.PREF_PREFIX + PreferenceConstants.DEFAULT_PATH_SEPARATOR + "AllVersions";
    }

    private synchronized void loadPARBrokerProfilesFromServer()
    {
        if (parProfilesLoaded)
        {
            for (PARBrokerProfile parProfile : getPARBrokerProfiles())
            {
                cleanupAndRemovePARBrokerProfile(parProfile);
            }
        }
        String[] allVersions = findAllPARBrokerProfileVersions();
        for (String version : allVersions)
        {
            // be sure to concat PreferenceConstants.DEFAULT_PATH_SEPARATOR to the end of the pref prefix, so we
            // won't accidentally get other versions' prefs included in this profile (e.g., if the pref prefix
            // ended in "4" without the separator, the query could also return "4-4", etc.)
            String prefPrefix = PARBrokerProfile.PREF_PREFIX + PreferenceConstants.DEFAULT_PATH_SEPARATOR + version +
                    PreferenceConstants.DEFAULT_PATH_SEPARATOR;
            try
            {
                PreferenceStruct[] prefs = SystemAdminAPIFactory.find().getUserPreferencesByPrefix(getUserId(),
                        prefPrefix);
                PARBrokerProfile parProfile = PARBrokerProfileFactoryHome.find().create(getUserId(), version, prefs); 
                setPARBrokerProfile(version, parProfile, false);
            }
            catch (UserException e)
            {
                DefaultExceptionHandlerHome.find().process(e, "Unable to get PAR profile preferences for userID: '" + getUserId() +
                        "' preference prefix: '" + prefPrefix + "'.");
            }
        }
        parProfilesLoaded = true;
    }

    private String[] findAllPARBrokerProfileVersions()
    {
        String[] retVal = new String[0];
        String versionPrefName = getPARBrokerProfileVersionsPrefName();
        try
        {
            PreferenceStruct[] prefs = SystemAdminAPIFactory.find().getUserPreferencesByPrefix(getUserId(), versionPrefName);
            if(prefs.length == 1)
            {
                String prefValue = prefs[0].value;
                if (prefValue.length() > 0)
                {
                    retVal = prefValue.split(Character.toString(PreferenceConstants.DELIMITER));
                }
            }
        }
        catch (UserException e)
        {
            GUILoggerHome.find().exception(e, "Unable to get '" + versionPrefName +
                    "' preference from the server for UserID '" + getUserId() + "' -- user has no saved PAR Broker Profiles.");
        }
        return retVal;
    }

    private void loadUserFirmAffiliation()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (!isUserFirmAffiliationLoaded)
        {
            try
            {
                userFirmAffiliation = SystemAdminAPIFactory.find().getAffiliatedFirmForUser(getExchangeAcronym());
                isUserFirmAffiliationLoaded = true;
            }
            catch (NotFoundException e)
            {
                // This means there is no firm affiliation for this user
                if(GUILoggerHome.find().isDebugOn() &&
                   GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
                {
                    GUILoggerHome.find().debug("Firm Affiliation not found for user=" + getUserId(),
                                               GUILoggerSABusinessProperty.USER_MANAGEMENT);
                }
            }
        }
    }

    private void initializeEnablementStructures(boolean fireEvent)
    {
        isEnablementsLoaded = false;
        isEnablementsChanged = false;
        enablements = allocateEnablementList(100);
        enablementsByClass = new HashMap<SessionProductClass, List<Enablement>>(101);
        enablementsByOperation = new HashMap<Integer, List<Enablement>>(101);
        testClassPropertyGroup = null;
        mdxPropertyGroup = null;
        tradingFirmPropertyGroup = null;

        if(fireEvent)
        {
            firePropertyChange(ENABLEMENTS_CHANGE_EVENT, null, null);
            firePropertyChange(TESTCLASSES_CHANGE_EVENT, null, null);
            firePropertyChange(MDX_ENABLEMENT_CHANGE_EVENT, null, null);
            firePropertyChange(TRADINGFIRM_ENABLEMENT_CHANGE_EVENT, null, null);
        }
    }

    private void initialize()
    {
        propertyEventManager = new PropertyChangeSupport(this);

        sessionClearingAcronyms = null;
        userQRMProfile = null;
        smsSession = null;

        initializeEnablementStructures(false);

        isUserFirmAffiliationLoaded = false;
        userFirmAffiliation = null;
    }

    private ArrayList<Enablement> allocateEnablementList(int preferredSize)
    {
        return preferredSize <= 0 ? new ArrayList<Enablement>(): new ArrayList<Enablement>(preferredSize);
    }

    private ArrayList<Enablement> allocateEnablementList()
    {
        return allocateEnablementList(OperationTypes.ALL_TYPES.length);
    }
}
