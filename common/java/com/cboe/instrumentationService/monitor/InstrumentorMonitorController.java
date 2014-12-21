package com.cboe.instrumentationService.monitor;

import java.util.*;
import com.cboe.common.log.Logger;

/**
 * InstrumentorMonitorController.java
 *
 *
 * Created: Thu Sep 11 08:57:11 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class InstrumentorMonitorController {

	private HashMap instMonitorMap = new HashMap();
	private String instrumentorType;

	public InstrumentorMonitorController( String p_instrumentorType ) {
		this.instrumentorType = p_instrumentorType;
	} // InstrumentorMonitorController constructor


	public synchronized void startMonitor( String monitorName, InstrumentorMonitor monitor, long sampleInterval ) throws InstrumentorMonitorAlreadyRegistered {
	    if ( sampleInterval <= 0 ) {
		Logger.sysNotify("InstrumentorMonitorController.startMonitor: SampleInterval(" + sampleInterval + ") for monitor(" + monitorName + ") is invalid." );
		throw new InstrumentorMonitorAlreadyRegistered("SampleInterval(" + sampleInterval + ") for monitor(" + monitorName + ") is invalid.");
	    }

		InstrumentorMonitorRunnable runner = (InstrumentorMonitorRunnable)instMonitorMap.get( monitorName );
		if ( runner != null ) {
			throw new InstrumentorMonitorAlreadyRegistered( monitorName );
		}

		runner = new InstrumentorMonitorRunnable( monitorName, monitor, sampleInterval );
		Thread t = new Thread( runner, "InstrumentorMonitor(" + instrumentorType + ") - " + monitorName );
		instMonitorMap.put( monitorName, runner );
		t.setDaemon( true );
		t.start();
	}

	public synchronized void stopMonitor( String monitorName ) {
		InstrumentorMonitorRunnable runner = (InstrumentorMonitorRunnable)instMonitorMap.remove( monitorName );
		if ( runner != null ) {
			runner.setExitRun( true ); // Tell the thread to stop.
		}
	}

	public synchronized InstrumentorMonitor[] listMonitors() {
		Collection mons = instMonitorMap.values();
		InstrumentorMonitor[] monsArray = null;
		if ( mons != null ) {
			monsArray = new InstrumentorMonitor[mons.size()];
			Iterator iter = mons.iterator();
			for( int i = 0; i < monsArray.length; i++ ) {
				monsArray[i] = ((InstrumentorMonitorRunnable)iter.next()).getMonitor();
			}
		}
		return monsArray;
	}

} // InstrumentorMonitorController
