package com.cboe.lwt.transaction;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * @author dotyl
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class TransactionTest extends TestCase
{
    ////////////////////////////////////////////////////////////////////////////
    // inner class
    
    public static class TransObj extends DefaultTransactable
    {
        private int uid;
        private int num;
        private String name;
       

        public TransObj( int p_uid, int p_num, String p_name )
        {
            uid  = p_uid;
            num  = p_num;
            name = p_name;
        }
        
        
        private static final int integerClassKey = Uid.getClassKey( Integer.class );
        
        public Uid getUid()
        {
            return new Uid( integerClassKey, uid );
        }
       
        
        public final void setNum( int p_num )
        {
            num = p_num;
            dirty();
        }
        
        
        public final int getNum()
        {
            return num;
        }
        
        
        public final void setName( String p_name )
        {
            name = p_name;
            dirty();
        }
        
        
        public final String getName()
        {
            return name;
        }
        
        
        public boolean isEqualTo( Transactable p_other )
        {
            if ( ! ( p_other instanceof TransObj ) )
            {
                return false;
            }
            
            TransObj other = (TransObj)p_other;
            
            if ( uid != other.uid )
            {
                return false;
            }
            if ( num != other.num )
            {
                return false;
            }
            return name.equals( other.name );
        }


        public Transactable newCopy()
        {
            TransObj newObj = new TransObj( uid, num, name );
            
            return newObj;
        }


        public void updateFrom( Transactable p_newState )
        {
            TransObj fromObj = (TransObj)p_newState;
            uid  = fromObj.uid;
            num  = fromObj.num;
            name = fromObj.name;
        }
        /* (non-Javadoc)
         * @see com.cboe.lwt.transaction.Transactable#discard()
         */
        public void release()
        {
            // no op
        }
    }

    // inner class
    ////////////////////////////////////////////////////////////////////////////


    public TransactionTest( String p_testName )
    {
        super( p_testName );
    }
        
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run( suite() );
    }

    
    public static Test suite()
    {
        TestSuite suite = new TestSuite( TransactionTest.class );
        
        return suite;
    }
    
    
    private void assertEqual( String p_msg, TLink p_a, TLink p_b )
    {
        TransObj a = (TransObj)p_a.tGet(); 
        TransObj b = (TransObj)p_b.tGet();
        
        assertEquals( p_msg, a.getNum(), b.getNum() );
    }
    
    
    private void assertNotEqual( String p_msg, TLink p_a, TLink p_b )
    {
        TransObj a = (TransObj)p_a.tGet();
        TransObj b = (TransObj)p_b.tGet();
        
        assertTrue( p_msg, a.getNum() != b.getNum() );
    }
    
    
    public void testFindByUid()
    {
        ///////////////////////////
        {
            CachedTransaction xact1 = CachedTransaction.getInstance( "A", new LockMgr() );
            
            TransObj underlyingA = new TransObj( 1, 100, "a" );

            TLink ref1A = xact1.addExisting( underlyingA );
            TLink ref1B = xact1.findByUid( underlyingA.getUid() );
            
            assertTrue( ref1A != null );
            assertTrue( ref1B != null );
            assertEqual( "RefA != refB", ref1A, ref1B );
        }
        
        ///////////////////////////
        {
            CachedTransaction xact1 = CachedTransaction.getInstance( "B", new LockMgr() );
            
            TransObj underlyingA = new TransObj( 1, 100, "a" );
            TransObj underlyingB = new TransObj( 2, 200, "b" );

            TLink ref1A = xact1.addExisting( underlyingA );
            TLink ref1B = xact1.findByUid( underlyingB.getUid() );
            
            assertTrue( ref1A != null );
            assertTrue( ref1B == null );
        }
        
        ///////////////////////////
        {
            CachedTransaction xact1 = CachedTransaction.getInstance( "C", new LockMgr() );
            
            TransObj underlyingA = new TransObj( 1, 100, "a" );
            TransObj underlyingB = new TransObj( 2, 200, "b" );

            TLink ref1A = xact1.addExisting( underlyingA );
            assertTrue( ref1A != null );
            
            CachedTransaction xact11 = xact1.startNestedTransaction("unknown");
            TLink ref11B = xact11.addExisting( underlyingB );
            assertTrue( ref11B != null );
            
            TLink ref11B2  = xact11.findByUid( underlyingB.getUid() );
            assertTrue( ref11B2 != null );
            assertEqual( "ref11B != ref11B", ref11B2, ref11B );

            TLink ref11A  = xact11.findByUid( underlyingA.getUid() );
            assertTrue( ref11A != null );
        }
        
        ///////////////////////////
        {
            CachedTransaction xact1 = CachedTransaction.getInstance( "Test", new LockMgr() );
            
            TransObj underlyingA = new TransObj( 1, 100, "a" );
            TransObj underlyingB = new TransObj( 2, 200, "b" );

            CachedTransaction xact11 = xact1.startNestedTransaction("unknown");
            TLink ref11B = xact11.addExisting( underlyingB );
            assertTrue( ref11B != null );
            
            TLink ref11B2  = xact11.findByUid( underlyingB.getUid() );
            assertTrue( ref11B2 != null );
            assertEqual( "ref11B2 != ref11B", ref11B2, ref11B );

            TLink ref11A  = xact11.findByUid( underlyingA.getUid() );
            assertTrue( ref11A == null );
        }
    }

    
    public void testAbort()
    {
        LockMgr lockMgr = new LockMgr();
        
        CachedTransaction xact1 = CachedTransaction.getInstance( "Test", lockMgr );
        CachedTransaction xact2 = CachedTransaction.getInstance( "Test", lockMgr );
        
        TransObj underlyingA = new TransObj( 1, 100, "a" );

        TLink ref1A = xact1.addExisting( underlyingA );
        TLink ref1B = xact1.findByUid( underlyingA.getUid() );
        assertEqual( "ref1A != ref1B", ref1A, ref1B );
        
        TLink ref2A = xact2.addExisting( underlyingA );
        TLink ref2B = xact2.findByUid( underlyingA.getUid() );
        assertEqual( "ref2A != ref2B", ref2A, ref2B );
        
        TransObj a1 = (TransObj)ref1A.get();
        TransObj b1 = (TransObj)ref1B.get();
        assertTrue( "1A != 1B", a1.isEqualTo( b1 ) );

        TransObj a2 = (TransObj)ref2A.get();
        TransObj b2 = (TransObj)ref2B.get();
        assertTrue( "a2 != b2", a2.isEqualTo( b2 ) );
        
        a1.setNum( 155 );  // transaction1
        
        assertEqual( "ref1A != ref1B", ref1A, ref1B );
        assertEqual( "ref1A != ref1B", ref1A, ref1B );
        assertNotEqual( "Should be different from other transaction", ref1A, ref2A );
        assertNotEqual( "Should be different from other transaction", ref1B, ref2B );
        
        xact1.abort();
        assertEqual( "ref1A != ref1B", ref1A, ref1B );
        assertTrue( "Should NO LONGER be different from original", ! ref1A.tGet().isDirty() );  // not different from original
        assertEqual( "Should now again be the same as other transaction", ref1A, ref2A ); // different transaction
        
        assertTrue( underlyingA.getNum() == 100 );
    }

    
    public void testCommit()
    {
        LockMgr lockMgr = new LockMgr();
        
        CachedTransaction xact1 = CachedTransaction.getInstance( "Test", lockMgr );
        CachedTransaction xact2 = CachedTransaction.getInstance( "Test", lockMgr );
        
        TransObj underlyingA = new TransObj( 1, 100, "a" );
        
        TLink ref1A = xact1.addExisting( underlyingA );
        TLink ref1B = xact1.findByUid( underlyingA.getUid() );
        assertEqual( "Ref1A != ref1B", ref1A, ref1B );
        TLink ref2A = xact2.addExisting( underlyingA );
        TLink ref2B = xact2.findByUid( underlyingA.getUid() );
        assertEqual( "ref2A != ref2A", ref2A, ref2A );
        
        TransObj a1 = (TransObj)ref1A.get();
        TransObj b1 = (TransObj)ref1B.get();
        assertTrue( "1A != 1B", a1.isEqualTo( b1 ) );

        TransObj a2 = (TransObj)ref2A.get();
        TransObj b2 = (TransObj)ref2B.get();
        assertTrue( "a2 != b2", a2.isEqualTo( b2 ) );
        
        a1.setNum( 155 );  // transaction1
        
        assertEqual( "ref1A != ref1B", ref1A, ref1B );
        assertTrue( "Should be different from original", ref1A.tGet().isDirty() );  // different from original
        assertNotEqual( "Should be different from other transaction", ref1A, ref2A );
        assertNotEqual( "Should be different from other transaction", ref1B, ref2B );
        
        assertEquals( 100, underlyingA.getNum() );
        try
        {
            xact1.commit();
            assertEquals( 155, underlyingA.getNum() );
            
            assertNotEqual( "Should be different from other transaction", ref1A, ref2A );
            assertNotEqual( "Should be different from other transaction", ref1B, ref2B );
            assertEqual( "1A != 1B", ref1A, ref1B ); 
           
            assertTrue( underlyingA.getNum() == 155 );
            xact2.commit();  // nothing should have changed
        }
        catch( CommitVetoedException ex )
        {
            ex.printStackTrace();
            fail( "Commit Vetoed" );
        }
        catch( InterruptedException ex )
        {
            ex.printStackTrace();
            fail( "Interrupted" );
        }

        assertTrue( underlyingA.getNum() == 155 );
        
        assertEqual( "1A != 1B", ref1A, ref1B ); 
    }

    
    public void testCreateNew()
    {
        LockMgr lockMgr = new LockMgr();
        
        CachedTransaction xact1 = CachedTransaction.getInstance( "Test", lockMgr );
        
        TransObj underlying = new TransObj( 1, 100, "a" );
        
        TLink ref1A = xact1.createNew( underlying );
        TLink ref1B = xact1.findByUid( underlying.getUid() );
        assertEqual( "Ref1A != ref1B", ref1A, ref1B );
        
        TransObj a1 = (TransObj)ref1A.get();
        TransObj b1 = (TransObj)ref1B.get();
        assertTrue( "1A != 1B", a1.isEqualTo( b1 ) );

        a1.setNum( 155 );  // transaction1
        
        assertEqual( "ref1A != ref1B", ref1A, ref1B );
        assertTrue( "Should be different from original", ref1A.tGet().isDirty() );  // different from original
        
        assertEquals( 155, underlying.getNum() );  // underlying is modified directly for createNew, since it's new and will just be discarded on abort
        try
        {
            xact1.commit();
        }
        catch( CommitVetoedException ex )
        {
            ex.printStackTrace();
            fail( "Commit Vetoed" );
        }
        catch( InterruptedException ex )
        {
            ex.printStackTrace();
            fail( "Interrupted" );
        }
        assertEquals( 155, underlying.getNum() );
        
        assertEqual( "1A != 1B", ref1A, ref1B ); 
       
        assertTrue( underlying.getNum() == 155 );
    }

    
    public void testNestedAbort()
    {
        LockMgr lockMgr = new LockMgr();
        
        CachedTransaction xact1  = CachedTransaction.getInstance( "Test", lockMgr );
        
        TransObj underlyingA = new TransObj( 1, 100, "a" );
        TransObj underlyingB = new TransObj( 2, 200, "b" );
        
        TLink ref1A = xact1.addExisting( underlyingA );
        TransObj a1 = (TransObj)ref1A.get();
        assertTrue( ! ref1A.tGet().isDirty() );
        a1.setNum( 155 );  
        assertTrue( a1.isDirty() );
        
        
        // 1st nested transaction abort
        CachedTransaction xact11 = xact1.startNestedTransaction("unknown");
        TLink ref11A = xact11.findByUid( a1.getUid() );
        assertTrue( ref11A.tGet().isDirty() );  // dirty because parent is dirty
        TransObj a11 = (TransObj)ref11A.get();
        assertEquals( 155, a1.getNum() );
        assertEquals( 155, a11.getNum() );
        a11.setNum( 166 );

        assertEquals( 166, a1.getNum() );
        assertEquals( 166, a11.getNum() );
        
        
        TLink ref11B = xact11.addExisting( underlyingB );
        assertTrue( ! ref11B.tGet().isDirty() );
        TransObj b11 = (TransObj)ref11B.get();
        assertTrue( b11.getNum() == 200 );
        b11.setNum( 266 );
        
        assertEquals( 166, a1.getNum() );
        assertEquals( 166, a11.getNum() );
        assertEquals( 266, b11.getNum() );
        
        xact11.abort();
        
        assertEquals( 155, a1.getNum() );
        
        TLink ref1B = xact1.addExisting( underlyingB );
        assertTrue( ! ref1B.tGet().isDirty() );
        TransObj b1 = (TransObj)ref1B.get();
        assertTrue( b1.getNum() == 200 );
        
        // top level modification
        
        b1.setNum( 366 );
        
        // 2nd nested transaction abort
        CachedTransaction xact12 = xact1.startNestedTransaction("unknown");
        TLink ref12B = xact12.findByUid( b1.getUid() );
        TransObj b12 = (TransObj)ref12B.get();
        assertTrue( b1.getNum() == 366 );
        assertTrue( b12.getNum() == 366 );
        
        b12.setNum( 466 );
        
        xact12.abort();
        
        assertTrue( a1.getNum() == 155 );
        assertTrue( b1.getNum() == 366 );
        
        xact1.abort();
        
        assertEquals( 100, underlyingA.getNum() );
        assertEquals( 200, underlyingB.getNum() );
    }

    
    public void testNestedAbortNestedAbortedChangeConflict()
    {
        LockMgr lockMgr = new LockMgr();
        
        CachedTransaction xact1  = CachedTransaction.getInstance( "Test", lockMgr );
        CachedTransaction xact2  = CachedTransaction.getInstance( "Test", lockMgr );
        
        TransObj underlyingA = new TransObj( 1, 100, "a" );
        TransObj underlyingB = new TransObj( 2, 200, "b" );
        
        try
        {
            TLink ref1A = xact1.addExisting( underlyingA );
            TransObj a1 = (TransObj)ref1A.get();
            a1.setNum( 155 );  
            
            // 1st nested transaction abort
            CachedTransaction xact11 = xact1.startNestedTransaction("unknown");
            TLink ref11A = xact11.findByUid( a1.getUid() );
            TransObj a11 = (TransObj)ref11A.get();
            assertTrue( a1.getNum() == 155 );
            assertTrue( a11.getNum() == 155 );
            a11.setNum( 166 );
            assertTrue( a1.getNum() == 166 );
            assertTrue( a11.getNum() == 166 );
            
            TLink ref11B = xact11.addExisting( underlyingB );
            TransObj b11 = (TransObj)ref11B.get();
            assertTrue( b11.getNum() == 200 );
            b11.setNum( 266 );
            assertTrue( a1.getNum() == 166 );
            assertTrue( a11.getNum() == 166 );
            assertTrue( b11.getNum() == 266 );
            
            // conflict on nested underlying B
            TLink ref2B = xact2.addExisting( underlyingB );
            TransObj b2 = (TransObj)ref2B.get();
            b2.setNum( 155 );  
            assertTrue( b2.isDirty() );
            
            xact11.abort();
            
            assertTrue( a1.getNum() == 155 );
            
            xact2.commit();
            assertEquals( 100, underlyingA.getNum() );
            assertEquals( 155, underlyingB.getNum() );
            
            xact1.commit();
            
            assertEquals( 155, underlyingA.getNum() );
            assertEquals( 155, underlyingB.getNum() );
        }
        catch( Throwable ex )
        {
            ex.printStackTrace();
            fail();
        }
    }

    
    public void testNestedCommit()
    {
        LockMgr lockMgr = new LockMgr();
        
        CachedTransaction xact1  = CachedTransaction.getInstance( "Test", lockMgr );
        
        TransObj underlyingA = new TransObj( 1, 100, "a" );
        TransObj underlyingB = new TransObj( 2, 200, "b" );
        
        try
        {
            TLink ref1A = xact1.addExisting( underlyingA );
            TransObj a1 = (TransObj)ref1A.get();
            a1.setNum( 155 );  
            assertTrue( a1.getNum() == 155 );
            assertTrue( underlyingA.getNum() == 100 );
            
            
            // 1st nested transaction 
            CachedTransaction xact11 = xact1.startNestedTransaction("unknown");
            TLink ref11A = xact11.findByUid( a1.getUid() );
            assertTrue( ref11A.tGet().isDirty() ); // dirty because parent link is dirty
            TransObj a11 = (TransObj)ref11A.get();
            assertTrue( a1.getNum() == 155 );
            assertTrue( a11.getNum() == 155 );
            a11.setNum( 166 );
            assertTrue( a1.getNum() == 166 );
            assertTrue( a11.getNum() == 166 );
            assertTrue( underlyingA.getNum() == 100 );
            assertTrue( underlyingB.getNum() == 200 );
            
            TLink ref11B = xact11.addExisting( underlyingB );
            assertTrue( ! ref11B.tGet().isDirty() );
            TransObj b11 = (TransObj)ref11B.get();
            assertTrue( b11.getNum() == 200 );
            b11.setNum( 266 );
            assertTrue( a1.getNum() == 166 );
            assertTrue( a11.getNum() == 166 );
            assertTrue( b11.getNum() == 266 );
            
            xact11.commit();
            
            assertTrue( a1.getNum() == 166 );
            TLink ref1B = xact1.findByUid( b11.getUid() );  // should now be there because of nested
            assertTrue( ref1B.tGet().isDirty() );
            TransObj b1 = (TransObj)ref1B.get();
            assertTrue( b1.getNum() == 266 );
            assertEquals( 100, underlyingA.getNum() );
            assertEquals( 200, underlyingB.getNum() );
            
            
            // top level modification
            
            b1.setNum( 366 );
            assertTrue( b1.getNum() == 366 );
            assertTrue( underlyingA.getNum() == 100 );
            assertTrue( underlyingB.getNum() == 200 );
            
            // 2nd nested transaction abort
            CachedTransaction xact12 = xact1.startNestedTransaction("unknown");
            TLink ref12B = xact12.findByUid( b1.getUid() );
            assertTrue( ref12B.tGet().isDirty() ); // dirty because parent is dirty
            TransObj b12 = (TransObj)ref12B.get();
            assertTrue( b1.getNum() == 366 );
            assertTrue( b12.getNum() == 366 );
            
            b12.setNum( 466 );
            xact12.commit();
            
            assertTrue( b1.getNum() == 466 );
            assertEquals( 100, underlyingA.getNum() );
            assertEquals( 200, underlyingB.getNum() );
            
            xact1.commit();
            
            assertEquals( 166, underlyingA.getNum() );
            assertEquals( 466, underlyingB.getNum() );
        }
        catch( Throwable ex )
        {
            ex.printStackTrace();
            fail();
        }
    }

    
    public void testInterlockedOptimisticFailure()
    {
        LockMgr lockMgr = new LockMgr();
        
        CachedTransaction xact1 = CachedTransaction.getInstance( "Test", lockMgr );
        CachedTransaction xact2 = CachedTransaction.getInstance( "Test", lockMgr );
        
        TransObj underlyingA = new TransObj( 1, 100, "a" );
        TransObj underlyingB = new TransObj( 2, 200, "b" );
        
        try
        {
            TLink ref1A = xact1.addExisting( underlyingA );
            TLink ref1B = xact1.addExisting( underlyingB );
            
            TLink ref2A = xact2.addExisting( underlyingA );
            TLink ref2B = xact2.addExisting( underlyingB );
            
            TransObj a1 = (TransObj)ref1A.get();
            ref1B.get();  // just to cache
            ref2A.get();  // just to cache
            TransObj b2 = (TransObj)ref2B.get();
            
            a1.setNum( 155 );  // transaction1
            
            b2.setNum( 266 );  // transaction 2  this will cause opptimistic lock failure on commit
            
            assertNotEqual( "1a == 2a", ref1A, ref2A ); 
            assertNotEqual( "1b == 2b", ref1B, ref2B ); 
            assertEquals( 100, underlyingA.getNum() );
            assertEquals( 200, underlyingB.getNum() );
            
            
            xact1.commit();
            assertEquals( 155, underlyingA.getNum() );
            assertEquals( 200, underlyingB.getNum() );

            TransObj a2 = (TransObj)ref2A.get(); 
            a2.setNum( 355 );  // set to same value as current underlying
            
            xact2.commit();  // this commit now succeeds because we're no longer read isolated

            assertEquals( 355, underlyingA.getNum() );
            assertEquals( 266, underlyingB.getNum() );
        }
        catch( Throwable ex )
        {
            ex.printStackTrace();
            fail();
        }
    }
   
    
/* The testing of uniqueness has now been pushed back to the transactable class
    public void testNonniqueFails()
    {
        LockMgr lockMgr = new LockMgr();
        
        CachedTransaction xact1 = CachedTransaction.getInstance( "Test", lockMgr );
        
        TransObj underlyingA = new TransObj( 1, 100, "a" );
        TransObj underlyingB = new TransObj( 1, 200, "b" );
        
        TLink ref1A = xact1.addToTransaction( underlyingA );
        TransObj a = (TransObj)ref1A.get();
        assertEquals( 100, a.num );

        try
        {
            TLink ref1B = xact1.addToTransaction( underlyingB );
            fail( "duplicate uid should have failed" );
            ref1B.get();  // just here to prevent a warning
        }
        catch( RuntimeException ex )
        {
            // success
        }
    }
*/
}
