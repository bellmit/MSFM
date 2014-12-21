/*
 * Created on Mar 23, 2004
 *
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */

package com.cboe.lwt.transaction;


import java.util.Iterator;
import java.util.LinkedList;

import com.cboe.lwt.collection.Stack;
import com.cboe.lwt.eventLog.Logger;


/**
 * @author dotyl
 *
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public final class CachedTransaction implements Transaction
{
    private static final int DEFAULT_INTERNAL_VECTOR_SIZE = 32;
    private static final int DEFAULT_NESTED_XACT_SIZE     = 4;

    String            name = "unassigned";
    LockMgr           lockMgr                 = null;
    TLinkVector       xactLinks               = null;
    Stack             nestedXacts             = null;
    CachedTransaction parent                  = null;
    TLinkVector       newForParentTransaction = null;
    
    boolean committing = false;
    boolean aborting   = false;
    

    ////////////////////////////////////////////////////////////////////////////
    // private construction

    private CachedTransaction( String p_name )
    {
        name = p_name;
        xactLinks = TLinkVector.getInstance( DEFAULT_INTERNAL_VECTOR_SIZE );
        assert( xactLinks.isEmpty() ) : name;
        nestedXacts = new Stack( DEFAULT_NESTED_XACT_SIZE );
    }


    // private construction
    ////////////////////////////////////////////////////////////////////////////
    // Transaction Creation
    
    
    public static CachedTransaction getInstance( String p_name, LockMgr p_lockMgr )
    {
        CachedTransaction result = new CachedTransaction( p_name );

        result.init( p_lockMgr,
                     null );
        
        return result;
    }


    public final CachedTransaction startNestedTransaction( String p_name )
    {
        CachedTransaction result = new CachedTransaction( p_name );
        
        result.init( null,
                     this );
        
        nestedXacts.push( result );
        result.xactLinks.addAll( xactLinks );
        if ( result.newForParentTransaction == null )
        {
            result.newForParentTransaction = TLinkVector.getInstance( DEFAULT_INTERNAL_VECTOR_SIZE );
        }
        
        return result;
    }


    // Transaction Creation
    ////////////////////////////////////////////////////////////////////////////


    public final TLink addExisting( Transactable p_underlying )
    {
        if ( p_underlying == null )
        {
            Logger.error( "Attempting to add null object -- Name : " + name );
            throw new NullPointerException( "Attempting to add null object" );
        }

        TLink result = TopLevelTLink.createVersion( p_underlying );
        
        if ( parent != null ) // might be in parent transaction
        {
            newForParentTransaction.addEnd( result );
            result = NestedTLink.createVersion( result );
        }
        
        xactLinks.addEnd( result );

        return result;
    }


    public final TLink createNew( Transactable p_underlying )
    {
        if ( p_underlying == null )
        {
            Logger.error( "Attempting to add null object -- Name : " + name );
            throw new NullPointerException( "Attempting to add null object" );
        }

        TLink result = CreatedTLink.createVersion( p_underlying );
        
        if ( parent != null ) // might be in parent transaction
        {
            newForParentTransaction.addEnd( result );
        }
        
        xactLinks.addEnd( result );

        return result;
    }


    public final TLink findByUid( Uid p_uid )
    {
        return xactLinks.find( p_uid );
    }


    public final CachedTransaction getParentTransaction()
    {
        return parent;
    }


    public final CachedTransaction getTopLevelTransaction()
    {
        CachedTransaction cur = this;
        while ( cur.getParentTransaction() != null )
        {
            cur = cur.getParentTransaction();
        }

        return cur;
    }


    public final void abort()
    {
        if ( nestedXacts.available() > 0 )
        {
            Logger.error( "Attempting to abort parent transaciton with open nested transactions -- name : " + name );
            throw new RuntimeException( "Attempting to abort parent transaciton with open nested transactions" );
        }
        
        // abort this transaction
        TLink link = null;
        TLink[] links = xactLinks.exposeValues();
        int end = xactLinks.available();
        for ( int i = 0; i < end; ++i )
        {
            link = links[ i ];
            link.abort();
        }
        
        xactLinks.clear();

        if ( parent != null )
        {
            CachedTransaction betterBeThis = parent.removeNestedTransaction();
            if ( betterBeThis != this )
            {
                Logger.error( "Error: nested transaction removed out of order -- Name : " + name );
                throw new RuntimeException( "Error: nested transaction removed out of order -- Name : " + name + " Should have been : " + betterBeThis.name );
            }

            if ( newForParentTransaction != null )
            {
                TLink[] parentLinks = newForParentTransaction.exposeValues();
                int parentEnd = newForParentTransaction.available();
                for ( int i = 0; i < parentEnd; ++i )
                {
                    link = parentLinks[ i ];
                    link.abort();
                }
        
                newForParentTransaction.clear();                
            }
        }

        // if here, abort succeeded
        notifyAborted();
    }


    public void commit() 
        throws InterruptedException,
               CommitVetoedException
    {
        notifyCommitting();
        
        int numXactLinks = xactLinks.available();
        if ( numXactLinks <= 0 )
        {
            return;
        }
        
        if ( parent != null )
        {
            commitNested();
        }
        else
        {
            commitTopLevel( numXactLinks );
        }

        // if here, transaction commit succeeded
        notifyCommitComplete();
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    // Subject part of the Observer pattern


    LinkedList observers = new LinkedList();


    public synchronized void addObserver( TransactionObserver p_observer )
    {
        if ( committing ) 
        {
            throw new RuntimeException( "Can't add Observers during commit" );
        }
        if ( aborting ) 
        {
            throw new RuntimeException( "Can't add Observers during abort" );
        }

        observers.add( p_observer ); 
    }


    public synchronized void removeObserver( TransactionObserver p_observer )
    {
        if ( committing || aborting ) 
        {
            throw new RuntimeException( "Can't remove Observers during commit or abort" );
        }
        
        observers.remove( p_observer );
    }


    private void notifyAborted()
    { 
        TransactionObserver cur;
        Iterator observerIter = observers.iterator();
        while ( observerIter.hasNext() )
        {
            cur = (TransactionObserver)observerIter.next();
            cur.observeAborted( this );
        }
    }


    private void notifyCommitting()
        throws CommitVetoedException
    {
        TransactionObserver cur;

        Iterator observerIter = observers.iterator();
        while ( observerIter.hasNext() )
        {
            cur = (TransactionObserver)observerIter.next();
            cur.observeCommitting( this );
        }
    }


    private void notifyCommitComplete()
    {
        TransactionObserver cur;

        Iterator observerIter = observers.iterator();
        while ( observerIter.hasNext() )
        {
            cur = (TransactionObserver)observerIter.next();
            cur.observeCommitComplete( this );
        }
    }

    
    // Subject part of the Observer pattern
    ////////////////////////////////////////////////////////////////////////////
    // private implementation
    
    private void init( LockMgr           p_lockMgr, 
                       CachedTransaction p_parent )
    {
        assert( xactLinks.isEmpty() ) : "last name " + name + ", name " + name + "\n" + xactLinks.toString();
        if ( newForParentTransaction != null )
        {
            assert( newForParentTransaction.isEmpty() );
        }

        lockMgr                 = p_lockMgr;
        parent                  = p_parent; // null ==> top level
        newForParentTransaction = null;
    }


    private CachedTransaction removeNestedTransaction()
    {
        return (CachedTransaction)nestedXacts.pop();
    }

    
    private void commitTopLevel( int numXactLinks ) 
        throws InterruptedException
    {
        TLink link = null;
   
        TLink[] links = xactLinks.exposeValues();
        
        if ( lockMgr != null )  // if there is a lock mgr
        {
            lockMgr.lock( this );
        }
   
        for ( int i = 0; i < numXactLinks; ++i )
        {
            link = links[ i ];
            link.commit();
        }
        
        if ( lockMgr != null )  // if there is a lock mgr
        {
            lockMgr.unlock( this );
        }
        
        xactLinks.clear();
    }


    private void commitNested()
    {
        CachedTransaction betterBeThis = parent.removeNestedTransaction();
        if ( betterBeThis != this )
        {
            Logger.error( "Error: nested transaction removed out of order -- Name : " + name );
            throw new RuntimeException( "Error: nested transaction removed out of order -- Name : " + name + " Should have been : " + betterBeThis.name );
        }

        TLink[] parentLinks = newForParentTransaction.exposeValues();
        int parentEnd = newForParentTransaction.available();
        for ( int i = 0; i < parentEnd; ++i )
        {
            parent.xactLinks.addEnd( parentLinks[ i ] );
        }
        
        newForParentTransaction.clear();
    }
    
}