package com.cboe.presentation.collector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.omg.CORBA.ORB;

import com.cboe.common.log.Logger;
import com.cboe.common.utils.InvalidMatchList;
import com.cboe.common.utils.MatchUtil;
import com.cboe.eventServiceUtilities.EventChannelUtility;
import com.cboe.eventServiceUtilities.FilterUtility;
import com.cboe.idl.instrumentationService.instrumentors.CountInstrumentorStruct;
import com.cboe.idl.instrumentationService.instrumentors.HeapInstrumentorStruct;
import com.cboe.idl.instrumentationService.instrumentors.InstrumentorInfo;
import com.cboe.idl.instrumentationService.instrumentors.InstrumentorOutputHelper;
import com.cboe.idl.instrumentationService.instrumentors.InstrumentorOutputPOA;
import com.cboe.idl.instrumentationService.instrumentors.KeyValueInstrumentorStruct;
import com.cboe.idl.instrumentationService.instrumentors.MethodInstrumentorStruct;
import com.cboe.idl.instrumentationService.instrumentors.NetworkConnectionInstrumentorStruct;
import com.cboe.idl.instrumentationService.instrumentors.QueueInstrumentorStruct;
import com.cboe.idl.instrumentationService.instrumentors.ThreadPoolInstrumentorStruct;
import com.cboe.instrumentationService.InstrumentorHome;
import com.cboe.instrumentationService.InstrumentorTypeValues;
import com.cboe.instrumentationService.calculator.CountInstrumentorCalculatedFactory;
import com.cboe.instrumentationService.calculator.HeapInstrumentorCalculatedFactory;
import com.cboe.instrumentationService.calculator.InstrumentorCalculatedEventBlockListener;
import com.cboe.instrumentationService.calculator.InstrumentorCalculatedEventFinishListener;
import com.cboe.instrumentationService.calculator.InstrumentorCalculatedEventListener;
import com.cboe.instrumentationService.calculator.JmxInstrumentorCalculatedFactory;
import com.cboe.instrumentationService.calculator.JstatInstrumentorCalculatedFactory;
import com.cboe.instrumentationService.calculator.MethodInstrumentorCalculatedFactory;
import com.cboe.instrumentationService.calculator.NetworkConnectionInstrumentorCalculatedFactory;
import com.cboe.instrumentationService.calculator.QueueInstrumentorCalculatedFactory;
import com.cboe.instrumentationService.calculator.ThreadPoolInstrumentorCalculatedFactory;
import com.cboe.instrumentationService.distribution.InstrumentorMonitorECInitException;
import com.cboe.instrumentationService.distribution.InstrumentorMonitorECRegister;
import com.cboe.instrumentationService.distribution.InstrumentorMonitorECRegistrar;
import com.cboe.instrumentationService.factories.CountInstrumentorFactory;
import com.cboe.instrumentationService.factories.CountInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.EventChannelInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.HeapInstrumentorFactory;
import com.cboe.instrumentationService.factories.HeapInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.InstrumentorFactory;
import com.cboe.instrumentationService.factories.JmxInstrumentorFactory;
import com.cboe.instrumentationService.factories.JmxInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.JstatInstrumentorFactory;
import com.cboe.instrumentationService.factories.JstatInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.NetworkConnectionInstrumentorFactory;
import com.cboe.instrumentationService.factories.NetworkConnectionInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.QueueInstrumentorFactory;
import com.cboe.instrumentationService.factories.QueueInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.ThreadPoolInstrumentorFactory;
import com.cboe.instrumentationService.factories.ThreadPoolInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.instrumentors.CalculatedCountInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedHeapInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedJmxInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedJstatInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedMethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedNetworkConnectionInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedQueueInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedThreadPoolInstrumentor;
import com.cboe.instrumentationService.instrumentors.CountInstrumentor;
import com.cboe.instrumentationService.instrumentors.EventChannelInstrumentor;
import com.cboe.instrumentationService.instrumentors.HeapInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.instrumentors.JmxInstrumentor;
import com.cboe.instrumentationService.instrumentors.JstatInstrumentor;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.NetworkConnectionInstrumentor;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import com.cboe.instrumentationService.instrumentors.ThreadPoolInstrumentor;
import com.cboe.instrumentationService.monitor.InstrumentorMonitorDefaults;

/**
 * This class represents a consumer-side API for receiving, calculating and
 * reporting instrumentation information.  A listener object is called
 * for various calculation events.  The listener object is called on an
 * interval defined by a SampleInterval and a CalcToSampleFactor.  The
 * SampleInterval defines how often processes publish raw instrumentors.
 * So, the listener gets called every "CalcToSampleFactor" SampleIntervals.
 * If SampleInterval is 10 seconds (10000 millis), and the CalcToSampleFactor
 * is 3, then the listener will get called with calculated information every
 * 30 seconds.  The CalcToSampleFactor can be adjusted dynamically, but
 * the SampleInterval cannot.  Every call to the listener includes the
 * current so-called raw instrumentor, and the calculated instrumentor
 * as well as the timestamp of the interval.
 *
 *
 * Created: Fri Sep 19 10:48:41 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class InstrumentorECCollector {

	private static final short CALC_TO_SAMPLE_FACTOR_DEFAULT = 6;

	private String channelName;
	private short calcToSampleFactor;
	private String monitorName;
	private long sampleInterval;
	private ORB orb;
	private EventChannelUtility ecUtil;
	private InstrumentorMonitorECRegister register;
	private InstrumentorConsumer cons;
	private InstrumentorCalculatedEventListener listener = null;
	private InstrumentorCalculatedEventBlockListener blockListener = null;
	private InstrumentorCalculatedEventFinishListener finishListener = null;
	private boolean doCalcs = true;
	private boolean usingDefaultMonitors = false;
	private MatchUtil ciMatchList = null;
	private MatchUtil hiMatchList = null;
	private MatchUtil miMatchList = null;
	private MatchUtil nciMatchList = null;
	private MatchUtil qiMatchList = null;
	private MatchUtil tpiMatchList = null;
    private MatchUtil kviMatchList;
    private HashMap ciOrbFilters = new HashMap();
	private HashMap hiOrbFilters = new HashMap();
	private HashMap miOrbFilters = new HashMap();
	private HashMap nciOrbFilters = new HashMap();
	private HashMap qiOrbFilters = new HashMap();
	private HashMap tpiOrbFilters = new HashMap();
    private HashMap kviOrbFilters = new HashMap();
    
    private HashMap <String,Short> invalidJSTATKeyLogTracker = new HashMap <String,Short>();
    private HashMap <String,Short> invalidJMXKeyLogTracker = new HashMap   <String,Short>();
    private HashMap <String,Short> invalidKeyValueLogTracker = new HashMap <String,Short>();
    
    private FilterUtility ciAllEventsFilter = null;
	private FilterUtility hiAllEventsFilter = null;
	private FilterUtility miAllEventsFilter = null;
	private FilterUtility nciAllEventsFilter = null;
	private FilterUtility qiAllEventsFilter = null;
	private FilterUtility tpiAllEventsFilter = null;
    private FilterUtility kviAllEventsFilter;

    /**
	 * This constructor is used to watch the DefaultMonitor for each type
	 * of instrumentor.  This also uses a default CalcToSampleFactor.  However,
	 * this value can be subsequently overridden.  It uses the configured default channel name.
	 *
	 * @param listener an <code>InstrumentorCalculatedEventListener</code> value
	 * @exception InstrumentorMonitorECInitException if an error occurs
	 */
	public InstrumentorECCollector( InstrumentorCalculatedEventListener listener ) throws InstrumentorMonitorECInitException {
		this( System.getProperty( InstrumentorMonitorECRegistrar.INSTRUMENTATION_CHANNEL_NAME_PROP, InstrumentorMonitorECRegistrar.INSTRUMENTATION_CHANNEL_NAME_DEFAULT ), InstrumentorMonitorDefaults.MONITOR_NAME, false, InstrumentorMonitorDefaults.SAMPLE_INTERVAL, CALC_TO_SAMPLE_FACTOR_DEFAULT, listener );
		usingDefaultMonitors = true;
	} // InstrumentorECCollector constructor

	/**
	 * This constructor sets up a monitor for the given MonitorName, SampleInterval and
	 * CalcToSampleFactor.  It uses the configured default channel name.
	 *
	 * @param monitorName a <code>String</code> value
	 * @param directedRegistration a <code>boolean</code> value
	 * @param sampleInterval a <code>long</code> value
	 * @param calcToSampleFactor a <code>short</code> value
	 * @param listener an <code>InstrumentorCalculatedEventListener</code> value
	 * @exception InstrumentorMonitorECInitException if an error occurs
	 */
	public InstrumentorECCollector( String monitorName, boolean directedRegistration, long sampleInterval, short calcToSampleFactor, InstrumentorCalculatedEventListener listener ) throws InstrumentorMonitorECInitException {
		this( System.getProperty( InstrumentorMonitorECRegistrar.INSTRUMENTATION_CHANNEL_NAME_PROP, InstrumentorMonitorECRegistrar.INSTRUMENTATION_CHANNEL_NAME_DEFAULT ), monitorName, directedRegistration, sampleInterval, calcToSampleFactor, listener );
	}

	/**
	 * This constructor is used to watch the DefaultMonitor for each type
	 * of instrumentor.  This also uses a default CalcToSampleFactor.  However,
	 * this value can be subsequently overridden.  It uses the configured default channel name.
	 *
	 * @param blockListener an <code>InstrumentorCalculatedEventBlockListener</code> value
	 * @exception InstrumentorMonitorECInitException if an error occurs
	 */
	public InstrumentorECCollector( InstrumentorCalculatedEventBlockListener blockListener ) throws InstrumentorMonitorECInitException {
		this( System.getProperty( InstrumentorMonitorECRegistrar.INSTRUMENTATION_CHANNEL_NAME_PROP, InstrumentorMonitorECRegistrar.INSTRUMENTATION_CHANNEL_NAME_DEFAULT ), InstrumentorMonitorDefaults.MONITOR_NAME, false, InstrumentorMonitorDefaults.SAMPLE_INTERVAL, CALC_TO_SAMPLE_FACTOR_DEFAULT, blockListener );
		usingDefaultMonitors = true;
	} // InstrumentorECCollector constructor

	/**
	 * This constructor sets up a monitor for the given MonitorName, SampleInterval and
	 * CalcToSampleFactor.  It uses the configured default channel name.
	 *
	 * @param monitorName a <code>String</code> value
	 * @param directedRegistration a <code>boolean</code> value
	 * @param sampleInterval a <code>long</code> value
	 * @param calcToSampleFactor a <code>short</code> value
	 * @param blockListener an <code>InstrumentorCalculatedEventBlockListener</code> value
	 * @exception InstrumentorMonitorECInitException if an error occurs
	 */
	public InstrumentorECCollector( String monitorName, boolean directedRegistration, long sampleInterval, short calcToSampleFactor, InstrumentorCalculatedEventBlockListener blockListener ) throws InstrumentorMonitorECInitException {
		this( System.getProperty( InstrumentorMonitorECRegistrar.INSTRUMENTATION_CHANNEL_NAME_PROP, InstrumentorMonitorECRegistrar.INSTRUMENTATION_CHANNEL_NAME_DEFAULT ), monitorName, directedRegistration, sampleInterval, calcToSampleFactor, blockListener );
	}

	/**
	 * This constructor sets up a monitor for the given MonitorName, SampleInterval and
	 * CalcToSampleFactor.  It uses the given channel name.
	 *
	 * @param channelName a <code>String</code> value
	 * @param monitorName a <code>String</code> value
	 * @param directedRegistration a <code>boolean</code> value
	 * @param sampleInterval a <code>long</code> value
	 * @param calcToSampleFactor a <code>short</code> value
	 * @param listener an <code>InstrumentorCalculatedEventListener</code> value
	 * @exception InstrumentorMonitorECInitException if an error occurs
	 */
	public InstrumentorECCollector( String channelName, String monitorName, boolean directedRegistration, long sampleInterval, short calcToSampleFactor, InstrumentorCalculatedEventListener listener ) throws InstrumentorMonitorECInitException {
		this( channelName, monitorName, directedRegistration, sampleInterval, calcToSampleFactor,
			 listener, null );
	}

	/**
	 * This constructor sets up a monitor for the given MonitorName, SampleInterval and
	 * CalcToSampleFactor.  It uses the given channel name.
	 *
	 * @param channelName a <code>String</code> value
	 * @param monitorName a <code>String</code> value
	 * @param directedRegistration a <code>boolean</code> value
	 * @param sampleInterval a <code>long</code> value
	 * @param calcToSampleFactor a <code>short</code> value
	 * @param blockListener an <code>InstrumentorCalculatedEventBlockListener</code> value
	 * @exception InstrumentorMonitorECInitException if an error occurs
	 */
	public InstrumentorECCollector( String channelName, String monitorName, boolean directedRegistration, long sampleInterval, short calcToSampleFactor, InstrumentorCalculatedEventBlockListener blockListener ) throws InstrumentorMonitorECInitException {
		this( channelName, monitorName, directedRegistration, sampleInterval, calcToSampleFactor,
			 null, blockListener );
	}

	/**
	 * This constructor sets up a monitor for the given MonitorName, SampleInterval and
	 * CalcToSampleFactor.  It uses the given channel name.  One or the other of
	 * listener and blockListener will be null.  A given instance of this class
	 * will only do one style of listener callback.
	 *
	 * @param channelName a <code>String</code> value
	 * @param monitorName a <code>String</code> value
	 * @param directedRegistration a <code>boolean</code> value
	 * @param sampleInterval a <code>long</code> value
	 * @param calcToSampleFactor a <code>short</code> value
	 * @param listener an <code>InstrumentorCalculatedEventListener</code> value
	 * @param blockListener an <code>InstrumentorCalculatedEventBlockListener</code> value
	 * @exception InstrumentorMonitorECInitException if an error occurs
	 */
	private InstrumentorECCollector( String channelName, String monitorName, boolean directedRegistration, long sampleInterval, short calcToSampleFactor, InstrumentorCalculatedEventListener listener, InstrumentorCalculatedEventBlockListener blockListener ) throws InstrumentorMonitorECInitException {
		this( channelName, monitorName, directedRegistration, sampleInterval, calcToSampleFactor,
			  listener, blockListener, true );
	}

	/**
	 * This constructor sets up a monitor for the given MonitorName, SampleInterval and
	 * CalcToSampleFactor.  It uses the given channel name.  One or the other of
	 * listener and blockListener will be null.  A given instance of this class
	 * will only do one style of listener callback.
	 *
	 * @param channelName a <code>String</code> value
	 * @param monitorName a <code>String</code> value
	 * @param directedRegistration a <code>boolean</code> value
	 * @param sampleInterval a <code>long</code> value
	 * @param calcToSampleFactor a <code>short</code> value
	 * @param listener an <code>InstrumentorCalculatedEventListener</code> value
	 * @param blockListener an <code>InstrumentorCalculatedEventBlockListener</code> value
	 * @param doCalculations an <code>boolean</code> value
	 * @exception InstrumentorMonitorECInitException if an error occurs
	 */
	private InstrumentorECCollector( String channelName, String monitorName, boolean directedRegistration, long sampleInterval, short calcToSampleFactor, InstrumentorCalculatedEventListener listener, InstrumentorCalculatedEventBlockListener blockListener, boolean doCalculations ) throws InstrumentorMonitorECInitException {
		this.channelName = channelName;
		this.monitorName = monitorName;
		if ( directedRegistration ) {
			this.monitorName = InstrumentorMonitorECRegistrar.DIRECTED_MONITOR_NAME_PREFIX +
				this.monitorName;
		}
		this.sampleInterval = sampleInterval;
		this.calcToSampleFactor = calcToSampleFactor;
		this.listener = listener;
		this.blockListener = blockListener;
		this.doCalcs = doCalculations;
		orb = com.cboe.ORBInfra.ORB.Orb.init();
		ecUtil = new EventChannelUtility( orb );

		register = new InstrumentorMonitorECRegister( channelName );
		cons = new InstrumentorConsumer();
		startConsumer();
		try {
			ecUtil.startEventService();
		} catch( Exception e ) {
			throw new InstrumentorMonitorECInitException( "InstrumentorMonitorECRegister: unable to initialize EventChannelUtility. " + e );
		}
	}

	public void setDoCalcs( boolean newValue ) {
		doCalcs = newValue;
	}

	public void setEventFinishListener( InstrumentorCalculatedEventFinishListener pFinishListener ) {
		finishListener = pFinishListener;
	}

	/**
	 * Returns the SampleInterval.
	 *
	 * @return a <code>long</code> value
	 */
	public long getSampleInterval() {
		return sampleInterval;
	}

	/**
	 * Returns the current value for CalcToSampleFactor.
	 *
	 * @return a <code>short</code> value
	 */
	public synchronized short getCalcToSampleFactor() {
		return calcToSampleFactor;
	}

	/**
	 * Sets the CalcToSampleFactor.  This drives how often
	 * the listener gets called with calculated information.
	 *
	 * @param newValue a <code>short</code> value
	 */
	public synchronized void setCalcToSampleFactor( short newValue ) {
		calcToSampleFactor = newValue;
	}

	/**
	 * A registration request is sent out for the monitor name associated
	 * with this collector.  If using the DefaultMonitor, then the
	 * matchList is applied locally.  The DefaultMonitor does not allow
	 * a matchList setting.
	 *
	 * @param matchList a <code>String[]</code> value
	 * @exception InvalidMatchList if an error occurs
	 */
	public void registerCountInstrumentorMonitor( String[] matchList ) throws InvalidMatchList {
		ArrayList filters = new ArrayList();
		ciAllEventsFilter = new FilterUtility( "monitorCountInstrumentor", "$.monitorCountInstrumentor.monitorName == '" + monitorName + "'", InstrumentorOutputHelper.id() );
		filters.add( ciAllEventsFilter );
		try {
			ecUtil.applyFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
		} catch( Exception e ) {
			Logger.sysAlarm( "InstrumentorECCollector.regDefCIMon: unable to apply monitorName filter." );
		}

		register.registerCountInstrumentorMonitor( monitorName, sampleInterval, usingDefaultMonitors ? null : matchList );
		setCountInstrumentorMatchList( matchList );
	}

	/**
	 * This method is for use with the DefaultMonitor, and will set filters based
	 * upon an OrbName.  Thus, only events with the DefaultMonitor and OrbName
	 * will pass through.
	 *
	 * @param orbName a <code>String</code> value
	 */
	public synchronized void filterOrbCountInstrumentor( String orbName ) {
		FilterUtility fu = (FilterUtility)ciOrbFilters.get( orbName );
		if ( fu == null ) {
			fu = new FilterUtility( "monitorCountInstrumentor", "$.monitorCountInstrumentor.orbName == '" + orbName + "'", InstrumentorOutputHelper.id() );
			try {
				ArrayList filters = new ArrayList();
				filters.add( fu );
				ecUtil.applyFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
				ciOrbFilters.put( orbName, fu );
			} catch( Exception e ) {
				Logger.sysAlarm( "InstrumentorECCollector.filterOrbCI: unable to apply orbName(" + orbName + ") filter.", e );
			}
		} else {
			return; // Already there.
		}
	}

	/**
	 * A registration request is sent out for the monitor name associated
	 * with this collector.  If using the DefaultMonitor, then the
	 * matchList is applied locally.  The DefaultMonitor does not allow
	 * a matchList setting.
	 *
	 * @deprecated
	 * @param matchList a <code>String[]</code> value
	 * @exception InvalidMatchList if an error occurs
	 */
	public void registerEventChannelInstrumentorMonitor( String[] matchList ) throws InvalidMatchList {
	}

	/**
	 * This method is for use with the DefaultMonitor, and will set filters based
	 * upon an OrbName.  Thus, only events with the DefaultMonitor and OrbName
	 * will pass through.
	 *
	 * @deprecated
	 * @param orbName a <code>String</code> value
	 */
	public synchronized void filterOrbEventChannelInstrumentor( String orbName ) {
	}

	/**
	 * A registration request is sent out for the monitor name associated
	 * with this collector.  If using the DefaultMonitor, then the
	 * matchList is applied locally.  The DefaultMonitor does not allow
	 * a matchList setting.
	 *
	 * @param matchList a <code>String[]</code> value
	 * @exception InvalidMatchList if an error occurs
	 */
	public void registerHeapInstrumentorMonitor( String[] matchList ) throws InvalidMatchList {
		ArrayList filters = new ArrayList();
		hiAllEventsFilter = new FilterUtility( "monitorHeapInstrumentor", "$.monitorHeapInstrumentor.monitorName == '" + monitorName + "'", InstrumentorOutputHelper.id() );
		filters.add( hiAllEventsFilter );
		try {
			ecUtil.applyFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
		} catch( Exception e ) {
			Logger.sysAlarm( "InstrumentorECCollector.regDefHIMon: unable to apply monitorName filter." );
		}

		register.registerHeapInstrumentorMonitor( monitorName, sampleInterval, usingDefaultMonitors ? null : matchList );
		setHeapInstrumentorMatchList( matchList );
	}

	/**
	 * This method is for use with the DefaultMonitor, and will set filters based
	 * upon an OrbName.  Thus, only events with the DefaultMonitor and OrbName
	 * will pass through.
	 *
	 * @param orbName a <code>String</code> value
	 */
	public synchronized void filterOrbHeapInstrumentor( String orbName ) {
		FilterUtility fu = (FilterUtility)hiOrbFilters.get( orbName );
		if ( fu == null ) {
			fu = new FilterUtility( "monitorHeapInstrumentor", "$.monitorHeapInstrumentor.orbName == '" + orbName + "'", InstrumentorOutputHelper.id() );
			try {
				ArrayList filters = new ArrayList();
				filters.add( fu );
				ecUtil.applyFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
				hiOrbFilters.put( orbName, fu );
			} catch( Exception e ) {
				Logger.sysAlarm( "InstrumentorECCollector.filterOrbHI: unable to apply orbName(" + orbName + ") filter.", e );
			}
		} else {
			return; // Already there.
		}
	}

	/**
	 * A registration request is sent out for the monitor name associated
	 * with this collector.  If using the DefaultMonitor, then the
	 * matchList is applied locally.  The DefaultMonitor does not allow
	 * a matchList setting.
	 *
	 * @param matchList a <code>String[]</code> value
	 * @exception InvalidMatchList if an error occurs
	 */
	public void registerMethodInstrumentorMonitor( String[] matchList ) throws InvalidMatchList {
		ArrayList filters = new ArrayList();
		miAllEventsFilter = new FilterUtility( "monitorMethodInstrumentor", "$.monitorMethodInstrumentor.monitorName == '" + monitorName + "'", InstrumentorOutputHelper.id() );
		filters.add( miAllEventsFilter );
		try {
			ecUtil.applyFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
		} catch( Exception e ) {
			Logger.sysAlarm( "InstrumentorECCollector.regDefMIMon: unable to apply monitorName filter." );
		}

		register.registerMethodInstrumentorMonitor( monitorName, sampleInterval, usingDefaultMonitors ? null : matchList );
		setMethodInstrumentorMatchList( matchList );
	}

	/**
	 * This method is for use with the DefaultMonitor, and will set filters based
	 * upon an OrbName.  Thus, only events with the DefaultMonitor and OrbName
	 * will pass through.
	 *
	 * @param orbName a <code>String</code> value
	 */
	public synchronized void filterOrbMethodInstrumentor( String orbName ) {
		FilterUtility fu = (FilterUtility)miOrbFilters.get( orbName );
		if ( fu == null ) {
			fu = new FilterUtility( "monitorMethodInstrumentor", "$.monitorMethodInstrumentor.orbName == '" + orbName + "'", InstrumentorOutputHelper.id() );
			try {
				ArrayList filters = new ArrayList();
				filters.add( fu );
				ecUtil.applyFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
				miOrbFilters.put( orbName, fu );
			} catch( Exception e ) {
				Logger.sysAlarm( "InstrumentorECCollector.filterOrbMI: unable to apply orbName(" + orbName + ") filter.", e );
			}
		} else {
			return; // Already there.
		}
	}

	/**
	 * A registration request is sent out for the monitor name associated
	 * with this collector.  If using the DefaultMonitor, then the
	 * matchList is applied locally.  The DefaultMonitor does not allow
	 * a matchList setting.
	 *
	 * @param matchList a <code>String[]</code> value
	 * @exception InvalidMatchList if an error occurs
	 */
	public void registerNetworkConnectionInstrumentorMonitor( String[] matchList ) throws InvalidMatchList {
		ArrayList filters = new ArrayList();
		nciAllEventsFilter = new FilterUtility( "monitorNetworkConnectionInstrumentor", "$.monitorNetworkConnectionInstrumentor.monitorName == '" + monitorName + "'", InstrumentorOutputHelper.id() );
		filters.add( nciAllEventsFilter );
		try {
			ecUtil.applyFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
		} catch( Exception e ) {
			Logger.sysAlarm( "InstrumentorECCollector.regDefNCIMon: unable to apply monitorName filter." );
		}

		register.registerNetworkConnectionInstrumentorMonitor( monitorName, sampleInterval, usingDefaultMonitors ? null : matchList );
		setNetworkConnectionInstrumentorMatchList( matchList );
	}

	/**
	 * This method is for use with the DefaultMonitor, and will set filters based
	 * upon an OrbName.  Thus, only events with the DefaultMonitor and OrbName
	 * will pass through.
	 *
	 * @param orbName a <code>String</code> value
	 */
	public synchronized void filterOrbNetworkConnectionInstrumentor( String orbName ) {
		FilterUtility fu = (FilterUtility)nciOrbFilters.get( orbName );
		if ( fu == null ) {
			fu = new FilterUtility( "monitorNetworkConnectionInstrumentor", "$.monitorNetworkConnectionInstrumentor.orbName == '" + orbName + "'", InstrumentorOutputHelper.id() );
			try {
				ArrayList filters = new ArrayList();
				filters.add( fu );
				ecUtil.applyFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
				nciOrbFilters.put( orbName, fu );
			} catch( Exception e ) {
				Logger.sysAlarm( "InstrumentorECCollector.filterOrbNCI: unable to apply orbName(" + orbName + ") filter.", e );
			}
		} else {
			return; // Already there.
		}
	}

	/**
	 * A registration request is sent out for the monitor name associated
	 * with this collector.  If using the DefaultMonitor, then the
	 * matchList is applied locally.  The DefaultMonitor does not allow
	 * a matchList setting.
	 *
	 * @param matchList a <code>String[]</code> value
	 * @exception InvalidMatchList if an error occurs
	 */
	public void registerQueueInstrumentorMonitor( String[] matchList ) throws InvalidMatchList {
		ArrayList filters = new ArrayList();
		qiAllEventsFilter = new FilterUtility( "monitorQueueInstrumentor", "$.monitorQueueInstrumentor.monitorName == '" + monitorName + "'", InstrumentorOutputHelper.id() );
		filters.add( qiAllEventsFilter );
		try {
			ecUtil.applyFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
		} catch( Exception e ) {
			Logger.sysAlarm( "InstrumentorECCollector.regDefQIMon: unable to apply monitorName filter." );
		}

		register.registerQueueInstrumentorMonitor( monitorName, sampleInterval, usingDefaultMonitors ? null : matchList );
		setQueueInstrumentorMatchList( matchList );
	}

	/**
	 * This method is for use with the DefaultMonitor, and will set filters based
	 * upon an OrbName.  Thus, only events with the DefaultMonitor and OrbName
	 * will pass through.
	 *
	 * @param orbName a <code>String</code> value
	 */
	public synchronized void filterOrbQueueInstrumentor( String orbName ) {
		FilterUtility fu = (FilterUtility)qiOrbFilters.get( orbName );
		if ( fu == null ) {
			fu = new FilterUtility( "monitorQueueInstrumentor", "$.monitorQueueInstrumentor.orbName == '" + orbName + "'", InstrumentorOutputHelper.id() );
			try {
				ArrayList filters = new ArrayList();
				filters.add( fu );
				ecUtil.applyFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
				qiOrbFilters.put( orbName, fu );
			} catch( Exception e ) {
				Logger.sysAlarm( "InstrumentorECCollector.filterOrbQI: unable to apply orbName(" + orbName + ") filter.", e );
			}
		} else {
			return; // Already there.
		}
	}

	/**
	 * A registration request is sent out for the monitor name associated
	 * with this collector.  If using the DefaultMonitor, then the
	 * matchList is applied locally.  The DefaultMonitor does not allow
	 * a matchList setting.
	 *
	 * @param matchList a <code>String[]</code> value
	 * @exception InvalidMatchList if an error occurs
	 */
	public void registerThreadPoolInstrumentorMonitor( String[] matchList ) throws InvalidMatchList {
		ArrayList filters = new ArrayList();
		tpiAllEventsFilter = new FilterUtility( "monitorThreadPoolInstrumentor", "$.monitorThreadPoolInstrumentor.monitorName == '" + monitorName + "'", InstrumentorOutputHelper.id() );
		filters.add( tpiAllEventsFilter );
		try {
			ecUtil.applyFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
		} catch( Exception e ) {
			Logger.sysAlarm( "InstrumentorECCollector.regDefTPIMon: unable to apply monitorName filter." );
		}

		register.registerThreadPoolInstrumentorMonitor( monitorName, sampleInterval, usingDefaultMonitors ? null : matchList );
		setThreadPoolInstrumentorMatchList( matchList );
	}

	/**
	 * This method is for use with the DefaultMonitor, and will set filters based
	 * upon an OrbName.  Thus, only events with the DefaultMonitor and OrbName
	 * will pass through.
	 *
	 * @param orbName a <code>String</code> value
	 */
	public synchronized void filterOrbThreadPoolInstrumentor( String orbName ) {
		FilterUtility fu = (FilterUtility)tpiOrbFilters.get( orbName );
		if ( fu == null ) {
			fu = new FilterUtility( "monitorThreadPoolInstrumentor", "$.monitorThreadPoolInstrumentor.orbName == '" + orbName + "'", InstrumentorOutputHelper.id() );
			try {
				ArrayList filters = new ArrayList();
				filters.add( fu );
				ecUtil.applyFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
				tpiOrbFilters.put( orbName, fu );
			} catch( Exception e ) {
				Logger.sysAlarm( "InstrumentorECCollector.filterOrbTPI: unable to apply orbName(" + orbName + ") filter.", e );
			}
		} else {
			return; // Already there.
		}
	}
    
    /**
     * A registration request is sent out for the monitor name associated
     * with this collector.  If using the DefaultMonitor, then the
     * matchList is applied locally.  The DefaultMonitor does not allow
     * a matchList setting.
     *
     * @param matchList a <code>String[]</code> value
     * @exception InvalidMatchList if an error occurs
     */
    public void registerKeyValueInstrumentorMonitor(String[] matchList) throws InvalidMatchList
    {
        ArrayList filters = new ArrayList();
        kviAllEventsFilter = new FilterUtility("monitorKeyValueInstrumentor",
                                                "$.monitorKeyValueInstrumentor.monitorName == '" +
                                                monitorName + "'", InstrumentorOutputHelper.id());
        filters.add(kviAllEventsFilter);
        try
        {
            ecUtil.applyFilters(channelName, InstrumentorOutputHelper.id(), cons, filters);
        }
        catch(Exception e)
        {
            Logger.sysAlarm("InstrumentorECCollector.registerKeyValueInstrumentorMonitor: unable to apply monitorName filter.");
        }

        register.registerKeyValueInstrumentorMonitor(monitorName, sampleInterval,
                                                     usingDefaultMonitors ? null : matchList);
        setKeyValueInstrumentorMatchList(matchList);
    }

    /**
     * This method is for use with the DefaultMonitor, and will set filters based
     * upon an OrbName.  Thus, only events with the DefaultMonitor and OrbName
     * will pass through.
     *
     * @param orbName a <code>String</code> value
     */
    public synchronized void filterOrbKeyValueInstrumentor(String orbName)
    {
        FilterUtility fu = (FilterUtility) kviOrbFilters.get(orbName);
        if(fu == null)
        {
            fu = new FilterUtility("monitorKeyValueInstrumentor",
                                   "$.monitorKeyValueInstrumentor.orbName == '" + orbName + "'",
                                   InstrumentorOutputHelper.id());
            try
            {
                ArrayList filters = new ArrayList();
                filters.add(fu);
                ecUtil.applyFilters(channelName, InstrumentorOutputHelper.id(), cons, filters);
                kviOrbFilters.put(orbName, fu);
            }
            catch(Exception e)
            {
                Logger.sysAlarm("InstrumentorECCollector.filterOrbKeyValueInstrumentor: unable to apply orbName(" +
                                orbName + ") filter.", e);
            }
        }
        else
        {
            return; // Already there.
        }
    }

	/**
	 * Sends an unregister request for the monitor name associated
	 * with this collector.  If using the DefaultMonitor, no
	 * unregister request is sent.
	 *
	 */
	public void unregisterCountInstrumentorMonitor() {
		if ( !usingDefaultMonitors ) {
			register.unregisterCountInstrumentorMonitor( monitorName );
		}

		try {
			if ( ciAllEventsFilter != null ) {
				ArrayList filters = new ArrayList();
				filters.add( ciAllEventsFilter );
				ecUtil.removeFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
				ciAllEventsFilter = null;
			}
		} catch( Exception e ) {
			Logger.sysAlarm( "InstrumentorECCollector.unregCIMon: unable to remove filter.", e );
		}
	}

	/**
	 * This method is for use with the DefaultMonitor, and will remove filters based
	 * upon an OrbName.
	 *
	 * @param orbName a <code>String</code> value
	 */
	public synchronized void unfilterOrbCountInstrumentor( String orbName ) {
		FilterUtility fu = (FilterUtility)ciOrbFilters.get( orbName );
		if ( fu != null ) {
			try {
				ArrayList filters = new ArrayList();
				filters.add( fu );
				ecUtil.removeFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
				ciOrbFilters.remove( orbName );
			} catch( Exception e ) {
				Logger.sysAlarm( "InstrumentorECCollector.unfilterOrbCI: unable to remove orbName(" + orbName + ") filter.", e );
			}
		}
	}

	/**
	 * Sends an unregister request for the monitor name associated
	 * with this collector.  If using the DefaultMonitor, no
	 * unregister request is sent.
	 *
	 * @deprecated
	 */
	public void unregisterEventChannelInstrumentorMonitor() {
	}

	/**
	 * This method is for use with the DefaultMonitor, and will remove filters based
	 * upon an OrbName.
	 *
	 * @deprecated
	 * @param orbName a <code>String</code> value
	 */
	public synchronized void unfilterOrbEventChannelInstrumentor( String orbName ) {
	}

	/**
	 * Sends an unregister request for the monitor name associated
	 * with this collector.  If using the DefaultMonitor, no
	 * unregister request is sent.
	 *
	 */
	public void unregisterHeapInstrumentorMonitor() {
		if ( !usingDefaultMonitors ) {
			register.unregisterHeapInstrumentorMonitor( monitorName );
		}

		try {
			if ( hiAllEventsFilter != null ) {
				ArrayList filters = new ArrayList();
				filters.add( hiAllEventsFilter );
				ecUtil.removeFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
				hiAllEventsFilter = null;
			}
		} catch( Exception e ) {
			Logger.sysAlarm( "InstrumentorECCollector.unregHIMon: unable to remove filter.", e );
		}
	}

	/**
	 * This method is for use with the DefaultMonitor, and will remove filters based
	 * upon an OrbName.
	 *
	 * @param orbName a <code>String</code> value
	 */
	public synchronized void unfilterOrbHeapInstrumentor( String orbName ) {
		FilterUtility fu = (FilterUtility)hiOrbFilters.get( orbName );
		if ( fu != null ) {
			try {
				ArrayList filters = new ArrayList();
				filters.add( fu );
				ecUtil.removeFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
				hiOrbFilters.remove( orbName );
			} catch( Exception e ) {
				Logger.sysAlarm( "InstrumentorECCollector.unfilterOrbHI: unable to remove orbName(" + orbName + ") filter.", e );
			}
		}
	}

	/**
	 * Sends an unregister request for the monitor name associated
	 * with this collector.  If using the DefaultMonitor, no
	 * unregister request is sent.
	 *
	 */
	public void unregisterMethodInstrumentorMonitor() {
		if ( !usingDefaultMonitors ) {
			register.unregisterMethodInstrumentorMonitor( monitorName );
		}

		try {
			if ( miAllEventsFilter != null ) {
				ArrayList filters = new ArrayList();
				filters.add( miAllEventsFilter );
				ecUtil.removeFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
				miAllEventsFilter = null;
			}
		} catch( Exception e ) {
			Logger.sysAlarm( "InstrumentorECCollector.unregMIMon: unable to remove filter.", e );
		}
	}

	/**
	 * This method is for use with the DefaultMonitor, and will remove filters based
	 * upon an OrbName.
	 *
	 * @param orbName a <code>String</code> value
	 */
	public synchronized void unfilterOrbMethodInstrumentor( String orbName ) {
		FilterUtility fu = (FilterUtility)miOrbFilters.get( orbName );
		if ( fu != null ) {
			try {
				ArrayList filters = new ArrayList();
				filters.add( fu );
				ecUtil.removeFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
				miOrbFilters.remove( orbName );
			} catch( Exception e ) {
				Logger.sysAlarm( "InstrumentorECCollector.unfilterOrbMI: unable to remove orbName(" + orbName + ") filter.", e );
			}
		}
	}

	/**
	 * Sends an unregister request for the monitor name associated
	 * with this collector.  If using the DefaultMonitor, no
	 * unregister request is sent.
	 *
	 */
	public void unregisterNetworkConnectionInstrumentorMonitor() {
		if ( !usingDefaultMonitors ) {
			register.unregisterNetworkConnectionInstrumentorMonitor( monitorName );
		}

		try {
			if ( nciAllEventsFilter != null ) {
				ArrayList filters = new ArrayList();
				filters.add( nciAllEventsFilter );
				ecUtil.removeFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
				nciAllEventsFilter = null;
			}
		} catch( Exception e ) {
			Logger.sysAlarm( "InstrumentorECCollector.unregNCIMon: unable to remove filter.", e );
		}
	}

	/**
	 * This method is for use with the DefaultMonitor, and will remove filters based
	 * upon an OrbName.
	 *
	 * @param orbName a <code>String</code> value
	 */
	public synchronized void unfilterOrbNetworkConnectionInstrumentor( String orbName ) {
		FilterUtility fu = (FilterUtility)nciOrbFilters.get( orbName );
		if ( fu != null ) {
			try {
				ArrayList filters = new ArrayList();
				filters.add( fu );
				ecUtil.removeFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
				nciOrbFilters.remove( orbName );
			} catch( Exception e ) {
				Logger.sysAlarm( "InstrumentorECCollector.unfilterOrbNCI: unable to remove orbName(" + orbName + ") filter.", e );
			}
		}
	}

	/**
	 * Sends an unregister request for the monitor name associated
	 * with this collector.  If using the DefaultMonitor, no
	 * unregister request is sent.
	 *
	 */
	public void unregisterQueueInstrumentorMonitor() {
		if ( !usingDefaultMonitors ) {
			register.unregisterQueueInstrumentorMonitor( monitorName );
		}

		try {
			if ( qiAllEventsFilter != null ) {
				ArrayList filters = new ArrayList();
				filters.add( qiAllEventsFilter );
				ecUtil.removeFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
				qiAllEventsFilter = null;
			}
		} catch( Exception e ) {
			Logger.sysAlarm( "InstrumentorECCollector.unregQIMon: unable to remove filter.", e );
		}
	}

	/**
	 * This method is for use with the DefaultMonitor, and will remove filters based
	 * upon an OrbName.
	 *
	 * @param orbName a <code>String</code> value
	 */
	public synchronized void unfilterOrbQueueInstrumentor( String orbName ) {
		FilterUtility fu = (FilterUtility)qiOrbFilters.get( orbName );
		if ( fu != null ) {
			try {
				ArrayList filters = new ArrayList();
				filters.add( fu );
				ecUtil.removeFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
				qiOrbFilters.remove( orbName );
			} catch( Exception e ) {
				Logger.sysAlarm( "InstrumentorECCollector.unfilterOrbQI: unable to remove orbName(" + orbName + ") filter.", e );
			}
		}
	}

	/**
	 * Sends an unregister request for the monitor name associated
	 * with this collector.  If using the DefaultMonitor, no
	 * unregister request is sent.
	 *
	 */
	public void unregisterThreadPoolInstrumentorMonitor() {
		if ( !usingDefaultMonitors ) {
			register.unregisterThreadPoolInstrumentorMonitor( monitorName );
		}

		try {
			if ( tpiAllEventsFilter != null ) {
				ArrayList filters = new ArrayList();
				filters.add( tpiAllEventsFilter );
				ecUtil.removeFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
				tpiAllEventsFilter = null;
			}
		} catch( Exception e ) {
			Logger.sysAlarm( "InstrumentorECCollector.unregTPIMon: unable to remove filter.", e );
		}
	}

	/**
	 * This method is for use with the DefaultMonitor, and will remove filters based
	 * upon an OrbName.
	 *
	 * @param orbName a <code>String</code> value
	 */
	public synchronized void unfilterOrbThreadPoolInstrumentor( String orbName ) {
		FilterUtility fu = (FilterUtility)tpiOrbFilters.get( orbName );
		if ( fu != null ) {
			try {
				ArrayList filters = new ArrayList();
				filters.add( fu );
				ecUtil.removeFilters( channelName, InstrumentorOutputHelper.id(), cons, filters );
				tpiOrbFilters.remove( orbName );
			} catch( Exception e ) {
				Logger.sysAlarm( "InstrumentorECCollector.unfilterOrbTPI: unable to remove orbName(" + orbName + ") filter.", e );
			}
		}
	}

    /**
     * Sends an unregister request for the monitor name associated
     * with this collector.  If using the DefaultMonitor, no
     * unregister request is sent.
     *
     */
    public void unregisterKeyValueInstrumentorMonitor()
    {
        if(!usingDefaultMonitors)
        {
            register.unregisterKeyValueInstrumentorMonitor(monitorName);
        }

        try
        {
            if(kviAllEventsFilter != null)
            {
                ArrayList filters = new ArrayList();
                filters.add(kviAllEventsFilter);
                ecUtil.removeFilters(channelName, InstrumentorOutputHelper.id(), cons, filters);
                kviAllEventsFilter = null;
            }
        }
        catch(Exception e)
        {
            Logger.sysAlarm("InstrumentorECCollector.unregKVIMon: unable to remove filter.", e);
        }
    }

    /**
     * This method is for use with the DefaultMonitor, and will remove filters based
     * upon an OrbName.
     *
     * @param orbName a <code>String</code> value
     */
    public synchronized void unfilterOrbKeyValueInstrumentor(String orbName)
    {
        FilterUtility fu = (FilterUtility) kviOrbFilters.get(orbName);
        if(fu != null)
        {
            try
            {
                ArrayList filters = new ArrayList();
                filters.add(fu);
                ecUtil.removeFilters(channelName, InstrumentorOutputHelper.id(), cons, filters);
                kviOrbFilters.remove(orbName);
            }
            catch(Exception e)
            {
                Logger.sysAlarm("InstrumentorECCollector.unfilterOrbKVI: unable to remove orbName(" +
                                orbName + ") filter.", e);
            }
        }
    }

	/**
	 * If using DefaultMonitor, sets the local matchList.  This will alter
	 * which instrumentors are reported to the listener.
	 *
	 * @param matchList a <code>String[]</code> value
	 * @exception InvalidMatchList if an error occurs
	 */
	public synchronized void setCountInstrumentorMatchList( String[] matchList ) throws InvalidMatchList {
		if ( usingDefaultMonitors ) {
			ciMatchList = new MatchUtil( matchList );
		}
	}

	/**
	 * If using DefaultMonitor, sets the local matchList.  This will alter
	 * which instrumentors are reported to the listener.
	 *
	 * @deprecated
	 * @param matchList a <code>String[]</code> value
	 * @exception InvalidMatchList if an error occurs
	 */
	public synchronized void setEventChannelInstrumentorMatchList( String[] matchList ) throws InvalidMatchList {
	}

	/**
	 * If using DefaultMonitor, sets the local matchList.  This will alter
	 * which instrumentors are reported to the listener.
	 *
	 * @param matchList a <code>String[]</code> value
	 * @exception InvalidMatchList if an error occurs
	 */
	public synchronized void setHeapInstrumentorMatchList( String[] matchList ) throws InvalidMatchList {
		if ( usingDefaultMonitors ) {
			hiMatchList = new MatchUtil( matchList );
		}
	}

	/**
	 * If using DefaultMonitor, sets the local matchList.  This will alter
	 * which instrumentors are reported to the listener.
	 *
	 * @param matchList a <code>String[]</code> value
	 * @exception InvalidMatchList if an error occurs
	 */
	public synchronized void setMethodInstrumentorMatchList( String[] matchList ) throws InvalidMatchList {
		if ( usingDefaultMonitors ) {
			miMatchList = new MatchUtil( matchList );
		}
	}

	/**
	 * If using DefaultMonitor, sets the local matchList.  This will alter
	 * which instrumentors are reported to the listener.
	 *
	 * @param matchList a <code>String[]</code> value
	 * @exception InvalidMatchList if an error occurs
	 */
	public synchronized void setNetworkConnectionInstrumentorMatchList( String[] matchList ) throws InvalidMatchList {
		if ( usingDefaultMonitors ) {
			nciMatchList = new MatchUtil( matchList );
		}
	}

	/**
	 * If using DefaultMonitor, sets the local matchList.  This will alter
	 * which instrumentors are reported to the listener.
	 *
	 * @param matchList a <code>String[]</code> value
	 * @exception InvalidMatchList if an error occurs
	 */
	public synchronized void setQueueInstrumentorMatchList( String[] matchList ) throws InvalidMatchList {
		if ( usingDefaultMonitors ) {
			qiMatchList = new MatchUtil( matchList );
		}
	}

    /**
     * If using DefaultMonitor, sets the local matchList.  This will alter
     * which instrumentors are reported to the listener.
     *
     * @param matchList a <code>String[]</code> value
     * @exception InvalidMatchList if an error occurs
     */
    public synchronized void setThreadPoolInstrumentorMatchList( String[] matchList ) throws InvalidMatchList {
        if ( usingDefaultMonitors ) {
            tpiMatchList = new MatchUtil( matchList );
        }
    }

    /**
     * If using DefaultMonitor, sets the local matchList.  This will alter
     * which instrumentors are reported to the listener.
     *
     * @param matchList a <code>String[]</code> value
     * @exception InvalidMatchList if an error occurs
     */
    public synchronized void setKeyValueInstrumentorMatchList(String[] matchList) throws InvalidMatchList
    {
        if(usingDefaultMonitors)
        {
            kviMatchList = new MatchUtil(matchList);
        }
    }

	private synchronized boolean matches( String name, MatchUtil matchList ) {
		if ( matchList != null ) {
			return matchList.matches( name );
		} else {
			return true;
		}
	}

	private void startConsumer() throws InstrumentorMonitorECInitException {
		ArrayList filters = new ArrayList();

		// Set up false filters for each method so nothing comes in until
		// something specific is requested.
		FilterUtility filter = null;
		filter = new FilterUtility( "monitorCountInstrumentor", "0", InstrumentorOutputHelper.id() );
		filters.add( filter );
		filter = new FilterUtility( "monitorHeapInstrumentor", "0", InstrumentorOutputHelper.id() );
		filters.add( filter );
		filter = new FilterUtility( "monitorMethodInstrumentor", "0", InstrumentorOutputHelper.id() );
		filters.add( filter );
		filter = new FilterUtility( "monitorNetworkConnectionInstrumentor", "0", InstrumentorOutputHelper.id() );
		filters.add( filter );
		filter = new FilterUtility( "monitorQueueInstrumentor", "0", InstrumentorOutputHelper.id() );
		filters.add( filter );
		filter = new FilterUtility( "monitorThreadPoolInstrumentor", "0", InstrumentorOutputHelper.id() );
		filters.add( filter );
        filter = new FilterUtility( "monitorKeyValueInstrumentor", "0", InstrumentorOutputHelper.id() );
        filters.add( filter );

		try {
			if ( ecUtil.connectConsumer( channelName, InstrumentorOutputHelper.id(), cons, filters ) ) {
				Logger.sysNotify( "InstrumentorECCollector: started Instrumentor consumer with false filters on channel(" + channelName + ")." );
			} else {
				throw new InstrumentorMonitorECInitException( "Unable to start instrumentor consumer on channel(" + channelName + ")." );
			}
		} catch( InstrumentorMonitorECInitException e ) {
			throw e;
		} catch( Exception e ) {
			throw new InstrumentorMonitorECInitException( "Unable to start instrumentor consumer on channel(" + channelName + "). " + e );
		}
	}

	private class InstrumentorConsumer extends InstrumentorOutputPOA {

		ThreadLocal ciTl = new ThreadLocal();
		ThreadLocal hiTl = new ThreadLocal();
		ThreadLocal miTl = new ThreadLocal();
		ThreadLocal nciTl = new ThreadLocal();
		ThreadLocal qiTl = new ThreadLocal();
		ThreadLocal tpiTl = new ThreadLocal();
        ThreadLocal jmxiTl = new ThreadLocal();
        ThreadLocal jstatiTl = new ThreadLocal();
		Map overallHWMMap = Collections.synchronizedMap( new HashMap() );

		public InstrumentorConsumer() {
		}

		/**
		 * There needs to be a calculated factory for each ORB, since the
		 * instrumentor names might not be unique between different processes.
		 *
		 * @param orbName a <code>String</code> value
		 * @return a <code>CountInstrumentorFactory</code> value
		 */
		private synchronized CountInstrumentorFactory getCIFactory( String orbName ) {
			String factoryName = channelName + orbName;
			if ( !doCalcs ) {
				factoryName = Thread.currentThread().toString();
			}
			CountInstrumentorFactory ciF = InstrumentorHome.findCountInstrumentorFactory( factoryName );
			if ( ciF == null ) {
				Logger.sysNotify( "InstrumentorECCollector.getCIFactory: Creating new factory(" + factoryName + ")." );
				ciF = new CountInstrumentorCalculatedFactory();
				InstrumentorHome.registerCountInstrumentorFactory( factoryName, ciF );
			}
			return ciF;
		}

		/**
		 * There needs to be a calculated factory for each ORB, since the
		 * instrumentor names might not be unique between different processes.
		 *
		 * @param orbName a <code>String</code> value
		 * @return a <code>CountInstrumentorFactory</code> value
		 */
		private synchronized HeapInstrumentorFactory getHIFactory( String orbName ) {
			String factoryName = channelName + orbName;
			if ( !doCalcs ) {
				factoryName = Thread.currentThread().toString();
			}
			HeapInstrumentorFactory hiF = InstrumentorHome.findHeapInstrumentorFactory( factoryName );
			if ( hiF == null ) {
				Logger.sysNotify( "InstrumentorECCollector.getHIFactory: Creating new factory(" + factoryName + ")." );
				hiF = new HeapInstrumentorCalculatedFactory();
				InstrumentorHome.registerHeapInstrumentorFactory( factoryName, hiF );
			}
			return hiF;
		}

		/**
		 * There needs to be a calculated factory for each ORB, since the
		 * instrumentor names might not be unique between different processes.
		 *
		 * @param orbName a <code>String</code> value
		 * @return a <code>MethodInstrumentorFactory</code> value
		 */
		private synchronized MethodInstrumentorFactory getMIFactory( String orbName ) {
			String factoryName = channelName + orbName;
			if ( !doCalcs ) {
				factoryName = Thread.currentThread().toString();
			}
			MethodInstrumentorFactory miF = InstrumentorHome.findMethodInstrumentorFactory( factoryName );
			if ( miF == null ) {
				Logger.sysNotify( "InstrumentorECCollector.getMIFactory: Creating new factory(" + factoryName + ")." );
				miF = new MethodInstrumentorCalculatedFactory();
				InstrumentorHome.registerMethodInstrumentorFactory( factoryName, miF );
			}
			return miF;
		}

		/**
		 * There needs to be a calculated factory for each ORB, since the
		 * instrumentor names might not be unique between different processes.
		 *
		 * @param orbName a <code>String</code> value
		 * @return a <code>NetworkConnectionInstrumentorFactory</code> value
		 */
		private synchronized NetworkConnectionInstrumentorFactory getNCIFactory( String orbName ) {
			String factoryName = channelName + orbName;
			if ( !doCalcs ) {
				factoryName = Thread.currentThread().toString();
			}
			NetworkConnectionInstrumentorFactory nciF = InstrumentorHome.findNetworkConnectionInstrumentorFactory( factoryName );
			if ( nciF == null ) {
				Logger.sysNotify( "InstrumentorECCollector.getNCIFactory: Creating new factory(" + factoryName + ")." );
				nciF = new NetworkConnectionInstrumentorCalculatedFactory();
				InstrumentorHome.registerNetworkConnectionInstrumentorFactory( factoryName, nciF );
			}
			return nciF;
		}

		/**
		 * There needs to be a calculated factory for each ORB, since the
		 * instrumentor names might not be unique between different processes.
		 *
		 * @param orbName a <code>String</code> value
		 * @return a <code>QueueInstrumentorFactory</code> value
		 */
		private synchronized QueueInstrumentorFactory getQIFactory( String orbName ) {
			String factoryName = channelName + orbName;
			if ( !doCalcs ) {
				factoryName = Thread.currentThread().toString();
			}
			QueueInstrumentorFactory qiF = InstrumentorHome.findQueueInstrumentorFactory( factoryName );
			if ( qiF == null ) {
				Logger.sysNotify( "InstrumentorECCollector.getQIFactory: Creating new factory(" + factoryName + ")." );
				qiF = new QueueInstrumentorCalculatedFactory();
				InstrumentorHome.registerQueueInstrumentorFactory( factoryName, qiF );
			}
			return qiF;
		}

		/**
		 * There needs to be a calculated factory for each ORB, since the
		 * instrumentor names might not be unique between different processes.
		 *
		 * @param orbName a <code>String</code> value
		 * @return a <code>ThreadPoolInstrumentorFactory</code> value
		 */
		private synchronized ThreadPoolInstrumentorFactory getTPIFactory( String orbName ) {
			String factoryName = channelName + orbName;
			if ( !doCalcs ) {
				factoryName = Thread.currentThread().toString();
			}
			ThreadPoolInstrumentorFactory tpiF = InstrumentorHome.findThreadPoolInstrumentorFactory( factoryName );
			if ( tpiF == null ) {
				Logger.sysNotify( "InstrumentorECCollector.getTPIFactory: Creating new factory(" + factoryName + ")." );
				tpiF = new ThreadPoolInstrumentorCalculatedFactory();
				InstrumentorHome.registerThreadPoolInstrumentorFactory( factoryName, tpiF );
			}
			return tpiF;
		}

        /**
         * There needs to be a calculated factory for each ORB, since the
         * instrumentor names might not be unique between different processes.
         *
         * @param orbName a <code>String</code> value
         * @return a <code>JmxInstrumentorFactory</code> value
         */
        private synchronized JmxInstrumentorFactory getJMXIFactory( String orbName ) {
            String factoryName = channelName + orbName;
            if ( !doCalcs ) {
                factoryName = Thread.currentThread().toString();
            }
            JmxInstrumentorFactory jmxiF = InstrumentorHome.findJmxInstrumentorFactory( factoryName );
            if ( jmxiF == null ) {
                Logger.sysNotify( "InstrumentorECCollector.getKVIFactory: Creating new factory(" + factoryName + ")." );
                jmxiF = new JmxInstrumentorCalculatedFactory();
                InstrumentorHome.registerJmxInstrumentorFactory( factoryName, jmxiF );
            }
            return jmxiF;
        }

        /**
         * There needs to be a calculated factory for each ORB, since the
         * instrumentor names might not be unique between different processes.
         *
         * @param orbName a <code>String</code> value
         * @return a <code>JstatInstrumentorFactory</code> value
         */
        private synchronized JstatInstrumentorFactory getJSTATIFactory( String orbName ) {
            String factoryName = channelName + orbName;
            if ( !doCalcs ) {
                factoryName = Thread.currentThread().toString();
            }
            JstatInstrumentorFactory jstatiF = InstrumentorHome.findJstatInstrumentorFactory( factoryName );
            if ( jstatiF == null ) {
                Logger.sysNotify( "InstrumentorECCollector.getKVIFactory: Creating new factory(" + factoryName + ")." );
                jstatiF = new JstatInstrumentorCalculatedFactory();
                InstrumentorHome.registerJstatInstrumentorFactory( factoryName, jstatiF );
            }
            return jstatiF;
        }

		/**
		 * This method will remove entries from an instrumentor factory
		 * associated with an ORB.  The current collection from the ORB
		 * is checked against the factory for that ORB.  If any instrumentor
		 * objects are in the factory that are not currently reported by
		 * the actual ORB process, then they are removed.  This will prevent
		 * excessive memory use by users of this class, when ORB processes
		 * create and remove "temporary" instrumentors.
		 *
		 * @param orbName a <code>String</code> value
		 * @param factory an <code>InstrumentorFactory</code> value
		 * @param infoSeq an <code>InstrumentorInfo[]</code> value
		 */
		private void cleanupFactory( String orbName, InstrumentorFactory factory, InstrumentorInfo[] infoSeq ) {
			if ( !doCalcs ) return;

			// Create a HashSet for the instrumentor names from infoSeq.
			HashSet currentInstSet = new HashSet();
			for( int i = 0; i < infoSeq.length; i++ ) {
				currentInstSet.add( infoSeq[i].instrumentorName );
			}

			// This call will get the instrumentor map from the factory.
			Map factoryInstMap = new FactoryVisitor().getInstrumentorMap( factory );

			// Iterate through the factory instrumentor map to see if
			// it contains anything NOT in the current set.  For any
			// instrumentor from the factory NOT in the current set,
			// remove it from the factory.
			Iterator factoryIter = factoryInstMap.values().iterator();
			while( factoryIter.hasNext() ) {
				Instrumentor inst = (Instrumentor)factoryIter.next();
				if ( !currentInstSet.contains( inst.getName() ) ) {
					Logger.debug( "InstrumentorECCollector.cleanupFactory: orbName(" + orbName +
							    "), cleanup instrumentor(" + inst.getName() + ")." );
					if ( factory instanceof CountInstrumentorFactory ) {
						((CountInstrumentorFactory)factory).unregister( (CountInstrumentor)inst );
					} else if ( factory instanceof HeapInstrumentorFactory ) {
						((HeapInstrumentorFactory)factory).unregister( (HeapInstrumentor)inst );
					} else if ( factory instanceof MethodInstrumentorFactory ) {
						((MethodInstrumentorFactory)factory).unregister( (MethodInstrumentor)inst );
					} else if ( factory instanceof NetworkConnectionInstrumentorFactory ) {
						((NetworkConnectionInstrumentorFactory)factory).unregister( (NetworkConnectionInstrumentor)inst );
					} else if ( factory instanceof QueueInstrumentorFactory ) {
						((QueueInstrumentorFactory)factory).unregister( (QueueInstrumentor)inst );
					} else if ( factory instanceof ThreadPoolInstrumentorFactory ) {
						((ThreadPoolInstrumentorFactory)factory).unregister( (ThreadPoolInstrumentor)inst );
				    } else if ( factory instanceof JmxInstrumentorFactory ) {
				        ((JmxInstrumentorFactory)factory).unregister( (JmxInstrumentor)inst );
                    } else if ( factory instanceof JstatInstrumentorFactory ) {
                        ((JstatInstrumentorFactory)factory).unregister( (JstatInstrumentor)inst );
				    }
				}
			}
		}

		public void monitorCountInstrumentor( String monitorName, String clusterName, String orbName, InstrumentorInfo[] infoSeq, CountInstrumentorStruct[] cInstrumentorSeq ) {
			ArrayList iList = new ArrayList(); // For use in block calls.
			Logger.debug( "InstrumentorECCollector.monCountInst: monitor(" + monitorName + "), fromOrb(" + orbName + "), size(" + infoSeq.length + ")." );
			CountInstrumentorFactory ciFactory = getCIFactory( orbName );
			cleanupFactory( orbName, ciFactory, infoSeq );
			CountInstrumentor ci = null;
			if ( !doCalcs ) {
				ci = (CountInstrumentor)ciTl.get();
				if ( ci == null ) {
					ci = ciFactory.getInstance( "Dummy", null );
					ciTl.set( ci );
				}
			}
			for( int i = 0; i < infoSeq.length; i++ ) {
				String instName = infoSeq[i].instrumentorName;
				if ( doCalcs ) {
					ci = ciFactory.getInstance( instName, infoSeq[i].userData );
				} else {
					ci.rename( instName );
				}
				ci.setKey( infoSeq[i].key );
				ci.setCount( cInstrumentorSeq[i].count );
				ci.setUserData( infoSeq[i].userData );
				((CalculatedInstrumentor)ci).sumIntervalTime( infoSeq[i].timestamp );
				long numCISamples = ((CalculatedInstrumentor)ci).incSamples();
				if ( numCISamples % calcToSampleFactor == 0 ) {
					if ( doCalcs ) {
						((CalculatedInstrumentor)ci).calculate( calcToSampleFactor );
					}
					if ( matches( instName, ciMatchList ) ) {
						if ( listener != null ) {
							Logger.debug( "InstrumentorECCollector.monCountInst: calling calc-listener, monitor(" + monitorName + "), fromOrb(" + orbName + "), ci(" + ci.toString() + ")." );
							listener.acceptCalculatedCountInstrumentorEvent( infoSeq[i].timestamp,
																    ci,
																    (CalculatedCountInstrumentor)ci,
																    clusterName,
																    orbName );
						} else {
							// For use later in block call.
							iList.add( ci );
						}
					}
				}
			}
			if ( finishListener != null ) {
				finishListener.finishEvent();
			}
			if ( iList.size() != 0 && blockListener != null ) {
				Logger.debug( "InstrumentorECCollector.monCountInst: calling calc-blocklistener, monitor(" + monitorName + "), fromOrb(" + orbName + ")." );
				// We are making block call.  Assume timestamp comes from first instrumentor.
				blockListener.acceptCalculatedCountInstrumentorsEvent( infoSeq[0].timestamp,
															iList,
															iList,
															clusterName,
															orbName );
			}
		}

		public void monitorEventChannelInstrumentor( String monitorName, String clusterName, String orbName, InstrumentorInfo[] infoSeq, QueueInstrumentorStruct[] ecInstrumentorSeq ) {
		}

		public void monitorHeapInstrumentor( String monitorName, String clusterName, String orbName, InstrumentorInfo[] infoSeq, HeapInstrumentorStruct[] hInstrumentorSeq ) {
			ArrayList iList = new ArrayList(); // For use in block calls.
			HeapInstrumentorFactory hiFactory = getHIFactory( orbName );
			cleanupFactory( orbName, hiFactory, infoSeq );
			HeapInstrumentor hi = null;
			if ( !doCalcs ) {
				hi = (HeapInstrumentor)hiTl.get();
				if ( hi == null ) {
					hi = hiFactory.getInstance( "Dummy", null );
					hiTl.set( hi );
				}
			}
			for( int i = 0; i < infoSeq.length; i++ ) {
				String instName = infoSeq[i].instrumentorName;
				if ( doCalcs ) {
					hi = hiFactory.getInstance( instName, infoSeq[i].userData );
				} else {
					hi.rename( instName );
				}
				hi.setKey( infoSeq[i].key );
				hi.setMaxMemory( hInstrumentorSeq[i].maxMemory );
				hi.setTotalMemory( hInstrumentorSeq[i].totalMemory );
				hi.setFreeMemory( hInstrumentorSeq[i].freeMemory );
				hi.setUserData( infoSeq[i].userData );
				((CalculatedInstrumentor)hi).sumIntervalTime( infoSeq[i].timestamp );
				long numHISamples = ((CalculatedInstrumentor)hi).incSamples();
				if ( numHISamples % calcToSampleFactor == 0 ) {
					if ( doCalcs ) {
						((CalculatedInstrumentor)hi).calculate( calcToSampleFactor );
					}
					if ( matches( instName, hiMatchList ) ) {
						if ( listener != null ) {
							listener.acceptCalculatedHeapInstrumentorEvent( infoSeq[i].timestamp,
																   hi,
																   (CalculatedHeapInstrumentor)hi,
																   clusterName,
																   orbName );
						} else {
							// For use later in block call.
							iList.add( hi );
						}
					}
				}
			}
			if ( finishListener != null ) {
				finishListener.finishEvent();
			}
			if ( iList.size() != 0 && blockListener != null ) {
				Logger.debug( "InstrumentorECCollector.monHeapInst: calling calc-blocklistener, monitor(" + monitorName + "), fromOrb(" + orbName + ")." );
				// We are making block call.  Assume timestamp comes from first instrumentor.
				blockListener.acceptCalculatedHeapInstrumentorsEvent( infoSeq[0].timestamp,
														    iList,
														    iList,
														    clusterName,
														    orbName );
			}
		}

		public void monitorMethodInstrumentor( String monitorName, String clusterName, String orbName, InstrumentorInfo[] infoSeq, MethodInstrumentorStruct[] mInstrumentorSeq ) {
			ArrayList iList = new ArrayList(); // For use in block calls.
			MethodInstrumentorFactory miFactory = getMIFactory( orbName );
			cleanupFactory( orbName, miFactory, infoSeq );
			MethodInstrumentor mi = null;
			if ( !doCalcs ) {
				mi = (MethodInstrumentor)miTl.get();
				if ( mi == null ) {
					mi = miFactory.getInstance( "Dummy", null );
					miTl.set( mi );
				}
			}
			for( int i = 0; i < infoSeq.length; i++ ) {
				String instName = infoSeq[i].instrumentorName;
				if ( doCalcs ) {
					mi = miFactory.getInstance( instName, infoSeq[i].userData );
				} else {
					mi.rename( instName );
				}
				mi.setKey( infoSeq[i].key );
				mi.setCalls( mInstrumentorSeq[i].calls );
				mi.setExceptions( mInstrumentorSeq[i].exceptions );
				mi.setMethodTime( mInstrumentorSeq[i].methodTime );
				mi.setSumOfSquareMethodTime( mInstrumentorSeq[i].sumOfSquareMethodTime );
				mi.setMaxMethodTime( mInstrumentorSeq[i].maxMethodTime );
				mi.setUserData( infoSeq[i].userData );
				((CalculatedInstrumentor)mi).sumIntervalTime( infoSeq[i].timestamp );
				long numMISamples = ((CalculatedInstrumentor)mi).incSamples();
				if ( numMISamples % calcToSampleFactor == 0 ) {
					if ( doCalcs ) {
						((CalculatedInstrumentor)mi).calculate( calcToSampleFactor );
					}
					if ( matches( instName, miMatchList ) ) {
						if ( listener != null ) {
							listener.acceptCalculatedMethodInstrumentorEvent( infoSeq[i].timestamp,
																	mi,
																	(CalculatedMethodInstrumentor)mi,
																	clusterName,
																	orbName );
						} else {
							// For use later in block call.
							iList.add( mi );
						}
					}
				}
			}
			if ( finishListener != null ) {
				finishListener.finishEvent();
			}
			if ( iList.size() != 0 && blockListener != null ) {
				Logger.debug( "InstrumentorECCollector.monMethodInst: calling calc-blocklistener, monitor(" + monitorName + "), fromOrb(" + orbName + ")." );
				// We are making block call.  Assume timestamp comes from first instrumentor.
				blockListener.acceptCalculatedMethodInstrumentorsEvent( infoSeq[0].timestamp,
															 iList,
															 iList,
															 clusterName,
															 orbName );
			}
		}

		public void monitorNetworkConnectionInstrumentor( String monitorName, String clusterName, String orbName, InstrumentorInfo[] infoSeq, NetworkConnectionInstrumentorStruct[] ncInstrumentorSeq ) {
			ArrayList iList = new ArrayList(); // For use in block calls.
			NetworkConnectionInstrumentorFactory nciFactory = getNCIFactory( orbName );
			cleanupFactory( orbName, nciFactory, infoSeq );
			NetworkConnectionInstrumentor nci = null;
			if ( !doCalcs ) {
				nci = (NetworkConnectionInstrumentor)nciTl.get();
				if ( nci == null ) {
					nci = nciFactory.getInstance( "Dummy", null );
					nciTl.set( nci );
				}
			}
			for( int i = 0; i < infoSeq.length; i++ ) {
				String instName = infoSeq[i].instrumentorName;
				if ( doCalcs ) {
					nci = nciFactory.getInstance( instName, infoSeq[i].userData );
				} else {
					nci.rename( instName );
				}
				nci.setKey( infoSeq[i].key );
				nci.setBytesSent( ncInstrumentorSeq[i].bytesSent );
				nci.setBytesReceived( ncInstrumentorSeq[i].bytesReceived );
				nci.setMsgsSent( ncInstrumentorSeq[i].msgsSent );
				nci.setMsgsReceived( ncInstrumentorSeq[i].msgsReceived );
				nci.setPacketsSent( ncInstrumentorSeq[i].packetsSent );
				nci.setPacketsReceived( ncInstrumentorSeq[i].packetsReceived );
				nci.setInvalidPacketsReceived( ncInstrumentorSeq[i].invalidPacketsReceived );
				nci.setGarbageBytesReceived( ncInstrumentorSeq[i].garbageBytesReceived );
				nci.setConnects( ncInstrumentorSeq[i].connects );
				nci.setDisconnects( ncInstrumentorSeq[i].disconnects );
				nci.setExceptions( ncInstrumentorSeq[i].exceptions );
				nci.setLastTimeSent( ncInstrumentorSeq[i].lastTimeSent );
				nci.setLastTimeReceived( ncInstrumentorSeq[i].lastTimeReceived );
				nci.setLastConnectTime( ncInstrumentorSeq[i].lastConnectTime );
				nci.setLastDisconnectTime( ncInstrumentorSeq[i].lastDisconnectTime );
				nci.setLastExceptionTime( ncInstrumentorSeq[i].lastExceptionTime );
                if (ncInstrumentorSeq[i].lastExceptionMessage.length() > 0 ){
    				nci.setLastException( new Exception( ncInstrumentorSeq[i].lastExceptionMessage ) );
                } else {
					nci.setLastException( null );
				}
    			nci.setStatus( ncInstrumentorSeq[i].status );
				nci.setUserData( infoSeq[i].userData );
				((CalculatedInstrumentor)nci).sumIntervalTime( infoSeq[i].timestamp );
				long numNCISamples = ((CalculatedInstrumentor)nci).incSamples();
				if ( numNCISamples % calcToSampleFactor == 0 ) {
					if ( doCalcs ) {
						((CalculatedInstrumentor)nci).calculate( calcToSampleFactor );
					}
					if ( matches( instName, nciMatchList ) ) {
						if ( listener != null ) {
							listener.acceptCalculatedNetworkConnectionInstrumentorEvent( infoSeq[i].timestamp,
																			 nci,
																			 (CalculatedNetworkConnectionInstrumentor)nci,
																			 clusterName,
																			 orbName );
						} else {
							// For use later in block call.
							iList.add( nci );
						}
					}
				}
			}
			if ( finishListener != null ) {
				finishListener.finishEvent();
			}
			if ( iList.size() != 0 && blockListener != null ) {
				Logger.debug( "InstrumentorECCollector.monNCInst: calling calc-blocklistener, monitor(" + monitorName + "), fromOrb(" + orbName + ")." );
				// We are making block call.  Assume timestamp comes from first instrumentor.
				blockListener.acceptCalculatedNetworkConnectionInstrumentorsEvent( infoSeq[0].timestamp,
																	  iList,
																	  iList,
																	  clusterName,
																	  orbName );
			}
		}

		public void monitorQueueInstrumentor( String monitorName, String clusterName, String orbName, InstrumentorInfo[] infoSeq, QueueInstrumentorStruct[] qInstrumentorSeq ) {
			ArrayList iList = new ArrayList(); // For use in block calls.
			QueueInstrumentorFactory qiFactory = getQIFactory( orbName );
			cleanupFactory( orbName, qiFactory, infoSeq );
			QueueInstrumentor qi = null;
			if ( !doCalcs ) {
				qi = (QueueInstrumentor)qiTl.get();
				if ( qi == null ) {
					qi = qiFactory.getInstance( "Dummy", null );
					qiTl.set( qi );
				}
			}
			for( int i = 0; i < infoSeq.length; i++ ) {
				String instName = infoSeq[i].instrumentorName;
				if ( doCalcs ) {
					qi = qiFactory.getInstance( instName, infoSeq[i].userData );
				} else {
					qi.rename( instName );
				}
				qi.setKey( infoSeq[i].key );
				qi.setEnqueued( qInstrumentorSeq[i].enqueued );
				qi.setDequeued( qInstrumentorSeq[i].dequeued );
				qi.setFlushed( qInstrumentorSeq[i].flushed );
				qi.setOverlaid( qInstrumentorSeq[i].overlaid );
				qi.setCurrentSize( qInstrumentorSeq[i].currentSize );
				// For receipt on the event channel, do setCurrentSize first, as above.
				// Then do HWM calcs.  Currently, OverallHWM is not sent on the
				// channel, so calculate it based upon the HWM that is sent, which
				// is reset at the source every interval.
				// If !doCalcs, then I don't have proper accounting for overallHWM.
				// In this case, to make overallHWM work, I will keep a separate
				// map containing the current overalHWM per qi.  This is a little
				// against what !doCalcs is for, but wanted make overallHWM work.
				long overallHWM = qi.getOverallHighWaterMark();
				long overallHWMTime = qi.getOverallHighWaterMarkTime();
				if ( !doCalcs ) {
					OverallHWM orbInstOverallHWM = (OverallHWM)overallHWMMap.get( orbName + instName );
					if ( orbInstOverallHWM == null ) {
						overallHWM = 0;
						overallHWMTime = 0;
					} else {
						overallHWM = orbInstOverallHWM.overallHWM;
						overallHWMTime = orbInstOverallHWM.overallHWMTime;
					}
					// Need to reset these to current values for orb+inst.
					qi.setOverallHighWaterMark( overallHWM );
					qi.setOverallHighWaterMarkTime( overallHWMTime );
				}
				qi.setHighWaterMark( qInstrumentorSeq[i].highWaterMark );
				if ( qi.getHighWaterMark() > overallHWM ) {
					qi.setOverallHighWaterMark( qi.getHighWaterMark() );
					if ( !doCalcs ) {
						overallHWMMap.put( orbName + instName, new OverallHWM( qi.getOverallHighWaterMark(), qi.getOverallHighWaterMarkTime() ) );
					}
				}
				qi.setStatus( qInstrumentorSeq[i].status );
				qi.setUserData( infoSeq[i].userData );
				((CalculatedInstrumentor)qi).sumIntervalTime( infoSeq[i].timestamp );
				long numQISamples = ((CalculatedInstrumentor)qi).incSamples();
				if ( numQISamples % calcToSampleFactor == 0 ) {
					if ( doCalcs ) {
						((CalculatedInstrumentor)qi).calculate( calcToSampleFactor );
					}
					if ( matches( instName, qiMatchList ) ) {
						if ( listener != null ) {
							listener.acceptCalculatedQueueInstrumentorEvent( infoSeq[i].timestamp,
																    qi,
																    (CalculatedQueueInstrumentor)qi,
																    clusterName,
																    orbName );
						} else {
							// For use later in block call.
							iList.add( qi );
						}
					}
				}
			}
			if ( finishListener != null ) {
				finishListener.finishEvent();
			}
			if ( iList.size() != 0 && blockListener != null ) {
				Logger.debug( "InstrumentorECCollector.monQueueInst: calling calc-blocklistener, monitor(" + monitorName + "), fromOrb(" + orbName + ")." );
				// We are making block call.  Assume timestamp comes from first instrumentor.
				blockListener.acceptCalculatedQueueInstrumentorsEvent( infoSeq[0].timestamp,
															iList,
															iList,
															clusterName,
															orbName );
			}
		}

		public void monitorThreadPoolInstrumentor( String monitorName, String clusterName, String orbName, InstrumentorInfo[] infoSeq, ThreadPoolInstrumentorStruct[] tpInstrumentorSeq ) {
			ArrayList iList = new ArrayList(); // For use in block calls.
			ThreadPoolInstrumentorFactory tpiFactory = getTPIFactory( orbName );
			cleanupFactory( orbName, tpiFactory, infoSeq );
			ThreadPoolInstrumentor tpi = null;
			if ( !doCalcs ) {
				tpi = (ThreadPoolInstrumentor)tpiTl.get();
				if ( tpi == null ) {
					tpi = tpiFactory.getInstance( "Dummy", null );
					tpiTl.set( tpi );
				}
			}
			for( int i = 0; i < infoSeq.length; i++ ) {
				String instName = infoSeq[i].instrumentorName;
				if ( doCalcs ) {
					tpi = tpiFactory.getInstance( instName, infoSeq[i].userData );
				} else {
					tpi.rename( instName );
				}
				tpi.setKey( infoSeq[i].key );
				tpi.setCurrentlyExecutingThreads( tpInstrumentorSeq[i].currentlyExecutingThreads );
				tpi.setStartedThreads( tpInstrumentorSeq[i].startedThreads );
				tpi.setPendingThreads( tpInstrumentorSeq[i].pendingThreads );
				tpi.setStartedThreadsHighWaterMark( tpInstrumentorSeq[i].startedThreadsHighWaterMark );
				tpi.setPendingTaskCount( tpInstrumentorSeq[i].pendingTaskCount );
				tpi.setPendingTaskCountHighWaterMark( tpInstrumentorSeq[i].pendingTaskCountHighWaterMark );
				tpi.setUserData( infoSeq[i].userData );
				((CalculatedInstrumentor)tpi).sumIntervalTime( infoSeq[i].timestamp );
				long numTPISamples = ((CalculatedInstrumentor)tpi).incSamples();
				if ( numTPISamples % calcToSampleFactor == 0 ) {
					if ( doCalcs ) {
						((CalculatedInstrumentor)tpi).calculate( calcToSampleFactor );
					}
					if ( matches( instName, tpiMatchList ) ) {
						if ( listener != null ) {
							listener.acceptCalculatedThreadPoolInstrumentorEvent( infoSeq[i].timestamp,
																	    tpi,
																	    (CalculatedThreadPoolInstrumentor)tpi,
																	    clusterName,
																	    orbName );
						} else {
							// For use later in block call.
							iList.add( tpi );
						}
					}
				}
			}
			if ( finishListener != null ) {
				finishListener.finishEvent();
			}
			if ( iList.size() != 0 && blockListener != null ) {
				Logger.debug( "InstrumentorECCollector.monTPInst: calling calc-blocklistener, monitor(" + monitorName + "), fromOrb(" + orbName + ")." );
				// We are making block call.  Assume timestamp comes from first instrumentor.
				blockListener.acceptCalculatedThreadPoolInstrumentorsEvent( infoSeq[0].timestamp,
																iList,
																iList,
																clusterName,
																orbName );
			}
		}

        public void monitorKeyValueInstrumentor( String monitorName, String clusterName, String orbName, InstrumentorInfo[] infoSeq, KeyValueInstrumentorStruct[] kvInstrumentorSeq ) {
            switch (kvInstrumentorSeq[0].type) {
            case InstrumentorTypeValues.JMX:
                monitorJmxInstrumentor( monitorName, clusterName, orbName, infoSeq, kvInstrumentorSeq );
                break;
            case InstrumentorTypeValues.JSTAT:
                monitorJstatInstrumentor( monitorName, clusterName, orbName, infoSeq, kvInstrumentorSeq );
                break;
                
            default:
                String key = orbName + "_" + kvInstrumentorSeq[0].type;
            	if(!invalidKeyValueLogTracker.containsKey(key)){
            		invalidKeyValueLogTracker.put(key, kvInstrumentorSeq[0].type);
            		Logger.sysAlarm( "An invalid KeyValue type.  The type was: " + kvInstrumentorSeq[0].type);
            	}
            	break;
            }
        }
        
        public void monitorJmxInstrumentor( String monitorName, String clusterName, String orbName, InstrumentorInfo[] infoSeq, KeyValueInstrumentorStruct[] kvInstrumentorSeq ) {
 
            ArrayList iList = new ArrayList(); // For use in block calls.
            JmxInstrumentorFactory jmxiFactory = getJMXIFactory( orbName );
            cleanupFactory( orbName, jmxiFactory, infoSeq );
            JmxInstrumentor jmxi = null;
            if ( !doCalcs ) {
                jmxi = (JmxInstrumentor)jmxiTl.get();
                if ( jmxi == null ) {
                    jmxi = jmxiFactory.getInstance( "Dummy", null );
                    jmxiTl.set( jmxi );
                }
            }
            for( int i = 0; i < infoSeq.length; i++ ) {
                String instName = infoSeq[i].instrumentorName;
                if ( doCalcs ) {
                    jmxi = jmxiFactory.getInstance( instName, infoSeq[i].userData );
                } else {
                    jmxi.rename( instName );
                }
                jmxi.setKey( infoSeq[i].key );
                for(int k = 0; k < kvInstrumentorSeq[i].keyValue.length; k++) {
                    switch (kvInstrumentorSeq[i].keyValue[k].key) {

                    case InstrumentorTypeValues.PEAKTHREADCOUNT:
                        jmxi.setPeakThreadCount((int)kvInstrumentorSeq[i].keyValue[k].dblValue);                        
                        break;
                    
                    case InstrumentorTypeValues.CURENTTHREADCOUNT:
                        jmxi.setCurrentThreadCount((int)kvInstrumentorSeq[i].keyValue[k].dblValue);
                        break;
                    
                    case InstrumentorTypeValues.TOTALTHREADSSTARTED:
                        jmxi.setTotalThreadsStarted((int)kvInstrumentorSeq[i].keyValue[k].dblValue);
                        break;
                    
                    case InstrumentorTypeValues.TOTALCPUTIME:
                        jmxi.setTotalCPUTime((long)kvInstrumentorSeq[i].keyValue[k].dblValue);
                        break;
                    
                    default:
                        
                    	String key = orbName + "_" + kvInstrumentorSeq[i].keyValue[k].key;
                    	if(!invalidJMXKeyLogTracker.containsKey(key)){
                    		invalidJMXKeyLogTracker.put(key, kvInstrumentorSeq[i].keyValue[k].key);
                    		Logger.sysAlarm( "JMX:An invalid kvInstrumentorSeq[i].KeyValue[k] type for orb" +orbName+ ".  The type was: " + kvInstrumentorSeq[i].keyValue[k].key);
                    	}
                        break;
                    }
                }

                jmxi.setUserData( infoSeq[i].userData );
                ((CalculatedInstrumentor)jmxi).sumIntervalTime( infoSeq[i].timestamp );
                long numKVISamples = ((CalculatedInstrumentor)jmxi).incSamples();
                if ( numKVISamples % calcToSampleFactor == 0 ) {
                    if ( doCalcs ) {
                        ((CalculatedInstrumentor)jmxi).calculate( calcToSampleFactor );
                    }
                    if ( matches( instName, kviMatchList ) ) {
                        if ( listener != null ) {
                            listener.acceptCalculatedJmxInstrumentorEvent( infoSeq[i].timestamp,
                                                                        jmxi,
                                                                        (CalculatedJmxInstrumentor)jmxi,
                                                                        clusterName,
                                                                        orbName );
                        } else {
                            // For use later in block call.
                            iList.add( jmxi );
                        }
                    }
                }
            }
            if ( finishListener != null ) {
                finishListener.finishEvent();
            }
            if ( iList.size() != 0 && blockListener != null ) {
                Logger.debug( "InstrumentorECCollector.monJMXInst: calling calc-blocklistener, monitor(" + monitorName + "), fromOrb(" + orbName + ")." );
                // We are making block call.  Assume timestamp comes from first instrumentor.
                blockListener.acceptCalculatedJmxInstrumentorsEvent( infoSeq[0].timestamp,
                                                                iList,
                                                                iList,
                                                                clusterName,
                                                                orbName );
            }
        }

        public void monitorJstatInstrumentor( String monitorName, String clusterName, String orbName, InstrumentorInfo[] infoSeq, KeyValueInstrumentorStruct[] kvInstrumentorSeq ) {
            
            ArrayList iList = new ArrayList(); // For use in block calls.
            JstatInstrumentorFactory jstatiFactory = getJSTATIFactory( orbName );
            cleanupFactory( orbName, jstatiFactory, infoSeq );
            JstatInstrumentor jstati = null;
            if ( !doCalcs ) {
                jstati = (JstatInstrumentor)jstatiTl.get();
                if ( jstati == null ) {
                    jstati = jstatiFactory.getInstance( "Dummy", null );
                    jstatiTl.set( jstati );
                }
            }

            for( int i = 0; i < infoSeq.length; i++ ) {
                String instName = infoSeq[i].instrumentorName;
                if ( doCalcs ) {
                    jstati = jstatiFactory.getInstance( instName, infoSeq[i].userData );
                } else {
                    jstati.rename( instName );
                }
                jstati.setKey( infoSeq[i].key );
                for(int k = 0; k < kvInstrumentorSeq[i].keyValue.length; k++) {
                    switch (kvInstrumentorSeq[i].keyValue[k].key) {

                    case InstrumentorTypeValues.S0CAPACITY:
                        jstati.setS0Capacity(kvInstrumentorSeq[i].keyValue[k].dblValue);  
                        break;
                    
                    case InstrumentorTypeValues.S1CAPACITY:
                        jstati.setS1Capacity(kvInstrumentorSeq[i].keyValue[k].dblValue);                        
                        break;
                    
                    case InstrumentorTypeValues.S0UTILIZATION:
                        jstati.setS0Utilization(kvInstrumentorSeq[i].keyValue[k].dblValue);
                        break;

                    case InstrumentorTypeValues.S1UTILIZATION:
                        jstati.setS1Utilization(kvInstrumentorSeq[i].keyValue[k].dblValue);
                        break;

                    case InstrumentorTypeValues.ECAPACITY:
                        jstati.setECapacity(kvInstrumentorSeq[i].keyValue[k].dblValue);                        
                        break;
                    
                    case InstrumentorTypeValues.EUTILIZATION:
                        jstati.setEUtilization(kvInstrumentorSeq[i].keyValue[k].dblValue);
                        break;

                    case InstrumentorTypeValues.OCAPACITY:
                        jstati.setOCapacity(kvInstrumentorSeq[i].keyValue[k].dblValue);                        
                        break;
                    
                    case InstrumentorTypeValues.OUTILIZATION:
                        jstati.setOUtilization(kvInstrumentorSeq[i].keyValue[k].dblValue);
                        break;
                    
                    case InstrumentorTypeValues.PCAPACITY:
                        jstati.setPCapacity(kvInstrumentorSeq[i].keyValue[k].dblValue);                        
                        break;
                    
                    case InstrumentorTypeValues.PUTILIZATION:
                        jstati.setPUtilization(kvInstrumentorSeq[i].keyValue[k].dblValue);
                        break;

                    case InstrumentorTypeValues.NBRYGGCS:
                        jstati.setNbrYgGcs((long)kvInstrumentorSeq[i].keyValue[k].dblValue);
                        break;

                    case InstrumentorTypeValues.TIMEYGGCS:
                        jstati.setTimeYgGcs(kvInstrumentorSeq[i].keyValue[k].dblValue);
                        break;

                    case InstrumentorTypeValues.NBRFULLGCS:
                        jstati.setNbrFullGcs((long)kvInstrumentorSeq[i].keyValue[k].dblValue);
                        break;

                    case InstrumentorTypeValues.TIMEFULLGCS:
                        jstati.setTimeFullGcs(kvInstrumentorSeq[i].keyValue[k].dblValue);
                        break;

                    case InstrumentorTypeValues.TIMEYGFULLGCS:
                        jstati.setTimeYgFullGcs(kvInstrumentorSeq[i].keyValue[k].dblValue);
                        break;
                    
                    case InstrumentorTypeValues.TICKFREQ:
                        jstati.setTickFreq((long)kvInstrumentorSeq[i].keyValue[k].dblValue);
                        break;
                    
                    case InstrumentorTypeValues.APPTIME:
                        jstati.setApplicationTime((long)kvInstrumentorSeq[i].keyValue[k].dblValue);
                        break;
                    
                    case InstrumentorTypeValues.SAFEPOINTSYNCTIME:
                        jstati.setSafepointSyncTime((long)kvInstrumentorSeq[i].keyValue[k].dblValue);
                        break;
                    
                    case InstrumentorTypeValues.SAFEPOINTTIME:
                        jstati.setSafepointTime((long)kvInstrumentorSeq[i].keyValue[k].dblValue);
                        break;
                    
                    case InstrumentorTypeValues.SAFEPOINTS:
                        jstati.setSafepoints((long)kvInstrumentorSeq[i].keyValue[k].dblValue);
                        break;
                        

                    default:
                    	String key = orbName + "_" + kvInstrumentorSeq[i].keyValue[k].key;
                    	if(!invalidJSTATKeyLogTracker.containsKey(key)){
                    		invalidJSTATKeyLogTracker.put(key, kvInstrumentorSeq[i].keyValue[k].key);
                    		Logger.sysAlarm( "JSTAT:An invalid kvInstrumentorSeq[i].KeyValue[k] type for orb " + orbName + ".  The type was: " + kvInstrumentorSeq[i].keyValue[k].key);
                    	}
                        break;
                        
                    }
                }

                jstati.setUserData( infoSeq[i].userData );
                ((CalculatedInstrumentor)jstati).sumIntervalTime( infoSeq[i].timestamp );
                long numKVISamples = ((CalculatedInstrumentor)jstati).incSamples();
                if ( numKVISamples % calcToSampleFactor == 0 ) {
                    if ( doCalcs ) {
                        ((CalculatedInstrumentor)jstati).calculate( calcToSampleFactor );
                    }
                    if ( matches( instName, kviMatchList ) ) {
                        if ( listener != null ) {
                            listener.acceptCalculatedJstatInstrumentorEvent( infoSeq[i].timestamp,
                                                                        jstati,
                                                                        (CalculatedJstatInstrumentor)jstati,
                                                                        clusterName,
                                                                        orbName );
                        } else {
                            // For use later in block call.
                            iList.add( jstati );
                        }
                    }
                }
            }
            if ( finishListener != null ) {
                finishListener.finishEvent();
            }
            if ( iList.size() != 0 && blockListener != null ) {
                Logger.debug( "InstrumentorECCollector.monJstatInst: calling calc-blocklistener, monitor(" + monitorName + "), fromOrb(" + orbName + ")." );
                // We are making block call.  Assume timestamp comes from first instrumentor.
                blockListener.acceptCalculatedJstatInstrumentorsEvent( infoSeq[0].timestamp,
                                                                iList,
                                                                iList,
                                                                clusterName,
                                                                orbName );
            }
        }


		public org.omg.CORBA.Object get_typed_consumer() {
			return null;
		}

		public void push( org.omg.CORBA.Any any ) throws org.omg.CosEventComm.Disconnected {
		}

		public void disconnect_push_consumer() {
		}
	}

	private class OverallHWM {
		public long overallHWM;
		public long overallHWMTime;

		public OverallHWM( long pOverallHWM, long pOverallHWMTime ) {
			overallHWM = pOverallHWM;
			overallHWMTime = pOverallHWMTime;
		}
	}

	private class FactoryVisitor implements CountInstrumentorFactoryVisitor, EventChannelInstrumentorFactoryVisitor, HeapInstrumentorFactoryVisitor, MethodInstrumentorFactoryVisitor, NetworkConnectionInstrumentorFactoryVisitor, QueueInstrumentorFactoryVisitor, ThreadPoolInstrumentorFactoryVisitor, JmxInstrumentorFactoryVisitor, JstatInstrumentorFactoryVisitor {

		Map instMap = null;

		public FactoryVisitor() {
		}

		public Map getInstrumentorMap( InstrumentorFactory factory ) {

			// These calls will in turn call "start" below.  This sets
			// instMap.
			if ( factory instanceof CountInstrumentorFactory ) {
				((CountInstrumentorFactory)factory).accept( this, true );
			} else if ( factory instanceof HeapInstrumentorFactory ) {
				((HeapInstrumentorFactory)factory).accept( this, true );
			} else if ( factory instanceof MethodInstrumentorFactory ) {
				((MethodInstrumentorFactory)factory).accept( this, true );
			} else if ( factory instanceof NetworkConnectionInstrumentorFactory ) {
				((NetworkConnectionInstrumentorFactory)factory).accept( this, true );
			} else if ( factory instanceof QueueInstrumentorFactory ) {
				((QueueInstrumentorFactory)factory).accept( this, true );
			} else if ( factory instanceof ThreadPoolInstrumentorFactory ) {
				((ThreadPoolInstrumentorFactory)factory).accept( this, true );
            } else if ( factory instanceof JmxInstrumentorFactory ) {
                ((JmxInstrumentorFactory)factory).accept( this, true );
            } else if ( factory instanceof JstatInstrumentorFactory ) {
                ((JstatInstrumentorFactory)factory).accept( this, true );
			}

			return instMap;
		}

		public boolean start( Map unmodifiableCloneOfInstrumentorMap ) {
			instMap = unmodifiableCloneOfInstrumentorMap;
			return false; // Don't bother calling anything else.
		}

		public void end() {
		}

		public boolean visit( CountInstrumentor ci ) {
			return false; // Don't continue.
		}

		public boolean visit( EventChannelInstrumentor eci ) {
			return false; // Don't continue.
		}

		public boolean visit( HeapInstrumentor hi ) {
			return false; // Don't continue.
		}

		public boolean visit( MethodInstrumentor mi ) {
			return false; // Don't continue.
		}

		public boolean visit( NetworkConnectionInstrumentor nci ) {
			return false; // Don't continue.
		}

		public boolean visit( QueueInstrumentor qi ) {
			return false; // Don't continue.
		}

		public boolean visit( ThreadPoolInstrumentor tpi ) {
			return false; // Don't continue.
		}

        public boolean visit( JmxInstrumentor jmxi ) {
            return false; // Don't continue.
        }

        public boolean visit( JstatInstrumentor jstati ) {
            return false; // Don't continue.
        }

	}

} // InstrumentorECCollector
