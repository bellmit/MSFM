package com.cboe.instrumentationService.impls;

import java.util.*;
import com.cboe.common.log.Logger;
import com.cboe.instrumentationService.factories.ThreadPoolInstrumentorFactory;
import com.cboe.instrumentationService.factories.ThreadPoolInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.instrumentors.ThreadPoolInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

/**
 * ThreadPoolInstrumentorDefaultFactory.java
 *
 *
 * Created: Thu Aug 28 14:35:24 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class ThreadPoolInstrumentorDefaultFactory extends InstrumentorFactoryImpl implements ThreadPoolInstrumentorFactory {

	private HashMap tpiMap = new HashMap();
	private InstrumentorKeyUtil keyUtil = new InstrumentorKeyUtil( ThreadPoolInstrumentor.INSTRUMENTOR_TYPE_NAME );

	public synchronized ThreadPoolInstrumentor getInstance( String name, Object userObject ) {
		ThreadPoolInstrumentor tpi = null;
		try {
			tpi = create( name, userObject );
			register( tpi );
		} catch( InstrumentorAlreadyCreatedException e ) {
			tpi = find( name );
		}

		return tpi;
	}

	public synchronized ThreadPoolInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		ThreadPoolInstrumentor tpi = (ThreadPoolInstrumentor)tpiMap.get( name );
		if ( tpi != null ) {
            throw new InstrumentorAlreadyCreatedException( "ThreadPoolInstrumentor '" + name + "' already exists." );
        }
		tpi = new ThreadPoolInstrumentorImpl( name, userObject );
		byte[] key = makeKey( name );
		tpi.setKey( key );
		tpi.setFactory( this );

		return tpi;
	}

	protected byte[] makeKey( String name ) {
		return keyUtil.makeKey( name );
	}

	public synchronized ThreadPoolInstrumentor find( String name ) {
		return (ThreadPoolInstrumentor)tpiMap.get( name );
	}

	public synchronized void register( ThreadPoolInstrumentor tpi ) {
		if ( tpi != null ) {
			byte[] key = tpi.getKey();
			if ( key == null ) {
				// Something other than my create may be registering an instrumentor.  If so,
				// and there is no key currently, create a key and set it.
				key = makeKey( tpi.getName() );
				tpi.setKey( key );
			}
			// If the map does not already contain this instrumentor, add it.
			if ( tpi.getName() != null && tpiMap.get( tpi.getName() ) == null ) {
				tpiMap.put( tpi.getName(), tpi );
				notifyInstrumentorAdded( tpi );
			}
		}
	}

	public synchronized void unregister( ThreadPoolInstrumentor tpi ) {
		tpiMap.remove( tpi.getName() );
		notifyInstrumentorRemoved( tpi );
	}

	public void accept( ThreadPoolInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
		HashMap tempMap = null;
		synchronized( this ) {
			tempMap = (HashMap)tpiMap.clone();
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
				ThreadPoolInstrumentor tpi = (ThreadPoolInstrumentor)iter.next();
				if ( !visitor.visit( tpi ) ) {
					visitor.end();
					return; // Visitor requested to quit.
				}
			}
		} catch( ConcurrentModificationException e ) {
			Logger.sysNotify( "TPIFactory.accept: map modified.  Shouldn't have happened!", e );
		}
		visitor.end();
	}

} // ThreadPoolInstrumentorDefaultFactory
