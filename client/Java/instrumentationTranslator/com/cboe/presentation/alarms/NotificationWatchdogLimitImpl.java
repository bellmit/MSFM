//
// -----------------------------------------------------------------------------------
// Source file: NotificationWatchdogLimitImpl.java
//
// PACKAGE: com.cboe.presentation.alarms
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms;

import java.text.ParseException;
import java.util.*;

import com.cboe.idl.alarm.NotificationWatchdogLimitStruct;
import com.cboe.idl.alarmConstants.NotificationWatchdogLimitTypes;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.dateTime.Time;
import com.cboe.interfaces.instrumentation.alarms.NotificationWatchdogLimit;
import com.cboe.interfaces.instrumentation.alarms.NotificationWatchdogLimitMutable;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

import com.cboe.instrumentationCollector.common.watchdog.LimitTypeDataParser;
import com.cboe.instrumentationCollector.watchdogs.validation.WatchdogFieldValidatorFactory;

public class NotificationWatchdogLimitImpl extends AbstractMutableBusinessModel implements NotificationWatchdogLimitMutable
{
    private NotificationWatchdogLimitStruct struct;
    private int[] parsedCountFields;
    private Time[] parsedTimeFields;

    NotificationWatchdogLimitImpl()
    {
        NotificationWatchdogLimitStruct struct = new NotificationWatchdogLimitStruct();
        struct.extensions = "";
        struct.limitType = NotificationWatchdogLimitTypes.WITHIN_TIME;
        struct.countBoundaries = "";
        struct.timeBoundaries = "";
        setStruct(struct);

        setModified(true);
    }

    NotificationWatchdogLimitImpl(NotificationWatchdogLimitStruct struct)
    {
        setStruct(struct);
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if(!isEqual)
        {
            isEqual = obj instanceof NotificationWatchdogLimit;
            if(isEqual)
            {
                NotificationWatchdogLimit castedObj = (NotificationWatchdogLimit) obj;
                isEqual = getExtensionsField().equals(castedObj.getExtensionsField()) &&
                          getLimitType() == castedObj.getLimitType() &&
                          Arrays.equals(getCountBoundaryFields(), castedObj.getCountBoundaryFields()) &&
                          Arrays.equals(getTimeBoundaryFields(), castedObj.getTimeBoundaryFields());
            }
        }

        return isEqual;
    }

    public int hashCode()
    {
        return getId().intValue();
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        NotificationWatchdogLimitImpl newImpl = (NotificationWatchdogLimitImpl) super.clone();

        NotificationWatchdogLimitStruct newStruct = getStruct();
        newImpl.setStruct(newStruct);

        return newImpl;
    }

    @Deprecated public NotificationWatchdogLimitStruct getStruct()
    {
        NotificationWatchdogLimitStruct newStruct = new NotificationWatchdogLimitStruct();
        newStruct.notificationWatchdogLimitId = getId().intValue();
        newStruct.limitType = getLimitType();
        newStruct.extensions = getExtensionsField();

        encodeCountFields();
        newStruct.countBoundaries = struct.countBoundaries;

        encodeTimeFields();
        newStruct.timeBoundaries = struct.timeBoundaries;

        return newStruct;
    }

    @Deprecated public void setStruct(NotificationWatchdogLimitStruct struct)
    {
        checkParam(struct, "NotificationWatchdogLimitStruct");

        try
        {
            WatchdogFieldValidatorFactory.getInstance().validateLimit(0, struct);

            this.struct = struct;
        }
        catch(DataValidationException e)
        {
            throw new IllegalArgumentException("struct is invalid.", e);
        }

        try
        {
            decodeCountFields();
        }
        catch(IllegalArgumentException e)
        {
            parsedCountFields = new int[0];
            DefaultExceptionHandlerHome.find().process(e, "CountFields from Struct were not parsable.");
        }

        try
        {
            decodeTimeFields();
        }
        catch(ParseException e)
        {
            parsedTimeFields = new Time[0];
            DefaultExceptionHandlerHome.find().process(e, "TimeFields from Struct were not parsable.");
        }
    }

    public Integer getId()
    {
        return struct.notificationWatchdogLimitId;
    }

    public short getLimitType()
    {
        return struct.limitType;
    }

    public void setLimitType(short limitType)
    {
        if(getLimitType() != limitType)
        {
            try
            {
                WatchdogFieldValidatorFactory.getInstance().validateLimitType(getId(), limitType);
                struct.limitType = limitType;
                setModified(true);
            }
            catch(DataValidationException e)
            {
                throw new IllegalArgumentException("limitType is invalid.", e);
            }
        }
    }

    public String getExtensionsField()
    {
        return struct.extensions;
    }

    public void setExtensionsField(String extensionsField)
    {
        if(!getExtensionsField().equals(extensionsField))
        {
            try
            {
                WatchdogFieldValidatorFactory.getInstance().validateLimitExtensions(getId(), extensionsField);
                struct.extensions = extensionsField;
                setModified(true);
            }
            catch(DataValidationException e)
            {
                throw new IllegalArgumentException("extensionsField is invalid.", e);
            }
        }
    }

    public int getCountBoundaryField(int fieldIndex)
    {
        return parsedCountFields[fieldIndex];
    }

    public int[] getCountBoundaryFields()
    {
        return parsedCountFields.clone();
    }

    public int getCountBoundaryFieldsSize()
    {
        return parsedCountFields.length;
    }

    public void setCountBoundaryFields(int[] countFieldValues)
    {
        if(countFieldValues != null)
        {
            parsedCountFields = new int[countFieldValues.length];
            System.arraycopy(countFieldValues, 0, parsedCountFields, 0, countFieldValues.length);
        }
        else
        {
            parsedCountFields = new int[0];
        }
        struct.countBoundaries = "";
        setModified(true);
    }

    public void setCountBoundaryField(int index, int countFieldValue)
    {
        parsedCountFields[index] = countFieldValue;
        struct.countBoundaries = "";
        setModified(true);
    }

    public void setCountBoundaryFields(LimitTypeDataParser dataParser)
    {
        setCountBoundaryFields(dataParser.getCountFields());
        struct.countBoundaries = "";
        setModified(true);
    }

    public void addCountBoundaryField(int index, int countFieldValue)
    {
        Integer[] objectFields = new Integer[parsedCountFields.length];
        for(int i = 0; i < parsedCountFields.length; i++)
        {
            objectFields[i] = parsedCountFields[i];
        }
        
        objectFields = (Integer[]) AlarmNotificationWatchdogFactory.addObjectToArray(index, countFieldValue,
                                                                                     objectFields);

        parsedCountFields = new int[objectFields.length];
        for(int i = 0; i < objectFields.length; i++)
        {
            parsedCountFields[i] = objectFields[i];
        }
        struct.countBoundaries = "";
        setModified(true);
    }

    public void removeCountBoundaryField(int index)
    {
        Integer[] objectFields = new Integer[parsedCountFields.length];
        for(int i = 0; i < parsedCountFields.length; i++)
        {
            objectFields[i] = parsedCountFields[i];
        }

        objectFields = (Integer[]) AlarmNotificationWatchdogFactory.removeObjectFromArray(index, objectFields);

        parsedCountFields = new int[objectFields.length];
        for(int i = 0; i < objectFields.length; i++)
        {
            parsedCountFields[i] = objectFields[i];
        }
        struct.countBoundaries = "";
        setModified(true);
    }

    public Time getTimeBoundaryField(int fieldIndex)
    {
        return parsedTimeFields[fieldIndex];
    }

    public Time[] getTimeBoundaryFields()
    {
        return parsedTimeFields.clone();
    }

    public int getTimeBoundaryFieldsSize()
    {
        return parsedTimeFields.length;
    }

    public void setTimeBoundaryFields(Time[] timeFieldValues)
    {
        if(timeFieldValues != null)
        {
            parsedTimeFields = new Time[timeFieldValues.length];
            System.arraycopy(timeFieldValues, 0, parsedTimeFields, 0, timeFieldValues.length);
        }
        else
        {
            parsedTimeFields = new Time[0];
        }
        struct.timeBoundaries = "";
        setModified(true);
    }

    public void setTimeBoundaryField(int index, Time timeFieldValue)
    {
        parsedTimeFields[index] = timeFieldValue;
        struct.timeBoundaries = "";
        setModified(true);
    }

    public void setTimeBoundaryFields(LimitTypeDataParser dataParser)
    {
        setTimeBoundaryFields(dataParser.getTimeFields());
        struct.timeBoundaries = "";
        setModified(true);
    }

    public void addTimeBoundaryField(int index, Time timeFieldValue)
    {
        parsedTimeFields = (Time[]) AlarmNotificationWatchdogFactory.addObjectToArray(index, timeFieldValue,
                                                                                      parsedTimeFields);
        struct.timeBoundaries = "";
        setModified(true);
    }

    public void removeTimeBoundaryField(int index)
    {
        parsedTimeFields = (Time[]) AlarmNotificationWatchdogFactory.removeObjectFromArray(index, parsedTimeFields);
        struct.timeBoundaries = "";
        setModified(true);
    }

    private void decodeCountFields()
    {
        int[] countFields = LimitTypeDataParser.decodeCountFields(struct.countBoundaries);
        parsedCountFields = countFields;
    }

    private void encodeCountFields()
    {
        String countFields = LimitTypeDataParser.encodeCountFields(getCountBoundaryFields());
        struct.countBoundaries = countFields;
    }

    private void decodeTimeFields()
            throws ParseException
    {
        Time[] timeFields = LimitTypeDataParser.decodeTimeFields(struct.timeBoundaries);
        parsedTimeFields = timeFields;
    }

    private void encodeTimeFields()
    {
        String timeFields = LimitTypeDataParser.encodeTimeFields(getTimeBoundaryFields());
        struct.timeBoundaries = timeFields;
    }
 }
