package com.cboe.infrastructureServices.loggingService;

/**
 * An abstraction of the StdMsgType used by logging service. This eliminates a need for
 * users of the logging service to import com.cboe.idl.infrastructureServices.loggingService.corba.StdMsgType.
 * @version 3.0 Requires Increment3 TRC infrastructure.
 */
public class StdMsgType
    implements java.lang.Cloneable
{
    public static final com.cboe.idl.infrastructureServices.loggingService.corba.StdMsgType NonStd = com.cboe.idl.infrastructureServices.loggingService.corba.StdMsgType.NonStd ;
    public static final com.cboe.idl.infrastructureServices.loggingService.corba.StdMsgType FileReadFailure = com.cboe.idl.infrastructureServices.loggingService.corba.StdMsgType.FileReadFailure;
    public static final com.cboe.idl.infrastructureServices.loggingService.corba.StdMsgType ManagementEvent = com.cboe.idl.infrastructureServices.loggingService.corba.StdMsgType.ManagementEvent;
//    public static final com.cboe.idl.infrastructureServices.loggingService.corba.StdMsgType MethodEnded = com.cboe.idl.infrastructureServices.loggingService.corba.StdMsgType.MethodEnded ;
//    public static final com.cboe.loggingService.corba.StdMsgType NonStd = com.cboe.loggingService.corba.StdMsgType.NonStd ;
//    public static final com.cboe.loggingService.corba.StdMsgType FileReadFailure = com.cboe.loggingService.corba.StdMsgType.FileReadFailure;
//    public static final com.cboe.loggingService.corba.StdMsgType ManagementEvent = com.cboe.loggingService.corba.StdMsgType.ManagementEvent;

}
