package com.cboe.interfaces.domain;

/**
 * A base class for persistent objects.
 * 
 * @author John Wickberg
 */
public interface DomainBase {
/**
 * Gets the time this object was created.
 *
 * @return creation time of this object
 * 
 * @author John Wickberg
 */
public long getCreatedTime();
/**
 * Gets the time when this object was last modified.
 * 
 * @return last modification time of this object.
 *
 * @author John Wickberg
 */
public long getLastModifiedTime();
/**
 * Resets the last modified time to the current system time.
 * 
 * @author John Wickberg
 */
public void resetLastModifiedTime();
}
