//
// -----------------------------------------------------------------------------------
// Source file: MasterUserCollection.java
//
// PACKAGE: com.cboe.internalPresentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.user;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import com.cboe.idl.user.SessionProfileUserDefinitionStruct;
import com.cboe.idl.user.UserSummaryStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;

import com.cboe.interfaces.internalPresentation.user.UserAccountModel;
import com.cboe.interfaces.internalPresentation.user.UserCollection;
import com.cboe.interfaces.internalPresentation.user.UserCollectionEvent;
import com.cboe.interfaces.internalPresentation.user.UserCollectionListener;
import com.cboe.interfaces.internalPresentation.user.UserFirmAffiliation;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelListener;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.threading.APIWorkerImpl;
import com.cboe.presentation.threading.GUIWorkerImpl;
import com.cboe.presentation.user.ExchangeAcronymFactory;

import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;

/**
 * Implementation of UserCollection for the master collection.
 */
public class MasterUserCollection implements UserCollection, EventChannelListener
{
    public static final int DEFAULT_LIST_SIZE = 8000;
    public static final int DEFAULT_EXCHANGE_ACRONYM_USER_LIST_SIZE = 10;
    public static final int DEFAULT_EXCHANGE_ACRONYM_LIST_SIZE = 2000;

    private List userCollection;
    private Map exchangeAcronymUserMap;
    private Map exchangeAcronymByExchangeMap;
    private Map usersByUserIdMap;
    private SortedSet sortedUserIdSet;

    private final List globalListeners = new ArrayList(30);
    private final Map allUserListeners = new HashMap(30);

    private PropertyChangeListener userModelPropertyListener = new PropertyChangeListener()
    {
        public void propertyChange(PropertyChangeEvent event)
        {
            if( event.getPropertyName().equals(UserAccountModel.SAVED_CHANGE_EVENT) )
            {
                UserAccountModel user = (UserAccountModel)event.getSource();
                UserCollectionEvent myNewEvent = new UserCollectionEvent(MasterUserCollection.this, user);

                fireGlobalUserSavedEvent(myNewEvent);
                fireUserSavedEvent(user, myNewEvent);
            }
        }
    };

    public static final UserCollectionListener[] DEFAULT_LISTENERS_ARRAY = new UserCollectionListener[0];
    public static final UserAccountModel[] DEFAULT_USER_ACCOUNT_MODEL_ARRAY = new UserAccountModel[0];
    public static final ExchangeAcronym[] DEFAULT_EXCHANGE_ACRONYM_ARRAY = new ExchangeAcronym[0];

    protected MasterUserCollection()
    {
        userCollection = new ArrayList(DEFAULT_LIST_SIZE);
        exchangeAcronymUserMap = new HashMap(DEFAULT_LIST_SIZE);
        exchangeAcronymByExchangeMap = new HashMap(DEFAULT_LIST_SIZE);
        usersByUserIdMap = new HashMap(DEFAULT_LIST_SIZE);
    }

    /**
     * Receives events from API for user changes.
     * This method must remain synchronized with the UserCollectionFactory.buildMasterCollection so that
     * we don't start processing events before the collection is loaded up.
     */
    public synchronized void channelUpdate(ChannelEvent event)
    {
        GUILoggerHome.find().debug(getClass().getName(),
                                   GUILoggerSABusinessProperty.USER_MANAGEMENT, "API User Event received.");

        int channelType = ((ChannelKey)event.getChannel()).channelType;
        final Object eventData = event.getEventData();

        if(channelType == ChannelType.CB_USER_EVENT_ADD_USER)
        {
            GUIWorkerImpl worker = new GUIWorkerImpl(UserCollectionFactory.getUpdateLockObject())
            {
                private SessionProfileUserDefinitionStruct userStruct;
                private UserAccountModel foundModel;

                public boolean isCleanUpEnabled()
                {
                    return false;
                }

                public boolean isInitializeEnabled()
                {
                    return false;
                }

                public void execute() throws Exception
                {
                    GUILoggerHome.find().debug( getClass().getName(),
                                                GUILoggerSABusinessProperty.USER_MANAGEMENT,
                                                "Processing API User Event for add." );

                    userStruct = (SessionProfileUserDefinitionStruct)eventData;

                    foundModel = find( userStruct.userId );
                }

                public void processData()
                {
                    if(foundModel != null)
                    {
                        GUILoggerHome.find().debug( MasterUserCollection.this.getClass().getName(),
                                                    GUILoggerSABusinessProperty.USER_MANAGEMENT,
                                                    "User for Event already existed, updating..." );
                        updateUserDefinitionStructForUser(foundModel, userStruct);
                    }
                    else
                    {
                        GUILoggerHome.find().debug( MasterUserCollection.this.getClass().getName(),
                                                    GUILoggerSABusinessProperty.USER_MANAGEMENT,
                                                    "User for Event did not exist, adding..." );
                        UserAccountModel tempModel = new UserAccountModelImpl( userStruct );

                        ExchangeAcronymStruct exchAcrStruct = userStruct.userAcronym;
                        ExchangeAcronym existingExAcr = findExchangeAcronym(exchAcrStruct.exchange,
                                                                            exchAcrStruct.acronym);
                        if(existingExAcr != null && existingExAcr.isNeverBeenSaved())
                        {
                            ExchangeAcronym newExchAcr = ExchangeAcronymFactory.createExchangeAcronym(exchAcrStruct);
                            updateUnsavedExchangeAcronymToSaved(existingExAcr, newExchAcr);
                        }

                        addUser( tempModel );
                    }
                }

                public void handleException(Exception e)
                {
                    DefaultExceptionHandlerHome.find().process(e, "Error occurred processing a new or changed user.");
                }
            };
            APIWorkerImpl.run(worker);
        }
        else if(channelType == ChannelType.CB_USER_EVENT_DELETE_USER)
        {
            GUIWorkerImpl worker = new GUIWorkerImpl( UserCollectionFactory.getUpdateLockObject() )
            {
                private UserAccountModel foundModel;

                public boolean isCleanUpEnabled()
                {
                    return false;
                }

                public boolean isInitializeEnabled()
                {
                    return false;
                }

                public void execute() throws Exception
                {
                    UserSummaryStruct userSummaryStruct = (UserSummaryStruct) eventData;
                    foundModel = find(userSummaryStruct.userId);
                }

                public void processData()
                {
                    if(foundModel != null)
                    {
                        GUILoggerHome.find().debug( MasterUserCollection.this.getClass().getName(),
                                                    GUILoggerSABusinessProperty.USER_MANAGEMENT,
                                                    "Processing API User Event for delete." );
                        removeUser( foundModel );
                    }
                }

                public void handleException( Exception e )
                {
                    DefaultExceptionHandlerHome.find().process( e, "Error occurred processing a deleted user." );
                }
            };
            APIWorkerImpl.run( worker );
        }
        else if (channelType == ChannelType.CB_USER_EVENT_USER_FIRM_AFFILIATION_UPDATE)
        {
            GUIWorkerImpl worker = new GUIWorkerImpl(UserCollectionFactory.getUpdateLockObject())
            {
                private UserAccountModel[] foundModels;
                private UserFirmAffiliation affiliation;

                public boolean isCleanUpEnabled()
                {
                    return false;
                }

                public boolean isInitializeEnabled()
                {
                    return false;
                }

                public void execute() throws Exception
                {
                    GUILoggerHome.find().debug(getClass().getName(),
                                               GUILoggerSABusinessProperty.USER_MANAGEMENT,
                                               "Processing API User Event for Firm Affiliation Update.");

                    affiliation  = (UserFirmAffiliation) eventData;
                    foundModels = getAllUsersForExchangeAcronym(affiliation.getExchangeAcronym());
                }

                public void processData()
                {
                    if(foundModels != null)
                    {
                        GUILoggerHome.find().debug(MasterUserCollection.this.getClass().getName(),
                                                   GUILoggerSABusinessProperty.USER_MANAGEMENT,
                                                   "User Event for Firm Affiliation Update." );

                        for(int i = 0; i < foundModels.length; i++)
                        {
                            UserAccountModel foundModel = foundModels[i];
                            foundModel.setUserFirmAffiliation(affiliation.getAffiliatedFirm());
                        }
                    }
                }

                public void handleException(Exception e)
                {
                    DefaultExceptionHandlerHome.find().process(e, "Error occurred processing a Update User Firm Affiliation.");
                }
            };
            APIWorkerImpl.run(worker);
        }
        else if (channelType == ChannelType.CB_USER_EVENT_USER_FIRM_AFFILIATION_DELETE)
        {
            GUIWorkerImpl worker = new GUIWorkerImpl(UserCollectionFactory.getUpdateLockObject())
            {
                private UserAccountModel[] foundModels;
                private UserFirmAffiliation affiliation;

                public boolean isCleanUpEnabled()
                {
                    return false;
                }

                public boolean isInitializeEnabled()
                {
                    return false;
                }

                public void execute() throws Exception
                {
                    GUILoggerHome.find().debug(getClass().getName(),
                                               GUILoggerSABusinessProperty.USER_MANAGEMENT,
                                               "Processing API User Event for Firm Affiliation Delete.");


                    affiliation  = (UserFirmAffiliation) eventData;
                    foundModels = getAllUsersForExchangeAcronym(affiliation.getExchangeAcronym());
                }

                public void processData()
                {
                    if(foundModels != null)
                    {
                        GUILoggerHome.find().debug(MasterUserCollection.this.getClass().getName(),
                                                   GUILoggerSABusinessProperty.USER_MANAGEMENT,
                                                   "User Event for Firm Affiliation Delete." );
                        for(int i = 0; i < foundModels.length; i++)
                        {
                            UserAccountModel foundModel = foundModels[i];
                            foundModel.resetUserFirmAffiliation();
                        }
                    }
                }

                public void handleException(Exception e)
                {
                    DefaultExceptionHandlerHome.find().process(e, "Error occurred processing a deleted Firm Affiliation.");
                }
            };
            APIWorkerImpl.run(worker);
        }
    }

    /**
     * Adds the passed listener to receive events for all users contained by
     * the collection.
     * @param listener to add
     */
    public void addListener(UserCollectionListener listener)
    {
        synchronized(globalListeners)
        {
            if(!globalListeners.contains(listener))
            {
                globalListeners.add(listener);
            }
        }
    }

    /**
     * Adds the passed listener to receive events for <code>user</code> if contained by
     * the collection.
     * @param listener to add
     * @param user to only receive events for.
     */
    public void addListener(UserCollectionListener listener, UserAccountModel user)
    {
        synchronized(allUserListeners)
        {
            List userListeners = ( List ) allUserListeners.get(user);

            if(userListeners == null)
            {
                userListeners = new ArrayList(30);
            }

            synchronized( userListeners )
            {
                if( !userListeners.contains(listener) )
                {
                    userListeners.add(listener);
                }
            }

            allUserListeners.put(user, userListeners);
        }
    }

    /**
     * Removes the passed listener from receiving events for all users contained by
     * the collection.
     * @param listener to remove
     */
    public void removeListener(UserCollectionListener listener)
    {
        synchronized(globalListeners)
        {
            globalListeners.remove(listener);
        }
    }

    /**
     * Removes the passed listener from receiving events for <code>user</code> if contained by
     * the collection.
     * @param listener to remove
     * @param user to only remove listener for.
     */
    public void removeListener(UserCollectionListener listener, UserAccountModel user)
    {
        synchronized( allUserListeners )
        {
            List userListeners = ( List ) allUserListeners.get(user);

            if( userListeners != null )
            {
                synchronized( userListeners )
                {
                    userListeners.remove(listener);
                }

                allUserListeners.put(user, userListeners);
            }
        }
    }

    /**
     * Removes the passed listener from receiving any events for any user.
     * @param listener to remove
     */
    public void removeAllListener(UserCollectionListener listener)
    {
        removeListener(listener);

        synchronized(allUserListeners)
        {
            Set allListeners = allUserListeners.entrySet();

            for(Iterator iterator = allListeners.iterator(); iterator.hasNext();)
            {
                Map.Entry entry = (Map.Entry)iterator.next();
                UserAccountModel user = (UserAccountModel)entry.getKey();
                removeListener(listener, user);
            }
        }
    }

    /**
     * Adds a user to this User Collection.
     * @param newUser to add
     */
    public void addUser(UserAccountModel newUser)
    {
        boolean wasAdded = false;
        synchronized(this)
        {
            if( !containsUser( newUser ) )
            {
                internalAddUser(newUser);
                wasAdded = true;
            }
        }
        if( wasAdded )
        {
            UserCollectionEvent myNewEvent = new UserCollectionEvent(this, newUser);
            fireGlobalUserAddedEvent(myNewEvent);
        }
    }

    /**
     * Adds a user to this User Collection.
     * @param newUser to add
     * @return model that will represent <code>newUser</code> struct that
     * was passed.
     */
    public synchronized UserAccountModel addUser(SessionProfileUserDefinitionStruct newUser)
    {
        UserAccountModel newModel = new UserAccountModelImpl(newUser);

        int foundIndex = userCollection.indexOf(newModel);
        if(foundIndex > -1)
        {
            newModel = getUserAccount(foundIndex);
        }
        else
        {
            addUser(newModel);
        }

        return newModel;
    }

    /**
     * Determines if this collection contains a user matching the
     * passed user.
     * @param user to check for.
     * @return boolean True if exist, false otherwise.
     */
    public synchronized boolean containsUser(UserAccountModel user)
    {
        return userCollection.contains(user);
    }

    /**
     * Finds the UserAccountModel that has the passed userId.
     * @param userId of UserAccountModel to locate.
     * @return UserAccountModel contained in collection with matching userId, if not found
     * result will be null
     */
    public synchronized UserAccountModel find(String userId)
    {
        UserAccountModel model = (UserAccountModel) usersByUserIdMap.get(userId);
        return model;
    }

    /**
     * Finds the <code>UserAccountModel</code> that has the passed <code>partialUserId</code>.
     * @param   partialUserId - the user id (or leading characters of) whose UserAccountModel we wish to locate.
     * @param   userIdIterator the Iterator used to step thru user ids. Caller decides whether the iterator points
     *          to the first user id, or some other position, such as the id we stopped at in a previous call.
     * @return  UserAccountModel contained in collection with a matching user id, or null if no match found.
     * @since   Single Acronym scrum - May 4, 2005 - Shawn Khosravani
     */
    public synchronized UserAccountModel findPartial(String partialUserId, Iterator userIdIterator)
    {
        partialUserId = partialUserId.trim().toUpperCase();

        UserAccountModel model  = null;
        String           userId = null;
        while (model == null  &&  userIdIterator.hasNext())
        {
            userId = ((String) userIdIterator.next()).trim().toUpperCase();
            if (userId != null  &&  userId.startsWith(partialUserId))
            {
                model = (UserAccountModel) usersByUserIdMap.get(userId);
            }
        }
        return model;
    }

    /**
     * Returns a sorted set of user ids held in the collection.
     * @return  A sorted set of user ids held in the collection.
     * @since   Single Acronym scrum - May 4, 2005 - Shawn Khosravani
     */
    public synchronized SortedSet getSortedUserIdSet()
    {
        if (sortedUserIdSet == null)
        {
            // the sorted set must be recreated every time a user is added or removed
            sortedUserIdSet = new TreeSet(usersByUserIdMap.keySet());
        }
        return sortedUserIdSet;
    }

    /**
     * Resets the sorted user id set, so that it may be recreated the next time it is needed. This must be called
     * whenever the collection changes (i.e. users are added or removed).
     * @since   Single Acronym scrum - May 4, 2005 - Shawn Khosravani
     */
    public synchronized void resetSortedUserIdSet()
    {
        sortedUserIdSet = null;
    }

    /**
     * Finds all the UserAccountModel's that have the passed user ExchangeAcronym.
     * @param exchangeAcronym of UserAccountModel's to locate.
     * @return array of UserAccountModel contained in collection with matching ExhangeAcronym. If not found, result will
     *         be a zero-length array.
     */
    public synchronized UserAccountModel[] getAllUsersForExchangeAcronym(ExchangeAcronym exchangeAcronym)
    {
        UserAccountModel[] result = DEFAULT_USER_ACCOUNT_MODEL_ARRAY;

        List userListForExchangeAcronym = (List) exchangeAcronymUserMap.get(exchangeAcronym);
        if(userListForExchangeAcronym != null)
        {
            result = (UserAccountModel[]) userListForExchangeAcronym.toArray(result);
        }
        return result;
    }

    /**
     * Gets a specific element from the collection.
     * @param index Row into collection to retrieve.
     * @return UserAccountModel Model containing struct info.
     */
    public synchronized UserAccountModel getUserAccount(int index)
    {
        return (UserAccountModel)userCollection.get(index);
    }

    /**
     * Gets the number of elements in the collection.
     * @return int
     */
    public synchronized int getSize()
    {
        return userCollection.size();
    }

    /**
     * Obtains on Iterator over the collection of users for convenience of visiting
     * all user instances.
     * @return java.util.Iterator
     */
    public Iterator getIteratorForUsers()
    {
        return getListCollection().iterator();
    }

    /**
     * Removes a UserAccountModel from this collection.
     * @param userModel to remove.
     */
    public void removeUser(UserAccountModel userModel)
    {
        boolean wasRemoved = false;
        synchronized(this)
        {
            if( containsUser( userModel ) )
            {
                internalRemoveUser(userModel);
                wasRemoved = true;
            }
        }
        if( wasRemoved )
        {
            UserCollectionEvent myNewEvent = new UserCollectionEvent(this, userModel);
            fireGlobalUserRemovedEvent(myNewEvent);
        }
    }

    /**
     * Gets the full collection as a List. An unmodifiable version should be returned.
     */
    public synchronized List getListCollection()
    {
        return Collections.unmodifiableList(userCollection);
    }

    /**
     * Returns an array of all known ExchangeAcronym's
     * @return known ExchangeAcronym's from all users
     */
    public synchronized ExchangeAcronym[] getAllExchangeAcronyms()
    {
        Set allExchangeAcronyms = exchangeAcronymUserMap.keySet();
        return (ExchangeAcronym[]) allExchangeAcronyms.toArray(new ExchangeAcronym[allExchangeAcronyms.size()]);
    }

    /**
     * Gets all the ExchangeAcronym's that are for a specific Exchange.
     * @param exchange to find ExchangeAcronym's for
     * @return all ExchangeAcronym's whose exchange is equal to the passed exchange. If none are found, a zero length
     *         array should be returned.
     */
    public synchronized ExchangeAcronym[] getExchangeAcronymsForExchange(String exchange)
    {
        ExchangeAcronym[] result = DEFAULT_EXCHANGE_ACRONYM_ARRAY;

        List exchangeAcronymList = (List) exchangeAcronymByExchangeMap.get(exchange);
        if(exchangeAcronymList != null)
        {
            result = (ExchangeAcronym[]) exchangeAcronymList.toArray(result);
        }
        return result;
    }

    /**
     * Attempts to verify if an existing ExchangeAcronym exists for the passed parameters.
     * @param exchange to find existing ExchangeAcronym for
     * @param acronym to find existing ExchangeAcronym for
     * @return if an ExchangeAcronym was found for the passed parameters, it is returned, otherwise, null is returned.
     */
    public synchronized ExchangeAcronym findExchangeAcronym(String exchange, String acronym)
    {
        ExchangeAcronym result = null;

        List exchangeAcronymList = (List) exchangeAcronymByExchangeMap.get(exchange);
        if(exchangeAcronymList != null)
        {
            ExchangeAcronym templateExchAcr = ExchangeAcronymFactory.createExchangeAcronym(exchange, acronym);
            int existingIndex = exchangeAcronymList.indexOf(templateExchAcr);
            if(existingIndex > -1)
            {
                result = (ExchangeAcronym) exchangeAcronymList.get(existingIndex);
            }
        }

        return result;
    }

    protected void updateUserDefinitionStructForUser(UserAccountModel userModel,
                                                     SessionProfileUserDefinitionStruct userStruct)
    {
        ExchangeAcronym oldExchangeAcronym = userModel.getExchangeAcronym();
        ExchangeAcronym newExchangeAcronym = ExchangeAcronymFactory.createExchangeAcronym(userStruct.userAcronym);
        updateExchangeAcronymForUser(oldExchangeAcronym, newExchangeAcronym, userModel);
        userModel.setUserDefinitionStruct(userStruct);
    }

    protected synchronized void updateExchangeAcronymForUser(ExchangeAcronym oldExchangeAcronym,
                                                             ExchangeAcronym newExchangeAcronym,
                                                             UserAccountModel user)
    {
        internalRemoveUserFromExchangeAcronymMap(user, oldExchangeAcronym);
        internalAddUserToExchangeAcronymMap(user, newExchangeAcronym);
    }

    protected void updateUnsavedExchangeAcronymToSaved(ExchangeAcronym oldExchangeAcronym,
                                                       ExchangeAcronym newExchangeAcronym)
    {
        UserAccountModel[] usersToUpdate = getAllUsersForExchangeAcronym(oldExchangeAcronym);
        for(int i = 0; i < usersToUpdate.length; i++)
        {
            UserAccountModel userAccountModel = usersToUpdate[i];
            internalRemoveUserFromExchangeAcronymMap(userAccountModel, oldExchangeAcronym);
        }
        for(int i = 0; i < usersToUpdate.length; i++)
        {
            UserAccountModel userAccountModel = usersToUpdate[i];
            internalAddUserToExchangeAcronymMap(userAccountModel, newExchangeAcronym);
            userAccountModel.setExchangeAcronym(newExchangeAcronym);
        }
    }

    /**
     * Loads a fresh collection from the array.
     */
    protected synchronized void loadCollection(UserAccountModel[] users)
    {
        clearListeners();

        userCollection.clear();
        exchangeAcronymUserMap.clear();
        exchangeAcronymByExchangeMap.clear();
        usersByUserIdMap.clear();

        for( int i = 0; i < users.length; i++ )
        {
            internalAddUser(users[i]);
        }
    }

    protected void fireGlobalUserSavedEvent(UserCollectionEvent event)
    {
        UserCollectionListener[] listeners = DEFAULT_LISTENERS_ARRAY;
        synchronized( globalListeners )
        {
            listeners = ( UserCollectionListener[] ) globalListeners.toArray(listeners);
        }
        for( int i = 0; i < listeners.length; i++ )
        {
            UserCollectionListener userCollectionListener = listeners[i];
            userCollectionListener.userSaved(event);
        }
    }

    protected void fireGlobalUserRemovedEvent(UserCollectionEvent event)
    {
        UserCollectionListener[] listeners = DEFAULT_LISTENERS_ARRAY;
        synchronized( globalListeners )
        {
            listeners = ( UserCollectionListener[] ) globalListeners.toArray(listeners);
        }
        for( int i = 0; i < listeners.length; i++ )
        {
            UserCollectionListener userCollectionListener = listeners[i];
            userCollectionListener.userRemoved(event);
        }
    }

    protected void fireGlobalUserAddedEvent(UserCollectionEvent event)
    {
        UserCollectionListener[] listeners = DEFAULT_LISTENERS_ARRAY;
        synchronized( globalListeners )
        {
            listeners = ( UserCollectionListener[] ) globalListeners.toArray(listeners);
        }
        for( int i = 0; i < listeners.length; i++ )
        {
            UserCollectionListener userCollectionListener = listeners[i];
            userCollectionListener.userAdded(event);
        }
    }

    protected void fireUserSavedEvent(UserAccountModel user, UserCollectionEvent event)
    {
        UserCollectionListener[] listeners = DEFAULT_LISTENERS_ARRAY;
        synchronized( allUserListeners )
        {
            List userListeners = ( List ) allUserListeners.get(user);
            if( userListeners != null )
            {
                synchronized( userListeners )
                {
                    listeners = ( UserCollectionListener[] ) userListeners.toArray(listeners);
                }
            }
        }
        for( int i = 0; i < listeners.length; i++ )
        {
            UserCollectionListener userCollectionListener = listeners[i];
            userCollectionListener.userSaved(event);
        }
    }

    protected void fireUserRemovedEvent(UserAccountModel user, UserCollectionEvent event)
    {
        UserCollectionListener[] listeners = DEFAULT_LISTENERS_ARRAY;
        synchronized( allUserListeners )
        {
            List userListeners = ( List ) allUserListeners.get(user);
            if( userListeners != null )
            {
                synchronized( userListeners )
                {
                    listeners = ( UserCollectionListener[] ) userListeners.toArray(listeners);
                }
            }
        }
        for( int i = 0; i < listeners.length; i++ )
        {
            UserCollectionListener userCollectionListener = listeners[i];
            userCollectionListener.userRemoved(event);
        }
    }

    protected void fireUserAddedEvent(UserAccountModel user, UserCollectionEvent event)
    {
        UserCollectionListener[] listeners = DEFAULT_LISTENERS_ARRAY;
        synchronized( allUserListeners )
        {
            List userListeners = ( List ) allUserListeners.get(user);
            if( userListeners != null )
            {
                synchronized( userListeners )
                {
                    listeners = ( UserCollectionListener[] ) userListeners.toArray(listeners);
                }
            }
        }
        for( int i = 0; i < listeners.length; i++ )
        {
            UserCollectionListener userCollectionListener = listeners[i];
            userCollectionListener.userAdded(event);
        }
    }

    private void clearListeners()
    {
        synchronized(this)
        {
            for( Iterator iterator = userCollection.iterator(); iterator.hasNext(); )
            {
                (( UserAccountModel ) iterator.next()).removePropertyChangeListener(userModelPropertyListener);
            }
        }

        synchronized(globalListeners)
        {
            globalListeners.clear();
        }
        synchronized(allUserListeners)
        {
            allUserListeners.clear();
        }
    }

    /**
     * Adds a user to this User Collection.  A check to see if the arrayList contains
     * this user is not done.
     * @param newUser to add
     */
    private synchronized void internalAddUser(UserAccountModel newUser)
    {
        userCollection.add(newUser);

        usersByUserIdMap.put(newUser.getUserId(), newUser);

        internalAddUserToExchangeAcronymMap(newUser, newUser.getExchangeAcronym());

        newUser.addPropertyChangeListener(userModelPropertyListener);

        resetSortedUserIdSet();
    }

    private synchronized void internalRemoveUser(UserAccountModel userModel)
    {
        userModel.removePropertyChangeListener(userModelPropertyListener);

        internalRemoveUserFromExchangeAcronymMap(userModel, userModel.getExchangeAcronym());

        usersByUserIdMap.remove(userModel.getUserId());

        userCollection.remove(userModel);

        resetSortedUserIdSet();
    }

    private synchronized void internalRemoveUserFromExchangeAcronymMap(UserAccountModel userModel,
                                                                       ExchangeAcronym exchangeAcronym)
    {
        List userListForExchangeAcronym = (List) exchangeAcronymUserMap.get(exchangeAcronym);
        if(userListForExchangeAcronym != null)
        {
            userListForExchangeAcronym.remove(userModel);

            if(userListForExchangeAcronym.size() == 0)
            {
                exchangeAcronymUserMap.remove(exchangeAcronym);

                List exchangeAcronymListForExchange =
                        (List) exchangeAcronymByExchangeMap.get(exchangeAcronym.getExchange());
                if(exchangeAcronymListForExchange != null)
                {
                    exchangeAcronymListForExchange.remove(exchangeAcronym);

                    if(exchangeAcronymListForExchange.size() == 0)
                    {
                        exchangeAcronymByExchangeMap.remove(exchangeAcronym.getExchange());
                    }
                }
            }
        }
    }

    private synchronized void internalAddUserToExchangeAcronymMap(UserAccountModel newUser,
                                                                  ExchangeAcronym exchangeAcronym)
    {
        List userListForExchangeAcronym = (List) exchangeAcronymUserMap.get(exchangeAcronym);
        if(userListForExchangeAcronym == null)
        {
            userListForExchangeAcronym = new ArrayList(DEFAULT_EXCHANGE_ACRONYM_USER_LIST_SIZE);
            exchangeAcronymUserMap.put(exchangeAcronym, userListForExchangeAcronym);
            userListForExchangeAcronym.add(newUser);
        }
        else if(!userListForExchangeAcronym.contains(newUser))
        {
            userListForExchangeAcronym.add(newUser);
        }

        if(exchangeAcronym.getAcronym() != null && exchangeAcronym.getAcronym().length() > 0)
        {
            List exchangeAcronymListForExchange = (List) exchangeAcronymByExchangeMap.get(exchangeAcronym.getExchange());
            if(exchangeAcronymListForExchange == null)
            {
                exchangeAcronymListForExchange = new ArrayList(DEFAULT_EXCHANGE_ACRONYM_LIST_SIZE);
                exchangeAcronymByExchangeMap.put(exchangeAcronym.getExchange(), exchangeAcronymListForExchange);
                exchangeAcronymListForExchange.add(exchangeAcronym);
            }
            else if(!exchangeAcronymListForExchange.contains(exchangeAcronym))
            {
                exchangeAcronymListForExchange.add(exchangeAcronym);
            }
        }
    }
}