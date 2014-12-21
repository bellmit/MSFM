package com.cboe.common.utils;

public class MutableInteger implements Comparable<MutableInteger> {

	private int value = 0;

	public MutableInteger() {
	}

	public MutableInteger( int initalValue ) {
		value = initalValue;
	}

	public final void set( int newValue ) {
		value = newValue;
	}

	public final int get() {
		return value;
	}

	public final int intValue() {
		return value;
	}

    /**
     * Returns a hash code for this <code>MutableInteger</code>.
     *
     * @return  a hash code value for this object, equal to the 
     *          primitive <code>int</code> value represented by this 
     *          <code>Integer</code> object. 
     */
    public int hashCode() {
    	return value;
    }

    /**
     * Compares this object to the specified object.  The result is
     * <code>true</code> if and only if the argument is not
     * <code>null</code> and is an <code>Integer</code> object that
     * contains the same <code>int</code> value as this object.
     *
     * @param   obj   the object to compare with.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     */
    public final boolean equals(Object obj) {
    	if (obj instanceof MutableInteger) {
    		return value == ((MutableInteger)obj).value;
    	} else if ( obj instanceof Integer ) {
    		// Make this usuable against Integer objects, too.
    		return value == ((Integer)obj).intValue();
    	}
    	
    	return false;
    }

    /**
     * Compares two <code>Integer</code> objects numerically.
     *
     * @param   anotherInteger   the <code>Integer</code> to be compared.
     * @return	the value <code>0</code> if this <code>Integer</code> is
     * 		equal to the argument <code>Integer</code>; a value less than
     * 		<code>0</code> if this <code>Integer</code> is numerically less
     * 		than the argument <code>Integer</code>; and a value greater 
     * 		than <code>0</code> if this <code>Integer</code> is numerically
     * 		 greater than the argument <code>Integer</code> (signed
     * 		 comparison).
     * @since   1.2
     */
    public final int compareTo(MutableInteger anotherInteger) {
    	int thisVal = value;
    	int anotherVal = anotherInteger.value;
    	return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
    }

    public String toString() {
    	return "" + value;
    }
}
