package com.cboe.infrastructureServices.loggingService;
/**
   Defines the valid priority values of log messages.
 */
public class MsgPriority
{
	public int value;
	private MsgPriority(int key ){ value = key; }
    /**
       Use for messages that should be handled before performing any other system function. These messages typically concern issues posing a threat to the correct functioning or integrity of the system.
     */
    public static final MsgPriority critical = new MsgPriority(0);
	public static final int _critical = 0;
    /**
       Use for messages that require the attention of the system or an external entity as soon as possible.
     */
    public static final MsgPriority  high = new MsgPriority(1);
	public static final int _high = 1;
    /**
       Use for messages that are of interest and should be handled on an as time permits basis.
     */
    public static final MsgPriority  medium = new MsgPriority(2);
	public static final int _medium = 2;
    /**
       Use for messages that are for informational purposes only.  No action on the part of the system or an external entity is needed.
     */
    public static final MsgPriority  low = new MsgPriority(3) ;
	public static final int _low = 3;

}
