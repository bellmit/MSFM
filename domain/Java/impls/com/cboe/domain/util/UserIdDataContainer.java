package com.cboe.domain.util;

/**
 * This is a hashable container class for our data struct.
 * @author Connie Feng
 */
public class UserIdDataContainer {
    private String userId;
	private Object data;
	private int hashcode;

    /**
      * Sets the internal fields to the passed values
      */
    public UserIdDataContainer(String userId, Object data) {
		this.userId = userId;
		this.data = data;
		hashcode = (userId.hashCode() + data.hashCode()) >> 1;
    }
    public String getUserId()
    {
        return userId;
    }

    public Object getData()
    {
        return data;
    }
    /**
      * The equals for the key.
      * @param obj Object
      * @return boolean
      */
    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof UserIdDataContainer)) {
            return ( userId.equals(
                       ((UserIdDataContainer) obj).userId) &&
					data.equals(
                       ((UserIdDataContainer) obj).data));

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
        StringBuilder buf = new StringBuilder(50);

        buf.append(" UserId: ")
        .append(userId)
        .append(" Data: ")
        .append(data);
        return buf.toString();
    }
}
