package com.cboe.instrumentationService.aggregator;
/**
 * 
 * JstatInstrumentorAggregatedFactory
 * 
 * @author neher
 * 
 * Created: November 2, 2006
 *
 * @version 1.0
 *
 */
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.impls.JstatInstrumentorDefaultFactory;
import com.cboe.instrumentationService.instrumentors.JstatInstrumentor;

public class JstatInstrumentorAggregatedFactory extends JstatInstrumentorDefaultFactory {
    public synchronized JstatInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
        JstatInstrumentor jstati = find( name );
        if ( jstati != null ) {
            throw new InstrumentorAlreadyCreatedException( "JstatInstrumentor '" + name + "' already exists." );
        }
        
        jstati = new JstatInstrumentorAggregatedImpl( name, userObject );
        byte[] key = makeKey( name );
        jstati.setKey( key );
        jstati.setFactory( this );

        return jstati;
    }

}
