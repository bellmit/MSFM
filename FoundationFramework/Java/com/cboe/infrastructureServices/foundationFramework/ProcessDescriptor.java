package com.cboe.infrastructureServices.foundationFramework;

//import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.policies.TransactionPolicy;
import com.cboe.infrastructureServices.loggingService.LogService;
import com.cboe.infrastructureServices.loggingService.MsgCategory;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException;
 
/**
 * The ProcessDescriptor has the now how to create the BOHomeDescriptors and BOContainerDescriptors
 * from the configuration service.
 *
 * @author Dave Hoag
 * @version 3.3
 */
public class ProcessDescriptor implements ConfigurationConstants
{
	public String name;
	public Properties properties;

	//public FoundationFramework foundationFramework;
	Hashtable boContainerDescriptors;
	Vector boHomeDescriptors;
	
	/**
	 * Before this class is really useful, a call to 'intitialze()' must made.
	 * @see #initialize(java.lang.String)
	 */
	public ProcessDescriptor() 
	{
	}
	/**
	 * Using the FoundationFramework configuration service, obtain the information necessary
	 * for the creation of homes and containers.
	 * Two techniques are available to define BOHomesDescriptors and BOContainerDescriptors.
	 * Either the intialHome/next pattern or a listOfHomes=one,two,three pattern.
	 * 
	 * @param processName A string to prepend to all of the properties for which we are looking.
	 */
	public void initialize(final String processName) throws CBOELoggableException
	{
		LogService logSvc = FoundationFramework.getInstance().getDefaultLogService();
		
		boContainerDescriptors = new Hashtable();
		boHomeDescriptors = new Vector();
		setName(processName);
		  
		ConfigurationService svc = FoundationFramework.getInstance().getConfigService();
		//Initialize information from appConfigService.

		try
		{
			createListOfContainerDescriptors(processName, svc);
		}
		catch (NoSuchPropertyException ex1)
		{
		    logSvc.log(MsgPriority.low, MsgCategory.systemNotification,"ProcessDescriptor.initialize",  "No listOfContainers. Trying the initialize/next pattern."); 
			
			try
			{ 
				String containerName = svc.getProperty(processName + '.' + INITCONTAINER);
				logSvc.log(MsgPriority.low, MsgCategory.debug, "ProcessDescriptor.initialize", "ProcessDescriptor:Initial Container Name:" + containerName);
				createContainerDescriptors( processName, containerName, svc);
			}
			catch (NoSuchPropertyException ex) 
			{
		    	logSvc.log(MsgPriority.low, MsgCategory.systemNotification,"ProcessDescriptor.initialize",  "No containers for this server.");
        	}
        }
		
		try
		{
			createListOfHomeDescriptors(processName, svc);
		}
		catch (NoSuchPropertyException ex1)
		{
		    logSvc.log(MsgPriority.low, MsgCategory.systemNotification, "ProcessDescriptor.initialize", "No listOfHomes. Trying the initialize/next pattern."); 
			try 
			{ 
				String homeName = svc.getProperty(processName + '.' + INITHOME);
				createHomeDescriptors( processName, homeName, svc);
			}
			catch (NoSuchPropertyException ex) 
			{
		    	logSvc.log(MsgPriority.low, MsgCategory.systemNotification, "ProcessDescriptor.initialize", "No Homes for this server."); 
        	}
        }
	}
	/**
	 * Look for a list of BOHomeDescriptor names. If the listOfHomes property can not be found, throw an exception.
	 * This is different than the createHomeDescriptors method in that it doesnt use the 
	 * initial/next pattern.
	 * 
	 * @param processName The root value of the property keys.
	 * @param svc The foundation framework configuration service.
	 * @exception com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException Suspected of not having the listOfHomes property.
	 * @see #createHomeDescriptors
	 */
	protected void createListOfHomeDescriptors(String processName, ConfigurationService svc) throws NoSuchPropertyException
	{
		String homeList = svc.getProperty(processName + '.' + HOMELIST);
		StringTokenizer st = new StringTokenizer(homeList, ", \t\n\r");
		while(st.hasMoreTokens())
		{
			String homeDescriptorName = st.nextToken().trim();
			String propertyKey = processName + '.' +  homeDescriptorName  + '.';
			BOHomeDescriptor bohd = createHomeDescriptor(homeDescriptorName, svc, propertyKey );
			addBOHomeDescriptor(bohd);
		}
	}
	/**
	 * Look for a list of BOContainerDescriptor names. If the listOfContainers property can not be found, throw an exception.
	 * This is different than the createContainerDescriptors method in that it doesnt use the 
	 * initial/next pattern.
	 * 
	 * @param processName The root value of the property keys.
	 * @param svc The foundation framework configuration service.
	 * @exception com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException Suspected of not having the listOfHomes property.
	 * @see #createContainerDescriptors
	 */
	protected void createListOfContainerDescriptors(String processName, ConfigurationService svc) throws NoSuchPropertyException, CBOELoggableException
	{
		String containerList = svc.getProperty(processName + '.' + CONTAINERLIST);
		StringTokenizer st = new StringTokenizer(containerList, ",");
		while(st.hasMoreTokens())
		{
			String containerDescriptorName = st.nextToken().trim();
			String propertyKey = processName + '.' +  containerDescriptorName  + '.';
			BOContainerDescriptor bohd = createContainerDescriptor(containerDescriptorName, svc, propertyKey );
			addBOContainerDescriptor(bohd);
		}
	}
	/**
	 * Add a BOContainerDescriptor to the container list.
	 * The BOContainerDescriptor describes the properties of a BOContainer.
	 * 
	 * @param boContainerDescriptor A completed BOContainerDescriptor.
	 * @exception com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException If the container with the same name already exists.
	 */
	public void addBOContainerDescriptor(BOContainerDescriptor boContainerDescriptor) throws CBOELoggableException
	{
		if (!boContainerDescriptors.containsKey(boContainerDescriptor.getName()))
		{
			boContainerDescriptors.put(boContainerDescriptor.getName(), boContainerDescriptor);
		}
		else
		{
			throw new CBOELoggableException("Duplicate Container name", MsgPriority.medium);
		}
	}
	/**
	 * Add a BOHomeDescriptor to the container list.
	 * If a BOHomeDescriptor is already in the collection of BOHomeDescriptors with the same name
	 * the original BOHomeDescriptor is discarded and replaced with the new one.
	 * @param boHomeDescriptor An object that describes a BOHome.
	 */
	public void addBOHomeDescriptor(BOHomeDescriptor boHomeDescriptor)
	{
		BOHomeDescriptor orig = getHomeDescriptor(boHomeDescriptor.getName());
		if(orig != null)
		{
			boHomeDescriptors.removeElement(orig);
		}
		
		boHomeDescriptors.addElement(boHomeDescriptor);
	}
	/**
	 * Find the BOHomeDescriptor with the provided name.
	 * 
	 * @param name String The name of a previously defined BOHomeDescriptor
	 * @return BOHomeDescriptor with the parameter for a name or null if none are found.
	 */
	protected BOHomeDescriptor getHomeDescriptor(String name)
	{
		Enumeration e = boHomeDescriptors.elements();
		while(e.hasMoreElements())
		{
			BOHomeDescriptor obj = (BOHomeDescriptor)e.nextElement();
			if(obj.getName().equals(name)) return obj;
		} 
		return null;
	}
	/**
	 * Use the intial/next pattern to recursively define all of the container descriptors.
	 * 
	 * @param containerName java.lang.String
	 * @param processName java.lang.String
	 * @param svc The FoundationFramework configuration service.
	 * @exception com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException
	 */
	protected void createContainerDescriptors(String processName, String containerName, ConfigurationService svc) throws CBOELoggableException 
	{
		String propertyKey = processName + '.' + containerName + '.';
		BOContainerDescriptor bocd = createContainerDescriptor(containerName, svc, propertyKey);
		addBOContainerDescriptor(bocd);

		try
		{
			String value = svc.getProperty(propertyKey + NEXT);
			if (value != null)
			{
				createContainerDescriptors(processName, value, svc);
			}
		}
		catch (NoSuchPropertyException ex) {}
	}
	/**
	 * This will look in the ConfigurationService for properties of the following formats:
	 * processName.containerName.containerImpl= com.cboe.infrastructureServices.foundationFramework.BOContainer
	 * processName.containerName.transactionPolicy=OBJECT_MANAGED
	 * 
	 * The ConfigurationService is assumed to be initialized.
	 * @param containerName A name that uniquely identified this container.
	 * @param svc The FoundationFramework ConfigurationService.
	 * @param propertyKey The key to preprend to the property names. The key is assumed to end with a '.'.
	 */
	public BOContainerDescriptor createContainerDescriptor(final String containerName, final ConfigurationService svc, final String propertyKey ) throws CBOELoggableException 
	{
		LogService logSvc = FoundationFramework.getInstance().getDefaultLogService();
		BOContainerDescriptor bocd = new BOContainerDescriptor();
		bocd.setName(containerName);

		String value ="";
		logSvc.log(MsgPriority.low, MsgCategory.systemNotification, "ProcessDescriptor.createContainerDescriptor", "Creating container descriptor: " + containerName); 
		try
		{ 
			value = svc.getProperty(propertyKey + CONTAINER_IMPL);
			bocd.setContainerImpl(value);
		}
		catch (NoSuchPropertyException ex) 
		{
		    logSvc.log(MsgPriority.low, MsgCategory.debug, "ProcessDescriptor.createContainerDescriptor","Container descriptor " + containerName + " has no containerImpl." ); 
        }
		
		try
		{
			value = svc.getProperty(propertyKey + LOG_COMPONENT);
			bocd.setLoggingServiceComponentName(value);
		}
		catch (NoSuchPropertyException ex) 
		{
		    logSvc.log(MsgPriority.low, MsgCategory.debug,"ProcessDescriptor.createContainerDescriptor", "Container descriptor " + containerName + " has no logServiceComponent." ); 
        }

		try
		{ 
			value = svc.getProperty(propertyKey + TRANSACTION_POLICY);
			java.lang.reflect.Field f = TransactionPolicy.class.getDeclaredField(value);

			TransactionPolicy transactionPolicy = (TransactionPolicy)f.get(null);
			bocd.setTransactionPolicy(transactionPolicy);
		}
		catch (NoSuchFieldException ex)
		{
		    throw new CBOELoggableException(ex.toString() ,MsgPriority.low);
        }
		catch (IllegalAccessException ex) 
		{
		    throw new CBOELoggableException(ex.toString() ,MsgPriority.low);
        }
		catch (NoSuchPropertyException ex) {/* Use default TransactionPolicy */ }
		return bocd;
	}
	/**
	 * Use a '.next' property to determine the next homeDescriptor to create. This is a recursive method
	 * that will continue to create BOHomeDescriptors as long as .next property is found.
	 * 
	 * @param processName java.lang.String
	 * @param homeDescriptorName a Name that uniquely identifies the home descriptor.
	 * @param svc The FoundationFramework ConfigurationService.
	 * @exception com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException
	 */
	protected void createHomeDescriptors(String processName, String homeDescriptorName,ConfigurationService svc) throws CBOELoggableException
	{
		String propertyKey = processName + '.' + homeDescriptorName + '.';
		
		BOHomeDescriptor bocd = createHomeDescriptor( homeDescriptorName, svc, propertyKey);
		addBOHomeDescriptor(bocd);

		try
		{
			String value = svc.getProperty(propertyKey + NEXT);
			if(value != null)
			{
				createHomeDescriptors(processName, value, svc);
			}
		}
		catch (NoSuchPropertyException ex) { /* No more home definitions */}
	}
	/**
	 * The actual creation of a BOHomeDescriptor occurs in this method.
	 * This will look in the ConfigurationService for properties of the following formats:
	 * processName.homeDescName.homeImpl= com.cboe.infrastructureServices.foundationFramework.examples.ProductHomeImpl
	 * processName.homeDescName.homeName=ProductHome
	 * processName.homeDescName.containerDescName=OneContainer
	 * 
	 * @param homeDescriptorName a Name that uniquely identifies the home descriptor.
	 * @param svc The FoundationFramework ConfigurationService.
	 * @param propertyKey The key to preprend to the property names. The key is assumed to end with a '.'.
	 */
	protected BOHomeDescriptor createHomeDescriptor(final String homeDescriptorName, final ConfigurationService svc, final String propertyKey ) 
	{
		LogService logSvc = FoundationFramework.getInstance().getDefaultLogService();
		BOHomeDescriptor bohd = new BOHomeDescriptor();
		bohd.setName(homeDescriptorName);

		String value = null;
		logSvc.log(MsgPriority.low, MsgCategory.systemNotification, "ProcessDescriptor.createHomeDescriptor","Creating home descriptor: " + homeDescriptorName); 
		try
		{
			value = svc.getProperty(propertyKey + HOME_IMPL);
			bohd.setBOHomeImplClassName(value);
		} 
		catch (NoSuchPropertyException ex) 
		{
		    logSvc.log(MsgPriority.medium, MsgCategory.systemAlarm,"ProcessDescriptor.createHomeDescriptor", "Home descriptor " + homeDescriptorName + " has no homeImpl property." ); 
        }

		try
		{
			value = svc.getProperty(propertyKey + HOME_NAME);
			bohd.setBOHomeName(value);
		}
		catch (NoSuchPropertyException ex) 
		{
		    logSvc.log(MsgPriority.medium, MsgCategory.systemAlarm,"ProcessDescriptor.createHomeDescriptor", "Home descriptor " + homeDescriptorName + " has no homeName property." ); 
        }

		try
		{
			value = svc.getProperty(propertyKey + CONTAINER_DESC_NAME);
			bohd.setBOContainerDescriptorName(value);
		}
		catch (NoSuchPropertyException ex) 
		{
		    logSvc.log(MsgPriority.medium, MsgCategory.systemAlarm, "ProcessDescriptor.createHomeDescriptor","Home descriptor " + homeDescriptorName + " has no containerDescName property." ); 
        }

		try
		{
			value = svc.getProperty(propertyKey + LOG_COMPONENT);
			bohd.setLoggingServiceComponentName(value);
		}
		catch (NoSuchPropertyException ex) 
		{
		    logSvc.log(MsgPriority.low, MsgCategory.debug, "ProcessDescriptor.createHomeDescriptor","Home descriptor " + homeDescriptorName + " has no logServiceComponent." ); 
        }

		try
		{
			value = svc.getProperty(propertyKey + INTERCEPTOR_NAME);
			bohd.setBOInterceptorName(value);
		}
		catch (NoSuchPropertyException ex) 
		{
		    logSvc.log(MsgPriority.low, MsgCategory.debug, "ProcessDescriptor.createHomeDescriptor","Home descriptor " + homeDescriptorName + " has no interceptorName." ); 
        }
		try
		{
			value = svc.getProperty(propertyKey + INTERCEPTOR_IMPL);
			bohd.setBOInterceptorClassName(value);
		}
		catch (NoSuchPropertyException ex) 
		{
		    logSvc.log(MsgPriority.low, MsgCategory.debug,"ProcessDescriptor.createHomeDescriptor", "Home descriptor " + homeDescriptorName + " has no interceptorImpl." ); 
        }
        return bohd;
	}
	/**
	 * Return a BOContainerDescriptor with the supplied Name
	 * 
	 * @param name The name of a previously added BOContainerDescriptor
	 * @return BOContainerDescriptor created from properties in the configuration service.
	 * @see #addBOContainerDescriptor
	 */
	public BOContainerDescriptor getBOContainerDescriptor(String name)
	{
		return (BOContainerDescriptor )boContainerDescriptors.get(name);
	}
	/**
	 * Just get the BOContainerDescriptor names.
	 * 
	 * @return Enumeration of Strings that are the BOContainerDescriptor names.
	 */
	public Enumeration getBOContainerDescriptorNames()
	{
		return boContainerDescriptors.keys();
	}

	/**
	 * The full list of all defined BOContainerDescriptors.
	 * 
	 * @return Collection of all BOContainerDescriptors
	 */
	public Enumeration getBOContainerDescriptors()
	{
		return boContainerDescriptors.elements();
	}
	/**
	 * Return a BOHomeDescriptor with the supplied Name
	 * 
	 * @param name BOHomeDescriptor created from properties in the configuration service.
	 * @return A BOHomeDescriptor.
	 * @see #addBOHomeDescriptor
	 */
	public BOHomeDescriptor getBOHomeDescriptor(String name)
	{
		return getHomeDescriptor(name);
	}

	/**
	 * The full list of all defined BOHomeDescriptors.
	 * 
	 * @return Collection of all BOHomeDescriptors
	 */
	public Enumeration getBOHomeDescriptors()
	{
		return boHomeDescriptors.elements();
	}
	/**
	 * The name of the process is the root of all properties being defined.
	 * 
	 * @return String of the name of the ProcessDescriptor
	 */
	public String getName()
	{
		return name;
	}
	/**
	 * Set the name of the ProcessDescriptor
	 * The name of the process is the root of all properties being defined.
	 * @param String of the name of the ProcessDescriptor
	 */
	public void setName(String aName)
	{
		name = aName;
	}
	/**
	 * Unit test of the ProcessDescriptor.
	 */
	public static class UnitTest extends TestCase
	{
		java.util.Properties props;
		LogService logSvc = FoundationFramework.getInstance().getDefaultLogService();
	    
	    public UnitTest(String name) {
			super(name);
		}
	    
		protected ConfigurationService getTestProperties()
		{
			props = new java.util.Properties();

			props.put("processDescriptor." + HOMELIST, "one,two,three");
			props.put("processDescriptor." + INITHOME, "one");
			props.put("processDescriptor.one." + NEXT, "three");
			props.put("processDescriptor.one." + HOME_NAME, "oneHomeName");
			props.put("processDescriptor.three." + NEXT, "two");
			props.put("processDescriptor.two." + LOG_COMPONENT, "myLogComponent");

			props.put("processDescriptor." + CONTAINERLIST, "oneCont,twoCont,threeCont");
			props.put("processDescriptor." + INITCONTAINER, "oneCont");
			props.put("processDescriptor.oneCont." + NEXT, "twoCont");
			props.put("processDescriptor.oneCont." + LOG_COMPONENT, "myLogComponent");
			props.put("processDescriptor.twoCont." + NEXT, "threeCont");

			ConfigurationService cf = FoundationFramework.getConfigurationService(props);
			cf.initialize(null, 0);
			return cf;
		}
		public void testListOfContainers() throws NoSuchPropertyException, CBOELoggableException
		{
			ProcessDescriptor pd = new ProcessDescriptor();
			pd.boContainerDescriptors = new Hashtable();
			pd.boHomeDescriptors = new Vector();

			ConfigurationService cf = getTestProperties();

			pd.createListOfContainerDescriptors("processDescriptor", cf);
			assertEquals("Invalid number of container descriptors.", 3 ,pd.boContainerDescriptors.size());
			Enumeration anEnum = pd.getBOContainerDescriptors();
			int i = 0;
			String [] list = { "oneCont", "twoCont", "threeCont" } ;
			while(anEnum.hasMoreElements())
			{
				i++;
				BOContainerDescriptor obj = (BOContainerDescriptor)anEnum.nextElement();
				assertEquals("Wrong container returned at name : " + obj.getName(), obj, pd.getBOContainerDescriptor(obj.getName()));
				if(obj.getName().equals("oneCont"))
				{
					assertEquals("Failed to set container property.", "myLogComponent", obj.getLoggingServiceComponentName());
				}
			} 
			assertEquals("Wrong number or no containers found in enumeration!", 3, i);
		}
		public void testListOfHomes() throws NoSuchPropertyException
		{
			ProcessDescriptor pd = new ProcessDescriptor();
			pd.boContainerDescriptors = new Hashtable();
			pd.boHomeDescriptors = new Vector();
			
			pd.createListOfHomeDescriptors("processDescriptor", getTestProperties());
			assertEquals("Invalid number of home descriptors.", 3 ,pd.boHomeDescriptors.size());
			Enumeration anEnum = pd.getBOHomeDescriptors();
			int i = 0;
			String [] list = { "one", "two", "three" } ;
			while(anEnum.hasMoreElements())
			{
				BOHomeDescriptor obj = (BOHomeDescriptor)anEnum.nextElement();
				assertEquals("Invalid order of homes!", list[i++], obj.getName());
				assertEquals("Wrong home returned at name : " + obj.getName(), obj, pd.getBOHomeDescriptor(obj.getName()));
				if(obj.getName().equals("two"))
				{
					assertEquals("Failed to set container property.", "myLogComponent", obj.getLoggingServiceComponentName());
				}
			} 
			assertEquals("Wrong number or no homes found in enumeration!", 3, i);
		}
		public void testHomeNext() throws NoSuchPropertyException, CBOELoggableException
		{
			ProcessDescriptor pd = new ProcessDescriptor();
			pd.boContainerDescriptors = new Hashtable();
			pd.boHomeDescriptors = new Vector();
			
			pd.createHomeDescriptors("processDescriptor", "one", getTestProperties());
			assertEquals("Invalid number of home descriptors.", 3 ,pd.boHomeDescriptors.size());
			Enumeration anEnum = pd.getBOHomeDescriptors();
			int i = 0;
			String [] list = { "one", "three", "two" } ;
			while(anEnum.hasMoreElements())
			{
				BOHomeDescriptor obj = (BOHomeDescriptor)anEnum.nextElement();
				assertEquals("Invalid order of homes!", list[i++], obj.getName());
				assertEquals("Wrong home returned at name : " + obj.getName(), obj, pd.getBOHomeDescriptor(obj.getName()));
				if(obj.getName().equals("two"))
				{
					assertEquals("Failed to set container property.", "myLogComponent", obj.getLoggingServiceComponentName());
				}
			} 
			assertEquals("Wrong number or no homes found in enumeration!", 3, i);
		}
		public void testContainerNext() throws NoSuchPropertyException, CBOELoggableException
		{
			ProcessDescriptor pd = new ProcessDescriptor();
			pd.boContainerDescriptors = new Hashtable();
			pd.boHomeDescriptors = new Vector();

			ConfigurationService cf = getTestProperties();

			pd.createContainerDescriptors("processDescriptor", "oneCont", getTestProperties());
			assertEquals("Invalid number of container descriptors.", 3 ,pd.boContainerDescriptors.size());
			Enumeration anEnum = pd.getBOContainerDescriptors();
			int i = 0;
			String [] list = { "oneCont", "twoCont", "threeCont" } ;
			while(anEnum.hasMoreElements())
			{
				i++;
				BOContainerDescriptor obj = (BOContainerDescriptor)anEnum.nextElement();
				assertEquals("Wrong container returned at name : " + obj.getName(), obj, pd.getBOContainerDescriptor(obj.getName()));
				if(obj.getName().equals("oneCont"))
				{
					assertEquals("Failed to set container property.", "myLogComponent", obj.getLoggingServiceComponentName());
				}
			} 
			assertEquals("Wrong number or no containers found in enumeration!", 3, i);
		}
		public void testInitialize() throws CBOELoggableException
		{
			ConfigurationService cf = getTestProperties();
        	FoundationFramework.getInstance().configService = cf;
        	ProcessDescriptor pd = new ProcessDescriptor();
        	pd.initialize("processDescriptor");
			assertEquals("Invalid number of container descriptors.", 3 , pd.boContainerDescriptors.size());
			assertEquals("Invalid number of home descriptors.", 3 ,pd.boHomeDescriptors.size());
			
			props.remove("processDescriptor." + HOMELIST);
        	pd = new ProcessDescriptor();
        	pd.initialize("processDescriptor");
			assertEquals("Invalid number of container descriptors.", 3 ,pd.boContainerDescriptors.size());
			assertEquals("Invalid number of home descriptors.", 3 ,pd.boHomeDescriptors.size());
		}
		/**
		 * No arguments will run all tests. Each argument is a test name.
		 */
        public static void main(String [] args)
        {
        	TestSuite suite = new TestSuite(UnitTest.class);
            TestRunner.run(suite); 
        }
		public void setUp()
		{
			try {
				super.setUp();
			}
			catch (Exception e){
			    logSvc.log(MsgPriority.medium, MsgCategory.systemAlarm, "SetUp failed in Unit Test" + e ); 
			}
        	if(FoundationFramework.getInstance().getName() == null)
        	{
        		FoundationFramework.initializeForTest("processDescriptor", null, null);
        	}
        }
	}
}
