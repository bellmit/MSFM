package com.cboe.domain.util;

/**
 * This is a hashable container class for userId, sessionName and ClassKey
 * @author Keval Desai
 * @version 12/5/00
 */
public class UserSessionClassContainer {
    private String userId;
	private String sessionName;
	private int classKey;
	private int hashcode;

    /**
      * Sets the internal fields to the passed values
      */
    public UserSessionClassContainer(String userId, String sessionName, int classKey) {
		this.userId = userId;
		this.sessionName = sessionName;
		this.classKey = classKey;
		hashcode = (userId.hashCode() + sessionName.hashCode() + new Integer(classKey).hashCode() ) / 3;
    }
    public String getUserId()
    {
        return userId;
    }
    public String getSessionName()
    {
        return sessionName;
    }
    public int getClassKey()
    {
        return classKey;
    }

    /**
      * The equals for the key.
      * @param obj Object
      * @return boolean
      */
    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof UserSessionClassContainer)) {
            return ((classKey == ((UserSessionClassContainer) obj).classKey) &&
                    userId.equals(
                       ((UserSessionClassContainer) obj).userId) &&
					 sessionName.equals(
                       ((UserSessionClassContainer) obj).sessionName));

         }
        return false;
    }

    /**
      * The hashCode for the key.
      * @return int
      */
    public int hashCode() {
        return hashcode;
    }

    /**
      * The toString() for the key.
      * @return String
      */
    public String toString() {
        StringBuilder buf = new StringBuilder(60);

        buf.append(" UserId: ")
        .append(userId)
		.append(" SessionName: ")
		.append(sessionName)
		.append(" ClassKey: ")
		.append(classKey);
        return buf.toString();
    }
}