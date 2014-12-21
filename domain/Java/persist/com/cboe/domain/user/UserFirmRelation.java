package com.cboe.domain.user;

import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;


import java.lang.reflect.Field;
import java.util.Vector;

/**
 * An association between an independent broker and a clearing firm
 *
 * @author Matt Sochacki
 */
public class UserFirmRelation extends PersistentBObject {

	/**
	 * Table name used for object mapping.
	 */
	public static final String TABLE_NAME = "user_firm_relation";

	/**
	 * independent broker in the relationship.
	 */
	private AcronymUserImpl user;

	/**
	 * Firm in the relationship.
	 */
	private int firmKey;

	/*
	 * Fields for JavaGrinder.
	 */
	private static Field _user;
	private static Field _firmKey;

	/*
	 * JavaGrinder attribute descriptions.
	 */
	private static Vector classDescriptor;

	/*
	 * Initialize fields
	 */
	static {
		try {
			_user = UserFirmRelation.class.getDeclaredField("user");
			_firmKey = UserFirmRelation.class.getDeclaredField("firmKey");
		}
		catch (Exception e) {
			System.out.println("Unable to initialize JavaGrinder fields for UserFirmRelation: " + e);
		}
	}

	/**
	 * Constructs a new relationship.  This constructor is needed for queries.
	 */
	public UserFirmRelation() {
	}

	/**
	 * Constructs a new assignment.
	 *
	 * @param user user in the relationship
	 * @param firmKey firmKey
	 */
	public UserFirmRelation(AcronymUserImpl user, int relatedFirm) {
		super();
		setUser(user);
		setFirmKey(relatedFirm);
	}

	/**
	 * Gets firmKey key.
	 */
	public int getFirmKey() {
		return editor.get(_firmKey, firmKey);
	}

	/**
	 * Gets the user
	 */
	public AcronymUserImpl getUser() {
		return (AcronymUserImpl) editor.get(_user, user);
	}

	/**
	 * Describe how this class relates to the relational database.
	 */
	public void initDescriptor()
	{
		synchronized (UserFirmRelation.class)
		{
			if (classDescriptor != null)
				return;
			Vector tempDescriptor = getSuperDescriptor();
			tempDescriptor.addElement(AttributeDefinition.getForeignRelation(AcronymUserImpl.class, "user_key", _user));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("firm_key", _firmKey));
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
	 * Sets the firm key.
	 *
	 * @param firmKey
	 */
	protected void setFirmKey(int aFirmKey) {
		editor.set(_firmKey, aFirmKey, firmKey);
	}

	/**
	 * Sets the user
	 *
	 * @param newUser owner of the assignment
	 */
	private void setUser(AcronymUserImpl newUser) {
		editor.set(_user, newUser, user);
	}

	/**
	 * Formats this relationship as a string.
	 */
	public String toString() {
		AcronymUserImpl aUser = getUser();
		String userId = (null == aUser) ? "**no user**" : aUser.loggableName();
		return userId + " " + getFirmKey();
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

