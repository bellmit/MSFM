package com.cboe.domain.user;

//----------------------------------------------------------------------
// Source file: Java/com/cboe/domain/user/UserImpl.java
//
// PACKAGE: com.cboe.domain.user
//----------------------------------------------------------------------
// Copyright (c) 1999 The Chicago Board Options Exchange. All Rights Reserved.
//----------------------------------------------------------------------

import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.interfaces.domain.user.UserEnablementElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Enumeration;

/**
 */
public class UserEnablementElementImpl extends PersistentBObject implements UserEnablementElement
{
	/**
	 * Name of database table.
	 */
	public static final String TABLE_NAME = "sbt_user_enablement";
	/**
	 * User login name.
	 */
	private String userId;
	/**
	 * user's key
	 */
	private int userKey;
	/**
	 * trading session name
	 */
	private String sessionName;
	/**
	 * enablement product type
	 */
	private short productType;

	// JavaGrinder variables
	static Field _userId;
	static Field _userKey;
	static Field _sessionName;
	static Field _productType;

	static Vector classDescriptor;

	/**
	* This static block will be regenerated if persistence is regenerated.
	*/
	static { /*NAME:fieldDefinition:*/
		try{
			_userId = UserEnablementElementImpl.class.getDeclaredField("userId");
			_userKey = UserEnablementElementImpl.class.getDeclaredField("userKey");
			_sessionName = UserEnablementElementImpl.class.getDeclaredField("sessionName");
			_productType = UserEnablementElementImpl.class.getDeclaredField("productType");
			_userId.setAccessible(true);
			_userKey.setAccessible(true);
			_sessionName.setAccessible(true);
			_productType.setAccessible(true);
		}
		catch (NoSuchFieldException ex) { System.out.println(ex); }
	}

	/**
	 */
	public UserEnablementElementImpl()
	{
		super();
        setUsing32bitId(true);
	}

	/**
	 */
	public String getUserId()
	{
		return (String) editor.get(_userId, userId);
	}

	/**
	 */
	public int getUserKey()
	{
		return editor.get(_userKey, userKey);
	}

	/**
	 */
	public String getSessionName()
	{
		return (String)editor.get(_sessionName, sessionName);
	}

	/**
	 */
	public short getProductType()
	{
		return editor.get(_productType, productType);
	}

	/**
	 * Describe how this class relates to the relational database.
	 */
	public void initDescriptor()
	{
		synchronized (AcronymUserImpl.class)
		{
			if (classDescriptor != null)
				return;
			Vector tempDescriptor = getSuperDescriptor();
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("user_id", _userId));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("user_key", _userKey));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("session_name", _sessionName));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("product_type", _productType));
			classDescriptor = tempDescriptor;
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
	 */
	public void setUserId(String aValue)
	{
		editor.set(_userId, aValue, userId);
	}

	/**
	 */
	public void setUserKey(int aValue)
	{
		editor.set(_userKey, aValue, userKey);
	}

	/**
	 */
	public void setSessionName(String aValue)
	{
		editor.set(_sessionName, aValue, sessionName);
	}

	/**
	 */
	public void setProductType(short aValue)
	{
		editor.set(_productType, aValue, productType);
	}

	/**
	 * This method allows me to get arounds security problems with updating
	 * and object from a generic framework.
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

