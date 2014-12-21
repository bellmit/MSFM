package com.cboe.domain.userSession;

import com.cboe.interfaces.domain.userSession.*;
import com.cboe.exceptions.*;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.*;
import java.lang.reflect.*;
import java.util.*;
 

/**
 *  @author Steven Sinclair
 */
public class UserLoginHistoryImpl extends PersistentBObject implements UserLoginHistory
{
	public static final String TABLE_NAME = "userHistory";

	protected int sessionKey;
	protected int action;
	protected String userId;
	protected String sourceComponent;
	protected long time;
	protected String description;

	private static Field _sessionKey;
	private static Field _action;
	private static Field _userId;
	private static Field _sourceComponent;
	private static Field _time;
	private static Field _description;

	private static Vector classDescriptor;

	public int getSessionKey()
	{
		return editor.get(_sessionKey, sessionKey);
	}
	public int getAction()
	{
		return editor.get(_action, action);
	}
	public String getUserId()
	{
		return (String)editor.get(_userId, userId);
	}
	public String getSourceComponent()
	{
		return (String)editor.get(_sourceComponent, sourceComponent);
	}
	public Date getTime()
	{
		return new Date(editor.get(_time, time));
	}
	public String getDescription()
	{
		return (String)editor.get(_description, description);
	}

	public void setSessionKey(int sessionKey)
	{
		editor.set(_sessionKey, sessionKey, this.sessionKey);
	}
	public void setAction(int action)
	{
		editor.set(_action, action, this.action);
	}
	public void setUserId(String userId)
	{
		editor.set(_userId, userId, this.userId);
	}
	public void setSourceComponent(String sourceComponent)
	{
		editor.set(_sourceComponent, sourceComponent, this.sourceComponent);
	}
	public void setTime(Date time)
	{
		editor.set(_time, time.getTime(), this.time);
	}
	public void setDescription(String description)
	{
		editor.set(_description, description, this.description);
	}

	/**
	 *  This static block will be regenerated if persitence is regenerated
	 */
	static
	{ 
		try
		{
			_sessionKey = UserLoginDescriptorImpl.class.getDeclaredField("sessionOpen");
			_action = UserLoginDescriptorImpl.class.getDeclaredField("action");
			_userId = UserLoginDescriptorImpl.class.getDeclaredField("userId");
			_sourceComponent = UserLoginDescriptorImpl.class.getDeclaredField("sourceComponent");
			_time = UserLoginDescriptorImpl.class.getDeclaredField("time");
			_description = UserLoginDescriptorImpl.class.getDeclaredField("desciption");
		}
		catch (NoSuchFieldException ex)
		{
			System.out.println(ex); 
		}
	}

    public UserLoginHistoryImpl()
    {
      super();
      setUsing32bitId(true);
    }

	/**
	 * Describe how this class relates to the relational database.
	 */
	public void initDescriptor()
	{
		synchronized (UserLoginDescriptorImpl.class)
		{
			if (classDescriptor == null)
			{
				Vector tempVector = getSuperDescriptor();
				tempVector.addElement(AttributeDefinition.getAttributeRelation("sessionKey", _sessionKey));
				tempVector.addElement(AttributeDefinition.getAttributeRelation("userId", _userId));
				tempVector.addElement(AttributeDefinition.getAttributeRelation("source", _sourceComponent));
				tempVector.addElement(AttributeDefinition.getAttributeRelation("action", _action));
				tempVector.addElement(AttributeDefinition.getAttributeRelation("time", _time));
				tempVector.addElement(AttributeDefinition.getAttributeRelation("description", _description));
				classDescriptor = tempVector;
			}
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
