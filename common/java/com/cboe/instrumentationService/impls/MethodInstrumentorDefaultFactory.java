package com.cboe.instrumentationService.impls;

import java.util.*;
import com.cboe.common.log.Logger;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

/**
 * MethodInstrumentorDefaultFactory.java
 *
 *
 * Created: Tue Sep  9 13:59:40 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class MethodInstrumentorDefaultFactory extends InstrumentorFactoryImpl implements MethodInstrumentorFactory {

	private HashMap miMap = new HashMap();
	private InstrumentorKeyUtil keyUtil = new InstrumentorKeyUtil( MethodInstrumentor.INSTRUMENTOR_TYPE_NAME );

	public synchronized MethodInstrumentor getInstance( String name, Object userObject ) {
		MethodInstrumentor mi = null;
		try {
			mi = create( name, userObject );
			register( mi );
		} catch( InstrumentorAlreadyCreatedException e ) {
			mi = find( name );
		}

		return mi;
	}

	public synchronized MethodInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		MethodInstrumentor mi = (MethodInstrumentor)miMap.get( name );
		if ( mi != null ) {
            throw new InstrumentorAlreadyCreatedException( "MethodInstrumentor '" + name + "' already exists." );
        }
		mi = new MethodInstrumentorImpl( name, userObject );
		byte[] key = makeKey( name );
		mi.setKey( key );
		mi.setFactory( this );

		return mi;
	}

	protected byte[] makeKey( String name ) {
		return keyUtil.makeKey( name );
	}

	public synchronized MethodInstrumentor find( String name ) {
		return (MethodInstrumentor)miMap.get( name );
	}

	public synchronized void register( MethodInstrumentor mi ) {
		if ( mi != null ) {
			byte[] key = mi.getKey();
			if ( key == null ) {
				// Something other than my create may be registering an instrumentor.  If so,
				// and there is no key currently, create a key and set it.
				key = makeKey( mi.getName() );
				mi.setKey( key );
			}
			// If the map does not already contain this instrumentor, add it.
			if ( mi.getName() != null && miMap.get( mi.getName() ) == null ) {
				miMap.put( mi.getName(), mi );
				notifyInstrumentorAdded( mi );
			}
		}
	}

	public synchronized void unregister( MethodInstrumentor mi ) {
		miMap.remove( mi.getName() );
		notifyInstrumentorRemoved( mi );
	}

	public void accept( MethodInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
		HashMap tempMap = null;
		synchronized( this ) {
			tempMap = (HashMap)miMap.clone();
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
				MethodInstrumentor mi = (MethodInstrumentor)iter.next();
				if ( !visitor.visit( mi ) ) {
					visitor.end();
					return; // Visitor requested to quit.
				}
			}
		} catch( ConcurrentModificationException e ) {
			Logger.sysNotify( "MIFactory.accept: map modified.  Shouldn't have happened!", e );
		}
		visitor.end();
	}

} // MethodInstrumentorDefaultFactory
