package com.cboe.domain.util;

import com.cboe.interfaces.domain.DomainBase;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.persistenceService.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * An implementation of the base class for persistent objects.
 *
 * @author John Wickberg
 */
public abstract class DomainBaseImpl  extends PersistentBObject implements DomainBase {
	public long createdTime;
	public long lastModifiedTime;
	static Field _createdTime;
	static Field _lastModifiedTime;
	/**
	* This static block will be regenerated if persistence is regenerated.
	*/
	static { /*NAME:fieldDefinition:*/
		try{
			_createdTime = DomainBaseImpl.class.getDeclaredField("createdTime");
			_lastModifiedTime = DomainBaseImpl.class.getDeclaredField("lastModifiedTime");

		}
		catch (NoSuchFieldException ex) { System.out.println(ex); }
	}
	private static Vector classDescriptor;
/**
 * DomainBaseImpl constructor comment.
 */
public DomainBaseImpl()
{
	super();
}
/**
 * @see DomainBase@getCreatedTime
 *
 * @author John Wickberg
 */
public long getCreatedTime() {
	return editor.get(_createdTime, createdTime);
}
/**
* A conveince method to get a copy of this static class descriptor.
*/
protected static Vector getDescriptor()
{
	return (Vector) classDescriptor.clone();
}
/**
 * @see DomainBase@getLastModifiedTime
 *
 * @author John Wickberg
 */
public long getLastModifiedTime() {
	return editor.get(_lastModifiedTime, lastModifiedTime);
}
/**
* Describe how this class relates to the relational database.
*/
private void initDescriptor()
{
	synchronized (DomainBaseImpl.class)
	{
		if (classDescriptor != null)
		{
			return; // already initialized
		}
		Vector tempDescriptor = getSuperDescriptor();
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("create_time", _createdTime));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("lmod_time", _lastModifiedTime));
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
	result.setClassDescription(classDescriptor);
	return result;
}
/**
 * @see DomainBase@resetLastModifiedTime
 *
 * @author John Wickberg
 */
public void resetLastModifiedTime() {
	setLastModifiedTime(System.currentTimeMillis());
}

public void save() throws PersistenceException
{
	// update times directly, transaction is in progress by during this call and
	// transaction log has already been applied to this object.
	if (isRetrievedFromDatabase())
	{
		lastModifiedTime = System.currentTimeMillis();
	}
	else
	{
		createdTime = lastModifiedTime = System.currentTimeMillis();
	}
	super.save();
}
/**
 * Sets created time.
 *
 * @param newTime creation time
 *
 * @author John Wickberg
 */
private void setCreatedTime(long newTime) {
	editor.set(_createdTime, newTime, createdTime);
}
/**
 * Sets last modified time.
 *
 * @param newTime modified time
 *
 * @author John Wickberg
 */
private void setLastModifiedTime(long newTime) {
	editor.set(_lastModifiedTime, newTime, lastModifiedTime);
}
	/**  This method allows me to get arounds security problems with updating
	* and object from a generic framework.
	*/
	public void update(boolean get, Object [] data, Field [] fields)
	{
		for(int i = 0; i < data.length; i++){
			try{
			if(get)
				data[i] = fields[i].get(this);
			else
				fields[i].set(this, data[i]);
			} catch(IllegalAccessException ex) { System.out.println(ex); }
			catch(IllegalArgumentException ex) { System.out.println(ex); }
		}
	}
}
