package com.cboe.interfaces.presentation.common.instruction;

import java.util.EventListener;

/**
  Interface which allows listeners to be notified when
  the instruction set has items available.
  @author Will McNabb
 */
public interface InstructionSetListener extends EventListener
{
    /**
     Invoked when InstructionSet items are available
     */
    public void instructionsAvailable();
    
    /**
     Invoked when the instruction set is empty
     */
    public void setIsEmpty();
}
