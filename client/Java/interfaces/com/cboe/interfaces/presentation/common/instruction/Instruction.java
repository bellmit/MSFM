package com.cboe.interfaces.presentation.common.instruction;

/**
   @author Will McNabb
 */
public interface Instruction extends Comparable 
{
    /**
     Gets the identifier for this instruction
     @return InstructionIdentifier
     */
    public InstructionIdentifier getIdentifier(); 
    
    /**
     Calls upon the instruction target to update itself.
     @return boolean - true if the operation resulted in a 
      state change in the target.
     */
    public boolean performOperation(); 
    
    /**
      Gets the target object of this instruction
      @return InstructionTarget
    */
    public InstructionTarget getTarget();
}
