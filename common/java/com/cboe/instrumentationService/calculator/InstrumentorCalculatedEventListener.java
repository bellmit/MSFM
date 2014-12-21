package com.cboe.instrumentationService.calculator;

import com.cboe.instrumentationService.instrumentors.CalculatedCountInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedEventChannelInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedHeapInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedJmxInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedJstatInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedMethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedNetworkConnectionInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedQueueInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedThreadPoolInstrumentor;
import com.cboe.instrumentationService.instrumentors.CountInstrumentor;
import com.cboe.instrumentationService.instrumentors.EventChannelInstrumentor;
import com.cboe.instrumentationService.instrumentors.HeapInstrumentor;
import com.cboe.instrumentationService.instrumentors.JmxInstrumentor;
import com.cboe.instrumentationService.instrumentors.JstatInstrumentor;
import com.cboe.instrumentationService.instrumentors.OutlierInstrumentor;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.NetworkConnectionInstrumentor;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import com.cboe.instrumentationService.instrumentors.ThreadPoolInstrumentor;

/**
 * InstrumentorCalculatedEventListener.java
 *
 *
 * Created: Wed Sep 17 08:14:45 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface InstrumentorCalculatedEventListener {

	public void acceptCalculatedCountInstrumentorEvent( long eventTimeMillis, CountInstrumentor total, CalculatedCountInstrumentor calculated, String clusterName, String orbName );
	public void acceptCalculatedEventChannelInstrumentorEvent( long eventTimeMillis, EventChannelInstrumentor total, CalculatedEventChannelInstrumentor calculated, String clusterName, String orbName );
	public void acceptCalculatedHeapInstrumentorEvent( long eventTimeMillis, HeapInstrumentor total, CalculatedHeapInstrumentor calculated, String clusterName, String orbName );
	public void acceptCalculatedMethodInstrumentorEvent( long eventTimeMillis, MethodInstrumentor total, CalculatedMethodInstrumentor calculated, String clusterName, String orbName );
	public void acceptCalculatedNetworkConnectionInstrumentorEvent( long eventTimeMillis, NetworkConnectionInstrumentor total, CalculatedNetworkConnectionInstrumentor calculated, String clusterName, String orbName );
	public void acceptCalculatedQueueInstrumentorEvent( long eventTimeMillis, QueueInstrumentor total, CalculatedQueueInstrumentor calculated, String clusterName, String orbName );
    public void acceptCalculatedThreadPoolInstrumentorEvent( long eventTimeMillis, ThreadPoolInstrumentor total, CalculatedThreadPoolInstrumentor calculated, String clusterName, String orbName );
    public void acceptCalculatedJmxInstrumentorEvent( long eventTimeMillis, JmxInstrumentor total, CalculatedJmxInstrumentor calculated, String clusterName, String orbName );
    public void acceptCalculatedJstatInstrumentorEvent( long eventTimeMillis, JstatInstrumentor total, CalculatedJstatInstrumentor calculated, String clusterName, String orbName );
    public void acceptOutlierInstrumentorEvent( long eventTimeMillis, OutlierInstrumentor total, String clusterName, String orbName );

} // InstrumentorCalculatedEventListener
