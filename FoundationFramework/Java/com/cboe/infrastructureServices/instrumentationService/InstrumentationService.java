package com.cboe.infrastructureServices.instrumentationService;

import java.util.Enumeration;
import java.util.Hashtable;

import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
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
 * The interface for the InstrumentationService. This service is used to gather
 * instrumentation information to aid in gathering performance metrics.
 */
public interface InstrumentationService
{
	/** 
	 * @roseuid 365B8BFD004B
	 */
	public int countInstrumentors() ;
	/**
	 * @roseuid 3658E5EA01A3
	 */
	public boolean deleteInstrumentor(String instrumentorName);
    /**
	 * @roseuid 365B8CFE025D
	 */
	public Enumeration getInstrumentorNames();
	/**
	 * Return an instrumentor if one with the name exists otherwise create one.
	 * @roseuid 3655C63D0098
	 */
	public Instrumentor getInstrumentor(String instrumentorName);
	/**
	 * @roseuid 3655C5B603A1
	 */
	public Hashtable getInstrumentors();
	/**
	 * @roseuid 3656354F0328
	 */
	public boolean initialize(ConfigurationService configService);
	/**
	 * @roseuid 3655C4F000C1
	 */
	public void setInstrumentationPolicy(InstrumentationPolicyValue value);
	/**
	 * Set the name 
	 * @roseuid 3658CDBF039B
	 */
	public void setName(String aName);
	/**
	 * Return the name 
	 * @roseuid 3658CDBF03AF
	 */
	public String getName();
    /**
     * Initialize registrar
     */
    public void initRegistrar();


	public EventChannelInstrumentorFactory getEventChannelInstrumentorFactory();
	public HeapInstrumentorFactory getHeapInstrumentorFactory();
	public NetworkConnectionInstrumentorFactory getNetworkConnectionInstrumentorFactory();
	public QueueInstrumentorFactory getQueueInstrumentorFactory();
	public ThreadPoolInstrumentorFactory getThreadPoolInstrumentorFactory();
	public MethodInstrumentorFactory getMethodInstrumentorFactory();
    public JmxInstrumentorFactory getJmxInstrumentorFactory();
    public JstatInstrumentorFactory getJstatInstrumentorFactory();
    

	public EventChannelInstrumentorFactory getAggregatedEventChannelInstrumentorFactory();
	public NetworkConnectionInstrumentorFactory getAggregatedNetworkConnectionInstrumentorFactory();
	public QueueInstrumentorFactory getAggregatedQueueInstrumentorFactory();
	public ThreadPoolInstrumentorFactory getAggregatedThreadPoolInstrumentorFactory();
	public MethodInstrumentorFactory getAggregatedMethodInstrumentorFactory();
    public JmxInstrumentorFactory getAggregatedJmxInstrumentorFactory();
    public JstatInstrumentorFactory getAggregatedJstatInstrumentorFactory();

	 public TransactionTimerFactory getTransactionTimerFactory();
    
}
