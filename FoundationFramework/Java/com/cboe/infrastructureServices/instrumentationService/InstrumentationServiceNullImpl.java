package com.cboe.infrastructureServices.instrumentationService;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.instrumentationService.InstrumentorHome;
import com.cboe.instrumentationService.factories.EventChannelInstrumentorFactory;
import com.cboe.instrumentationService.factories.HeapInstrumentorFactory;
import com.cboe.instrumentationService.factories.JmxInstrumentorFactory;
import com.cboe.instrumentationService.factories.JstatInstrumentorFactory;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.NetworkConnectionInstrumentorFactory;
import com.cboe.instrumentationService.factories.QueueInstrumentorFactory;
import com.cboe.instrumentationService.factories.ThreadPoolInstrumentorFactory;
import com.cboe.instrumentationService.transactionTimingCommon.TransactionTimerFactory;

/**
 * Use the comm path to provide instrumentation support.
 * For testing purposes.
 */
public class InstrumentationServiceNullImpl extends InstrumentationServiceBaseImpl
{
	public InstrumentationServiceNullImpl()
	{
	}
    public Instrumentor getInstrumentor(String instrumentorName)
    {
        return new LightWeightInstrumentor();
    }
	public boolean initialize(ConfigurationService configService)
	{
		return true;
	}
    public void initRegistrar()
    {
    }

	public EventChannelInstrumentorFactory getEventChannelInstrumentorFactory() {
		return InstrumentorHome.findEventChannelInstrumentorFactory();
	}

	public HeapInstrumentorFactory getHeapInstrumentorFactory() {
		return InstrumentorHome.findHeapInstrumentorFactory();
	}

	public NetworkConnectionInstrumentorFactory getNetworkConnectionInstrumentorFactory() {
		return InstrumentorHome.findNetworkConnectionInstrumentorFactory();
	}

	public QueueInstrumentorFactory getQueueInstrumentorFactory() {
		return InstrumentorHome.findQueueInstrumentorFactory();
	}

	public ThreadPoolInstrumentorFactory getThreadPoolInstrumentorFactory() {
		return InstrumentorHome.findThreadPoolInstrumentorFactory();
	}

	public MethodInstrumentorFactory getMethodInstrumentorFactory() {
		return InstrumentorHome.findMethodInstrumentorFactory();
	}

    public JmxInstrumentorFactory getJmxInstrumentorFactory() {
        return InstrumentorHome.findJmxInstrumentorFactory();
    }

    public JstatInstrumentorFactory getJstatInstrumentorFactory() {
        return InstrumentorHome.findJstatInstrumentorFactory();
    }
    
	public EventChannelInstrumentorFactory getAggregatedEventChannelInstrumentorFactory() {
		return InstrumentorHome.findAggregatedEventChannelInstrumentorFactory();
	}

	public NetworkConnectionInstrumentorFactory getAggregatedNetworkConnectionInstrumentorFactory() {
		return InstrumentorHome.findAggregatedNetworkConnectionInstrumentorFactory();
	}

	public QueueInstrumentorFactory getAggregatedQueueInstrumentorFactory() {
		return InstrumentorHome.findAggregatedQueueInstrumentorFactory();
	}

	public ThreadPoolInstrumentorFactory getAggregatedThreadPoolInstrumentorFactory() {
		return InstrumentorHome.findAggregatedThreadPoolInstrumentorFactory();
	}

	public MethodInstrumentorFactory getAggregatedMethodInstrumentorFactory() {
		return InstrumentorHome.findAggregatedMethodInstrumentorFactory();
	}

    public JmxInstrumentorFactory getAggregatedJmxInstrumentorFactory() {
        return InstrumentorHome.findAggregatedJmxInstrumentorFactory();
    }

    public JstatInstrumentorFactory getAggregatedJstatInstrumentorFactory() {
        return InstrumentorHome.findAggregatedJstatInstrumentorFactory();
    }
	 public TransactionTimerFactory getTransactionTimerFactory()
	 {
		 return TransactionTimerFactoryImpl.getInstance();
	 }

}
