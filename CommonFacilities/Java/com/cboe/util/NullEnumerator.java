package com.cboe.util;

import java.util.*;

/**
 * This class is used to create a null enumeration, i.e., an enumeration that alway returns false on hasMoreElements and
 * will throw an exception if the nextElement method is used.
  * 
 * @author John Wickberg
 */
public class NullEnumerator implements Enumeration {
/**
 * This is the constructor for the null enumerator.
 */
public NullEnumerator () {
}
/**
 * This method always returns false, there are not elements in a null enumeration.
 * @return always false, no elements
 */
public boolean hasMoreElements() {
	return false;
}
/**
 * This method always thows an exception since a null enumeration has no elements.
 * @return will not return, always throws NoSuchElementException
 */
public Object nextElement() {
	throw new NoSuchElementException("Null Enumerator nextElement method was called");	
}
}
