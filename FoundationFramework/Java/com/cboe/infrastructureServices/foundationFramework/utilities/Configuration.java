package com.cboe.infrastructureServices.foundationFramework.utilities;

import java.util.*;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException;
import com.cboe.infrastructureServices.systemsManagementService.InappropriateValueException;

/**
 * This class is used to get configuration information from a BOHome.
 * @author John Wickberg
 *
 */
public class Configuration {
/**
 * BOHome for this configuration
 */
private BOHome home;

/**
 * This method was created by a SmartGuide.
 */
public Configuration ( BOHome home ) {
	this.home = home;
}
/**
 * Gets boolean value.
 * @return boolean
 * @param propertyName java.lang.String
 * @param defaultValue boolean
 */
public boolean getBoolean(String propertyName, boolean defaultValue) {
	boolean value = defaultValue;
	String property = home.getProperty(propertyName, null);
	if (property != null)
	{
		value = new Boolean(property).booleanValue();
	}
	return value;
}
/**
 * This method looks up the name of the class in the configuration
 * properties and then returns an instance of than class.  An instance
 * of the passed default class is returned if the property is not defined.
 *
 * @return java.lang.Object
 * @param propertyName java.lang.String
 * @param defaultClass java.lang.Class
 */
public Object getInstanceOf(String propertyName, Class defaultClass) {
	String className = home.getProperty(propertyName, null);
	Class instanceClass = defaultClass;
	Object classInstance = null;
	try {
		if (className != null) {
			instanceClass = Class.forName(className);
		}
		classInstance = instanceClass.newInstance();
	}
	catch (Exception e) {
		// what should be done?
	}
	return classInstance;
}
/**
 * Gets a property as an integer.
 *
 * @param propertyName name of property
 */
public int getInt(String propertyName) throws NoSuchPropertyException, InappropriateValueException {
    String value = home.getProperty(propertyName);
    try {
        return Integer.parseInt(value);
    }
    catch (NumberFormatException e) {
        throw new InappropriateValueException("Cannot covert value to int: " + value);
    }
}
/**
 * Gets a property as an integer.
 *
 * @param propertyName name of property
 * @param defaultValue value returned if property not defined
 */
public int getInt(String propertyName, int defaultValue) {
    int result = defaultValue;
    String value = home.getProperty(propertyName, null);
    if (value != null) {
        try {
            result = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            // ignore error and use default value
        }
    }
    return result;
}
/**
 * Gets property.
 */
public String getProperty(String propertyName) throws NoSuchPropertyException {
    return home.getProperty(propertyName);
}
/**
 * Gets property with a default value.
 */
public String getProperty(String propertyName, String defaultValue) {
    return home.getProperty(propertyName, defaultValue);
}
/**
 * Gets list of tokens using default values.
 *
 * @return java.lang.String[]
 * @param propertyName java.lang.String
 */
public String[] getPropertyList(String propertyName) {
	return getPropertyList(propertyName, null, " ");
}
/**
 * Gets list of tokens using default values.
 *
 * @return java.lang.String[]
 * @param propertyName property name
 * @param delimiters characters used to delimit tokens
 */
public String[] getPropertyList(String propertyName, String delimiters) throws NoSuchPropertyException {
    String value = getProperty(propertyName);
	return getPropertyList(propertyName, value, delimiters);
}
/**
 * Gets list of tokens from property value.
 * @return java.lang.String[]
 * @param propertyName java.lang.String
 * @param defaultValue java.lang.String
 */
public String[] getPropertyList(String propertyName, String defaultValue, String delimiters) {
	String value = home.getProperty(propertyName, defaultValue);
	String[] tokens;
	if (value != null)
	{
		StringTokenizer tokenizer = new StringTokenizer(value, delimiters);
		tokens = new String[tokenizer.countTokens()];
		for (int i = 0; tokenizer.hasMoreTokens(); i++)
		{
			tokens[i] = tokenizer.nextToken();
		}
	}
	else
	{
		tokens = new String[0];
	}
	return tokens;
}
}
