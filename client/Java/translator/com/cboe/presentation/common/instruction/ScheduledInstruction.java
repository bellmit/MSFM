package com.cboe.presentation.common.instruction;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.interfaces.presentation.common.instruction.InstructionIdentifier;
import com.cboe.interfaces.presentation.common.instruction.InstructionTarget;

/**
   This instruction is given an execution time that is used to schedule when the
   instruction will be executed by its caller.
   @author Will McNabb
 */
public class ScheduledInstruction extends AbstractInstruction
{
///////////////////////////////////////////////////////////////////////////////
// ATTRIBUTES

    private Long executionTime;

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTION

    /**
      ScheduledInstruction constructor
      @param InstructionIdentifier identifier
      @param InstructionTarget target
      @param long execTime
     */
    public ScheduledInstruction(InstructionIdentifier identifier, InstructionTarget target, long execTime)
    {
        super(identifier, target);
        this.executionTime = new Long(execTime);
    }


///////////////////////////////////////////////////////////////////////////////
// PUBLIC METHODS
    /**
       Gets the execution time of this ScheduledInstruction.
       @return Long
     */
    public Long getExecutionTime()
    {
        return this.executionTime;
    }

    public int compareTo(Object o)
    {
        int retVal = -1;
        try
        {
            ScheduledInstruction other = (ScheduledInstruction)o;
            retVal = this.executionTime.compareTo(other.executionTime);
            if (retVal == 0)
            {
                retVal = super.compareTo(o);
            }
        }
        catch (ClassCastException e)
        {
            GUILoggerHome.find().debug("ScheduledInstruction compareTo expected "
                + " ScheduledInstruction and got " + o.getClass().getName(), GUILoggerBusinessProperty.COMMON);
            throw e;
        }
        return retVal;
    }
}
