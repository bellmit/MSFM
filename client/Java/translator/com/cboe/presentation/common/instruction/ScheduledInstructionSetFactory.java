package com.cboe.presentation.common.instruction;

/**
  A factory class used to access the ScheduledInstructionSet instance.
  @author WillMcNabb
*/
public class ScheduledInstructionSetFactory 
{
///////////////////////////////////////////////////////////////////////////////
// ATTRIBUTES
    
    private static ScheduledInstructionSet instructionSet;
    
///////////////////////////////////////////////////////////////////////////////
// PUBLIC METHODS

    /**
      Gets the singleton instance of the ScheduledInstructionSet.
      @return ScheduledInstructionSet
     */
    public static ScheduledInstructionSet find() 
    {
        if (instructionSet == null)
        {
            instructionSet = new ScheduledInstructionSet();
        }
        return instructionSet;
    }
}
