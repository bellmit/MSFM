package com.cboe.infrastructureServices.foundationFramework;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import junit.framework.TestCase;

import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
/**
 * Manage the collection and creation of BOHomes.
 * @author Dave Hoag
 * @version 3.3
 */
public class HomeFactory
{
	static HomeFactory instance;
	//Keep an ordered list of the names.
	Vector<String> homeNames;
	ConcurrentHashMap<String,BOHome> homes;
	ConcurrentHashMap<String,BOHomeDescriptor> homeDescs;
	static final String ALIAS_PROPERTY = "alias";
    boolean homesHaveBeenFound = false;
	/**
	 * HomeFactory constructor comment.
	 * Initialize the empty collections for managing the homes.
	 */
	public HomeFactory()
	{
		super();
		homes= new ConcurrentHashMap<String,BOHome>(20);
		homeDescs = new ConcurrentHashMap<String,BOHomeDescriptor>(20);
		homeNames = new Vector<String>();
	}
    /**
     * Provide the ability to change the 'order' of the homes by putting in your own vector.
     */
    public void changeHomeNames(Vector<String> newListOfNames) 
    {
        if(homesHaveBeenFound) throw new IllegalStateException("Home names can not be changed once any homes have been found.");
        validateListOfNames(newListOfNames);
        homeNames = newListOfNames;
    }
    /**
     * We are changing the known list of homes. This allows for ordered startup and initialization, 
     * however,the listOfHomes may not be in sync with the actual defined list of homes. This will validate
     * the newListOfHomes against the defined list of homes.
     */
    private void validateListOfNames(Vector<String> newListOfNames)
    {
        ArrayList<String> list = new ArrayList<String>(homeNames.size());
        list.addAll(homeNames);
        int size = newListOfNames.size();
        String missingDefinitions = null;
        for(int i = 0; i < size; i++)
        {
            Object name = newListOfNames.get(i);
            int elementIdx = list.indexOf(name);
            if(elementIdx < 0)
            {
                if(missingDefinitions == null) 
                {
                    missingDefinitions = name.toString();
                }
                else
                {
                    missingDefinitions += ", " + name.toString();
                }
            }
            else
            {
                list.remove(elementIdx);
            }
        }
        if(missingDefinitions != null)
        {
            throw new IllegalArgumentException("Homes specified in the listOfHomes are undefined : " + missingDefinitions);
        }
        for(int i = 0; i < list.size(); i++)
        {
            Object definedButNotInListOfHomes = list.get(i);
            if(definedButNotInListOfHomes != null)
            {
                Log.information("Perhaps by intent the home " + definedButNotInListOfHomes + "  was defined but not included in the listOfHomes. This home will not be initialized, started, nor told to goMaster/goSlave. ");
            }
        }
    }
	/**
	 * Use the descriptor to get the properties for the new home.
	 * With the BOHome implementation class name, use reflection to create a
	 * new instance of the BOHome.
	 * Initialize the new BOHome instance with a reference to it's container.
	 */
	protected BOHome createBOHome(BOHomeDescriptor homeDescriptor) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		Class c= Class.forName(homeDescriptor.getBOHomeImplClassName());
		BOHome home = (BOHome)c.newInstance();
		home.setBOHomeDescriptor(homeDescriptor);
		home.setName(homeDescriptor.getBOHomeName());

		String containerName = homeDescriptor.getBOContainerDescriptorName();
		BOContainer container = ContainerFactory.getInstance().getBOContainer(containerName);
		home.setContainer(container);
        home.setManagedResourceName(homeDescriptor.getManagedResourceName());

        //Exists in descriptor, but usage is unknown.
		homeDescriptor.getBObjectClassName();
		return home;
	}
	/**
	* 'Register' the home with the home factory.
	*
	* @author Dave Hoag
	* @param homeName java.lang.String An arbitrary name. Will be used for determining property values.
	* @param desc com.cboe.infrastructureServices.foundationFramework.BOHomeDescriptor
	*/
	public void defineHome(String homeName, BOHomeDescriptor desc)
	{
		homeDescs.put(homeName, desc);
		if(homeNames.contains(homeName))
		{
			homeNames.removeElement(homeName);
		}
		homeNames.addElement(homeName);
	}
	/**
	 * Find the home defined with the provided name. If the home can not be found,
	 * lazily initialize the home.
	 *
	 * @author Dave Hoag
	 * @return com.cboe.infrastructureServices.foundationFramework.BOHome
	 * @param homeName java.lang.String The name of a previously defined home.
	 */
	public BOHome findHome(String homeName) throws CBOELoggableException
	{
        homesHaveBeenFound = true;
		BOHome result = homes.get(homeName);
		if(result == null)
		{
			BOHomeDescriptor hod = homeDescs.get(homeName);
			if(hod == null) return null;
			try
			{
				result = createBOHome(hod);
				homes.put(homeName, result);

				String aliasList = result.getFrameworkProperty(ALIAS_PROPERTY, null);
                
				if(aliasList != null)
				{
					StringTokenizer st = new StringTokenizer(aliasList, ",");
					while(st.hasMoreTokens())
					{
						String alias= st.nextToken();
						homes.put(alias, result);
						homeNames.addElement(alias);
					}
				}
			}
			catch (Exception e)
			{
				Log.exception(e);
				throw new CBOELoggableException(e.toString(), MsgPriority.medium);
			}
		}
		return result;
	}
	/**
	 * Enumerate over the list of registered BOHomes.
	 * Used a custom Enumeration implementation to laziliy initialize the BOHomes.
	 * @return Enumeration of BOHomes.
	 */
	public Enumeration getHomes()
	{
		final Enumeration names = homeNames.elements();
		return new Enumeration()
					{
						public boolean hasMoreElements()
						{
							return names.hasMoreElements();
						}
						public Object nextElement()
						{
							String name = (String)names.nextElement();
							try
							{
								return findHome(name);
							}
							catch (CBOELoggableException e) { throw new RuntimeException("Fatal Interal Error: Home " + name +  " can not be located!"); }
						}
					};
	}
	/**
	 * Get the one and only instance of the home factory.
	 *
	 * @return HomeFactor the singleton instance.
	 * @roseuid 362CA738020F
	 */
	public static HomeFactory getInstance()
	{
		if (instance == null)
		{
			instance =  new HomeFactory();
		}
		return instance;
	}
	public void addHomeForTesting (String homeName, BOHome aTestHome)
	{
		homes.put(homeName, aTestHome);
	}
    /**
     * HomeFactory Unit Test.
     */
	public static class UnitTest extends TestCase
	{
	    FoundationFramework ff;
	    HomeFactory hf;
        public UnitTest(String methodName)
        {
            super(methodName);
        }
        public static void clearInstance()
        {
            HomeFactory.instance = null;
        }
        public static void main(String [] args)
        {
            junit.textui.TestRunner.run(UnitTest.class);
        }
		public void testHomeEnumeration()
		{
			Enumeration e = hf.getHomes();
			int count = 0;
			while(e.hasMoreElements())
			{
				count ++;
				e.nextElement();
			}
			assertEquals(2, count);
		}
		public void testAliasProperty() throws Exception
		{
			BOHome home = hf.findHome("aHome");
			assertTrue("Expected test home 'aHome' not found!", home != null);
			BOHome two = hf.findHome("three");
			assertEquals(home, two);
		}
		public void setUp() throws Exception
		{
            super.setUp();
			if(ff == null)
			{
		    	ff = FoundationFramework.getInstance();
		    	ff.setName("UnitTest");

				hf = new HomeFactory();
		    	java.util.Properties defaultProps = new java.util.Properties();
		    	defaultProps.put("UnitTest.aContainer.aHome.alias","three");
		    	ConfigurationService cof = FoundationFramework.getConfigurationService(defaultProps);
		    	ff.setConfigService(cof);
		    	cof.initialize(null, 0);

				BOContainer container = new BOContainer();
				container.setName("aContainer");
                container.initialize();
		    	ContainerFactory.getInstance().containers.put("aContainer", container);

		    	BOHomeDescriptor desc = new BOHomeDescriptor();
		    	desc.setBOHomeImplClassName("com.cboe.infrastructureServices.foundationFramework.HomeFactory$UnitTest$MyHome");
		    	desc.setBOHomeName("aHome");
		    	desc.setBOContainerDescriptorName("aContainer");
		    	hf.defineHome("aHome", desc);

		    }
		}

		public static class MyHome extends BOHome
		{
		}
	}

}
