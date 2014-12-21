package com.cboe.instrumentationService.impls;

import java.util.*;
import com.cboe.common.log.Logger;
import com.cboe.instrumentationService.factories.EventChannelInstrumentorFactory;
import com.cboe.instrumentationService.factories.EventChannelInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.instrumentors.EventChannelInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

/**
 * EventChannelInstrumentorDefaultFactory.java
 *
 *
 * Created: Thu Aug 28 14:35:24 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class EventChannelInstrumentorDefaultFactory extends InstrumentorFactoryImpl implements EventChannelInstrumentorFactory {

	private HashMap eciMap = new HashMap();
	private InstrumentorKeyUtil keyUtil = new InstrumentorKeyUtil( EventChannelInstrumentor.INSTRUMENTOR_TYPE_NAME );

	public synchronized EventChannelInstrumentor getInstance( String name, Object userObject ) {
		EventChannelInstrumentor eci = null;
		try {
			eci = create( name, userObject );
			register( eci );
		} catch( InstrumentorAlreadyCreatedException e ) {
			eci = find( name );
		}

		return eci;
	}

	public synchronized EventChannelInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		EventChannelInstrumentor eci = (EventChannelInstrumentor)eciMap.get( name );
		if ( eci != null ) {
            throw new InstrumentorAlreadyCreatedException( "EventChannelInstrumentor '" + name + "' already exists." );
        }
        
        eci = new EventChannelInstrumentorImpl( name, userObject );
		byte[] key = makeKey( name );
		eci.setKey( key );
		eci.setFactory( this );

		return eci;
	}

	protected byte[] makeKey( String name ) {
		return keyUtil.makeKey( name );
	}

	public synchronized EventChannelInstrumentor find( String name ) {
		return (EventChannelInstrumentor)eciMap.get( name );
	}

	public synchronized void register( EventChannelInstrumentor eci ) {
		if ( eci != null ) {
			byte[] key = eci.getKey();
			if ( key == null ) {
				// Something other than my create may be registering an instrumentor.  If so,
				// and there is no key currently, create a key and set it.
				key = makeKey( eci.getName() );
				eci.setKey( key );
			}
			// If the map does not already contain this instrumentor, add it.
			if ( eci.getName() != null && eciMap.get( eci.getName() ) == null ) {
				eciMap.put( eci.getName(), eci );
				notifyInstrumentorAdded( eci );
			}
		}
	}

	public synchronized void unregister( EventChannelInstrumentor eci ) {
		eciMap.remove( eci.getName() );
		notifyInstrumentorRemoved( eci );
	}

	public void accept( EventChannelInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
		HashMap tempMap = null;
		synchronized( this ) {
			tempMap = (HashMap)eciMap.clone();
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
				EventChannelInstrumentor eci = (EventChannelInstrumentor)iter.next();
				if ( !visitor.visit( eci ) ) {
					visitor.end();
					return; // Visitor requested to quit.
				}
			}
		} catch( ConcurrentModificationException e ) {
			Logger.sysNotify( "ECIFactory.accept: map modified.  Shouldn't have happened!", e );
		}
		visitor.end();
	}

} // EventChannelInstrumentorDefaultFactory
