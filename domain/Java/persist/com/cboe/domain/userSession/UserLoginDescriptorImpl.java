package com.cboe.domain.userSession;

import com.cboe.interfaces.domain.userSession.*;
import com.cboe.exceptions.*;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.persistenceService.*;
import java.lang.reflect.*;
import java.util.*;
 
/**
 *  A persistent implementation of an UserLoginDescriptor object.
 *
 *  @author Steven Sinclair
 */
public class UserLoginDescriptorImpl extends PersistentBObject implements UserLoginDescriptor
{
	public static final String TABLE_NAME = "userLogin";

	protected UserSessionDescriptor userSession;
	protected String sourceComponent;
	protected int referenceCount;

	private static Field _userSession;
	private static Field _sourceComponent;
	private static Field _referenceCount;

	private static Vector classDescriptor;

	/**
	 *  This static block will be regenerated if persistence is regenerated
	 */
	static
	{ 
		try
		{
			_userSession = UserLoginDescriptorImpl.class.getDeclaredField("userSession");
			_sourceComponent = UserLoginDescriptorImpl.class.getDeclaredField("sourceComponent");
			_referenceCount = UserLoginDescriptorImpl.class.getDeclaredField("referenceCount");
		}
		catch (NoSuchFieldException ex)
		{
			System.out.println(ex); 
		}
	}

    public UserLoginDescriptorImpl()
    {
      super();
      setUsing32bitId(true);
    }

	public UserSessionDescriptor getUserSession()
	{
		return (UserSessionDescriptor)editor.get(_userSession, userSession);
	}

	public String getSourceComponent()
	{
		return (String)editor.get(_sourceComponent, sourceComponent);
	}

	public int getReferenceCount()
	{
		return getReferenceCount();
	}

	public void setUserSession(UserSessionDescriptor userSession)
	{
		editor.set(_userSession, userSession, this.userSession);
	}

	public void setSourceComponent(String sourceComponent)
	{
		editor.set(_sourceComponent, sourceComponent, this.sourceComponent);
	}

	public void setReferenceCount(int referenceCount)
	{
		editor.set(_referenceCount, referenceCount, this.referenceCount);
	}

	public void incrReferenceCount()
	{
		setReferenceCount(getReferenceCount()+1);
	}

	public void decrReferenceCount()
	{
		setReferenceCount(Math.max(0, getReferenceCount()-1));
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
				tempVector.addElement(AttributeDefinition.getForeignRelation(UserSessionDescriptorImpl.class, "userSession", _userSession));
				tempVector.addElement(AttributeDefinition.getAttributeRelation("source", _sourceComponent));
				tempVector.addElement(AttributeDefinition.getAttributeRelation("refCount", _referenceCount));
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
