/*
 * ByteIteratorTest.java
 * JUnit based test
 *
 * Created on August 29, 2002, 1:58 PM
 */

package com.cboe.lwt.byteUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author dotyl
 */
public class ByteIteratorTest extends TestCase
{
    
    public ByteIteratorTest(java.lang.String testName)
    {
        super(testName);
    }
    
    public static void main(java.lang.String[] args)
    {
        junit.textui.TestRunner.run(suite());
    }
    
    public void testGetDebugString()
    {
        // untested
    }
    
    public void testGetInstance()
    {
        System.out.println("testGetInstance");
        
        ByteIterator iter = ByteIterator.getInstance();
        assertTrue( iter != null );
    }
    
    public void testMakeClone()
    {
        System.out.println("testMakeClone");
        
        ByteIterator iterA = ByteIterator.getInstance( "one".getBytes() );
       
        ByteIterator iterB = iterA.shallowCopy();
        
        assertTrue( iterA.equals( iterB ) );
        
        iterB.first().set( (byte)'x' );
        
        assertTrue( iterA.equals( iterB ) );
    }
    
    public void testRebase()
    {
        System.out.println("testRebase");
        
        ByteIterator iterA = ByteIterator.getInstance( "one".getBytes() );
       
        ByteIterator iterB = ByteIterator.getInstance();
        
        assertTrue( ! iterA.equals( iterB ) );
        
        iterB.rebase( iterA );
        assertTrue( iterA.equals( iterB ) );
        
        iterB.first().set( (byte)'x' );
        
        assertTrue( iterA.equals( iterB ) );
    }
    
    
    public void testLeftTrim()
    {
        System.out.println("testLeftTrim");
        
        ByteIterator iterA = ByteIterator.getInstance( "one".getBytes() );

        iterA.first().leftTrim();
        
        ByteArrayUtils.assertEqual( iterA.toArray(), "one".getBytes() );
        
        iterA.next().leftTrim();
        
        ByteArrayUtils.assertEqual( iterA.toArray(), "ne".getBytes() );
        
        iterA.next().leftTrim();
        
        ByteArrayUtils.assertEqual( iterA.toArray(), "e".getBytes() );
        
        iterA.next();
        
        boolean errorCondition = false;
        try
        {
            iterA.leftTrim();
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }
        
        if ( errorCondition )
        {
            assert( false ) : "expecting assertion";
        }
        
        iterA.prev(2);
        
        try
        {
            iterA.leftTrim();
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }
        
        if ( errorCondition )
        {
            assert( false ) : "expecting assertion";
        }
    }
    
    
    public void testLeftTrimToLength()
    {
        System.out.println("testLeftTrimToLength");
        
        ByteIterator iterA = ByteIterator.getInstance( "one".getBytes() );

        iterA.first().leftTrimToLength( 3 );
        
        ByteArrayUtils.assertEqual( iterA.toArray(), "one".getBytes() );
        
        iterA.next().leftTrimToLength( 2 );
        
        ByteArrayUtils.assertEqual( iterA.toArray(), "ne".getBytes() );
        
        iterA.next().leftTrimToLength( 1 );
        
        ByteArrayUtils.assertEqual( iterA.toArray(), "e".getBytes() );
        
        iterA.next();
        
        boolean errorCondition = false;
        try
        {
            iterA.rebaseToArrayBounds();
            iterA.first();
            iterA.leftTrimToLength( 4 );
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }
        
        if ( errorCondition )
        {
            assert( false ) : "expecting assertion";
        }
        
        try
        {
            iterA.rebaseToArrayBounds();
            iterA.next();
            iterA.leftTrimToLength( 3 );
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }
        
        if ( errorCondition )
        {
            assert( false ) : "expecting assertion";
        }
        
        try
        {
            iterA = ByteIterator.getInstance( "abcde".getBytes(), 1, 2, 1 );
            iterA.next( 3 );
            iterA.leftTrimToLength( 1 );
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }
        
        if ( errorCondition )
        {
            assert( false ) : "expecting assertion";
        }
        
        try
        {
            iterA = ByteIterator.getInstance( "abcde".getBytes(), 1, 3, 1 );
            iterA.prev();
            iterA.leftTrimToLength( 3 );
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }
        
        if ( errorCondition )
        {
            assert( false ) : "expecting assertion";
        }
    }
 
    
    public void testRightTrim()
    {
        System.out.println("testRightTrim");
        
        ByteIterator iterA = ByteIterator.getInstance( "one".getBytes() );
        
        iterA.last().rightTrim();
        
        ByteArrayUtils.assertEqual( iterA.toArray(), "one".getBytes() );
        
        iterA.prev().rightTrim();
        
        ByteArrayUtils.assertEqual( iterA.toArray(), "on".getBytes() );
        
        iterA.prev().rightTrim();
        
        ByteArrayUtils.assertEqual( iterA.toArray(), "o".getBytes() );
        
        iterA.prev();
        
        boolean errorCondition = false;
        try
        {
            iterA.rightTrim();
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }
        
        if ( errorCondition )
        {
            assert( false ) : "expecting assertion";
        }
        
        iterA.next(2);
        
        try
        {
            iterA.rightTrim();
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }
        
        if ( errorCondition )
        {
            assert( false ) : "expecting assertion";
        }
    }
    
    
    public void testRebaseToArrayBounds()
    {
        System.out.println("testRebaseToArrayBounds");
        
        ByteIterator iterA = ByteIterator.getInstance( "one two three".getBytes() );

        iterA.setIndex( 4 ).leftTrim();
        
        ByteArrayUtils.assertEqual( iterA.toArray(), "two three".getBytes() );

        iterA.setIndex( 2 ).rightTrim();
        
        ByteArrayUtils.assertEqual( iterA.toArray(), "two".getBytes() );
        
        iterA.rebaseToArrayBounds();
        byte[] wholeArray = iterA.toArray();
        
        ByteArrayUtils.assertEqual( wholeArray, "one two three".getBytes() );
    }
    
    
    public void testGetDistanceFromIter()
    {
        System.out.println("testGetDistanceFromIter");
        
        ByteIterator iterA = ByteIterator.getInstance( "12345678901234567890".getBytes() );
        ByteIterator iterB = iterA.shallowCopy();
        
        assertTrue( iterA.getDistanceFromIter( iterB ) == 0 );
        
        iterB.next( 2 );
        assertTrue( iterA.getDistanceFromIter( iterB ) == -2 );
        
        assertTrue( iterB.getDistanceFromIter( iterA ) == 2 );
    }
    
    
    public void testToArray()
    {
        System.out.println("testToArray");

        ByteIterator iterA = ByteIterator.getInstance( "12345678901234567890".getBytes() );
        
        ByteArrayUtils.assertEqual( "12345678901234567890".getBytes(), iterA.toArray() );
        
        boolean errorCondition = false;
        try
        {
            iterA.rebase( "".getBytes() );
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }
        
        if ( errorCondition )
        {
            assert( false ) : "expecting assertion";
        }
    }
    
    public void testRead()
    {
        System.out.println("testRead");
        
        ByteIterator iterA = ByteIterator.getInstance( "12345678901234567890".getBytes() );
        
        // test iter version
        ByteIterator iterB = ByteIterator.getInstance( new byte[3] );

        iterA.read( iterB, 3 );
        assertTrue( ! iterA.equals( iterB ) );
        
        ByteArrayUtils.assertEqual( iterA.toArray(), iterB.toArray(), 3 );
        
        
        // test byte[] version
        byte[] buff = new byte[20];
        
        iterA.first().read( buff, 0, 3 );
       
        ByteArrayUtils.assertEqual( iterA.toArray(), buff, 3 );
        
        iterA.read( buff, 3, 3 );
        ByteArrayUtils.assertEqual( iterA.toArray(), buff, 6 );
    }
    
    
    public void testSet()
    {
        System.out.println("testSet");
        
        ByteIterator iterA = ByteIterator.getInstance( "12345678901234567890".getBytes() );
        
        // test single byte version
        byte v = (byte)'a';
        for ( iterA.first(); iterA.isValid(); iterA.next( 2 ) )
        {
            iterA.set( v++ );
        }
        
        ByteArrayUtils.assertEqual( iterA.toArray(), "a2b4c6d8e0f2g4h6i8j0".getBytes() );
        
        
        // test array copy version
        ByteIterator iterB = ByteIterator.getInstance( "12345678901234567890".getBytes() );
        
        iterB.write( "abc".getBytes(), 0, 3 );
        
        iterB.next().write( "xxxdef".getBytes(), 3, 3 );
 
        iterB.next( 3 ).write( "xxxghi".getBytes(), 3, 3 );
        iterB.write( "jkl".getBytes(), 0, 3 );
        
        ByteArrayUtils.assertEqual( "abc4def890ghijkl7890".getBytes(), iterB.toArray() );

        boolean errorCondition = false;
        try
        {
            iterB.next( 2 ).write( "xxx".getBytes(), 0, 3 );
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }
        catch( ArrayIndexOutOfBoundsException ex )
        {
            // no asserts lead us here
        }
        
        if ( errorCondition )
        {
            assert( false ) : "expecting assertion";
        }

        try
        {
            iterB.end().write( "yyy".getBytes(), 0, 1 );
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }
        catch( ArrayIndexOutOfBoundsException ex )
        {
            // no asserts lead us here
        }
        
        if ( errorCondition )
        {
            assert( false ) : "expecting assertion";
        }
    }
    
    
    public void testFill()
    {
        System.out.println("testFill");

        ByteIterator iterA = ByteIterator.getInstance( "12345678901234567890".getBytes() );
        
        iterA.fill( (byte)'x', 4 );
        
        ByteArrayUtils.assertEqual( iterA.toArray(), "xxxx5678901234567890".getBytes() );
     
        iterA.next( 2 ).fill( (byte)'y' );
        
        ByteArrayUtils.assertEqual( iterA.toArray(), "xxxx56yyyyyyyyyyyyyy".getBytes() );
    }
    
    
    public void testRemainingBytes()
    {
        System.out.println("testRemainingBytes");

        ByteIterator iterA = ByteIterator.getInstance( "12345678901234567890".getBytes() );

        assertTrue( iterA.remaining() == 20 );
        
        assertTrue( iterA.next().remaining() == 19 );
        
        assertTrue( iterA.last().remaining() == 1);
        
        assertTrue( iterA.end().remaining() == 0 );
        
        assertTrue( iterA.next( 2 ).remaining() == -2 );
    }
    
    
    public void testIsValid()
    {
        System.out.println("testIsValid");
        
        ByteIterator iterA = ByteIterator.getInstance( "12345678901234567890".getBytes() );
        
        int i = 0;
        for ( iterA.first(); iterA.isValid(); iterA.next() )
        {
            ++i;
        }
        
        assertEquals( 20, i );
        
        assertEquals( false, iterA.next().isValid() );
    }
    
    
    public void testGet()
    {
        System.out.println("testGet");

        byte[] buff = "1234567890".getBytes();
        ByteIterator iterA = ByteIterator.getInstance( buff );
        
        int i = 0;
        for ( iterA.first(); iterA.isValid(); iterA.next() )
        {
            assertEquals( buff[i++], iterA.get() );
        }

        try
        {
            iterA.end().get();
            assert( false ) : "expecting assertion";
        }
        catch( ArrayIndexOutOfBoundsException ex )
        {
        }
    }
    
    
    public void testFirst()
    {
        System.out.println("testFirst");

        byte[] buff = "1234567890".getBytes();
        ByteIterator iterA = ByteIterator.getInstance( buff );
        
        assertEquals( buff[4], iterA.setIndex( 4 ).get() );
        
        assertEquals( buff[0], iterA.first().get() );
        
        assertEquals( buff[buff.length - 1], iterA.last().get() );
        
        assertEquals( false, iterA.end().isValid() );
        
        assertEquals( buff[0], iterA.first().get() );
    }
    
    
    public void testLast()
    {
        // tested in testFirst()
    }
    
    
    public void testEnd()
    {
        // tested in testFirst()
    }
    
    
    public void testMoveToLogicalIndex()
    {
        // tested in other tests
    }
    
    
    public void testNext()
    {
        // tested in other tests
    }
    
    
    public void testPrev()
    {
        // tested in other tests
    }
    
    
    public void testParseExpecting()
    {
        byte[] arr = "abcdefghijklmnopqrstuv123456789".getBytes();
        ByteIterator iter = ByteIterator.getInstance( "abcdefghijklmnopqrstuv123456789".getBytes() );
        
        // not equal, current position does not move
        assertTrue( ! iter.nextTokenIs( arr, 1, 3 ) );
        assertEquals( (byte)'a', iter.get() );
        
        // equal, current position moves
        assertTrue( iter.nextTokenIs( arr, 0, 3 ) );
        assertEquals( (byte)'d', iter.get() );
        
        assertTrue( iter.nextTokenIs( arr, 3, 1 ) );
        assertEquals( (byte)'e', iter.get() );
        
        assertTrue( iter.nextTokenIs( arr, 4, 1 ) );
        assertEquals( (byte)'f', iter.get() );
        
        assertTrue( iter.nextTokenIs( arr, 5, 8 ) );
        assertEquals( (byte)'n', iter.get() );
        
        // not equal, current position does not move
        assertTrue( ! iter.nextTokenIs( arr, 1, 3 ) );
        assertEquals( (byte)'n', iter.get() );
    }
    
    
    public void testGetLogicalIndex()
    {
        System.out.println("testGetLogicalIndex");

        byte[] buff = "1234567890".getBytes();
        ByteIterator iterA = ByteIterator.getInstance( buff );
        
        assertEquals( buff[4], iterA.setIndex( 4 ).get() );
        
        ByteIterator iterB = ByteIterator.getInstance( buff,
                                                       4, 
                                                       5, 
                                                       6 );
        
        assertEquals( buff[6], iterB.get() );
        assertEquals( 2, iterB.getIndex() );
        
        assertEquals( buff[8], iterB.next( 2 ).get() );
        assertEquals( 4, iterB.getIndex() );
        
        assertEquals( 6, iterB.next( 2 ).getIndex() );
        
        try
        {
            iterB.get();
            assert( false ) : "expecting assertion";
        }
        catch( ArrayIndexOutOfBoundsException ex )
        {
        }
    }
    
    
    public void testAsciiToInt()
    {
        System.out.println("testAsciiToInt");
     
        byte[] buff = "1234567890a".getBytes();
        ByteIterator iterA = ByteIterator.getInstance( buff );
        
        assertEquals( 1, iterA.readAsciiInt( 1 ) );
        assertEquals( 23, iterA.readAsciiInt( 2 ) );
        assertEquals( 4567890, iterA.readAsciiInt( 7 ) );
        
        assertEquals( 4, iterA.setIndex(3).readAsciiInt( 1 ) );
        assertEquals( 56, iterA.readAsciiInt( 2 ) );
        assertEquals( 7890, iterA.readAsciiInt( 4 ) );
        
        boolean errorCondition = false;
        try
        {
            iterA.readAsciiInt( 7 );
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }
        catch( ArrayIndexOutOfBoundsException ex )
        {
            // no asserts lead us here
        }
        
        if ( errorCondition )
        {
            assert( false ) : "expecting assertion";
        }
    }
    
    
    public void testOffsetOf()
    {
        System.out.println("testOffsetOf");
        
        ByteIterator iterA = ByteIterator.getInstance( "onetwothreex".getBytes() );
        
        assertEquals( -1, iterA.offsetOf( (byte)'o', 0 ) );
        assertEquals( -1, iterA.offsetOf( (byte)'n', 1 ) );
        assertEquals( -1, iterA.offsetOf( (byte)'e', 2 ) );
        
        assertEquals( -1, iterA.offsetOf( (byte)'z' ) );
        assertEquals( 0,  iterA.offsetOf( (byte)'o' ) );
        assertEquals( 1,  iterA.offsetOf( (byte)'n' ) );
        assertEquals( 2,  iterA.offsetOf( (byte)'e' ) );
        assertEquals( 3,  iterA.offsetOf( (byte)'t' ) );
        assertEquals( 11, iterA.offsetOf( (byte)'x' ) );
        
        iterA.next( 3 );
        
        assertEquals( -1, iterA.offsetOf( (byte)'z' ) );
        assertEquals( 0,  iterA.offsetOf( (byte)'t' ) );
        assertEquals( 1,  iterA.offsetOf( (byte)'w' ) );
        assertEquals( 2,  iterA.offsetOf( (byte)'o' ) );
        assertEquals( 8,  iterA.offsetOf( (byte)'x' ) );
        
        iterA.end();
        
        assertEquals( -1, iterA.offsetOf( (byte)'z' ) );
        assertEquals( -1, iterA.offsetOf( (byte)'o' ) );
        assertEquals( -1, iterA.offsetOf( (byte)'n' ) );
        assertEquals( -1, iterA.offsetOf( (byte)'t' ) );
        assertEquals( -1, iterA.offsetOf( (byte)'e' ) );
        assertEquals( -1, iterA.offsetOf( (byte)'x' ) );
        
        iterA.prev();
        
        assertEquals( -1, iterA.offsetOf( (byte)'z' ) );
        assertEquals( -1, iterA.offsetOf( (byte)'o' ) );
        assertEquals( -1, iterA.offsetOf( (byte)'n' ) );
        assertEquals( -1, iterA.offsetOf( (byte)'t' ) );
        assertEquals( -1, iterA.offsetOf( (byte)'e' ) );
        assertEquals( 0,  iterA.offsetOf( (byte)'x' ) );
    }

    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(ByteIteratorTest.class);
        
        return suite;
    }
    
}
