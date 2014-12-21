package com.cboe.instrumentationService.factories;

import com.cboe.instrumentationService.instrumentors.JmxInstrumentor;

/**
 * JmxInstrumentorFactoryVisitor.java
 *
 * Created: Thur Oct 19 2006
 * @author neher
 * @version 1.0
 */

public interface JmxInstrumentorFactoryVisitor extends InstrumentorFactoryVisitor {
    public boolean visit( JmxInstrumentor jmxi );
}
