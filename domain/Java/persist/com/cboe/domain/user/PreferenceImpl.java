package com.cboe.domain.user;

//----------------------------------------------------------------------
// Source file: Java/com/cboe/domain/user/PreferenceImpl.java
//
// PACKAGE: com.cboe.domain.user
//----------------------------------------------------------------------
// Copyright (c) 1999 The Chicago Board Options Exchange. All Rights Reserved.
//----------------------------------------------------------------------

import com.cboe.idl.cmiUser.*;
import com.cboe.interfaces.domain.user.*;
import com.cboe.domain.util.*;
import com.cboe.infrastructureServices.persistenceService.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * A persistent implementation of <code>Preference</code>.
 *
 * @author John Wickberg
 * @author Brad Samuels
 */
public class PreferenceImpl extends DomainBaseImpl implements Preference
{
	/**
	 * Name of database table.
	 */
	public static final String TABLE_NAME = "pref";
	/**
	 * Owner of the preference.
	 */
	private int userKey;
	/**
	 * Type of preference.
	 */
	private int type;
	/**
	 * The name of this preference.
	 */
	private String preferenceName;
	/**
	 * The value of this preference.
	 */
	private String value;
	
	private static Field _userKey;
	private static Field _type;
	private static Field _preferenceName;
	private static Field _value;

	private static Vector classDescriptor;
	
	/**
	* This static block will be regenerated if persistence is regenerated. 
	*/
	static { /*NAME:fieldDefinition:*/
		try{
			_userKey = PreferenceImpl.class.getDeclaredField("userKey");
			_type = PreferenceImpl.class.getDeclaredField("type");
			_preferenceName = PreferenceImpl.class.getDeclaredField("preferenceName");
			_value = PreferenceImpl.class.getDeclaredField("value");
		}
		catch (NoSuchFieldException ex) { System.out.println(ex); }
	}
/**
 * Creates a default instance.
 *
 */
public PreferenceImpl()
{
	super();
}
/**
 * This method initializes the instance of a preference.
 * @author Brad Samuels
 * @param userKey owner of preference.
 * @param type type code for preference
 * @param preferenceName name of the preference
 * @param value value of the preference
 */
protected void create(int userKey, int type, String preferenceName, String value) {
	setUserKey(userKey);
	setType(type);
	setPreferenceName(preferenceName);
	setValue(value);
}
/**
 * @see Preference#getPreferenceName
 *
 */
public String getPreferenceName()
{
	String result = (String) editor.get(_preferenceName, preferenceName);
	return result;
}
/**
 * Gets type of preference.
 */
protected int getType()
{
	return editor.get(_type, type);
}
/**
 * Gets owner of preference.
 */
protected int getUserKey()
{
	return editor.get(_userKey, userKey);
}
/**
 * @see Preference#getValue
 *
 */
public String getValue()
{
	String result = (String) editor.get(_value, value);
	if(result == null)
	{
		result = "";
	}
	return result;
}
/**
 * Describe how this class relates to the relational database.
 */
public void initDescriptor()
{
	synchronized (PreferenceImpl.class)
	{
		if (classDescriptor != null)
			return;
		Vector tempVector = super.getDescriptor();
		tempVector.addElement(AttributeDefinition.getAttributeRelation("user_key", _userKey));
		tempVector.addElement(AttributeDefinition.getAttributeRelation("pref_type", _type));
		tempVector.addElement(AttributeDefinition.getAttributeRelation("pref_name", _preferenceName));
		tempVector.addElement(AttributeDefinition.getAttributeRelation("pref_value", _value));
		classDescriptor = tempVector;
	}
}
/**
* Needed to define table name and the description of this class.
*/
public ObjectChangesIF initializeObjectEditor()
{
	final DBAdapter result = (DBAdapter) super.initializeObjectEditor();
	if (classDescriptor == null)
		initDescriptor();
	result.setTableName(TABLE_NAME);
	result.setClassDescription(classDescriptor);
	return result;
}
/**
 * Sets name of preference.
 *
 * @param newName preference name
 * 
 */
private void setPreferenceName(String newName)
{
	editor.set(_preferenceName, newName, preferenceName);
}
/**
 * Sets the preference type.
 */
protected void setType(int newType)
{
	editor.set(_type, newType, type);
}
/**
 * Sets the preference owner.
 */
protected void setUserKey(int newKey)
{
	editor.set(_userKey, newKey, userKey);
}
/**
 * @see Preference#setValue
 *
 */
public void setValue(String newValue)
{
	editor.set(_value, newValue, value);
}
/**
 * This method allows me to get arounds security problems with updating
 * and object from a generic framework.
 * @param get This flag indicates whether the data is returned in <code>data</code> or set into <code>data</data>.
 * @param data The data array.
 * @param fields An array of field elements.
 */
public void update(boolean get, Object[] data, Field[] fields)
{
	for (int i = 0; i < data.length; i++)
	{
		try
		{
			if (get)
				data[i] = fields[i].get(this);
			else
				fields[i].set(this, data[i]);
		}
		catch (IllegalAccessException ex)
		{
			System.out.println(ex);
		}
		catch (IllegalArgumentException ex)
		{
			System.out.println(ex);
		}
	}
}
}
