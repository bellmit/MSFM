package com.cboe.interfaces.domain.user;

// Source file: d:/sources/com/cboe/interfaces/domain/user/Preference.java

import com.cboe.idl.cmiUser.*;

/**
 * This is a representation of a name/value pair.
 *
 * @author John Wickberg
 */
public interface Preference
{
/**
 * Returns the name of the preference.
 *
 * @return preference name
 */
public String getPreferenceName();
/**
 * Returns the value of the preference.
 *
 * @return preference value
 */
public String getValue();
/**
 * Changes the value of the preference.
 *
 * @param newValue New preference value.
 */
public void setValue(String newValue);
}
