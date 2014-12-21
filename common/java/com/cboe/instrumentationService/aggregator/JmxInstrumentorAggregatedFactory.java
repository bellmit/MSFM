package com.cboe.instrumentationService.aggregator;
/**
 * 
 * JmxInstrumentorAggregatedFactory
 * 
 * @author neher
 * 
 * Created: Thur Oct 19 2006
 *
 * @version 1.0
 *
 */
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.impls.JmxInstrumentorDefaultFactory;
import com.cboe.instrumentationService.instrumentors.JmxInstrumentor;

public class JmxInstrumentorAggregatedFactory extends JmxInstrumentorDefaultFactory {
    public synchronized JmxInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
        JmxInstrumentor jmxi = find( name );
        if ( jmxi != null ) {
            throw new InstrumentorAlreadyCreatedException( "JmxInstrumentor '" + name + "' already exists." );
        }
        
        jmxi = new JmxInstrumentorAggregatedImpl( name, userObject );
        byte[] key = makeKey( name );
        jmxi.setKey( key );
        jmxi.setFactory( this );

        return jmxi;
    }

}
