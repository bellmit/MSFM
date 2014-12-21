package com.cboe.domain.util;

/**
* Wrapper object for class key and product state information.  The
* product state applies to all products in the class
* @author Connie Feng
*/
public class ClassProductStateStructContainer
{
    private int         classKey;
    private short       productState;
    private String      sessionName;
    private int hashcode;

    /**
     * ClassProductStateStructContainer constructor.
     */
    private ClassProductStateStructContainer() {
    	super();
    	hashcode = (classKey + productState + sessionName.hashCode())/3;
    }

    public ClassProductStateStructContainer(int classKey, String sessionName, short productState)
    {
    	this.classKey = classKey;
    	this.productState = productState;
        this.sessionName = sessionName;
        hashcode = (classKey + productState + sessionName.hashCode())/3;
    }

    /**
     * The equals for the key.
     */
    public boolean equals(Object obj)
    {
    	if ((obj != null) && (obj instanceof ClassProductStateStructContainer))
    	{
    		return ((classKey == ((ClassProductStateStructContainer) obj).classKey) &&
    		       (productState == ((ClassProductStateStructContainer) obj).productState) &&
                    sessionName.equals(((ClassProductStateStructContainer)obj).getSessionName()));
    	}
    	return false;
    }

    /**
     * The hashCode for the key.
     */
    public int hashCode() {
    	return hashcode;
    }

    /**
     * The getter method to the class key.
     */
    public int getClassKey()
    {
        return classKey;
    }

    /**
     * The getter method to the product state.
     */
    public short getProductState()
    {
        return productState;
    }

    public String getSessionName()
    {
        return sessionName;
    }
    /**
     * The toString for the key.
     */
    public String toString()
    {
        StringBuilder buf = new StringBuilder(50);

        buf.append(" classKey: ")
        .append(classKey)
        .append(" productState: ")
        .append(productState)
        .append(" sessionName: ")
        .append(sessionName);

        return buf.toString();
    }
}// end  class ClassProductStateStructContainer
