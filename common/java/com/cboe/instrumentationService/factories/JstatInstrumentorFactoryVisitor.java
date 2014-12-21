package com.cboe.instrumentationService.factories;
/**
 * 
 * JstatInstrumentorFactoryVisitor
 * 
 * @author neher
 * 
 * Created: November 2, 2006
 *
 * @version 1.0
 *
 */
import com.cboe.instrumentationService.instrumentors.JstatInstrumentor;

public interface JstatInstrumentorFactoryVisitor extends InstrumentorFactoryVisitor {
    public boolean visit( JstatInstrumentor jstati );
}
