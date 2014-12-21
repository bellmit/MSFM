package com.cboe.lwt.byteUtil;

import java.text.DecimalFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.cboe.lwt.byteUtil.ByteArrayUtils;
import com.cboe.lwt.byteUtil.ByteIterator;
import com.cboe.lwt.testUtils.SystemUtils;


/**
 * JUnit test object to test the EbcdicUtils object.
 *
 * @author peterson
 */
public class EbcdicConverterTest extends TestCase
{
    private static final int BLOCK_SIZE = 256;
    private static final int PERF_BLOCKS_TO_CONVERT = 500000;
    
    static byte[] convertableArray;
    
    static
    {
        convertableArray = new byte[ BLOCK_SIZE ];
        
        for ( int i = 0; i < BLOCK_SIZE; i++)
        {
            convertableArray[i] = (byte)i;
        }
    }
    
    /**
     * Constructor for EbcdicUtilsTest.
     * @param arg0
     */
    public EbcdicConverterTest(String arg0)
    {
        super(arg0);
    }
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run( suite() );
        junit.textui.TestRunner.run( perfSuite() );
    }
    
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(EbcdicConverterTest.class);
        
        return suite;
    }
    
    public static Test perfSuite()
    {
        TestSuite suite = new TestSuite();
        
        suite.addTest( new EbcdicConverterTest( "perfEbcdicToAscii_IP" ) );
        suite.addTest( new EbcdicConverterTest( "perfEbcdicToAscii_OP" ) );
        
        return suite;
    }
    
    
    public void perfEbcdicToAscii_IP()
    {
        System.out.println("Perf: EBCDIC conversion to ASCII (Inplace)");
        
        ByteIterator inIter = ByteIterator.getInstance( (byte[])convertableArray.clone() );
        ByteIterator outIter = inIter.shallowCopy();
        
        // test a single buffer for both the input and output
        long startTime = System.currentTimeMillis();
        
        for ( int i = 0; i < PERF_BLOCKS_TO_CONVERT; ++i )
        {
            EbcdicConverter.convertToAscii( inIter, outIter, convertableArray.length );
            inIter.first();
            outIter.first();
        }
        long endTime = System.currentTimeMillis();
        
        printStats( startTime, endTime, PERF_BLOCKS_TO_CONVERT, BLOCK_SIZE );
    }
    
    
    /**
     * Test the ebcdic to ascii translation
     */
    public void perfEbcdicToAscii_OP()
    {
        System.out.println("Perf: EBCDIC conversion to ASCII (out of place)");
        
        ByteIterator inIter = ByteIterator.getInstance( (byte[])convertableArray.clone() );
        ByteIterator outIter = ByteIterator.getInstance( new byte[ convertableArray.length ] );
        
        // test a single buffer for both the input and output
        long startTime = System.currentTimeMillis();
        
        for ( int i = 0; i < PERF_BLOCKS_TO_CONVERT; ++i )
        {
            EbcdicConverter.convertToAscii( inIter, outIter, convertableArray.length );
            inIter.first();
            outIter.first();
        }
        long endTime = System.currentTimeMillis();
        
        printStats( startTime, endTime, PERF_BLOCKS_TO_CONVERT, BLOCK_SIZE );
    }
    
    
    private static DecimalFormat fmt = new DecimalFormat( "###,###,###,###" );

    
    private void printStats( long p_startTime,
                             long p_endTime,
                             int  p_blocks,
                             int  p_bytesPerBlock )
    {
        long byteTp  = SystemUtils.getThroughput( p_bytesPerBlock * p_blocks,  p_startTime, p_endTime );
        long blockTp = SystemUtils.getThroughput( p_blocks, p_startTime, p_endTime );

        System.out.println( "    Time             : " + fmt.format( p_endTime - p_startTime ) );
        System.out.println( "    Blocks           : " + fmt.format( p_blocks ) );
        System.out.println( "    Bytes per block  : " + fmt.format( p_bytesPerBlock ) );
        System.out.println( "    Byte Throughput  : " + fmt.format( byteTp ) );
        System.out.println( "    Block Throughput : " + fmt.format( blockTp ) );
    }
    
    
    /**
     * Test the ebcdic to ascii translation
     */
    public void testEbcdicToAscii()
    {
        System.out.println("Testing EBCDIC conversion to ASCII");
        
        ByteIterator inIter  = ByteIterator.getInstance( (byte[])convertableArray.clone() );
        ByteIterator outIter = inIter.shallowCopy();
        
        // test a single buffer for both the input and output
        EbcdicConverter.convertToAscii( inIter, outIter, convertableArray.length );

        // verify the conversion happened. Not much we can test here except
        // to make sure some of the byte actually changed.  At least we
        // know some conversion took place.  We do know, because the EBCDIC/ASCII
        // relationship is not likely to change, that EBCDIC 0x04 is ASCII 0x9C,
        // so minimally, we can check that byte in the converted buffer.
        outIter.setIndex( 4 );
        assertEquals( (byte)0x9C, outIter.get() );
                
        // test using different buffers for input and output
        inIter.rebase( (byte[])convertableArray.clone() );

        outIter.rebase( new byte[ BLOCK_SIZE ] );

        EbcdicConverter.convertToAscii( inIter, outIter, convertableArray.length );

        // we can check a few things here.  Make sure we didn't change
        // the original input buffer.
        ByteArrayUtils.assertEqual( inIter.toArray(), convertableArray );
        
        // now make sure the outputBuffer got filled in.  Again, we
        // need to check only one byte to know that some conversion happened.
        // EBCDIC 0x04 should always result in ASCII 0x9C.
        outIter.setIndex( 4 );
        assertEquals( 0x9C, 0xFF & outIter.get() );
        
        
        // test invalid output buffer length
        try
        {
            inIter.first();
            outIter.end();
            
            EbcdicConverter.convertToAscii( inIter, outIter, 1 ); // should run out of space
            fail( "didn't assert on bad length... make sure you are compiling with -ea" );
        }
        catch( ArrayIndexOutOfBoundsException ex )
        {
        }
    }

        
    /**
     * Test the ascii to ebcdic translation
     */
    public void testasciiToEbcdic()
    {
        System.out.println("Testing ASCII conversion to EBCDIC");
        
        ByteIterator inIter  = ByteIterator.getInstance( (byte[])convertableArray.clone() );
        ByteIterator outIter = inIter.shallowCopy();

        // test a single buffer for both the input and output
        EbcdicConverter.convertFromAscii( inIter, outIter, convertableArray.length );

        // verify the conversion happened. Not much we can test here except
        // to make sure some of the byte actually changed.  At least we
        // know some conversion took place.  We do know, because the EBCDIC/ASCII
        // relationship is not likely to change, that ASCII 0x61 is EBCDIC 0x81,
        // so minimally, we can check that byte in the converted buffer.
        inIter.setIndex( 97 );
        assertEquals( 0x81, 0xFF & inIter.get() );
                
        // test using different buffers for input and output
        inIter.rebase( (byte[])convertableArray.clone() );

        outIter.rebase( (byte[])convertableArray.clone()  );

        EbcdicConverter.convertFromAscii( inIter, outIter, convertableArray.length );

        // we can check a few things here.  Make sure we didn't change
        // the original input buffer.
        for ( inIter.first(); inIter.isValid(); inIter.next() )
        {
            assertEquals( (byte)(0 | inIter.getIndex() ), inIter.get() );
        }
        
        // now make sure the outputBuffer got filled in.  Again, we
        // need to check only one byte to know that some conversion happened.
        // ASCII 0x61 should always result in EBCDIC 0x81.
        outIter.setIndex( 97 );
        assertEquals( (byte)(0 | 0x81), outIter.get() );
        
        
        // test invalid output buffer length
        try
        {
            inIter.first();
            outIter.end();
            EbcdicConverter.convertFromAscii( inIter, outIter, 1 ); // should run out of space
            fail( "didn't assert on bad length... make sure you are compiling with -ea" );
        }
        catch( ArrayIndexOutOfBoundsException ex )
        {
        }
    }
    
    
    /**
     * Test the conversion from ebcdic to ascii and then back to
     * ebcdic to verify the original byte pattern is generated.
     */
    public void testdoubleConversion()
    {
        System.out.println("Testing EBCDIC conversion to ASCII and then back to EBCDIC");
        
        // test a single buffer for both the input and output
        ByteIterator inIter  = ByteIterator.getInstance( (byte[])convertableArray.clone() );
        ByteIterator outIter = ByteIterator.getInstance( new byte[ convertableArray.length ] );
        

        // convert to ascii out-of-place
        EbcdicConverter.convertToAscii( inIter, outIter, convertableArray.length );
      
        // now convert back to ebcdic in place
        outIter.first();
        inIter.rebase( outIter );
        
        EbcdicConverter.convertFromAscii( inIter, outIter, convertableArray.length );
        
        // every byte after converting back to EBCDIC
        // should be the same as the original input
        ByteArrayUtils.assertEqual( convertableArray, outIter.toArray() );
    }
    
    
    public void testspecificConversion()
    {
        System.out.println("Testing specific character conversion");
        
        byte[] ebcdicVals = 
            {
                (byte)0xC1,     // 'A'
                (byte)0xD4,     // 'M'
                (byte)0xE9,     // 'Z'
                (byte)0x81,     // 'a'
                (byte)0xA9,     // 'z'
                (byte)0xF1,     // '1'
                (byte)0xF9,     // '9'
                (byte)0x6B,     // ','
                (byte)0x6C,     // '%'
                (byte)0x4B      // '.'
            };

        byte[] asciiVals ="AMZaz19,%.".getBytes();

        ByteIterator inIter  = ByteIterator.getInstance( (byte[])ebcdicVals.clone() );
        ByteIterator outIter = ByteIterator.getInstance( new byte[ ebcdicVals.length ] );

        EbcdicConverter.convertToAscii( inIter, outIter, asciiVals.length );
        
        ByteArrayUtils.assertEqual( asciiVals, outIter.toArray() );
        
        inIter.rebase( (byte[])asciiVals.clone() );
        outIter.first();
        
        EbcdicConverter.convertFromAscii( inIter, outIter, asciiVals.length );
        
        ByteArrayUtils.assertEqual( ebcdicVals, outIter.toArray() );
    }
    
}
