package com.cboe.instrumentationService.impls;

import java.util.*;
import com.cboe.common.log.Logger;
import com.cboe.instrumentationService.factories.QueueInstrumentorFactory;
import com.cboe.instrumentationService.factories.QueueInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

/**
 * QueueInstrumentorDefaultFactory.java
 *
 *
 * Created: Thu Aug 28 14:35:24 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class QueueInstrumentorDefaultFactory extends InstrumentorFactoryImpl implements QueueInstrumentorFactory {

	private HashMap qiMap = new HashMap();
	private InstrumentorKeyUtil keyUtil = new InstrumentorKeyUtil( QueueInstrumentor.INSTRUMENTOR_TYPE_NAME );

	public synchronized QueueInstrumentor getInstance( String name, Object userObject ) {
		QueueInstrumentor qi = null;
		try {
			qi = create( name, userObject );
			register( qi );
		} catch( InstrumentorAlreadyCreatedException e ) {
			qi = find( name );
		}

		return qi;
	}

	public synchronized QueueInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		QueueInstrumentor qi = (QueueInstrumentor)qiMap.get( name );
		if ( qi != null ) {
            throw new InstrumentorAlreadyCreatedException( "QueueInstrumentor '" + name + "' already exists." );
        }
		qi = new QueueInstrumentorImpl( name, userObject );
		byte[] key = makeKey( name );
		qi.setKey( key );
		qi.setFactory( this );

		return qi;
	}

	protected byte[] makeKey( String name ) {
		return keyUtil.makeKey( name );
	}

	public synchronized QueueInstrumentor find( String name ) {
		return (QueueInstrumentor)qiMap.get( name );
	}

	public synchronized void register( QueueInstrumentor qi ) {
		if ( qi != null ) {
			byte[] key = qi.getKey();
			if ( key == null ) {
				// Something other than my create may be registering an instrumentor.  If so,
				// and there is no key currently, create a key and set it.
				key = makeKey( qi.getName() );
				qi.setKey( key );
			}
			// If the map does not already contain this instrumentor, add it.
			if ( qi.getName() != null && qiMap.get( qi.getName() ) == null ) {
				qiMap.put( qi.getName(), qi );
				notifyInstrumentorAdded( qi );
			}
		}
	}

	public synchronized void unregister( QueueInstrumentor qi ) {
		qiMap.remove( qi.getName() );
		notifyInstrumentorRemoved( qi );
	}

	public void accept( QueueInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
		HashMap tempMap = null;
		synchronized( this ) {
			tempMap = (HashMap)qiMap.clone();
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
				QueueInstrumentor qi = (QueueInstrumentor)iter.next();
				if ( !visitor.visit( qi ) ) {
					visitor.end();
					return; // Visitor requested to quit.
				}
			}
		} catch( ConcurrentModificationException e ) {
			Logger.sysNotify( "QIFactory.accept: map modified.  Shouldn't have happened!", e );
		}
		visitor.end();
	}

} // QueueInstrumentorDefaultFactory
