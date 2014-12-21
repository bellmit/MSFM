package com.cboe.instrumentationService.factories;

import com.cboe.instrumentationService.instrumentors.JmxInstrumentor;

/**
 * JmxInstrumentorFactory
 *
 * Created: Thur Oct 19 2006
 * @author neher
 * @version 1.0
 */

public interface JmxInstrumentorFactory extends InstrumentorFactory {

    public JmxInstrumentor getInstance( String name, Object userObject );
    public JmxInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException;
    public JmxInstrumentor find( String name );
    public void register( JmxInstrumentor jmxi );
    public void unregister( JmxInstrumentor jmxi );
    public void accept( JmxInstrumentorFactoryVisitor jmxiVisitor, boolean exposeClonedMap );

}

