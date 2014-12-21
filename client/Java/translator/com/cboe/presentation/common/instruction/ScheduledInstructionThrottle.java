package com.cboe.presentation.common.instruction;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.interfaces.presentation.api.TraderAPI;
import com.cboe.interfaces.presentation.common.instruction.Instruction;
import com.cboe.interfaces.presentation.common.instruction.InstructionProcessor;
import com.cboe.presentation.common.time.TimeSyncWrapper;
/**
   This throttle adds a value for the "execution time" of
   the given ScheduledInstruction.
   @author Will McNabb
 */
public class ScheduledInstructionThrottle extends AbstractInstructionThrottle
{
///////////////////////////////////////////////////////////////////////////////
// ATTRIBUTES

    private long execTimeThreshold;

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTION

    public ScheduledInstructionThrottle(long updateFrequency, InstructionProcessor processor)
    {
        this(updateFrequency, 0, 0);
        this.processor = processor;
    }

    /**
      ScheduledInstructionThrottle constructor
      @param long updateFrequency
      @param int loadFactor
      @param long execTimeThreshold
     */
    public ScheduledInstructionThrottle(long updateFrequency, int loadFactor, long execTimeThreshold)
    {
        super(updateFrequency, loadFactor);
        this.execTimeThreshold = execTimeThreshold;
        ScheduledInstructionSetFactory.find().addInstructionSetListener(this);
    }

///////////////////////////////////////////////////////////////////////////////
// INTERFACE IMPLEMENTATION

    /**
      Implements Runnable.
    */
    public void run()
    {
        while (true)
        {
            //If instructions are available, process them. Otherwise sleep for the
            //pre-determined sleep interval.
            //Get all of the ScheduledInstruction instances that have execution times that
            //are older than the execTimeThreshold
            ScheduledInstructionSet scheduledInstructionSet = ScheduledInstructionSetFactory.find();

//            this.execTimeThreshold = System.currentTimeMillis();
            this.execTimeThreshold = TimeSyncWrapper.getCorrectedTimeMillis();

            Instruction[] processInstructions = scheduledInstructionSet.getScheduledInstructionsAtOrOlderThanExecTime(this.execTimeThreshold);
            if (processInstructions.length > 0)
            {
                processor.addInstructions(processInstructions);

                //Run the processor (Single thread mode for this case)
                processor.run();
            }

            try
            {
                Thread.currentThread().sleep(getUpdateFrequency());
            }
            catch (InterruptedException e)
            {
                GUILoggerHome.find().exception(TraderAPI.TRANSLATOR_NAME + ": ScheduledInstructionThrottle.run", e);
            }
        }
    }

///////////////////////////////////////////////////////////////////////////////
// PUBLIC METHODS

    /**
       Gets the execution time that marks the item (or items) that define the beginning of the instruction block that is to be pulled off of the instruction set contained in the RFQCacheImpl. The RFQCacheImpl should also take into consideration the load factor setting of this throttle in determining the size of the block to be pulled from the instruction set.
       @return long - the time threshold in milliseconds
     */
    public long getExecutionTimeThreshold()
    {
        return this.execTimeThreshold;
    }


}
