
package com.cboe.instrumentationService.commPath;

/**
 * This interface defines a listener for notifications of new CommPath objects
 * added to the master collection.
 *
 * @author Kevin Yaussy
 */
public interface CommPathListener extends java.util.EventListener {

/**
 * This method gets called when a new CommPath is added to the master
 * collection.
 * @param c The new CommPath added.
 */
public void commPathAdded( CommPath c );
}
