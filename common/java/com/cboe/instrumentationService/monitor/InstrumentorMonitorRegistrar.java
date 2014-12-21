package com.cboe.instrumentationService.monitor;

/**
 * InstrumentorMonitorRegistrar.java
 *
 *
 * Created: Thu Dec 11 14:07:44 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface InstrumentorMonitorRegistrar {

	public void registerCountInstrumentorMonitor( String monitorName,
										 long sampleInterval,
										 String[] matchList ) throws InstrumentorMonitorRegistrationException;
	public void registerEventChannelInstrumentorMonitor( String monitorName,
											   long sampleInterval,
											   String[] matchList ) throws InstrumentorMonitorRegistrationException;
	public void registerHeapInstrumentorMonitor( String monitorName,
										long sampleInterval,
										String[] matchList ) throws InstrumentorMonitorRegistrationException;
	public void registerMethodInstrumentorMonitor( String monitorName,
										  long sampleInterval,
										  String[] matchList ) throws InstrumentorMonitorRegistrationException;
	public void registerNetworkConnectionInstrumentorMonitor( String monitorName,
												   long sampleInterval,
												   String[] matchList ) throws InstrumentorMonitorRegistrationException;
	public void registerQueueInstrumentorMonitor( String monitorName,
										 long sampleInterval,
										 String[] matchList ) throws InstrumentorMonitorRegistrationException;
	public void registerThreadPoolInstrumentorMonitor( String monitorName,
											 long sampleInterval,
											 String[] matchList ) throws InstrumentorMonitorRegistrationException;
    public void registerJmxInstrumentorMonitor( String monitorName,
                                         long sampleInterval,
                                         String[] matchList ) throws InstrumentorMonitorRegistrationException;
    public void registerJstatInstrumentorMonitor( String monitorName,
                                        long sampleInterval,
                                        String[] matchList ) throws InstrumentorMonitorRegistrationException;

	public void unregisterCountInstrumentorMonitor( String monitorName );
	public void unregisterEventChannelInstrumentorMonitor( String monitorName );
	public void unregisterHeapInstrumentorMonitor( String monitorName );
	public void unregisterMethodInstrumentorMonitor( String monitorName );
	public void unregisterNetworkConnectionInstrumentorMonitor( String monitorName );
	public void unregisterQueueInstrumentorMonitor( String monitorName );
	public void unregisterThreadPoolInstrumentorMonitor( String monitorName );
    public void unregisterJmxInstrumentorMonitor( String monitorName );
    public void unregisterJstatInstrumentorMonitor( String monitorName );
    
	public String[] listActiveCountInstrumentorMonitors();
	public String[] listActiveEventChannelInstrumentorMonitors();
	public String[] listActiveHeapInstrumentorMonitors();
	public String[] listActiveMethodInstrumentorMonitors();
	public String[] listActiveNetworkConnectionInstrumentorMonitors();
	public String[] listActiveQueueInstrumentorMonitors();
	public String[] listActiveThreadPoolInstrumentorMonitors();
    public String[] listActiveJmxInstrumentorMonitors();
    public String[] listActiveJstatInstrumentorMonitors();
    
} // InstrumentorMonitorRegistrar
