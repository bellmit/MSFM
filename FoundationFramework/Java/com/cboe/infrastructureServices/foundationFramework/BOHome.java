package com.cboe.infrastructureServices.foundationFramework;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import com.cboe.infrastructureServices.loggingService.MsgCategory;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException;
/**
 * The BOHome interface utilizes find methods to locate BOInterceptors and create methods to instantiate BOInterceptors and BOInstances.
 * For each class of Business Object (BObject) and BOInterceptor, there is one BOHome.
 *
 * @version 4.5
 * @author Dave Hoag
 */
public class BOHome extends FrameworkComponentImpl
{
	protected String name;
	protected Hashtable businessObjects;
	public BOContainer boContainer;
	protected Hashtable boInterceptors;
	protected BOHomeDescriptor homeDescriptor;
	//The following data members cache data
	transient private String componentName;
	transient private String brokerName;
	transient private boolean lookupBrokerName = true; //Start as true
	transient private String fullName;
	transient private Constructor constructorWithArg;
	transient protected String managedResourceName;
	protected String specialFullName;
	/**
	 * Don't use this method. For use by the getFrameworkFullName() method.
	 */
	public void setSpecialFullName(String str)
	{
		specialFullName = str;
	}
	/**
	 * Check with my home descriptor, then check with the container.
	 * If no component name is specified at the home or component level, get the component name
	 * from the FoundationFramework.
	 *
	 * @return String componentName to be used when logging messages.
	 */
	public String getComponentName()
	{
		if(componentName == null)
		{
			String val = null;
			final BOHomeDescriptor desc = getBOHomeDescriptor();
			final BOContainer boContainer = getContainer();
			if(desc != null)
			{
				val = desc.getLoggingServiceComponentName();
			}
			if(val == null && boContainer != null)
			{
				val = boContainer.getComponentName();
			}
			if(val == null)
			{
				val = FoundationFramework.getInstance().getComponentName();
			}
			componentName = val;
		}
		return componentName ;
	}
	/**
	 * Set up configuration information about the BObject. This is a VERY important step
	 * if the whole Container/Home model is to work.
	 * Every BObject must be added to the container prior to use in any additional methods.
	 * A BObject that has not been added to a container will cause problems.
	 *
	 * @param bo A business object that is to exist within the CBOE infrastructure.
	 */
	public void addToContainer(BObject bo)
	{
		initializeFrameworkProperties(bo);
	}
	/**
	 * Associate the BOBject with this home. 
	 * Allow the Container to modify the BObject with any necessary properties.
	 * @param bo A business object that is to exist within the CBOE infrastructure.
	 */
	public void initializeFrameworkProperties(BObject bo)
	{
		boContainer.addBObject(this, bo);
		bo.setBOHome(this);
	}
	/**
	 * DefaultConstructor. Every home must contain a public default constructor. Very little
	 * code (if any) belongs in the default constructor.
	 * @see #initialize()
	 * @see #start()
	 */
	public BOHome()
	{
	}
	/**
	 * A BOHomeDescriptor describes configuration information about the home.
	 *
	 * @return BOHomeDescriptor The source of information that was used to create this home.
	 */
	public BOHomeDescriptor getBOHomeDescriptor()
	{
		return homeDescriptor;
	}
	/**
	 * A BOHomeDescriptor describes configuration information about the home.
	 *
	 * @param homeDescriptor BOHomeDescriptor The source of information that was used to create this home.
	 */
	public void setBOHomeDescriptor( BOHomeDescriptor homeDescriptor)
	{
		this.homeDescriptor = homeDescriptor;
	}
	/**
	 * Get the interceptors that have been 'managed' via this home.
	 *
	 * @return Hashtable Keys are the name of the interceptors.
	 * @see #manageObject( BOInterceptor )
	 */
	public Hashtable getBOInterceptors()
	{
		if (boInterceptors == null)
		{
		   boInterceptors = new Hashtable();
		}
		return boInterceptors;
	}
	/**
	 * Get the business object that has been managed via the manageObject method.
	 *
	 * @param aName The name of the BObject.
	 * @return BObject found at the parameter value.
	 * @see #manageObject( BOInterceptor )
	 */
	public Object getBusinessObject(String aName)
	{
		return getBusinessObjects().get(aName);
	}
	/**
	 * Get the business objects that have been 'managed' via this home.
	 *
	 * @return Hashtable with key values of the BObject names and values of BObjects
	 */
	public Hashtable getBusinessObjects()
	{
		if (businessObjects == null)
		{
		   businessObjects = new Hashtable();
		}
		return businessObjects;
	}
	/**
	 * The default container for this home. Every home must be associated with a container.
	 *
	 * @return BOContainer as specified by the HomeDescriptor
	 */
	public BOContainer getContainer()
	{
		return boContainer;
	}
	/**
	 * Return a String of the name of the BOHome
	 *
	 * @return String Name of the home.
	 */
	public String getName()
	{
		return name;
	}
	/**
	* This method is a place holder for the implmentation of any setup a home instance may need to perform.
	* Any calls within this method can use the FoundationFramework core services, but can not use any other
	* business services.
	*
	* @author Dave Hoag
	*/
	public void initialize()
	{
	}
	/**
	 * This step associates the BObject name with the interceptor and the instance.
	 * This step is NOT required, but simply acts as a utility for managing instances and interceptors.
	 * @assume The name has been set on the BObject. The name is represents a unique instance.
	 *
	 * @deprecated Should not be used anywhere because if you want to get rid of managed object you can't
	 * @param boi The interceptor for a BObject.
	 */
	protected void manageObject( BOInterceptor boi)
	{
	    String name = boi.getBObject().getName();
	    getBOInterceptors().put( name, boi);
	    getBusinessObjects().put( name, boi.getBObject());
	}
	/**
	 * Set the default container.
	 * Every BOHome must be associated with a container.
	 *
	 * @param aVal BOContainer default for this home.
	 */
	public void setContainer(BOContainer aVal)
	{
		boContainer = aVal;
		super.setParentComponent(aVal);
	}
	/**
	 * Set the name of the BOHome
	 *
	 * @param aString An arbitrary string value that should uniquely identify this home instance.
	 */
	public void setName(String aString)
	{
		this.name = aString;
	}
    /**
     */
    public String getSmaType()
    {
        String resultType = super.getSmaType();
        if(resultType == null)
        {
            String derivedName = getName();
            if(derivedName != null)
            {
                derivedName = "Global" + derivedName;
                String className = this.getClass().getName();
                int idx = className.lastIndexOf('.');
                className = className.substring(idx + 1);
                resultType = derivedName + "." + className;
            }
        }
        return resultType;
    }
	/**
	 * A place holder for subclass implementations. This is called when the process is being
	 * shutdown. The default behavior is to do nothing.
	 */
	public void shutdown()
	{
	}
	/**
	 * A place holder for subclass implementations. This is called when the process is being
	 * started. The default behavior is to do nothing.
	 */
	public void start()
	{
	}
	/**
	 * Configuration information is stored in the ConfigurationService. A path is necessary to
	 * get the properties related to this particular home.
	 * This is based upon the assumption that Homes will not switch containers once instantitated.
	 *
	 * @return String The path to use to determine property values in the configuration service.
	 */
	public String getFullName()
	{
		if(fullName == null)
		{
			String potentialResult = super.getFullName();
			if(potentialResult.equals(""))
			{
				FoundationFramework ff = FoundationFramework.getInstance();
				String tmp; //Use temp var to avoid multithreaded issues
				tmp = ff.getName();
				tmp += '.' + getContainer().getName();
				tmp += '.' + getName();
				fullName = tmp;
			}
			else
			{
				fullName = potentialResult;
			}
		}
		return fullName;
	}
	/**
	 * Find the name of the broker specified for objects created via this home.
	 * If none is specified, return null.
	 * This will search the fullName.persistentBrokerName, <processName>.<containerName>.persistentBrokerName
	 * and finally the <processName>.persistentBrokerName for the persistentBrokerName.
	 *
	 * @return String or null.
	 * @see #getFullName()
	 */
	public String findBrokerName()
	{
		if(lookupBrokerName)
		{
			brokerName = getFrameworkProperty( "persistentBrokerName", null);
			lookupBrokerName = false;
		}
		return brokerName;
	}
	/**
	 * A utility method that handles all of the necessary initialization of an newly create BObject.
	 * @param obj to use in constructor of the Interceptor. The object to manage.
	 * @return The reflectively created BOInterceptor for the provided BObject or null (if createInterceptor returns null).
	 * @exception java.lang.ClassNotFoundException The interceptor specified in configuration could not be found.
	 * @exception java.lang.IllegalAccessException
	 * @exception java.lang.InstantiationException
	 * @exception java.lang.ClassCastException
	 */
	public BOInterceptor initializeObject(BObject obj) throws ClassNotFoundException, IllegalAccessException, InstantiationException, ClassCastException
	{
		addToContainer(obj);
		BOInterceptor result = createInterceptor(obj);
		if ( result != null ) {
			manageObject(result);
		}
		else {
			FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.medium, MsgCategory.debug, "BObject [" + obj.getName() + "] createInterceptor() returned null, manageObject() method *not* done.");
		}

		return result;
	}
	/**
	 * A reflected mechanism to create BOInterceptors. Uses the interceptor class name
	 * as specified in the HomeDescriptor.
	 *
	 * @param obj to use in constructor. Or null if default constructor is to be used.
	 * @return A BOInterceptor
	 * @exception java.lang.ClassNotFoundException The interceptor specified in configuration could not be found.
	 * @exception java.lang.IllegalAccessException
	 * @exception java.lang.InstantiationException
	 * @exception java.lang.ClassCastException
	 */
	public BOInterceptor createInterceptor(BObject obj) throws ClassNotFoundException, IllegalAccessException, InstantiationException, ClassCastException
	{
		BOInterceptor result = null;
		Class c = null;
		String className = getBOHomeDescriptor().getBOInterceptorClassName();
		if ( className != null ) {
			c = Class.forName(className);
			if (obj != null) {
				try { // Log any exceptions with this constructor, but don't give up.
					Constructor con = getConstructorWithArg(c, obj.getClass());
					Object[] objs = new Object[1];
					objs[0] = obj;
					result = (BOInterceptor)con.newInstance(objs);
				}
				catch(java.lang.reflect.InvocationTargetException ite) {
					FoundationFramework.getInstance().getLogService(getComponentName()).log(MsgPriority.medium, MsgCategory.debug, "BOHome.createInterceptor", ite.toString(), ite.getTargetException());
				}
				catch(Exception e) {
					FoundationFramework.getInstance().getLogService(getComponentName()).log(MsgPriority.low, MsgCategory.debug, "BOHome.createInterceptor", e.toString(), e);
				}
			}

			if ( result == null ) { // Try the default constructor
				result = (BOInterceptor)c.newInstance();
			}
		}
		else{
			if(obj != null)
				throw new InstantiationException(" Interceptor ClassName is null for " + obj.getName());
			else
				throw new InstantiationException(" BObject passed is NULL and Interceptor Class Name is NULL ");
		}

		return result;
	}
	/**
	 * Used by createInterceptor to reflectively create the interceptors.
	 *
	 * @param c The interceptor class upon which we are searching for a constructor.
	 * @param arg The BOHome class that is argument to the constructor.
	 * @return Constructor The reflected constructor
	 * @exception java.lang.NoSuchMethodException The constrcutor could not be found.
	 */
	protected Constructor getConstructorWithArg(final Class c, final Class arg) throws NoSuchMethodException
	{
		if(constructorWithArg != null) return constructorWithArg;

	    Constructor [] cons = c.getDeclaredConstructors();
	    for(int i = 0; i < cons.length; ++i)
	    {
	        if(cons[i].getParameterTypes().length == 1)
	        {
	            if(cons[i].getParameterTypes()[0].isAssignableFrom(arg))
	            {
	            	constructorWithArg = cons[i];
	                return cons[i];
	            }
	        }
	    }
	    throw new NoSuchMethodException("Class " +  c + " does not contain (" + arg + ')');
	}
	/**
	 * Append ".<propertyName> to each element in the key looking for a broker name.
	 * ex.
	 * TraderServer.containerName.homeName could be the initial key.
	 * This method will look for a configuration setting of
	 * TraderServer.containerName.homeName.<propertyName> and
	 * TraderServer.containerName.<propertyName> and
	 * TraderServer.<propertyName>
	 * before throwing a NoSuchPropertyException
	 * @return The property value
	 */
	public String getProperty(String propertyName) throws NoSuchPropertyException
	{
		String result = getProperty(propertyName, null);
		if(result == null) throw new NoSuchPropertyException(propertyName);
		return result;
	}
 	/**
     * Used only for the SMA support.
	 * @param name If this home will have properties, the SMA implementation will need to know the managed resource name.
	 */
	public void setManagedResourceName(String name)
	{
		setSmaName(name);
		managedResourceName = name;
	}
	/**
	 */
	public String getSmaName()
	{
		String result = super.getSmaName();
		if(result == null) result = getName();
		return result;
	}
	/**
	 */
	public String getManagedResourceName()
	{
		return managedResourceName;
	}
	/**
	 * @return The property value
	 * @param propertyName String property.
	 * @param defaultValue The value to return if the property can not be found.
	 */
	public String getProperty(String propertyName, String defaultValue)
	{
		return super.getProperty(propertyName, defaultValue);
    }
	protected String getPropertyFileProperty(String propertyName, String defaultValue)
	{
		String fullName = getFullName();
		StringTokenizer st = new StringTokenizer(fullName, ".");
		ArrayList list = new ArrayList();
		while(st.hasMoreTokens())
		{
			list.add(st.nextToken());
		}
		int size = list.size();
		String result = null;
		int i = size;
		ConfigurationService configService = FoundationFramework.getInstance().getConfigService();
		result = defaultValue;
		while( i > 0 )
		{
			String key = "";
			for(int j = 0; j < i; ++j)
			{
				key += ((String)list.get(j)) + '.';
			}
			i--;
			key += propertyName;

			result = configService.getProperty(key, null);
			if(result == null)
			{
				result = defaultValue;
			}
			else
			{
				break;
			}
		}
		return result;
	}
	/**
	 * Needed because getFullName will mean different things to the various home implementations.
	 */
	public String getFrameworkFullName()
	{
		if(specialFullName != null) 
		{
			return specialFullName;
		}
		String fullName = getFullName();
		if(fullName.startsWith(FoundationFramework.getInstance().getName()))
        {
            return getContainer().getFullName() + "."+ getName();
        }
        else
        {
            return getContainer().getFullName() + ".BOHome(" + getName() + ")";
        }
	}
	/**
	 * Different from getProperty in that this intended to be used only by the framework.
	 * Append ".<propertyName> to each element in the key looking for a broker name.
	 * ex.
	 * TraderServer.containerName.homeName could be the initial key.
	 * This method will look for a configuration setting of
	 * TraderServer.containerName.homeName.<propertyName> and
	 * TraderServer.containerName.<propertyName> and
	 * TraderServer.<propertyName>
	 * before returning the default value.
	 * 
	 * @return The property value
	 * @param propertyName String property.
	 * @param defaultValue The value to return if the property can not be found.
	 */
	protected String getFrameworkProperty(String propertyName, String defaultValue)
	{
		String fullName = getFullName();
		if(fullName.startsWith(FoundationFramework.getInstance().getName()))
		{
			return getPropertyFileProperty(propertyName, defaultValue);
		}
		FoundationFramework instance =FoundationFramework.getInstance();

		ConfigurationService config = instance.getConfigService();
		String result = config.getProperty(getFrameworkFullName() + "." + propertyName, null);
		if(result == null)
		{
			result = config.getProperty( getContainer().getFullName() + "." + propertyName, null);
		}
		if(result == null)
		{
			result = config.getProperty(instance.getFullName() + "." + propertyName, defaultValue);
		}
		return result;
	}
    /**
     * Called when the server is going 'live'. Useful if any work is needed to be done during this transition.
     * Default behavior is a no-op
	 * If the goMaster call has been made by the operator, the operator can indidcate a failover situation.
	 * @param failover boolean  The operator may specify if this server is going master as the result of failover.
     */
    public void goMaster(boolean failover)
    {
    }
    /**
     * Called when the server is going to a 'slave' state.
     * Default behavior is a no-op
     */
    public void goSlave()
    {
    }
    /**
     * BOHome Unit Test.
     */
	public static class UnitTest extends TestCase
	{
	    FoundationFramework ff;
        public UnitTest(String methodName)
        {
            super(methodName);
        }
        public static void main(String [] args)
        {
            junit.textui.TestRunner.run(UnitTest.class);
        }
        public void testGetSmaType()
        {
            BOHome home = new BOHome();
            home.setName("TestName");
            assertEquals("GlobalTestName.BOHome", home.getSmaType());
            home.setSmaType("override");
            assertEquals("override", home.getSmaType());
        }
		public void testCreateSequence() throws Exception
		{
			BOHome home = new BOHome();
			home.setContainer(ContainerFactory.getInstance().getBOContainer("aContainer"));
			BOHomeDescriptor desc = new BOHomeDescriptor();
			desc.setBOInterceptorClassName("com.cboe.infrastructureServices.foundationFramework.BOHome$UnitTest$MyInterceptor");
			home.setBOHomeDescriptor(desc);

	    	BObject bo = new BObject(){};
	    	String name = "aBoName";
	    	bo.create(name);
	    	home.addToContainer(bo); //BObjects MUST be added to the container.
	    	//The addToContainer call MUST occur prior to creation of the interceptor.

	    	MyInterceptor my = (MyInterceptor)home.createInterceptor(bo);
	    	home.manageObject( my );

			Object obj = home.getBusinessObject(name);
			assertEquals(bo, obj);

		}
		public void testGetProperty()
		{
			BOHome home = new BOHome();
			home.setName("aHome");
			BOContainer boc = new BOContainer();
			boc.initialize();
			boc.setName("aContainer");
			home.setContainer(boc);

		    String propertyValue = home.getProperty("myProperty","NULL");
		    assertEquals("three", propertyValue );
		    propertyValue = home.getFrameworkProperty("persistentBrokerName","NULL");
		    assertEquals("two", propertyValue );
		    propertyValue = home.getFrameworkProperty("poaName","NULL");
		    assertEquals("one", propertyValue );
		    propertyValue = propertyValue = home.getFrameworkProperty("poaName2",null);
		    assertTrue("Did not default to null.", null == propertyValue );
		}
		public void testGetComponentName()
		{
			BOHome home = new BOHome();
			home.setName("aHome");
			BOContainer boc = new BOContainer();
			home.setContainer(boc);
			boc.setName("aContainer");
			BOHomeDescriptor desc = new BOHomeDescriptor();
			home.setBOHomeDescriptor(desc);
			BOContainerDescriptor cDesc = new BOContainerDescriptor();
			boc.setBOContainerDescriptor(cDesc);
			assertEquals("Default Log Service not use!","defaultLogService" ,home.getComponentName());
			cDesc.setLoggingServiceComponentName("contCompName");
			home.componentName = null; //clear the cache
			assertEquals("Container Log Service not use!","contCompName" ,home.getComponentName());
			home.componentName = null;
			desc.setLoggingServiceComponentName("homeCompName");
			assertEquals("Home Log Service not use!","homeCompName" ,home.getComponentName());
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
		    	defaultProps.put("UnitTest.componentName","one");
		    	defaultProps.put("UnitTest.aContainer.persistentBrokerName","two");
		    	defaultProps.put("UnitTest.aContainer.aHome.myProperty","three");
			defaultProps.put("UnitTest.logServiceComponent","defaultLogService");
		    	//FoundationFramework.initializexeForTest("UnitTest", null, defaultProps);

		    	ConfigurationService cof = FoundationFramework.getConfigurationService(defaultProps);
				cof.setName("UnitTest");
		    	ff = FoundationFramework.getInstance();
		    	ff.setName("UnitTest");
		    	ff.setConfigService(cof);
			ff.setDefaultLogComponent("defaultLogService");
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
