package com.cboe.domain.userSession;

import com.cboe.interfaces.domain.userSession.*;
import com.cboe.exceptions.*;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.*;
import java.lang.reflect.*;
import java.util.*;
 
/**
 *  A persistent implementation of an UserSessionDescriptor object.
 *
 *  @author Steven Sinclair
 */
public class UserSessionDescriptorImpl extends PersistentBObject implements UserSessionDescriptor
{
	public static final String TABLE_NAME = "userSession";

	protected String userId;
	protected String lastLoginSource;
	protected boolean sessionOpen;
	protected Vector userLogins;

	private static Field _userId;
	private static Field _lastLoginSource;
	private static Field _sessionOpen;
	private static Field _userLogins;

	private static Vector classDescriptor;

	/**
	 *  This static block will be regenerated if persitence is regenerated
	 */
	static
	{ 
		try
		{
			_userId = UserLoginDescriptorImpl.class.getDeclaredField("userId");
			_lastLoginSource = UserLoginDescriptorImpl.class.getDeclaredField("lastLoginSource");
			_sessionOpen = UserLoginDescriptorImpl.class.getDeclaredField("sessionOpen");
			_userLogins = UserLoginDescriptorImpl.class.getDeclaredField("userLogins");
		}
		catch (NoSuchFieldException ex)
		{
			System.out.println(ex); 
		}
	}

    public UserSessionDescriptorImpl()
    {
      super();
      setUsing32bitId(true);
    }

	public void login(String sourceComponent)
	{
		UserLoginDescriptor desc = findBySourceComponent(sourceComponent);
		if (desc != null)
		{
			desc.incrReferenceCount();
		}
		else
		{
			try
			{
				UserLoginDescriptorHome loginDescriptorHome;
				loginDescriptorHome = (UserLoginDescriptorHome)HomeFactory.getInstance().findHome(UserLoginDescriptorHome.HOME_NAME);
				desc = loginDescriptorHome.create(this, sourceComponent);
			}
			catch (CBOELoggableException ex)
			{
				String msg="Failed to find user login descriptor home.";
				Log.exception(this, msg, ex);
				throw new RuntimeException(msg + " " + ex);
			}
			catch (org.omg.CORBA.UserException ex)
			{
				String msg="Error creating login descriptor.";
				Log.exception(this, msg, ex);
				throw new RuntimeException(msg + " " + ex);
			}
			addLogin(desc);
		}
	}

	public void logout(String sourceComponent)
	{
		UserLoginDescriptor desc = findBySourceComponent(sourceComponent);
		if (desc != null)
		{
			desc.decrReferenceCount();
			int count = desc.getReferenceCount();
			if (count < 1)
			{
				removeLogin(desc);
			}
		}
	}

	public UserLoginDescriptor findBySourceComponent(String sourceComponent)
	{
		Iterator iter = getUserLogins().iterator();
		while (iter.hasNext())
		{
			UserLoginDescriptor desc = (UserLoginDescriptor)iter.next();
			if (desc.getSourceComponent().equals(sourceComponent))
			{
				return desc;
			}
		}
		return null;
	}

	public Collection getUserLogins()
	{
		return (Collection)editor.get(_userLogins, userLogins);
	}

	public String getUserId()
	{
		return (String)editor.get(_userId, userId);
	}

	public String getLastLoginSource()
	{
		return (String)editor.get(_lastLoginSource, lastLoginSource);
	}

	public boolean isSessionOpen()
	{
		return (boolean)editor.get(_sessionOpen, sessionOpen);
	}

	public int getSessionKey()
	{
		return getObjectIdentifierAsInt();
	}

	public void setUserId(String userId)
	{
		editor.set(_userId, userId, this.userId);
	}

	public void setLastLoginSource(String lastLoginSource)
	{
		editor.set(_lastLoginSource, lastLoginSource, this.lastLoginSource);
	}

	public void setSessionOpen(boolean sessionOpen)
	{
		editor.set(_sessionOpen, sessionOpen, this.sessionOpen);
	}

	public void addLogin(UserLoginDescriptor userLogin)
	{
		userLogin.setUserSession(this);
		getUserLogins().add(userLogin);
	}

	public void removeLogin(UserLoginDescriptor userLogin)
	{
		userLogin.setUserSession(null);
		setLastLoginSource(userLogin.getSourceComponent());
		getUserLogins().remove(userLogin);
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
				tempVector.addElement(AttributeDefinition.getCollectionRelation(UserSessionDescriptorImpl.class, _userLogins));
				tempVector.addElement(AttributeDefinition.getAttributeRelation("userId", _userId));
				tempVector.addElement(AttributeDefinition.getAttributeRelation("lastLoginSource", _lastLoginSource));
				tempVector.addElement(AttributeDefinition.getAttributeRelation("sessionOpen", _sessionOpen));
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
