package com.cboe.infrastructureServices.foundationFramework;

import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.interfaces.adminService.Property;
import com.cboe.infrastructureServices.interfaces.adminService.StorageType;



/**
 * Similar to homes, but this will represent any participating member of a FoundationFramework configuration.
 * A place to put code that each component may need, including the 'Framework'. 
 * @author Dave Hoag
 * @version 1.1
 */
public class FrameworkComponentImpl implements FrameworkComponent
{
    static boolean configVerbose = System.getProperty("configVerbose", null) != null;
	protected String smaType;
	protected String smaName;
	private static final String preinitializingState = "pre-Init";
	protected FrameworkComponent parentComponent;
	/**
	 * Return a 'string' that will enable the configured configuration service to get properties.
	 */
	public String getFullName()
	{
		return FoundationFramework.getInstance().getConfigService().getFullName(this);
	}
	/**
	 * Append the property to the 'fullName' and get the value.
	 * 
	 * @return The property value
	 * @param propertyName String property.
	 * @param defaultValue The value to return if the property can not be found.
	 */
	public String getProperty(String propertyName, String defaultValue)
	{
		return getPropImpl(getFullName() + '.' + propertyName, defaultValue );
	}
    /**
     */
    protected String getPropImpl(final String propertyName, final String defaultValue)
    {
        if(configVerbose)
        {
            System.out.println("Getting property " + propertyName + " with default " + defaultValue);
        }

		return FoundationFramework.getInstance().getConfigService().getProperty( propertyName, defaultValue );
    }

    /**
     * Set the current state of the Foundation Framework so the other services can retrive the state of the FF
     * @param state of the Foundation Framework e.g. Slave, Master, Initializing....
     */
    protected void setStatus(String state)
    {
		String propName = null;

        propName = getFullName() + '.' + "state";

        setProperty(propName, state);
    }

    /**
     * Set the current state of the Foundation Framework so the other services can retrive the state of the FF
     * this method creates the property Structure needed by the config service method
     * @param propName qualified name of property
     * @param state of the Foundation Framework e.g. Slave, Master, Initializing....
     */
    private void setProperty(String propName, String state)
    {
        StorageType sType = StorageType.PERSISTENT;

        com.cboe.infrastructureServices.interfaces.adminService.Property prop = new Property(propName, state, sType);

        FoundationFramework.getInstance().getConfigService().defineProperty(prop );
    }

    /**
     * Get the current status of Foundation Framework
     **/
    public String getStatus()
    {
        return getProperty("state", preinitializingState);
    }
	/**
	 * Append the externalCommandName to the 'fullName' and register the command with the callback service.
	 * 
	 * @return none
	 * @param callbackObject The receiver of the callback command
	 * @param externalCommandName The name of the command to 'outside world'
	 * @param methodName the name of the method to invoke on the receiver
	 * @param argumentTypes the types of the arguments to pass to the receiving object
	 * @param argumentDescriptions a description of each argument
	 */
	public void registerCommand(Object callbackObject, String externalCommandName, String methodName, String commandDescription, String[] argumentTypes, String[] argumentDescriptions) throws CBOELoggableException
	{
        FoundationFramework.getInstance().getCommandCallbackService().registerForCommandCallback( callbackObject, 
                                                                                                  getFullName() + "." + externalCommandName,
                                                                                                  methodName,
                                                                                                  commandDescription,
                                                                                                  argumentTypes, 
                                                                                                  argumentDescriptions);

	}
	/**
	 * The SMA type is the SystemsManagement ManagedResource name.
	 */
	public void setSmaType(String type)
	{
		smaType = type;
	}
	public String getSmaType()
	{
		return smaType;
	}
	/**
	 * A string value that assists in determining property values.
	 * In the context of SMA, the instance name is the particular configuration
	 * of the declared ManagedResource.
	 */
	public void setSmaName(String aName)
	{
		smaName =aName;
	}
	public String getSmaName()
	{
		return smaName;
	}
	/**
	 * FrameworkComponents may exist in a tree like structure, it may be necessary to find our
	 * parent component.
	 *
	 * @return FrameworkComponent parent instance or null.
	 */
	public FrameworkComponent getParentComponent()
	{
		return parentComponent;
	}

	/**
	 * Setting the parent component to the given FrameworkComponent
	 */
	public void setParentComponent(FrameworkComponent parent)
	{
		parentComponent = parent;
	}
}
