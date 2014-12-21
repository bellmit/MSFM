package com.cboe.instrumentationService.impls;

import java.util.*;
import com.cboe.common.log.Logger;
import com.cboe.instrumentationService.factories.HeapInstrumentorFactory;
import com.cboe.instrumentationService.factories.HeapInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.instrumentors.HeapInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

/**
 * HeapInstrumentorDefaultFactory.java
 *
 *
 * Created: Thu Aug 28 14:35:24 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class HeapInstrumentorDefaultFactory extends InstrumentorFactoryImpl implements HeapInstrumentorFactory {

	private HashMap hiMap = new HashMap();
	private InstrumentorKeyUtil keyUtil = new InstrumentorKeyUtil( HeapInstrumentor.INSTRUMENTOR_TYPE_NAME );

	public synchronized HeapInstrumentor getInstance( String name, Object userObject ) {
		HeapInstrumentor hi = null;
		try {
			hi = create( name, userObject );
			register( hi );
		} catch( InstrumentorAlreadyCreatedException e ) {
			hi = find( name );
		}

		return hi;
	}

	public synchronized HeapInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		HeapInstrumentor hi = (HeapInstrumentor)hiMap.get( name );
		if ( hi != null ) {
            throw new InstrumentorAlreadyCreatedException( "HeapInstrumentor '" + name + "' already exists." );
        }
		hi = new HeapInstrumentorImpl( name, userObject );
		byte[] key = makeKey( name );
		hi.setKey( key );
		hi.setFactory( this );

		return hi;
	}

	protected byte[] makeKey( String name ) {
		return keyUtil.makeKey( name );
	}

	public synchronized HeapInstrumentor find( String name ) {
		return (HeapInstrumentor)hiMap.get( name );
	}

	public synchronized void register( HeapInstrumentor hi ) {
		if ( hi != null ) {
			byte[] key = hi.getKey();
			if ( key == null ) {
				// Something other than my create may be registering an instrumentor.  If so,
				// and there is no key currently, create a key and set it.
				key = makeKey( hi.getName() );
				hi.setKey( key );
			}
			// If the map does not already contain this instrumentor, add it.
			if ( hi.getName() != null && hiMap.get( hi.getName() ) == null ) {
				hiMap.put( hi.getName(), hi );
				notifyInstrumentorAdded( hi );
			}
		}
	}

	public synchronized void unregister( HeapInstrumentor hi ) {
		hiMap.remove( hi.getName() );
		notifyInstrumentorRemoved( hi );
	}

	public void accept( HeapInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
		HashMap tempMap = null;
		synchronized( this ) {
			tempMap = (HashMap)hiMap.clone();
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
				HeapInstrumentor hi = (HeapInstrumentor)iter.next();
				if ( !visitor.visit( hi ) ) {
					visitor.end();
					return; // Visitor requested to quit.
				}
			}
		} catch( ConcurrentModificationException e ) {
			Logger.sysNotify( "HIFactory.accept: map modified.  Shouldn't have happened!", e );
		}
		visitor.end();
	}

} // HeapInstrumentorDefaultFactory
