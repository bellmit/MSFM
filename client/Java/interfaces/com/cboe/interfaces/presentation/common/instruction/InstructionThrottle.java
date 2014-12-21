package com.cboe.interfaces.presentation.common.instruction;

/**
  Interface used to describe a mechnism for throttling the processing
  rate of Instruction instances.
  @author Will McNabb
*/
public interface InstructionThrottle extends Runnable, InstructionSetListener
{
    /**
      Get the update frequency of this throttle. The update frequency determines
      how often the throttle will poll the InstructionSet.
      @return long
    */
    public long getUpdateFrequency();
    /**
      Set the update frequency of this throttle. The update frequency determines
      how often the throttle will poll the InstructionSet.
      @param long
    */
    public void setUpdateFrequency(long updateFreq);
    /**
      Get the load factor of this throttle. The load factor determines
      the quantity of data pulled from the InstructionSet at each interval
      defined by the update frequency.
      @return int
    */
    public int getLoadFactor();
    /**
      Set the load factor of this throttle. The load factor determines
      the quantity of data pulled from the InstructionSet at each interval
      defined by the update frequency.
      @param int
    */
    public void setLoadFactor(int lf);
}

