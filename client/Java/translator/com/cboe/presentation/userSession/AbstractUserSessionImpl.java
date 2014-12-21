//
// -----------------------------------------------------------------------------------
// Source file: AbstractUserSessionImpl.java
//
// PACKAGE: com.cboe.presentation.userSession;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.userSession;

import com.cboe.domain.util.CallbackDeregistrationInfoStruct;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiAdmin.HeartBeatStruct;
import com.cboe.interfaces.presentation.permissionMatrix.UserPermissionMatrix;
import com.cboe.interfaces.presentation.user.UserStructModel;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.time.TimeSyncWrapper;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.permissionMatrix.PermissionMatrixFactory;
import com.cboe.presentation.threading.SwingEventThreadWorker;
import com.cboe.util.ReflectiveObjectWriter;
import com.cboe.util.event.EventChannelListener;
import org.omg.CORBA.UserException;

import javax.swing.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Implements a UserSession for the client GUI.
 */
public abstract class AbstractUserSessionImpl implements UserSession
{
    protected final static long HEARTBEAT_START_DELAY = 30 * 1000; // timer delay start 30 seconds
    protected final static long DEFAULT_HEARTBEAT_TIMER_INTERVAL = 25 * 1000; // default to checking for a heartbeat every 25 seconds
    protected final static int NO_HEARTBEAT = 0;

    private Long heartBeatInterval;
    private int heartBeatFrequency; // how many heartbeats received in the last getHeartbeatTimerInterval()

    public static final String VERSION_PROPERTY_CATEGORY_KEY = "Session";
    public static final String VERSION_PROPERTY_NAME_KEY = "CMICompatibleVersion";

    protected boolean isLoggedIn = false;
    protected Date loginTime = null;
    protected Date logoutTime = new Date();
    private UserSessionListener listeners = null;
    protected UserSessionLoginInfo loginInfo = null;
    protected UserStructModel userModel = null;
    protected int userSessionType = NORMAL_USER_SESSION;

    protected static final String AUTOCLOSE_WAITTIME_KEY = "AutoCloseWaitTime";

    protected static final long DEFAULT_AUTOCLOSE_WAIT = 5;

    private Timer autoClose;

    /**
     * Constructor
     */
    public AbstractUserSessionImpl()
    {
        super();
    }

    /**
     * Returns the default login session type for this session.
     * @return Either LOGIN_SESSION_TYPE_PRIMARY, LOGIN_SESSION_TYPE_SECONDARY, LOGIN_SESSION_TYPE_NOT_SPECIFIED
     */
    public abstract int getDefaultLoginSessionType();

    /**
     * Gets the EventChannelListener implementation for this session.
     * @return Implementation of EventChannelListener
     */
    public abstract EventChannelListener getSessionsEventChannelListener();

    /**
     * Attempts to login in a user.
     * @param loginInfo to use for login.
     * @return true if logged in successfully, false otherwise.
     * @exception AuthorizationException log in failed. Probably bad login information.
     * @exception UserException another type of fatal failure occurred.
     */
    public abstract boolean login(UserSessionLoginInfo loginInfo) throws AuthorizationException, UserException, Exception;

    /**
     * Attempts to logout a user.
     * @return true if logged out successfully, false otherwise.
     */
    public abstract boolean logout();

    /**
     * Informs the session that it has already been forced to logout.
     */
    public abstract void forcedLogout();

    /**
     * This message will be displayed to the user when the GUI app has not received
     * a heartbeat from the CAS within the num millis returned by
     * getHeartbeatTimerPeriod()
     */
    protected abstract String getHeartBeatLostMessage();

    // Subclasses will be responsible for creating the auto-close warning dialog because this
    // is in the translator layer, which doesn't have access to some necessary GUI classes
    protected abstract JDialog createAutoCloseDlg(String title, String message);

    /**
     * Returns the number of milliseconds that will pass between checking the
     * heartbeat frequency.
     */
    protected synchronized long getHeartbeatTimerInterval()
    {
        if(heartBeatInterval == null)
        {
            heartBeatInterval = getAppPropertyLongValue(PROPERTIES_TIMER_SECTION_NAME, HEARTBEAT_INTERVAL_PROP_KEY, DEFAULT_HEARTBEAT_TIMER_INTERVAL);
        }
        // HEARTBEAT_INTERVAL_PROP_KEY is stored as number of milliseconds
        return heartBeatInterval;
    }

    /**
     * Changes the password on the current session.
     * @param oldPassword that user is currently logged in as.
     * @param newPassword to change it to.
     * @return True is change successful, false otherwise.
     * @exception AuthorizationException Change password failed for security reasons.
     * Probably used wrong <code>oldPassword</code> or user not currently logged in.
     * @exception UserException another type of fatal failure occurred.
     * @exception IllegalArgumentException old password was invalid or new password was not required length.
     */
    public boolean changePassword(String oldPassword, String newPassword) throws AuthorizationException, UserException, IllegalArgumentException
    {
        if(!isLoggedIn())
        {
            throw new IllegalStateException("Session is not logged in.");
        }

        if(loginInfo.getPassword().equals(oldPassword))
        {
            if(newPassword.length() >= MINIMUM_PASSWORD_LENGTH)
            {
                APIHome.findCommonAPI().changePassword(oldPassword, newPassword);
                loginInfo.setPassword(newPassword);

                UserSessionEvent event = new UserSessionEvent(this, new Date(), UserSessionEvent.PASSWORD_CHANGE_EVENT);
                fireUserSessionEvent(event);

                return true;
            }
            else
            {
                throw new IllegalArgumentException("New password length is less than required length of " + MINIMUM_PASSWORD_LENGTH + ".");
            }
        }
        else
        {
            throw new IllegalArgumentException("Invalid old password.");
        }
    }

    /**
     * Determines if the user is currently logged in.
     * @return true if logged in, false otherwise.
     */
    public boolean isLoggedIn()
    {
        return isLoggedIn;
    }

    /**
     * Determines if this user is logged in as a primary login.
     * @return True if logged in as a primary login, false otherwise.
     */
    public boolean isPrimaryLogin()
    {
        return getUserLoginInfo().getSessionLoginType() == LOGIN_SESSION_TYPE_PRIMARY;
    }

    /**
     * Determines if this user is logged in as a secondary login.
     * @return True if logged in as a secondary login, false otherwise.
     */
    public boolean isSecondaryLogin()
    {
        return getUserLoginInfo().getSessionLoginType() == LOGIN_SESSION_TYPE_SECONDARY;
    }

    /**
     * Gets the time the user logged in.
     * @return Time user logged in. Should return null if not logged in.
     */
    public Calendar getTimeLoggedIn()
    {
        if(!isLoggedIn() || loginTime == null)
        {
            return null;
        }
        else
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(loginTime);
            return cal;
        }
    }


    /**
     * Gets the time the user logged out.
     * @return Time user logged out. Should return null if logged in.
     */
    public Calendar getTimeLoggedOut()
    {
        if(isLoggedIn() || logoutTime == null)
        {
            return null;
        }
        else
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(logoutTime);
            return cal;
        }
    }

    /**
     * Gets the user model this session represents.
     * @return UserStructModel containing user information. Returns null if not logged in.
     */
    public UserStructModel getUserModel()
    {
        return userModel;
    }

    /**
     * Gets the permission Matrix for current user.
     * @return UserPermissionMatrix containing user permissions.
     */
    public UserPermissionMatrix getUserPermissionMatrix()
    {
        return PermissionMatrixFactory.findUserPermissionMatrix();
    }

    /**
     * Gets the user login info that was used to login with.
     * @return UserSessionLoginInfo containing login information. Returns null if not logged in.
     */
    public UserSessionLoginInfo getUserLoginInfo()
    {
        return loginInfo;
    }

    /**
     * Adds a listener to received events for UserSession changes.
     * @param listener to add
     */
    public void addUserSessionListener(UserSessionListener listener)
    {
        listeners = UserSessionEventMulticaster.add(listeners, listener);
    }

    /**
     * Removes a listener from receiving events for UserSession changes.
     * @param listener to remove
     */
    public void removeUserSessionListener(UserSessionListener listener)
    {
        listeners = UserSessionEventMulticaster.remove(listeners, listener);
    }

    /**
     * Fires the event to the multicaster for broadcast.
     * @param event to send
     */
    protected void fireUserSessionEvent(UserSessionEvent event)
    {
        if(listeners != null)
        {
            listeners.userSessionChange(event);
        }
    }

    /**
     * Sets whether this user was logged in successful or not. Must be called immediately after
     * a successful login.
     * @param success True if user is logged in, false otherwise.
     * @param loginInfo used to login in. Must not be null if success is true.
     * @exception SystemException occurred when we tried to get the user struct from the API. Will
     * only occur during <code>setLoggedIn(true, loginInfo)</code>.
     * @exception CommunicationException occurred when we tried to get the user struct from the API. Will
     * only occur during <code>setLoggedIn(true, loginInfo)</code>.
     * @exception AuthorizationException occurred when we tried to get the user struct from the API. Will
     * only occur during <code>setLoggedIn(true, loginInfo)</code>.
     */
    protected void setLoggedIn(boolean success, UserSessionLoginInfo loginInfo) throws SystemException, CommunicationException, AuthorizationException
    {
        if(success && loginInfo == null)
        {
            throw new IllegalArgumentException("loginInfo must not be null if success was true.");
        }

        if(success)
        {
            this.loginInfo = loginInfo;

            loginTime = new Date();
            logoutTime = null;
            isLoggedIn = true;

            userModel = APIHome.findCommonAPI().getValidUser();
        }
        else
        {
            if(isLoggedIn())
            {
                logoutTime = new Date();
            }
            else
            {
                logoutTime = null;
            }

            loginTime = null;
            isLoggedIn = false;

            userModel = null;
            this.loginInfo = null;
        }
    }

    protected String writeObject(Object object)
    {
        StringWriter structStringWriter = new StringWriter();
        String contentsStringWriter = null;

        try
        {
            //have the holdWindowTitle passed in a blank, the 2nd parameter
            ReflectiveObjectWriter.writeObject(object, " ", structStringWriter);
            contentsStringWriter = structStringWriter.toString();
        }
        catch (IOException e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }

        return contentsStringWriter;
    }

    protected void handleUnregisterCallback(CallbackDeregistrationInfoStruct aCallbackDeregistrationInfoStruct )
    {
        StringBuffer message = new StringBuffer();
        message.append("Consumer unregistered. You should consider logging off\n");
        message.append("Interface name:");
        message.append(aCallbackDeregistrationInfoStruct.getCallbackInformationStruct().subscriptionInterface+"\n");
        message.append("Operation:");
        message.append(aCallbackDeregistrationInfoStruct.getCallbackInformationStruct().subscriptionOperation+"\n");
        message.append("Value:");
        message.append(aCallbackDeregistrationInfoStruct.getCallbackInformationStruct().subscriptionValue+"\n");
        String textMsg = message.toString();
        Exception ex = new Exception(textMsg);
        DefaultExceptionHandlerHome.find().process(ex, "Consumer unregistered, you should consider logging off.");
//        GUILoggerHome.find().alarm("AbstractUserSession - CB_UNREGISTER_LISTENER ",textMsg);
    }


    protected UserSessionLoginInfo changeVersionNumber(UserSessionLoginInfo loginInfo)
    {
        if(AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String compatibleVersion = AppPropertiesFileFactory.find().getValue(VERSION_PROPERTY_CATEGORY_KEY,
                                                                         VERSION_PROPERTY_NAME_KEY);
            if(compatibleVersion != null && compatibleVersion.length() > 0)
            {
                loginInfo.setVersion(compatibleVersion);
            }
        }

        return loginInfo;
    }

	public int getUserSessionType() {
		return this.userSessionType;
	}

	public void setUserSessionType(int newUserSessionType) {
		this.userSessionType = newUserSessionType;
	}

    /**
     * Calculates the offset time from the time passed
     * in the heartBeat struct.
     * @param heartBeat
     */
    protected void calculateOffsetTimeFromHeartbeat(HeartBeatStruct heartBeat)
    {
        TimeSyncWrapper.calculateTimeOffset(heartBeat);
    }

    /**
     * This method starts the Timer that checks for the heartbeat from the CAS every 'getHeartbeatTimerInterval()'
     * milliseconds.  If getHeartbeatTimerInterval() returns a number <= 0, the timer will not be started and the
     * GUI will not monitor heartbeat frequency, so the user will not be forced out if the GUI doesn't receive any
     * heartbeats.
     */
    protected void startHeartBeatTimer()
    {
        long interval = getHeartbeatTimerInterval();
        if(interval > 0)
        {
            heartBeatFrequency = NO_HEARTBEAT;
            Timer heartBeatTimer = new Timer("HeartbeatTimer");
            heartBeatTimer.scheduleAtFixedRate(getHeartBeatTask(), HEARTBEAT_START_DELAY, interval);
            GUILoggerHome.find().debug(getClass().getName() + ".startHeartBeatTimer()", GUILoggerBusinessProperty.USER_SESSION, "HeartBeat will be checked every '" + interval + "' milliseconds.");
        }
        else
        {
            GUILoggerHome.find().debug(getClass().getName() + ".startHeartBeatTimer()", GUILoggerBusinessProperty.USER_SESSION, "HeartBeat interval '" + interval + "' is not greater than zero, so HeartBeats will not be monitored.");
        }
    }

    protected int incrementHeartBeatCount()
    {
        if(getHeartbeatTimerInterval() > 0)
        {
            return ++heartBeatFrequency;
        }
        return NO_HEARTBEAT;
    }

    protected TimerTask getHeartBeatTask()
    {
        return new TimerTask()
        {
            public void run()
            {
                if (GUILoggerHome.find().isDebugOn())
                {
                    GUILoggerHome.find().debug("HeartBeat Timer Check. " + heartBeatFrequency + " heartbeats", GUILoggerBusinessProperty.USER_SESSION);
                }

                if (heartBeatFrequency == NO_HEARTBEAT) // if we have no heartbeat, then assume we are not connected to the CAS anymore
                {
                    GUILoggerHome.find().alarm("HeartBeat lost, shutting down..");

                    SwingEventThreadWorker worker = new SwingEventThreadWorker(true)
                    {
                        public Object process()
                        {
                            forcedLogout();
                            showAutoCloseDlg("HeartBeat Lost", getHeartBeatLostMessage());
                            return null;
                        }
                    };

                    try
                    {
                        worker.doProcess();
                    }
                    catch (Exception e)
                    {
                        DefaultExceptionHandlerHome.find().process(e);
                    }

                    System.exit(0);
                }
                else // if we are not equal to NO_HEARTBEAT, then we have a heartBeatFrequency and reset the heartBeatFrequency to NO_HEARTBEAT
                {
                    heartBeatFrequency = NO_HEARTBEAT;

                    if (GUILoggerHome.find().isDebugOn())
                    {
                        GUILoggerHome.find().debug("We have a heartbeat from the CAS, resetting heartbeat frequency counter.", GUILoggerBusinessProperty.USER_SESSION);
                    }
                }
            }
        };
    }

    /**
     * This shows a warning dialog to the user.  The app will be exited when the
     * user closes the dialog, or when the autoClose Timer pops.
     *
     * @param title   of the dialog
     * @param message to display in the content pane of the dialog
     */
    public void showAutoCloseDlg(String title, String message)
    {
        final JDialog dlg = createAutoCloseDlg(title, message);
        final JOptionPane closePane = new JOptionPane(message, JOptionPane.WARNING_MESSAGE);

        closePane.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent e)
            {
                String prop = e.getPropertyName();

                if (dlg.isVisible() && (e.getSource() == closePane)
                        && (prop.equals(JOptionPane.VALUE_PROPERTY)))
                {
                    if (getAutoCloseTimer() != null)
                    {
                        getAutoCloseTimer().cancel();
                    }

                    dlg.setVisible(false);
                    System.exit(0);
                }
            }
        });

        dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dlg.setTitle(title);
        dlg.setContentPane(closePane);
        dlg.pack();

        startAutoCloseTimer();
        dlg.setVisible(true);
    }

    protected Timer getAutoCloseTimer()
    {
        if(autoClose == null)
        {
            autoClose = new Timer("AutoCloseTimer");
        }
        return autoClose;
    }

    protected void startAutoCloseTimer()
    {
        getAutoCloseTimer().schedule(new TimerTask()
        {
            public void run()
            {
                System.exit(0);
            }
        }, getAutoCloseWaitTime());
    }

    protected long getAutoCloseWaitTime()
    {
        // AUTOCLOSE_WAITTIME_KEY property is stored as number of minutes, so converting to millis
        long minutes = getAppPropertyLongValue(PROPERTIES_TIMER_SECTION_NAME, AUTOCLOSE_WAITTIME_KEY, DEFAULT_AUTOCLOSE_WAIT);
        return minutesToMillis(minutes);
    }

    protected long getAppPropertyLongValue(String propertiesSection, String propertyName, long defaultValue)
    {
        long retValue = defaultValue;
        if (AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String value = AppPropertiesFileFactory.find().getValue(propertiesSection, propertyName);

            try
            {
                retValue = Long.parseLong(value.trim());
            }
            catch (Exception e)
            {
                GUILoggerHome.find().exception(getClass().getName() + ".getAppPropertyLongValue()", "Error parsing section=" +
                        propertiesSection + " property=" + propertyName + " value=" + value, e);
            }
        }

        return retValue;
    }

    protected long minutesToMillis(long mins)
    {
        return (mins * 60L) * 1000L;
    }
}
