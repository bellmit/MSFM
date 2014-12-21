package com.cboe.internalPresentation.api;

//import com.cboe.interfaces.internalApplication.*;

import com.cboe.consumers.internalPresentation.UserSessionAdminConsumerFactory;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.internalApplication.SystemAdminSessionManager;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.interfaces.internalApplication.SystemAdminUserAccess;
import com.cboe.interfaces.internalApplication.SystemAdminUserAccessHome;
import com.cboe.interfaces.internalPresentation.SystemAdminAPI;
import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;
import org.omg.CORBA.OBJECT_NOT_EXIST;

/**
 * This factory creates the singleton instance of the SystemAdminUserAccessImpl.
 *
 * @author Keith A. Korecky
 */
public class SystemAdminUserAccessFactory
{
   /**
    * Reference to remote query service.
    */
   private static SystemAdminUserAccess      userAccess = null;

   /**
    * Creates the factory instance.
    *
    * @author Keith A. Korecky
    */
   public SystemAdminUserAccessFactory()
   {
      super();
   }

   /**
    * Creates an instance of the SystemAdminUserAccess
    *
    * @author Keith A. Korecky
    */
   protected static SystemAdminUserAccess create()
   {
      try
      {
         SystemAdminUserAccessHome home = (SystemAdminUserAccessHome)HomeFactory.getInstance().findHome(SystemAdminUserAccessHome.HOME_NAME);
         userAccess = (SystemAdminUserAccess)home.find();
         if ( userAccess == null )
         {
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("Could not find SystemAdminUserAccessHome::SystemAdminUserAccessHome.create()", GUILoggerSABusinessProperty.COMMON);
            }
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find SystemAdminUserAccessHome");
         }
      }
      catch ( CBOELoggableException e )
      {
            if (GUILoggerHome.find().isExceptionOn())
            {
                GUILoggerHome.find().exception(e, "Could not find SystemAdminUserAccessHome::SystemAdminUserAccessHome.create()");
            }

         // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find SystemAdminUserAccessHome");
      }

      return userAccess;
   }

   /**
    * Returns the singleton instance of the SystemAdminUserAccess
    *
    * @author Keith A. Korecky
    */
   protected static SystemAdminUserAccess find()
   {
      return create();
   }

   /**
    * log on to the system admin cas
    * @param logonStruct user log on data structure
    * @return SystemAdminAPI for the user
    * @author Connie Feng
    */
   public static SystemAdminAPI logon(UserLogonStruct logonStruct, EventChannelListener clientListener)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
      CMIUserSessionAdmin          userSessionAdminConsumer;
      SystemAdminUserAccess      userAccesss;
      SystemAdminSessionManager  userSession                = null;
      SystemAdminAPI             theAPI                     = null;

      if ( clientListener == null )
      {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("Must pass an Event Channel Listener in order to logon.", GUILoggerSABusinessProperty.COMMON);
        }
         return theAPI;
      }
      else
      {
            try
            {
                FoundationFramework.getInstance().getSecurityService().authenticateWithPassword(logonStruct.userId, logonStruct.password);
            }
            catch (Exception e)
            {
                GUILoggerHome.find().exception(e, "SystemAdminUserAccessFactory.logon");
                throw ExceptionBuilder.systemException(e.toString(), 0);
            }
            boolean success = FoundationFramework.getInstance().getSecurityService().createClientInterceptor();
            if (!success)
            {
                throw ExceptionBuilder.systemException("Could not create the client interceptor for security", 0);
            }

            EventChannelAdapterFactory.find().setDynamicChannels(true);
            registerClientListener(clientListener);
            userSessionAdminConsumer   = UserSessionAdminConsumerFactory.create(EventChannelAdapterFactory.find());
            userAccess                 = find();
            userSession                = userAccess.logon(logonStruct, userSessionAdminConsumer);

        try
        {
            theAPI = SystemAdminAPIFactory.create(userSession, userSessionAdminConsumer, clientListener);
            return theAPI;
        }
        catch(OBJECT_NOT_EXIST e)
        {
            // indicates the CAS was not found.  Just let it propagate.
            // don't need to do userSession.logout because this would be thrown
            // by find() before userSession is initialized.
            throw e;
        }
        catch (Exception e)
        {
            if (userSession != null)
            {
                userSession.logout();
            }
            GUILoggerHome.find().exception(e, "SystemAdminUserAccessFactory.logon");
            throw ExceptionBuilder.systemException(e.toString(), 0);
        }
      }

   }

    /**
     * Register the event channel listener for specific events
     * @usage can be used to subscribe for events
     * @param clientListener the listener to subscribe
     * @returns none
     */
    protected static void registerClientListener(EventChannelListener clientListener)
    {
        if ( clientListener != null )
        {
            registerForLogoff(clientListener);
            registerForTextMessage(clientListener);
            registerForAuthenticate(clientListener);
            registerForHeartbeat(clientListener);
        }
    }

    /**
     * Unregister the event channel listener for specific events
     * @usage can be used to unsubscribe for events
     * @param clientListener the listener to subscribe
     * @returns none
     */
    protected static void unregisterClientListener(EventChannelListener clientListener)
    {
        if ( clientListener != null )
        {
            // remove the clientListener for ALL channels
            EventChannelAdapterFactory.find().removeChannelListener(clientListener);
        }
    }

    /**
     * Register the event channel listener for the logoff events
     * @usage can be used to subscribe for log off events
     * @param eventChannel to register listener with
     * @param clientListener the listener to subscribe
     * @returns none
     */
    private static void registerForLogoff(EventChannelListener clientListener)
    {
        if ( clientListener != null )
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_LOGOUT, new Integer(0));
            EventChannelAdapterFactory.find().addChannelListener(clientListener, clientListener, key);
        }
    }

    /**
     * Register the event channel listener for the text message events
     * @usage can be used to subscribe for log off events
     * @param eventChannel to register listener with
     * @param clientListener the listener to subscribe
     * @returns none
     */
    private static void registerForTextMessage(EventChannelListener clientListener)
    {
        if ( clientListener != null )
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_TEXT_MESSAGE, new Integer(0));
            EventChannelAdapterFactory.find().addChannelListener(clientListener, clientListener, key);
        }
    }

    /**
     * Register the event channel listener for authenticate events
     * @usage can be used to subscribe for log off events
     * @param clientListener the listener to subscribe
     * @returns none
     */
    private static void registerForAuthenticate(EventChannelListener clientListener)
    {
        if ( clientListener != null )
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_AUTHENTICATION_NOTICE, new Integer(0));
            EventChannelAdapterFactory.find().addChannelListener(clientListener, clientListener, key);
        }
    }

    /**
     * Register the event channel listener for hearbeat events
     * @usage can be used to subscribe for hearbeat events
     * @param clientListener the listener to subscribe
     * @returns none
     */
    private static void registerForHeartbeat(EventChannelListener clientListener)
    {
        if ( clientListener != null )
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_HEARTBEAT, new Integer(0));
            EventChannelAdapterFactory.find().addChannelListener(clientListener, clientListener,key);
        }
    }

}
