
package com.cboe.instrumentationService.commPath;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.cboe.common.log.Logger;

/**
 *	This class is a factory for creating and managing CommPath objects.
 *	The collection of CommPath objects is a Composite Collection.  The
 *	structure of the collection is flexible - it follows a hierarchical
 *	directory structure model.
 * @author Kevin Yaussy
 */
public class CommPathFactory {
	public final static short TIMEDIST_DEFAULT_SECS = 2;
	public final static short TIMEDIST_DEFAULT_BUCKETS_PER_SEC = 2;
	public final static short TIMEDIST_MAX_BUCKETS_PER_SEC = 1024;
	static private CommPathFactory theCommPathFactory;
	static private Hashtable theCommPathCollection;
	static private Vector commPathListeners;
	static private int initialHashTableSize;
	static private int numCommPaths;

/**
 * This constructor is not to be called.  The getCommPathManager methods
 * must be used to obtain The CommPathManager - there is only one.
 */
private CommPathFactory ( ) {}
/**
 * This method adds to the list of listeners for changes to the CommPath
 * collection.
 * @param l com.cboe.instrumentationService.commPath.CommPathListener
 */
public synchronized void addCommPathListener( CommPathListener l ) {
	commPathListeners.add( l );
}
/**
 *	This method returns the value to be used to right-shift a delta-
 *	millisecond tran-time value by.  The shifted value will
 *	represent the bucket number within the timeDist array which
 *	should have its value incremented.
 * @return short
 * @param timeDistBucketsPerSec short
 */
private short calcTimeDistShiftAmt( short timeDistBucketsPerSec ) {
short i, timeDistShiftAmt;
int shiftNumber;
boolean found;

	shiftNumber = TIMEDIST_MAX_BUCKETS_PER_SEC / timeDistBucketsPerSec;
	timeDistShiftAmt = 0;
	found = false;
	for( i = 0; !found && (1 << i) <= TIMEDIST_MAX_BUCKETS_PER_SEC; i++) {
		if (shiftNumber == (1 << i)) {
			found = true;
			timeDistShiftAmt = i;
		}
	}

	return timeDistShiftAmt;
}
/**
 * This method is an entry to shutdown the commpath capture thread cleanly.
 * @author Brad Samuels
 */
public void cleanup() {
	return;
}
/**
 *	This method calls the base createCommPath with the default values
 *	for the Time Distribution values.
 * @param fullCommPathName java.lang.String
 * @param newUserData java.lang.Object
 * @return CommPath
 * @author Kevin Yaussy
 */
public synchronized CommPath createCommPath ( String fullCommPathName, Object newUserData ) {
CommPath cp = null;

	try {
		cp = createCommPath( fullCommPathName, newUserData,
			TIMEDIST_DEFAULT_SECS, TIMEDIST_DEFAULT_BUCKETS_PER_SEC );
	}
	// Don't do anything with the exception, as the values being validated
	// are known defaults.
	catch ( InvalidTimeDistBucketsPerSecException e ) {}
	return cp;
}

/**
 *	This method will create a new CommPath object.  The factory will
 *	create both root objects and leaf objects.  The fullCommPathName
 *	specifies the hierarchy to be used.  The format of this argument
 *	follows a UNIX-like hierarchical directory structure model.
 *		For example:	"root1/a/b/c"
 *					"root2/x/y"
 *					"root2/x/b"
 * @param fullCommPathName java.lang.String
 * @param newUserData java.lang.Object
 * @param timeDistSecs int
 * @param timeDistBucketsPerSec int
 * @return CommPath
 * @author Kevin Yaussy
 */
	public synchronized CommPath createCommPath ( String fullCommPathName,
										 Object newUserData,
										 short timeDistSecs,
										 short timeDistBucketsPerSec  )
		throws InvalidTimeDistBucketsPerSecException {
		CommPath newCommPath;
		StringTokenizer strTok = new StringTokenizer( fullCommPathName, "/" );
		String key;
		StringBuffer pathName = new StringBuffer();
		Hashtable curCommPathCollection;
		short depth = 0, timeDistShiftAmt;

		if (!isValidTimeDistBucketsPerSec( timeDistBucketsPerSec ))
			throw new InvalidTimeDistBucketsPerSecException();
		timeDistShiftAmt = calcTimeDistShiftAmt( timeDistBucketsPerSec );

		newCommPath = null;
		curCommPathCollection = theCommPathCollection;
		while( strTok.hasMoreTokens() ) {
			key = strTok.nextToken();
			if (key.length() == 0)
				continue;
			pathName.append( "/" + key );
			newCommPath = (CommPath)curCommPathCollection.get( key );
			if (newCommPath == null) {
				numCommPaths++;
				newCommPath = new CommPath( pathName.toString(), key,
									   depth, initialHashTableSize,
									   timeDistSecs, timeDistBucketsPerSec,
									   timeDistShiftAmt, !strTok.hasMoreTokens() );
				curCommPathCollection.put( key,
														  newCommPath );
				// Notify listeners for each CommPath added in the 
				// heirarchy.
				notifyCommPathAdded( newCommPath );
			}
			curCommPathCollection = newCommPath.aCommPathCollection;
			depth++;
		}

		newCommPath.setUserData( newUserData );
		Logger.debug("CreateCommPath: Added comm-path to collection: " + fullCommPathName );
		return newCommPath;

	}
/**
 *	This is a method for testing the validity of the collection.  It is
 *	only for display purposes to the console.
 *	@author Kevin Yaussy
 */
public synchronized void displayCommPathCollection ( ) {
Enumeration				e;
CommPath						cp;

	e = theCommPathCollection.elements();
	while (e.hasMoreElements()) {
		cp = (CommPath)e.nextElement();
		cp.displayCommPathCollection( "" );
	}	
}
/**
 *	This method returns a CommPath object, if it has been created,
 *	based upon the argument "fullCommPathName".
 * @return CommPath
 * @param fullCommPathName String
 * @author Kevin Yaussy
 */
public CommPath getCommPath (String fullCommPathName) {
String commPathName;
CommPath aCommPath;
StringTokenizer aTok = new StringTokenizer( fullCommPathName, "/" );

	if (aTok.hasMoreTokens()) {
		commPathName = aTok.nextToken();
		aCommPath = (CommPath)theCommPathCollection.get( commPathName );
		if (aCommPath == null) {
			return null;
        }
		
			return aCommPath.getCommPath( aTok );
	}

		return null;
}
	/**
	 * This is goofy, I know.  I need something that I can try to call
	 * from messaging (or other low-level places) to ensure that I am
	 * using the correct version of comm-path.  During the rollout
	 * of maintenance release 5.11 I am going to have to make sure the
	 * application survives (runs) using the new messaging but old
	 * comm-path.  The old comm-path tries to initialize the FF, but
	 * that will break things down inside messaging.
	 *
	 */
	public static void newVersion() {
	}

/**
 *	Creates or returns the Factory.  This particular method will create
 *	the Factory with the default hashtable size.
 * @return CommPathFactory
 * @author Kevin Yaussy
 */
public static	synchronized	CommPathFactory getCommPathFactory() {

	return getCommPathFactory( 0 );

}
/**
 *	Creates or returns the Factory.  This particular method will create
 *	the factory with a hash table size specified in the argument
 *	"initialSize".  If initialSize is 0, then the default size is used.
 * @return CommPathFactory
 * @param initialSize int
 * @author Kevin Yaussy
 */
public static synchronized CommPathFactory getCommPathFactory ( int initialSize ) {

	initialHashTableSize = initialSize;
	if (theCommPathFactory == null) {
		theCommPathFactory = new CommPathFactory();
		if (initialHashTableSize > 0)
			theCommPathCollection = new Hashtable( initialHashTableSize );
		else
			theCommPathCollection = new Hashtable( );
		commPathListeners = new Vector();
	}
	return theCommPathFactory;

}

/**
 * This method returns an enumeration of the entire collection of comm path
 * objects.
 * @return Enumeration An enumeration of the collection of comm path objects.
 */
public synchronized Iterator getCommPathList ( ) {
	Iterator rootEnum, leafEnum;
	CommPath cp;
	Vector tempV = new Vector();

	rootEnum = theCommPathCollection.values().iterator();
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
 * This method returns a vector containing the first level set of comm path
 * objects.
 * @return java.util.Vector
 */
public synchronized Vector getCommPaths() {
Vector v = new Vector();
Iterator e;

	e = theCommPathCollection.values().iterator();
	while ( e.hasNext() )
		v.add( e.next() );

	return v;
}
/**
 * This method returns the collection of CommPaths for the given
 * (partial) pathName.  I.E. the pathName can specify a partial path
 * and the method returns all collections underneath.
 * @return java.util.Vector
 * @param String pathName
 * @author Kevin Yaussy
 */
public synchronized Vector getCommPaths( String pathName ) {
Vector v = new Vector();
Iterator e;
CommPath cp;

	cp = getCommPath( pathName );
	if ( cp != null ) {
		e = cp.aCommPathCollection.values().iterator();
		while ( e.hasNext() )
			v.add( e.next() );
	}

	return v;
}
/**
 * Getter for numCommPaths.
 * @return int
 */
public int getNumCommPaths() {
	return numCommPaths;
}
/**
 * This method returns true if the singleton reference has been initialized.
 * @return boolean
 * @author Brad Samuels
 */
public static synchronized boolean isInitialized() {
	if(theCommPathFactory == null)
		return false;
	return true;
}
/**
 * Returns true if the value of timeDistBucketsPerSec is a valid value.
 * Powers of 2 up to TIMEDIST_MAX_BUCKETS_PER_SEC are valid values.
 * @return boolean
 * @param timeDistBucketsPerSec int
 */
private boolean isValidTimeDistBucketsPerSec( short timeDistBucketsPerSec ) {
boolean found;
short i;

	found = false;
	for (i = 0; !found && (1 << i) <= TIMEDIST_MAX_BUCKETS_PER_SEC; i++) {
		if (timeDistBucketsPerSec == (1 << i))
			found = true;
	}

	return found;
}
/**
 * This method is called when a new CommPath object has been added to the
 * master collection.  Each commPathListener is called to notify them of the
 * addition.
 * @param cp The CommPath object just added.
 */
private void notifyCommPathAdded( CommPath cp ) {
int i;

	for ( i = 0; i < commPathListeners.size(); i++ )
		((CommPathListener)commPathListeners.get( i )).commPathAdded( cp );
}
/**
 * This method removes a listener from the list of CommPathListeners.
 * @param l com.cboe.instrumentationService.commPath.CommPathListener
 */
public synchronized void removeCommPathListener( CommPathListener l ) {
	commPathListeners.remove( l );
}
}
