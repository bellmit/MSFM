package com.cboe.interfaces.presentation.common.instruction;

/**
  An interface that describes the implementor as a target to an Instruction.
  @author Will McNabb
*/
public interface InstructionTarget 
{
    /**
     This method is used to alert the implementor that it must update itself.
     @boolean - returns true if the target's state was changed as a result
        of the update.
     */
    public boolean doUpdate();
}
