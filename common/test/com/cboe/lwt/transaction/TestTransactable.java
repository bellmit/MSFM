package com.cboe.lwt.transaction;


import com.cboe.lwt.pool.ObjectPool;


/**
 * This class is the transactable Quote implementation (hard to believe from the
 * name, eh?)
 */
public class TestTransactable extends DefaultTransactable
{
    ////////////////////////////////////////////////////////////////////////////
    // Static
    ////////////////////////////////////////////////////////////////////////////
    
    private static ObjectPool pool = null;
    private static int        uidClassKey = Uid.getClassKey( TestTransactable.class ); 
            
    /**
     * Establishes an object pool of Message objects with the specified buffer 
     * size, initial, and maximum pool sizes.
     *
     * @param p_maxPoolSize the maximum number of entries the pool can expand to hold
     * @param p_initialPoolSize the initial size of the pool, and the number of 
     * threads to initially populate the pool with
     */
    static ObjectPool establishPool( int p_maxPoolSize,
                                     int p_initialPoolSize )
    {
        if ( pool != null ) 
        {
            throw new RuntimeException( "programming error: Pool already established" );
        }
        
        pool = ObjectPool.getInstance( "Transactable Pool", p_maxPoolSize );

        for ( int i = 0; i < p_initialPoolSize; ++i )
        {
            pool.checkIn( new TestTransactable() );
        }
        
        return pool;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // END Static
    ////////////////////////////////////////////////////////////////////////////
        

    private Uid   uid = null;
    private int   quoteKey;                      // was _quoteKey;

    public int    productKey;                    // was _productKey
    public String sessionName;                   // was _sessionName
    public String userId;                        // was _userId
    public String bidPrice;                      // was _bidSidePrice;
    public int    bidQuantity;                   // was _bidSideQuantity
    public String askPrice;                      // was _askSidePrice
    public int    askQuantity;                   // was _askSideQuantity
    public int    transactionSequenceNumber;     // was _transactionSequenceNumber
    public String userAssignedId;                // was _userAssignedId
    public int    userKey;                       // was _userKey
    public int    classKey;                      // was _classKey
    public long   askBookedTime;                 // was _askBookedTime
    public int    askBookedStatus;               // was _askBookedStatus
    public long   bidBookedTime;                 // was _bidBookedTime
    public int    bidBookedStatus;               // was _bidBookedStatus
    public int    cancelReportQuoteKey;          // was _cancelReportQuoteKey


    ////////////////////////////////////////////////////////////////////////////
    // static
    
    
    static int curSequenceNumber = 0;            
            
    private synchronized static int nextSequenceNumber()
    {
        return ++curSequenceNumber;
    }
                
    
    private TestTransactable()
    {
        transactionSequenceNumber = nextSequenceNumber();
    }
    
    
    static TestTransactable getInstance( int p_key )
    {
        TestTransactable result = null;
        
        if ( pool != null )
        {
            result = (TestTransactable)pool.checkOut();

            if ( result == null )
            {
                result = new TestTransactable();
            }
        }
        else
        {
            // not pooling
            result = new TestTransactable();
        }
        
        result.transactionSequenceNumber = TestTransactable.nextSequenceNumber();

        assert ( result != null ) : "Programming error";
        
        result.setKey( p_key );
        
        return result;
    }
    
    // static
    ////////////////////////////////////////////////////////////////////////////
    

    public void setKey( int p_key )
    {
        quoteKey = p_key;
        uid = new Uid( uidClassKey, p_key );
    }

    public int getKey()
    {
        return uid.getOid(); 
    }


    public Uid getUid()
    {
        return uid;
    }


    public final boolean isEqualTo( Transactable p_other )
    {
        if ( ! ( p_other instanceof TestTransactable ) )
        {
            throw new RuntimeException( "Programming error... updating from a different type of object" );
        }

        TestTransactable other = (TestTransactable)p_other;

        if ( quoteKey                  != other.quoteKey )                  return false;
        if ( productKey                != other.productKey )                return false;
        if ( sessionName               != other.sessionName )               return false;
        if ( userId                    != other.userId )                    return false;
        if ( bidPrice                  != other.bidPrice )                  return false;
        if ( bidQuantity               != other.bidQuantity )               return false;
        if ( askPrice                  != other.askPrice )                  return false;
        if ( askQuantity               != other.askQuantity )               return false;
        if ( transactionSequenceNumber != other.transactionSequenceNumber ) return false;
        if ( userAssignedId            != other.userAssignedId  )           return false;
        if ( userKey                   != other.userKey              )      return false;
        if ( classKey                  != other.classKey             )      return false;
        if ( askBookedTime             != other.askBookedTime        )      return false;
        if ( askBookedStatus           != other.askBookedStatus      )      return false;
        if ( bidBookedTime             != other.bidBookedTime        )      return false;
        if ( bidBookedStatus           != other.bidBookedStatus      )      return false;
        if ( cancelReportQuoteKey      != other.cancelReportQuoteKey )      return false;

        return true;
    }


    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        
        
        sb.append( "\n    quoteKey                    : " ).append( quoteKey );
        sb.append( "\n    productKey                  : " ).append( productKey );
        sb.append( "\n    sessionName                 : " ).append( sessionName );
        sb.append( "\n    userId                      : " ).append( userId );
        sb.append( "\n    bidPrice                    : " ).append( bidPrice );
        sb.append( "\n    bidQuantity                 : " ).append( bidQuantity );
        sb.append( "\n    askPrice                    : " ).append( askPrice );
        sb.append( "\n    askQuantity                 : " ).append( askQuantity );
        sb.append( "\n    transactionSequenceNumber   : " ).append( transactionSequenceNumber );
        sb.append( "\n    userAssignedId              : " ).append( userAssignedId );
        sb.append( "\n    userKey                     : " ).append( userKey );
        sb.append( "\n    classKey                    : " ).append( classKey );
        sb.append( "\n    askBookedTime               : " ).append( askBookedTime );
        sb.append( "\n    askBookedStatus             : " ).append( askBookedStatus );
        sb.append( "\n    bidBookedTime               : " ).append( bidBookedTime );
        sb.append( "\n    bidBookedStatus             : " ).append( bidBookedStatus );
        sb.append( "\n    cancelReportQuoteKey        : " ).append( cancelReportQuoteKey );
        
        return sb.toString();
    }


    public final Transactable newCopy()
    {
        TestTransactable result = TestTransactable.getInstance( quoteKey );

        result.updateFrom( this );
        
        return result;
    }


    public final void updateFrom( Transactable p_newState )
    {
        if ( ! ( p_newState instanceof TestTransactable ) )
        {
            throw new RuntimeException( "Programming error... updating from a different type of object" + p_newState.toString() );
        }

        TestTransactable newState = (TestTransactable)p_newState;

        quoteKey                  = newState.quoteKey;
        productKey                = newState.productKey;
        sessionName               = newState.sessionName;
        userId                    = newState.userId;
        bidPrice                  = newState.bidPrice;
        bidQuantity               = newState.bidQuantity;
        askPrice                  = newState.askPrice;
        askQuantity               = newState.askQuantity;
        transactionSequenceNumber = newState.transactionSequenceNumber;
        userAssignedId            = newState.userAssignedId;
        userKey                   = newState.userKey;
        classKey                  = newState.classKey;
        askBookedTime             = newState.askBookedTime;
        askBookedStatus           = newState.askBookedStatus;
        bidBookedTime             = newState.bidBookedTime;
        bidBookedStatus           = newState.bidBookedStatus;
        cancelReportQuoteKey      = newState.cancelReportQuoteKey;
    }
    

    public void release()
    {
        if ( pool != null )
        {
            pool.checkIn( this );
        }
    }
}