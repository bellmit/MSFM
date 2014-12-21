package com.cboe.lwt.byteUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.cboe.lwt.eventLog.Logger;
import com.cboe.lwt.testUtils.SystemUtils;

/**
 * @author dotyl
  */
public class ByteArrayUtilsTest
    extends TestCase
{
    public ByteArrayUtilsTest( String p_name )
    {
        super( p_name );
    }
    
    
    public static void main( String[] args )
    {
        junit.textui.TestRunner.run( ByteArrayUtilsTest.class );
    }
    
    
    public static Test suite()
    {
        return new TestSuite( ByteArrayUtilsTest.class );
    }

    
    /*
     * Class under test for int intToAscii(byte[], int, int, int)
     */
    public final void testIntToAscii()
    {
        byte[] dest = new byte[4];
        ByteArrayUtils.intToAscii( dest, 0, 4, 1234 );
        ByteArrayUtils.assertEqual( dest, "1234".getBytes() );
        try
        {
            ByteArrayUtils.intToAscii( dest, 0, 2, 1244 );
        }
        catch ( RuntimeException ex )
        {
            ByteArrayUtils.assertEqual( dest, "4434".getBytes() );
            return;
        }
        
        fail( "Expecting runtime exception for insufficient conversion length" );
    }


    /*
     * Class under test for int intToAscii(ByteIterator, int, int)
     */
    public final void testIntToAsciiIter()
    {
        ByteIterator dest = ByteIterator.getInstance( 4 );
        ByteArrayUtils.intToAscii( dest.first(), 4, 1234 );
        ByteArrayUtils.assertEqual( dest.physicalStorage, "1234".getBytes() );
        try
        {
            ByteArrayUtils.intToAscii( dest.first(), 2, 1244 );
        }
        catch ( RuntimeException ex )
        {
            ByteArrayUtils.assertEqual( dest.physicalStorage, "4434".getBytes() );
            return;
        }
        
        fail( "Expecting runtime exception for insufficient conversion length" );
    }


    /*
     * Class under test for int asciiToInt(ByteIterator, int)
     */
    public final void testAsciiToIntIter()
    {
        ByteIterator src = ByteIterator.getInstance( "12345".getBytes() );
        int result = ByteArrayUtils.asciiToInt( src.first(), 4 );
        assertEquals( 1234, result );
        src.first().fill( (byte)'0' );
        result = ByteArrayUtils.asciiToInt( src.first(), 4 );
        assertEquals( 0, result );
    }


    /*
     * Class under test for int asciiToInt(byte[], int, int)
     */
    public final void testAsciiToInt()
    {
        byte[] src = "12345".getBytes();
        int result = ByteArrayUtils.asciiToInt( src, 0, 4 );
        assertEquals( 1234, result );

        byte[] src2 = "00000".getBytes();
        int result2 = ByteArrayUtils.asciiToInt( src2, 0, 4 );
        assertEquals( 0, result2 );
    }


    public final void testReadWriteBoolean()
    {
        {
            ByteIterator dest = ByteIterator.getInstance( 5 );
            ByteArrayUtils.writeBoolean( dest, true );
            boolean result = ByteArrayUtils.readBoolean( dest.first() );
            assertEquals( true, result );
        }

        {
            ByteIterator dest = ByteIterator.getInstance( 5 );
            ByteArrayUtils.writeBoolean( dest, false );
            boolean result = ByteArrayUtils.readBoolean( dest.first() );
            assertEquals( false, result );
        }
    }


    public final void testReadWriteString()
    {
        {
            String TEST_STRING = "TestString";
            ByteIterator dest = ByteIterator.getInstance( 50 );
            ByteArrayUtils.writeAsciiString( dest, TEST_STRING );
            byte[] result = ByteArrayUtils.readAsciiString( dest.first() );
            ByteArrayUtils.assertEqual( TEST_STRING.getBytes(), result );
        }

        {
            String TEST_STRING = "TestString";
            ByteIterator src = ByteIterator.getInstance( TEST_STRING );
            ByteIterator dest = ByteIterator.getInstance( 50 );
            ByteArrayUtils.writeAsciiString( dest, src, TEST_STRING.length() );
            
            ByteIterator result = ByteIterator.getInstance( 40 );
            ByteArrayUtils.readAsciiStringInto( dest.first(), result );
            ByteArrayUtils.assertEqual( src.first().toArray(), result.first().toArray(), TEST_STRING.length() );
        }
    }
    

    public final void testReadWriteShort()
    {
        {
            ByteIterator dest = ByteIterator.getInstance( 5 );
            ByteArrayUtils.writeShort( dest, (short)1234 );
            short result = ByteArrayUtils.readShort( dest.first() );
            assertEquals( 1234, result );
        }

        {
            ByteIterator dest = ByteIterator.getInstance( 5 );
            ByteArrayUtils.writeShort( dest, (short)0 );
            short result = ByteArrayUtils.readShort( dest.first() );
            assertEquals( 0, result );
        }

        {
            ByteIterator dest = ByteIterator.getInstance( 5 );
            ByteArrayUtils.writeShort( dest, (short)-1234 );
            short result = ByteArrayUtils.readShort( dest.first() );
            assertEquals( -1234, result );
        }
    }


    public final void testReadWriteInt()
    {
        {
            ByteIterator dest = ByteIterator.getInstance( 5 );
            ByteArrayUtils.writeInt( dest, 1234 );
            int result = ByteArrayUtils.readInt( dest.first() );
            assertEquals( 1234, result );
        }

        {
            ByteIterator dest = ByteIterator.getInstance( 5 );
            ByteArrayUtils.writeInt( dest, 0 );
            int result = ByteArrayUtils.readInt( dest.first() );
            assertEquals( 0, result );
        }

        {
            ByteIterator dest = ByteIterator.getInstance( 5 );
            ByteArrayUtils.writeInt( dest, -1234 );
            int result = ByteArrayUtils.readInt( dest.first() );
            assertEquals( -1234, result );
        }
    }


    public final void testReadWriteLong()
    {
        {
            ByteIterator dest = ByteIterator.getInstance( 8 );
            ByteArrayUtils.writeLong( dest, 1234567890123456l );
            long result = ByteArrayUtils.readLong( dest.first() );
            assertEquals( 1234567890123456l, result );
        }

        {
            ByteIterator dest = ByteIterator.getInstance( 8 );
            ByteArrayUtils.writeLong( dest, 1234l );
            long result = ByteArrayUtils.readLong( dest.first() );
            assertEquals( 1234, result );
        }

        {
            ByteIterator dest = ByteIterator.getInstance( 8 );
            ByteArrayUtils.writeLong( dest, 0l );
            long result = ByteArrayUtils.readLong( dest.first() );
            assertEquals( 0, result );
        }

        {
            ByteIterator dest = ByteIterator.getInstance( 8 );
            ByteArrayUtils.writeLong( dest, -1234l );
            long result = ByteArrayUtils.readLong( dest.first() );
            assertEquals( -1234, result );
        }
        
        {
            ByteIterator dest = ByteIterator.getInstance( 16 );
            ByteArrayUtils.writeLong( dest, -1234567890123456l );
            long result = ByteArrayUtils.readLong( dest.first() );
            assertEquals( -1234567890123456l, result );
        }
        
        // perf test
        final int BUFF_SIZE  = 1000000;
        final int NUM_PASSES = 16;
        final int NUM_WRITES_PER_BUFF = BUFF_SIZE / 8;
        final int TOTAL_OPERATIONS = NUM_WRITES_PER_BUFF * NUM_PASSES;
        
        {
            ByteIterator dest = ByteIterator.getInstance( BUFF_SIZE );
            
            long startTime = System.currentTimeMillis();
            
            for ( int passes = 0; passes < NUM_PASSES; ++passes )
            {
                dest.first();
                for ( int writes = 0; writes < NUM_WRITES_PER_BUFF; ++writes )
                {
                    ByteArrayUtils.writeLong( dest, writes );
                }
             }
            
            long endTime = System.currentTimeMillis();
            
            long tp = SystemUtils.getThroughput( TOTAL_OPERATIONS, startTime, endTime);

            Logger.info( "WRITE Long test -- Iterations : " + BUFF_SIZE + ", Throughput = " + tp );
            
            startTime = System.currentTimeMillis();
            
            for ( int passes = 0; passes < NUM_PASSES; ++passes )
            {
                dest.first();
                for ( int writes = 0; writes < NUM_WRITES_PER_BUFF; ++writes )
                {
                    ByteArrayUtils.readLong( dest );
                }
            }
            
            endTime = System.currentTimeMillis();
            
            tp = SystemUtils.getThroughput( TOTAL_OPERATIONS, startTime, endTime);

            Logger.info( "READ Long test -- Iterations : " + BUFF_SIZE + ", Throughput = " + tp );
        }
                
    }

}
