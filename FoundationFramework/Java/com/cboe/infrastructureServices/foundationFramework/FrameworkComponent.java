package com.cboe.infrastructureServices.foundationFramework;
/**
 * Similar to homes, but this will represent any participating member of a FoundationFramework configuration.
 * A place to put code that each component may need, including the 'Framework'. 
 * @author Dave Hoag
 * @version 1.0
 */
public interface FrameworkComponent
{
	/**
	 * Return a 'string' that will enable the configured configuration service to get properties.
	 */
	public String getFullName();
	/**
	 * Append the property to the 'fullName' and get the value.
	 * 
	 * @return The property value
	 * @param propertyName String property.
	 * @param defaultValue The value to return if the property can not be found.
	 */
	public String getProperty(String propertyName, String defaultValue);
	/**
	 * The SMA type is the SystemsManagement ManagedResource name.
	 */
	public void setSmaType(String type);
	public String getSmaType();
	/**
	 * A string value that assists in determining property values.
	 * In the context of SMA, the instance name is the particular configuration
	 * of the declared ManagedResource.
	 */
	public void setSmaName(String name);
	public String getSmaName();
	/**
	 * FrameworkComponents may exist in a tree like structure, it may be necessary to find our
	 * parent component.
	 *
	 * @return FrameworkComponent parent instance or null.
	 */
	public FrameworkComponent getParentComponent();
	public void setParentComponent(FrameworkComponent parentComponent);
}
