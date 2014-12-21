package com.cboe.interfaces.presentation.common.instruction;

/**
  A unique identifier used to distinguish Instruction instances
  within an InstructionSet.
  @author Will McNabb
*/
public interface InstructionIdentifier extends Comparable
{
    /**
      Gets a unique string identifier for this InstructionIdentifier.
     */
    public String getUniqueIDString();
    public boolean equals(Object other);
}
