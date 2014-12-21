package com.cboe.domain.util;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;

/**
 * This is a hashable container class for userId, FirmKey
 * @author Keval Desai
 * @version 12/5/00
 */
public class UserIdFirmKeyContainer {
    private String userId;
	private ExchangeFirmStruct firmKey;
	private int hashcode; 

    /**
      * Sets the internal fields to the passed values
      */
    public UserIdFirmKeyContainer(String userId, ExchangeFirmStruct firmKey) {
		this.userId = userId;
		this.firmKey = firmKey;
		hashcode = (userId.hashCode() + firmKey.exchange.hashCode()+ firmKey.firmNumber.hashCode()) / 3;
    }
    public String getUserId()
    {
        return userId;
    }
    public ExchangeFirmStruct getFirmKey()
    {
        return firmKey;
    }

    /**
      * The equals for the key.
      * @param obj Object
      * @return boolean
      */
    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof UserIdFirmKeyContainer)) {
            return ( userId.equals(
                       ((UserIdFirmKeyContainer) obj).userId) &&
                    firmKey.firmNumber.equals(
                       ((UserIdFirmKeyContainer) obj).firmKey.firmNumber) &&
					firmKey.exchange.equals(
                       ((UserIdFirmKeyContainer) obj).firmKey.exchange));

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
		.append(" FirmKey: ")
		.append(firmKey.exchange)
		.append(firmKey.firmNumber);
        return buf.toString();
    }
}