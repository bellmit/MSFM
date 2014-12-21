package com.cboe.instrumentationService.impls;

import java.util.*;
import com.cboe.common.log.Logger;
import com.cboe.instrumentationService.factories.CountInstrumentorFactory;
import com.cboe.instrumentationService.factories.CountInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.instrumentors.CountInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

/**
 * CountInstrumentorDefaultFactory.java
 *
 *
 * Created: Mon Oct  6 14:21:31 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class CountInstrumentorDefaultFactory extends InstrumentorFactoryImpl implements CountInstrumentorFactory {

	private HashMap ciMap = new HashMap();
	private InstrumentorKeyUtil keyUtil = new InstrumentorKeyUtil( CountInstrumentor.INSTRUMENTOR_TYPE_NAME );

	public synchronized CountInstrumentor getInstance( String name, Object userObject ) {
		CountInstrumentor ci = null;
		try {
			ci = create( name, userObject );
			register( ci );
		} catch( InstrumentorAlreadyCreatedException e ) {
			ci = find( name );
		}

		return ci;
	}

	public synchronized CountInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		CountInstrumentor ci = (CountInstrumentor)ciMap.get( name );
		if ( ci != null ) {
            throw new InstrumentorAlreadyCreatedException( "CountInstrumentor '" + name + "' already exists." );
        }
		ci = new CountInstrumentorImpl( name, userObject );
		byte[] key = makeKey( name );
		ci.setKey( key );
		ci.setFactory( this );

		return ci;
	}

	protected byte[] makeKey( String name ) {
		return keyUtil.makeKey( name );
	}

	public synchronized CountInstrumentor find( String name ) {
		return (CountInstrumentor)ciMap.get( name );
	}

	public synchronized void register( CountInstrumentor ci ) {
		if ( ci != null ) {
			byte[] key = ci.getKey();
			if ( key == null ) {
				// Something other than my create may be registering an instrumentor.  If so,
				// and there is no key currently, create a key and set it.
				key = makeKey( ci.getName() );
				ci.setKey( key );
			}
			// If the map does not already contain this instrumentor, add it.
			if ( ci.getName() != null && ciMap.get( ci.getName() ) == null ) {
				ciMap.put( ci.getName(), ci );
				notifyInstrumentorAdded( ci );
			}
		}
	}

	public synchronized void unregister( CountInstrumentor ci ) {
		ciMap.remove( ci.getName() );
		notifyInstrumentorRemoved( ci );
	}

	public void accept( CountInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
		HashMap tempMap = null;
		synchronized( this ) {
			tempMap = (HashMap)ciMap.clone();
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
				CountInstrumentor ci = (CountInstrumentor)iter.next();
				if ( !visitor.visit( ci ) ) {
					visitor.end();
					return; // Visitor requested to quit.
				}
			}
		} catch( ConcurrentModificationException e ) {
			Logger.sysNotify( "CIFactory.accept: map modified.  Shouldn't have happened!", e );
		}
		visitor.end();
	}

} // CountInstrumentorDefaultFactory
