/*
 * Created on Mar 23, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.cboe.lwt.transaction;


/**
 * @author dotyl
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface Transactable extends Identifiable
{
    Transactable newCopy();
    void         updateFrom( Transactable p_newState );
    
    void         release();
    
    void         dirty();
    void         clean();
    boolean      isDirty();
}
