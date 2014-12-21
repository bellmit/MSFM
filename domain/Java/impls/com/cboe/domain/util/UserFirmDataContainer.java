package com.cboe.domain.util;

/**
 * This is a hashable container class for our data struct.
 * @author Connie Feng
 */
public class UserFirmDataContainer {
    private String userId;
	private String firmKey;
	private Object data;
	private int hashcode;

    /**
      * Sets the internal fields to the passed values
      */
    public UserFirmDataContainer(String userId, String firmKey, Object data) {
		this.userId = userId;
		this.firmKey = firmKey;
		this.data = data;
		hashcode = (userId.hashCode() + firmKey.hashCode() + data.hashCode()) / 3;
    }
    public String getUserId()
    {
        return userId;
    }
    public String getFirmKey()
    {
        return firmKey;
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
        if ((obj != null) && (obj instanceof UserFirmDataContainer)) {
            return ( userId.equals(
                       ((UserFirmDataContainer) obj).userId) &&
					 firmKey.equals(
                       ((UserFirmDataContainer) obj).firmKey) &&
					data.equals(
                       ((UserFirmDataContainer) obj).data));

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
        StringBuilder buf = new StringBuilder(100);

        buf.append(" UserId: ")
        .append(userId)
		.append(" FirmKey: ")
		.append(firmKey)
        .append(" Data: ")
        .append(data);
        return buf.toString();
    }
}
