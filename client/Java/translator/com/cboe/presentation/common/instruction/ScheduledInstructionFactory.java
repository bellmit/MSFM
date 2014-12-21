package com.cboe.presentation.common.instruction;

import com.cboe.interfaces.presentation.common.instruction.InstructionTarget;

/**
   @author Will McNabb
 */
public class ScheduledInstructionFactory
{
///////////////////////////////////////////////////////////////////////////////
// ATTRIBUTES
    
    static long idNumber = -1;
    
///////////////////////////////////////////////////////////////////////////////
// PUBLIC METHODS

    /**
       Creates a ScheduledInstruction instance withe the given target and
       execution time.
       @param InstructionTarget target
       @param long execTime
       @return ScheduledInstruction
     */
    public static ScheduledInstruction create(InstructionTarget target, long execTime) 
    {
        return new ScheduledInstruction(new DefaultInstructionIdentifier(), target, execTime);
    }
}
