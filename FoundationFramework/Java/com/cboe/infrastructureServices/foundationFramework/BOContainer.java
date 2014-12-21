package com.cboe.infrastructureServices.foundationFramework;

//import com.cboe.infrastructureServices.foundationFramework.examples.*;
import com.cboe.infrastructureServices.foundationFramework.policies.*;
import java.util.*;

/**
 * A BOContainer is responsible for containment of service  and  interceptor objects.  The BOContainers  behavior is determined by the policy values in its maintained in its BOContainerDescriptor.
 * For now, most of the policies are not used
 *
 * @version 1.4
 * @author Dave Hoag
 */
public class BOContainer extends FrameworkComponentImpl
{
	private String name;
	private ThreadPolicy threadPolicy;
	private TransactionPolicy transactionPolicy;
	private ExceptionPolicy exceptionPolicy;
	public BOContainerDescriptor containerDescriptor;
	public Hashtable boHomes;
	/**
	 * @param BOContainerDescriptor The configuration information from which this was created.
	 */
	public void setBOContainerDescriptor(BOContainerDescriptor boc)
	{
		containerDescriptor = boc;
	}
	/**
	 * The component name as specified by the configuration.
	 * @return Sting the Logging Service component name specified in the containerDescriptor...or null.
	 */
	public String getComponentName()
	{
	    String result;
		if(containerDescriptor != null)
		{
			result =  containerDescriptor.getLoggingServiceComponentName();
	    }
	    else
	    {
	        result = null;
	    }
		return result;
	}
	/**
	 * The container MUST be notified of every BOObject created in that container.
	 * @param home BOHome object responsible for the BObject
	 * @param bo BObject being managed.
	 */
	public void addBObject(BOHome home, BObject bo)
	{
		bo.associateHome(home);
	}
	/** 
	 * The default constructor will be invoked by the FoundationFramework.
	 * Every BOContainer implementation MUST have a public default constructor.
	 * Very little code should be in this method.
	 * @see #initialize()
	 */
	public BOContainer()
	{
		//setSmaType("BOContainer");
    }
    /**
     */
    public String getSmaType()
    {
        return FoundationFramework.getInstance().getFullName() + ".BOContainer";
    }
	/**
	 * Add a new BOHome to this container.
	 * @param aName The name of the BOHome
	 * @param home BOHome object to add.
	 */
	public void addBOHome(String aName, BOHome home)
	{
		getBOHomes().put(aName, home);
	}
	/**
	 * @return Hashtable keys of BOHome names and values of BOHomes.
	 */
	public Hashtable getBOHomes()
	{
		if (boHomes  == null)
		{
			boHomes = new Hashtable();
		}
		return boHomes;
	}
	/**
	 * @roseuid 365B27E801CD
	 * @return Enumeration of Strings of all BOHomes added to this container.
	 */
	public Enumeration getBOHomesNames()
	{
		return boHomes.keys();
	}
	/**
	 * An unused property.
	 * @roseuid 366065890378
	 */
	public ExceptionPolicy getExceptionPolicy(TransactionPolicy policy)
	{
		return exceptionPolicy;
	}
	/**
	 * A unique name identifying this container.
	 * @roseuid 362DFC1402B7
	 */
	public String getName()
	{
		return name;
	}
	/**
	 * An unused property.
	 * @roseuid 3660653F02B3
	 */
	public ThreadPolicy getThreadPolicy()
	{
		return threadPolicy;
	}
	/**
	 * An unused property.
	 * @roseuid 3660654303D2
	 */
	public TransactionPolicy getTransactionPolicy()
	{
		return transactionPolicy;
	}
	/**
	* Called immediately after instantiation by the FoundationFramework.
	* This is where any initialization code belongs.
	* 
	*/
	public void initialize()
	{
		setParentComponent(FoundationFramework.getInstance());
	}
	/**
	 * An unused property.
	 * @roseuid 3658E55600A6
	 */
	public void setExceptionPolicyValue(ExceptionPolicy value)
	{
		exceptionPolicy = value;
	}
	/**
	 * @roseuid 362DFC220113
	 */
	public void setName(String name)
	{
		setSmaName(name); //For containers, these are one in the same
		this.name = name;
	}
	/**
	 * An unused property.
	 * @roseuid 3658E3D40277
	 */
	public void setThreadPolicy(ThreadPolicy value)
	{
		threadPolicy = value;
	}
	/**
	 * An unused property.
	 * @roseuid 3658E48D0269
	 */
	public void setTransactionPolicy(TransactionPolicy value)
	{
		transactionPolicy = value;
	}
}
