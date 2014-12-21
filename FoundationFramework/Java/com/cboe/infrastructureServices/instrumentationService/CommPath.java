package com.cboe.infrastructureServices.instrumentationService;
import	java.util.*;
/**
 * Replcate the commpath behavior as an interface in the FoundationFramework.
 */
public interface CommPath
{
	public static short	STATUS_MIN_VALUE = 0;
	public static short	STATUS_UNDEFINED = 0;
	public static short	STATUS_UP = 1;
	public static short	STATUS_DOWN = 2;
	public static short	STATUS_INIT = 3;
	public static short	STATUS_SUSPENDED = 4;
	public static short	STATUS_STOPPED = 5;
	public static short	STATUS_NO_DATA = 6;
	public static short	STATUS_DOWN_PROBLEM = 7;
	public static short	STATUS_MAX_VALUE = 7;

/*  0 I'm hoping the CommPathMetrics material is not needed. 

public CommPathMetrics cloneCommPathMetrics();
/*
 *	This method copies all information for the current object's
 *	CommPathMetrics into the user supplied destCpm object.  The copy
 *	takes the whole heirarchy into account.
 * @return com.cboe.instrumentationService.commPath.CommPathMetrics
 * @param destCpm com.cboe.instrumentationService.commPath.CommPathMetrics
public void copyCommPathMetrics( CommPathMetrics destCpm );
*/
/**
 *	Returns the total blocks ignored for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public int getBlocksIgnored ( );
/**
 *	Returns the total blocks lost for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public int getBlocksLost ( );
/**
 *	Returns the total blocks received for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public int getBlocksReceived ( ) ;
public int getBlocksSent ( );
/**
 *	Returns the total bytes ignored for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public int getBytesIgnored ( );
/**
 *	Returns the total bytes lost for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public int getBytesLost ( );
/**
 *	Returns the total bytes received for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public int getBytesReceived ( );
/**
 *	Returns the total bytes sent for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public int getBytesSent ( );
/**
 *	Returns the commPathName for this CommPath.
 * @return String
 * @author Kevin Yaussy
 */
public String getCommPathName();
/**
 * Returns the collection of CommPaths for "this" as a Vector.
 * @return java.util.Vector
 * @author Kevin Yaussy
 */
public Vector getCommPaths();
/**
 * Returns the depth of the CommPath in the full tree.
 * @return short
 * @author Kevin Yaussy
 */
public short getDepth() ;
/**
 *	Returns the fullCommPathName for this CommPath.
 * @return String
 * @author Kevin Yaussy
 */
public String getFullCommPathName() ;
/**
 *	Returns the total msgs ignored for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public int getMsgsIgnored ( );
public int	getMsgsLost ( );
/**
 *	Returns the total msgs received for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public int getMsgsReceived ( );
/**
 *	Returns the total msgs sent for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public int getMsgsSent( );
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int getNumCommPaths();
/**
 * Returns the current status of the CommPath.
 * @return short
 * @author Kevin Yaussy
 */
public short getStatus() ;
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int getTranTimeMills();
/**
 * @return int
 */
public int getTranTimeMillsMax();
/**
 *	Returns the userData for this CommPath.
 * @return String
 * @author Kevin Yaussy
 */
public Object getUserData();
/**
 * Returns true if the CommPath status is STATUS_DOWN.
 * @return boolean
 * @author Kevin Yaussy
 */
public boolean isDown();
/**
 * Returns true if the CommPath status is STATUS_DOWN_PROBLEM.
 * @return boolean
 * @author Kevin Yaussy
 */
public boolean isDownProblem();
/**
 * Returns true if the CommPath status is STATUS_INIT.
 * @return boolean
 * @author Kevin Yaussy
 */
public boolean isInit() ;
/**
 * Returns true if the CommPath status is STATUS_NO_DATA.
 * @return boolean
 * @author Kevin Yaussy
 */
public boolean isNoData();
/**
 * Returns true if the CommPath status is STATUS_STOPPED.
 * @return boolean
 * @author Kevin Yaussy
 */
public boolean isStopped();
/**
 * Returns true if the CommPath status is STATUS_SUSPENDED.
 * @return boolean
 * @author Kevin Yaussy
 */
public boolean isSuspended();
/**
 * Returns true if the CommPath status is STATUS_UP.
 * @return boolean
 * @author Kevin Yaussy
 */
public boolean isUp();
/**
 * Sets the CommPath status.  
 * @param newStatus short
 * @author Kevin Yaussy
 */
public void setStatus( short newStatus ) throws InvalidCommPathStatusException ;
/**
 * This method sets the user data.
 * @param newUserData String
 * @author Kevin Yaussy
 */
public void setUserData ( Object newUserData ); 


////FROM CommPathMetrics class definintion
/**
 * This method was created by a SmartGuide.
 * @return java.util.Date
 */
public long getLastConnectTime() ;
/**
 * This method was created by a SmartGuide.
 * @return java.util.Date
 */
public long getLastDisconnectTime() ;
/**
 * This method was created by a SmartGuide.
 * @return java.lang.Exception
 */
public Exception getLastError() ;
/**
 * This method was created by a SmartGuide.
 * @return java.util.Date
 */
public long getLastErrorTime() ;
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int getNumConnects() ;
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int getNumDisconnects() ;
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int getNumErrors() ;
/**
 * This method was created by a SmartGuide.
 * @return int[]
 */
public int[] getTimeDist() ;
/**
 * This method was created by a SmartGuide.
 * @return short
 */
public short getTimeDistBucketsPerSec() ;
/**
 * This method was created by a SmartGuide.
 * @return short
 */
public short getTimeDistSecs() ;
/**
 * This method was created by a SmartGuide.
 * @return short
 */
public short getTimeDistShiftAmt() ;
/**
 * This method increments the blocks ignored.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public void incBlocksIgnored ( int incAmount ) ;
/**
 * This method increments the blocks lost.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public void incBlocksLost ( int incAmount ) ;
/**
 * This method increments the blocks received.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public void incBlocksReceived ( int incAmount ) ;
/**
 * This method increments the blocks sent.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public void incBlocksSent ( int incAmount ) ;

/**
 * This method increments the bytes ignored.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public void incBytesIgnored ( int incAmount ) ;
/**
 * This method increments the bytes lost.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public void incBytesLost ( int incAmount ) ;
/**
 * This method increments the bytes received.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public void incBytesReceived ( int incAmount ) ;
/**
 * This method increments the bytes sent.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public void incBytesSent ( int incAmount ) ;
/**
 * This method increments all "ignored" instance variables.
 * @param msgsInc int
 * @param blocksInc int
 * @param bytesInc int
 * @author Kevin Yaussy
 */
public void incIgnoredInfo ( int msgsInc, int blocksInc, int bytesInc ) ;
/**
 * This method increments all "lost" instance variables.
 * @param msgsInc int
 * @param blocksInc int
 * @param bytesInc int
 * @author Kevin Yaussy
 */
public void incLostInfo ( int msgsInc, int blocksInc, int bytesInc ) ;
/**
 * This method incrments the messages ignored.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public void incMsgsIgnored ( int incAmount ) ;
/**
 * This method increments the messages lost.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public void incMsgsLost ( int incAmount ) ;
/**
 * This method increments the messages received.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public void incMsgsReceived ( int incAmount ) ;
/**
 * This method increments the messages sent.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public void incMsgsSent( int incAmount ) ;
/**
 * This method increments all "received" instance variables.
 * @param msgsInc int
 * @param blocksInc int
 * @param bytesInc int
 * @author Kevin Yaussy
 */
public void incReceivedInfo ( int msgsInc, int blocksInc, int bytesInc ) ;
/**
 * This method increments the "sent" instance variables.
 * @param msgsInc int
 * @param blocksInc int
 * @param bytesInc int
 * @author Kevin Yaussy
 */
public void incSentInfo ( int msgsInc, int blocksInc, int bytesInc ) ;
/**
 *	This method increments the tranTimeMills instance var.  The
 *	timeDist array is also dealt with.
 *
 *
 * @param incAmountMills int
 * @author Kevin Yaussy
 */
public void incTranTimeMills( int incAmountMills ) ;
/**
 * This method sets the "connect" instance variables.
 * @param connectsInc int
 * @param connectTime Date
 * @author Kevin Yaussy
 */
public void setConnectInfo ( int connectsInc, long connectTime ) ;
/**
 * This method sets the "disconnect" instance variables.
 * @param disconnectsInc int
 * @param disconnectTime Date
 * @author Kevin Yaussy
 */
public void setDisconnectInfo ( int disconnectsInc, long disconnectTime ) ;
/**
 * This method sets the "error" instance variables.
 * @param errorInc int
 * @param errorTime Date
 * @param e Exception
 * @author Kevin Yaussy
 */
public void setErrorInfo ( int errorInc, long errorTime, Exception e ) ;
/**
 * This method increments all "ignored" instance variables.
 * @param msgsInc int
 * @param blocksInc int
 * @param bytesInc int
 * @author Kevin Yaussy
 */
public void setIgnoredInfo ( int msgsInc, int blocksInc, int bytesInc ) ;
/**
 * This method increments all "lost" instance variables.
 * @param msgsInc int
 * @param blocksInc int
 * @param bytesInc int
 * @author Kevin Yaussy
 */
public void setLostInfo ( int msgsInc, int blocksInc, int bytesInc ) ;
/**
 * This method increments all "received" instance variables.
 * @param msgsInc int
 * @param blocksInc int
 * @param bytesInc int
 * @author Kevin Yaussy
 */
public void setReceivedInfo ( int msgsInc, int blocksInc, int bytesInc ) ;
/**
 * This method increments the "sent" instance variables.
 * @param msgsInc int
 * @param blocksInc int
 * @param bytesInc int
 * @author Kevin Yaussy
 */
public void setSentInfo ( int msgsInc, int blocksInc, int bytesInc ) ;
/**
 * This method was created by a SmartGuide.
 * @param newTimeDist int[]
 */
public void setTimeDist( int newTimeDist[] ) ;
/**
 * This method was created by a SmartGuide.
 * @param newTranTimeMills int
 */
public void setTranTimeMills( int newTranTimeMills ) ;
/**
 * This method was created by a SmartGuide.
 * @param newTranTimeMills int
 */
public void setTranTimeMillsMax( int newTranTimeMillsMax ) ;
/**
 * This method sets the "connect" instance variables.
 * @param connectsInc int
 * @param connectTime Date
 * @author Kevin Yaussy
 */
public void updateConnectInfo ( int connectsInc, long connectTime ) ;
/**
 * This method sets the "disconnect" instance variables.
 * @param disconnectsInc int
* @param disconnectTime Date
 * @author Kevin Yaussy
 */
public void updateDisconnectInfo ( int disconnectsInc, long disconnectTime ) ;
/**
 * This method sets the "error" instance variables.
 * @param errorInc int
 * @param errorTime Date
 * @param e Exception
 * @author Kevin Yaussy
 */
public void updateErrorInfo ( int errorInc, long errorTime, Exception e ) ;

}
