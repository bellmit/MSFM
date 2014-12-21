package com.cboe.presentation.common.instruction;

import com.cboe.interfaces.presentation.common.instruction.InstructionProcessor;
import com.cboe.interfaces.presentation.common.instruction.InstructionThrottle;

public class ScheduledInstructionThrottleFactory
{
    // MOVE THE FREQUENCY FOR QUEUE UPDATES TO A CONFIG PARAMETER FOR THE APPLICATION

    public static synchronized ScheduledInstructionThrottle create()
    {
        return new ScheduledInstructionThrottle(1000, 0, 0);
    }

    public static synchronized ScheduledInstructionThrottle create(InstructionProcessor processor)
    {
        return new ScheduledInstructionThrottle(1000, processor);
    }
}