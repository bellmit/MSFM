package com.cboe.lwt.byteUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;



/**
 * Abstracts the physical storage for a virtual array.  Tracks a current position
 * within this array.  Allows for reading and writing at this current position,
 * and moving this position.
 *
 * @author dotyl
 */
public final class ByteVector 
{
    // package private to allow iterator to have access
    byte[] physicalStorage;  // the underlying array
    int    lowBound;         // the index of the first reachable element of the underlying array
    int    highBound;        // the index of the last reachable element of the underlying array
    
 
    ////////////////////////////////////////////////////////////////////////////
    //  constructor
    
    // forces use of static factory method
    private ByteVector()
    {
        physicalStorage = null;
        lowBound        = 0;
        highBound       = 0;
    }
  
    
    ////////////////////////////////////////////////////////////////////////////
    // factory methods
        
    
    /**
     * @return an instance of an uninitialized vector.  This returned vector
     * MUST BE REBASED before it can be used
     */
    public static final ByteVector getInstance()
    {
        return new ByteVector();
    }
    
    
    public static final ByteVector getInstance( int p_minSize )
    {
        return ByteVector.getInstance( new byte[ p_minSize ] );
    }

    
    /**
     * Wraps the specified byte array with a ByteVector.
     *
     * @param p_physicalStorage Array to be referenced
     * @return a ByteVector with an iteration range over the whole array
     */
    public static final ByteVector getInstance( byte[] p_physicalStorage )
    {
        ByteVector vector = new ByteVector();
        
        vector.rebase( p_physicalStorage,
                       0,
                       p_physicalStorage.length );
        
        return vector;
    }
    
    
    /**
     * Convenience method:
     * 
     * converts a string to bytes, then wraps the byte array with a ByteVector
     *
     * @param string to convert
     * @return a ByteVector with an usable region of the bytes in p_string
     */
    public static final ByteVector getInstance( String p_string )
    {
        return getInstance( p_string.getBytes() );
    }
    
    
    /**
     * returns a usable ByteVector referencing the specified subarray
     *
     * @param p_physicalStorage The underlying array
     * @param p_offset the index of the first reachable element of the underlying storage
     * @param p_length the number of bytes the vector can traverse
     * @param p_curPhysicalIndex the index of the current element in the underlying storage
     * @return  a usable ByteVector referencing the specified subarray
     */
    public static final ByteVector getInstance( byte[] p_physicalStorage,
                                                int    p_offset,
                                                int    p_length )
    {
        ByteVector vector = new ByteVector();
        
        vector.rebase( p_physicalStorage,
                       p_offset,
                       p_length );
        
        return vector;
    }

    
    ////////////////////////////////////////////////////////////////////////////
    // methods to modify the logical array (changes in underlying array as well as iteratable region)
    
    /** rebases this ByteVector to reflect the specified underlying storage
     * Changes this instance into a clone of the spedified ByteVector
     *
     * @param p_iter ByteVector to immitate
     */
    public final void rebase( ByteVector p_iter )
    {
        physicalStorage = p_iter.physicalStorage;
        lowBound        = p_iter.lowBound;
        highBound       = p_iter.highBound;
    }
    
    
    /** rebases this ByteVector to reflect the specified underlying storage
     * @param p_physicalStorage The underlying array
     * @param p_offset the index of the first reachable element of the underlying storage
     * @param p_length the number of bytes the vector can traverse
     */
    public final void rebase( byte[] p_physicalStorage,
                              int    p_offset,
                              int    p_length )
    {
        assert( p_offset >= 0 ) : getAssertionMsg( "offset is negative" );
        assert( p_length > 0 )  : getAssertionMsg( "length must be > 0" );
        
        physicalStorage = p_physicalStorage;
        lowBound        = p_offset;
        highBound       = p_offset + p_length - 1;   // -1 for length/index transform
        
        assert( physicalStorage != null );
        assert( lowBound  < physicalStorage.length ) : getAssertionMsg( "Illegal Low Bound" );
        assert( highBound < physicalStorage.length ) : getAssertionMsg( "Illegal High Bound" );
    }
    
    
    /** rebases this ByteVector to reflect the specified underlying storage
     * The entire array will be referencable through the ByteVector
     *
     * @param p_physicalStorage Array to be referenced
     */
    public final void rebase( byte[] p_physicalStorage )
    {
        rebase( p_physicalStorage,
                0,
                p_physicalStorage.length );
    }
    
    
    /**
     * Stretches the bounds of this ByteVector to the bounds of the underlying storage
     */
    public final void rebaseToArrayBounds()
    {
        lowBound = 0;
        highBound = physicalStorage.length - 1;  // -1 is for length/index transformation
    }
    
    
    /** rebases this object to exclude the specified number of bytes at the beginning of the vector
     *
     * @param p_trimSize the number of bytes to remove from the front of the vector
     */
    public final void leftTrim( int p_trimSize )
    {
        assert ( length() >= p_trimSize ) : getAssertionMsg( "Cant trim to less than a zero length" );
        assert ( p_trimSize >= 0 ) : "negative trim size not allowed";
        
        lowBound += p_trimSize;
    }

    
    /** rebases this object to exclude the specified number of bytes at the end of the vector
     *
     * @param p_trimSize the number of bytes to remove from the end of the vector
     */
    public final void rightTrim( int p_trimSize )
    {
        assert ( length() >= p_trimSize ) : getAssertionMsg( "Cant trim to less than a zero length" );
        assert ( p_trimSize >= 0 ) : "negative trim size not allowed";
        
        highBound -= p_trimSize;
    }

    
    // methods to modify the logical array (changes in underlying array as well as iteratable region)
    ////////////////////////////////////////////////////////////////////////////
    // copy/clone methods
    
    
    /** creates a new ByteVector which references the specified sub-region of
     *  this vector
     * 
     * @param p_offset the first logical index of the subvector (may cause left trimming)
     * @param p_length length of the new vector (may cause right trimming)
     * @return new sub vector
     */
    public final ByteVector subVector( int p_offset, 
                                       int p_length ) 
    {
        int newHighBound = lowBound + p_offset + p_length - 1;  // -1 is for length to index calc
        
        assert ( p_offset >= 0 )             : getAssertionMsg( "offset < 0, == " + p_offset );
        assert ( p_offset <= length() )        : getAssertionMsg( "offset > size, == " + p_offset );
        assert ( newHighBound <= highBound ) : getAssertionMsg( "subVector can't have a higher high bound" );
        assert ( p_length >= 0 )             : getAssertionMsg( "length cannot be negative end > totalSize" );

        ByteVector sub = shallowCopy();
        sub.lowBound += p_offset;
        sub.highBound = newHighBound;
        
        return sub;
    }
    
    
    /**
     * @return a clone of this ByteVector that refers to the same storage
     *
     * NOTE: Does NOT copy the underlying storage!
     */
    public final ByteVector shallowCopy()  // didn't use clone since didn't want to return type Object (ick!)
    {
        ByteVector vector = new ByteVector();
        
        vector.rebase( this );
        
        return vector;
    }
    
    
    /** Creates a new ByteVector with its own copy of the physical storage 
     * ONLY IN THE AVAILABLE REGION of this ByteVector
     *
     * NOTE: the current instance will not be changed in any way.
     */
    public final ByteVector deepCopy() 
    {
        ByteVector resultCopy = ByteVector.getInstance( length() );
        
        System.arraycopy( physicalStorage,
                          0,
                          resultCopy.physicalStorage,
                          0,
                          length() );
        resultCopy.lowBound  = 0;
        resultCopy.highBound = length() - 1;
        
        return resultCopy;
    }   
    

    // copy/clone methods
    ////////////////////////////////////////////////////////////////////////////
    

    /**
     * @return a copy of this ByteVector's controlled subarray (the iteratable region)
     */
    public final byte[] toArray()
    {
        int    length = ( highBound - lowBound ) + 1; // +1 is for index/length transform
        byte[] result = new byte[ length ];
        
        System.arraycopy( physicalStorage, lowBound, result, 0, length );
        
        return result;
    }


    ////////////////////////////////////////////////////////////////////////////
    // get methods
    
    
    /**
     * returns the specified logical element (the element p_logicalIndex away from lowBound)
     * @param p_logicalIndex to read from
     *
     * @return this object to allow command chaining
     */
    public final byte get( int p_logicalIndex )
    {
        return physicalStorage[ p_logicalIndex + lowBound ];
    }
    
    
    /** Puts p_length bytes from the specified offset into p_dest
     * The current index is moved to the end of the copy region
     *
     * @param p_dest the destination of the copy operation (a ByteBuffer)
     * @param p_length how many bytes to copy
     */
    public final void get( ByteBuffer p_dest, int p_srcOffset, int p_length )
    {
        assertSubvector( p_srcOffset,
                         p_length );
        
        assert( p_dest.remaining() >= p_length )
                : getAssertionMsg( "Destination remaining length (" + ( p_dest.remaining() )
                                   + ") is shorter than copy length (" + p_length + ")" );
        
        try
        {
            p_dest.put( physicalStorage,
                        lowBound + p_srcOffset,
                        p_length );
        }
        catch ( RuntimeException ex )
        {
            ex.initCause( new Exception( "Writing length of " + p_length + " to ByteBuffer with remaining bytes = " + p_dest.remaining() ) );
            throw ex; 
        }
    }
    
    
    /** Copies p_length bytes from p_srcOffset into p_dest starting at p_destOffset
     *
     * @param p_srcOffset offset in this vector to start at
     * @param p_dest destination of the write
     * @param p_destOffset offset within the destination
     * @param p_length length of the write
     */
    public final void get( int    p_srcOffset, 
                           byte[] p_dest, 
                           int    p_destOffset,
                           int    p_length )
    {
        assertSubvector( p_srcOffset,
                         p_length );
        
        assert( p_dest.length - p_destOffset >= p_length )
                : getAssertionMsg( "Destination length from offset (" + ( p_dest.length - p_destOffset )
                                   + ") is shorter than copy length (" + p_length + ")" );
        
        System.arraycopy( physicalStorage,
                          lowBound + p_srcOffset,
                          p_dest,
                          p_destOffset,
                          p_length );
    }
    
    
    /** Copies p_length bytes from this object at p_srcOffset into p_dest starting at p_destOffset
     *
     * @param p_srcOffset offset in this vector to start at
     * @param p_dest destination of the write
     * @param p_destOffset offset within the destination
     * @param p_length length of the write
     */
    public final void get( int        p_srcOffset, 
                           ByteVector p_dest, 
                           int        p_destOffset,
                           int        p_length )
    {
        assertSubvector( p_srcOffset,
                         p_length );
        
        System.arraycopy( physicalStorage,
                          lowBound + p_srcOffset,
                          p_dest.physicalStorage,
                          p_destOffset,
                          p_length );
    }
    
    
    /** returns the offset of p_target, if it exists in the next p_length bytes
     * otherwise returns -1
     *
     * @return offset of p_target if found, -1 otherwise
     * @param p_target byte to look for
     * @param p_length the number of bytes to search before failure NOTE: this must be <= remainingBytes
     */
    public final int offsetOf( byte p_target, int p_startAtOffset, int p_length )
    {
        int stopBeforeIndex = lowBound + p_startAtOffset + p_length;
        
        assert ( stopBeforeIndex <= highBound + 1 ) : getAssertionMsg( "illegal length" );
        
        for ( int i = p_startAtOffset + lowBound; i < stopBeforeIndex; ++i )
        {
            if ( physicalStorage[ i ] == p_target )
            {
                return i - lowBound;
            }
        }
        return -1; // didn't find it
    }
    
    
    /** returns the offset of p_target, if it exists in the remaining bytes
     * otherwise returns -1
     *
     * @return offset of p_target if found, -1 otherwise
     * @param p_target byte to look for
     */
    public final int offsetOf( byte p_target, int p_length )
    {
        return offsetOf( p_target, 0, p_length );
    }
    
    
    /** returns the offset of p_target, if it exists in the remaining bytes
     * otherwise returns -1
     *
     * @return offset of p_target if found, -1 otherwise
     * @param p_target byte to look for
     */
    public final int offsetOf( byte p_target )
    {
        return offsetOf( p_target, 0, length() );
    }
    
    
    /** returns the offset of p_target, if it exists in the remaining bytes
     * otherwise returns -1
     *
     * @return offset of p_target if found, -1 otherwise
     * @param p_target byte to look for
     */
    public final int offsetOf( byte[] p_target, int p_startSearchAtOffset, int p_searchLength )
    {
        assert( length() >= p_target.length ) : "Bad search length of " + p_searchLength + ", with length of " +  p_target.length;
        
        ByteIterator iter = iterator( p_startSearchAtOffset, p_searchLength );
 
        while( iter.isValid() )
        {
            int searchPos = iter.offsetOf( p_target[0] );
        
            if ( searchPos == -1 )
            { 
                return -1;
            }
            
            iter.next( searchPos );
            
            if ( iter.remaining() < p_target.length )
            { 
                return -1;
            }
    
            if ( iter.nextTokenIs( p_target ) )
            {
                return iter.getIndex() + p_startSearchAtOffset - p_target.length;
            }
            
            iter.next();
        }
        
        return -1;
    }
    
    
    /** returns the offset of p_target, if it exists in the next p_length bytes
     * otherwise returns -1
     *
     * @return offset of p_target if found, -1 otherwise
     * @param p_target byte to look for
     * @param p_length the number of bytes to search before failure NOTE: this must be <= remainingBytes
     */
    public final int offsetOf( byte[] p_target, int p_startAtOffset )
    {
        return offsetOf( p_target,
                         p_startAtOffset,
                         length() - p_startAtOffset );
    }
    
    
    /** returns the offset of p_target, if it exists in the next p_length bytes
     * otherwise returns -1
     *
     * @return offset of p_target if found, -1 otherwise
     * @param p_target byte to look for
     * @param p_length the number of bytes to search before failure NOTE: this must be <= remainingBytes
     */
    public final int offsetOf( byte[] p_target )
    {
        return offsetOf( p_target,
                         0,
                         length() );
    }


    // get methods
    ////////////////////////////////////////////////////////////////////////////
    // set methods


    /** Sets the byte at logical index p_logicalIndex to p_new
     * @param p_new 
     * @param p_logicalIndex
     */
    public final void set( byte p_new, int p_logicalIndex )
    {
        assert( p_logicalIndex >= 0 && p_logicalIndex < length() ) : getAssertionMsg( "idx out of range" );
        physicalStorage[ lowBound + p_logicalIndex ] = p_new;
    }
     
    
    /** Copies p_length bytes from p_src into this object beginning at the p_destOffset
     * The current index is placed after the last copied byte after the operation
     *
     * NOTE: this ByteVector's current position will be left on the byte AFTER the
     *       last copied character
     *
     * @param p_src source of the copied bytes
     * @param p_srcOffset offset within p_src of the first byte to copy
     * @param p_length the copy length
     */
    public final void set( byte[] p_src, 
                           int    p_srcOffset, 
                           int    p_destOffset,
                           int    p_length )
    {
        assertSubvector( p_destOffset,
                         p_length );
        
        assert( p_src.length - p_srcOffset >= p_length ) : getAssertionMsg( "Source remaining length is shorter than copy length" );
        
        System.arraycopy( p_src,
                          p_srcOffset,
                          physicalStorage,
                          p_destOffset,
                          p_length );
    }
     
    
    /** Copies p_length bytes from p_src into this object beginning at the p_destOffset
     * The current index is placed after the last copied byte after the operation
     *
     * NOTE: this ByteVector's current position will be left on the byte AFTER the
     *       last copied character
     *
     * @param p_src source of the copied bytes
     * @param p_srcOffset offset within p_src of the first byte to copy
     * @param p_length the copy length
     */
    public final void set( ByteVector p_src, 
                           int        p_srcOffset, 
                           int        p_destOffset,
                           int        p_length )
    {  
        assertSubvector( p_destOffset,
                         p_length );
        
        p_src.get( p_srcOffset,
                   this,
                   lowBound + p_destOffset,
                   p_length );
    }

    
    /** copies p_fill into all bytes in the ByteVector between index and highBound (inclusive)
     * after the operation, the current index will be after the last filled byte
     *
     * NOTE: this ByteVector's current position will be left on the byte AFTER the
     *       last copied character
     *
     * @param p_fill the byte to fill with
     * @param p_fillLength the number of fill characters to insert
     */
    public final void fill( byte p_fill, 
                            int  p_offset, 
                            int  p_length )
    {
        assert( length() >= p_offset + p_length )
                : getAssertionMsg( "vector size after offset (" + ( length() - p_offset )
                                   + ") is shorter than copy length (" + p_length + ")" );
        
        Arrays.fill( physicalStorage,
                     lowBound + p_offset, 
                     lowBound + p_offset + p_length,
                     p_fill );
    }
    
    
    // set methods
    ////////////////////////////////////////////////////////////////////////////
    // informational methods
    
    
    /**
     * @return the number of bytes available for iteration- before and after the current position
     */
    public final int length()
    {
        return ( highBound - lowBound ) + 1;  // +1 is because both lowBound and highBound are included
    }
    
    
    // informational methods
    ////////////////////////////////////////////////////////////////////////////
    // iterator methods
    
    /**
     * moves the iterator to the first element
     *
     * @return this object to allow command chaining
     */
    public final ByteIterator iterator()
    {
        ByteIterator result = ByteIterator.getInstance( physicalStorage, 
                                                        lowBound,
                                                        length(),
                                                        lowBound );        
        return result;
    }
    
    
    /**
     * moves the iterator to the first element
     *
     * @return this object to allow command chaining
     */
    public final ByteIterator iterator( int p_offset,
                                        int p_length )
    {
        ByteIterator result = ByteIterator.getInstance( physicalStorage, 
                                                        lowBound + p_offset,
                                                        p_length );
        return result;
    }


    // iterator methods
    ////////////////////////////////////////////////////////////////////////////
    
    /** @param p_test vector to test for equivalence
     * @return true if this vector refers to the same byte in the same array as p_test
     */
    public final boolean equals( ByteVector p_test )
    {
        return this == p_test 
            || ( p_test.physicalStorage == physicalStorage 
              && p_test.lowBound == lowBound 
              && p_test.highBound == highBound );
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    //  methods to display the ByteVector's state

    private final String getAssertionMsg( String p_msg )
    {
        StringBuffer sb = new StringBuffer( p_msg );
        sb.append( "\n--------------\n" );
        
        sb.append( "Low = " )
          .append( lowBound )
          .append( ", High = " )
          .append( highBound )
          .append( '\n' )
          .append( "\n--------------\n" )
          .append( toString() );
         
        return sb.toString();
    }
    
    
    /**
     * @return string with a detailed (verbose) description of the current state
     * of this vector
     */
    public final void appendDebugString( StringBuffer p_sb )
    {
        appendDebugString( p_sb, lowBound, highBound ); 
    }
    
        
    /**
     * @return string with a detailed (verbose) description of the current state
     * of this iterator
     */
    public final void appendUnderlyingDebugString( StringBuffer p_sb )
    {
        appendDebugString( p_sb, 0, physicalStorage.length - 1 ); 
    }
    
    
    /**
     * @param p_start The first index to display
     * @param p_end the Last index to display
     * @return string with a detailed (verbose) description of the current state
     * of this vector between the specified indices
     */
    public final void appendDebugString( StringBuffer p_sb,
                                         int p_start, 
                                         int p_end )
    {
        if ( physicalStorage == null )
        {
            p_sb.append( "Underlying storage is NULL" );
        }
        
        if ( physicalStorage.length == 0 )
        {
            p_sb.append( "Underlying storage has 0 length" );
        }
        
        assert( p_start >= 0 
             && p_end >= p_start 
             && p_end <= physicalStorage.length ) : getAssertionMsg( "bad range" );
                 
        p_sb.append( "\n-----------------------\nByteVector\n-----------------------\n" )
            .append( "\nLow = " )
            .append( lowBound )
            .append( ", High = " )
            .append( highBound )
            .append( '\n' );
            
        for ( int i = p_start; i <= p_end; i++ )
        {
            // print the index
            p_sb.append( "pos : " + i )
                .append( " = ["  )
                .append( physicalStorage[i] & 0xFF )
                .append( "] (" )
                .append( (char) physicalStorage[i] )
                .append( ")" );
                
            // decorate the index with any signifigance...
                
            if ( i == lowBound )
            {
                p_sb.append( " = Low Bound" );
            }
                
            if ( i == highBound )
            {
                p_sb.append( " = High Bound" );
            }

            p_sb.append( "\n" ); 
        }
    
        p_sb.append( "-----------------------\n" );
    }
  
    
    /**
     * @return string with a detailed (verbose) description of the current state
     * of this vector
     */
    public final String toString()
    {
        return toString( lowBound, highBound ); 
    }
    
    /**
     * @param p_start The first index to display
     * @param p_end the Last index to display
     * @return string with a detailed (verbose) description of the current state
     * of this vector between the specified indices
     */
    public final String toString( int p_start, 
                                  int p_end )
    {
        if ( physicalStorage == null )
        {
            return "Underlying storage is NULL";
        }
        
        if ( physicalStorage.length == 0 )
        {
            return "Underlying storage has 0 length";
        }
        
        if (  p_start < 0 )
        {
            return "Illegal start index";
        }
        
        if ( p_end < p_start )
        {
            return "start index must be less than or equal to end index";
        }
        
        StringBuffer result = new StringBuffer();
        
        if ( p_end > physicalStorage.length - 1 )
        {
            result.append( "end index is greater than storage length, truncating to storage length" );
            p_end = physicalStorage.length - 1;
        }
        
        
        for ( int i = p_start; i <= p_end; i++ )
        {
            result.append( (char) physicalStorage[i] );
        }
        
        return result.toString();
    }

    
    /**
     * @param p_srcOffset
     * @param p_length
     */
    private void assertSubvector( int p_srcOffset, int p_length )
    {
        int availableSpace = length();
        assert( p_length >= 0 )                                    : getAssertionMsg( "Bad length : " + p_length );
        assert( p_srcOffset >= 0 && p_srcOffset < availableSpace ) : getAssertionMsg( "Bad Offset : " + p_length ); 
        assert( p_srcOffset + p_length <= availableSpace )         : getAssertionMsg( "offset (" + p_srcOffset
                                                                                    + ") and copy length (" + p_length + ") is too long" );
    }
    
};

