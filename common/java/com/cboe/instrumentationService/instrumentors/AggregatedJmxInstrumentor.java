package com.cboe.instrumentationService.instrumentors;
/**
 * 
 * AggregatedJmxInstrumentor
 * 
 * @author neher
 * 
 * Created: Thur Oct 19 2006
 *
 * @version 1.0
 *
 */
public interface AggregatedJmxInstrumentor extends AggregatedInstrumentor {

    public void addInstrumentor( JmxInstrumentor jmxi );
    public void removeInstrumentor( JmxInstrumentor jmxi );
}
