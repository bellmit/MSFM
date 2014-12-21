package com.cboe.interfaces.presentation.common.instruction;

/**
  Defines an object used to process Instruction instances.
  @author Will McNabb
*/
public interface InstructionProcessor extends Runnable
{
    public void addInstructions(Instruction[] instructions);
    public void addInstruction(Instruction instruction);
}

