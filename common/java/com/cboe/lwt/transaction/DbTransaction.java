/*
 * Created on Mar 23, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */

package com.cboe.lwt.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;



/**
 * @author dotyl
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public final class DbTransaction implements Transaction, TransactionObserver
{
    static final boolean metricsEnabled = Boolean.getBoolean( "xact.transactionMetrics" );

    HashMap xactLinks;
    Connection con;


    ////////////////////////////////////////////////////////////////////////////
    // private construction

    private DbTransaction( String  p_name, Connection p_con )
    {
        xactLinks = new HashMap();
        con = p_con;
    }


    // private construction
    ////////////////////////////////////////////////////////////////////////////
    // Transaction Creation

    public static DbTransaction getInstance( String p_name, Connection p_con )
    {
        return new DbTransaction( p_name, p_con );
    }


    // Transaction Creation
    ////////////////////////////////////////////////////////////////////////////
 
 
    public final Connection getConnection()
    {
        return con;
    }
    
    
    public final DbLink insert( Persistable p_persistent ) 
        throws TransactException
    {
        if ( p_persistent == null )
        {
            throw new NullPointerException( "Attempting to add null object" );
        }

        Object uid = p_persistent.getUid();

        DbLink result = (DbLink)xactLinks.get( uid );

        if ( result == null ) // legal insert
        {
            result = DbLink.insertAsNew( p_persistent );
            xactLinks.put( uid,
                           result );
        }
        else
        {
            // inserting duplicate
            throw new TransactException( "Inserting duplicate key" );
        }

        return result;
    }


    public final DbLink addToTransaction( Persistable p_persistent ) 
    {
        if ( p_persistent == null )
        {
            throw new NullPointerException( "Attempting to add null object" );
        }

        Object uid = p_persistent.getUid();

        DbLink result = (DbLink)xactLinks.get( uid );

        if ( result == null ) // add new
        {
            result = DbLink.newReference( p_persistent );
            xactLinks.put( uid,
                           result );
        }
        else
        {
            // refreshing old
            result.refreshFromDb( p_persistent );
        }

        return result;
    }


    public final DbLink findByUid( Uid p_uid )
    {
        return (DbLink)xactLinks.get( p_uid );
    }


    public final void notifyUidChanged( Uid p_oldUid, Uid p_newUid )
        throws TransactException
    {
        DbLink linkToReindex = findByUid( p_oldUid );

        if ( linkToReindex == null )
        {
            throw new TransactException( "Link not found" );
        }

        xactLinks.remove( p_oldUid );
        xactLinks.put( p_newUid,
                       linkToReindex );
    }


    // Adding objects to the trasaction
    ////////////////////////////////////////////////////////////////////////////
    // Ending Transactions

    public final void abort()
    {
        // abort this transaction
        DbLink link = null;

        Iterator xactLinksIter = xactLinks.values()
                .iterator();
        while ( xactLinksIter.hasNext() )
        {
            link = (DbLink)xactLinksIter.next();
            link.abort();
        }

        // if here, abort succeeded
        subjectAborted();
    }


    public final void commit()
        throws CommitVetoedException
    {
        DbLink cur = null;

        try
        {
            Iterator xactLinksIter = xactLinks.values().iterator();
            while ( xactLinksIter.hasNext() )
            {
                cur = (DbLink)xactLinksIter.next();
                cur.commitPhase1( con );
            }

            // if here, then the transaction is successfully committed, but
            // still able to roll back
            subjectCommitting(); // NOTE: notifyCommitting can veto the commit by
                                 // throwing a CommitVetoedException
            
            con.commit();
        }
        catch( TransactException ex )
        {
            abort();
            throw new CommitVetoedException( "Transaction commit Aborted!  (optimistic locking failure)", ex );
        }
        catch( SQLException ex )
        {
            abort();
            throw new CommitVetoedException( "Transaction commit Aborted!  (underlying database commit)", ex );
        }
        finally
        {
            xactLinks.clear();
        }

        // if here, transaction commit succeeded
        subjectCommitComplete();
    }


    // Ending Transactions
    ////////////////////////////////////////////////////////////////////////////
    // Subject part of the Observer pattern

    LinkedList observers = new LinkedList();


    public synchronized void addObserver( TransactionObserver p_observer )
    {
        observers.add( p_observer );
    }


    public synchronized void removeObserver( TransactionObserver p_observer )
    {
        observers.remove( p_observer );
    }


    ////////////////////////////////////////////////////////////////////////////
    // Subject part of the Subject-Observer Pattern
 
 
    private void subjectAborted()
    {
        TransactionObserver cur;

        Iterator observerIter = observers.iterator();
        while ( observerIter.hasNext() )
        {
            cur = (TransactionObserver)observerIter.next();
            cur.observeAborted( this );
        }
    }


    private void subjectCommitting()
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


    private void subjectCommitComplete()
    {
        TransactionObserver cur;

        Iterator observerIter = observers.iterator();
        while ( observerIter.hasNext() )
        {
            cur = (TransactionObserver)observerIter.next();
            cur.observeCommitComplete( this );
        }
    }


    // Subject part of the Subject-Observer Pattern
    ////////////////////////////////////////////////////////////////////////////
    // Observer part of the Subject-Observer Pattern
    

    public void observeAborted( Transaction p_transaction )
    {
        abort();
    }
 
 
    public void observeCommitting( Transaction p_transaction )
        throws CommitVetoedException
    {
        commit();  // will veto if the commit fails
    }

    public void observeCommitComplete( Transaction p_transaction )
    {
        // no op
    }


    // Observer part of the Subject-Observer Pattern
    ////////////////////////////////////////////////////////////////////////////

}