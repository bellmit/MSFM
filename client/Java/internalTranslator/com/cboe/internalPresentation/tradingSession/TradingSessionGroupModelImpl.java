// -----------------------------------------------------------------------------------
// Source file: TradingSessionGroupModelImpl
//
// PACKAGE: com.cboe.internalPresentation.product.models
//
// Created: Mar 9, 2004 11:17:00 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingSession;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.cboe.idl.session.TradingSessionGroupStruct;
import com.cboe.interfaces.internalPresentation.tradingSession.TradingSessionModel;
import com.cboe.interfaces.internalPresentation.product.GroupModel;
import com.cboe.internalPresentation.tradingSession.TradingSessionModelFactory;
import com.cboe.internalPresentation.product.GroupModelFactory;
import com.cboe.interfaces.internalPresentation.tradingSession.TradingSessionGroupModel;

public class TradingSessionGroupModelImpl implements TradingSessionGroupModel
{
    private TradingSessionGroupStruct tradingSessionGroupStruct;
    private PropertyChangeSupport propertyEventManager = new PropertyChangeSupport(this);

    private TradingSessionModel tradingSessionModel;
    private GroupModel[] sessionGroups;

    public TradingSessionGroupModelImpl(TradingSessionGroupStruct struct)
    {
        if(struct == null)
        {
            throw new IllegalArgumentException("TradingSessionGroupStruct may not be null.");
        }

        setTradingSessionGroupStruct(struct);
    }

    public void setTradingSessionGroupStruct(TradingSessionGroupStruct newStruct)
    {
        if( newStruct != null )
        {
            TradingSessionGroupStruct oldStruct = getTradingSessionGroupStruct();
            tradingSessionGroupStruct = newStruct;
            tradingSessionModel = TradingSessionModelFactory.createTradingSessionModel(newStruct.session);

            int groupLength = newStruct.groups.length;
            sessionGroups = new GroupModel[groupLength];

            for (int i = 0; i < groupLength; i++)
            {
                sessionGroups[i] = GroupModelFactory.createGroupModel(newStruct.groups[i]);
            }

            propertyEventManager.firePropertyChange(STRUCT_CHANGE_EVENT, oldStruct, newStruct);
        }
    }

    public TradingSessionModel getTradingSession()
    {
        return tradingSessionModel;
    }

    public GroupModel[] getSessionGroups()
    {
        return sessionGroups;
    }

    public TradingSessionGroupStruct getTradingSessionGroupStruct()
    {
        return tradingSessionGroupStruct;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyEventManager.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyEventManager.removePropertyChangeListener(listener);
    }

    public String toString()
    {
        return tradingSessionModel.getSessionName();
    }

} // -- end of class TradingSessionGroupModelImpl
