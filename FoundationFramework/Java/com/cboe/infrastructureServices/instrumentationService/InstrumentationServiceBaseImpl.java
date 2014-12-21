package com.cboe.infrastructureServices.instrumentationService;

import java.util.Enumeration;
import java.util.Hashtable;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.loggingService.MsgCategory;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.instrumentationService.factories.EventChannelInstrumentorFactory;
import com.cboe.instrumentationService.factories.HeapInstrumentorFactory;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.NetworkConnectionInstrumentorFactory;
import com.cboe.instrumentationService.factories.QueueInstrumentorFactory;
import com.cboe.instrumentationService.factories.ThreadPoolInstrumentorFactory;
/**
 * The base implementation for the instrumentation service.
 * Provide some support for managing instrumentors and the singleton instance of
 * an instrumentation service.
 * @version 2.0
 */
public abstract class InstrumentationServiceBaseImpl implements InstrumentationService
{
	protected String methodSignature = null;
	protected int direction = 0;
	public String name;
	protected static InstrumentationService instance;
    protected static String instrumentationServiceImplClass = "com.cboe.infrastructureServices.instrumentationService.InstrumentationServiceImpl";
	protected Hashtable instrumentors;
	/**
	 * The class specified by this parameter must provide a public constructor.
	 * @param className The fully qualified name of an implementor of the InstrumentationService.
	 */
    public static void setServiceImplClassName(String className)
    {
        instrumentationServiceImplClass = className;
    }
    /**
     * Instance of this class should be created via the getInstance() method.
     */
    InstrumentationServiceBaseImpl()
    {
	}
	/** 
	   @roseuid 365B8BFD004B
	 */
	public int countInstrumentors()
	{
		return getInstrumentors().size();
	}
	/**
	   @roseuid 3658E5EA01A3
	 */
	public boolean deleteInstrumentor(String instrumentorName)
	{
		if (!getInstrumentors().containsKey(instrumentorName))
		{
		    System.out.println("Instrumentor " + instrumentorName + " not found");
		    return false;
		} 
		instrumentors.remove(instrumentorName);
		return true;
	}
	/**
	   @roseuid 365B8CFE025D
	 */
	public Enumeration getInstrumentorNames()
	{
	    return getInstrumentors().keys();
	}
	/**
	 */
	public static InstrumentationService getInstance()
	{
		 if (instance == null)
		 {
		    try
		    {
			    Class c = Class.forName(instrumentationServiceImplClass);
			    instance = (InstrumentationService)c.newInstance();
			}
			catch (Exception e)
			{
				FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm, "InstrumentationService.getInstance", "Failed to create the instrumentation service.", e);
			}
		 }
		 return instance;
	}
	/**
	 * Return an instrumentor if one with the name exists otherwise create one.
	 * @roseuid 3655C63D0098
	 */
	public abstract Instrumentor getInstrumentor(String instrumentorName);
	/**
	   @roseuid 3655C5B603A1
	 */
	public Hashtable getInstrumentors()
	{
	    if (instrumentors == null )
	    {
	        instrumentors = new Hashtable();
	    }
		return instrumentors;
	}
	/**
	   Return the name 
	   @roseuid 3658CDBF03AF
	 */
	public String getName()
	{
		return name;
	}
	/**
	 * Configure the instrumentation service.
	   @roseuid 3656354F0328
	 */
	public boolean initialize(ConfigurationService configService)
	{
		return true;
	}
	/**
	   @roseuid 3655C4F000C1
	 */
	public void setInstrumentationPolicy(InstrumentationPolicyValue value)
	{
	}
	/**
	   Set the name 
	   @roseuid 3658CDBF039B
	 */
	public void setName(String aName)
	{
	    name = aName;
	}
	/**
	 * @author Murali Yellepeddy
	 */
	public synchronized void setMethodSignature(String signature)
	{
		this.methodSignature = signature;
	}
	/**
	 * @author Murali Yellepeddy
	 */
	public synchronized String getMethodSignature()
	{
		return methodSignature;
	}
	/**
	 * @author Murali Yellepeddy
	 */
	public synchronized void setDirection(int direction)
	{
		this.direction = direction;
	}
	/**
	 * @author Murali Yellepeddy
	 */
	public synchronized int direction()
	{
		return direction;
	}
	/**
	 * @author Murali Yellepeddy
	 */
	public void sendMethodEvent(long entityID)
	{
		if (methodSignature != null)
		{
		}
	}
	/**
	 * @author Murali Yellepeddy
	 */
	public void sendQueueEvent(long queueID, int queueSize,long entityID)
	{
	}

	public abstract EventChannelInstrumentorFactory getEventChannelInstrumentorFactory();

	public abstract HeapInstrumentorFactory getHeapInstrumentorFactory();

	public abstract NetworkConnectionInstrumentorFactory getNetworkConnectionInstrumentorFactory();

	public abstract QueueInstrumentorFactory getQueueInstrumentorFactory();

	public abstract ThreadPoolInstrumentorFactory getThreadPoolInstrumentorFactory();

	public abstract MethodInstrumentorFactory getMethodInstrumentorFactory();


	public abstract EventChannelInstrumentorFactory getAggregatedEventChannelInstrumentorFactory();

	public abstract NetworkConnectionInstrumentorFactory getAggregatedNetworkConnectionInstrumentorFactory();

	public abstract QueueInstrumentorFactory getAggregatedQueueInstrumentorFactory();

	public abstract ThreadPoolInstrumentorFactory getAggregatedThreadPoolInstrumentorFactory();

	public abstract MethodInstrumentorFactory getAggregatedMethodInstrumentorFactory();

}
