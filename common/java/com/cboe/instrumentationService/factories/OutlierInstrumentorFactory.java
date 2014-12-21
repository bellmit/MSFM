package com.cboe.instrumentationService.factories;

import com.cboe.instrumentationService.instrumentors.OutlierInstrumentor;

/**
 * OutlierInstrumentorFactory
 *
 * Created: Thur Oct 19 2006
 * @author neher
 * @version 1.0
 */

public interface OutlierInstrumentorFactory extends InstrumentorFactory {

    public OutlierInstrumentor getInstance( String name, Object userObject );
    public OutlierInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException;
    public OutlierInstrumentor find( String name );
    public void register( OutlierInstrumentor oi );
    public void unregister( OutlierInstrumentor oi );
//    public void accept( OutlierInstrumentorFactoryVisitor oiVisitor, boolean exposeClonedMap );

}

