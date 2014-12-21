/*
 * ByteVectorTest.java JUnit based test
 * 
 * Created on August 29, 2002, 1:58 PM
 */

package com.cboe.lwt.byteUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * @author dotyl
 */
public class ByteVectorTest
        extends TestCase
{

    public ByteVectorTest( java.lang.String testName )
    {
        super( testName );
    }


    public static void main( java.lang.String[] args )
    {
        junit.textui.TestRunner.run( suite() );
    }


    public void testGetDebugString()
    {
        // untested
    }


    public void testGetInstance()
    {
        System.out.println( "testGetInstance" );

        ByteVector vect = ByteVector.getInstance();
        assertTrue( vect != null );
    }


    public void testShallowCopy()
    {
        System.out.println( "testShallowCopy" );

        ByteVector vectA = ByteVector.getInstance( "one".getBytes() );

        ByteVector vectB = vectA.shallowCopy();

        assertTrue( vectA.equals( vectB ) );

        vectB.set( (byte)'x', 0 );

        assertTrue( vectA.equals( vectB ) );
        
        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "xne".getBytes() );
        
        ByteArrayUtils.assertEqual( vectB.toArray(),
                                    "xne".getBytes() );
    }


    public void testDeepCopy()
    {
        System.out.println( "testDeepCopy" );

        ByteVector vectA = ByteVector.getInstance( "one".getBytes() );

        ByteVector vectB = vectA.deepCopy();

        assertTrue( ! vectA.equals( vectB ) );

        vectB.set( (byte)'x', 0 );

        
        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "one".getBytes() );
        
        ByteArrayUtils.assertEqual( vectB.toArray(),
                                    "xne".getBytes() );
    }


    public void testRebase()
    {
        System.out.println( "testRebase" );

        ByteVector vectA = ByteVector.getInstance( "one".getBytes() );

        ByteVector vectB = ByteVector.getInstance();

        assertTrue( ! vectA.equals( vectB ) );

        vectB.rebase( vectA );
        assertTrue( vectA.equals( vectB ) );


        vectB.set( (byte)'x', 0 );

        assertTrue( vectA.equals( vectB ) );
        
        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "xne".getBytes() );
        
        ByteArrayUtils.assertEqual( vectB.toArray(),
                                    "xne".getBytes() );
    }


    public void testLeftTrim()
    {
        System.out.println( "testLeftTrim" );

        ByteVector vectA = ByteVector.getInstance( "one".getBytes() );

        vectA.leftTrim( 0 );

        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "one".getBytes() );

        vectA.leftTrim( 1 );

        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "ne".getBytes() );

        vectA.leftTrim( 1 );

        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "e".getBytes() );

        vectA.leftTrim( 1 );

        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "".getBytes() );

        boolean errorCondition = false;
        try
        {
            vectA.leftTrim( 1 );
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }

        if ( errorCondition )
        {
            assert ( false ) : "expecting assertion";
        }
    }


    public void testLeftAndRightTrim()
    {
        System.out.println( "testLeftAndRightTrim" );

        ByteVector vectA = ByteVector.getInstance( "one".getBytes() );

        vectA.leftTrim( 0 );
        vectA.rightTrim( 0 );

        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "one".getBytes() );

        vectA.leftTrim( 1 );
        vectA.rightTrim( 0 );

        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "ne".getBytes() );

        vectA.leftTrim( 0 );
        vectA.rightTrim( 1 );

        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "n".getBytes() );

        boolean errorCondition = false;
        try
        {
            vectA.rebaseToArrayBounds();
            vectA.leftTrim( 4 );
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }

        if ( errorCondition )
        {
            assert ( false ) : "expecting assertion";
        }

        try
        {
            vectA.rebaseToArrayBounds();
            vectA.leftTrim( 0 );
            vectA.rightTrim( 4 );
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }

        if ( errorCondition )
        {
            assert ( false ) : "expecting assertion";
        }

        try
        {
            vectA = ByteVector.getInstance( "abcde".getBytes(),
                                              1,
                                              2 );
            vectA.leftTrim( 3 );
            vectA.rightTrim( 1 );
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }

        if ( errorCondition )
        {
            assert ( false ) : "expecting assertion";
        }

        try
        {
            vectA = ByteVector.getInstance( "abcde".getBytes(),
                                              1,
                                              3 );
            vectA.leftTrim( -1 );
            vectA.rightTrim( 3 );
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }

        if ( errorCondition )
        {
            assert ( false ) : "expecting assertion";
        }
    }


    public void testRightTrim()
    {
        System.out.println( "testRightTrim" );

        ByteVector vectA = ByteVector.getInstance( "one".getBytes() );

        vectA.rightTrim( 0 );

        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "one".getBytes() );

        vectA.rightTrim( 1 );

        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "on".getBytes() );

        vectA.rightTrim( 1 );

        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "o".getBytes() );

        vectA.rightTrim( 1 );

        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "".getBytes() );

        boolean errorCondition = false;
        try
        {
            vectA.rightTrim( 1 );
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }

        if ( errorCondition )
        {
            assert ( false ) : "expecting assertion";
        }

    }


    public void testRebaseToArrayBounds()
    {
        System.out.println( "testRebaseToArrayBounds" );

        ByteVector vectA = ByteVector.getInstance( "one two three".getBytes() );

        vectA.leftTrim( 4 );

        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "two three".getBytes() );

        vectA.rightTrim( 6 );

        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "two".getBytes() );

        vectA.rebaseToArrayBounds();
        byte[] wholeArray = vectA.toArray();

        ByteArrayUtils.assertEqual( wholeArray,
                                    "one two three".getBytes() );
    }


    public void testToArray()
    {
        System.out.println( "testToArray" );

        ByteVector vectA = ByteVector.getInstance( "12345678901234567890".getBytes() );

        ByteArrayUtils.assertEqual( "12345678901234567890".getBytes(),
                                    vectA.toArray() );

        boolean errorCondition = false;
        try
        {
            vectA.rebase( "".getBytes() );
            errorCondition = true;
        }
        catch( AssertionError ex )
        {
        }

        if ( errorCondition )
        {
            assert ( false ) : "expecting assertion";
        }
    }


    public void testGet()
    {
        System.out.println( "testCopyInto" );

        ByteVector vectA = ByteVector.getInstance( "12345678901234567890".getBytes() );

        // test byte version
        assertEquals( (byte)'1', vectA.get( 0 ) );
        assertEquals( (byte)'2', vectA.get( 1 ) );
        assertEquals( (byte)'0', vectA.get( 19 ) );
        
        boolean errorCondition = false;
        try
        {
            vectA.get( 20 );
            errorCondition = true;
        }
        catch( ArrayIndexOutOfBoundsException ex )
        {
        }

        if ( errorCondition )
        {
            assert ( false ) : "expecting assertion";
        }

        try
        {
            vectA.get( -1 );
            errorCondition = true;
        }
        catch( ArrayIndexOutOfBoundsException ex )
        {
        }

        if ( errorCondition )
        {
            assert ( false ) : "expecting assertion";
        }

        // test vect version
        ByteVector vectB = ByteVector.getInstance( new byte[3] );

        vectA.get( 0,
                   vectB,
                   0,
                   3 );
        assertTrue( ! vectA.equals( vectB ) );

        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    vectB.toArray(),
                                    3 );


        // test byte[] version
        byte[] buff = new byte[20];

        vectA.get( 0,
                   buff,
                   0,
                   3 );

        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    buff,
                                    3 );

        vectA.get( 3,
                   buff,
                   3,
                   3 );
        
        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    buff,
                                    6 );
    }


    public void testSet()
    {
        System.out.println( "testSet" );

        ByteVector vectA = ByteVector.getInstance( "12345678901234567890".getBytes() );

        // test single byte version
        byte v = (byte)'a';
        for ( int i = 0; i < vectA.length(); i += 2 )
        {
            vectA.set( v++, i );
        }

        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "a2b4c6d8e0f2g4h6i8j0".getBytes() );


        // test array copy version
        ByteVector vectB = ByteVector.getInstance( "12345678901234567890".getBytes() );

        vectB.set( "abc".getBytes(),
                   0,
                   0,
                   3 );
 
        vectB.set( "xxxdef".getBytes(),
                   3,
                   3,
                   3 );

        vectB.set( "xxxghi".getBytes(),
                   3,
                   6,
                   3 );
        vectB.set( "jkl".getBytes(),
                   0,
                   9,
                   3 );

        ByteArrayUtils.assertEqual( "abcdefghijkl34567890".getBytes(),
                                    vectB.toArray() );

        vectB.set( "xxx".getBytes(),
                   0,
                   14,
                   3 );

        boolean errorCondition = false;
        try
        {
            vectB.set( "yyy".getBytes(),
                       0,
                       20,
                       1 );
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
            assert ( false ) : "expecting assertion";
        }
    }


    public void testFill()
    {
        System.out.println( "testFill" );

        ByteVector vectA = ByteVector.getInstance( "12345678901234567890".getBytes() );

        vectA.fill( (byte)'x',
                    0,
                    4 );

        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "xxxx5678901234567890".getBytes() );

        vectA.fill( (byte)'y', 
                    6,
                    vectA.length() - 6 );

        ByteArrayUtils.assertEqual( vectA.toArray(),
                                    "xxxx56yyyyyyyyyyyyyy".getBytes() );
    }
    
    
    public void testOffsetOf()
    {
        System.out.println("testOffsetOf");
        
        ByteVector vect = ByteVector.getInstance( "onetwothreex".getBytes() );
        
        assertEquals( 0,  vect.offsetOf( "one".getBytes() ) );
        assertEquals( 3,  vect.offsetOf( "two".getBytes() ) );
        assertEquals( 11, vect.offsetOf( "x".getBytes() ) );
        assertEquals( 10, vect.offsetOf( "ex".getBytes() ) );
        assertEquals( -1, vect.offsetOf( "efx".getBytes() ) );
        
        assertEquals( -1, vect.offsetOf( "one".getBytes(), 1,  10 ) );
        assertEquals( 0,  vect.offsetOf( "one".getBytes(), 0,  3 ) );
        assertEquals( 3,  vect.offsetOf( "two".getBytes(), 2,  5 ) );
        assertEquals( 11, vect.offsetOf( "x".getBytes(),   10, 2 ) );
        assertEquals( 10, vect.offsetOf( "ex".getBytes(),  10, 2 ) );
        assertEquals( -1, vect.offsetOf( "efx".getBytes(), 0,  11 ) );
        
        assertEquals( 0,  vect.offsetOf( (byte)'o', 0,  1 ) );
        assertEquals( -1, vect.offsetOf( (byte)'n', 0,  1 ) );
        assertEquals( 1,  vect.offsetOf( (byte)'n', 1,  1 ) );
        assertEquals( -1, vect.offsetOf( (byte)'x', 10, 1 ) );
        assertEquals( 11, vect.offsetOf( (byte)'x', 10, 2 ) );
        
        assertEquals( -1, vect.offsetOf( (byte)'o', 0 ) );
        assertEquals( -1, vect.offsetOf( (byte)'n', 1 ) );
        assertEquals( -1, vect.offsetOf( (byte)'e', 2 ) );
        
        assertEquals( -1, vect.offsetOf( (byte)'z' ) );
        assertEquals( 0,  vect.offsetOf( (byte)'o' ) );
        assertEquals( 1,  vect.offsetOf( (byte)'n' ) );
        assertEquals( 2,  vect.offsetOf( (byte)'e' ) );
        assertEquals( 3,  vect.offsetOf( (byte)'t' ) );
        assertEquals( 11, vect.offsetOf( (byte)'x' ) );
        
        vect.leftTrim( 3 );
        
        assertEquals( -1, vect.offsetOf( (byte)'z' ) );
        assertEquals( 0,  vect.offsetOf( (byte)'t' ) );
        assertEquals( 1,  vect.offsetOf( (byte)'w' ) );
        assertEquals( 2,  vect.offsetOf( (byte)'o' ) );
        assertEquals( 8,  vect.offsetOf( (byte)'x' ) );
        
        vect.leftTrim( 8 );
        
        assertEquals( -1, vect.offsetOf( (byte)'z' ) );
        assertEquals( -1, vect.offsetOf( (byte)'o' ) );
        assertEquals( -1, vect.offsetOf( (byte)'n' ) );
        assertEquals( -1, vect.offsetOf( (byte)'t' ) );
        assertEquals( -1, vect.offsetOf( (byte)'e' ) );
        assertEquals( 0,  vect.offsetOf( (byte)'x' ) );
        
        vect.leftTrim( 1 );
        
        assertEquals( -1, vect.offsetOf( (byte)'z' ) );
        assertEquals( -1, vect.offsetOf( (byte)'o' ) );
        assertEquals( -1, vect.offsetOf( (byte)'n' ) );
        assertEquals( -1, vect.offsetOf( (byte)'t' ) );
        assertEquals( -1, vect.offsetOf( (byte)'e' ) );
        assertEquals( -1, vect.offsetOf( (byte)'x' ) );
    }

    
    public static Test suite()
    {
        TestSuite suite = new TestSuite( ByteVectorTest.class );

        return suite;
    }

}