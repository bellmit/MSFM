package com.cboe.instrumentationService.impls;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.cboe.common.log.Logger;

/**
 * InstrumentorKeyUtil.java
 * This class will generate md5 keys based upon the following criteria:
 * 1) InstrumentorType, which is just a string describing the type of instrumentor
 * 2) InstrumentorName
 * 3) and OrbName
 *
 * It's not really appropriate to use OrbName in this code package, since there is no
 * ORB dependency here, but it is a value which is unique within an environment.  No
 * ORB code will be used / referenced, just getting the ORB.OrbName property.
 *
 * Created: Fri Sep  5 14:47:17 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class InstrumentorKeyUtil {

	private String instrumentorType;
	private String orbName = System.getProperty( "ORB.OrbName", "" );
	private MessageDigest theDigester = null;

	public InstrumentorKeyUtil( String instrumentorType ) {
		this.instrumentorType = instrumentorType;
		try {
			theDigester = MessageDigest.getInstance("SHA");
		}
		catch(NoSuchAlgorithmException nsae) {
			Logger.sysNotify( "InstrumentorKeyUtil: unable to create MessageDigest.", nsae );
		}
	} // InstrumentorKeyUtil constructor

	public byte[] makeKey( String name ) {
		if ( name == null ) {
            return null;
        }
		synchronized( theDigester ) {
			theDigester.reset();
			theDigester.update( orbName.getBytes() );
			theDigester.update( instrumentorType.getBytes() );
			theDigester.update( name.getBytes() );

			return theDigester.digest();
		}
	}
	
} // InstrumentorKeyUtil
