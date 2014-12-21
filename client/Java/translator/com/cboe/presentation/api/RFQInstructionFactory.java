package com.cboe.presentation.api;

import com.cboe.interfaces.presentation.common.instruction.InstructionTarget;
import com.cboe.presentation.common.instruction.DefaultInstructionIdentifier;

/**
   @author Will McNabb
 */
public class RFQInstructionFactory
{
///////////////////////////////////////////////////////////////////////////////
// ATTRIBUTES
    
    static long idNumber = -1;
    
///////////////////////////////////////////////////////////////////////////////
// PUBLIC METHODS

    /**
       Creates a RFQInstruction instance withe the given target and
       execution time.
       @param InstructionTarget target
       @param long execTime
       @param boolean isDelete
       @return RFQInstruction
     */
    public static RFQInstruction create(InstructionTarget target, long execTime, boolean isDelete) 
    {
        return new RFQInstruction(new DefaultInstructionIdentifier(), target, execTime, isDelete);
    }
}
