package com.cboe.presentation.api;

import com.cboe.presentation.common.instruction.ScheduledInstruction;
import com.cboe.interfaces.presentation.common.instruction.InstructionIdentifier;
import com.cboe.interfaces.presentation.common.instruction.InstructionTarget;

/**
 Interface to add the "deleted" element to a scheduled instruction
 for RFQ.
 @author Will McNabb
*/
public class RFQInstruction extends ScheduledInstruction
{
///////////////////////////////////////////////////////////////////////////////
// ATTRIBUTES

    private boolean isDeleteInstruction;

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTION

    /**
      RFQInstruction constructor
      @param InstructionIdentifier identifier
      @param InstructionTarget target
      @param long execTime
      @param boolean isDeleteInstruction
     */
    public RFQInstruction(InstructionIdentifier identifier, InstructionTarget target, 
        long execTime, boolean isDeleteInstruction)
    {
        super(identifier, target, execTime);
        this.isDeleteInstruction = isDeleteInstruction;
    }

///////////////////////////////////////////////////////////////////////////////
// PUBLIC METHODS

    public boolean isDeleteInstruction()
    {
        return this.isDeleteInstruction;
    }
}

