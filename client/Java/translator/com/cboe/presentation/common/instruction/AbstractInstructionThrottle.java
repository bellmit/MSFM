package com.cboe.presentation.common.instruction;

import java.util.*;
import com.cboe.interfaces.presentation.common.instruction.InstructionProcessor;
import com.cboe.interfaces.presentation.common.instruction.InstructionThrottle;
import com.cboe.interfaces.presentation.common.instruction.InstructionSetListener;

/**
   AbstractInstructionThrottle provides an abstract implementation of the InstructionThrottle interface. 
   This implementation contains an InstructionProcessor for processing the Instructions pulled from the 
   InstructionSet by this throttle.
   @author Will McNabb
 */
public abstract class AbstractInstructionThrottle extends Thread implements InstructionThrottle, InstructionSetListener
{
///////////////////////////////////////////////////////////////////////////////
// ATTRIBUTES
    
    protected InstructionProcessor processor;
    protected boolean instructionsAvailable;
    
    private long updateFrequency;
    private int loadFactor;
    
///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTION

    /**
       DefaultInstructionThrottle constructor
       @param long updateFrequency
       @param int loadFactor
     */
    public AbstractInstructionThrottle(long updateFrequency, int loadFactor) 
    {
        this.updateFrequency = updateFrequency;
        this.loadFactor = loadFactor;
        initInstructionProcessor();
    }
    
///////////////////////////////////////////////////////////////////////////////
// INTERFACE IMPLEMENTATION
    
    /**
      Implements InstructionThrottle
    */
    public long getUpdateFrequency()
    {
        return this.updateFrequency;
    }
    /**
      Implements InstructionThrottle
    */
    public void setUpdateFrequency(long updateFreq)
    {
        this.updateFrequency = updateFreq;
    }
    /**
      Implements InstructionThrottle
    */
    public int getLoadFactor()
    {
        return this.loadFactor;
    }
    /**
      Implements InstructionThrottle
    */
    public void setLoadFactor(int lf)
    {
        this.loadFactor = lf;
    }
    /**
      Implements InstructionSetListener
    */
    public void instructionsAvailable()
    {
        //Instructions are now available on the queue. Waken the
        //throttle to begin processing if it is asleep.
        this.instructionsAvailable = true;
    }
    /**
      Implements InstructionSetListener
    */
    public void setIsEmpty()
    {
        //There are no instructions available at this time. Put 
        //this throttle to sleep until instructions are available.
        this.instructionsAvailable = false;
    }

///////////////////////////////////////////////////////////////////////////////
// PROTECTED METHODS

    /**
      Initializes the processor used by this InstructionThrottle
     */
    protected void initInstructionProcessor() 
    {
        this.processor = new DefaultInstructionProcessor();
    }
}
