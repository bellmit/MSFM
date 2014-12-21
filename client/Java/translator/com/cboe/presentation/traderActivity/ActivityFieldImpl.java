//
// -----------------------------------------------------------------------------------
// Source file: ActivityFieldImpl.java
//
// PACKAGE: com.cboe.presentation.traderActivity;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.traderActivity;

import java.util.*;

import com.cboe.idl.cmiTraderActivity.ActivityFieldStruct;
import com.cboe.interfaces.presentation.activity.ActivityField;
import com.cboe.presentation.common.formatters.ActivityFieldTypes;

/**
 * ***************************************************************************** Represents an activity field which is
 * contained in an activity record
 *
 * @see com.cboe.idl.cmiTraderActivity.ActivityFieldStruct
 */
public class ActivityFieldImpl implements ActivityField
{
//*** Public Attributes

//*** Private Attributes

    private ActivityFieldStruct m_activityFieldStruct = null;
    private Map<String, String> m_formattedStrings = null;

//*** Public Methods

    /**
     * **************************************************************************
     * Memberwise constructor
     *
     * @see com.cboe.idl.cmiTraderActivity.ActivityFieldStruct
     * @param activityFieldStruct field struct
     */
    public ActivityFieldImpl(ActivityFieldStruct activityFieldStruct)
    {
        m_activityFieldStruct = activityFieldStruct;
        m_formattedStrings = new HashMap<String, String>(2);
    }

    /**
     * Default constructor
     */
    public ActivityFieldImpl()
    {
    }



    /**
     * ************************************************************************** Returns the ActivityFieldStruct that
     * this object represents
     * <p/>
     * Note: This method exists primarily for backwards compatability reasons. Please use the wrapper objects whenever
     * possible
     *
     * @see com.cboe.idl.cmiTraderActivity.ActivityFieldStruct
     * @return field struct
     */
    public ActivityFieldStruct getActivityFieldStruct()
    {
        return m_activityFieldStruct;
    }


    /**
     * ************************************************************************** Returns the field type
     * @return the field type
     */
    public short getFieldTypeCode()
    {
        return m_activityFieldStruct.fieldType;
    }


    /**
     * ************************************************************************** Returns the field name
     * @return the field name
     */
    public String getFieldName()
    {
        return m_activityFieldStruct.fieldName;
    }


    /**
     * ************************************************************************** Returns the field value
     * @return field value
     */
    public String getFieldValue()
    {
        return m_activityFieldStruct.fieldValue;
    }

    /**
     * attempt to format field value and return
     * @return formatted field value
     */
    public String getFormattedFieldValue() {
        return ActivityFieldTypes.toString(getFieldTypeCode(), getFieldValue(), ActivityFieldTypes.TRADERS_FORMAT);
    }

    /**
     * {@inheritDoc}. 
     */
    public String getFormattedFieldValue(String formatSpecified){
        return ActivityFieldTypes.toString(getFieldTypeCode(), getFieldValue(), formatSpecified);
    }

    /**
     * checks if the  is valid
     */
    public boolean isPriorityEnforceable(){
        return ActivityFieldTypes.isPriorityEnforceable(getFieldTypeCode(), getFieldValue());
    }

    /**
     * ************************************************************************** Returns a string representation of the
     * object in NAME_VALUE_FORMAT format
     *
     * @return a string representation of the object
     */
    public String toString()
    {
        return toString(ActivityField.NAME_VALUE_FORMAT);
    }


    /**
     * ************************************************************************** Returns a string representation of the
     * object in the given format
     *
     * @param formatSpecifier - a string that specifies how the object should format itself.
     * @return a string representation of the object
     * @see java.text.SimpleDateFormat
     */
    public String toString(String formatSpecifier)
    {
        // Check to see if we've already rendered this string
        String formattedString = m_formattedStrings.get(formatSpecifier);
        if (formattedString == null)
        {
            if (ActivityField.NAME_VALUE_FORMAT.equals(formatSpecifier))
            {
                formattedString = "Field Name: " + getFieldName() +
                        ", Field Value: " + getFieldValue();
            }
            else if(ActivityField.DETAILED_EQUALS_FORMAT.equals(formatSpecifier)){
                formattedString = getFieldName() + " = " + getFormattedFieldValue(); 
            }
            else
            {
                formattedString = "ERROR: Format not supported";
            }
            m_formattedStrings.put(formatSpecifier, formattedString);
        }
        return formattedString;
    }

    public boolean isValid()
    {
        return true;
    }

}



