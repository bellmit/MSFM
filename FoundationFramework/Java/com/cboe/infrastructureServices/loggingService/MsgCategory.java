package com.cboe.infrastructureServices.loggingService;

/**
   Defines the valid values for the categories that log messages may be classified under.
   @version 1.2
 */
public class MsgCategory
{
	private MsgCategory(int key)
	{
		value = key;
	}
	public int value;
    /**
       Information to be used for debugging.
     */
    public static final MsgCategory debug = new MsgCategory(0);
    public static final int _debug = 0;
    /**
       Information to be used for auditing.
     */
    public static final MsgCategory audit = new MsgCategory(1);
    public static final int _audit = 1;
    /**
       Information to be used for non-repudiation.
     */
    public static final MsgCategory nonRepudiation = new MsgCategory(2);
    public static final int _nonRepudiation = 2;
    /**
       Information to be used for general monitoring of the system.
     */
    public static final MsgCategory systemNotification = new MsgCategory(3);
    public static final int _systemNotification = 3;
    /**
       Information to be used for warning system administration of an event or condition threatening the integrity or functionality of the system.
     */
    public static final MsgCategory systemAlarm = new MsgCategory(4);
    public static final int _systemAlarm = 4;
    /**
       Information that may not be classified under any of the other categories.
     */
    public static final MsgCategory information = new MsgCategory(5);
    public static final int _information = 5;

    
}
