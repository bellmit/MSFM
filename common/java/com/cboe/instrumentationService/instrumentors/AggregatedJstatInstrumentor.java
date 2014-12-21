package com.cboe.instrumentationService.instrumentors;
/**
 * 
 * AggregatedJstatInstrumentor
 * 
 * @author neher
 * 
 * Created: November 2, 2006
 *
 * @version 1.0
 *
 */
public interface AggregatedJstatInstrumentor extends AggregatedInstrumentor {

    public void addInstrumentor( JstatInstrumentor jstati );
    public void removeInstrumentor( JstatInstrumentor jstati );
}
