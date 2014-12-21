
package com.cboe.instrumentationService.commPath;

import	java.util.*;

/**
 *	This is the class that the CommPathFactory creates.
 *	A CommPath is defined as an object an application can
 *	use to monitor or meter any type of internal or external activity or
 *	"communications path".  At the most obvious level, an application can
 *	create a CommPath for an input or output stream (i.e. a socket or a
 *	file) to monitor message rates and byte rates, etc.  However, even
 *	though a CommPath's state is defined in terms of messages, bytes, etc.,
 *	interpretation of the state is totally by convention.
 * @author Kevin Yaussy
 */
public class CommPath extends CommPathMetrics {
	public final static short STATUS_MIN_VALUE = 0;
	public final static short STATUS_UNDEFINED = 0;
	public final static short STATUS_UP = 1;
	public final static short STATUS_DOWN = 2;
	public final static short STATUS_INIT = 3;
	public final static short STATUS_SUSPENDED = 4;
	public final static short STATUS_STOPPED = 5;
	public final static short STATUS_NO_DATA = 6;
	public final static short STATUS_DOWN_PROBLEM = 7;
	public final static short STATUS_MAX_VALUE = 7;
	private String commPathName;
	private String fullCommPathName;
	protected Hashtable aCommPathCollection;
	private Object userData;
	private short depth;
	private short status = STATUS_UNDEFINED;
	private boolean leaf;
	private long heapTotal;
	private long heapFree;

	private Object sentLock = new Object();
	private Object recvLock = new Object();
	private Object lostLock = new Object();
	private Object ignoredLock = new Object();

/**
 * This constructor is not to be called.  Only the CommPathFactory
 * can create a CommPath.
 */
private CommPath ( ) {
}
/**
 *	This constructor is not to be called by the general public - only
 *	by the CommPathFactory when creating a new CommPath.
 * @param newFullCommPathName String
 * @param newCommPathName String
 * @param newUserData String
 * @param initialSize int
 * @param newTimeDistSecs short
 * @param newTimeDistBucketsPerSec short
 * @param newTimeDistShiftAmt short
 * @author Kevin Yaussy
 */
protected CommPath ( String newFullCommPathName, String newCommPathName,
				 short newDepth, int initialSize, short newTimeDistSecs,
				 short newTimeDistBucketsPerSec,
				 short newTimeDistShiftAmt, boolean leaf ) {

	super( newTimeDistSecs, newTimeDistBucketsPerSec, newTimeDistShiftAmt );

	super.setSentLock( sentLock );
	super.setRecvLock( recvLock );
	super.setLostLock( lostLock );
	super.setIgnoredLock( ignoredLock );

	fullCommPathName = newFullCommPathName;
	commPathName = newCommPathName;
	if (initialSize > 0)
		aCommPathCollection = new Hashtable( initialSize );
	else
		aCommPathCollection = new Hashtable();
	
	depth = newDepth;
	this.leaf = leaf;

}
/**
 *	This method returns a new CommPathMetrics object, created
 *	internally, using copyCommPathMetrics( destCpm ).
 * @return com.cboe.instrumentationService.commPath.CommPathMetrics
 */
public CommPathMetrics cloneCommPathMetrics() {
	CommPathMetrics newCpm = new CommPathMetrics();

	copyCommPathMetrics( newCpm );
	return newCpm;
}
/**
 *	This method copies all information for the current object's
 *	CommPathMetrics into the user supplied destCpm object.  The copy
 *	takes the whole heirarchy into account.
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
 *	This method is for testing the validity of the collections.  It is
 *	for display purposes only.  It is very similar to a binary tree
 *	display.
 * @param nameSoFar String
 * @author Kevin Yaussy
 */
protected void displayCommPathCollection( String nameSoFar ) {
	Iterator e;
	CommPath cp;
	String myOutput;
	StringBuffer sb = new StringBuffer();

	myOutput = new String( nameSoFar + "/" + commPathName );
	e = aCommPathCollection.values().iterator();
	while (e.hasNext()) {
		sb.setLength( 0 );
		sb.append( myOutput );
		cp = (CommPath)e.next();
		cp.displayCommPathCollection( sb.toString() );
	}
	System.out.println( myOutput );
}
/**
 *	Returns the total blocks ignored for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public final int getBlocksIgnored ( ) {
	Iterator e;
	int totalBlocksIgnored = 0;

	synchronized( ignoredLock ) {
		if (!aCommPathCollection.isEmpty()) {
			e = aCommPathCollection.values().iterator();
			while (e.hasNext())
				totalBlocksIgnored += ((CommPath)e.next()).getBlocksIgnored();
		}
		return super.getBlocksIgnored() + totalBlocksIgnored;
	}
}
/**
 *	Returns the total blocks lost for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public final int getBlocksLost ( ) {
	Iterator e;
	int totalBlocksLost = 0;

	synchronized( lostLock ) {
		if (!aCommPathCollection.isEmpty()) {
			e = aCommPathCollection.values().iterator();
			while (e.hasNext())
				totalBlocksLost += ((CommPath)e.next()).getBlocksLost();
		}
		return super.getBlocksLost() + totalBlocksLost;
	}
}
/**
 *	Returns the total blocks received for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public final int getBlocksReceived ( ) {
	Iterator e;
	int totalBlocksReceived = 0;

	synchronized( recvLock ) {
		if (!aCommPathCollection.isEmpty()) {
			e = aCommPathCollection.values().iterator();
			while (e.hasNext())
				totalBlocksReceived += ((CommPath)e.next()).getBlocksReceived();
		}
		return super.getBlocksReceived() + totalBlocksReceived;
	}
}
/**
 *	Returns the total blocks sent for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public final int getBlocksSent ( ) {
	Iterator e;
	int totalBlocksSent = 0;

	synchronized( sentLock ) {
		if (!aCommPathCollection.isEmpty()) {
			e = aCommPathCollection.values().iterator();
			while (e.hasNext())
				totalBlocksSent += ((CommPath)e.next()).getBlocksSent();
		}
		return super.getBlocksSent() + totalBlocksSent;
	}
}

/**
 *	Returns the total bytes ignored for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public final int getBytesIgnored ( ) {
	Iterator e;
	int totalBytesIgnored = 0;

	synchronized( ignoredLock ) {
		if (!aCommPathCollection.isEmpty()) {
			e = aCommPathCollection.values().iterator();
			while (e.hasNext())
				totalBytesIgnored += ((CommPath)e.next()).getBytesIgnored();
		}
		return super.getBytesIgnored() + totalBytesIgnored;
	}
}
/**
 *	Returns the total bytes lost for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public final int getBytesLost ( ) {
	Iterator e;
	int totalBytesLost = 0;

	synchronized( lostLock ) {
		if (!aCommPathCollection.isEmpty()) {
			e = aCommPathCollection.values().iterator();
			while (e.hasNext())
				totalBytesLost += ((CommPath)e.next()).getBytesLost();
		}
		return super.getBytesLost() + totalBytesLost;
	}
}
/**
 *	Returns the total bytes received for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public final int getBytesReceived ( ) {
	Iterator e;
	int totalBytesReceived = 0;

	synchronized( recvLock ) {
		if (!aCommPathCollection.isEmpty()) {
			e = aCommPathCollection.values().iterator();
			while (e.hasNext())
				totalBytesReceived += ((CommPath)e.next()).getBytesReceived();
		}
		return super.getBytesReceived() + totalBytesReceived;
	}
}
/**
 *	Returns the total bytes sent for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public final int getBytesSent ( ) {
	Iterator e;
	int totalBytesSent = 0;

	synchronized( sentLock ) {
		if (!aCommPathCollection.isEmpty()) {
			e = aCommPathCollection.values().iterator();
			while (e.hasNext())
				totalBytesSent += ((CommPath)e.next()).getBytesSent();
		}
		return super.getBytesSent() + totalBytesSent;
	}
}
/**
 *	This method is only for use by the CommPathFactory.  It is used to
 *	locate and return a CommPath based upon a particular pathName.
 *	This requires traversing the collections found for each root object.
 *	The pathName is broken down as we traverse the collections.
 * @return CommPath
 * @param aFullCommPathName String
 * @author Kevin Yaussy
 */
protected final CommPath getCommPath( StringTokenizer aFullCommPathName ) {
	CommPath aCommPath;

	if (aFullCommPathName.hasMoreTokens()) {
		aFullCommPathName.nextToken();
		aCommPath = (CommPath)aCommPathCollection.get( commPathName );
		if (aCommPath == null)
        {
			return null;
        }
        
        return aCommPath.getCommPath( aFullCommPathName );
	}

    return this;
}
/**
 *	This method returns an enumeration of all of the CommPaths in this
 *	instances' collection.
 * @return java.util.Enumeration
 * @author Kevin Yaussy
 */
protected Iterator getCommPathList( ) {
	Iterator rootEnum, leafEnum;
	CommPath cp;
	Vector tempV = new Vector();

	rootEnum = aCommPathCollection.values().iterator();
	while( rootEnum.hasNext() ) {
		cp = (CommPath)rootEnum.next();
		tempV.add( cp );
		leafEnum = cp.getCommPathList();
		while( leafEnum.hasNext() ) {
			cp = (CommPath)leafEnum.next();
			tempV.add( cp );
		}
	}

	return tempV.iterator();

}
/**
 *	Returns the commPathName for this CommPath.
 * @return String
 * @author Kevin Yaussy
 */
public final String getCommPathName() {
	return commPathName;
}
/**
 * Returns the collection of CommPaths for "this" as a Vector.
 * @return java.util.Vector
 * @author Kevin Yaussy
 */
public Vector getCommPaths() {
	Vector v = new Vector();
	Iterator e;

	e = aCommPathCollection.values().iterator();
	while ( e.hasNext() )
		v.add( e.next() );

	return v;
}
/**
 * Returns the depth of the CommPath in the full tree.
 * @return short
 * @author Kevin Yaussy
 */
public short getDepth() {
	return depth;
}
/**
 *	Returns the fullCommPathName for this CommPath.
 * @return String
 * @author Kevin Yaussy
 */
public final String getFullCommPathName() {
	return fullCommPathName;
}
/**
 *	Returns the total msgs ignored for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public final int getMsgsIgnored ( ) {
	Iterator e;
	int totalMsgsIgnored = 0;

	synchronized( ignoredLock ) {
		if (!aCommPathCollection.isEmpty()) {
			e = aCommPathCollection.values().iterator();
			while (e.hasNext())
				totalMsgsIgnored += ((CommPath)e.next()).getMsgsIgnored();
		}
		return super.getMsgsIgnored() + totalMsgsIgnored;
	}
}
/**
 *	Returns the total msgs lost for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public final int	getMsgsLost ( ) {
	Iterator e;
	int totalMsgsLost = 0;

	synchronized( lostLock ) {
		if (!aCommPathCollection.isEmpty()) {
			e = aCommPathCollection.values().iterator();
			while (e.hasNext())
				totalMsgsLost += ((CommPath)e.next()).getMsgsLost();
		}
		return super.getMsgsLost() + totalMsgsLost;
	}
}
/**
 *	Returns the total msgs received for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public final int getMsgsReceived ( ) {
	Iterator e;
	int totalMsgsReceived = 0;

	synchronized( recvLock ) {
		if (!aCommPathCollection.isEmpty()) {
			e = aCommPathCollection.values().iterator();
			while (e.hasNext())
				totalMsgsReceived += ((CommPath)e.next()).getMsgsReceived();
		}
		return super.getMsgsReceived() + totalMsgsReceived;
	}
}
/**
 *	Returns the total msgs sent for this CommPath and all of its
 *	children.
 * @return int
 * @author Kevin Yaussy
 */
public final int getMsgsSent( ) {
	Iterator e;
	int totalMsgsSent = 0;

	synchronized( sentLock ) {
		if (!aCommPathCollection.isEmpty()) {
			e = aCommPathCollection.values().iterator();
			while (e.hasNext())
				totalMsgsSent += ((CommPath)e.next()).getMsgsSent();
		}
		return super.getMsgsSent() + totalMsgsSent;
	}
}
/**
 * Getter for the total number of CommPaths in this collection.
 * @return int
 */
public int getNumCommPaths() {
	return aCommPathCollection.size();
}
/**
 * Returns the current status of the CommPath.
 * @return short
 * @author Kevin Yaussy
 */
public final short getStatus() {
	return status;
}
/**
 * This method returns the total tran time down the chain of children.
 * @return int
 */
public final int getTranTimeMills() {
	Iterator e;
	int totalTranTimeMills = 0;

	synchronized( this ) {
		if (!aCommPathCollection.isEmpty()) {
			e = aCommPathCollection.values().iterator();
			while (e.hasNext())
				totalTranTimeMills += ((CommPath)e.next()).getTranTimeMills();
		}
		return super.getTranTimeMills() + totalTranTimeMills;
	}
}
/**
 * Finds and returns the maximum tran time so far.  This goes down the children
 * collections to find the max.
 * @return int
 */
public final int getTranTimeMillsMax() {
	Iterator e;
	int tranTimeMillsMaxSoFar, tranTimeMillsMaxDown;

	synchronized( this ) {
		tranTimeMillsMaxSoFar = super.getTranTimeMillsMax();
		if (!aCommPathCollection.isEmpty()) {
			e = aCommPathCollection.values().iterator();
			while (e.hasNext()) {
				tranTimeMillsMaxDown = ((CommPath)e.next()).getTranTimeMillsMax();
				if (tranTimeMillsMaxDown > tranTimeMillsMaxSoFar)
					tranTimeMillsMaxSoFar = tranTimeMillsMaxDown;
			}
		}
		return tranTimeMillsMaxSoFar;
	}

}
/**
 *	Returns the userData for this CommPath.
 * @return String
 * @author Kevin Yaussy
 */
public final Object getUserData() {
	return userData;
}
/**
 * Returns true if the CommPath status is STATUS_DOWN.
 * @return boolean
 * @author Kevin Yaussy
 */
public final boolean isDown() {
	return status == STATUS_DOWN;
}
/**
 * Returns true if the CommPath status is STATUS_DOWN_PROBLEM.
 * @return boolean
 * @author Kevin Yaussy
 */
public final boolean isDownProblem() {
	return status == STATUS_DOWN_PROBLEM;
}
/**
 * Returns true if the CommPath status is STATUS_INIT.
 * @return boolean
 * @author Kevin Yaussy
 */
public final boolean isInit() {
	return status == STATUS_INIT;
}
/**
 * Returns true if the CommPath status is STATUS_NO_DATA.
 * @return boolean
 * @author Kevin Yaussy
 */
public final boolean isNoData() {
	return status == STATUS_NO_DATA;
}
/**
 * Returns true if the CommPath status is STATUS_STOPPED.
 * @return boolean
 * @author Kevin Yaussy
 */
public final boolean isStopped() {
	return status == STATUS_STOPPED;
}
/**
 * Returns true if the CommPath status is STATUS_SUSPENDED.
 * @return boolean
 * @author Kevin Yaussy
 */
public final boolean isSuspended() {
	return status == STATUS_SUSPENDED;
}
/**
 * Returns true if the CommPath status is STATUS_UP.
 * @return boolean
 * @author Kevin Yaussy
 */
public final boolean isUp() {
	return status == STATUS_UP;
}
/**
 * Sets the CommPath status.  
 * @param newStatus short
 * @author Kevin Yaussy
 */
public final void setStatus( short newStatus ) throws InvalidCommPathStatusException {

	synchronized( this ) {
		if (newStatus >= STATUS_MIN_VALUE && newStatus <= STATUS_MAX_VALUE )
			status = newStatus;
		else
			throw new InvalidCommPathStatusException();
	}
}
/**
 * This method sets the user data.
 * @param newUserData String
 * @author Kevin Yaussy
 */
public void setUserData ( Object newUserData ) {

	synchronized( this ) {
		userData = newUserData;
	}

}

	/**
	 * Set value of total heap.
	 *
	 * @param heapTotal a <code>long</code> value
	 */
	public final void setHeapTotal( long heapTotal ) {
		synchronized( this ) {
			this.heapTotal = heapTotal;
		}
	}

	/**
	 * Set value of free heap.
	 *
	 * @param heapFree a <code>long</code> value
	 */
	public final void setHeapFree( long heapFree ) {
		synchronized( this ) {
			this.heapFree = heapFree;
		}
	}

	/**
	 * Return heapTotal.
	 *
	 * @return a <code>long</code> value
	 */
	public final long getHeapTotal() {
		synchronized( this ) {
			return heapTotal;
		}
	}

	/**
	 * Return heapFree.
	 *
	 * @return a <code>long</code> value
	 */
	public final long getHeapFree() {
		synchronized( this ) {
			return heapFree;
		}
	}

	/**
	 * Returns the value of the leaf flag.  Indicates whether this comm-path
	 * is a leaf-node.
	 *
	 * @return a <code>boolean</code> value
	 */
	public boolean isLeaf() {
		return leaf;
	}
}
