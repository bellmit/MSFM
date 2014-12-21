package com.cboe.instrumentationService.calculator;
/**
 * 
 * KeyValueInstrumentorCalulatedFactory
 * 
 * @author neher
 * 
 * Created: Fri Oct 20 2006
 *
 * @version 1.0
 *
 */
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.impls.JmxInstrumentorDefaultFactory;
import com.cboe.instrumentationService.instrumentors.JmxInstrumentor;

public class JmxInstrumentorCalculatedFactory extends JmxInstrumentorDefaultFactory {
    public JmxInstrumentorCalculatedFactory() {
    } // JmxInstrumentorCalculatedFactory constructor

    public synchronized JmxInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
        // Rely on default factory to throw exception.
        JmxInstrumentor rawInst = super.create( name, userObject );
        JmxInstrumentor calcInst = new JmxInstrumentorCalculatedImpl( rawInst );
        calcInst.setFactory( this );
        return calcInst;
    }
}
