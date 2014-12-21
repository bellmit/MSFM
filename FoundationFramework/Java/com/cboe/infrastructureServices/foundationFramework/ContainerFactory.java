package com.cboe.infrastructureServices.foundationFramework;

import java.util.*;

/**
 * Manage the collection and creation of BOContainers.
 * @author Dave Hoag
 * @version 1.2
 */
public class ContainerFactory
{
	static ContainerFactory instance;
	Hashtable containers;
	Hashtable containerDescs;
	/**
	 * ContainerFactory constructor comment.
	 * Initialize the empty collections for managing the containers.
	 */
	public ContainerFactory()
	{
		super();
		containers = new Hashtable();
		containerDescs = new Hashtable();
	}
	/**
	 * Create a BOcontainer from a BOContainerDescriptor
	 * 
	 * @param containerDescriptor BOContainerDescriptor The configuration information for the BOContainer.
	 * @exception java.lang.InstantiationException An exception creating the container from the Descriptor.
	 * @exception java.lang.IllegalAccessException
	 * @exception java.lang.ClassNotFoundException The class specified by the decriptor coult not be found.
	 */
	public BOContainer createBOContainer(BOContainerDescriptor containerDescriptor) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		Class c= Class.forName(containerDescriptor.getContainerImpl());
		BOContainer container = (BOContainer)c.newInstance();
		container.setName(containerDescriptor.getName());
		container.setThreadPolicy(containerDescriptor.getThreadPolicy());
		container.setTransactionPolicy(containerDescriptor.getTransactionPolicy());
		container.setBOContainerDescriptor(containerDescriptor);
		container.initialize();
		return container;
	}
	/**
	* 'Register' the home with the home factory.
	*
	* @author Dave Hoag
	* @param containerName java.lang.String An arbitrary name. Will be used for determining property values.
	* @param desc com.cboe.infrastructureServices.foundationFramework.BOContainerDescriptor
	*/
	public void defineContainer(String containerName, BOContainerDescriptor desc)
	{
		containerDescs.put(containerName, desc);
	}
	/**
	 * Add the container to list of known containers. The containers are indexed via their Container
	 * Name, so it better be a unique name or else one container will replace another.
	 * @author Dave Hoag
	 * @param cont com.cboe.infrastructureServices.foundationFramework.BOContainer
	 */
	public void addBOContainer(BOContainer cont)
	{
		containers.put(cont.getName(), cont);
		defineContainer(cont.getName(), cont.containerDescriptor);
	}
	/**
	 * Get the container with the specified name.
	 * Return null if container could not be found.
	 * @author Dave Hoag
	 * 
	 * @param name java.lang.String The name of a container previously added.
	 * @return com.cboe.infrastructureServices.foundationFramework.BOContainer The container with that name or null if non exits.
	 */
	public BOContainer getBOContainer(String name)
	{
	    BOContainer result = null;
		if(name != null) 
		{
		    result = (BOContainer)containers.get(name);
		}
		return result;
	}
	/**
	 * Enumerate over the list of registered BOContainers.
	 * Used a custom Enumeration implementation to laziliy initialize the BOContainers.
	 * @return Enumeration of BOContainers.
	 */
	public Enumeration getContainers()
	{
		final Enumeration names = containerDescs.keys();
		return new Enumeration()
					{
						public boolean hasMoreElements()
						{
							return names.hasMoreElements();
						}
						public Object nextElement()
						{
							String name = (String)names.nextElement();
							return getBOContainer(name);
						}
					};
	}
	/**
	 * Get the one and only instance of the home factory.
	 * 
	 * @return HomeFactor the singleton instance.
	 * @roseuid 362CA738020F
	 */
	public static ContainerFactory getInstance()
	{
		if (instance == null)
		{
			instance =  new ContainerFactory();
		}
		return instance;
	}
}
