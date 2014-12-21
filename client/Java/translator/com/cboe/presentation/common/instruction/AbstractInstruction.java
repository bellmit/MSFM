package com.cboe.presentation.common.instruction;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.interfaces.presentation.common.instruction.InstructionIdentifier;
import com.cboe.interfaces.presentation.common.instruction.InstructionTarget;
import com.cboe.interfaces.presentation.common.instruction.Instruction;

/**
   Abstract implementation of Instruction interface.
   @author Will McNabb
 */
public abstract class AbstractInstruction implements Instruction
{
///////////////////////////////////////////////////////////////////////////////
// ATTRIBUTES

    protected InstructionTarget instructionTarget;
    protected InstructionIdentifier identifier;

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTION

    /**
      AbstractInstruction constructor.
      @param InstructionIdentifier identifier
      @param InstructionTarget target
     */
    public AbstractInstruction(InstructionIdentifier identifier, InstructionTarget target)
    {
        this.identifier = identifier;
        this.instructionTarget = target;
    }
///////////////////////////////////////////////////////////////////////////////
// INTERFACE IMPLEMENTATION

    /**
      Compares the identifier of this instruction with the given
      instruction.
     */
    public int compareTo(Object o)
    {
        int retVal = -1;
        if ((o != null) && (this.identifier != null))
        {
            try
            {
                Instruction other = (Instruction)o;
                retVal = this.identifier.compareTo(other.getIdentifier());
            }
            catch (ClassCastException e)
            {
                GUILoggerHome.find().debug("Instruction compareTo expected "
                    + " Instruction and got " + o.getClass().getName(), GUILoggerBusinessProperty.COMMON);
                throw e;
            }
        }

        return retVal;
    }

///////////////////////////////////////////////////////////////////////////////
// PUBLIC METHODS

    /**
     Gets the identifier for this instruction
     @return InstructionIdentifier
     */
    public InstructionIdentifier getIdentifier()
    {
        return this.identifier;
    }

    /**
     Calls upon the instruction target to update itself.
     */
    public boolean performOperation()
    {
        return this.instructionTarget.doUpdate();
    }

    public InstructionTarget getTarget()
    {
        return this.instructionTarget;
    }
}
