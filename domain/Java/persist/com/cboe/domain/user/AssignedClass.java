package com.cboe.domain.user;

import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import java.lang.reflect.Field;
import java.util.Vector;

/**
 * An association between a user and one of their assigned classes.
 *
 * @author John Wickberg
 */
public class AssignedClass extends PersistentBObject {
	/**
	 * Table name used for object mapping.
	 */
	public static final String TABLE_NAME = "asgn_class";
	/**
	 * Owner of the class assignment.
	 */
	private AcronymUserImpl user;
	/**
	 * Class key of the assigned class.
	 */
	private int assignedClass;
    /**
     * Type of assignment (regular, eDPM, RMM)
     */
    private short assignmentType;

    private String sessionName;

	/*
	 * Fields for JavaGrinder.
	 */
	private static Field _user;
	private static Field _assignedClass;
	private static Field _assignmentType;
	private static Field _sessionName;

	/*
	 * JavaGrinder attribute descriptions.
	 */
	private static Vector classDescriptor;

	/*
	 * Initialize fields
	 */
	static {
		try {
			_user = AssignedClass.class.getDeclaredField("user");
			_assignedClass = AssignedClass.class.getDeclaredField("assignedClass");
			_assignmentType = AssignedClass.class.getDeclaredField("assignmentType");
			_sessionName = AssignedClass.class.getDeclaredField("sessionName");
			_user.setAccessible(true);
			_assignedClass.setAccessible(true);
			_assignmentType.setAccessible(true);
			_sessionName.setAccessible(true);
		}
		catch (Exception e) {
			System.out.println("Unable to initialize JavaGrinder fields for AssignedClass: " + e);
		}
	}

	/**
	 * Constructs a new assignment.  This constructor is needed for queries.
	 */
	public AssignedClass() {
	}

	/**
	 * Constructs a new assignment.
	 *
	 * @param user user who owns assignment
	 * @param classKey key of the assignedClass
	 */
	public AssignedClass(AcronymUserImpl user, int classKey, short assignmentType, String sessionName) {
		super();
		setUser(user);
		setAssignedClass(classKey);
		setAssignmentType(assignmentType);
		setSessionName(sessionName);
	}

	/**
	 * Gets assigned class.
	 */
	public int getAssignedClass() {
		return editor.get(_assignedClass, assignedClass);
	}

	/**
	 * Gets assignment type.
	 */
	public short getAssignmentType() {
		return editor.get(_assignmentType, assignmentType);
	}

	/**
	 * Gets session name.
	 */
	public String getSessionName() {
		return (String)editor.get(_sessionName, sessionName);
	}

	/**
	 * Gets the owner of the assignment.
	 */
	public AcronymUserImpl getUser() {
		return (AcronymUserImpl) editor.get(_user, user);
	}

	/**
	 * Describe how this class relates to the relational database.
	 */
	public void initDescriptor()
	{
		synchronized (AssignedClass.class)
		{
			if (classDescriptor != null)
				return;
			Vector tempDescriptor = getSuperDescriptor();
			tempDescriptor.addElement(AttributeDefinition.getForeignRelation(AcronymUserImpl.class, "user_key", _user));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("asgn_class_key", _assignedClass));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("assignment_type", _assignmentType));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("session_name", _sessionName));
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
	 * Sets the assigned class.
	 *
	 * @param classKey key of the assigned class
	 */
	public void setAssignedClass(int classKey) {
		editor.set(_assignedClass, classKey, assignedClass);
	}

	/**
	 * Sets the assigned type.
	 *
	 * @param assignmentType - type of assignment
	 */
	public void setAssignmentType(short asgnType) {
		editor.set(_assignmentType, asgnType, this.assignmentType);
	}

	/**
	 * Sets the session name
	 *
	 * @param sessionName - the name of the session
	 */
	public void setSessionName(String session) {
		editor.set(_sessionName, session, this.sessionName);
	}

	/**
	 * Sets the owner of the assignment.
	 *
	 * @param newUser owner of the assignment
	 */
	public void setUser(AcronymUserImpl newUser) {
		editor.set(_user, newUser, user);
	}

	/**
	 * Formats this assignment as a string.
	 */
	public String toString() {
		AcronymUserImpl aUser = getUser();
		String userId = (null == aUser) ? "**no user**" : aUser.loggableName();
		return userId + " " + getAssignedClass();
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

