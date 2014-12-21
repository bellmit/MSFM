/*
 * Created by IntelliJ IDEA.
 * User: HUANGE
 * Date: Mar 1, 2002
 * Time: 11:50:25 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.userServices;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.util.ExchangeAcronymContainer;

import com.cboe.util.*;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.loggingService.*;
import com.cboe.exceptions.SystemException;
import com.cboe.application.shared.ServicesHelper;

import com.cboe.interfaces.application.UserEnablement;
import com.cboe.interfaces.application.UserEnablementHome;
import com.cboe.idl.cmiProduct.ClassStruct;

import java.util.HashMap;

/**
 * This class controls the creation of all user enablement service objects
 * and implements the CBOE Home Pattern.
 *
 * @author Emily Huang
 * @version 03/01/2002
 *
 */
public class UserEnablementHomeImpl extends ClientBOHome implements UserEnablementHome
{
    // Collection of UserEnablements
    private HashMap userIds;
    private HashMap exchangeAcronyms;

    public UserEnablementHomeImpl() {
        super();
        userIds = new HashMap();
        exchangeAcronyms = new HashMap();
    }

    /**
     * This method creates a new UserEnablement object given the userId.
     *
     * @param userId
     * @return the UserEnablement for the requesting user.
     *
     * @author Emily Huang
     */

    public synchronized UserEnablement create(String userId, String exchange, String acronym)
    {
        UserEnablementImpl userEnablementImpl = null;
        ExchangeAcronymContainer exchangeAcronymContainer = null;

        UserEnablement userEnablement = getUserEnablement( userId );

        if (Log.isDebugOn())
        {
            Log.debug("UserEnablementHomeImpl::create userId:exchange:acronym::" + userId + ":" + exchange + ":" + acronym);
        }

        if (userEnablement == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug("UserEnablementHomeImpl::create not found for userId::" + userId);
            }

            // create key container
            exchangeAcronymContainer = new ExchangeAcronymContainer(exchange, acronym);

            // see if exists on exchange/acronym level
            userEnablement = getUserEnablement(exchange, acronym);

            // create full blown new one
            if(userEnablement == null)
            {
                if(Log.isDebugOn())
                {
                    Log.debug("UserEnablementHomeImpl::create not found for userId:exchange:acronym::" + userId + ":" + exchange + ":" + acronym);
                }

                userEnablementImpl = new UserEnablementImpl(userId, exchange, acronym);
                //Every bo object must be added to the container, ?
                addToContainer(userEnablementImpl);

                //Every BOObject create MUST have a name...if the object is to be a managed object, ?
                userEnablementImpl.create(String.valueOf(userEnablementImpl.hashCode()));

                // add to ExchangeAcronyms
                getExchangeAcronyms().put(exchangeAcronymContainer, userEnablementImpl);

                userEnablement = userEnablementImpl;
            }

            // add userId to user table and "share" existing enablement impl
            getUserIds().put(userId, exchangeAcronymContainer);
        }
        return userEnablement;
    }

    /**
    * Finds an instance of the user enablement service that has been created.
    * @param userId
    * @return reference to user enablement service
    * @author Emily Huang
    */
   public UserEnablement find(String userId, String exchange, String acronym)
   {
       UserEnablement enablementService = null;
       enablementService = getUserEnablement(exchange, acronym);

        if ( enablementService == null )
        {
            Log.alarm(this, "failed attempting to locate userEnablement Service for user:exchange:acronym"
                            + userId
                            + ":" + exchange
                            + ":" + acronym
            );
        }

        return enablementService;
   }

    private HashMap getUserIds()
    {
        if(userIds == null)
        {
            userIds = new HashMap();
        }
        return userIds;
    }

    private HashMap getExchangeAcronyms()
    {
        if(exchangeAcronyms == null)
        {
            exchangeAcronyms = new HashMap();
        }
        return exchangeAcronyms;
    }

    public UserEnablement getUserEnablement(String userId)
    {
        UserEnablement userEnablement = null;

        ExchangeAcronymContainer exchangeAcronym = (ExchangeAcronymContainer) getUserIds().get(userId);
        userEnablement = (UserEnablement) getExchangeAcronyms().get(exchangeAcronym);

        return userEnablement;
    }

    private UserEnablement getUserEnablement(String exchange, String acronym)
    {
        UserEnablement userEnablement = null;

        userEnablement = (UserEnablement) getExchangeAcronyms().get(new ExchangeAcronymContainer(exchange,acronym));

        return userEnablement;
    }

    public void clientStart()
        throws Exception
    {

    }

    /**
    * Removes an instance of the user enablement service.
    *
    * @param userId
    * @author Emily Huang
    */
   public synchronized void remove( String userId, String exchange, String acronym )
     {
        UserEnablementImpl userEnablement = (UserEnablementImpl)getUserEnablement(exchange, acronym);
        if (userEnablement!= null)
        {
            getUserIds().remove(userId);

            // check to see if last user for given exchange/acronym
            // cleanup if so
            ExchangeAcronymContainer exchangeAcronymContainer = new ExchangeAcronymContainer(exchange, acronym);
            if (!getUserIds().containsValue(exchangeAcronymContainer))
            {
                userEnablement.cacheCleanUp();
                getExchangeAcronyms().remove(exchangeAcronymContainer);
            }
        }
    }

}
