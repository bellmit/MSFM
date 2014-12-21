package com.cboe.domain.util;

/**
 * This is a hashable container class for userId, sessionName and ProductKey
 * @author William Wei
 * @version 1/23/02
 */
public class UserSessionProductContainer {
    private String userId;
	private String sessionName;
	private int productKey;
	private int hashCode;

    /**
      * Sets the internal fields to the passed values
      */
    public UserSessionProductContainer(String userId, String sessionName, int productKey) {
		this.userId = userId;
		this.sessionName = sessionName;
		this.productKey = productKey;
		hashCode = (userId.hashCode() + sessionName.hashCode() + new Integer(productKey).hashCode() ) / 3;
    }
    public String getUserId()
    {
        return userId;
    }
    public String getSessionName()
    {
        return sessionName;
    }
    public int getProductKey()
    {
        return productKey;
    }

    /**
      * The equals for the key.
      * @param obj Object
      * @return boolean
      */
    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof UserSessionProductContainer)) {
            return ((productKey == ((UserSessionProductContainer) obj).productKey) &&
                    userId.equals(
                       ((UserSessionProductContainer) obj).userId) &&
					 sessionName.equals(
                       ((UserSessionProductContainer) obj).sessionName));

         }
        return false;
    }

    /**
      * The hashCode for the key.
      * @return int
      */
    public int hashCode() {
        return hashCode;
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
		.append(" ProductKey: ")
		.append(productKey);
        return buf.toString();
    }
}
