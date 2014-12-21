package com.cboe.instrumentationService;

public class InstrumentorTypeValues {

    // INSTRUMENTOR TYPES
    public static final short COUNT = 1;
    public static final short EVENTCHANNEL = 2;
    public static final short JMX = 3;
    public static final short JSTAT = 4;
    public static final short METHOD = 5;
    public static final short NETWORKCONNECTION = 6;
    public static final short QUEUE = 7;
    public static final short THREADPOOL = 8;
    public static final short RATE = 9;
    public static final short OUTLIER = 10;

    // JMX INFO KEYS
    public static final int PEAKTHREADCOUNT = 1;
    public static final int CURENTTHREADCOUNT = 2;
    public static final int TOTALTHREADSSTARTED = 3;
    public static final int TOTALCPUTIME = 4;
    
    // JSTAT GC INFO KEYS
    public static final int S0CAPACITY = 1;
    public static final int S1CAPACITY = 2;
    public static final int S0UTILIZATION = 3;
    public static final int S1UTILIZATION = 4;
    public static final int ECAPACITY = 5;
    public static final int EUTILIZATION = 6;
    public static final int OCAPACITY = 7;
    public static final int OUTILIZATION = 8;
    public static final int PCAPACITY = 9;
    public static final int PUTILIZATION = 10;
    public static final int NBRYGGCS = 11;
    public static final int TIMEYGGCS = 12;
    public static final int NBRFULLGCS = 13;
    public static final int TIMEFULLGCS = 14;
    public static final int TIMEYGFULLGCS = 15;
	public static final int TICKFREQ = 16;
	public static final int APPTIME = 17;
	public static final int SAFEPOINTSYNCTIME = 18;
	public static final int SAFEPOINTTIME = 19;
	public static final int SAFEPOINTS = 20;
    
    // OUTLIER INFO KEYS
    public static final int METHODNAME = 1;
    public static final int MACHINE= 2;
    public static final int TIMEOUTVALUE = 3;
    public static final int ACTUALDURATION = 4;
    public static final int TIMESTAMP= 5;
    public static final int CLASSKEY= 6;
    public static final int BLOCKSIZE= 7;
    public static final int SESSIONNUMBER= 8;
    public static final int PROCESS= 9;
}
