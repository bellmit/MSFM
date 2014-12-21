//
// -----------------------------------------------------------------------------------
// Source file: AlarmActivationImpl.java
//
// PACKAGE: com.cboe.presentation.alarms
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms;

import org.omg.CORBA.UserException;

import com.cboe.idl.alarm.AlarmActivationStruct;
import com.cboe.idl.alarmConstants.NotificationTypes;

import com.cboe.interfaces.instrumentation.alarms.AlarmActivationMutable;
import com.cboe.interfaces.instrumentation.alarms.AlarmActivation;
import com.cboe.interfaces.instrumentation.alarms.AlarmDefinition;
import com.cboe.interfaces.domain.dateTime.DateTime;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.dateTime.DateTimeImpl;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.api.InstrumentationTranslatorFactory;

import com.cboe.domain.util.DateWrapper;

public class AlarmActivationImpl extends AbstractMutableBusinessModel implements AlarmActivationMutable
{
    private AlarmActivationStruct struct;
    private AlarmDefinition definition;
    private DateTime cachedDateTime;
    private Integer cachedId;

    public AlarmActivationImpl(AlarmDefinition definition)
    {
        super();
        checkParam(definition, "AlarmDefinition");

        AlarmActivationStruct newStruct = new AlarmActivationStruct();
        newStruct.activeStatus = false;
        newStruct.notificationReceiver = "";
        newStruct.notificationType = NotificationTypes.GLOBAL;
        newStruct.lastChanged = DateWrapper.convertToDateTime(System.currentTimeMillis());
        newStruct.definition = definition.getStruct();
        struct = newStruct;

        this.definition = definition;

        cachedDateTime = null;
        cachedId = null;
    }

    public AlarmActivationImpl(AlarmActivationStruct struct)
    {
        super();
        checkParam(struct, "AlarmActivationStruct");
        checkParam(struct.definition, "AlarmActivationStruct.definition");
        this.struct = struct;

        try
        {
            definition = InstrumentationTranslatorFactory.find().getAlarmDefinitionById(struct.definition.definitionId);
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Could not load definition for activation.");
        }

        cachedDateTime = null;
        cachedId = null;
    }

    public AlarmActivationImpl(AlarmActivationStruct struct, AlarmDefinition definition)
    {
        super();
        checkParam(struct, "AlarmActivationStruct");
        checkParam(definition, "AlarmDefinition");
        this.struct = struct;
        this.definition = definition;

        cachedDateTime = null;
        cachedId = null;
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if(!isEqual)
        {
            isEqual = obj instanceof AlarmActivation;
            if(isEqual)
            {
                AlarmActivation castedObj = (AlarmActivation) obj;
                isEqual = (getAlarmDefinition().getId().equals(castedObj.getAlarmDefinition().getId()) &&
                           getNotificationReceiver().equals(castedObj.getNotificationReceiver()) &&
                           getNotificationType() == castedObj.getNotificationType());
            }
        }

        return isEqual;
    }

    public int hashCode()
    {
        if(isSaved())
        {
            return getId().intValue();
        }
        else
        {
            return getAlarmDefinition().hashCode() + getNotificationType();
        }
    }

    public Object clone() throws CloneNotSupportedException
    {
        AlarmActivationStruct newStruct = new AlarmActivationStruct();
        newStruct.activationId = getId().intValue();
        newStruct.activeStatus = isActive();
        newStruct.lastChanged = getLastChanged().getDateTimeStruct();
        newStruct.notificationReceiver = getNotificationReceiver();
        newStruct.notificationType = getNotificationType();
        newStruct.definition = getAlarmDefinition().getStruct();

        AlarmActivationImpl newImpl = new AlarmActivationImpl(newStruct, getAlarmDefinition());
        newImpl.setModified(isModified());

        return newImpl;
    }

    /**
     * Performs a default comparison by <code>getAlarmDefinition().compareTo(Object)</code>.
     */
    public int compareTo(Object obj)
    {
        return getAlarmDefinition().compareTo(((AlarmActivation) obj).getAlarmDefinition());
    }

    public boolean isActive()
    {
        return struct.activeStatus;
    }

    public void setActive(boolean activeStatus)
    {
        if(activeStatus != struct.activeStatus)
        {
            boolean oldValue = struct.activeStatus;
            struct.activeStatus = activeStatus;
            setModified(true);
            firePropertyChange(ACTIVE_PROPERTY, oldValue, activeStatus);
        }
    }

    public String getNotificationReceiver()
    {
        return struct.notificationReceiver;
    }

    public void setNotificationReceiver(String notificationReceiver)
    {
        checkParam(notificationReceiver, "notificationReceiver");
        if(!notificationReceiver.equals(struct.notificationReceiver))
        {
            String oldValue = struct.notificationReceiver;
            struct.notificationReceiver = notificationReceiver;
            setModified(true);
            firePropertyChange(NOTIFICATION_RECEIVER_PROPERTY, oldValue, notificationReceiver);
        }
    }

    public short getNotificationType()
    {
        return struct.notificationType;
    }

    public void setNotificationType(short notificationType)
    {
        if(notificationType != struct.notificationType)
        {
            short oldValue = struct.notificationType;
            struct.notificationType = notificationType;
            setModified(true);
            firePropertyChange(NOTIFICATION_TYPE_PROPERTY, oldValue, notificationType);
        }
    }

    public Integer getId()
    {
        if(cachedId == null)
        {
            cachedId = new Integer(struct.activationId);
        }
        return cachedId;
    }

    public DateTime getLastChanged()
    {
        if(cachedDateTime == null)
        {
            cachedDateTime = new DateTimeImpl(struct.lastChanged);
        }
        return cachedDateTime;
    }

    public void setAlarmDefinition(AlarmDefinition definition)
    {
        this.definition = definition;
    }

    public AlarmDefinition getAlarmDefinition()
    {
        return definition;
    }

    /**
     * @deprecated used only for IDL API access
     */
    public AlarmActivationStruct getStruct()
    {
        return struct;
    }

    /**
     * @deprecated used only for IDL API access
     */
    public void setStruct(AlarmActivationStruct struct)
    {
        this.struct = struct;

        try
        {
            definition =
                InstrumentationTranslatorFactory.find().getAlarmDefinitionById(struct.definition.definitionId);
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Could not load definition for activation.");
        }

        cachedDateTime = null;
        cachedId = null;
        setModified(true);
    }

    public boolean isSaved()
    {
        if(getId().intValue() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
