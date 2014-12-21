package com.cboe.instrumentationService.impls;
/**
 * 
 * JstatInstrumentorDefaultFactory
 * 
 * @author neher
 * 
 * Created: November 2, 2006
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
import com.cboe.instrumentationService.factories.JstatInstrumentorFactory;
import com.cboe.instrumentationService.factories.JstatInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.instrumentors.JstatInstrumentor;

public class JstatInstrumentorDefaultFactory extends InstrumentorFactoryImpl implements JstatInstrumentorFactory {

    private HashMap jstatiMap = new HashMap();
    private InstrumentorKeyUtil keyUtil = new InstrumentorKeyUtil( JstatInstrumentor.INSTRUMENTOR_TYPE_NAME );

    public synchronized JstatInstrumentor getInstance( String name, Object userObject ) {
        JstatInstrumentor jstati = null;
        try {
            jstati = create( name, userObject );
            register( jstati );
        } catch( InstrumentorAlreadyCreatedException e ) {
            jstati = find( name );
        }

        return jstati;
    }

    public synchronized JstatInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
        JstatInstrumentor jstati = (JstatInstrumentor)jstatiMap.get( name );
        if ( jstati != null ) {
            throw new InstrumentorAlreadyCreatedException( "JstatInstrumentor '" + name + "' already exists." );
        }
        jstati = new JstatInstrumentorImpl( name, userObject );
        byte[] key = makeKey( name );
        jstati.setKey( key );
        jstati.setFactory( this );

        return jstati;
    }

    protected byte[] makeKey( String name ) {
        return keyUtil.makeKey( name );
    }

    public synchronized JstatInstrumentor find( String name ) {
        return (JstatInstrumentor)jstatiMap.get( name );
    }

    public synchronized void register( JstatInstrumentor jstati ) {
        if ( jstati != null ) {
            byte[] key = jstati.getKey();
            if ( key == null ) {
                // Something other than my create may be registering an instrumentor.  If so,
                // and there is no key currently, create a key and set it.
                key = makeKey( jstati.getName() );
                jstati.setKey( key );
            }
            // If the map does not already contain this instrumentor, add it.
            if ( jstati.getName() != null && jstatiMap.get( jstati.getName() ) == null ) {
                jstatiMap.put( jstati.getName(), jstati );
                notifyInstrumentorAdded( jstati );
            }
        }
    }

    public synchronized void unregister( JstatInstrumentor jstati ) {
        jstatiMap.remove( jstati.getName() );
        notifyInstrumentorRemoved( jstati );
    }

    public void accept( JstatInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
        HashMap tempMap = null;
        synchronized( this ) {
            tempMap = (HashMap)jstatiMap.clone();
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
                JstatInstrumentor jstati = (JstatInstrumentor)iter.next();
                if ( !visitor.visit( jstati ) ) {
                    visitor.end();
                    return; // Visitor requested to quit.
                }
            }
        } catch( ConcurrentModificationException e ) {
            Logger.sysNotify( "JSTATIFactory.accept: map modified.  Shouldn't have happened!", e );
        }
        visitor.end();
    }

}
