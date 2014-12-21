//
// -----------------------------------------------------------------------------------
// Source file: AbstractOldTradingPropertyGroup.java
//
// PACKAGE: com.cboe.internalPresentation.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingProperty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.omg.CORBA.IntHolder;

import com.cboe.idl.property.PropertyGroupStruct;
import com.cboe.idl.tradingProperty.BooleanStruct;
import com.cboe.idl.tradingProperty.DoubleStruct;
import com.cboe.idl.tradingProperty.LongStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.TradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyFactory;
import com.cboe.interfaces.internalPresentation.tradingProperty.OldTradingPropertyGroup;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.internalPresentation.api.SystemAdminAPIFactory;

import com.cboe.domain.property.PropertyFactory;
import com.cboe.domain.tradingProperty.AbstractTradingPropertyGroup;
import com.cboe.domain.tradingProperty.TradingPropertyElements;
import com.cboe.domain.tradingProperty.TradingPropertyFactoryHome;
import com.cboe.domain.tradingProperty.common.SimpleBooleanTradingPropertyImpl;

public abstract class AbstractOldTradingPropertyGroup
        extends AbstractTradingPropertyGroup
        implements OldTradingPropertyGroup
{
    private Method saveMethod;
    private String[] saveMethodParmNames;
    private Method queryMethod;
    private String[] queryMethodParmNames;
    private Method queryAllClassesMethod;
    private String[] queryAllClassesMethodParmNames;

    protected IntHolder sequenceHolder;

    /**
     * Constructor that initializes with the immutable trading session name and class key. WARNING!! THIS CONSTRUCTOR
     * MUST ALWAYS BE EXPOSED. THE FACTORY WILL USE THIS CONSTRUCTOR TO INSTANTIATE THE OBJECT WITH THE IMMUTABLE
     * SESSION NAME AND CLASS KEY.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    protected AbstractOldTradingPropertyGroup(String sessionName, int classKey)
    {
        super(sessionName, classKey);
        sequenceHolder = new IntHolder();
    }

    /**
     * Constructor that initializes with the immutable trading session name, class key and the
     * PropertyServicePropertyGroup to initialize the sub-classes trading property data with.
     * @param sessionName that this TradingPropertyGroup is for
     * @param classKey that this TradingPropertyGroup is for
     * @param propertyGroup to initialize with
     */
    protected AbstractOldTradingPropertyGroup(String sessionName, int classKey,
                                              PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException
    {
        super(sessionName, classKey, propertyGroup);
        sequenceHolder = new IntHolder();
    }

    public Method getQueryMethod()
    {
        return queryMethod;
    }

    public void setQueryMethod(Method queryMethod)
    {
        this.queryMethod = queryMethod;
    }

    public Method getQueryAllClassesMethod()
    {
        return queryAllClassesMethod;
    }

    public void setQueryAllClassesMethod(Method queryAllClassesMethod)
    {
        this.queryAllClassesMethod = queryAllClassesMethod;
    }

    public Method getSaveMethod()
    {
        return saveMethod;
    }

    public void setSaveMethod(Method saveMethod)
    {
        this.saveMethod = saveMethod;
    }

    public String[] getQueryParameterOrder()
    {
        String[] copy = copyStringArray(queryMethodParmNames);
        return copy;
    }

    public void setQueryParameterOrder(String[] queryMethodParmNames)
    {
        this.queryMethodParmNames = copyStringArray(queryMethodParmNames);
    }

    public String[] getQueryAllClassesParameterOrder()
    {
        String[] copy = copyStringArray(queryAllClassesMethodParmNames);
        return copy;
    }

    public void setQueryAllClassesParameterOrder(String[] queryAllClassesMethodParmNames)
    {
        this.queryAllClassesMethodParmNames = copyStringArray(queryAllClassesMethodParmNames);
    }

    public String[] getSaveParameterOrder()
    {
        String[] copy = copyStringArray(saveMethodParmNames);
        return copy;
    }

    public void setSaveParameterOrder(String[] saveMethodParmNames)
    {
        this.saveMethodParmNames = copyStringArray(saveMethodParmNames);
    }

    /**
     * Overriden to use the old TradingProperty service remove method
     */
    public void delete()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException, NotFoundException
    {
        SystemAdminAPIFactory.find().removeTradingProperty(getSessionName(), getClassKey(),
                                                           getTradingPropertyType().getType());

        //this event will fake the rest of the client code into seeing this old trading property
        //as an event based object, even though we don't receive events for it.
        String propertyKey = TradingPropertyFactoryHome.find().buildTradingPropertyKey(this);
        String category = getTradingPropertyType().getPropertyCategory();
        EventChannelAdapter eventChannel = EventChannelAdapterFactory.find();
        ChannelKey channelKey = new ChannelKey(ChannelType.REMOVE_PROPERTY, category);
        ChannelEvent guiEvent = eventChannel.getChannelEvent(this, channelKey, propertyKey);
        eventChannel.dispatch(guiEvent);
    }

    /**
     * Overriden to save this TradingPropertyGroup to persistence, using the save Method.
     */
    public void save()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException, NotFoundException
    {
        TradingProperty[] allTradingProperties = getAllTradingProperties();
        if(allTradingProperties.length > 0)
        {
            Object[] parms = buildSaveParms(allTradingProperties);

            Method saveMethod = getSaveMethod();
            try
            {
                Object returnValue = saveMethod.invoke(SystemAdminAPIFactory.find(), parms);
            }
            catch(IllegalAccessException e)
            {
                SystemException newException = ExceptionBuilder.systemException("Exception calling save method.", 0);
                newException.initCause(e);
                throw newException;
            }
            catch(InvocationTargetException e)
            {
                Throwable cause = e.getCause();
                if(cause instanceof SystemException)
                {
                    throw (SystemException)cause;
                }
                else if(cause instanceof CommunicationException)
                {
                    throw (CommunicationException) cause;
                }
                else if(cause instanceof AuthorizationException)
                {
                    throw (AuthorizationException) cause;
                }
                else if(cause instanceof DataValidationException)
                {
                    throw (DataValidationException) cause;
                }
                else if(cause instanceof TransactionFailedException)
                {
                    throw (TransactionFailedException) cause;
                }
                else if(cause instanceof NotFoundException)
                {
                    throw (NotFoundException) cause;
                }
                else
                {
                    SystemException newException = ExceptionBuilder.systemException("Exception calling save method.", 0);
                    newException.initCause(e);
                    throw newException;
                }
            }
        }

        try
        {
            loadTradingProperties();
        }
        catch(IllegalAccessException e)
        {
            SystemException newException = ExceptionBuilder.systemException("Exception trying to load Trading Properties.", 0);
            newException.initCause(e);
            throw newException;
        }
        catch(InvocationTargetException e)
        {
            Throwable cause = e.getCause();
            if(cause instanceof SystemException)
            {
                throw (SystemException) cause;
            }
            else if(cause instanceof CommunicationException)
            {
                throw (CommunicationException) cause;
            }
            else if(cause instanceof AuthorizationException)
            {
                throw (AuthorizationException) cause;
            }
            else if(cause instanceof DataValidationException)
            {
                throw (DataValidationException) cause;
            }
            else if(cause instanceof TransactionFailedException)
            {
                throw (TransactionFailedException) cause;
            }
            else if(cause instanceof NotFoundException)
            {
                throw (NotFoundException) cause;
            }
            else
            {
                SystemException newException = ExceptionBuilder.systemException("Exception trying to load Trading Properties.", 0);
                newException.initCause(e);
                throw newException;
            }
        }

        //this event will fake the rest of the client code into seeing this old trading property
        //as an event based object, even though we don't receive events for it.
        String compoundKey =
                TradingPropertyFactoryHome.find().buildTradingPropertyKey(getSessionName(), getClassKey(),
                                                                          getTradingPropertyType().getName());
        String category = getTradingPropertyType().getPropertyCategory();
        PropertyGroupStruct struct = getPropertyGroup().getStruct();
        EventChannelAdapter eventChannel = EventChannelAdapterFactory.find();
        ChannelKey channelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY, compoundKey + category);
        ChannelEvent guiEvent = eventChannel.getChannelEvent(this, channelKey, struct);
        eventChannel.dispatch(guiEvent);
    }

    /**
     * Loads this TradingPropertyGroup from persistence, using the query Method.
     */
    public void loadTradingProperties()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException, NotFoundException, IllegalAccessException, InvocationTargetException
    {
        String[] queryMethodParmOrder = getQueryParameterOrder();
        Object[] parms = new Object[queryMethodParmOrder.length];
        for(int i = 0; i < queryMethodParmOrder.length; i++)
        {
            String queryMethodParmName = queryMethodParmOrder[i];
            if(SESSION_PARM_NAME.equalsIgnoreCase(queryMethodParmName))
            {
                parms[i] = getSessionName();
            }
            else if(CLASS_KEY_PARM_NAME.equalsIgnoreCase(queryMethodParmName))
            {
                parms[i] = new Integer(getClassKey());
            }
            else if(SEQUENCE_NUMBER_PARM_NAME.equalsIgnoreCase(queryMethodParmName))
            {
                parms[i] = sequenceHolder;
            }
            else
            {
                GUILoggerHome.find().alarm("Unknown Parameter Name encountered during load. Will be left null.");
            }
        }

        Method queryMethod = getQueryMethod();

        try
        {
            Object returnValue = queryMethod.invoke(SystemAdminAPIFactory.find(), parms);
            processDataFromLoad(returnValue);
        }
        catch(InvocationTargetException e)
        {
            Throwable cause = e.getCause();
            if(cause instanceof SystemException)
            {
                throw (SystemException) cause;
            }
            else if(cause instanceof CommunicationException)
            {
                throw (CommunicationException) cause;
            }
            else if(cause instanceof AuthorizationException)
            {
                throw (AuthorizationException) cause;
            }
            else if(cause instanceof DataValidationException)
            {
                throw (DataValidationException) cause;
            }
            else if(cause instanceof TransactionFailedException)
            {
                throw (TransactionFailedException) cause;
            }
            else if(cause instanceof NotFoundException)
            {
                throw (NotFoundException) cause;
            }
            else
            {
                throw e;
            }
        }
    }

    public OldTradingPropertyGroup[] getAllGroupsForAllClasses(String sessionName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException, IllegalAccessException, InvocationTargetException
    {
        OldTradingPropertyGroup[] newGroups;

        String[] queryMethodParmOrder = getQueryAllClassesParameterOrder();
        Object[] parms = new Object[queryMethodParmOrder.length];
        for(int i = 0; i < queryMethodParmOrder.length; i++)
        {
            String queryMethodParmName = queryMethodParmOrder[i];
            if(SESSION_PARM_NAME.equalsIgnoreCase(queryMethodParmName))
            {
                parms[i] = sessionName;
            }
            else if(TRADING_PROPERTY_TYPE_NUMBER.equalsIgnoreCase(queryMethodParmName))
            {
                parms[i] = new Integer(getTradingPropertyType().getType());
            }
            else
            {
                GUILoggerHome.find().alarm("Unknown Parameter Name encountered during load for all Classes. Will be left null.");
            }
        }

        Method queryMethod = getQueryAllClassesMethod();

        try
        {
            Object returnValue = queryMethod.invoke(SystemAdminAPIFactory.find(), parms);
            newGroups = processAllClassesDataFromLoad(sessionName, returnValue);
        }
        catch(InvocationTargetException e)
        {
            Throwable cause = e.getCause();
            if(cause instanceof SystemException)
            {
                throw (SystemException) cause;
            }
            else if(cause instanceof CommunicationException)
            {
                throw (CommunicationException) cause;
            }
            else if(cause instanceof AuthorizationException)
            {
                throw (AuthorizationException) cause;
            }
            else if(cause instanceof DataValidationException)
            {
                throw (DataValidationException) cause;
            }
            else if(cause instanceof TransactionFailedException)
            {
                throw (TransactionFailedException) cause;
            }
            else if(cause instanceof NotFoundException)
            {
                newGroups = new OldTradingPropertyGroup[0];
            }
            else
            {
                throw e;
            }
        }
        return newGroups;
    }

    protected OldTradingPropertyGroup[] processAllClassesDataFromLoad(String sessionName, Object returnValue)
            throws DataValidationException, InvocationTargetException
    {
        List newTPGList = new ArrayList(200);

        int classKey;
        String stringValue;
        PropertyServicePropertyGroup newGroup;
        int sequenceNumber;

        if(returnValue instanceof Object[])
        {
            Object[] array = (Object[]) returnValue;
            for(int i = 0; i < array.length; i++)
            {
                Object arrayElement = array[i];
                classKey = getClassKeyFromStructObject(arrayElement);

                sequenceNumber = getSequenceNumberFromStructObject(arrayElement);

                newGroup = createNewPropertyServicePropertyGroup(sessionName, classKey);
                stringValue = createEncodedStringValueFromStructObject(arrayElement);

                Property newProperty = PropertyFactory.createProperty(getTradingPropertyType().getName(), stringValue);
                newGroup.addProperty(newProperty);

                TradingPropertyFactory factory = TradingPropertyFactoryHome.find();

                AbstractOldTradingPropertyGroup newTPGroup =
                        (AbstractOldTradingPropertyGroup) factory.createNewTradingPropertyGroup(sessionName,
                                                                                                classKey,
                                                                                                getTradingPropertyType().getName());
                newTPGroup.setPropertyGroup(newGroup);
                newTPGroup.sequenceHolder.value = sequenceNumber;

                newTPGList.add(newTPGroup);
            }
        }

        return (OldTradingPropertyGroup[]) newTPGList.toArray(new OldTradingPropertyGroup[newTPGList.size()]);
    }

    protected String createEncodedStringValueFromStructObject(Object arrayElement)
    {
        String stringValue = "";
        int seqNum;

        if(arrayElement instanceof DoubleStruct)
        {
            DoubleStruct struct = (DoubleStruct) arrayElement;
            seqNum = struct.seqNum;
            double value = struct.doubleValue;
            stringValue = createEncodedStringValueForDouble(value, seqNum);
        }
        else if(arrayElement instanceof LongStruct)
        {
            LongStruct struct = (LongStruct) arrayElement;
            seqNum = struct.seqNum;
            int value = struct.longValue;
            stringValue = createEncodedStringValueForInteger(value, seqNum);
        }
        else if(arrayElement instanceof BooleanStruct)
        {
            BooleanStruct struct = (BooleanStruct) arrayElement;
            seqNum = struct.seqNum;
            boolean value = struct.boolValue;
            stringValue = createEncodedStringValueForBoolean(value, seqNum);
        }
        return stringValue;
    }

    protected int getClassKeyFromStructObject(Object arrayElement)
    {
        int classKey = 0;
        if(arrayElement instanceof DoubleStruct)
        {
            classKey = ((DoubleStruct)arrayElement).classKey;
        }
        else if(arrayElement instanceof LongStruct)
        {
            classKey = ((LongStruct) arrayElement).classKey;
        }
        else if(arrayElement instanceof BooleanStruct)
        {
            classKey = ((BooleanStruct) arrayElement).classKey;
        }
        return classKey;
    }

    protected int getSequenceNumberFromStructObject(Object arrayElement)
    {
        int sequenceNumber = 0;
        if(arrayElement instanceof DoubleStruct)
        {
            sequenceNumber = ((DoubleStruct) arrayElement).seqNum;
        }
        else if(arrayElement instanceof LongStruct)
        {
            sequenceNumber = ((LongStruct) arrayElement).seqNum;
        }
        else if(arrayElement instanceof BooleanStruct)
        {
            sequenceNumber = ((BooleanStruct) arrayElement).seqNum;
        }
        return sequenceNumber;
    }

    protected void processDataFromLoad(Object returnValue)
            throws DataValidationException
    {
        PropertyServicePropertyGroup newGroup = createNewPropertyServicePropertyGroup(getSessionName(), getClassKey());

        String stringValue = "";
        if(returnValue instanceof Double)
        {
            stringValue = createEncodedStringValueForDouble(((Double) returnValue).doubleValue(), sequenceHolder.value);
        }
        else if(returnValue instanceof Integer)
        {
            stringValue = createEncodedStringValueForInteger(((Integer) returnValue).intValue(), sequenceHolder.value);
        }
        else if(returnValue instanceof Boolean)
        {
            stringValue = createEncodedStringValueForBoolean(((Boolean) returnValue).booleanValue(), sequenceHolder.value);
        }

        Property newProperty = PropertyFactory.createProperty(getTradingPropertyType().getName(), stringValue);
        newGroup.addProperty(newProperty);
        setPropertyGroup(newGroup);
    }

    protected Object[] buildSaveParms(TradingProperty[] allTradingProperties)
    {
        String[] saveMethodParmOrder = getSaveParameterOrder();
        Object[] parms = new Object[saveMethodParmOrder.length];
        for(int i = 0; i < saveMethodParmOrder.length; i++)
        {
            String saveMethodParmName = saveMethodParmOrder[i];
            if(SESSION_PARM_NAME.equalsIgnoreCase(saveMethodParmName))
            {
                parms[i] = getSessionName();
            }
            else if(CLASS_KEY_PARM_NAME.equalsIgnoreCase(saveMethodParmName))
            {
                parms[i] = new Integer(getClassKey());
            }
            else if(SEQUENCE_NUMBER_PARM_NAME.equalsIgnoreCase(saveMethodParmName))
            {
                parms[i] = new Integer(sequenceHolder.value);
            }
            else if(VALUE_PARM_NAME.equalsIgnoreCase(saveMethodParmName))
            {
                Class valueType = getSaveMethod().getParameterTypes()[i];
                parms[i] = getSaveParmsData(allTradingProperties, valueType);
            }
            else
            {
                GUILoggerHome.find().alarm("Unknown Parameter Name encountered during save. Will be left null.");
            }
        }
        return parms;
    }

    protected Object getSaveParmsData(TradingProperty[] allTradingProperties, Class dataType)
    {
        Object returnValue = null;

        //should only have one, if more than 1, ignore.
        TradingProperty firstTradingProperty = allTradingProperties[0];
        Property property = firstTradingProperty.getProperty();
        TradingPropertyElements elements = new TradingPropertyElements(property.getValue());

        //must first figure out the type
        if(Integer.class.equals(dataType) || int.class.equals(dataType))
        {
            returnValue = new Integer(elements.getInteger1());
        }
        else if(Double.class.equals(dataType) || double.class.equals(dataType))
        {
            returnValue = new Double(elements.getDouble1());
        }
        else if(Boolean.class.equals(dataType) || boolean.class.equals(dataType))
        {
            returnValue = Boolean.valueOf(SimpleBooleanTradingPropertyImpl.convertInt(elements.getInteger1()));
        }

        return returnValue;
    }

    private PropertyServicePropertyGroup createNewPropertyServicePropertyGroup(String sessionName, int classKey)
    {
        String compoundPropertyKey;
        PropertyServicePropertyGroup newGroup;
        compoundPropertyKey =
            TradingPropertyFactoryHome.find().buildTradingPropertyKey(sessionName, classKey,
                                                           getTradingPropertyType().getName());
        newGroup =
            PropertyFactory.createPropertyGroup(getTradingPropertyType().getPropertyCategory(),
                                                compoundPropertyKey);
        return newGroup;
    }

    private String createEncodedStringValueForDouble(double value, int sequenceNumber)
    {
        String stringValue = TradingPropertyElements.encodeValues(0, 0, 0, value, 0.0, 0.0, sequenceNumber);
        return stringValue;
    }

    private String createEncodedStringValueForInteger(int value, int sequenceNumber)
    {
        String stringValue = TradingPropertyElements.encodeValues(value, 0, 0, 0.0, 0.0, 0.0, sequenceNumber);
        return stringValue;
    }

    private String createEncodedStringValueForBoolean(boolean value, int sequenceNumber)
    {
        int primitiveInt = SimpleBooleanTradingPropertyImpl.convertBoolean(value);
        String stringValue = TradingPropertyElements.encodeValues(primitiveInt, 0, 0, 0.0, 0.0, 0.0, sequenceNumber);
        return stringValue;
    }

    private String[] copyStringArray(String[] arrayToCopy)
    {
        String[] copy = new String[arrayToCopy.length];
        System.arraycopy(arrayToCopy, 0, copy, 0, arrayToCopy.length);
        return copy;
    }
}
