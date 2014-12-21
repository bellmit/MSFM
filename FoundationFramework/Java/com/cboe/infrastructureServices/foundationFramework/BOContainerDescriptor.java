package com.cboe.infrastructureServices.foundationFramework;

import com.cboe.infrastructureServices.foundationFramework.policies.*;
/**
 * A BOContainerDescriptor manages a set of policies to control its TaskManager behavior.  The ThreadPolicy and InstrumentationPolicy are used as on/off switches.  The rest of the policies have more elaborate behavior.
 * @version 1.3
 */
public class BOContainerDescriptor
{
	private ThreadPolicy threadPolicy;
	private TransactionPolicy transactionPolicy;
	public String name;
	private SecurityPolicy securityPolicy;
	private Class dbAdapterClass;
	public BOContainer boContainer;
	protected String loggingServiceComponentName = null;
	
	public String containerImpl = "com.cboe.infrastructureServices.foundationFramework.BOContainer";
	/**
	 * @param name A valid logging service component or null.
	 */
	public void setLoggingServiceComponentName(String name)
	{
		loggingServiceComponentName = name;
	}
	/**
	 */
	public String getLoggingServiceComponentName()
	{
		return loggingServiceComponentName;
	}
	/**
	 * @author Dave Hoag
	 * @return java.lang.String
	 */
	public String getContainerImpl()
	{
		return containerImpl;
	}
	/**
	   @roseuid 36534FAA01AE
	 */
	public String getName()
	{
		return name;
	}
	/**
	   @roseuid 362B8D5D0234
	 */
	public ThreadPolicy getThreadPolicy()
	{
		return ThreadPolicy.REUSE_THREAD;
	}
	/**
	   @roseuid 362B89CB02B2
	 */
	public TransactionPolicy getTransactionPolicy()
	{
		return TransactionPolicy.OBJECT_MANAGED;
	}
	/**
	* @author Dave Hoag
	* @param newValue java.lang.String
	*/
	public void setContainerImpl(String newValue)
	{
		this.containerImpl = newValue;
	}
	/**
	   @roseuid 36547BD703D5
	 */
	public void setDBAdapterClassName(String className) {	}
	/**
	   @roseuid 36534FB503E4
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	/**
	   @roseuid 362B8D6602D7
	 */
	public void setSecurityPolicy(SecurityPolicyValue value)
	{
	}
	/**
	   @roseuid 362B8D4F0053
	 */
	public void setThreadPolicy(ThreadPolicyValue value)
	{
	}
	/**
	   @roseuid 362B899700E1
	 */
	public void setTransactionPolicy(TransactionPolicy value)
	{
		transactionPolicy = value; 
	}
}