//
// -----------------------------------------------------------------------------------
// Source file: DefaultSortableUserCollection.java
//
// PACKAGE: com.cboe.internalPresentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.user;

import java.util.*;

import com.cboe.idl.user.SessionProfileUserDefinitionStruct;

import com.cboe.interfaces.internalPresentation.user.SortableCollectionListener;
import com.cboe.interfaces.internalPresentation.user.SortableUserCollection;
import com.cboe.interfaces.internalPresentation.user.UserAccountModel;
import com.cboe.interfaces.internalPresentation.user.UserCollection;
import com.cboe.interfaces.internalPresentation.user.UserCollectionEvent;
import com.cboe.interfaces.internalPresentation.user.UserCollectionListener;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;

import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Default implementation of UserCollection for the subset collection from the master.
 * This subset communicates directly to the master only.
 */
public class DefaultSortableUserCollection implements SortableUserCollection
{
    private UserCollection masterUserCollection = null;
    private List sortedUserCollection = null;

    private boolean usingSortedCollection = false;
    private Comparator sortComparator = null;

    private final List sortListeners = new ArrayList(30);
    private final List globalUserListeners = new ArrayList(30);
    private final Map allUserListeners = new HashMap(101);

    public static final SortableCollectionListener[] DEFAULT_LISTENERS_ARRAY = new SortableCollectionListener[0];

    private UserCollectionListener masterCollectionListener = new UserCollectionListener()
    {
        public void userAdded(UserCollectionEvent event)
        {
            synchronized(DefaultSortableUserCollection.this)
            {
                if(usingSortedCollection)
                {
                    addUserToSortedCollection(event.getUser());
//                    sortedUserCollection.add(event.getUser());
//                    // sort, but don't fire event until the lock is released
//                    sort(false);
                }
            }

//            fireSortEvent(new EventObject(this));
            UserCollectionEvent myNewEvent = new UserCollectionEvent(DefaultSortableUserCollection.this, event.getUser());
            fireGlobalUserAddedEvent(myNewEvent);
        }

        public void userRemoved(UserCollectionEvent event)
        {
            synchronized(DefaultSortableUserCollection.this)
            {
                if(usingSortedCollection)
                {
                    sortedUserCollection.remove(event.getUser());
                }
            }

            UserCollectionEvent myNewEvent = new UserCollectionEvent(DefaultSortableUserCollection.this, event.getUser());
            fireGlobalUserRemovedEvent(myNewEvent);
            fireUserRemovedEvent(event.getUser(), myNewEvent);
        }

        public void userSaved(UserCollectionEvent event)
        {
            UserCollectionEvent myNewEvent = new UserCollectionEvent(DefaultSortableUserCollection.this, event.getUser());
            fireGlobalUserSavedEvent(myNewEvent);
            fireUserSavedEvent(event.getUser(), myNewEvent);
        }
    };

    public DefaultSortableUserCollection(UserCollection masterCollection)
    {
        masterUserCollection = masterCollection;
        masterUserCollection.addListener(masterCollectionListener);
    }

    /**
     * Provides the signal that this collection should cleanup any resources it has.
     * Cleans up collections and removes listener
     */
    public synchronized void cleanup()
    {
        masterUserCollection.removeAllListener(masterCollectionListener);
        sortListeners.clear();
        globalUserListeners.clear();
        allUserListeners.clear();
        sortedUserCollection.clear();
    }

    /**
     * Adds the passed listener to receive events for all users contained by
     * the collection.
     * @param listener to add
     */
    public void addListener(UserCollectionListener listener)
    {
        synchronized( globalUserListeners )
        {
            if( !globalUserListeners.contains(listener) )
            {
                globalUserListeners.add(listener);
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
        synchronized( allUserListeners )
        {
            List userListeners = ( List ) allUserListeners.get(user);

            if( userListeners == null )
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
        synchronized( globalUserListeners )
        {
            globalUserListeners.remove(listener);
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

        synchronized( allUserListeners )
        {
            Set allListeners = allUserListeners.entrySet();

            for( Iterator iterator = allListeners.iterator(); iterator.hasNext(); )
            {
                Map.Entry entry = ( Map.Entry ) iterator.next();
                UserAccountModel user = ( UserAccountModel ) entry.getKey();
                removeListener(listener, user);
            }
        }
    }

    /**
     * Adds a user to backing master collection
     * @param newUser to add
     */
    public void addUser(UserAccountModel newUser)
    {
        masterUserCollection.addUser(newUser);
    }

    /**
     * Adds a user to this User Collection.
     * @param newUser to add
     * @return model that will represent <code>newUser</code> struct that
     * was passed.
     */
    public UserAccountModel addUser(SessionProfileUserDefinitionStruct newUser)
    {
        return masterUserCollection.addUser(newUser);
    }

    /**
     * Determines if this collection contains a user matching the
     * passed user.
     * @param user to check for.
     * @return boolean True if exist, false otherwise.
     */
    public boolean containsUser(UserAccountModel user)
    {
        return masterUserCollection.containsUser(user);
    }

    /**
     * Finds the UserAccountModel that has the passed userId.
     * @param userId of UserAccountModel to locate.
     * @return UserAccountModel contained in collection with matching userId, if not found
     * result will be null
     */
    public UserAccountModel find(String userId)
    {
        return masterUserCollection.find(userId);
    }
    /**
     * Finds the <code>UserAccountModel</code> that has the passed <code>partialUserId</code>.
     * @param   partialUserId - the user id (or leading characters of) whose UserAccountModel we wish to locate.
     * @param   userIdIterator the Iterator used to step thru user ids. Caller decides whether the iterator points
     *          to the first user id, or some other position, such as the id we stopped at in a previous call.
     * @return  UserAccountModel contained in collection with a matching user id, or null if no match found.
     * @since Single Acronym scrum - May 4, 2005 - Shawn Khosravani
     */
    public UserAccountModel findPartial(String partialUserId, Iterator userIdIterator)
    {
        return masterUserCollection.findPartial(partialUserId, userIdIterator);
    }

    /**
     * Returns a sorted set of user ids held in the collection.
     * @return  A sorted set of user ids held in the collection.
     * @since   Single Acronym scrum - May 4, 2005 - Shawn Khosravani
     */
    public SortedSet getSortedUserIdSet()
    {
        return masterUserCollection.getSortedUserIdSet();
    }


    /**
     * Finds all the UserAccountModel's that have the passed user ExchangeAcronym.
     * @param exchangeAcronym of UserAccountModel's to locate.
     * @return array of UserAccountModel contained in collection with matching ExhangeAcronym. If not found, result will
     *         be a zero-length array.
     */
    public UserAccountModel[] getAllUsersForExchangeAcronym(ExchangeAcronym exchangeAcronym)
    {
        return masterUserCollection.getAllUsersForExchangeAcronym(exchangeAcronym);
    }

    /**
     * Returns an array of all known ExchangeAcronym's
     * @return known ExchangeAcronym's from all users
     */
    public ExchangeAcronym[] getAllExchangeAcronyms()
    {
        return masterUserCollection.getAllExchangeAcronyms();
    }

    /**
     * Gets all the ExchangeAcronym's that are for a specific Exchange.
     * @param exchange to find ExchangeAcronym's for
     * @return all ExchangeAcronym's whose exchange is equal to the passed exchange. If none are found, a zero length
     *         array should be returned.
     */
    public ExchangeAcronym[] getExchangeAcronymsForExchange(String exchange)
    {
        return masterUserCollection.getExchangeAcronymsForExchange(exchange);
    }

    /**
     * Attempts to verify if an existing ExchangeAcronym exists for the passed parameters.
     * @param exchange to find existing ExchangeAcronym for
     * @param acronym to find existing ExchangeAcronym for
     * @return if an ExchangeAcronym was found for the passed parameters, it is returned, otherwise, null is returned.
     */
    public ExchangeAcronym findExchangeAcronym(String exchange, String acronym)
    {
        return masterUserCollection.findExchangeAcronym(exchange, acronym);
    }

    /**
     * Gets a specific element from the collection.
     * @param index Row into collection to retrieve.
     * @return UserAccountModel Model containing struct info.
     */
    public synchronized UserAccountModel getUserAccount(int index)
    {
        if(usingSortedCollection)
        {
            return (UserAccountModel)sortedUserCollection.get(index);
        }
        else
        {
            return masterUserCollection.getUserAccount(index);
        }
    }

    /**
     * Gets the number of elements in the collection.
     * @return int
     */
    public synchronized int getSize()
    {
        if(usingSortedCollection)
        {
            return sortedUserCollection.size();
        }
        else
        {
            return masterUserCollection.getSize();
        }
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
        masterUserCollection.removeUser(userModel);
    }

    /**
     * Adds the passed listener to receive events for sort changes.
     * @param listener to add
     */
    public void addListener(SortableCollectionListener listener)
    {
        synchronized(sortListeners)
        {
            if(!sortListeners.contains(listener))
            {
                sortListeners.add(listener);
            }
        }
        addListener(( UserCollectionListener )listener);
    }

    /**
     * Removes the passed listener from receiving events for sort changes.
     * @param listener to remove
     */
    public void removeListener(SortableCollectionListener listener)
    {
        synchronized( sortListeners )
        {
            sortListeners.remove(listener);
        }
        removeListener(( UserCollectionListener ) listener);
    }

    /**
     * Sets the sort for this collection
     */
    public synchronized void setSorter(Comparator comparator)
    {
        if(comparator == null)
        {
            if(getSorter() != null)
            {
                sortedUserCollection = null;
                usingSortedCollection = false;
                sortComparator = null;

                fireSortEvent(new EventObject(this));
            }
        }
        else if(comparator != getSorter())
        {
            sortComparator = comparator;
            usingSortedCollection = true;
            sort();
        }
    }

    /**
     * Gets the sort for this collection
     */
    public synchronized Comparator getSorter()
    {
        return sortComparator;
    }

    /**
     * Gets the full collection as an unmodifiable List.
     */
    public synchronized List getListCollection()
    {
        if(usingSortedCollection)
        {
            return Collections.unmodifiableList(sortedUserCollection);
        }
        else
        {
            return masterUserCollection.getListCollection();
        }
    }

    /**
     * Performs the sorting.  If fireEvent is true an event will be fired after sorting.
     */
    protected synchronized void sort(boolean fireEvent)
    {
        if(getSorter() != null)
        {
            sortedUserCollection = new ArrayList(masterUserCollection.getSize());

            sortedUserCollection.addAll(masterUserCollection.getListCollection());

            Collections.sort(sortedUserCollection, getSorter());

            if(fireEvent)
            {
                fireSortEvent(new EventObject(this));
            }
        }
    }

    /**
     * Performs the sorting and fires a sort event.
     */
    protected void sort()
    {
        sort(true);
    }

    protected void fireSortEvent(EventObject event)
    {
        SortableCollectionListener[] listeners = DEFAULT_LISTENERS_ARRAY;
        synchronized(sortListeners)
        {
            listeners = ( SortableCollectionListener[] ) sortListeners.toArray(listeners);
        }
        for( int i = 0; i < listeners.length; i++)
        {
            SortableCollectionListener sortableCollectionListener = listeners[i];
            sortableCollectionListener.collectionResorted(event);
        }
    }

    protected void fireGlobalUserSavedEvent(UserCollectionEvent event)
    {
        UserCollectionListener[] listeners = MasterUserCollection.DEFAULT_LISTENERS_ARRAY;
        synchronized( globalUserListeners )
        {
            listeners = ( UserCollectionListener[] ) globalUserListeners.toArray(listeners);
        }
        for( int i = 0; i < listeners.length; i++ )
        {
            UserCollectionListener userCollectionListener = listeners[i];
            userCollectionListener.userSaved(event);
        }
    }

    protected void fireGlobalUserRemovedEvent(UserCollectionEvent event)
    {
        UserCollectionListener[] listeners = MasterUserCollection.DEFAULT_LISTENERS_ARRAY;
        synchronized( globalUserListeners )
        {
            listeners = ( UserCollectionListener[] ) globalUserListeners.toArray(listeners);
        }
        for( int i = 0; i < listeners.length; i++ )
        {
            UserCollectionListener userCollectionListener = listeners[i];
            userCollectionListener.userRemoved(event);
        }
    }

    protected void fireGlobalUserAddedEvent(UserCollectionEvent event)
    {
        UserCollectionListener[] listeners = MasterUserCollection.DEFAULT_LISTENERS_ARRAY;
        synchronized( globalUserListeners )
        {
            listeners = ( UserCollectionListener[] ) globalUserListeners.toArray(listeners);
        }
        for( int i = 0; i < listeners.length; i++ )
        {
            UserCollectionListener userCollectionListener = listeners[i];
            userCollectionListener.userAdded(event);
        }
    }

    protected void fireUserSavedEvent(UserAccountModel user, UserCollectionEvent event)
    {
        UserCollectionListener[] listeners = MasterUserCollection.DEFAULT_LISTENERS_ARRAY;
        synchronized( allUserListeners )
        {
            List userListeners = ( List ) allUserListeners.get(user);
            if(userListeners != null)
            {
                synchronized(userListeners)
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
        UserCollectionListener[] listeners = MasterUserCollection.DEFAULT_LISTENERS_ARRAY;
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
        UserCollectionListener[] listeners = MasterUserCollection.DEFAULT_LISTENERS_ARRAY;
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

    private void addUserToSortedCollection(UserAccountModel user)
    {
        Comparator comparator = getSorter();
        int insertionIndex = Collections.binarySearch(sortedUserCollection, user, comparator);
        if(insertionIndex < 0)
        {
            insertionIndex = Math.abs(insertionIndex) - 1;
            sortedUserCollection.add(insertionIndex, user);
        }
        else
        {
            //this should not happen, if the comparator is not written correctly though, it can.
            //so just replace the one at that position
            sortedUserCollection.set(insertionIndex, user);
            Object[] args = {comparator, user.getUserSummaryStruct()};
            GUILoggerHome.find().alarm(getClass().getName() + ":addUserToSortedCollection - trying to add user, " +
                                       "but found an existing user that matched using the comparator.", args);
        }

    }
}

