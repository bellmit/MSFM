//
// -----------------------------------------------------------------------------------
// Source file: TradingSessionModel.java
//
// PACKAGE: com.cboe.internalPresentation.tradingSession;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingSession;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

import com.cboe.idl.cmiConstants.TradingSessionStates;
import com.cboe.idl.session.TradingSessionStrategyDescriptionStruct;
import com.cboe.idl.session.TradingSessionStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.internalPresentation.tradingSession.TradingSessionModel;

import com.cboe.presentation.common.formatters.Utility;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.internalPresentation.api.SystemAdminAPIFactory;

import com.cboe.domain.util.DateWrapper;

/**
 * Encapsulates changes to a TradingSessionStruct, informing others of any changes.
 * @author Troy Wehrle
 */
public class TradingSessionModelImpl implements TradingSessionModel
{
    private TradingSessionStruct tradingSession;
    private PropertyChangeSupport propertyEventManager = new PropertyChangeSupport(this);

    private TradingSessionStrategyDescriptionStruct tradingSessIionStrategyDescription;
    private DateWrapper dateWrapper = new DateWrapper();
    private boolean isModified = false;

    private final String category = this.getClass().getName();

    public TradingSessionModelImpl()
    {
        super();
    }

    public TradingSessionModelImpl(TradingSessionStruct struct)
    {
        this();
        tradingSession = struct;
        setModified(false);
    }

    /**
     * Aborts any changes in the underlying struct within this model to the API.
     */
    public void abortChanges() throws CommunicationException, DataValidationException, NotFoundException, SystemException, AuthorizationException
    {
        if( isModified() )
        {
            setTradingSessionStruct(SystemAdminAPIFactory.find().getTradingSessionByName(getSessionName()));
        }
    }

    /**
     * Add the listener for property changes to the Trading Session attributes.
     * @param listener PropertyChangeListener to receive a callback when a TradingSession
     * property is changed.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyEventManager.addPropertyChangeListener(listener);
    }

    /**
     * Gets the Session abortEndOfSession flag for the struct.
     * @return boolean abortEndOfSession flag
     */
    public boolean isAbortEndOfSession()
    {
        if( getTradingSessionStruct() != null )
        {
            return getTradingSessionStruct().abortEndOfSession;
        }
        return false;
    }

    /**
     * Gets the Session isAutoStartEndOfSession flag for the struct.
     * @return boolean isAutoStartEndOfSession flag
     */
    public boolean isAutoStartEndOfSession()
    {
        if( getTradingSessionStruct() != null )
        {
            return getTradingSessionStruct().autoStartEndOfSession;
        }
        return false;
    }

    /**
     * Gets the Session isLastSessionForBusinessDay flag for the struct.
     * @return boolean isLastSessionForBusinessDay flag
     */
    public boolean isLastSessionForBusinessDay()
    {
        if( getTradingSessionStruct() != null )
        {
            return getTradingSessionStruct().isLastSessionForBusinessDay;
        }
        return false;
    }

    /**
     * Gets the Session EndOfSessionStrategy for the struct.
     * @return String EndOfSessionStrategy flag
     */
    public String getEndOfSessionStrategy()
    {
        if( getTradingSessionStruct() != null )
        {
            return getTradingSessionStruct().endOfSessionStrategy;
        }
        return null;
    }

    /**
     * Gets the End Time for the struct
     * @return Calendar Calendar format of a <code>com.cboe.idl.cmiUtil.DateTimeStruct</code>
     */
    public Calendar getEndTime()
    {
        if( tradingSession != null && tradingSession.endTime != null )
        {
            dateWrapper.setTime(tradingSession.endTime);
            return dateWrapper.getNewCalendar();
        }
        else
        {
            return null;
        }
    }

    /**
     * Gets the Session Destination Code for the struct.
     * @return short destination Code
     */
    public short getDestinationCode()
    {
        return tradingSession.sessionDestinationCode;
    }

    /**
     * Sets the Session Destination Code for the struct.
     */
    public void setDestinationCode(short newCode)
    {
        if( tradingSession.sessionDestinationCode != newCode )
        {
            short oldValue = tradingSession.sessionDestinationCode;
            tradingSession.sessionDestinationCode = newCode;
            setModified(true);
            propertyEventManager.firePropertyChange(DESTINATION_CODE_CHANGE_EVENT, oldValue, newCode);
        }
    }

    /**
     * Gets the Session Name for the struct.
     * @return String session name
     */
    public String getSessionName()
    {
        return tradingSession.sessionName;
    }

    /**
     * Gets the Start Time for the struct
     * @return Calendar Calendar format of a <code>com.cboe.idl.cmiUtil.DateTimeStruct</code>
     */
    public Calendar getStartTime()
    {
        if( tradingSession != null && tradingSession.startTime != null )
        {
            dateWrapper.setTime(tradingSession.startTime);
            return dateWrapper.getNewCalendar();
        }
        else
        {
            return null;
        }
    }

    /**
     * Gets the BusinessDay for the struct
     * @return Calendar Calendar format of a <code>com.cboe.idl.cmiUtil.DateTimeStruct</code>
     */
    public Calendar getBusinessDay()
    {
        if( tradingSession != null && tradingSession.businessDay != null )
        {
            dateWrapper.setDate(tradingSession.businessDay);
            return dateWrapper.getNewCalendar();
        }
        else
        {
            return null;
        }
    }

    /**
     * Gets the Session State for the struct.
     * @return short session state
     */
    public short getState()
    {
        return tradingSession.sessionState;
    }

    /**
     * Gets the Session State text representation for the struct.
     * @return String session state
     */
    public String getStateString()
    {
        return Utility.tradingSessionStateToString(tradingSession.sessionState);
    }

    /**
     * Gets the TradingSessionStruct that this model represents.
     * @return com.cboe.idl.cmiSession.TradingSessionStruct
     */
    public TradingSessionStruct getTradingSessionStruct()
    {
        return tradingSession;
    }

    /**
     * Gets the TradingSessionStruct that this model represents.
     * @return com.cboe.idl.cmiSession.TradingSessionStruct
     */
    public com.cboe.idl.cmiSession.TradingSessionStruct getCmiTradingSessionStruct()
    {
        com.cboe.idl.cmiSession.TradingSessionStruct cmiStruct = new com.cboe.idl.cmiSession.TradingSessionStruct();
        cmiStruct.endTime = tradingSession.endTime;
        cmiStruct.sequenceNumber = tradingSession.sequenceNumber;
        cmiStruct.sessionName = tradingSession.sessionName;
        cmiStruct.startTime = tradingSession.startTime;
        cmiStruct.state = tradingSession.sessionState;
        return cmiStruct;
    }

    /**
     * Determines if the underlying struct has been modified.
     * @return True if it has been modified, false otherwise.
     */
    public boolean isModified()
    {
        return isModified;
    }

    /**
     * Removes the listener for property changes to the Trading Session attributes.
     * @param listener PropertyChangeListener to remove from receiving callbacks when a TradingSession
     * property is changed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyEventManager.removePropertyChangeListener(listener);
    }

    /**
     * Saves any changes in the underlying struct within this model to the API.
     * @exception CommunicationException
     * @exception DataValidationException
     * @exception TransactionFailedException
     * @exception SystemException
     * @exception AuthorizationException
     */
    public void saveChanges() throws CommunicationException, DataValidationException, NotFoundException, TransactionFailedException, SystemException, AuthorizationException
    {
        if( isModified() )
        {
            SystemAdminAPIFactory.find().modifyTradingSession(getTradingSessionStruct());
            setTradingSessionStruct(SystemAdminAPIFactory.find().getTradingSessionByName(getSessionName()));

            setModified(false);

            propertyEventManager.firePropertyChange(SAVED_CHANGE_EVENT, new Boolean(true), new Boolean(false));
        }
    }

    /**
     * Sets the End Time for the struct.
     * @param newTime session end time
     */
    public void setEndTime(Calendar newTime)
    {
        boolean changeMade = false;

        if( newTime == null )
        {
            throw new IllegalArgumentException("Illegal calendar. Must be a non-null value.");
        }
        else
        {
            if( getEndTime() == null )
            {
                changeMade = true;
            }
            else
            {
                if( !getEndTime().equals(newTime) )
                {
                    changeMade = true;
                }
            }
        }

        if( changeMade )
        {
            Calendar oldValue = getEndTime();
            dateWrapper.setDate(newTime);
            tradingSession.endTime = dateWrapper.toTimeStruct();
            setModified(true);
            propertyEventManager.firePropertyChange(END_TIME_CHANGE_EVENT, oldValue, newTime);
        }
    }

    /**
     * Sets the Business DAy  for the struct.
     * @param newBusinessDay new business day
     */
    public void setBusinessDay(Calendar newBusinessDay)
    {
        boolean changeMade = false;

        if( newBusinessDay == null )
        {
            throw new IllegalArgumentException("Illegal calendar. Must be a non-null value.");
        }
        else
        {
            if( getBusinessDay() == null )
            {
                changeMade = true;
            }
            else
            {
                if( !getBusinessDay().equals(newBusinessDay) )
                {
                    changeMade = true;
                }
            }
        }

        if( changeMade )
        {
            Calendar oldValue = getBusinessDay();
            dateWrapper.setDate(newBusinessDay);
            tradingSession.businessDay = dateWrapper.toDateStruct();
            setModified(true);
            propertyEventManager.firePropertyChange(BUSINESS_DAY_CHANGE_EVENT, oldValue, newBusinessDay);
        }
    }


    /**
     * Sets the Session isAutoStartEndOfSession flag for the struct.
     */
    public void setIsAutoStartEndOfSession(boolean flag)
    {
        if( getTradingSessionStruct() != null &&
                getTradingSessionStruct().autoStartEndOfSession != flag )
        {
            boolean oldValue = getTradingSessionStruct().autoStartEndOfSession;
            getTradingSessionStruct().autoStartEndOfSession = flag;
            setModified(true);
            propertyEventManager.firePropertyChange(AUTO_START_EOS_CHANGE_EVENT, oldValue, flag);
        }
    }

    /**
     * Sets the Session abortEndOfSession flag for the struct.
     */
    public void setIsAbortEndOfSession(boolean flag)
    {
        if( getTradingSessionStruct() != null &&
                getTradingSessionStruct().abortEndOfSession != flag )
        {
            boolean oldValue = getTradingSessionStruct().abortEndOfSession;
            getTradingSessionStruct().abortEndOfSession = flag;
            setModified(true);
            propertyEventManager.firePropertyChange(ABORT_EOS_CHANGE_EVENT, oldValue, flag);
        }
    }

    /**
     * Sets the Session isLastSessionForBusinessDay flag for the struct.
     */
    public void setIsLastSessionForBusinessDay(boolean flag)
    {
        if( getTradingSessionStruct() != null &&
                getTradingSessionStruct().isLastSessionForBusinessDay != flag )
        {
            boolean oldValue = getTradingSessionStruct().isLastSessionForBusinessDay;
            getTradingSessionStruct().isLastSessionForBusinessDay = flag;
            setModified(true);
            propertyEventManager.firePropertyChange(IS_LAST_SESSION_FOR_BUSINESS_DAY_CHANGE_EVENT, oldValue, flag);
        }
    }


    /**
     */
    private void setModified(boolean modified)
    {
        this.isModified = modified;
    }

    /**
     * Sets the Session Name for the struct.
     * @param newName session name
     */
    public void setSessionName(String newName)
    {
        boolean changeMade = false;

        if( (getSessionName() == null && newName != null) ||
                (getSessionName() != null && newName == null) )
        {
            changeMade = true;
        }
        else
        {
            if( getSessionName() != null && !getSessionName().equals(newName) )
            {
                changeMade = true;
            }
        }

        if( changeMade )
        {
            String oldValue = getSessionName();
            tradingSession.sessionName = newName;
            setModified(true);
            propertyEventManager.firePropertyChange(TRADING_SESSION_NAME_CHANGE_EVENT, oldValue, newName);
        }
    }

    /**
     * Sets the End of Session Strategy for the struct.
     * @param newStrategy
     */
    public void setEndOfSessionStrategy(String newStrategy)
    {
        boolean changeMade = false;

        if( (getEndOfSessionStrategy() == null && newStrategy != null) ||
                (getEndOfSessionStrategy() != null && newStrategy == null) )
        {
            changeMade = true;
        }
        else
        {
            if( getEndOfSessionStrategy() != null && !getEndOfSessionStrategy().equals(newStrategy) )
            {
                changeMade = true;
            }
        }

        if( changeMade )
        {
            String oldValue = getEndOfSessionStrategy();
            tradingSession.endOfSessionStrategy = newStrategy;
            setModified(true);
            propertyEventManager.firePropertyChange(EOS_STRATEGY_CHANGE_EVENT, oldValue, newStrategy);
        }
    }

    /**
     * Sets the Start Time for the struct.
     * @param newTime session start time
     */
    public void setStartTime(Calendar newTime)
    {
        boolean changeMade = false;

        if( newTime == null )
        {
            throw new IllegalArgumentException("Illegal calendar. Must be a non-null value.");
        }
        else
        {
            if( getStartTime() == null )
            {
                changeMade = true;
            }
            else
            {
                if( !getStartTime().equals(newTime) )
                {
                    changeMade = true;
                }
            }
        }

        if( changeMade )
        {
            Calendar oldValue = getStartTime();
            dateWrapper.setDate(newTime);
            tradingSession.startTime = dateWrapper.toTimeStruct();
            setModified(true);
            propertyEventManager.firePropertyChange(START_TIME_CHANGE_EVENT, oldValue, newTime);
        }
    }

    /**
     * Gets the Session State for the struct.
     * @return short session state
     */
    public void setState(short state)
    {
        if( state != TradingSessionStates.CLOSED && state != TradingSessionStates.OPEN )
        {
            throw new IllegalArgumentException("Invalid state setting.");
        }
        if( state != tradingSession.sessionState )
        {
            short oldValue = tradingSession.sessionState;
            tradingSession.sessionState = state;
            propertyEventManager.firePropertyChange(STATE_CHANGE_EVENT, oldValue, state);
        }

    }

    /**
     * Sets the TradingSessionStruct that this model represents.
     * @param newTradingSession New struct for this model to represent.
     */
    public void setTradingSessionStruct(TradingSessionStruct newTradingSession)
    {
        TradingSessionStruct oldTradingSession = getTradingSessionStruct();
        tradingSession = newTradingSession;

        setModified(false);

        propertyEventManager.firePropertyChange(STRUCT_CHANGE_EVENT, oldTradingSession, tradingSession);
    }

    public TradingSessionStrategyDescriptionStruct getTradingSessionStrategyDescription() throws CommunicationException, SystemException, AuthorizationException
    {
        if( tradingSessIionStrategyDescription == null )
        {
            String strategyName = getEndOfSessionStrategy();
            TradingSessionStrategyDescriptionStruct[] EOSstrategies = SystemAdminAPIFactory.find().getEndOfSessionStrategies();
            //Find strategy for this session
            for( int i = 0; i < EOSstrategies.length; i++ )
            {
                if( EOSstrategies[i].strategyName.equalsIgnoreCase(strategyName) )
                {
                    tradingSessIionStrategyDescription = EOSstrategies[i];
                    break;
                }
            }
            if( tradingSessIionStrategyDescription == null )
            {
                GUILoggerHome.find().alarm(category + ".getTradingSessionStrategyDescription",
                                           "End of SessionStrategy not found. SessionName: " +
                                           this.getSessionName() +
                                           " EOS Strategy: " + getEndOfSessionStrategy());

            }
        }
        return tradingSessIionStrategyDescription;
    }

    public String toString()
    {
        return getSessionName();
    }
}
