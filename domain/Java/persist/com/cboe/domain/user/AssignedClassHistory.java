package com.cboe.domain.user;

import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import java.lang.reflect.Field;
import java.util.Vector;

/**
 * A history of the changes in the association between a user and one of their assigned classes.
 *
 * @author Steven Sinclair
 */
public class AssignedClassHistory extends PersistentBObject {

	public static final char ADD = 'A';
	public static final char REMOVE = 'R';
	public static final char UPDATE = 'U';

	/**
	 * Table name used for object mapping.
	 */
	public static final String TABLE_NAME = "asgn_class_history";
	/**
	 * Class key of the assigned class.
	 */
	private int assignedClassKey;

	private char actionType;
	private long dateTime;
	private int membershipKey;
	private String userAcronym;
	private String userExchange;
    private short assignmentType; 
    private String sessionName;

	/*
	 * Fields for JavaGrinder.
	 */
	private static Field _actionType;
	private static Field _membershipKey;
	private static Field _userAcronym;
	private static Field _userExchange;
	private static Field _assignedClassKey;
	private static Field _dateTime;
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
			_actionType = AssignedClassHistory.class.getDeclaredField("actionType");
			_membershipKey = AssignedClassHistory.class.getDeclaredField("membershipKey");
			_userAcronym = AssignedClassHistory.class.getDeclaredField("userAcronym");
			_userExchange = AssignedClassHistory.class.getDeclaredField("userExchange");
			_assignedClassKey = AssignedClassHistory.class.getDeclaredField("assignedClassKey");
			_dateTime = AssignedClassHistory.class.getDeclaredField("dateTime");
			_assignmentType = AssignedClassHistory.class.getDeclaredField("assignmentType");
			_sessionName = AssignedClassHistory.class.getDeclaredField("sessionName");
			_actionType.setAccessible(true);
			_membershipKey.setAccessible(true);
			_userAcronym.setAccessible(true);
			_userExchange.setAccessible(true);
			_assignedClassKey.setAccessible(true);
			_dateTime.setAccessible(true);
            _assignmentType.setAccessible(true);
            _sessionName.setAccessible(true);
		}
		catch (Exception e) {
			System.out.println("Unable to initialize JavaGrinder fields for AssignedClassHistory: " + e);
		}
	}

	/**
	 * Constructs a new assignment.  This constructor is needed for queries.
	 */
	public AssignedClassHistory() {
	}

	/**
	 * Constructs a new assignment history, time stamped to this instant.
	 *
	 * @param newActionType - one of ADD, REMOVE or UPDATE
	 * @param memKey membership key of user who owns assignment
	 * @param userAcr acronym for user who owns assignment
	 * @param userExch exchange for user who owns assignment
	 * @param newClassKey key of the assignedClassKey
	 */
	public AssignedClassHistory(char newActionType, int memKey, String userAcr, String userExch, int newClassKey, short asgnType, String session) {
		setActionType(newActionType);
		setMembershipKey(memKey);
		setUserAcronym(userAcr);
		setUserExchange(userExch);
		setAssignedClassKey(newClassKey);
		setDateTime();
        setAssignmentType(asgnType);
        setSessionName(session);
	}

	/**
	 * Gets assigned class.
	 */
	public int getAssignedClassKey() {
		return editor.get(_assignedClassKey, assignedClassKey);
	}

	public int getMembershipKey() {
		return editor.get(_membershipKey, membershipKey);
	}

	public String getUserAcronym() {
		return (String) editor.get(_userAcronym, userAcronym);
	}

	public String getUserExchange() {
		return (String) editor.get(_userExchange, userExchange);
	}

	public char getActionType() {
		return editor.get(_actionType, actionType);
	}

	public long getDateTime() {
		return editor.get(_dateTime, dateTime);
	}

	public short getAssignmentType() {
		return (short)editor.get(_assignmentType, assignmentType);
	}

	public String getSessionName() {
		return (String)editor.get(_sessionName, sessionName);
	}

	/**
	 * Describe how this class relates to the relational database.
	 */
	public void initDescriptor()
	{
		synchronized (AssignedClassHistory.class)
		{
			if (classDescriptor != null)
				return;
			Vector tempDescriptor = getSuperDescriptor();
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("action_type", _actionType));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("ent_uid", _membershipKey));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("user_acr", _userAcronym));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("user_exch", _userExchange));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("asgn_class_key", _assignedClassKey));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("date_time", _dateTime));
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
	public void setAssignedClassKey(int classKey) {
		editor.set(_assignedClassKey, classKey, assignedClassKey);
	}
	
	public void setMembershipKey(int aValue) {
		editor.set(_membershipKey, aValue, membershipKey);
	}

	public void setUserAcronym(String aValue) {
		editor.set(_userAcronym, aValue, userAcronym);
	}

	public void setUserExchange(String aValue) {
		editor.set(_userExchange, aValue, userExchange);
	}

	/**
	 * Sets the action taken (added, remove)
	 * one of ADD or REMOVE
	 */
	public void setActionType(char newActionType) {
		editor.set(_actionType, newActionType, actionType);
	}

	/**
	 * Sets the date / time stamp for this history event to this instant
	 */
	public void setDateTime() {
		setDateTime(System.currentTimeMillis());
	}

	/**
	 * Sets the date / time stamp for this history event
	 */
	public void setDateTime(long newDateTimeMillis) {
		editor.set(_dateTime, newDateTimeMillis, dateTime);
	}

	/**
	 * Sets the assignment type (MM, eDPM, RMM)
	 */
	public void setAssignmentType(short asgnType) {
		editor.set(_assignmentType, asgnType, this.assignmentType);
	}

	/**
	 * Sets the session name
	 */
	public void setSessionName(String session) {
		editor.set(_sessionName, session, this.sessionName);
	}

	/**
	 * Formats this assignment as a string.
	 */
	public String toString() {
		return getUserAcronym() + ":" + getUserExchange() + " - " + getAssignedClassKey();
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

