package com.cboe.instrumentationService.impls;
/**
 * 
 * OutlierInstrumentorDefaultFactory
 * 
 * @author neher
 * 
 * Created: Thur Oct 19 2006
 *
 * @version 1.0
 *
 */
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.cboe.common.log.Logger;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.factories.OutlierInstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.OutlierInstrumentor;

public class OutlierInstrumentorDefaultFactory extends InstrumentorFactoryImpl implements OutlierInstrumentorFactory {
    
    private HashMap oiMap = new HashMap();
    private InstrumentorKeyUtil keyUtil = new InstrumentorKeyUtil( OutlierInstrumentor.INSTRUMENTOR_TYPE_NAME );

    public synchronized OutlierInstrumentor getInstance( String name, Object userObject ) {
        OutlierInstrumentor oi = null;
        try {
            oi = create( name, userObject );
            register( oi );
        } catch( InstrumentorAlreadyCreatedException e ) {
            oi = find( name );
        }

        return oi;
    }

    public synchronized OutlierInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
        OutlierInstrumentor oi = (OutlierInstrumentor)oiMap.get( name );
        if ( oi != null ) {
            throw new InstrumentorAlreadyCreatedException( "OutlierInstrumentor '" + name + "' already exists." );
        }
        oi = new OutlierInstrumentorImpl( name, userObject );
        byte[] key = makeKey( name );
        oi.setKey( key );
        oi.setFactory( this );

        return oi;
    }

    protected byte[] makeKey( String name ) {
        return keyUtil.makeKey( name );
    }

    public synchronized OutlierInstrumentor find( String name ) {
        return (OutlierInstrumentor)oiMap.get( name );
    }

    public synchronized void register( OutlierInstrumentor oi ) {
        if ( oi != null ) {
            byte[] key = oi.getKey();
            if ( key == null ) {
                // Something other than my create may be registering an instrumentor.  If so,
                // and there is no key currently, create a key and set it.
                key = makeKey( oi.getName() );
                oi.setKey( key );
            }
            // If the map does not already contain this instrumentor, add it.
            if ( oi.getName() != null && oiMap.get( oi.getName() ) == null ) {
                oiMap.put( oi.getName(), oi );
                notifyInstrumentorAdded( oi );
            }
        }
    }

    public synchronized void unregister( OutlierInstrumentor oi ) {
        oiMap.remove( oi.getName() );
        notifyInstrumentorRemoved( oi );
    }
/*
    public void accept( OutlierInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
        HashMap tempMap = null;
        synchronized( this ) {
            tempMap = (HashMap)oiMap.clone();
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
                OutlierInstrumentor oi = (OutlierInstrumentor)iter.next();
                if ( !visitor.visit( oi ) ) {
                    visitor.end();
                    return; // Visitor requested to quit.
                }
            }
        } catch( ConcurrentModificationException e ) {
            Logger.sysNotify( "OutlierInstrumentorFactoryVisitor.accept: map modified.  Shouldn't have happened!", e );
        }
        visitor.end();
    }
    */
}
