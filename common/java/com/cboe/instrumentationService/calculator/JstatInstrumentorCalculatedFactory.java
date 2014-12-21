package com.cboe.instrumentationService.calculator;
/**
 * 
 * JstatInstrumentorCalculatedFactory
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

public class JstatInstrumentorCalculatedFactory extends JstatInstrumentorDefaultFactory {
    public JstatInstrumentorCalculatedFactory() {
    } // JstatInstrumentorCalculatedFactory constructor

    public synchronized JstatInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
        // Rely on default factory to throw exception.
        JstatInstrumentor rawInst = super.create( name, userObject );
        JstatInstrumentor calcInst = new JstatInstrumentorCalculatedImpl( rawInst );
        calcInst.setFactory( this );
        return calcInst;
    }

}
