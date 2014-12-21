package com.cboe.instrumentationService.factories;
/**
 * JstatInstrumentorFactory.java
 *
 * Created: Thur November 2 2006
 * @author neher
 * @version 1.0
 */
import com.cboe.instrumentationService.instrumentors.JstatInstrumentor;

public interface JstatInstrumentorFactory extends InstrumentorFactory {
    public JstatInstrumentor getInstance( String name, Object userObject );
    public JstatInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException;
    public JstatInstrumentor find( String name );
    public void register( JstatInstrumentor jstati );
    public void unregister( JstatInstrumentor jstati );
    public void accept( JstatInstrumentorFactoryVisitor jstatiVisitor, boolean exposeClonedMap );

}
