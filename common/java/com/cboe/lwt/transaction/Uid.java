/*
 * Created on Apr 6, 2004
 *
 */
package com.cboe.lwt.transaction;

/**
 * @author dotyl
 *
 */
public final class Uid
{
    int objectId;
    int classId;


    public static final int getClassKey( Class p_class )
    {
        return p_class.hashCode();
    }
    
    
    public Uid( int p_classKey, int p_oid )
    {
        objectId = p_oid;
        classId  = p_classKey;
    }
    
    
    public final int getClassId()
    {
        return classId;
    }
    
    
    public final int getOid()
    {
        return objectId;
    }
    
    
    public final boolean isEqualTo( Uid p_other )
    {
        if ( classId != p_other.classId )
        {
            return false;
        }
        
        return objectId == p_other.objectId;                
    }
    
    
    public final boolean equals( Object p_other )
    {
        if ( ! ( p_other instanceof Uid ) )
        {
            return false;
        }
        
        return isEqualTo( (Uid)p_other );
    }
    
}
