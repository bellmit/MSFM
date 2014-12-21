package com.cboe.infrastructureServices.systemsManagementService;

/**
 * This interface defines methods that allow retrieving configuration
 * parameters from any source, whether it is a local file or a CORBA server.
 * @author Craig Murphy
 * @creation date 12/08/1998
 * @version 2.0 - Added the getFullName method to the interface.
 */
public interface ConfigurationService {
	/**
	 * The full name format is directly related to the selected configuration service impl.
	 * The full name request must be delegated to the config svc impl.
	 * 
	 * @param comp FrameworkComponent The component wishing to know it's full name.
	 * @return String The properly formed full name for this configuration service.
	 * @author Dave Hoag
	 */
	public String getFullName(com.cboe.infrastructureServices.foundationFramework.FrameworkComponent comp);
/**
 */
public void defineProperties(com.cboe.infrastructureServices.interfaces.adminService.Property[] props);
/**
 */
public void defineProperty(com.cboe.infrastructureServices.interfaces.adminService.Property props);
/**
 * @param propertyName	the name of a property with a boolean value
 * @return the boolean value associated with the property
 * @exception com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException 	this is thrown if the property is not known
 * @exception com.cboe.infrastructureServices.systemsManagementService.InappropriateValueException 	thrown when value isn't boolean
 */
public boolean getBoolean( String propertyName ) throws NoSuchPropertyException, InappropriateValueException;
/**
 * @param propertyName	the name of a property with a boolean value
 * @param defaultValue the value to be returned if propertyName is not known
 * @return the boolean	value associated with the property or defaultValue if the property is unknown
 */
public boolean getBoolean( String propertyName, boolean defaultValue );
/**
 * @param propertyName	the name of a property with a double value
 * @return the double value associated with the property
 * @exception com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException 	this is thrown if the property is not known
 * @exception com.cboe.infrastructureServices.systemsManagementService.InappropriateValueException 	thrown when value isn't a double
 */
public double getDouble( String propertyName ) throws NoSuchPropertyException, InappropriateValueException;
/**
 * @param propertyName	the name of a property with a double value
 * @param defaultValue	the value to be returned if the property is unknown
 * @return the boolean 	value associated with the property or defaultValue if the property is unknown
 */
public double getDouble( String propertyName, double defaultValue );
/**
 * This method get the name of a class from the configuration and attempts to instantiat it.
 * @return java.lang.Object
 * @param propertyName java.lang.String		Name of property
 * @exception com.cboe.infrastructureService.systemsManagementService.NoSuchPropertyException.
 * @exception com.cboe.infrastructureService.systemsManagementService.InappropriateValueException.
 */
Object getInstanceOf( String propertyName ) throws NoSuchPropertyException, InappropriateValueException;
/**
 * This method get the name of a class from the configuration and attempts to instantiat it.
 * @return java.lang.Object
 * @param propertyName java.lang.String 	Name of property
 * @param defaultValue Class				Class to use if the property doesn't exist
 * @exception com.cboe.infrastructureService.systemsManagementService.InappropriateValueException.
 */
Object getInstanceOf( String propertyName, Class defaultValue ) throws InappropriateValueException;
/**
 * @param propertyName	the name of a property with an integer value
 * @return the boolean value associated with the property
 * @exception com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException 	this is thrown if the property is not known
 * @exception com.cboe.infrastructureServices.systemsManagementService.InappropriateValueException 	thrown when value isn't an integer
 */
public int getInt( String propertyName ) throws NoSuchPropertyException, InappropriateValueException;
/**
 * @param propertyName	the name of a property with an integer value
 * @param defaultValue	the value to be returned if the property is unknown
 * @return the boolean 	value associated with the property or defaultValue if the property is unknown
 */
public int getInt( String propertyName, int defaultValue );
/**
 * @param propertyName	the name of a property with a long value
 * @return the boolean 	value associated with the property
 * @exception com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException 	this is thrown if the property is not known
 * @exception com.cboe.infrastructureServices.systemsManagementService.InappropriateValueException 	thrown when value isn't a long
 */
public long getLong( String propertyName ) throws NoSuchPropertyException, InappropriateValueException;
/**
 * @param propertyName	the name of a property with a long value
 * @param defaultValue	the value to be returned if the property is unknown
 * @return the boolean 	value associated with the property or defaultValue if the property is unknown
 */
public long getLong( String propertyName, long defaultValue );
	/**
	   Return a String of the name of the ProcessDescriptor
	   @roseuid 3658CE6F03D0
	 */
	public String getName();
/**
 * @param propertyName	the name of a property
 * @return the boolean 	value associated with the property
 * @exception com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException 	this is thrown if the property is not known
 */
public String getProperty( String propertyName ) throws NoSuchPropertyException;
/**
 * @param propertyName	the name of a property with a boolean value
 * @param defaultValue	the value to be returned if the property is unknown
 * @return the boolean value associated with the property or defaultValue if the property is unknown
 */
public String getProperty( String propertyName, String defaultValue );
/**
 * @param propertyNameForListProperty	The name of a property that has a list value, e.g., "red,blue,green"
 * @param itemDelimiters				A string with characters that separate items in the list, e.g., ","
 * @return An array of the items from the list
 * @exception com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException 	this is thrown if the property is not known
 */
public String[] getPropertyList( String propertyNameForListProperty, String itemDelimiters ) throws NoSuchPropertyException;
/**
 * @param propertyNameForListProperty	The name of a property that has a list value, e.g., "red,blue,green"
 * @param itemDelimiters				A string with characters that separate items in the list, e.g., ","
 * @param defaultValue					The value to use if propertyNameForListProperty is unknown
 * @return An array of the items from the list
 * @exception com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException 	this is thrown if the property is not known
 */
public String[] getPropertyList( String propertyNameForListProperty, String itemDelimiters, String defaultValue );
/**
 * @param propertyNameWithWildcards		A partially specified property name, e.g., "TradeServer.*.transactionPolicy"
 * @return The set of property/value pairs that match the wildcards
 */
public Property[] getPropertySet( String propertyNameWithWildcards );
	/**
	 * This method will initialize the particular implimentation of ConfigurationService.
	 *
	 * @param parameters	String[]	This is configuration parameters, probably command line arguments
	 * @param firstConfigParamater	int	This is the offset into parameters to the first argument pertinent to Configuration Service
	 * @return 	true means initialization went OK; false = initialization failure
	 *
	 */
	public abstract boolean initialize(String [] parameters, int firstConfigParameter );
	/**
	   Set the name of the ProcessDescriptor
	   @roseuid 3658CE6F033A
	 */
	public void setName(String name);
}
