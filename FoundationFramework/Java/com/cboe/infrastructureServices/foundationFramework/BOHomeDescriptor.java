package com.cboe.infrastructureServices.foundationFramework;
/**
 * Holds the description of a BOHome. Used to instantiate BOHomes.
 *
 * @version 2.0
 */
public class BOHomeDescriptor
{
	private String name;
	private String boHomeImplClassName;
	private String boObjectClassName;
	private String boHomeName;
	private String boContainerDescriptorName;
	private String boInterceptorClassName;
	private String boInterceptorName;
	protected String loggingServiceComponentName;
    protected String managedResourceName;
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
	 */
	public BOHomeDescriptor()
    {
	}
	public String getBObjectClassName()
    {
		  return boObjectClassName;
	}
	public String getBOContainerDescriptorName()
    {
	   return boContainerDescriptorName;
	}
	public String getBOHomeImplClassName()
    {
		  return boHomeImplClassName;
	}
	public String getBOHomeName()
    {
		return boHomeName;
	}
	public String getBOInterceptorClassName()
    {
		  return boInterceptorClassName;
	}
	public String getBOInterceptorName()
    {
		  return boInterceptorName;
	}
	/**
	   @roseuid 3653503F02CA
	 */
	public String getName()
    {
		return name;
	}
	/**
	   @roseuid 365457B100B9
	 */
	public void setBObjectClassName(String className)
    {
		  boObjectClassName = className;
	}
	/**
	   @roseuid 365457D1023B
	 */
	public void setBOContainerDescriptorName(String boContainerDescriptorName)
    {
		this.boContainerDescriptorName = boContainerDescriptorName;
	}
	 /**
	   @roseuid 365457480022s
	 */
	public void setBOHomeImplClassName(String className)
    {
		   boHomeImplClassName = className;
	}
	/**
	   @roseuid 365457120345
	 */
	public void setBOHomeName(String name)
    {
		boHomeName = name;
	}
	public void setBOInterceptorClassName(String className)
    {
		   boInterceptorClassName = className;
	}
	public void setBOInterceptorName(String name)
    {
		  boInterceptorName = name;
	}
	/**
	   @roseuid 3653503F0284
	 */
	public void setName(String name)
    {
		this.name = name;
	}
 	/**
	 * @param name
	 */
	public void setManagedResourceName(String name)
	{
		managedResourceName = name;
	}
	/**
	 */
	public String getManagedResourceName()
	{
		return managedResourceName;
	}

}