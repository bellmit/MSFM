//
// -----------------------------------------------------------------------------------
// Source file: NotFoundActivityFieldImpl.java
//
// PACKAGE: com.cboe.presentation.traderActivity;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.traderActivity;

import com.cboe.idl.cmiTraderActivity.ActivityFieldStruct;

import com.cboe.interfaces.presentation.activity.ActivityField;

/**
 * ***************************************************************************** 
 * Represents an activity field with an invalid/not found field type code.
 *
 * @see com.cboe.idl.cmiTraderActivity.ActivityFieldStruct
 */

public class NotFoundActivityFieldImpl extends ActivityFieldImpl
{

    public NotFoundActivityFieldImpl()
    {
    }

    @SuppressWarnings({"ReturnOfNull"})
    public ActivityFieldStruct getActivityFieldStruct()
    {
        return null;
    }

    public short getFieldTypeCode()
    {
        return 0;
    }

    public String toString()
    {
        return ActivityField.ACTIVITY_FIELD_NOT_FOUND;
    }

    public String toString(String formatSpecifier)
    {
        return toString();
    }

    public String getFieldValue()
    {
        return ActivityField.ACTIVITY_FIELD_NOT_FOUND;
    }

    public boolean isPriorityEnforceable()
    {
        return false;
    }


    public String getFormattedFieldValue()
    {
        return ActivityField.ACTIVITY_FIELD_NOT_FOUND;
    }

    /**
     * Get the formatted field value for an Not Found Activity Field.
     * The field value will be a constant String: {@value #ACTIVITY_FIELD_NOT_FOUND}.
     * @param formatSpecified not used.
     * @return a constant string.
     */
    public String getFormattedFieldValue(String formatSpecified){
        return ActivityField.ACTIVITY_FIELD_NOT_FOUND;
    }

    public boolean isValid()
    {
        return false;
    }

}



