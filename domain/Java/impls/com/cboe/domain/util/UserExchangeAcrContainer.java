package com.cboe.domain.util;

/**
 * This is a hashable container class for our data struct.
 * @author Keith A. Korecky
 */
public class UserExchangeAcrContainer
{
    private String userId;
    private String exchange;
	private String acronym;
	private int hashCode;

    /**
      * Sets the internal fields to the passed values
      */
    public UserExchangeAcrContainer(String userId, String exchange, String acronym)
    {
        this.userId = userId;
        this.exchange = exchange;
		this.acronym = acronym;
		hashCode = (exchange.hashCode() + acronym.hashCode() + userId.hashCode()) >> 1;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getExchange()
    {
        return exchange;
    }

    public String getAcronym()
    {
        return acronym;
    }

    /**
     * The equals for the key.
     * this is a multipart type comparison to help with conversion
     * and cross ref. type needs.
     * allows "lookup" by just user, exchange & acronym or full key
     * @param obj Object
     * @return boolean
     */
    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof UserExchangeAcrContainer))
        {
            UserExchangeAcrContainer compareObj = (UserExchangeAcrContainer)obj;
            return (userId.equals(compareObj.userId)
                    && exchange.equals(compareObj.exchange)
                    && acronym.equals(compareObj.acronym)
                   );
        }
        return false;
    }

    /**
      * The hashCode for the key.
      * @return int
      */
    public int hashCode()
    {
        return hashCode;
    }

    /**
      * The toString() for the key.
      * @return String
      */
    public String toString()
    {
        StringBuilder buf = new StringBuilder(50);
        buf.append(" UserId: ")
        .append(userId)
        .append(" Exchange: ")
        .append(exchange)
        .append(" Acronym: ")
        .append(acronym);
        return buf.toString();
    }
}
