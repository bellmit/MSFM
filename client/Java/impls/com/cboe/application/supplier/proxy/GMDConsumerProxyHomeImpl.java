package com.cboe.application.supplier.proxy;

import java.util.*;

import com.cboe.interfaces.application.SessionManager;

import com.cboe.domain.util.ExchangeFirmStructContainer;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.*;
import com.cboe.interfaces.domain.*;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelListener;
import com.cboe.domain.supplier.proxy.*;

/**
 * GMDConsumerProxyHomeImpl.
 * @author Emily Huang
 * @author Jimmy Wang
 */
public class GMDConsumerProxyHomeImpl
    extends BaseConsumerProxyHomeImpl
    implements GMDProxyHome
{
    //--------------------------------------------------------------------------
    // data members
    //--------------------------------------------------------------------------
    private ProxyTracker    userTracker;
    private ProxyTracker    firmTracker;


    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------
    /**
     * Default constructor
     */
    protected GMDConsumerProxyHomeImpl()
    {
        userTracker = new ProxyTracker("user");
        firmTracker = new ProxyTracker("firm");
    }


    //--------------------------------------------------------------------------
    // GMDProxyHome methods
    //--------------------------------------------------------------------------
    /**
     * Makes sure that the given consumer proxy is removed from all maps that
     * are managed by this tracker object.
     *
     * @param listener The consumer proxy to be removed.  Note that this class
     * checks the actual class of the listener that's passed in, which must be
     * an instance of the GMDSupplierProxy class.  If not, no action will be
     * taken.
     */
    public synchronized void cleanUpGMDProxy(ChannelListener listener)
    {
        if (listener instanceof GMDSupplierProxy)
        {
            GMDSupplierProxy proxy = (GMDSupplierProxy) listener;

            try {
                // Extract the possible keys for this proxy: the user ID and the
                // ExchangeFirmStructContainer object.
                Object userKey = getKey(proxy, true);  // true  == for user
                Object firmKey = getKey(proxy, false); // false == for firm

                // Now have our proxy tracker objects remove this proxy by their
                // corresponding keys.  We have to remove the proxy from both
                // trackers because we don't know which one it might be in.
                int numUserProxiesRemoved = userTracker.cleanUpProxy(userKey, proxy);
                int numFirmProxiesRemoved = firmTracker.cleanUpProxy(firmKey, proxy);

                if ((numUserProxiesRemoved + numFirmProxiesRemoved) == 0)
                {
                    Log.alarm(this,
                              "No GMD proxy "+ proxy + " currently registered for user " +
                              userKey + " or firm " + firmKey + ".");
                }
            }
            catch (Exception e)
            {
                Log.exception(this, e);
            }
        }
        else
        {
            Log.alarm(this, "Class cast error.  Attempt to remove a GMD proxy that is not an instance of GMDSupplierProxy.");
        }
    }


    //--------------------------------------------------------------------------
    // protected methods
    //--------------------------------------------------------------------------
    /*
     * Adds a user-level GMD proxy.
     */
    protected synchronized void addGMDProxy(boolean forUser,
                                            GMDSupplierProxy proxy)
        throws  DataValidationException
    {
        addGMDProxy(forUser, proxy, null);
    }

    /*
     * Adds a class-level GMD proxy.
     */
    protected synchronized void addGMDProxy(boolean forUser,
                                            GMDSupplierProxy proxy,
                                            Integer classKey)
        throws  DataValidationException
    {
        Object key = getKey(proxy, forUser);
        ProxyTracker tracker = (forUser ? userTracker : firmTracker);

        if (classKey == null)
            tracker.addProxy(key, proxy);
        else
            tracker.addProxy(key, proxy, classKey);
    }

    /**
     * Removes a user-level GMD proxy.
     */
    protected synchronized void removeGMDProxy(boolean forUser,
                                               GMDSupplierProxy proxy)
    {
        removeGMDProxy(forUser, proxy, null);
    }

    /**
     * Removes a class-level GMD proxy.
     */
    protected synchronized void removeGMDProxy(boolean forUser,
                                               GMDSupplierProxy proxy,
                                               Integer classKey)
    {
        Object key = getKey(proxy, forUser);
        ProxyTracker tracker = (forUser ? userTracker : firmTracker);

        if (classKey == null)
            tracker.removeProxy(key, proxy);
        else
            tracker.removeProxy(key, proxy, classKey);
    }


    //--------------------------------------------------------------------------
    // private methods
    //--------------------------------------------------------------------------
    /**
     * Returns the appropriate key object for the given proxy, based on whether
     * it's for a user or a firm.
     */
    private Object getKey(GMDSupplierProxy proxy, boolean forUser)
    {
        Object key = null;

        SessionManager sessionManager =
            (SessionManager) proxy.getSessionManager();

        try
        {
            if (forUser)
            {
                key = sessionManager.getUserId();
            }
            else
            {
                ExchangeFirmStruct firm =
                    sessionManager.getValidSessionProfileUser().defaultProfile.executingGiveupFirm;

                key = new ExchangeFirmStructContainer(firm);
            }
        }
        catch (Exception e)
        {
            Log.exception(e);
        }

        return key;
    }
}


//==============================================================================
// Helper classes
//==============================================================================
/**
 * Objects of this class simply manage the maps that hold the relationships
 * between keys (either user ID strings or ExchangeFirmStructContainer objects)
 * and the consumer proxies that are registered to those keys.
 */
class ProxyTracker
{
    //--------------------------------------------------------------------------
    // data members
    //--------------------------------------------------------------------------
    private String  type;
    private Map     mapIdToProxy;
    private Map     mapIdToSubmap;

    // This map exists only to aid in the cleanup process.  It maps a proxy to
    // all the class keys for which that proxy is a consumer.  When it comes
    // time to clean up the proxy from the submap, the proxy itself is looked
    // up in this map, and if it has an entry, its value is used to find all
    // the class keys that have to be deleted from the user's submap.  After
    // that whole process is done, the proxy in question will have been totally
    // cleaned up from the user's submap.
    private Map     mapProxyToClassKeys;


    //--------------------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------------------
    /**
     *
     */
    ProxyTracker(String typeText)
    {
        type                = typeText;
        mapIdToProxy        = new HashMap();
        mapIdToSubmap       = new HashMap();
        mapProxyToClassKeys  = new HashMap();
    }


    //--------------------------------------------------------------------------
    // package methods
    //--------------------------------------------------------------------------
    /**
     * Puts the given proxy into the general proxy map with the given key, IF
     * the given key does not yet exist in either of this object's maps.
     *
     * @param key Either a user ID string (for a normal user) or an
     * ExchangeFirmStructContainer object (for a firm user).
     *
     * @param proxy The consumer proxy.
     *
     * @throws DataValidationException If the proxy cannot be added because the
     * given user/firm already has a consumer registered.
     *
     * @return Returns 'true' if the proxy was added, otherwise 'false'.  If no
     * exception is thrown and this method returns normally, the only time the
     * proxy won't be added is if the given user/firm already has THIS EXACT
     * SAME PROXY already registered.
     */
    boolean addProxy(Object key, GMDSupplierProxy proxy)
        throws DataValidationException
    {
        boolean alreadyRegistered = false;
        boolean throwException = false;
        boolean cleanUpProxy = false;

        // If the given key does not yet exist within our 'key-to-proxy' map,
        // it means that this proxy is not yet registered as the sole GMD
        // proxy for this key (user or firm), which means that it might be okay
        // to use.
        GMDSupplierProxy alreadyRegisteredProxy =
            (GMDSupplierProxy) mapIdToProxy.get(key);

        if (alreadyRegisteredProxy != null)
        {
            if (proxy.equals(alreadyRegisteredProxy))
            {
                // The caller is trying to re-register the same proxy twice.
                // In this case we can safely ignore the request.
                alreadyRegistered = true;
            }
            else
            {
                // We already have some other proxy registered for this key.
                // This is an error condition and we have to throw an exception.
                throwException = true;

                // The passed-in proxy is a different one than the already-
                // registered one.  And since there is already some other proxy
                // registered for all classes (as opposed to a subset of specific
                // classes), it means that this passed-in proxy will not exist
                // in this user's submap (no proxies will).  Therefore, we can
                // safely clean up the passed-in proxy.
                cleanUpProxy = true;
            }
        }
        else
        {
            if (mapIdToSubmap.containsKey(key))
            {
                // This user/firm has already registered some callback for at
                // least one particular class, which means that we can't add
                // this general consumer here.
                throwException = true;

                // Only clean up the passed-in proxy if it is not also already
                // in this user's submap.
                cleanUpProxy = !mapProxyToClassKeys.containsKey(proxy);
            }
        }

        if (throwException)
        {
            if (cleanUpProxy)
            {
                proxy.cleanUp();
            }

            throw ExceptionBuilder.dataValidationException(
                "GMD consumer already exists for " + type + " " + key,
                DataValidationCodes.GMD_LISTENER_ALREADY_REGISTERED);
        }
        else
        {
            if (!alreadyRegistered)
            {
                mapIdToProxy.put(key, proxy);
            }
        }

        return !alreadyRegistered;
    }

    /**
     * Puts the given proxy into the class-specific proxy map for the given key,
     * IF the given key does not yet exist in the general proxy map AND the
     * user/firm doesn't already have any consumer registered for the requested
     * class.
     *
     * @param key Either a user ID string (for a normal user) or an
     * ExchangeFirmStructContainer object (for a firm user).
     *
     * @param proxy The consumer proxy.
     *
     * @param classKey The key for the class for which this proxy is to be
     * registered.  This argument CANNOT be null.
     *
     * @throws DataValidationException If the proxy cannot be added because the
     * given user/firm already has a consumer registered.
     *
     * @return Returns 'true' if the proxy was added, otherwise 'false'.  If no
     * exception is thrown and this method returns normally, the only time the
     * proxy won't be added is if the given user/firm already has THIS EXACT
     * SAME PROXY already registered for the given class.
     */
    boolean addProxy(Object key, GMDSupplierProxy proxy, Integer classKey)
        throws DataValidationException
    {
        if (classKey == null)
        {
            throw new NullPointerException("The 'classKey' argument cannot be null.");
        }

        boolean alreadyRegistered = false;
        boolean cleanUpProxy = false;

        // Right off the bat, if we already have an entry in our general map,
        // it means that this user/firm already has some consumer registered
        // all ALL events, which means that we can't add another one for any
        // specific class.
        GMDSupplierProxy alreadyRegisteredProxy =
            (GMDSupplierProxy)  mapIdToProxy.get(key);
        boolean throwException = (alreadyRegisteredProxy != null);
        Map submap = null;

        if (throwException)
        {
            // Only clean up the passed-in proxy if it's different than the
            // proxy that's already registered for 'all' for this user.
            cleanUpProxy = !alreadyRegisteredProxy.equals(proxy);
        }

        if (!throwException)
        {
            submap = (Map) mapIdToSubmap.get(key);

            if (submap != null)
            {
                alreadyRegisteredProxy = (GMDSupplierProxy) submap.get(classKey);

                if (alreadyRegisteredProxy != null)
                {
                    if (proxy.equals(alreadyRegisteredProxy))
                    {
                        // The caller is trying to re-register the same consumer
                        // for the same class -- ignore it.
                        alreadyRegistered = true;
                    }
                    else
                    {
                        // This user/firm already has some other proxy registered
                        // for this particular class, which means we can't add
                        // this new one!
                        throwException = true;

                        // Only clean up the passed-in proxy if it is not also
                        // already in this user's submap.
                        cleanUpProxy = !mapProxyToClassKeys.containsKey(proxy);
                    }
                }
            }
        }

        if (throwException)
        {
            if (cleanUpProxy)
            {
                proxy.cleanUp();
            }

            throw ExceptionBuilder.dataValidationException(
                "GMD consumer already exists for " + type + "classKey" + classKey + " " + key,
                DataValidationCodes.GMD_LISTENER_ALREADY_REGISTERED);
        }
        else
        {
            if (!alreadyRegistered)
            {
                if (submap == null)
                {
                    submap = new HashMap();
                    mapIdToSubmap.put(key, submap);
                }

                submap.put(classKey, proxy);

                addToCleanupMap(proxy, classKey);
            }
        }

        return !alreadyRegistered;
    }

    /**
     * Removes the given proxy from the given user's class-based proxy submap.
     *
     * @param key
     * @param proxy
     *
     * @return Returns 'true' if the proxy was removed from the main map,
     * otherwise 'false'.
     */
    boolean removeProxy(Object key, GMDSupplierProxy proxy)
    {
        boolean wasRemoved = false;
        GMDSupplierProxy alreadyRegisteredProxy =
            (GMDSupplierProxy) mapIdToProxy.get(key);

        if (alreadyRegisteredProxy != null)
        {
            if (proxy.equals(alreadyRegisteredProxy))
            {
                mapIdToProxy.remove(key);
                wasRemoved = true;
            }
        }

        return wasRemoved;
    }

    /**
     * Removes the given proxy from the given user's class-based proxy submap.
     *
     * @param key
     * @param proxy
     * @param classKey
     *
     * @return
     */
    boolean removeProxy(Object key, GMDSupplierProxy proxy, Integer classKey)
    {
        boolean wasRemoved = false;
        Map submap = (Map) mapIdToSubmap.get(key);

        if (submap != null)
        {
            GMDSupplierProxy alreadyRegisteredProxy =
                (GMDSupplierProxy) submap.get(classKey);

            if (alreadyRegisteredProxy != null)
            {
                // This user has some consumer registered for this class -- make
                // sure that the user they want to remove is the same as the
                // user that's registered.
                if (alreadyRegisteredProxy.equals(proxy))
                {
                    submap.remove(classKey);

                    if (submap.size() == 0)
                    {
                        // This submap is now empty, which means we have to
                        // remove it and its key from the top-level map.
                        mapIdToSubmap.remove(key);
                    }

                    // Now that we've removed this consumer from this class, we
                    // need to also remove this class from this proxy's set of
                    // cleanup classes.
                    Map cleanupMap = (Map) mapProxyToClassKeys.get(proxy);

                    // This cleanup map should never be null, but we'll check
                    // anyway, just to be sure.
                    if (cleanupMap != null)
                    {
                        cleanupMap.remove(classKey);

                        if (cleanupMap.size() == 0)
                        {
                            // We've just removed the last class key for this
                            // proxy, so we can clean up this proxy's cleanup
                            // map.
                            mapProxyToClassKeys.remove(proxy);
                        }
                    }

                    wasRemoved = true;
                }
            }
        }

        return wasRemoved;
    }

    /**
     * Ensures that the given proxy no longer exists within either of this
     * object's two maps.
     *
     * @param key The key for this proxy (either a user ID string or an
     * ExchangeFirmStructContainer object).
     *
     * @param proxy The proxy that is to be removed from both maps.
     *
     * @return Returns the total number of entries that were removed from the
     * maps.
     */
    int cleanUpProxy(Object key, GMDSupplierProxy proxy)
    {
        int numRemoved = (removeProxy(key, proxy) ? 1 : 0);

        // If this proxy was removed from the "all" map, it can't possibly also
        // exist in the "by class" map.
        if (numRemoved == 0)
        {
            numRemoved = removeFromIdToSubmap(key, proxy);
        }

        return numRemoved;
    }


    //--------------------------------------------------------------------------
    // private methods
    //--------------------------------------------------------------------------
    /**
     *
     */
    private void addToCleanupMap(GMDSupplierProxy proxy, Integer classKey)
    {
        Map cleanupMap = (Map) mapProxyToClassKeys.get(proxy);

        if (cleanupMap == null)
        {
            cleanupMap = new HashMap();
            mapProxyToClassKeys.put(proxy, cleanupMap);
        }

        cleanupMap.put(classKey, classKey);
    }

    /**
     * Removes the given proxy from the class-specific 'id-to-submap' map.
     */
    private int removeFromIdToSubmap(Object key, GMDSupplierProxy proxy)
    {
        int numRemoved = 0;
        Map submap = (Map) mapIdToSubmap.get(key);

        if (submap != null)
        {
            // This proxy will have a set that contains all the class keys for
            // which this proxy is registered as a consumer.  Spin through that
            // set of class keys, using each one as a key into this user's
            // submap.
            Map classKeyMap = (Map) mapProxyToClassKeys.get(proxy);

            if (classKeyMap != null)
            {
                Set keys = classKeyMap.keySet();
                Iterator iter = keys.iterator();

                while (iter.hasNext())
                {
                    submap.remove(iter.next());
                    ++numRemoved;
                }

                if (submap.size() == 0)
                {
                    // This submap is now empty, which means we have to remove it
                    // and its key from the top-level map.
                    mapIdToSubmap.remove(key);
                }

                // At this point we no longer need this proxy's entry in our
                // 'proxy-to-class-keys' map.
                mapProxyToClassKeys.remove(proxy);
            }
        }

        return numRemoved;
    }
}

