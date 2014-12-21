/*
 * Created by IntelliJ IDEA.
 * User: chenj
 * Date: Jan 7, 2003
 * Time: 1:04:28 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.domain.util;

public class UserClassContainer {
    private String userId;
	private int classKey;
	private int hashCode;

    /**
      * Sets the internal fields to the passed values
      */
    public UserClassContainer(String userId, int classKey) {
		this.userId = userId;
		this.classKey = classKey;
		hashCode = (userId.hashCode() + new Integer(classKey).hashCode() ) >> 1;
    }

    public UserClassContainer(int userId, int classKey) {
		this.userId = (new Integer(userId)).toString();
		this.classKey = classKey;
    }

    public String getUserId()
    {
        return userId;
    }
    public int getClassKey()
    {
        return classKey;
    }
    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof UserClassContainer)) {
            return ((classKey == ((UserClassContainer) obj).classKey) &&
                    userId.equals(
                       ((UserClassContainer) obj).userId) );

         }
        return false;
    }

    public int hashCode() {
        return hashCode;
    }

    /**
      * The toString() for the key.
      * @return String
      */
    public String toString() {
        StringBuilder buf = new StringBuilder(35);

        buf.append(" UserId: ")
        .append(userId)
		.append(" ClassKey: ")
		.append(classKey);
        return buf.toString();
    }
}
