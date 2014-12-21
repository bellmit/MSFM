package com.cboe.presentation.common.instruction;

import com.cboe.interfaces.presentation.common.instruction.InstructionIdentifier;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

/**
  A default implementation of the InstructionIdentifier interface.
  @author Will McNabb
*/
public class DefaultInstructionIdentifier implements InstructionIdentifier
{
///////////////////////////////////////////////////////////////////////////////
// ATTRIBUTES

    private static long idNumber = -1;
    long localID;

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTION

    public DefaultInstructionIdentifier()
    {
        synchronized (this)
        {
            if (idNumber == -1)
            {
                idNumber = System.currentTimeMillis();
            }
            else
            {
                ++idNumber;
            }
        }
        this.localID = idNumber;
    }

///////////////////////////////////////////////////////////////////////////////
// INTERFACE IMPLEMENTATION

    public String getUniqueIDString()
    {
        return String.valueOf(this.localID);
    }
    public boolean equals(Object other)
    {
        boolean retVal = false;
        try
        {
            DefaultInstructionIdentifier otherId = (DefaultInstructionIdentifier) other;
            retVal = otherId.localID == this.localID;
        }
        catch (ClassCastException e)
        {
            GUILoggerHome.find().debug("DefaultInstructionID equals() expected DefaultInstructionIdentifier and got "
                + other.getClass().getName(), GUILoggerBusinessProperty.COMMON);
            throw e;
        }
        return retVal;
    }

    public int compareTo(Object other)
    {
        int retVal = -1;
        try
        {
            DefaultInstructionIdentifier otherId = (DefaultInstructionIdentifier) other;
            if (otherId.localID == localID)
            {
                retVal = 0;
            } else if (otherId.localID > localID)
            {
                retVal = 1;
            }
        }
        catch (ClassCastException e)
        {
            GUILoggerHome.find().debug("DefaultInstructionID compareTo() expected DefaultInstructionIdentifier and got "
                + other.getClass().getName(), GUILoggerBusinessProperty.COMMON);
            throw e;
        }
        return retVal;
    }
}

