
package com.cboe.instrumentationService.commPath;


/**
 * This class contains all of the individual instance variables representing
 * a CommPath.
 * 
 * @author Kevin Yaussy
 */
public class CommPathMetrics {
	private int msgsSent;
	private int blocksSent;
	private int bytesSent;
	private int msgsLost;
	private int blocksLost;
	private int bytesLost;
	private int msgsIgnored;
	private int bytesIgnored;
	private int blocksIgnored;
	private int msgsReceived;
	private int blocksReceived;
	private int bytesReceived;
	private int numErrors;
	private Exception lastError;
	private long lastErrorTime;
	private int numConnects;
	private long lastConnectTime;
	private int numDisconnects;
	private long lastDisconnectTime;
	private int tranTimeMills;
	private int tranTimeMillsMax;
	private int timeDist[];
	private short timeDistSecs;
	private short timeDistBucketsPerSec;
	private short timeDistShiftAmt;

	private Object sentLock = new Object();
	private Object recvLock = new Object();
	private Object lostLock = new Object();
	private Object ignoredLock = new Object();

/**
 * Constructor.
 */
public CommPathMetrics ( ) {
}
/**
 *	Constructor for CommPathMetrics which sets up the timeDist array
 *	and timeDistShiftAmt stuff.
 * @param newTimeDistSecs short
 * @param newTimeDistBucketsPerSec short
 * @param newTimeDistShiftAmt short
 */
public CommPathMetrics ( short newTimeDistSecs, short newTimeDistBucketsPerSec,
					short newTimeDistShiftAmt ) {

	tranTimeMills = 0;
	tranTimeMillsMax = 0;
	timeDistSecs = newTimeDistSecs;
	timeDistBucketsPerSec = newTimeDistBucketsPerSec;
	timeDist = new int[timeDistSecs * timeDistBucketsPerSec];
	timeDistShiftAmt = newTimeDistShiftAmt;

}
	protected void setSentLock( Object sentLock ) {
		this.sentLock = sentLock;
	}

	protected void setRecvLock( Object recvLock ) {
		this.recvLock = recvLock;
	}

	protected void setLostLock( Object lostLock ) {
		this.lostLock = lostLock;
	}

	protected void setIgnoredLock( Object ignoredLock ) {
		this.ignoredLock = ignoredLock;
	}

/**
 *	This method copies all information for the current object's
 *	CommPathMetrics into the user supplied destCpm object.
 * @return com.cboe.instrumentationService.commPath.CommPathMetrics
 * @param destCpm com.cboe.instrumentationService.commPath.CommPathMetrics
 */
public void copyCommPathMetrics( CommPathMetrics destCpm ) {

	destCpm.setIgnoredInfo( this.getMsgsIgnored(), this.getBlocksIgnored(),
					    this.getBytesIgnored() );
	destCpm.setLostInfo( this.getMsgsLost(), this.getBlocksLost(),
					 this.getBytesLost() );
	destCpm.setReceivedInfo( this.getMsgsReceived(), this.getBlocksReceived(),
						this.getBytesReceived() );
	destCpm.setSentInfo( this.getMsgsSent(), this.getBlocksSent(),
					 this.getBytesSent() );

	destCpm.setConnectInfo( this.getNumConnects(),
					    this.getLastConnectTime() );
	destCpm.setDisconnectInfo( this.getNumDisconnects(),
						  this.getLastDisconnectTime() );
	destCpm.setErrorInfo( this.getNumErrors(),
					  this.getLastErrorTime(), this.getLastError() );

	destCpm.setTimeDist( this.getTimeDist() );
	destCpm.setTranTimeMills( this.getTranTimeMills() );
	destCpm.setTranTimeMillsMax( this.getTranTimeMillsMax() );

}
/**
 *	Returns the total blocks ignored for this CommPath.
 * @return int
 * @author Kevin Yaussy
 */
public int getBlocksIgnored ( ) {
	return blocksIgnored;
}
/**
 *	Returns the total blocks lost for this CommPath.
 * @return int
 * @author Kevin Yaussy
 */
public int getBlocksLost ( ) {
	return blocksLost;
}
/**
 *	Returns the total blocks received for this CommPath.
 * @return int
 * @author Kevin Yaussy
 */
public int getBlocksReceived ( ) {
	return blocksReceived;
}
/**
 *	Returns the total blocks sent for this CommPath.
 * @return int
 * @author Kevin Yaussy
 */
public int getBlocksSent ( ) {
	return blocksSent;
}

/**
 *	Returns the total bytes ignored for this CommPath.
 * @return int
 * @author Kevin Yaussy
 */
public int getBytesIgnored ( ) {
	return bytesIgnored;
}
/**
 *	Returns the total bytes lost for this CommPath.
 * @return int
 * @author Kevin Yaussy
 */
public int getBytesLost ( ) {
	return bytesLost;
}
/**
 *	Returns the total bytes received for this CommPath.
 * @return int
 * @author Kevin Yaussy
 */
public int getBytesReceived ( ) {
	return bytesReceived;
}
/**
 *	Returns the total bytes sent for this CommPath.
 * @return int
 * @author Kevin Yaussy
 */
public int getBytesSent ( ) {
	return bytesSent;
}
/**
 * Returns the last connect time for this CommPath.
 * @return java.util.Date
 */
public long getLastConnectTime() {
	return lastConnectTime;
}
/**
 * Returns the last disconnect time for this CommPath.
 * @return java.util.Date
 */
public long getLastDisconnectTime() {
	return lastDisconnectTime;
}
/**
 * Returns the last exception for this CommPath.
 * @return java.lang.Exception
 */
public Exception getLastError() {
	return lastError;
}
/**
 * Returns the last error time for this CommPath.
 * @return java.util.Date
 */
public long getLastErrorTime() {
	return lastErrorTime;
}
/**
 *	Returns the total msgs ignored for this CommPath.
 * @return int
 * @author Kevin Yaussy
 */
public int getMsgsIgnored ( ) {
	return msgsIgnored;
}
/**
 *	Returns the total msgs lost for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public int	getMsgsLost ( ) {
	return msgsLost;
}
/**
 *	Returns the total msgs received for this CommPath.
 * @return int
 * @author Kevin Yaussy
 */
public int getMsgsReceived ( ) {
	return msgsReceived;
}
/**
 *	Returns the total msgs sent for this CommPath.
 * @return int
 * @author Kevin Yaussy
 */
public int getMsgsSent( ) {
	return msgsSent;
}
/**
 * Returns the total number of connects for this CommPath.
 * @return int
 */
public int getNumConnects() {
	return numConnects;
}
/**
 * Returns the total number of disconnects for this CommPath.
 * @return int
 */
public int getNumDisconnects() {
	return numDisconnects;
}
/**
 * Returns the total number of errors for this CommPath.
 * @return int
 */
public int getNumErrors() {
	return numErrors;
}
/**
 * Returns the transaction time distribution array for this CommPath.
 * @return int[]
 */
public int[] getTimeDist() {
	return timeDist;
}
/**
 * Returns the transaction time number of buckets per second.
 * @return short
 */
public short getTimeDistBucketsPerSec() {
	return timeDistBucketsPerSec;
}
/**
 * Returns the timeDistSecs.
 * @return short
 */
public short getTimeDistSecs() {
	return timeDistSecs;
}
/**
 * Returns the transaction time distribution-shift-amount.
 * @return short
 */
public short getTimeDistShiftAmt() {
	return timeDistShiftAmt;
}
/**
 * Returns the transaction time millisecond value.
 * @return int
 */
public int getTranTimeMills() {
	return tranTimeMills;
}
/**
 * Returns the maximum transaction time.
 * @return int
 */
public int getTranTimeMillsMax() {
	return tranTimeMillsMax;
}
/**
 * This method increments the blocks ignored.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public final void incBlocksIgnored ( int incAmount ) {

	synchronized( ignoredLock ) {
		blocksIgnored += incAmount;
	}

}
/**
 * This method increments the blocks lost.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public final void incBlocksLost ( int incAmount ) {

	synchronized( lostLock ) {
		blocksLost += incAmount;
	}

}
/**
 * This method increments the blocks received.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public final void incBlocksReceived ( int incAmount ) {

	synchronized( recvLock ) {
		blocksReceived += incAmount;
	}

}
/**
 * This method increments the blocks sent.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public final void incBlocksSent ( int incAmount ) {

	synchronized( sentLock ) {
		blocksSent += incAmount;
	}

}

/**
 * This method increments the bytes ignored.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public final void incBytesIgnored ( int incAmount ) {

	synchronized( ignoredLock ) {
		bytesIgnored += incAmount;
	}

}
/**
 * This method increments the bytes lost.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public final void incBytesLost ( int incAmount ) {

	synchronized( lostLock ) {
		bytesLost += incAmount;
	}

}
/**
 * This method increments the bytes received.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public final void incBytesReceived ( int incAmount ) {

	synchronized( recvLock ) {
		bytesReceived += incAmount;
	}

}
/**
 * This method increments the bytes sent.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public final void incBytesSent ( int incAmount ) {

	synchronized( sentLock ) {
		bytesSent += incAmount;
	}

}
/**
 * This method increments all "ignored" instance variables.
 * @param msgsInc int
 * @param blocksInc int
 * @param bytesInc int
 * @author Kevin Yaussy
 */
public final void incIgnoredInfo ( int msgsInc, int blocksInc, int bytesInc ) {

	synchronized( ignoredLock ) {
		incMsgsIgnored (msgsInc);
		incBlocksIgnored (blocksInc);
		incBytesIgnored (bytesInc);
	}

}
/**
 * This method increments all "lost" instance variables.
 * @param msgsInc int
 * @param blocksInc int
 * @param bytesInc int
 * @author Kevin Yaussy
 */
public final void incLostInfo ( int msgsInc, int blocksInc, int bytesInc ) {

	synchronized( lostLock ) {
		incMsgsLost (msgsInc);
		incBlocksLost (blocksInc);
		incBytesLost (bytesInc);
	}

}
/**
 * This method incrments the messages ignored.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public final void incMsgsIgnored ( int incAmount ) {

	synchronized( ignoredLock ) {
		msgsIgnored += incAmount;
	}

}
/**
 * This method increments the messages lost.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public final void incMsgsLost ( int incAmount ) {

	synchronized( lostLock ) {
		msgsLost += incAmount;
	}

}
/**
 * This method increments the messages received.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public final void incMsgsReceived ( int incAmount ) {

	synchronized( recvLock ) {
		msgsReceived += incAmount;
	}

}
/**
 * This method increments the messages sent.
 * @param incAmount int
 * @author Kevin Yaussy
 */
public final void incMsgsSent( int incAmount ) {

	synchronized( sentLock ) {
		msgsSent += incAmount;
	}

}
/**
 * This method increments all "received" instance variables.
 * @param msgsInc int
 * @param blocksInc int
 * @param bytesInc int
 * @author Kevin Yaussy
 */
public final void incReceivedInfo ( int msgsInc, int blocksInc, int bytesInc ) {

	synchronized( recvLock ) {
		incMsgsReceived (msgsInc);
		incBlocksReceived (blocksInc);
		incBytesReceived (bytesInc);
	}

}
/**
 * This method increments the "sent" instance variables.
 * @param msgsInc int
 * @param blocksInc int
 * @param bytesInc int
 * @author Kevin Yaussy
 */
public final void incSentInfo ( int msgsInc, int blocksInc, int bytesInc ) {

	synchronized( sentLock ) {
		incMsgsSent (msgsInc);
		incBlocksSent (blocksInc);
		incBytesSent (bytesInc);
	}

}
/**
 *	This method increments the tranTimeMills instance var.  The
 *	timeDist array is also dealt with.
 *
 *
 * @param incAmountMills int
 * @author Kevin Yaussy
 */
public final void incTranTimeMills( int incAmountMills ) {
	int bucketNum;

	synchronized( this ) {
		tranTimeMills += incAmountMills;
		if (tranTimeMills > tranTimeMillsMax)
			tranTimeMillsMax = tranTimeMills;

		bucketNum = incAmountMills >> timeDistShiftAmt;
		if (bucketNum >= timeDist.length)
			bucketNum = timeDist.length - 1;
		if ( bucketNum < 0 )
			return; // Don't count these..?
		timeDist[bucketNum]++;
	}
}
/**
 * This method sets the "connect" instance variables.
 * @param connectsInc int
 * @param connectTime Date
 * @author Kevin Yaussy
 */
public final void setConnectInfo ( int connectsInc, long connectTime ) {

	synchronized( this ) {
		numConnects = connectsInc;
		lastConnectTime = connectTime;
	}

}
/**
 * This method sets the "disconnect" instance variables.
 * @param disconnectsInc int
 * @param disconnectTime Date
 * @author Kevin Yaussy
 */
public final void setDisconnectInfo ( int disconnectsInc, long disconnectTime ) {

	synchronized( this ) {
		numDisconnects = disconnectsInc;
		lastDisconnectTime = disconnectTime;
	}

}
/**
 * This method sets the "error" instance variables.
 * @param errorInc int
 * @param errorTime Date
 * @param e Exception
 * @author Kevin Yaussy
 */
public final void setErrorInfo ( int errorInc, long errorTime, Exception e ) {

	synchronized( this ) {
		numErrors = errorInc;
		lastErrorTime = errorTime;
		lastError = e;
	}

}
/**
 * This method increments all "ignored" instance variables.
 * @param msgsInc int
 * @param blocksInc int
 * @param bytesInc int
 * @author Kevin Yaussy
 */
public final void setIgnoredInfo ( int msgsInc, int blocksInc, int bytesInc ) {

	synchronized( ignoredLock ) {
		msgsIgnored = msgsInc;
		blocksIgnored = blocksInc;
		bytesIgnored = bytesInc;
	}

}
/**
 * This method increments all "lost" instance variables.
 * @param msgsInc int
 * @param blocksInc int
 * @param bytesInc int
 * @author Kevin Yaussy
 */
public final void setLostInfo ( int msgsInc, int blocksInc, int bytesInc ) {

	synchronized( lostLock ) {
		msgsLost = msgsInc;
		blocksLost = blocksInc;
		bytesLost = bytesInc;
	}

}
/**
 * This method increments all "received" instance variables.
 * @param msgsInc int
 * @param blocksInc int
 * @param bytesInc int
 * @author Kevin Yaussy
 */
public final void setReceivedInfo ( int msgsInc, int blocksInc, int bytesInc ) {

	synchronized( recvLock ) {
		msgsReceived = msgsInc;
		blocksReceived = blocksInc;
		bytesReceived = bytesInc;
	}

}
/**
 * This method increments the "sent" instance variables.
 * @param msgsInc int
 * @param blocksInc int
 * @param bytesInc int
 * @author Kevin Yaussy
 */
public final void setSentInfo ( int msgsInc, int blocksInc, int bytesInc ) {

	synchronized( sentLock ) {
		msgsSent = msgsInc;
		blocksSent = blocksInc;
		bytesSent = bytesInc;
	}

}
/**
 * This method sets the timeDist array to a new value.
 * @param newTimeDist int[]
 */
public void setTimeDist( int newTimeDist[] ) {
	synchronized( this ) {
		System.arraycopy( newTimeDist, 0, this.timeDist, 0, this.timeDist.length );
	}
}
/**
 * This method sets all instance variables to the delta value of sample1 -
 * sample2.
 * @param sample1 com.cboe.instrumentationService.commPath.CommPathMetrics
 * @param sample2 com.cboe.instrumentationService.commPath.CommPathMetrics
 */
public void setToDelta( CommPathMetrics sample1, CommPathMetrics sample2 ) {

	msgsSent = sample1.getMsgsSent() - sample2.getMsgsSent();
	blocksSent = sample1.getBlocksSent() - sample2.getBlocksSent();
	bytesSent = sample1.getBytesSent() - sample2.getBytesSent();
	msgsLost = sample1.getMsgsLost() - sample2.getMsgsLost();
	blocksLost = sample1.getBlocksLost() - sample2.getMsgsLost();
	bytesLost = sample1.getBytesLost() - sample2.getBytesLost();
	msgsIgnored = sample1.getMsgsIgnored() - sample2.getMsgsIgnored();
	bytesIgnored = sample1.getBytesIgnored() - sample2.getBytesIgnored();
	blocksIgnored = sample1.getBlocksIgnored() - sample2.getBlocksIgnored();
	msgsReceived = sample1.getMsgsReceived() - sample2.getMsgsReceived();
	blocksReceived = sample1.getBlocksReceived() - sample2.getBlocksReceived();
	bytesReceived = sample1.getBytesReceived() - sample2.getBytesReceived();
	numErrors = sample1.getNumErrors() - sample2.getNumErrors();
	lastError = sample2.getLastError();
	lastErrorTime = sample2.getLastErrorTime();
	numConnects = sample1.getNumConnects() - sample2.getNumConnects();
	lastConnectTime = sample2.getLastConnectTime();
	numDisconnects = sample1.getNumDisconnects() - sample2.getNumDisconnects();
	lastDisconnectTime = sample2.getLastDisconnectTime();
	tranTimeMills = sample1.getTranTimeMills() - sample2.getTranTimeMills();
	tranTimeMillsMax = sample2.getTranTimeMillsMax();
	timeDistSecs = sample2.getTimeDistSecs();
	timeDistBucketsPerSec = sample2.getTimeDistSecs();
	timeDistShiftAmt = sample2.getTimeDistShiftAmt();
	timeDist = sample2.getTimeDist();

}
/**
 * Sets the tranTimeMills value.
 * @param newTranTimeMills int
 */
public void setTranTimeMills( int newTranTimeMills ) {
	tranTimeMills = newTranTimeMills;
}
/**
 * Sets the tranTimeMillsMax value.
 * @param newTranTimeMills int
 */
public void setTranTimeMillsMax( int newTranTimeMillsMax ) {
	tranTimeMillsMax = newTranTimeMillsMax;
}
/**
 * This method sets the "connect" instance variables.
 * @param connectsInc int
 * @param connectTime Date
 * @author Kevin Yaussy
 */
public final void updateConnectInfo ( int connectsInc, long connectTime ) {

	synchronized( this ) {
		numConnects += connectsInc;
		lastConnectTime = connectTime;
	}

}
/**
 * This method sets the "disconnect" instance variables.
 * @param disconnectsInc int
 * @param disconnectTime Date
 * @author Kevin Yaussy
 */
public final void updateDisconnectInfo ( int disconnectsInc, long disconnectTime ) {

	synchronized( this ) {
		numDisconnects += disconnectsInc;
		lastDisconnectTime = disconnectTime;
	}

}
/**
 * This method sets the "error" instance variables.
 * @param errorInc int
 * @param errorTime Date
 * @param e Exception
 * @author Kevin Yaussy
 */
public final void updateErrorInfo ( int errorInc, long errorTime, Exception e ) {

	synchronized( this ) {
		numErrors += errorInc;
		lastErrorTime = errorTime;
		lastError = e;
	}

}
}
