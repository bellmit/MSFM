package com.cboe.infrastructureServices.foundationFramework;

import java.util.Hashtable;

import junit.framework.TestCase;

import com.cboe.ORBInfra.ORB.Orb;
import com.cboe.ORBInfra.ORB.ServerRequestImpl;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.instrumentationService.InstrumentationService;
import com.cboe.infrastructureServices.instrumentationService.Instrumentor;
import com.cboe.infrastructureServices.instrumentationService.LightWeightInstrumentor;
import com.cboe.infrastructureServices.loggingService.LogService;
import com.cboe.infrastructureServices.loggingService.MsgCategory;
import com.cboe.infrastructureServices.loggingService.MsgParameter;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.AggregatedMethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.transactionTiming.InfraTTEUtil;
import com.cboe.instrumentationService.transactionTiming.TransactionTimer;
import com.cboe.instrumentationService.transactionTiming.TransactionTimerBaseImpl;
import com.cboe.instrumentationService.transactionTiming.TransactionTimingContext;

/**
 * The remote implementation of the service The interceptor delegates the actual work to the service
 * object instance in a defined execution environment.
 *
 * @version 3.3
 * @author Dave Hoag
 */
public abstract class BOInterceptor
{
	public static final String INSTRUMENTATION_POLICY = "instrumentationEnabled";
	protected BObject bo = null;
	protected Hashtable oldWayInstrumentors;
    long time;
    LightWeightInstrumentor lightWeightInstrumentor;
    transient protected LogService cachedLogService;
    transient protected Boolean cachedInstrumentEnabled;
    protected String cachedPathName;

    protected TransactionTimer transactionTimer;
    private static TransactionTimer transactionTimerLocal = null;
    
    /* measure of the time from parsing to this object */
    private static long orbTimeMethodID;
    /* time waiting to read from stream, either from thread or previous message */
    private static long orbGrizzlyHandoffID;
    
    protected static boolean isORBRequestProcessingEmitPointEnabled=false;
    protected static boolean isORBGrizzlyHandoffEmitPointEnabled=false;
    
    
    private Orb orb;

	static{
		// LocalTT instance will be used for recording ORB time. Only one "methodID" to
		// register here, which is a measure of the time from the start of reading the
		// socket that supplied the current call, up to the current BOInterceptor emit point.
		// This will give a measure of the ORB time prior to the application.
		transactionTimerLocal = TransactionTimerBaseImpl.getLocalInstance();
		orbTimeMethodID = transactionTimerLocal.registerTransactionIdentifier("ORBRequestProcessing");
		orbGrizzlyHandoffID = transactionTimerLocal.registerTransactionIdentifier("ORBGrizzlyHandoff");
	}
    

    protected int bufferSize = 0;
    protected boolean transactionTimerEnabled;

	private static boolean CREATE_MI = !Boolean.getBoolean( "BOInterceptor.DontCreateMethodInstrumentor" );

	/**
	 * @author Murali Yellepeddy
	 */
	public void setBufferSize(int bufferSize){
		this.bufferSize = bufferSize;
	}
	/**
	 * @author Murali Yellepeddy
	 */
	public int getBufferSize(){
		return bufferSize;
	}
    /**
     * A BOInterceptor wraps invocations on a BObject.
     * This enables instrumentation and exception logging.
     * The BObject parameter must be fully configured prior to using this constructor.
     * This usually means a call to the BOHome.addToContainer(BObject ) method before
     * the creation of the interceptor.
     *
     * @param aBo BObject for which this is intercepting
     */
	public BOInterceptor( BObject aBo )
	{
		bo = aBo;
		oldWayInstrumentors = new Hashtable(50);

		lightWeightInstrumentor = new LightWeightInstrumentor();
		/* Murali Yellpeddy */
		transactionTimerEnabled = (System.getProperty("transactionTimer","false")).equalsIgnoreCase("true");
		
		if (System.getProperty("TT_TimeORBRequestProcessing")!=null){
			isORBRequestProcessingEmitPointEnabled=true;
		}
		if (System.getProperty("TT_TimeORBGrizzlyHandoff")!=null){
			isORBGrizzlyHandoffEmitPointEnabled=true;
		}
		
		if (transactionTimerEnabled){
			try
			{
				transactionTimer =TransactionTimerBaseImpl.getInstance();
			// initialize is called in the getInstance itself
			//transactionTimer.initialize();
			}
			catch(Throwable ex)
			{
				ex.printStackTrace();	
				Log.debug("Coudn't initialize TransactionTimer " + ex.getMessage());
			}
		}
		orb = (Orb) FoundationFramework.getInstance().getOrbService().getOrb();
	}
	/**
	 * Look for a property that tells if the instrumenation is enabled.
	 * Current implementation assumes instrumentation is static property.
	 * It can not change.
	 *
	 * @return boolean true indicates that the data is expected to be instrumented
	 */
	protected boolean isInstrumentationEnabled()
	{
		if(cachedInstrumentEnabled == null)
		{
			String property = getBObject().getBOHome().getFrameworkProperty(INSTRUMENTATION_POLICY, "true");
			cachedInstrumentEnabled = new Boolean (property.equals("true"));
		}
		return cachedInstrumentEnabled.booleanValue();
	}
	/**
	 * The instrumentation path.
	 * The business object for which this is an interceptor must be associated with a home before this
	 * method can safely be called.
	 * This method is called by the addIntrumentor method.
	 * @see #addIntrumentor(java.lang.String )
	 * @param methodKey String uniquely identifying the method.
	 */
	protected String getPath(final String methodKey)
	{
		if(cachedPathName == null)
		{
	    	if(getBObject().getBOHome() == null) throw new IllegalArgumentException("Business object " + getBObject() + " not associated with a home.");
		String path = getBObject().getName();
	    	cachedPathName = path.replace('.', '/');
	    }
	    return cachedPathName + '/' + methodKey;
	}
	/**
	 */
	protected void addInstrumentor(String methodKey)
	{
		if(isInstrumentationEnabled())
		{
	    	InstrumentationService srvc = FoundationFramework.getInstance().getInstrumentationService();
	    	//methodName
	    	Instrumentor in = srvc.getInstrumentor(getPath(methodKey));
			oldWayInstrumentors.put(methodKey, in);

			// Create new method instrumentor.
			try {
				if ( srvc.getMethodInstrumentorFactory() != null ) {
					MethodInstrumentor mi = srvc.getMethodInstrumentorFactory().create( getPath(methodKey), null );
					srvc.getMethodInstrumentorFactory().register( mi );
				}
			} catch( InstrumentorAlreadyCreatedException e ) {
			}
		}
	}
	/**
	 */
	protected Instrumentor createInstrumentor(final String methodKey)
	{
		if(isInstrumentationEnabled())
		{
	    	InstrumentationService srvc = FoundationFramework.getInstance().getInstrumentationService();
	    	//methodName
	    	Instrumentor in = srvc.getInstrumentor(getPath(methodKey));
			oldWayInstrumentors.put(methodKey, in);

			// Create new method instrumentor.
			try {
				if ( srvc.getMethodInstrumentorFactory() != null ) {
					MethodInstrumentor mi = srvc.getMethodInstrumentorFactory().create( getPath(methodKey), null );
					srvc.getMethodInstrumentorFactory().register( mi );
				}
			} catch( InstrumentorAlreadyCreatedException e ) {
			}

	    	return in;
		}
		return lightWeightInstrumentor;
	}
	/**
	 * @author Murali Yellepeddy
	 */
	protected Instrumentor createInstrumentor(final String methodKey, String methodSinature)
	{
		return createInstrumentor( methodKey, methodSinature, null, false );
	}

	protected Instrumentor createInstrumentor(final String methodKey, String methodSinature, String groupName, boolean privateFlag )
	{
		if(isInstrumentationEnabled())
		{
	    	InstrumentationService srvc = FoundationFramework.getInstance().getInstrumentationService();
	    	//methodName
	    	Instrumentor in = srvc.getInstrumentor(getPath(methodKey));
			oldWayInstrumentors.put(methodKey, in);

			in.setMethodSignature(methodSinature);

			// Create new method instrumentor.
			try {
				if ( srvc.getMethodInstrumentorFactory() != null && CREATE_MI ) {
					MethodInstrumentor mi = srvc.getMethodInstrumentorFactory().create( getPath(methodKey), null );
					mi.setPrivate( privateFlag );
					srvc.getMethodInstrumentorFactory().register( mi );

					if ( groupName != null && groupName.length() > 0 ) {
						MethodInstrumentor ami = null;
						try {
							ami = srvc.getAggregatedMethodInstrumentorFactory().create( groupName, null );
							srvc.getAggregatedMethodInstrumentorFactory().register( ami );
							ami.setPrivate( true );
						} catch( InstrumentorAlreadyCreatedException e ) {
							ami = srvc.getAggregatedMethodInstrumentorFactory().find( groupName );
						}
						((AggregatedMethodInstrumentor)ami).addInstrumentor( mi );
					}
				}
			} catch( InstrumentorAlreadyCreatedException e ) {
			}

	    	return in;
		}
		return lightWeightInstrumentor;
	}
	/**
	 * Get the the instrumentor cached at the method key.
	 * This instrumentor is assumed to have been created via the addInstrumentor method.
	 * @see #addInstrumentor(java.lang.String )
	 * @return The previously created interceptor.
	 * @param methodKey String uniquely identifying the method.
	 * @deprecated Regenerate your instrumentors.
	 */
	protected Instrumentor getInstrumentor(String methodKey)
	{
		if(isInstrumentationEnabled())
		{
			return (Instrumentor)oldWayInstrumentors.get(methodKey);
		}
	    else
	    {
	    	return lightWeightInstrumentor;
	    }
	}
	/**
	 * @return BObject The object for which this interceptor is wrapping.
	 */
	public BObject getBObject()
	{
	    return bo;
	}
	/**
	 * The current default behavior is to do nothing.
	 */
	protected void postProcess()
	{
	}
	/**
	 * The current default behavior is to do nothing.
	 */
	protected void preProcess()
	{
	}
	/**
	 * PreProcess merely tracks the start time of a method invocation.
	 * @param methodID String The id representing the method in progress.
	 */
	protected void preProcess(String methodID)
	{
		MethodInstrumentor mi = null;
		if ( FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory() != null ) {

			mi = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().find( getPath( methodID ) );
		}
		if ( mi != null ) {
			mi.beforeMethodCall();
		}
		preProcess( getInstrumentor( methodID ) );
	}
	/**
	 * PreProcess merely tracks the start time of a method invocation.
	 * @param methodID String The id representing the method in progress.
	 * @param instr tran-timing instrumentor to use.
	 * @param mi method-instrumentor to use.
	 */
	protected void preProcess(String methodID, Instrumentor instr, MethodInstrumentor mi )
	{
		if ( mi != null ) {
			mi.beforeMethodCall();
		}
		preProcess( instr );
	}
	/**
	 * If the exception parameter is true, we do not do any of the post processing.
	 * PostProcessing increments Message and block counts and Message times.
	 * @param exception boolean True if an exception occurred while executing the method
	 * @param methodID String The id representing the method in progress.
	 * @deprecated
	 */
	protected void postProcess(String methodID, Instrumentor in, boolean exception)
	{
		postProcess(methodID, exception); // instrumentor not needed in call.
	}
	/**
	 * If the exception parameter is true, we do not do any of the post processing.
	 * PostProcessing increments Message and block counts and Message times.
	 * @param exception boolean True if an exception occurred while executing the method
	 * @param methodID String The id representing the method in progress.
	 */
	protected void postProcess(long time, String methodID, Instrumentor in, boolean exception)
	{
		postProcess( methodID, exception ); // time and instrumentor not needed.
	}
	/**
	 * If the exception parameter is true, we do not do any of the post processing.
	 * PostProcessing increments Message and block counts and Message times.
	 * @param exception boolean True if an exception occurred while executing the method
	 * @param methodID String The id representing the method in progress.
	 */
	protected void postProcess(String methodID, boolean exception)
	{
		Instrumentor in = getInstrumentor( methodID );
		MethodInstrumentor mi = null;
		if ( FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory() != null ) {

			mi = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().find( getPath( methodID ) );
		}


		if(transactionTimerEnabled){
			try{
				in.sendMethodEvent(
					exception?TransactionTimer.LeaveWithException:TransactionTimer.Leave,
					TransactionTimingContext.getEntityID() );
			}
			catch(Throwable t){
				Log.debug("Couldn't Log Transaction Timer " + t.getMessage());
			}
		}
		if ( mi != null ) {
			mi.incCalls( 1 );
			mi.afterMethodCall();
			if ( exception ) {
				mi.incExceptions( 1 );
			}
		}
		if(exception) {
			return;
		}

		in.incMsgsReceived(1);
		in.incBlocksReceived(1);
		postProcess();
	}
	/**
	 * If the exception parameter is true, we do not do any of the post processing.
	 * PostProcessing increments Message and block counts and Message times.
	 * @param methodID String The id representing the method in progress.
	 * @param in tran-timing instrumentor to use
	 * @param mi method instrumentor to use
	 * @param exception boolean True if an exception occurred while executing the method
	 */
	protected void postProcess(String methodID, Instrumentor in, MethodInstrumentor mi, boolean exception)
	{
		if(transactionTimerEnabled){
			try{
				in.sendMethodEvent(
					exception?TransactionTimer.LeaveWithException:TransactionTimer.Leave,
				   TransactionTimingContext.getEntityID() );
			}
			catch(Throwable t){
				Log.debug("Couldn't Log Transaction Timer " + t.getMessage());
			}
		}
		if ( mi != null ) {
			mi.incCalls( 1 );
			mi.afterMethodCall();
			if ( exception ) {
				mi.incExceptions( 1 );
			}
		}
		if(exception) {
			return;
		}

		postProcess();
	}
	/**
	 * @author Murali Yellepeddy
	 */
	protected void preProcess(Instrumentor in)
	{
		// if entering the method the direction is set to 1
		if(transactionTimerEnabled){
			try{
				long entityID = TransactionTimingContext.getEntityID();
				if ( entityID != 0 ) {
					// Only do the ORB timing if entityID is live, and also only if there
					// is an ORB start time that is live.  If this BOInterceptor is called
					// on a non-transport-receive thread, then the value is not available.
					// Do both enter and leave here, since the real enter happened down in
					// the transport code, but we have the original nano-timestamp.
					ServerRequestImpl sr = orb.serviceContextCurrent().getServerRequest();
					if ( sr != null && sr.getTransportRecvTime() != 0 ) {
						
						if(isORBGrizzlyHandoffEmitPointEnabled){
							/* delay in waiting to read off the stream from threads or previous calls */
							transactionTimerLocal.sendTransactionMethodEventInternal(orbGrizzlyHandoffID, entityID, TransactionTimer.Enter, sr.getGrizzlyStartTime() );
							transactionTimerLocal.sendTransactionMethodEventInternal(orbGrizzlyHandoffID, entityID, TransactionTimer.Leave, sr.getGrizzlyEndTime() );
						}
						if(isORBRequestProcessingEmitPointEnabled){
						/* total time from after read is done to this point */
							long localEntityID = InfraTTEUtil.setMessageSizeInEntityID(entityID, sr.getGIOPMessageSize());
							transactionTimerLocal.sendTransactionMethodEventInternal(orbTimeMethodID, localEntityID, TransactionTimer.Enter, sr.getDispatchStartTime() );
							transactionTimerLocal.sendTransactionMethodEventInternal(orbTimeMethodID, localEntityID, TransactionTimer.Leave, System.nanoTime() );
						}
						// Clear out the value of timers and msg-size, so that if there's multiple
						// calls to BOInterceptors within the same thread, TT emit point is done
						// only on the first one.
						sr.setGrizzlyDelay(0, 0);
						sr.setTransportRecvTime(0);
						sr.setDispatchStartTime(0);
						sr.setGIOPMessageSize(0);
					}
				}
				// Do this one no matter the value of entityID.
				in.sendMethodEvent(TransactionTimer.Enter, entityID );
			}
			catch(Throwable t){
				Log.debug("Couldn't Log Transaction Timer " + t.getMessage());
			}
		}
		preProcess();
	}
	
		
	
	/**
	 * Log debug messages.
	 * @param aMessage String text to log.
	 * @param methodID String uniquely identifying the method.
	 */
	protected void debugLog( final String aMessage, final String methodID)
	{
	      getLogService().log( MsgPriority.high, MsgCategory.debug, getPath(methodID), aMessage, new  MsgParameter[0] );
	}
	/**
	 * Cache the logservice. Should slightly improve performance.
	 * @return com.cboe.infrastructureServices.loggingService.LogService interface.
	 */
	protected LogService getLogService()
	{
		if(cachedLogService == null)
		{
			FoundationFramework ff = FoundationFramework.getInstance();
	    	String componentName = getBObject().getBOHome().getComponentName();
	    	cachedLogService = ff.getLogService(componentName);
	    }
	    return cachedLogService;
	}
	/**
	 * Log debug messages.
	 * @param e Exception An exception for which to log information.
	 * @param methodID String uniquely identifying the method.
	 */
	protected void debugLog( final Exception e, final String methodID)
	{
	      getLogService().log( MsgPriority.high, MsgCategory.debug, "BOInterceptor.debugLog",getPath(methodID), e );
	}
	/**
	 * Log system notification messages.
	 * @param methodID String uniquely identifying the method.
	 * @param aMessage String text to log.
	 */
	protected void systemLog( final String aMessage, final String methodID)
	{
	      getLogService().log( MsgPriority.high, MsgCategory.systemNotification, getPath(methodID), aMessage, new  MsgParameter[0] );
	}
	/**
	 * Log system notification messages.
	 * @param e Exception An exception for which to log information.
	 * @param methodID String uniquely identifying the method.
	 */
	protected void systemLog( final Exception e, final String methodID)
	{
	      getLogService().log( MsgPriority.high, MsgCategory.systemNotification, "BOInterceptor.systemLog", getPath(methodID) , e );
	}
	public static class UnitTest extends TestCase
	{
	    FoundationFramework ff;
        public static void main(String [] args)
        {
            junit.textui.TestRunner.run(UnitTest.class);
        }
        public UnitTest(String methodName)
        {
            super(methodName);
        }
		public void testInstrumentEnabled()
		{
			BOHome home = new BOHome(){};
			home.setName("aHome");
			BOContainer boc = new BOContainer();
			boc.setName("aContainer");
			home.setContainer(boc);

	    	BObject bo = new BObject(){};
	    	String name = "aBoName";
	    	bo.create(name);
	    	home.addToContainer(bo); //BObjects MUST be added to the container.

	    	MyInterceptor interceptor = new MyInterceptor(bo);
	    	Instrumentor in = interceptor.getInstrumentor("aName");
	    	assertTrue("Failed to get light weight instrumentor!", in instanceof LightWeightInstrumentor);

	    	interceptor = new MyInterceptor(bo);
            interceptor.cachedInstrumentEnabled = new Boolean(true);
	    	boc.setName("aName");
	    	interceptor.addInstrumentor("aName");
	    	in = interceptor.getInstrumentor("aName");
	    	assertTrue("Failed to find real instrumentor!", !( in instanceof LightWeightInstrumentor));
		}
		public void setUp() throws Exception
		{
            super.setUp();
			if(ff == null)
			{
				BOContainer container = new BOContainer();
				container.setName("aContainer");
		    	ContainerFactory.getInstance().containers.put("aContainer", container);

		    	java.util.Properties defaultProps = new java.util.Properties();
		    	defaultProps.put("UnitTest.poaName","one");
		    	defaultProps.put("UnitTest.aContainer.instrumentationEnabled","false");

		    	com.cboe.infrastructureServices.systemsManagementService.ConfigurationService cof = FoundationFramework.getConfigurationService(defaultProps);
		    	ff = FoundationFramework.getInstance();
		    	ff.setName("UnitTest");
		    	ff.setConfigService(cof);
		    	cof.initialize(null, 0);
		    }
		}
		public static class MyInterceptor extends BOInterceptor
		{
			public MyInterceptor(BObject obj)
			{
				super(obj);
			}
		}
	}
}
