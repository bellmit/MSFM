/*
 * Created by IntelliJ IDEA.
 * User: chenj
 * Date: Jan 10, 2003
 * Time: 3:16:27 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.domain.util;

public class ObjectKeyContainer {
    private Object object;
	private int key;
	private int hashCode;

    /**
      * Sets the internal fields to the passed values
      */
    public ObjectKeyContainer(Object object, int key) {
		this.object = object;
		this.key = key;
		hashCode = (object.hashCode() + new Integer(key).hashCode() ) >> 1;
    }
    public Object getObject()
    {
        return object;
    }
    public int getKey()
    {
        return key;
    }
    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof ObjectKeyContainer)) {
            return ((key == ((ObjectKeyContainer) obj).key) &&
                    object.equals(((ObjectKeyContainer) obj).object));
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
        StringBuilder buf = new StringBuilder(20);

        buf.append(" object: ")
        .append(object)
		.append(" key: ")
		.append(key);
        return buf.toString();
    }
}
