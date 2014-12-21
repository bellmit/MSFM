package com.cboe.presentation.common.instruction;

import java.util.*;
import com.cboe.interfaces.presentation.common.instruction.Instruction;

/**
   A SortedInstructionSet used to collect ScheduledInstruction istances
   @author Will McNabb
 */
public class ScheduledInstructionSet extends SortedInstructionSet
{
///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTION

    /**
       ScheduledInstructionSet constructor
     */
    public ScheduledInstructionSet()
    {
        super();
    }

///////////////////////////////////////////////////////////////////////////////
// PUBLIC METHODS

    /**
      Gets the ScheduledInstructions that have execution times that occur prior to the
      given time.
      @param long time
      @return ScheduledInstruction[]
    */
    public ScheduledInstruction[] getScheduledInstructionsAtOrOlderThanExecTime(long time)
    {
        ScheduledInstruction refInstruction = new ScheduledInstruction(null, null, time);
        Instruction[] instructions = (Instruction[])super.getInstructionsLessThanOrEqual(refInstruction);
        ScheduledInstruction[] retVal = new ScheduledInstruction[instructions.length];
        System.arraycopy(instructions, 0, retVal, 0, instructions.length);
        return retVal;
    }

    /**
      Gets the ScheduledInstructions that have execution times that occur after the
      given time.
      @param long time
      @return ScheduledInstruction[]
    */
    public ScheduledInstruction[] getScheduledInstructionsAtOrYoungerThanExecTime(long time)
    {
        ScheduledInstruction refInstruction = new ScheduledInstruction(null, null, time);
        Instruction[] instructions = (Instruction[])super.getInstructionsGreaterThanOrEqual(refInstruction);
        ScheduledInstruction[] retVal = new ScheduledInstruction[instructions.length];
        System.arraycopy(instructions, 0, retVal, 0, instructions.length);
        return retVal;
    }
}
