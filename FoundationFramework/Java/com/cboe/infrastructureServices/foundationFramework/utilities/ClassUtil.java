package com.cboe.infrastructureServices.foundationFramework.utilities;

//import com.cboe.infrastructureServices.foundationFramework.*;
import java.util.*;
import java.lang.reflect.*;

public class ClassUtil {

	ClassUtil() {
	}
	/** return a class with the name className */
	 public static Class createClass(String className)throws Exception{
		Class returnClass = null;
		try {
			returnClass =  Class.forName(className);
			} catch (ClassNotFoundException exc) {
				System.out.println("Class Not Found " + className);
				System.out.println (exc.toString());
			  System.exit(1);
		}
		return returnClass;
	}
	 /** return a class with the name className */
	 public static Object createInstance(Class aClass)throws Exception{
		Object instance = null;
		try {
		   instance = aClass.newInstance();
			} catch (InstantiationException exc) {
				System.out.println("Cannot Create Instance of " + aClass.getName());
				System.out.println (exc.toString());
			  System.exit(1);
		}
		return instance;
	 }
	 /** Return string arguments as classes so that they can be evaluated */
	 public static Class[] getClassesForStringArguments(String[] parmStrings) throws Exception {
		 Class[] returnArguments = new Class[parmStrings.length];
		 for (int i = 0; i < parmStrings.length; i++) {
			returnArguments[i] = ClassUtil.createClass(parmStrings[i]);
		 }
		 return returnArguments;

	 }
	/**
	   @roseuid 365876EC010F
	 */
	public static Hashtable getFieldsAsHashtable(Object obj) {
		Field[] flds = obj.getClass().getDeclaredFields();
		Hashtable hashtable = new Hashtable();
		for (int i=0; i < flds.length; i++) {
			hashtable.put(flds[i].getName(), flds[i] );
		 }
		 return hashtable;

	}
	 /** return the declared methods (not Inherited) for a particular object */
	 public static Method[] getMethods(Object obj) {
		return obj.getClass().getDeclaredMethods();

	 }
	 /** return the declared methods with a name for a particular object
	 Does not find inherited  methods */
	 public static Vector getMethodsWithName(String aName, Object obj) {
		Method[] methods = ClassUtil.getMethods(obj);
		Vector returnMethods = new Vector();
		for   (int i = 0; i < methods.length; i++) {
				if (methods[i].getName() == aName) {
					returnMethods.addElement(methods[i]);
				}
		}
		return returnMethods;
	 }
	 /** Return a Method if the Object obj has a method with the name aName and t
	 argumentTypes parms.  Does not find inherited  methods */
	 public static Method getMethodWithMatchingNameAndParms(String aName, Object obj, String[] parms) throws NoSuchMethodException, Exception{
		Class[] arguments = ClassUtil.getClassesForStringArguments(parms);
		return obj.getClass().getDeclaredMethod(aName, arguments);
	 }
	 /** Return true if the Object obj has a method with the name aName and t
	 argumentTypes parms.  Does not find inherited  methods */
	 public static boolean hasMethodWithMatchingNameAndParms(String aName, Object obj, String[] parms) throws NoSuchMethodException, Exception{
		Class[] arguments = ClassUtil.getClassesForStringArguments(parms);
		if ( obj.getClass().getDeclaredMethod(aName, arguments) == null) {
			return false;
		}
		return true;

	 }
}