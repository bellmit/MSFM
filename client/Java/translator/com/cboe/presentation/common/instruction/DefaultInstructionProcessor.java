package com.cboe.presentation.common.instruction;

import java.util.*;
import com.cboe.interfaces.presentation.common.instruction.Instruction;
import com.cboe.interfaces.presentation.common.instruction.InstructionProcessor;

/**
  A Default implementation of InstructionProcessor
  @author Will McNabb
*/
public class DefaultInstructionProcessor implements InstructionProcessor
{
///////////////////////////////////////////////////////////////////////////////
// ATTRIBUTES
    
    protected List instructionList = new ArrayList();
    
///////////////////////////////////////////////////////////////////////////////
// INTERFACE IMPLEMENTATION
    
    public void addInstructions(Instruction[] instructions)
    {
        this.instructionList.addAll(Arrays.asList(instructions));
    }

    public void addInstruction(Instruction instruction)
    {
        this.instructionList.add(instruction);              
    }
    
    public void run()
    {
        for (ListIterator it = this.instructionList.listIterator(); it.hasNext(); )
        {
            Instruction instruction = (Instruction) it.next();
            instruction.performOperation();
        }
    }
}

