package com.cboe.infrastructureServices.foundationFramework.utilities;

import java.util.*;
import java.lang.reflect.*;
 
public class FieldUtil {

	FieldUtil() {
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
}