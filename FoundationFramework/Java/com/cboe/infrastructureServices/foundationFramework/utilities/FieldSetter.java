package com.cboe.infrastructureServices.foundationFramework.utilities;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Properties;


public class FieldSetter {
	private static String HIER_DELIMITER = ".";
	/**
	   @roseuid 365F247302A7
	 */
	FieldSetter() {
	}
	/**
	   @roseuid 365889110399
	 */
	public static String getSuffix(String inputString) {
		
		if (inputString.indexOf(".") > 0) {
		   return inputString.substring(inputString.lastIndexOf(FieldSetter.HIER_DELIMITER) + 1, inputString.length());
		 } else {
			 return inputString;
		 }
	}
	/**
	 */
	public static Object setObjectVariablesFromProperties(Object obj, Properties newProps, Properties props) {
		Enumeration propNames = props.propertyNames();
		String stringField = "";
		Field fld = null; 
		String newKey = "";
		String newValue = "";
		while (propNames.hasMoreElements()) {
		  try { 
			newKey = (String)propNames.nextElement();
			newValue = (String)props.get(newKey);
			stringField = getSuffix(newKey); 
			fld = (Field)obj.getClass().getField(stringField);
			newProps.put(newKey, newValue);
			if (fld.getType() == String.class) {
			   fld.set(obj, newValue); 
			  // System.out.println("set field with name " + fld.getName() + " to " + newValue);
			   fld = null;
			} 
		  }  
		  // Dont take action for the following exceptions
		  catch (NoSuchFieldException e) {}
		  catch (IllegalAccessException e) {}
		}
	  return obj; 
	}
}