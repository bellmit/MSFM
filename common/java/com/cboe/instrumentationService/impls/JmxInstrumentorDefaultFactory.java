package com.cboe.instrumentationService.impls;
/**
 * 
 * JmxInstrumentorDefaultFactory
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
import com.cboe.instrumentationService.factories.JmxInstrumentorFactory;
import com.cboe.instrumentationService.factories.JmxInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.instrumentors.JmxInstrumentor;

public class JmxInstrumentorDefaultFactory extends InstrumentorFactoryImpl implements JmxInstrumentorFactory {
    
    private HashMap jmxiMap = new HashMap();
    private InstrumentorKeyUtil keyUtil = new InstrumentorKeyUtil( JmxInstrumentor.INSTRUMENTOR_TYPE_NAME );

    public synchronized JmxInstrumentor getInstance( String name, Object userObject ) {
        JmxInstrumentor jmxi = null;
        try {
            jmxi = create( name, userObject );
            register( jmxi );
        } catch( InstrumentorAlreadyCreatedException e ) {
            jmxi = find( name );
        }

        return jmxi;
    }

    public synchronized JmxInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
        JmxInstrumentor jmxi = (JmxInstrumentor)jmxiMap.get( name );
        if ( jmxi != null ) {
            throw new InstrumentorAlreadyCreatedException( "JmxInstrumentor '" + name + "' already exists." );
        }
        jmxi = new JmxInstrumentorImpl( name, userObject );
        byte[] key = makeKey( name );
        jmxi.setKey( key );
        jmxi.setFactory( this );

        return jmxi;
    }

    protected byte[] makeKey( String name ) {
        return keyUtil.makeKey( name );
    }

    public synchronized JmxInstrumentor find( String name ) {
        return (JmxInstrumentor)jmxiMap.get( name );
    }

    public synchronized void register( JmxInstrumentor jmxi ) {
        if ( jmxi != null ) {
            byte[] key = jmxi.getKey();
            if ( key == null ) {
                // Something other than my create may be registering an instrumentor.  If so,
                // and there is no key currently, create a key and set it.
                key = makeKey( jmxi.getName() );
                jmxi.setKey( key );
            }
            // If the map does not already contain this instrumentor, add it.
            if ( jmxi.getName() != null && jmxiMap.get( jmxi.getName() ) == null ) {
                jmxiMap.put( jmxi.getName(), jmxi );
                notifyInstrumentorAdded( jmxi );
            }
        }
    }

    public synchronized void unregister( JmxInstrumentor jmxi ) {
        jmxiMap.remove( jmxi.getName() );
        notifyInstrumentorRemoved( jmxi );
    }

    public void accept( JmxInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
        HashMap tempMap = null;
        synchronized( this ) {
            tempMap = (HashMap)jmxiMap.clone();
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
                JmxInstrumentor jmxi = (JmxInstrumentor)iter.next();
                if ( !visitor.visit( jmxi ) ) {
                    visitor.end();
                    return; // Visitor requested to quit.
                }
            }
        } catch( ConcurrentModificationException e ) {
            Logger.sysNotify( "JMXIFactory.accept: map modified.  Shouldn't have happened!", e );
        }
        visitor.end();
    }
}
