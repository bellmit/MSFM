package com.cboe.infrastructureServices.systemsManagementService;

import java.io.*;
import java.util.*;


/**
 * Specialized Class of ConfigurationService that takes its input from files.
 * This class maintains a set of properties.  One of its properties is the filename and directory of its input file. 
 *
 * @version 3.1
 */
public class ConfigurationServiceFileImpl extends ConfigurationServiceBaseImpl
{
	public static final String PREFIX_PROPERTY_NAME = "PropertyPrefix";  // Name of system property for prefix
	public static final String PREFIX_VARIABLE = "<prefix>";  // Prefix variable that can be used in property names and values
	protected static java.util.Properties properties;  // The actual properties..fully qualified to resolve namespace issues
	private static File propertyFile;      // The configuration file with the properties

	static boolean isVerbose()
	{
		String prop = System.getProperty("configVerbose","false");
		if(prop.length() == 0) return false;
		char c = prop.charAt(0);
		return (c == 't' || c == 'T');
	}
	protected boolean verbose = isVerbose();
	
	/**
	 * Invoked by the ConfigurationService getInstance via reflection
	 */
	public ConfigurationServiceFileImpl()
	{
	}
	/**
	*/
	public void defineProperty(com.cboe.infrastructureServices.interfaces.adminService.Property prop)
	{
		String key = prop.name;
		String value = prop.value;
		properties.put(key, value);
	}
	/**
	*/
	public void defineProperties(com.cboe.infrastructureServices.interfaces.adminService.Property [] props)
	{
		for(int i = 0; i < props.length; i++)
		{
			String key = props[i].name;
			String value = props[i].value;
			properties.put(key, value);
		}
	}

/**
 * This method finds a file somewhere in the class path.
 * The method is used instead of the ClassLoader's getSystemResource method, because
 * that method returns a URL to the file, which cannot be written to.
 *
 * @author Craig Murphy
 *
 * @return java.io.File	The file is guaranteed to exist and to be a file (not a directory)
 *
 * @param fileName java.lang.String
 */
private static File findFileOnPath( String fileName )
{
	StringTokenizer pathElements = 
		new StringTokenizer( System.getProperty( "java.class.path" ), System.getProperty( "path.separator" ) );
	while ( pathElements.hasMoreElements() )
	{
		File file = new File( (String) pathElements.nextElement(), fileName );
		if ( file.isFile() ) {
			return file;
		}
	}
	return null;
}
/**
 * @param propertyName	the name of a property with a boolean value
 * @return the boolean value associated with the property
 * @exception com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException 	this is thrown if the property is not known
 * @exception com.cboe.infrastructureServices.systemsManagementService.InappropriateValueException 	thrown when value isn't boolean
 */
public boolean getBoolean( String propertyName ) throws NoSuchPropertyException, InappropriateValueException 
{
	boolean returnValue;
	String value = properties.getProperty( propertyName );
	if ( null != value ) {
		if ( value.equalsIgnoreCase( "true" ) ||
			 value.equalsIgnoreCase( "t" ) ||
			 value.equals( "1" )
		   ) {
			returnValue = true;
		}
		else if ( value.equalsIgnoreCase( "false" ) ||
				  value.equalsIgnoreCase( "f" ) ||
				  value.equals( "0" )
				) {
			returnValue = false;
		}
		else {
			throw new InappropriateValueException( "Value for property " + propertyName + " isn't a boolean: " + value );
		}
	}
	else {
		throw new NoSuchPropertyException( "Property not found: " + propertyName );
	}
	return returnValue;
}
/**
 * @param propertyName	the name of a property with a boolean value
 * @param defaultValue the value to be returned if propertyName is not known
 * @return the boolean	value associated with the property or defaultValue if the property is unknown
 */
public boolean getBoolean( String propertyName, boolean defaultValue ) {
	boolean returnValue;
	String value = properties.getProperty( propertyName );
	if ( null != value ) {
		if ( value.equalsIgnoreCase( "true" ) ||
			 value.equalsIgnoreCase( "t" ) ||
			 value.equals( "1" )
		   ) {
			returnValue = true;
		}
		else if ( value.equalsIgnoreCase( "false" ) ||
				  value.equalsIgnoreCase( "f" ) ||
				  value.equals( "0" )
				) {
			returnValue = false;
		}
		else {
			System.err.println( "Value for property " + propertyName + " isn't a boolean: " + value 
					+ ". The default value will be used: " + defaultValue  + "." );
			returnValue = defaultValue;
		}
	}
	else {
		returnValue = defaultValue;
	}
	return returnValue;
}
/**
 * @param propertyName	the name of a property with a double value
 * @return the double value associated with the property
 * @exception com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException 	this is thrown if the property is not known
 * @exception com.cboe.infrastructureServices.systemsManagementService.InappropriateValueException 	thrown when value isn't a double
 */
public double getDouble( String propertyName ) throws NoSuchPropertyException, InappropriateValueException {
	double returnValue;
	String value = properties.getProperty( propertyName );
	if ( null != value ) {
		try {
			returnValue = Double.valueOf( value ).doubleValue();
		}
		catch ( NumberFormatException nfe ) {
			throw new InappropriateValueException( "Value for property " + propertyName + " isn't a double: " + value );
		}
	}
	else {
		throw new NoSuchPropertyException( "Property not found: " + propertyName );
	}
	return returnValue;
}
/**
 * @param propertyName	the name of a property with a double value
 * @param defaultValue	the value to be returned if the property is unknown
 * @return the boolean 	value associated with the property or defaultValue if the property is unknown
 */
public double getDouble( String propertyName, double defaultValue ) {
	double returnValue;
	String value = properties.getProperty( propertyName );
	if ( null != value ) {
		try {
			returnValue = Double.valueOf( value ).doubleValue();
		}
		catch ( NumberFormatException nfe ) {
			System.err.println( "Value for property " + propertyName + " isn't a double: " + value 
					+ ". The default value will be used: " + defaultValue + "." );
			returnValue = defaultValue;
		}
	}
	else {
		returnValue = defaultValue;
	}
	return returnValue;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object		The instantiated object
 * @param propertyName java.lang.String		The property with the class name
 * @exception com.cboe.infrastructureSrevices.systemsManagementService.NoSuchPropertyException.
 * @exception com.cboe.infrastructureSrevices.systemsManagementService.InappropriateValueException.
*/
public Object getInstanceOf( String propertyName ) throws NoSuchPropertyException, InappropriateValueException {
		Object returnValue;
		Class propertyClass;
		String className = properties.getProperty( propertyName );
		if ( null != className ) {
			try {
				propertyClass = Class.forName( className );
				returnValue = propertyClass.newInstance();
			}
			catch ( ClassNotFoundException cnfe ) {
				System.err.println( cnfe );
				throw new InappropriateValueException( cnfe.toString() );
			}
			catch ( InstantiationException ie ) {
				System.err.println( ie );
				throw new InappropriateValueException( ie.toString() );
			}
			catch ( IllegalAccessException iae ) {
				System.err.println( iae );
				throw new InappropriateValueException( iae.toString() );
			}
		}
		else {
			throw new NoSuchPropertyException( "Property not found: " + propertyName );
		}
		return returnValue;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object		The instantiated object
 * @param propertyName java.lang.String		The property with the class name.
 * @exception com.cboe.infrastructureServices.systemsManagementService.InappropriateValueException.
 */
 
public Object getInstanceOf( String propertyName, Class defaultValue ) throws InappropriateValueException {
		Object returnValue = null; // null should never get returned, but VisualAge seems to think it needs to be initialized.
		Class propertyClass;
		String className = properties.getProperty( propertyName );
		boolean retry = false; // Used if we have to retry instantiation
		if ( null != className ) {
			try {
				propertyClass = Class.forName( className );
			}
			catch ( ClassNotFoundException cnfe ) {
				System.err.println( "Unable to find " + propertyName + " " + cnfe );
				System.err.println( "Default class " + defaultValue.getName() + " will be used instead." );
				propertyClass = defaultValue;
			}
		}
		else {
			propertyClass = defaultValue;
		}
		
		try {
			returnValue = propertyClass.newInstance();
		}
		catch ( InstantiationException ie ) {
			if ( propertyClass != defaultValue ) {
				System.err.println( "Unable to instantiate " + propertyName + " " + ie );
				System.err.println( "Default class " + defaultValue.getName() + " will be used instead." );
				propertyClass = defaultValue;
				retry = true;
			}
			else {
				System.err.println( ie );
				throw new InappropriateValueException( ie.toString() );
			}
		}
		catch ( IllegalAccessException iae ) {
			if ( propertyClass != defaultValue ) {
				System.err.println( "No access to " + propertyName + " " + iae );
				System.err.println( "Default class " + defaultValue.getName() + " will be used instead." );
				propertyClass = defaultValue;
				retry = true;
			}
			else {
				System.err.println( iae );
				throw new InappropriateValueException( iae.toString() );
			}
		}
		if ( retry ) {
			try {
				returnValue = propertyClass.newInstance();
			}
			catch ( InstantiationException ie ) {
				System.err.println( ie );
				throw new InappropriateValueException( ie.toString() );
			}
			catch ( IllegalAccessException iae ) {
				System.err.println( iae );
				throw new InappropriateValueException( iae.toString() );
			}
		}
		return returnValue;
}
/**
 * @param propertyName	the name of a property with an integer value
 * @return the boolean value associated with the property
 * @exception com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException 	this is thrown if the property is not known
 * @exception com.cboe.infrastructureServices.systemsManagementService.InappropriateValueException 	thrown when value isn't an integer
 */
public int getInt( String propertyName ) throws NoSuchPropertyException, InappropriateValueException {
	int returnValue;
	String value = properties.getProperty( propertyName );
	if ( null != value ) {
		try {
			returnValue = Integer.parseInt( value );
		}
		catch ( NumberFormatException nfe ) {
			throw new InappropriateValueException( "Value for property " + propertyName + " isn't an integer: " + value );
		}
	}
	else {
		throw new NoSuchPropertyException( "Property not found: " + propertyName );
	}
	return returnValue;
}
/**
 * @param propertyName	the name of a property with an integer value
 * @param defaultValue	the value to be returned if the property is unknown
 * @return the boolean 	value associated with the property or defaultValue if the property is unknown
 */
public int getInt( String propertyName, int defaultValue ) {
	int returnValue;
	String value = properties.getProperty( propertyName );
	if ( null != value ) {
		try {
			returnValue = Integer.parseInt( value );
		}
		catch ( NumberFormatException nfe ) {
			System.err.println( "Value for property " + propertyName + " isn't an integer: " + value
					+ ". The default value will be used: " + defaultValue + "." );
			returnValue = defaultValue;
		}
	}
	else {
		returnValue = defaultValue;
	}
	return returnValue;
}
/**
 * @param propertyName	the name of a property with a long value
 * @return the boolean 	value associated with the property
 * @exception com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException 	this is thrown if the property is not known
 * @exception com.cboe.infrastructureServices.systemsManagementService.InappropriateValueException 	thrown when value isn't a long
 */
public long getLong( String propertyName ) throws NoSuchPropertyException, InappropriateValueException {
	long returnValue;
	String value = properties.getProperty( propertyName );
	if ( null != value ) {
		try {
			returnValue = Long.parseLong( value );
		}
		catch ( NumberFormatException nfe ) {
			throw new InappropriateValueException( "Value for property " + propertyName + " isn't a long: " + value );
		}
	}
	else {
		throw new NoSuchPropertyException( "Property not found: " + propertyName );
	}
	return returnValue;
}
/**
 * @param propertyName	the name of a property with a long value
 * @param defaultValue	the value to be returned if the property is unknown
 * @return the boolean 	value associated with the property or defaultValue if the property is unknown
 */
public long getLong( String propertyName, long defaultValue ) {
	long returnValue;
	String value = properties.getProperty( propertyName );
	if ( null != value ) {
		try {
			returnValue = Long.parseLong( value );
		}
		catch ( NumberFormatException nfe ) {
			System.err.println( "Value for property " + propertyName + " isn't a long: " + value 
					+ ". The default value will be used: " + defaultValue  + "." );
			returnValue = defaultValue;
		}
	}
	else {
		returnValue = defaultValue;
	}
	return returnValue;
}
/**
 * @param propertyName	the name of a property
 * @return the boolean 	value associated with the property
 * @exception com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException 	this is thrown if the property is not known
 */
public String getProperty( String propertyName ) throws NoSuchPropertyException 
{
	String returnValue = getProperty( propertyName, null );
	if ( null == returnValue ) {
		throw new NoSuchPropertyException( "Property not found: " + propertyName );
	}
	return returnValue;
}
/**
 * @param propertyName	the name of a property with a boolean value
 * @param defaultValue	the value to be returned if the property is unknown
 * @return the boolean value associated with the property or defaultValue if the property is unknown
 */
public String getProperty( String propertyName, String defaultValue ) 
{
	if(verbose)
	{
		System.out.println("Requesting property " + propertyName + " default " + defaultValue);
	}
	return properties.getProperty( propertyName, defaultValue );
}
/**
 * @param propertyNameForListProperty	The name of a property that has a list value, e.g., "red,blue,green"
 * @param itemDelimiters				A string with characters that separate items in the list, e.g., ","
 * @return An array of the items from the list
 * @exception com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException 	this is thrown if the property is not known
 */
public String[] getPropertyList( String propertyNameForListProperty, String itemDelimiters ) throws NoSuchPropertyException {
	String value = properties.getProperty( propertyNameForListProperty );
	String[] returnValue;
	if ( null != value )
		returnValue = stringToList( value, itemDelimiters );
	else
		throw new NoSuchPropertyException( "Property not found: " + propertyNameForListProperty );
	return returnValue;
}
/**
 * @param propertyNameForListProperty	The name of a property that has a list value, e.g., "red,blue,green"
 * @param itemDelimiters				A string with characters that separate items in the list, e.g., ","
 * @param defaultValue					The default value associated with propertyNameForListProperty
 * @return An array of the items from the list
 */
public String[] getPropertyList( String propertyNameForListProperty, String itemDelimiters, String defaultValue ) {
	String value = properties.getProperty( propertyNameForListProperty, defaultValue );
	String[] returnValue;
	if ( null != value )
		returnValue = stringToList( value, itemDelimiters );
	else
		returnValue = new String[ 0 ]; // This will only happen if the default value is null
	return returnValue;
}
/**
 * @param propertyNameWithWildcards		A partially specified property name, e.g., "TradeServer.*.transactionPolicy"
 * @return The set of property/value pairs that match the wildcards
 */
public Property[] getPropertySet( String propertyNameWithWildcards ) {
	final int elementDelimiter = '.';
	final String elementDelimiterString = ".";
	final String wildCard = "*";
	Vector propertySet = new Vector();
	String[] propertyParts = stringToList( propertyNameWithWildcards, elementDelimiterString );
	int i;
	int numberOfParts = propertyParts.length;
	int penultimatePart = numberOfParts - 2;
	// Find out in advance where the wildcards are
	for ( i = 0; i < numberOfParts; i++ ) {
		if ( wildCard.equals( propertyParts[ i ] ) ) {
			propertyParts[ i ] = null;
		}
	}
	// Now we need to check all our properties
	Enumeration propertyNames = properties.propertyNames();
	while ( propertyNames.hasMoreElements() ){
		String currentPart;
		String currentProperty = (String) propertyNames.nextElement();
		int currentIndex = 0; // Used for finding delimiters in property
		int nextIndex;
		boolean foundMatch = true;
		for ( i = 0; i <= penultimatePart; i++ ){
			nextIndex = currentProperty.indexOf( elementDelimiter, currentIndex );
			currentPart = propertyParts[ i ];
			if ( -1 != nextIndex )	{
				if ( null != currentPart ) { // We have to match if we aren't at a wildcard
					if ( (nextIndex - currentIndex ) != currentPart.length() ||
						 !currentProperty.regionMatches( currentIndex, currentPart, 0, currentPart.length() ) ) {
						foundMatch = false;  // This property doesn't match our criteria
						break;
					}
				}
			}
			else { // We have to have this section or it isn't a match
				foundMatch = false;
				break;
			}
			currentIndex = nextIndex + 1;
		}
		if ( foundMatch ) { // Now we check the last segment
			currentPart = propertyParts[ numberOfParts - 1 ];
			if ( null != currentPart ) { // We don't need to match if we are at a wildcard
				if ( ( currentProperty.length() - currentIndex ) != currentPart.length() ||
 					   !currentProperty.regionMatches( currentIndex, currentPart, 0, currentPart.length() ) ) {
						foundMatch = false;
				}
			}
		}
		if ( foundMatch )
		{
			propertySet.addElement( new Property( currentProperty, properties.getProperty( currentProperty ) ) );
		}
	}
	Property[] returnValue = new Property[ propertySet.size() ];
	for ( i = 0; i < returnValue.length; i++ )
	{
		returnValue[ i ] = (Property) propertySet.elementAt( i );
	}
	return returnValue;
}
/**
 * This method loads in the configuration parameters from the specified file
 *
 * @param parameters 	String[]	The file with the parameters for initialization
 * @param firstConfigurationParameter	int		The offset into parameters to the first Configuration parameter 
 *
 * NOTE: the only parameter used by the File based configuration service is
 * the name of the file, which can be anyplace in the CLASSPATH
 *
 */
public boolean initialize(String[] parameters, int firstConfigurationParameter ) {
	String fileName = parameters[ firstConfigurationParameter ];
	InputStream in = null;
	boolean returnValue = false; // Prepare for the worst
	// Initialize the properties with the system properties as default values
	// this will allow command line defined values to be used
	properties = new java.util.Properties(System.getProperties());
	propertyFile = null;
	//Check the local directory first.
	File f = new File(fileName);
	if(f.isFile()) propertyFile = f;
	//This will allow the property file to reside anywhere along the CLASSPATH.
	if(propertyFile == null)
		propertyFile = findFileOnPath( fileName );
	if ( null != propertyFile ) {
		try {
			in = new FileInputStream( propertyFile );
			System.err.println("Loading property file " +  propertyFile.getAbsolutePath() );
			properties.load(in);
			properties = postProcess(properties);
			returnValue = true; // Success!
		}
		catch ( java.io.IOException ioe ) {
			ioe.printStackTrace( System.err );
		}
		finally {
			if ( null != in )
			{
				try {
					in.close();
				}
				catch ( java.io.IOException ioe )
				{
					ioe.printStackTrace( System.err );
				}
			}
		}
	}
	else
		System.err.println("Unable to find property file " + fileName );

	return returnValue;
}
/**
 * Substitutes prefix variable for defined prefix value in property names and property values.  The prefix variable
 * is &lt;prefix&gt;.
 *
 * @param properties properties to be processed
 * @return either the original or an updated properties
 */
private java.util.Properties postProcess(java.util.Properties properties) {
	String prefixValue = System.getProperty(PREFIX_PROPERTY_NAME);
	java.util.Properties result = properties;
	if (prefixValue != null) {
		System.err.println("Changing all values of <prefix> in properties to: " + prefixValue);
		result = new java.util.Properties(System.getProperties());
		Iterator iter = properties.entrySet().iterator();
		Map.Entry entry;
		String newName;
		String newValue;
		while (iter.hasNext()) {
			entry = (Map.Entry) iter.next();
			newName = replacePrefix((String) entry.getKey(), prefixValue);
			newValue = replacePrefix((String) entry.getValue(), prefixValue);
			result.setProperty(newName, newValue);
		}
	}
	return result;
}
/**
 * Replaces prefix variable with prefix value.
 *
 * @param value the value to be updated
 * @param prefix the value of the prefix
 * @return the updated value
 */
private String replacePrefix(String value, String prefix) {
	String result = value;
	if (value.startsWith(PREFIX_VARIABLE)) {
		result = prefix + value.substring(PREFIX_VARIABLE.length());
	}
	return result;
}
/**
 * @param propertyName java.lang.String
 * @param propertyValue java.lang.String
 */
public void setProperty( String propertyName, String propertyValue ) {
	setProperty( propertyName, propertyValue, true );
}
/**
 * @param propertyName java.lang.String
 * @param propertyValue java.lang.String
 * @param persist boolean
 */
public void setProperty( String propertyName, String propertyValue, boolean persist ) {
	OutputStream out = null;
	properties.put( propertyName, propertyValue );
	if ( persist ) {
		try {
			File backupVersion = new File( propertyFile.getAbsolutePath()  );
			File backupName = new File( backupVersion.getAbsolutePath() + "." + (new Date()).getTime() );
			if ( backupName.exists() ) {
				int counter = 0;
				while ( backupName.exists() ) {
					backupName = new File( backupVersion.getAbsolutePath() + "." + counter++ );
				}
			}
			if ( !backupVersion.renameTo( backupName ) ) {
				System.err.println( "Unable to make a backup of " + propertyFile.getAbsolutePath() + 
					" to "  + backupName.getAbsolutePath() );
				System.err.println( "Configuration not saved." );
				return;
			}
			
			out = new FileOutputStream( propertyFile );
			System.err.println("Saving property file " + propertyFile.getAbsolutePath() );
			String header;
			if ( null != getName() ) {
				header = "Configuration file for " + getName();
			}
			else {
				header = null;
			}
			properties.store( out, header );
			out.close();
		}
		catch ( java.io.IOException ioe ) {
			ioe.printStackTrace();
		}
		finally {
			if ( null != out )
			{
				try {
					out.close();
				}
				catch ( java.io.IOException ioe )
				{
					ioe.printStackTrace( System.err );
				}
			}
		}
	}
}
/**
 * @return java.lang.String[]	The elements in the value which were delimited by delimiters
 * @param value java.lang.String	The string to be broken into elements
 * @param delimiters java.lang.String	The delimiters of the elements in value
 */ 
private String[] stringToList( String value, String delimiters ) {
	StringTokenizer tokenizer = new StringTokenizer( value, delimiters );
	String[] returnValue = new String[ tokenizer.countTokens() ];
	for ( int i = 0; tokenizer.hasMoreTokens(); i++ )
	{
		returnValue[ i ] = tokenizer.nextToken();
	}
	return returnValue;
}
}
