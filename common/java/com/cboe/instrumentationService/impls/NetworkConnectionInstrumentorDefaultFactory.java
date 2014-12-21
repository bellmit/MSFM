package com.cboe.instrumentationService.impls;

import java.util.*;
import com.cboe.common.log.Logger;
import com.cboe.instrumentationService.factories.NetworkConnectionInstrumentorFactory;
import com.cboe.instrumentationService.factories.NetworkConnectionInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.instrumentors.NetworkConnectionInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

/**
 * NetworkConnectionInstrumentorDefaultFactory.java
 *
 *
 * Created: Thu Aug 28 14:35:24 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class NetworkConnectionInstrumentorDefaultFactory extends InstrumentorFactoryImpl implements NetworkConnectionInstrumentorFactory {

	private HashMap nciMap = new HashMap();
	private InstrumentorKeyUtil keyUtil = new InstrumentorKeyUtil( NetworkConnectionInstrumentor.INSTRUMENTOR_TYPE_NAME );

	public synchronized NetworkConnectionInstrumentor getInstance( String name, Object userObject ) {
		NetworkConnectionInstrumentor nci = null;
		try {
			nci = create( name, userObject );
			register( nci );
		} catch( InstrumentorAlreadyCreatedException e ) {
			nci = find( name );
		}

		return nci;
	}

	public synchronized NetworkConnectionInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		NetworkConnectionInstrumentor nci = (NetworkConnectionInstrumentor)nciMap.get( name );
		if ( nci != null ) {
            throw new InstrumentorAlreadyCreatedException( "NetworkConnectionInstrumentor '" + name + "' already exists." );
        }
		nci = new NetworkConnectionInstrumentorImpl( name, userObject );
		byte[] key = makeKey( name );
		nci.setKey( key );
		nci.setFactory( this );

		return nci;
	}

	protected byte[] makeKey( String name ) {
		return keyUtil.makeKey( name );
	}

	public synchronized NetworkConnectionInstrumentor find( String name ) {
		return (NetworkConnectionInstrumentor)nciMap.get( name );
	}

	public synchronized void register( NetworkConnectionInstrumentor nci ) {
		if ( nci != null ) {
			byte[] key = nci.getKey();
			if ( key == null ) {
				// Something other than my create may be registering an instrumentor.  If so,
				// and there is no key currently, create a key and set it.
				key = makeKey( nci.getName() );
				nci.setKey( key );
			}
			// If the map does not already contain this instrumentor, add it.
			if ( nci.getName() != null && nciMap.get( nci.getName() ) == null ) {
				nciMap.put( nci.getName(), nci );
				notifyInstrumentorAdded( nci );
			}
		}
	}

	public synchronized void unregister( NetworkConnectionInstrumentor nci ) {
		nciMap.remove( nci.getName() );
		notifyInstrumentorRemoved( nci );
	}

	public void accept( NetworkConnectionInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
		HashMap tempMap = null;
		synchronized( this ) {
			tempMap = (HashMap)nciMap.clone();
		}

		// Handing off the map to the visitor is a compromise with some
		// folks in the user community.  I am passing an unmodifiable clone
		// of the original so modifications aren't made to the original.
		Map roMap = exposeClonedMap ? Collections.unmodifiableMap( tempMap ) : null;
		if ( !visitor.start( roMap ) ) {
			return;
		}
		try {
			Iterator iter = tempMap.values().iterator();
			while( iter.hasNext() ) {
				NetworkConnectionInstrumentor nci = (NetworkConnectionInstrumentor)iter.next();
				if ( !visitor.visit( nci ) ) {
					visitor.end();
					return; // Visitor requested to quit.
				}
			}
		} catch( ConcurrentModificationException e ) {
			Logger.sysNotify( "NCIFactory.accept: map modified.  Shouldn't have happened!", e );
		}
		visitor.end();
	}

} // NetworkConnectionInstrumentorDefaultFactory
