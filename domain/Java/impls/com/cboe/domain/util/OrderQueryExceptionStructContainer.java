package com.cboe.domain.util;

/**
* Wrapper object for class key and product state information.  The
* product state applies to all products in the class
* @author Connie Feng
*/
public class OrderQueryExceptionStructContainer
{
    private String          userId;
    private Integer         exceptionMapNumber;
    private String          description;
    private int[]           groups;
    private int 			hashcode; 

    /**
     * OrderQueryExceptionStructContainer constructor.
     */
    private OrderQueryExceptionStructContainer()
    {
    	super();
    	hashcode = ( ( groups.hashCode() + userId.hashCode() + exceptionMapNumber.hashCode() + description.hashCode() ) >> 1 );
    }

    public OrderQueryExceptionStructContainer(String userId, int exceptionMapNumber, String description)
    {
        this( new int[0], userId, exceptionMapNumber, description );
        hashcode = ( ( groups.hashCode() + userId.hashCode() + this.exceptionMapNumber.hashCode() + description.hashCode() ) >> 1 );
    }

    public OrderQueryExceptionStructContainer(int[] groups, String userId, int exceptionMapNumber, String description)
    {
        this.groups             = groups;
        this.userId             = userId;
        this.exceptionMapNumber = new Integer( exceptionMapNumber );
        this.description        = description;
        hashcode = ( ( groups.hashCode() + userId.hashCode() + this.exceptionMapNumber.hashCode() + description.hashCode() ) >> 1 );
    }

    /**
     * The equals for the key.
     */
    public boolean equals(Object obj)
    {
    	if ((obj != null) && (obj instanceof OrderQueryExceptionStructContainer))
    	{
    		return (    (userId == ((OrderQueryExceptionStructContainer) obj).userId)
                  && (exceptionMapNumber == ((OrderQueryExceptionStructContainer) obj).exceptionMapNumber)
                  && (description == ((OrderQueryExceptionStructContainer) obj).description)
                  && (groups == ((OrderQueryExceptionStructContainer) obj).groups)
                  );
    	}
    	return false;
    }

    /**
     * The hashCode for the key.
     */
    public int hashCode()
    {
    	return hashcode;
    }

    /**
     * The getter method to the groups[]
     */
    public int[] getGroups()
    {
        return groups;
    }

    /**
     * The getter method to the userId.
     */
    public String getUserId()
    {
        return userId;
    }

    /**
     * The getter method to the excpetionMapNumber.
     */
    public int getExceptionMapNumber()
    {
        return exceptionMapNumber.intValue();
    }

    /**
     * The getter method to the description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * The toString for the key.
     */
    public String toString()
    {
        StringBuilder buf = new StringBuilder(200);

        buf.append(" groups: ")
        .append(groups)
        .append(" userId: ")
        .append(userId)
        .append(" exceptionMapNumber: ")
        .append(exceptionMapNumber.toString())
        .append(" description: ")
        .append(description);

        return buf.toString();
    }
}// end  class OrderQueryExceptionStructContainer
