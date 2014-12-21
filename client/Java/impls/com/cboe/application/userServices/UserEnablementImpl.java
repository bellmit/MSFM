//
// -----------------------------------------------------------------------------------
// Source file: UserEnablementImpl.java
//
// PACKAGE: com.cboe.application.userServices;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.userServices;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.cache.CacheFactory;
import com.cboe.application.tradingSession.cache.TradingSessionCacheKeyFactory;
import com.cboe.application.product.cache.ProductCacheKeyFactory;
import com.cboe.domain.util.SessionProfileUserEventStructContainer;
import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.property.PropertyFactory;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.ProductClass;
import com.cboe.idl.cmiErrorCodes.AuthorizationCodes;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.constants.OperationTypes;
import com.cboe.idl.property.PropertyGroupStruct;
import com.cboe.idl.property.PropertyStruct;
import com.cboe.idl.user.UserEnablementStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiProduct.ClassStruct;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.ProductQueryServiceAdapter;
import com.cboe.interfaces.application.UserEnablement;
import com.cboe.interfaces.domain.Delimeter;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.property.Property;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;


// User Enablement Service
public class UserEnablementImpl extends BObject implements UserEnablement, EventChannelListener{

    private String userId;
    private String exchange;
    private String acronym;
    private boolean testClassesOnly;
    private Map<String, Map<UserEnablementClassOperationKey,UserEnablementClassOperationKey>> userOperationEnablements;
    private ProductQueryServiceAdapter productQueryServiceAdapter;

    private static UserEnablementClassOperationKey[] allKeys;

    public static final String TEST_CLASSES_KEY = "TestClasses";
    public static final String TestClassesProperty = "TestClasses";

    public static final String MDX_PROPERTY_KEY = "MDX";
    private boolean mdxEnabled;

    // enablement for Drop Copy 2
    public static final String TRADING_FIRM_PROPERTY_KEY = "TradingFirm";
    private boolean tradingFirmEnabled;

    // FIXME - KAK - 5/2005 - require for single acr rollout
    // this flag is a MUST as the value of TestClassesOnly
    // has been moved to the property service but will NOT be removed from the sbt_user
    // or CacheUpdate path.
    // In order to make sure only the correct update event is used, this flag MUST
    // be set on "creation" time to indicate the real location of the TestClassesOnly flag
    // ONLY when all paths for CacheUpdate are removed will this flag no longer be needed.
    private boolean propertyUpdatesOnly = false;
    
    /**
     *  Key for the User Enablement second hash.
     */
    private class UserEnablementClassOperationKey
    {
        private int hashKey;
        private int classKey;
        private int operationType;

        /**
         *  Constructor for the key.  Builds the hashkey and sets
         *  the attributes.
         */
        public UserEnablementClassOperationKey(int classKey, int operationType)
        {
            this.classKey = classKey;
            this.operationType = operationType;
            hashKey = classKey << 7;
            hashKey = hashKey | operationType;
        }
        public String toString()
        {
            StringBuilder buf = new StringBuilder(42);

            buf.append(" classKey: ").append(classKey);
            buf.append(" operationType: ").append(operationType);

            return buf.toString();
        }
        public int getClassKey()
        {
            return classKey;
        }
        public int getOperationType()
        {
            return operationType;
        }
        /**
         *  Get the precalculated hashcode.
         *
         *  @return The precalculated hashcode.
         */
        public int hashCode()
        {
            return hashKey;
        }
        /**
         *  Check to see if the two objects are equal.
         */
        public boolean equals(Object object)
        {
            if (object == this)
            {
                return true;
            }
            if (object instanceof UserEnablementClassOperationKey)
            {
                UserEnablementClassOperationKey key = (UserEnablementClassOperationKey) object;
                if (key.classKey == classKey)
                {
                    if (key.operationType == operationType)
                    {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * Initialize the user Enablement collections.
     */
    public UserEnablementImpl( String userId, String exchange, String acronym )
    {
        super();
        this.userId = userId;
        this.exchange = exchange;
        this.acronym = acronym;
        this.userOperationEnablements = new HashMap<String, Map<UserEnablementClassOperationKey,UserEnablementClassOperationKey>>();
        this.testClassesOnly = false;

        // FIXME - KAK  after full single acr rollout, this will no longer be needed
        ChannelKey channelKey;
        channelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY_ENABLEMENT, userId);
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

        // FIXME - KAK  after full single acr rollout, this will no longer be needed
        // Note: When this goes away (user enablement through CacheUpdate)
        // you might want to remove the method getCacheUpdateConsumerHome() from the
        // ServicesHelper. -- Eric 3/11/2005
        ChannelKey channelKey2;
        channelKey2 = new ChannelKey(ChannelType.USER_EVENT_ADD_USER, userId);
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey2);
        try
        {
            // Note: When this goes away (user enablement through CacheUpdate)
            // you might want to remove the method getCacheUpdateConsumerHome() from the
            // ServicesHelper. -- Eric 3/11/2005
                ServicesHelper.getCacheUpdateConsumerHome().addFilter(channelKey2);

        }
        catch(Exception e)
        {
            Log.exception(this, "UserEnablementImpl -> Exception while adding CacheUpdate filter for user: " + userId, e);
        }

        // new support for enablements via property service/consumer
        String enablementKey = getUserEnablementKey();
        ChannelKey enableChannelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY_ENABLEMENT, enablementKey);
        EventChannelAdapterFactory.find().addChannelListener(this, this, enableChannelKey);

        String testClassesKey = getUserTestClassesKey();
        ChannelKey testClassesChannelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY_ENABLEMENT, testClassesKey);
        EventChannelAdapterFactory.find().addChannelListener(this, this, testClassesChannelKey);

        String mdxKey = getUserMDXEnablmentKey();
        ChannelKey mdxChannelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY_ENABLEMENT, mdxKey);
        EventChannelAdapterFactory.find().addChannelListener(this, this, mdxChannelKey);

        String tradingFirmKey = getUserTradingFirmEnablementKey();
        ChannelKey tradingFirmChannelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY_ENABLEMENT, tradingFirmKey);
        EventChannelAdapterFactory.find().addChannelListener(this, this, tradingFirmChannelKey);
    }

    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey  channelKey = (ChannelKey)event.getChannel();
        Object      channelData = event.getEventData();

        if (Log.isDebugOn())
        {
            Log.debug(this, "UserEnablementImpl -> "
                       + userId + ":" + exchange + ":" + acronym
                       + " : received event " + channelKey + " : " + channelData );
        }

        try
        {
            switch (channelKey.channelType)
            {
                // FIXME - KAK  after full single acr rollout, this will no longer be needed
                case ChannelType.USER_EVENT_ADD_USER:
                    if (Log.isDebugOn())
                    {
                        Log.debug(this, "UserEnablementImpl -> " + userId + " : received event " + channelKey.toString() + " "+ channelKey.key.toString() );
                    }
                    SessionProfileUserEventStructContainer sessionProfileUserEventStructContainer = (SessionProfileUserEventStructContainer)channelData;
                    acceptUserEnablementUpdate(sessionProfileUserEventStructContainer.getUserEnablementStruct());
                    break;
               case ChannelType.UPDATE_PROPERTY_ENABLEMENT:
                    if (Log.isDebugOn())
                    {
                       Log.debug(this, "UserEnablementImpl -> "
                                  + userId + ":" + exchange + ":" + acronym
                                  + " : received event " + channelKey.toString() + " " + channelKey.key.toString());
                    }
                    acceptUserEnablementUpdate((PropertyGroupStruct)channelData);
                    break;


                default :
                    Log.alarm(this, "User Based User Enablement Service -> Wrong Channel : " + channelKey.channelType);
                    break;
            }
        }
        catch (Exception e)
        {
            Log.exception(this, "UserEnablementImpl -> " + userId + ":" + exchange + ":" + acronym, e);
        }
    }

    // FIXME - KAK  after full single acr rollout, this will no longer be needed
    /**
     *
     */
    public void acceptUserEnablementUpdate(PropertyGroupStruct propertyGroupStruct, UserEnablementStruct userEnablementStruct)
    {
        acceptUserEnablementUpdate(propertyGroupStruct);
        acceptUserEnablementUpdate(userEnablementStruct);
    }

    // FIXME - KAK  after full single acr rollout, this will no longer be needed
    /**
     *  Update the testClassesOnly flag from the old disablements saving.
     */
    public void acceptUserEnablementUpdate(UserEnablementStruct userEnablementStruct )
    {
        if (!propertyUpdatesOnly)
        {
            // update testClassesOnly Flag
            testClassesOnly = userEnablementStruct.testClassesOnly;
            if(Log.isDebugOn())
            {
                Log.debug(this, "UserEnablementImpl --> " + getUserEnablementDump().toString());
            }
        }
        else
        {
            StringBuilder ignored = new StringBuilder(105);
            ignored.append("UserEnablementImpl --> userEnablementStruct update of TestClassesOnly ignored::")
                   .append("propertyUpdatesOnly=").append(propertyUpdatesOnly);
            Log.information(this, ignored.toString());
        }

    }

    /**
     *  Update the user enablements hashMaps.
     *
     *  @param propertyGroupStruct The struct with the property change for the user enablement.
     */
    public void acceptUserEnablementUpdate(PropertyGroupStruct propertyGroupStruct)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "UserEnablementImpl -> acceptUserEnablementUpdate for "
                       + userId + ":" + exchange + ":" + acronym +
                       ":" + dumpPropertyGroupStruct(propertyGroupStruct).toString());
        }

        // enablements now contain the addition of the TestClasses boolean setting
        // this will be a separate property on the UserEnablement category
        if (isTestClassesKey(propertyGroupStruct.propertyKey))
        {
            // FIXME - KAK  need to verify implementation with the GUI's usage/implementation
            PropertyServicePropertyGroup propertyServiceGroup = PropertyFactory.createPropertyGroup(propertyGroupStruct);
            Property testClassesProperty = propertyServiceGroup.getProperty(TestClassesProperty);
            testClassesOnly = Boolean.valueOf(testClassesProperty.getValue()).booleanValue();
        }
        else if(isMDXEnablementKey(propertyGroupStruct.propertyKey))
        {
            PropertyServicePropertyGroup propertyServiceGroup = PropertyFactory.createPropertyGroup(propertyGroupStruct);
            Property mdxProperty = propertyServiceGroup.getProperty(MDX_PROPERTY_KEY);
            mdxEnabled = Boolean.valueOf(mdxProperty.getValue()).booleanValue();
        }
        else if(isTradingFirmEnablementKey(propertyGroupStruct.propertyKey))
        {
            PropertyServicePropertyGroup propertyServiceGroup = PropertyFactory.createPropertyGroup(propertyGroupStruct);
            Property tradingFirmProperty = propertyServiceGroup.getProperty(TRADING_FIRM_PROPERTY_KEY);
            tradingFirmEnabled = Boolean.valueOf(tradingFirmProperty.getValue()).booleanValue();
        }
        else
        {
            HashMap<String, Map<UserEnablementClassOperationKey,UserEnablementClassOperationKey>> newEnablements = 
            				new HashMap<String, Map<UserEnablementClassOperationKey,UserEnablementClassOperationKey>>();

            // Check to see if the property change is for user enablements
            PropertyStruct[] preferences = propertyGroupStruct.preferenceSequence;
            // build new user Enablement collection
            for (int i=0; i < preferences.length; i++)
            {
                // Convert the value of the property to something we can use
                StringTokenizer tokens = new StringTokenizer(preferences[i].name,String.valueOf(Delimeter.PROPERTY_DELIMETER));
                String session;
                int    classKey;
                int    operationType;

                if (tokens.countTokens() != 3)
                {
                    // This means this property is hosed, report it and continue on
                    Log.alarm(this, "UserEnablementImpl -> invalid enablement [" + preferences[i].name + "] for "
                               + userId + ":" + exchange + ":" + acronym);
                    continue;
                }
                try
                {
                    session = tokens.nextToken();
                    classKey = Integer.parseInt(tokens.nextToken());
                    operationType = Integer.parseInt(tokens.nextToken());
                }
                catch (NumberFormatException e)
                {
                    // Skip processing this one, it is bad
                    Log.alarm(this, "UserEnablementImpl -> invalid enablement [" + preferences[i].name + "] for "
                               + userId + ":" + exchange + ":" + acronym);
                    continue;
                }

                Map<UserEnablementClassOperationKey,UserEnablementClassOperationKey>    sessionList;
                sessionList = newEnablements.get(session);
                if (sessionList == null)
                {
                    // Set the initial capacity to the size of the user enablements
                    // This gives us performance over memory.
                    sessionList = new HashMap<UserEnablementClassOperationKey,UserEnablementClassOperationKey>(preferences.length);
                    newEnablements.put(session,sessionList);
                }
                UserEnablementClassOperationKey key = new UserEnablementClassOperationKey(classKey,operationType);
                sessionList.put(key,key);
            }

            synchronized(userOperationEnablements)
            {
                userOperationEnablements = newEnablements;
            }
            if (Log.isDebugOn())
            {
                Log.debug(this, "UserEnablementImpl --> " + getUserEnablementDump().toString());
            }
        }
    }

    /**
     *  Check the enablements to see if the user has permission to perform the requested action.
     *
     *  @param sessionName Session the user is requesting to use.
     *  @param classKey    Key for the class the user is requesting to use.
     *  @param operationType The key for the operation type the user is requesting to use.
     */
    public void verifyUserEnablement(String sessionName, int classKey, int operationType)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if ( testClassesOnly)
        {
            if (TestClassCacheFactory.isTestClass(classKey))
            {
                return;
            }
            if(Log.isDebugOn())
            {
                Log.debug(this, "UserEnablementImpl::verifyUserEnablement fail testClassesOnly=" + testClassesOnly
                          + " sessionName:classKey:operationType::" + sessionName + ":" + classKey + ":" + operationType
                          + " userId:exchange:acronym::" + userId + ":" + exchange + ":" + acronym);
            }
        }
        else
        {
            Map<UserEnablementClassOperationKey,UserEnablementClassOperationKey> enablements = userOperationEnablements.get(sessionName);
            if (enablements != null)
            {
                UserEnablementClassOperationKey allKey = new UserEnablementClassOperationKey(ProductClass.DEFAULT_CLASS_KEY, operationType);
                if (enablements.containsKey(allKey))
                {
                    return;
                }
                if(Log.isDebugOn())
                {
                    Log.debug(this, "UserEnablementImpl::verifyUserEnablement fail default class key"
                              + " sessionName:classKey:operationType::" + sessionName + ":" + classKey + ":" + operationType
                              + " userId:exchange:acronym::" + userId + ":" + exchange + ":" + acronym);
                }

                UserEnablementClassOperationKey key = new UserEnablementClassOperationKey(classKey, operationType);
                if (enablements.containsKey(key))
                {
                    return;
                }
                if(Log.isDebugOn())
                {
                    Log.debug(this, "UserEnablementImpl::verifyUserEnablement fail classKey"
                              + " sessionName:classKey:operationType::" + sessionName + ":" + classKey + ":" + operationType
                              + " userId:exchange:acronym::" + userId + ":" + exchange + ":" + acronym);
                }
            }
        }

        getClassForSessionFromCache(sessionName, classKey);

        StringBuilder neo = new StringBuilder(sessionName.length()+40);
        neo.append("Not Enabled Operation: ").append(sessionName).append(":").append(classKey).append(":").append(operationType);
        throw ExceptionBuilder.authorizationException(neo.toString(), AuthorizationCodes.USER_DISABLED);
    }

    public void verifyUserEnablementForProduct(String sessionName, int productKey, short operationType)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        try
        {
            int classKey = getProductQueryServiceAdapter().getProductByKey(productKey).productKeys.classKey;
            //verify if it is enabled
            verifyUserEnablement(sessionName, classKey, operationType);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product: "+ sessionName + ":"+ productKey, DataValidationCodes.INVALID_PRODUCT);
        }
        catch (DataValidationException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product: "+ sessionName + ":"+ productKey, DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void verifyUserEnablementForSession(String sessionName, int operationType)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (testClassesOnly || sessionEnabled(sessionName, operationType))
        {
            return;
        }
        StringBuilder neo = new StringBuilder(sessionName.length()+30);
        neo.append("Not Enabled Operation: ").append(sessionName).append(":").append(operationType);
        throw ExceptionBuilder.authorizationException(neo.toString(),  AuthorizationCodes.USER_DISABLED );
    }

    public void verifyUserTradingFirmEnabled()
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if(tradingFirmEnabled)
        {
            return;
        }
        throw ExceptionBuilder.authorizationException("User not enabled for Trading Firm operations", AuthorizationCodes.USER_DISABLED);
    }

    public void verifyUserMDXEnabled()
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if(mdxEnabled)
        {
            return;
        }
        throw ExceptionBuilder.authorizationException("User not enabled for MDX operations", AuthorizationCodes.USER_DISABLED);
    }

    private boolean sessionEnabled(String sessionName, int operationType)
    {
        Map<UserEnablementClassOperationKey,UserEnablementClassOperationKey> enablements = userOperationEnablements.get(sessionName);
        if (enablements != null)
        {
            UserEnablementClassOperationKey key = new UserEnablementClassOperationKey(ProductClass.DEFAULT_CLASS_KEY, operationType);
            if (enablements.containsKey(key))
            {
                return true;
            }
        }
        return false;
    }

    public synchronized void cacheCleanUp()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "UserEnablementImpl -> calling cacheCleanUp: userId="
                      + userId + ":" + exchange + ":" + acronym);
        }
        try
        {
            if(Log.isDebugOn())
            {
                Log.debug(this, "UserEnablementImpl -> Cleaning up filter for CacheUpdate for userId="
                          + userId + ":" + exchange + ":" + acronym);
            }

// FIXME - KAK   this can be removed after single_acr is completely rolled out
//                  and the filter is no longer added above....
            ChannelKey channelKey = new ChannelKey(ChannelType.USER_EVENT_ADD_USER, userId);
            ServicesHelper.getCacheUpdateConsumerHome().removeFilter(channelKey);
        }
        catch(Exception e)
        {
            Log.exception(this, "UserEnablementImpl -> Error removing filter for user enablement during cache cleanup for user: "
                          + userId + ":" + exchange + ":" + acronym
                          , e);
        }
        
        EventChannelAdapterFactory.find().removeChannelListener(this);
        this.userOperationEnablements.clear();
    }

    protected ProductQueryServiceAdapter getProductQueryServiceAdapter()
    {
        if(productQueryServiceAdapter == null)
        {
            productQueryServiceAdapter = ServicesHelper.getProductQueryServiceAdapter();
        }
        return productQueryServiceAdapter;
    }

    /**
     * Validates the classKey by throwing a DataValidationException if the corresponding ClassStruct isn't cached.
     * @param classKey
     * @throws DataValidationException
     */
    protected void getClassFromCache(int classKey)
            throws DataValidationException
    {
        ClassStruct classStruct = (ClassStruct) CacheFactory.getClassCache().
                find(ProductCacheKeyFactory.getPrimaryClassKey(), Integer.valueOf(classKey));

        if(classStruct == null)
            throw ExceptionBuilder.dataValidationException("Invalid Data: " + classKey,
                                                           DataValidationCodes.INVALID_PRODUCT_CLASS);
    }

    /**
     *
     * @param sessionName
     * @param classKey
     * @throws DataValidationException
     * NOTE: Assumption here is that session/class information won't be updated during the day. This method code need to be rewrittedn if
     * CBOEDirect starts to update session or class during the trading day.
     */
    protected void getClassForSessionFromCache(String sessionName, int classKey) throws DataValidationException
    {

        SessionClassStruct sessionClass = (SessionClassStruct) CacheFactory.getSessionClassCache(sessionName).find(TradingSessionCacheKeyFactory.getPrimaryClassKey(), Integer.valueOf(classKey));
        if(sessionClass == null)
            // todo: shouldn't this use DataValidationCodes.INVALID_PRODUCT_CLASS ?
            throw ExceptionBuilder.dataValidationException("Invalid Data: "+ sessionName + ":"+ classKey, DataValidationCodes.INVALID_PRODUCT);

    }
    /**
     *  Get the All Class key for the specific operation type.  The all keys
     *  are cached so they are only created once.
     */
    private UserEnablementClassOperationKey getAllKey(int operationType)
    {
        if (allKeys == null)
        {
            synchronized(allKeys)
            {
                // The Operation Types in IDL are 1 based, so add one
                int MAX_OT = OperationTypes.MAXIUM_OPERATION_TYPE + 1;

                allKeys = new UserEnablementClassOperationKey[MAX_OT];
                for (int i=0;i<MAX_OT;i++)
                {
                    allKeys[i] = new UserEnablementClassOperationKey(ProductClass.DEFAULT_CLASS_KEY, i);
                }
            }
        }
        return allKeys[operationType];
    }

    /**
     *  Dump out the user enablements to a readable format.
     *
     *  @return User's enablments, represented as a tree by indentation.
     */
    private StringBuilder getUserEnablementDump()
    {
        StringBuilder dump = new StringBuilder(200);
        Iterator<String> enablementsIterator = userOperationEnablements.keySet().iterator();
        while (enablementsIterator.hasNext())
        {
            String session = (String) enablementsIterator.next();
            dump.append("\nSession ").append(session);
            Map<UserEnablementClassOperationKey,UserEnablementClassOperationKey> classOperation = userOperationEnablements.get(session);
            if (classOperation != null)
            {
                Iterator<UserEnablementClassOperationKey> classOperationIterator = classOperation.keySet().iterator();
                while (classOperationIterator.hasNext())
                {
                    UserEnablementClassOperationKey key = classOperationIterator.next();
                    dump.append("\n  Class ").append(key.getClassKey()).append(" Operation ").append(key.getOperationType());
                }
            }
        }
        dump.append("\n testClassesOnly = ").append(testClassesOnly);
        dump.append("\n mdxEnabled = ").append(mdxEnabled);
        dump.append("\n tradingFirmEnabled = ").append(tradingFirmEnabled);
        return dump;
    }
    /**
     *  Dump out the PropertyGroupStruct
     *
     *  @return String representation of the PropertyGroupStruct
     */
    private StringBuilder dumpPropertyGroupStruct(PropertyGroupStruct struct)
    {
        StringBuilder dump = new StringBuilder(200);


        dump.append("\n");
        dump.append("\nstruct.category = ").append(struct.category);
        dump.append("\nstruct.propertyKey = ").append(struct.propertyKey);
        dump.append("\nstruct.versionNumber = ").append(struct.versionNumber);
        for (int i=0; i < struct.preferenceSequence.length ;i++ )
        {
            dump.append("\n  struct.name = ").append(struct.preferenceSequence[i].name);
            dump.append("\n  struct.value = ").append(struct.preferenceSequence[i].value);
        }

        return dump;
    }

    private boolean isEnablementKey( String propertyKey )
    {
        String key[] = BasicPropertyParser.parseArray( propertyKey );
        if ( key.length == 2 )
        {
            return true;
        }
        return false;
    }

    private boolean isTestClassesKey( String propertyKey )
    {
        String key[] = BasicPropertyParser.parseArray(propertyKey);
        if(key.length == 3 && key[ 2 ].matches(TEST_CLASSES_KEY))
        {
            return true;
        }
        return false;
    }

    private boolean isMDXEnablementKey(String propertyKey)
    {
        boolean retVal = false;
        String key[] = BasicPropertyParser.parseArray(propertyKey);
        // key[] should contain exchange, acronym, MDX_PROPERTY_KEY
        if(key.length == 3 && key[2].matches(MDX_PROPERTY_KEY))
        {
            retVal = true;
        }
        return retVal;
    }

    private boolean isTradingFirmEnablementKey(String propertyKey)
    {
        boolean retVal = false;
        String key[] = BasicPropertyParser.parseArray(propertyKey);
        // key[] should contain exchange, acronym, TRADING_FIRM_PROPERTY_KEY
        if(key.length == 3 && key[2].matches(TRADING_FIRM_PROPERTY_KEY))
        {
            retVal = true;
        }
        return retVal;
    }

    public static String getUserEnablementKey(String userId, String exchange, String acronym)
    {
        String enablementKey = BasicPropertyParser.buildCompoundString(new String[]{exchange, acronym});
        return enablementKey;
    }

    public static String getUserTestClassesKey(String userId, String exchange, String acronym)
    {
        String enablementKey = BasicPropertyParser.buildCompoundString(new String[]{exchange, acronym, TEST_CLASSES_KEY});
        return enablementKey;
    }

    public static String getMDXKey(String exchange, String acronym)
    {
        return BasicPropertyParser.buildCompoundString(new String[]{exchange, acronym, MDX_PROPERTY_KEY});
    }

    public static String getTradingFirmKey(String exchange, String acronym)
    {
        return BasicPropertyParser.buildCompoundString(new String[]{exchange, acronym, TRADING_FIRM_PROPERTY_KEY});
    }

    public String getUserEnablementKey()
    {
        return getUserEnablementKey(userId, exchange, acronym);
    }

    public String getUserMDXEnablmentKey()
    {
        return getMDXKey(exchange, acronym);
    }

    public String getUserTestClassesKey()
    {
        return getUserTestClassesKey(userId, exchange, acronym);
    }

    public String getUserTradingFirmEnablementKey()
    {
        return getTradingFirmKey(exchange, acronym);
    }

    // FIXME - KAK required for single acr rollout
    public boolean setPropertyUpdatesOnly( boolean propertyOnly )
    {
        boolean priorValue = propertyUpdatesOnly;
        propertyUpdatesOnly = propertyOnly;
        return priorValue;
    }

    /**
     * Returns the session names for which the specified user has some enablements.
     *
     * @author Gijo Joseph
     */
    public Set<String> getSessionsWithAnyEnablements()
    {
    	return userOperationEnablements.keySet();
    }
}
