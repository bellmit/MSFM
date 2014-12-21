/**
 * Circularqueue.java
 *
 * Created on March 19, 2002, 11:23 AM
 */

package com.cboe.lwt.collection;




/**
 * Statically sized stack of objects.  
 *
 * The semantics for an push are:
 *      test if full
 *      if not full, push
 *
 * The semantics for a pop are:
 *      test if empty
 *      if not empty, pop
 *
 * USAGE NOTES:
 *  -   This class is <b>IN NO WAY SYNCHRONIZED:</b> all users must provide their own synchronization
 *  -   Fullness or emptiness of the stack should be checked before using the enqueue()/dequeue()
 *      methods.  Failure to do this will result in an Error being thrown.
 *
 * @author  dotyl
 */
public class LwtList 
{
    ////////////////////////////////////////////////////////////////////////////
    // Inner class
    
    
    private static final class Entry
    {
        Object obj;
        Entry next;
        Entry prev;
     
        Entry( Entry  p_prev,
               Object p_obj,
               Entry  p_next )
        {
            obj = p_obj;
            next = p_next;
            prev = p_prev;    
        }
        
        final Entry shallowCopy()
        {
            return new Entry( prev, obj, next );
        }
    };
    
    
    ////////////////////////////////////////////////////////////////////////////
    // Inner class

    
    public static final class Iter
    {
        private Entry   cur;
        private LwtList list;
                
        ///////////////////////////////
        // interface
                
        public final Iter shallowCopy()
        {
            return new Iter( list, cur );
        }
        
        
        public final LwtList getList()
        {
            return list;
        }


        public Object get()
        {
            return cur.obj;
        }
        
        
        public boolean isValid()
        {
            return cur != null;
        }
        
        
        public boolean isFirst()
        {
            return cur.prev == null;
        }
        
        
        public boolean isLast()
        {
            return cur.next == null;
        }
        
        
        public Iter next()
        {
            cur = cur.next; 
            return this;
        }
        
        
        public Object peekNext()
        {
            return ( cur.next != null )
                   ? cur.next.obj
                   : null;
        }
        
        
        public Iter prev()
        {
            cur = cur.prev; 
            return this;
        }
        
        
        public Object peekPrev()
        {
            return ( cur.prev != null )
                   ? cur.prev.obj
                   : null;
        }
       
        
        public Iter first()
        {
            cur = list.first;
            return this;
        }
        
        
        public Iter last()
        {
            cur = list.last;
            return this;
        }
        
        
        public void insertBefore( Object p_toAdd )
        {
            if ( list.isEmpty() )
            {
                list.insertFirst( p_toAdd );
            }
            else
            {
                list.insertBefore( p_toAdd, cur );
            }
        }
        
        
        public void insertAfter( Object p_toAdd )
        {
            if ( list.isEmpty() )
            {
                list.insertLast( p_toAdd );
            }
            else
            {
                list.insertAfter( p_toAdd, cur );
            }
        }
        
        
        /** removes the current item from the underlying list.  
         */
        public void remove()
        {
            list.remove( cur );
        }
        
        ///////////////////////////////
        // implementation
        
        Iter( LwtList p_list, Entry p_cur )
        {
            list = p_list;
            cur = p_cur;
        }
        
    };
    
    
    // Inner Class END
    ////////////////////////////////////////////////////////////////////////////
 
    
    Entry first;
    Entry last;
    int   size;
 
    
    ///////////////////////////////////////////////////////
    // Interface

    
    public LwtList()
    {
        clear();
    }
    
    
    public void clear()
    {
        first = null;
        last  = null;
        size  = 0;
    }
    
    
    public LwtList shallowCopy()
    {
        LwtList result = new LwtList();
        for( LwtList.Iter iter = first();
             iter.isValid();
             iter.next() )
        {
            result.insertLast( iter.get() );
        }
        
        return result;
    }
    
    
    public void transferAllTo( LwtList p_dest )
    {
        if ( p_dest.isEmpty() )
        {
            p_dest.first = first;
            p_dest.last = last;
            p_dest.size = size;
        }
        else
        {
            p_dest.last.next = first;
            p_dest.last      = last;
            p_dest.size += size;
        }
        
        clear();
    }
    
    
    public int size()
    {
        return size;
    }
    
    
    public boolean isEmpty()
    {
        return first == null;
    }
    
    
    public Iter first()
    {
        return new Iter( this, first );
    }
    
    
    public Iter last()
    {
        return new Iter( this, last );
    }
    
    
    public void insertFirst( Object p_insert )
    {
        Entry newEntry = new Entry( null, p_insert, first );
     
        if ( first != null )
        {
            first.prev = newEntry;
        }
        else
        {
            last = newEntry;  // because the list was empty, now only one entry == first and last
        }
        
        first = newEntry;
        
        ++size;
    }
    
    
    public void insertLast( Object p_insert )
    {
        Entry newEntry = new Entry( last, p_insert, null );
     
        if ( last != null )
        {
            last.next = newEntry;
        }
        else
        {
            first = newEntry;  // because the list was empty, now only one entry == first and last
        }
        
        last = newEntry;
        
        ++size;
    }
    
    
    ///////////////////////////////////////////////////////
    // Implementation
    
    
    void insertBefore( Object p_insert, Entry p_before )
    {
        Entry newEntry = new Entry( p_before.prev, p_insert, p_before );
     
        p_before.prev = newEntry;
        
        if ( newEntry.prev != null )
        {
            newEntry.prev.next = newEntry;
        }
        else
        {
            first = newEntry;
        }
        
        ++size;
    }
    
    
    void insertAfter( Object p_insert, Entry p_after )
    {
        Entry newEntry = new Entry( p_after, p_insert, p_after.next );
     
        p_after.next = newEntry;
        
        if ( newEntry.next != null )
        {
            newEntry.next.prev = newEntry;
        }
        else
        {
            last = newEntry;
        }
        
        ++size;
    }
    
    
    void remove( Entry p_rem )
    {
        if ( p_rem.prev == null )
        {
            first = p_rem.next;
        }
        else
        {
            p_rem.prev.next = p_rem.next;
        }
        
        if ( p_rem.next == null )
        {
            last = p_rem.prev;
        }
        else
        {
            p_rem.next.prev = p_rem.prev;
        }
        
        --size;
    }
}
