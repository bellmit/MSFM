package com.cboe.lwt.byteUtil;

import java.io.IOException;



/**
 * Abstracts the physical storage for a virtual array.  Tracks a current position
 * within this array.  Allows for reading and writing at this current position,
 * and moving this position.
 *
 * @author dotyl
 */
public final class ByteIterator implements ByteWriter
{
    byte[] physicalStorage;  // the underlying array
    int    lowBound;         // the index of the first reachable element of the underlying array
    int    highBound;        // the index of the last reachable element of the underlying array
    int    index;            // the current index
    
    ///////////////////////////////////
    // factory methods
    
    
    public static ByteIterator getInstance( int p_minSize )
    {
        ByteIterator iter = new ByteIterator();
        
        iter.rebase( new byte[ p_minSize ],
                     0,
                     p_minSize,
                     0 );
        
        return iter;
    }
        
    
    /**
     * @return an instance of an uninitialized iterator.  This returned iterator
     * MUST BE REBASED before it can be used
     */
    public static ByteIterator getInstance()
    {
        return new ByteIterator();
    }
    
    
    /**
     * Wraps the specified byte array with a byteIterator.
     *
     * @param p_physicalStorage Array to be referenced
     * @return a ByteIterator with an iteration range over the whole array
     */
    public static ByteIterator getInstance( byte[] p_physicalStorage )
    {
        ByteIterator iter = new ByteIterator();
        
        iter.rebase( p_physicalStorage,
                     0,
                     p_physicalStorage.length,
                     0 );
        
        return iter;
    }
    
    
    /**
     * Wraps the specified byte array with a byteIterator.
     *
     * @param p_physicalStorage Array to be referenced
     * @return a ByteIterator with an iteration range over the whole array
     */
    public static ByteIterator getInstance( String p_physicalStorage )
    {
        return getInstance( p_physicalStorage.getBytes() );
    }
    
    
    /**
     * returns a usable ByteIterator referencing the specified subarray with the
     * current position set to the first byte
     *
     * @param p_physicalStorage The underlying array
     * @param p_offset the index of the first reachable element of the underlying storage
     * @param p_length the number of bytes the iterator can traverse
     * @return  a usable ByteIterator referencing the specified subarray
     */
    public static ByteIterator getInstance( byte[] p_physicalStorage,
                                            int    p_offset,
                                            int    p_length )
    {
        ByteIterator iter = new ByteIterator();
        
        iter.rebase( p_physicalStorage,
                     p_offset,
                     p_length,
                     p_offset );
        
        return iter;
    }
    
    
    /**
     * returns a usable ByteIterator referencing the specified subarray
     *
     * @param p_physicalStorage The underlying array
     * @param p_offset the index of the first reachable element of the underlying storage
     * @param p_length the number of bytes the iterator can traverse
     * @param p_curPhysicalIndex the index of the current element in the underlying storage
     * @return  a usable ByteIterator referencing the specified subarray
     */
    public static ByteIterator getInstance( byte[] p_physicalStorage,
                                            int    p_offset,
                                            int    p_length,
                                            int    p_curPhysicalIndex )
    {
        ByteIterator iter = new ByteIterator();
        
        iter.rebase( p_physicalStorage,
                     p_offset,
                     p_length,
                     p_curPhysicalIndex );
        
        return iter;
    }
    
    
    // factory methods
    ///////////////////////////////////
    
    //  static
    ////////////////////////////////////////////////////////////////////////////
    
   
    ////////////////////////////////////////////////////////////////////////////
    //  constructor
    
    // forces use of static factory method
    private ByteIterator()
    {
        physicalStorage = null;
        lowBound        = 0;
        highBound       = 0;
        index           = 1;  // set the byteIterator to report no remaining available bytes (past highBound)
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    // conversion
    
    
    /** @return a ByteVector that has as its available region the iteratable region
     * of this iterator (the area between lowBound and highBound (inclusive))
     */
    public ByteVector vector()
    {
        ByteVector result = ByteVector.getInstance();
        
        result.physicalStorage = physicalStorage;
        result.lowBound = lowBound;
        result.highBound = highBound;
        
        return result;
    }
    
    
    // conversion
    ////////////////////////////////////////////////////////////////////////////
    // methods to change the logical array (changes in underlying array as well as iteratable region)
    
    /** rebases this ByteIterator to reflect the specified underlying storage
     * Changes this instance into a clone of the spedified ByteIterator
     *
     * @param p_iter ByteIterator to immitate
     */
    public final void rebase( ByteIterator p_iter )
    {
        physicalStorage = p_iter.physicalStorage;
        lowBound        = p_iter.lowBound;
        highBound       = p_iter.highBound;
        index           = p_iter.index;
    }
    
    
    /** rebases this ByteIterator to reflect the specified underlying storage
     * @param p_physicalStorage The underlying array
     * @param p_offset the index of the first reachable element of the underlying storage
     * @param p_length the number of bytes the iterator can traverse
     * @param p_curPhysicalIndex the PHYSICA (not logical) index of the current element in the underlying storage
     */
    public final void rebase( byte[] p_physicalStorage,
                              int    p_offset,
                              int    p_length,
                              int    p_curPhysicalIndex )
    {
        assert( p_offset >= 0 ) : getAssertionMsg( "offset is negative" );
        assert( p_length > 0 )  : getAssertionMsg( "length must be > 0" );
        
        physicalStorage = p_physicalStorage;
        lowBound        = p_offset;
        highBound       = p_offset + p_length - 1;   // -1 for length/index transform
        index           = p_curPhysicalIndex;
        
        assert( physicalStorage != null );
        assert( lowBound  < physicalStorage.length ) : getAssertionMsg( "Illegal Low Bound" );
        assert( highBound < physicalStorage.length ) : getAssertionMsg( "Illegal High Bound" );
        assert( index <= highBound && index >= lowBound ) : getAssertionMsg( "illegal index = " + index );
    }
    
    
    /** rebases this ByteIterator to reflect the specified underlying storage
     * The entire array will be referencable through the ByteIterator
     *
     * @param p_physicalStorage Array to be referenced
     */
    public final void rebase( byte[] p_physicalStorage )
    {
        rebase( p_physicalStorage,
                0,
                p_physicalStorage.length,
                0 );
    }
    

    /**
     * @return a clone of this ByteIterator that refers to the same storage
     *
     * NOTE: Does NOT copy the underlying storage!
     */
    public final ByteIterator shallowCopy()  // didn't use clone since didn't want to return type Object (ick!)
    {
        ByteIterator iter = new ByteIterator();
        
        iter.rebase( this );
        
        return iter;
    }
    
    
    /** Creates a new ByteIterator with its own copy of the physical storage 
     * ONLY IN THE ITERATABLE REGION of this ByteIterator
     *
     * NOTE: the current instance will not be changed in any way.
     */
    public final ByteIterator deepCopy() 
    {
        ByteIterator resultCopy = ByteIterator.getInstance( length() );
        
        System.arraycopy( physicalStorage,
                          index,
                          resultCopy.physicalStorage,
                          0,
                          length() );
        resultCopy.index     = index;
        resultCopy.lowBound  = 0;
        resultCopy.highBound = length() - 1;
        
        return resultCopy;
    }   
    

    // copy/clone/imitate methods
    ////////////////////////////////////////////////////////////////////////////
    // trim and boundary changes
    

    /**
     * rebases this object to exclude all bytes to the left of the current position
     * (the new lowBound is == to the current index... current index remains unchanged)
     */
    public final void leftTrim()
    {
        assert ( isValid() ) : getAssertionMsg( "Cant trim when iterating outside iteration region" );
        
        lowBound = index;
    }
    
    
    /** rebases this object to exclude all bytes to the left of the current position, then sets
     * highbound to be p_length bytes after cur
     *
     * Note:  after this method completes, 
     *        lowBound = to the index
     *        index remains unchanged 
     *        highBound == index + ( p_length -1 )
     *
     * @param p_length the new length (length of the iteratable region)
     */
    public final void leftTrimToLength( int p_length )
    {
        int newHighBound = index + ( p_length - 1 );  // -1 is for length/index transform

        assert ( isValid() ) : getAssertionMsg( "Cant trim when iterating outside iteration region" );
        assert ( newHighBound <= highBound ) : getAssertionMsg( "Cant trim to a longer length of : " + p_length );
        
        lowBound = index;
        highBound = newHighBound;
    }
    
    
    /**
     * rebases this object to exclude all bytes to the right of the current position
     * (the new highBound is == to the current index... current index remains unchanged)
     */
    public final void rightTrim()
    {
        assert ( isValid() ) : getAssertionMsg( "Cant trim when iterating outside iteration region" );
        
        highBound = index;
    }
    
    
    /**
     * Stretches the bounds of this byteIterator to the bounds of the underlying storage
     */
    public final void rebaseToArrayBounds()
    {
        lowBound = 0;
        highBound = physicalStorage.length - 1;  // -1 is for length/index transformation
    }
    
    
    /**
     * rebase to the full array with the index set to the first byte
     * 
     * @return the capacity of the reset array
     */
    public final int reset()
    {
        lowBound = 0;
        highBound = physicalStorage.length - 1;  // -1 is for length/index transformation
        index = 0;
        
        return physicalStorage.length;
    }
    
    
    /**
     * rebase to the full array with the index set to the first byte
     */
    public final void resetToLength( int p_length )
    {
        assert ( p_length < physicalStorage.length ) 
               : "Bad length of " 
                 + p_length 
                 + " for iter of size " 
                 + physicalStorage.length;
        
        lowBound = 0;
        highBound = p_length - 1;  // -1 is for length/index transformation
        index = 0;
    }
    
    
    // trim and boundary changes
    ////////////////////////////////////////////////////////////////////////////

    
    /**
     * <b>NOTE: the 2 iterators must have the asme underlying storage or this method
     * will assert</b>
     *
     * @param p_iter The ByteIterator to compare with
     * @return the distance in bytes of this iterator's current index from the
     * specified ByteIterator's current index <b>NOTE: if this iterator is positioned
     * to the right of p_iter, the result will be positive, if this iterator is referencing
     * a lower byte (current index), the result will be negative</b>
     *
     */
    public final int getDistanceFromIter( ByteIterator p_iter )
    {
        assert( physicalStorage == p_iter.physicalStorage ) : getAssertionMsg( "attempting to compare iterators based on different arrays" );
        return index - p_iter.index;
    }
    
    
    /**
     * @return a copy of this ByteIterator's controlled subarray (the iteratable region)
     */
    public final byte[] toArray()
    {
        int    length = ( highBound - lowBound ) + 1; // +1 is for index/length transform
        byte[] result = new byte[ length ];
        
        System.arraycopy( physicalStorage, lowBound, result, 0, length );
        
        return result;
    }

    
    /** returns an integer representation of the next p_length bytes
     * 
     * @param p_length The number of characters to convert
     * @return the integer representation of the next p_length bytes starting at 
     * the current position
     */
    public final int readAsciiInt( int p_length )
    {
        assert ( p_length <= remaining() ) : getAssertionMsg( "Length would exceed iteration region" );
        
        int start = index;  // mark current position
        
        index += p_length;
        
        return ByteArrayUtils.asciiToInt( physicalStorage, start, p_length );
    }
    
    
    /** sets the current element to be equal to p_new
     *
     * NOTE: does NOT advance the iterator
     *
     * @param p_new the new value to set the current element to
     */
    public final void set( byte p_new )
    {
        physicalStorage[ index ] = p_new;
    }
    
    
    /**
     * @return the number of bytes between the current index and the last reachable byte (inclusive)
     */
    public final int remaining()
    {
        return ( highBound - index ) + 1;  // +1 is because the current index is also remaining
    }
    
    
    /**
     * @return the number of bytes available for iteration- before and after the current position
     */
    public final int length()
    {
        return ( highBound - lowBound ) + 1;  // +1 is because both lowBound and highBound are included
    }
    
    
    /**
     * @return true if the iterator indexes a valid byte (one controlled by and
     * reachable through this iterator), false otherwise
     */
    public final boolean isValid() {
        return (   index >= lowBound
                && index <= highBound );
    }
    
    
    /** @return the byte at the current index
     *
     */
    public final byte get()
    {
        return physicalStorage[ index ];
    }
    

    ////////////////////////////////////////////////////////////////////////////
    // read (accessor) methods for the bytes controlled by this iterator
    
    
    /**
     * reads a byte of data from the iterator at the current index.  Then moves iterator
     * to the next index
     *
     * NOTE: this byteIterator's current position will be left on the byte AFTER the
     *       byte read
     * 
     * @return the byte at the current index when the call was made
     */
    public final byte read()
    {
        return physicalStorage[ index++ ];
    }
    
    
    /**
     * Reads data from the iterator's current position for length p_length bytes into p_out.
     *
     * NOTE: this byteIterator's current position will be left on the byte AFTER the
     *       last written character
     *
     * @param p_out    The ByteWriter to write to
     * @param p_length How many bytes to write
     * @throws IOException if the ByteWriter throws it
     */
    public final void read( ByteWriter p_out, int p_length )
        throws IOException
    {
        p_out.write( physicalStorage, index, p_length );
        index += p_length;
    }
    
    
    /** Reads p_length bytes from the current index into p_dest at the specified offset
     * The current index is moved to the end of the copy region
     *
     * NOTE: this byteIterator's current position will be left on the byte AFTER the
     *       last copied character
     *
     * @param p_dest the destination of the copy operation
     * @param p_destOffset the offset within p_dest to receive the first copied byte
     * @param p_length how many bytes to copy
     */
    public final void read( byte[] p_dest, int p_destOffset, int p_length )
    {
        assert( remaining() >= p_length )
                : getAssertionMsg( "source length (" + remaining()
                                   + ") is shorter than copy length (" + p_length + ")" );
        
        assert( p_dest.length - p_destOffset >= p_length )
                : getAssertionMsg( "Destination remaining length (" + ( p_dest.length - p_destOffset )
                                   + ") is shorter than copy length (" + p_length + ")" );
        
        System.arraycopy( physicalStorage,
                          index,
                          p_dest,
                          p_destOffset,
                          p_length );
        
        index += p_length;
    }
    
    
    /** Reads p_length bytes from the current index into p_dest at the current index
     * The current index is moved to the end of the copy region for both ByteIterators
     *
     * NOTE: this byteIterator's current position will be left on the byte AFTER the
     *       last copied character
     *
     * @param p_dest the destination of the copy operation (copy will begin at the current index)
     * @param p_length how many bytes to copy
     */
    public final void read( ByteIterator p_dest, int p_length )
    {
        assert( remaining() >= p_length )
                : getAssertionMsg( "source length (" + remaining()
                                   + ") is shorter than copy length (" + p_length + ")" );
        
        assert( p_dest.remaining() >= p_length )
                : getAssertionMsg( "Destination remaining length (" + p_dest.remaining()
                                   + ") is shorter than copy length (" + p_length + ")" );
        
        if ( p_length <= 0 ) 
        {
            return;
        }
        
        System.arraycopy( physicalStorage,
                          index,
                          p_dest.physicalStorage,
                          p_dest.index,
                          p_length );
        
        index += p_length;        
        p_dest.index += p_length; 
    }
    
    
    /** Reads p_length bytes from the current index into p_dest at p_destOffset index
     * The current index is moved to the end of the copy region in this ByteIterator
     *
     * NOTE: this byteIterator's current position will be left on the byte AFTER the
     *       last copied character
     *
     * @param p_dest the destination of the copy operation (copy will begin at the current index)
     * @param p_destOffset the index in p_dest to take the first copied byte
     * @param p_length how many bytes to copy
     */
    public final void read( ByteVector p_dest, 
                            int        p_destOffset, 
                            int        p_length )
    {
        assert( remaining() >= p_length )
                : getAssertionMsg( "source length (" + remaining()
                                   + ") is shorter than copy length (" + p_length + ")" );

        if ( p_length <= 0 ) 
        {
            return;
        }
        
        p_dest.set( physicalStorage, 
                    index, 
                    p_destOffset,
                    p_length );
        
        index += p_length;        
    }
    
    
    /** Reads p_length bytes from the current index into p_dest at the first index
     * The current index is moved to the end of the copy region in this ByteIterator
     *
     * NOTE: this byteIterator's current position will be left on the byte AFTER the
     *       last copied character
     *
     * @param p_dest the destination of the copy operation (copy will begin at the current index)
     * @param p_length how many bytes to copy
     */
    public final void read( ByteVector p_dest, 
                            int        p_length )
    {
        read( p_dest, 0, p_length );
    }
     
    
    // read (accessor) methods for the bytes controlled by this iterator
    ////////////////////////////////////////////////////////////////////////////
    // write (modifier) methods for the bytes controlled by this iterator

    
    /* writes p_src into the current position and moves the iterator
     * to the next byte
     * 
     * @param p_src byte to set
     */
    public void write( byte p_src )
    {
        physicalStorage[ index++ ] = p_src;
    }

    
    /** Writes bytes from p_src into this object beginning at the current index
     * The current index is placed after the last byte written 
     *
     * NOTE: this byteIterator's current position will be left on the byte AFTER the
     *       last written byte
     *
     * @param p_src source of the written bytes
     * @return the number of bytes written
     */
    public final int write( ByteReader p_in, int p_length )
        throws IOException
    {
        assert ( remaining() >= p_length ) : "Bad write length of " + p_length + " with only space enough for " + remaining();

        int writeLength = p_in.read( physicalStorage, index, p_length, p_length );
        index += writeLength;
        return writeLength;
    }

    
    /** Writes p_length bytes from p_src into this object beginning at the current index
     * The current index is placed after the last copied byte after the operation
     *
     * NOTE: this byteIterator's current position will be left on the byte AFTER the
     *       last copied character
     *
     * @param p_src source of the copied bytes
     * @param p_srcOffset offset within p_src of the first byte to copy
     * @param p_length the copy length
     */
    public final void write( byte[] p_src, int p_srcOffset, int p_length )
    {
        assert( remaining() >= p_length ) : getAssertionMsg( "Destination length is shorter than copy length " );
        assert( p_src.length - p_srcOffset >= p_length ) : getAssertionMsg( "Source remaining length is shorter than copy length" );
        
        System.arraycopy( p_src,
                          p_srcOffset,
                          physicalStorage,
                          index,
                          p_length );
        
        index += p_length;
    }

    
    /** Writes p_length bytes from the p_srcOffset index in p_src into this ByteIterator's storage
     * beginning at the current index.  The current index is placed after the last 
     * copied byte after the operation
     *
     * NOTE: this byteIterator's current position will be left on the byte AFTER the
     *       last copied character
     *
     * @param p_src source of the copied bytes
     * @param p_srcOffset offset within p_src of the first byte to copy
     * @param p_length the copy length
     */
    public final void write( ByteVector p_src, int p_srcOffset, int p_length )
    {
        assert( remaining() >= p_length ) : getAssertionMsg( "Destination length is shorter than copy length " );
        assert( p_src.length() - p_srcOffset >= p_length ) : getAssertionMsg( "Source remaining length is shorter than copy length" );
        
        p_src.get( p_srcOffset, physicalStorage, index, p_length );
        
        index += p_length;
    }

    
    /** Writes the entire p_src ByteVector into this ByteIterator's storage
     * beginning at the current index.  The current index is placed after the last 
     * copied byte after the operation
     *
     * NOTE: this byteIterator's current position will be left on the byte AFTER the
     *       last copied character
     *
     * @param p_src source of the copied bytes
     */
    public final void write( ByteVector p_src )
    {
        int copyLength = p_src.length();
        
        assert( remaining() >= copyLength ) : getAssertionMsg( "Destination length is shorter than copy length " );
        
        p_src.get( 0, physicalStorage, index, copyLength );
        
        index += copyLength;
    }

    
    // write (modifier) methods for the bytes controlled by this iterator
    ////////////////////////////////////////////////////////////////////////////
    // fill methods
    
    /** copies p_fill into all bytes in the byteIterator between index and highBound (inclusive)
     *
     * NOTE: this byteIterator's current position will be left on the byte AFTER the
     *       last copied character
     *
     * @param p_fill the byte to fill with
     */
    public final void fill( byte p_fill )
    {
        fill( p_fill, remaining() );
    }
    
    
    /** copies p_fill into all bytes in the byteIterator between index and highBound (inclusive)
     * after the operation, the current index will be after the last filled byte
     *
     * NOTE: this byteIterator's current position will be left on the byte AFTER the
     *       last copied character
     *
     * @param p_fill the byte to fill with
     * @param p_fillLength the number of fill characters to insert
     */
    public final void fill( byte p_fill, int p_fillLength )
    {
        assert( remaining() >= p_fillLength )
                : getAssertionMsg( "source length (" + remaining()
                                   + ") is shorter than copy length (" + p_fillLength + ")" );
        
        for ( int i = 0; i < p_fillLength; ++i )
        {
            physicalStorage[ index++ ] = p_fill;
        }
    }
    
    
    // fill methods
    ////////////////////////////////////////////////////////////////////////////
    // positioning methods
    
    
    /**
     * moves the iterator to the specified logical element (the element p_logicalIndex away from lowBound)
     * @param p_logicalIndex the index to move the iterator to
     *
     * @return this object to allow command chaining
     */
    public final ByteIterator setIndex( int p_logicalIndex )
    {
        index = p_logicalIndex + lowBound;
        
        return this;
    }

    
    /**
     * moves the iterator to the first element
     *
     * @return this object to allow command chaining
     */
    public final ByteIterator first()
    {
        index = lowBound;
        
        return this;
    }
    
    
    /** moves the iterator to the last byte referencable by this iterator
     *
     * @return this object to allow command chaining
     */
    public final ByteIterator last()
    {
        index = highBound;
        
        return this;
    }
    
    
    /**
     * moves the iterator to the element past the last element (accessing this element
     * will cause a bounds assertion)
     *
     * @return this object to allow command chaining
     */
    public final ByteIterator end()
    {
        index = highBound + 1;
        
        return this;
    }
    
    
    /** moves the iterator forward one byte
     *
     * @return this object to allow command chaining
     */
    public final ByteIterator next()
    {
        ++index;
        
        return this;
    }
    
    
    /** moves the iterator forward the specified number of bytes
     *
     * @return this object to allow command chaining
     */
    public final ByteIterator next( int p_offset )
    {
        index += p_offset;
        
        return this;
    }
    
    
    /** moves the iterator backward one byte
     *
     * @return this object to allow command chaining
     */
    public final ByteIterator prev()
    {
        --index;
        
        return this;
    }
    
    
    /** moves the iterator forward the specified number of spaces
     * @param p_offset the number of elements to move forward
     *
     * @return this object to allow command chaining
     */
    public final ByteIterator prev( int p_offset )
    {
        index -= p_offset;
        
        return this;
    }
    
    
    // positioning methods
    ////////////////////////////////////////////////////////////////////////////
    // methods for information gathering/parsing
        
    
    /**
     * @return the position within the logical array of the current index
     * (the distance between the lowBound and the current index)
     */
    public final int getIndex()
    {
        return index - lowBound;
    }

    
    /** @param p_test iterator to test for equivalence
     * @return true if this iterator refers to the same byte in the same array as p_test
     */
    public final boolean equals( ByteIterator p_test )
    {
        return p_test.physicalStorage == physicalStorage && p_test.index == index;
    }
    
    
    /** returns true iff the next bytes are equal to the entirre p_token
     *
     * SIDE EFFECT: if the method returns true (success) the current position will
     * be set to the byte AFTER the last compared byte
     *
     * @param p_token the bytes to look for
     * @return true if the test bytes are found, false otherwise
     */
    public final boolean nextTokenIs( byte[] p_token )
    {
        return nextTokenIs( p_token, 0, p_token.length );
    }
    
    
    /** returns true iff the next bytes are equal to p_token from offset
     * p_offset to p_offset + p_tokenLength.
     *
     * SIDE EFFECT: if the method returns true (success) the current position will
     * be set to the byte AFTER the last compared byte
     *
     * @param p_token the bytes to look for
     * @param p_tokenOffset the offset within p_token to start at
     * @param p_tokenLength the number of bytes to look for
     * @return true if the test bytes are found, false otherwise
     */
    public final boolean nextTokenIs( byte[] p_token, int p_tokenOffset, int p_tokenLength )
    {
        assert ( p_tokenLength > 0 ) : getAssertionMsg( "invalid length ( < 0 )" );
        
        if ( p_tokenLength > remaining() )
        {
            return false;
        }
        
        int i   = index;
        int end = i + p_tokenLength;
        
        for ( int j = p_tokenOffset; i < end; ++i, ++j )
        {
            if ( physicalStorage[ i ] != p_token[ j ] )
            {
                return false;
            }
        }
        
        // change current position to _after_ the last evaluated position
        index = i;
        
        return true;
    }
    
    
    /** returns the offset of p_target, if it exists in the next p_length bytes
     * otherwise returns -1
     *
     * @return offset of p_target if found, -1 otherwise
     * @param p_target byte to look for
     * @param p_length the number of bytes to search before failure NOTE: this must be <= remainingBytes
     */
    public final int offsetOf( byte p_target, int p_length )
    {
        int stopBeforeIndex = index + p_length;
        
        assert ( p_length <= remaining() ) : getAssertionMsg( "illegal length" );
        
        for ( int i = index; i < stopBeforeIndex; ++i )
        {
            if ( physicalStorage[ i ] == p_target )
            {
                return i - index;
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
    public final int offsetOf( byte p_target )
    {
        return offsetOf( p_target, remaining() );
    }
    
    
    // methods for information gathering/parsing
    ////////////////////////////////////////////////////////////////////////////
    //  methods to display the ByteIterator's state

    private final String getAssertionMsg( String p_msg )
    {
        StringBuffer sb = new StringBuffer( p_msg );
        sb.append( "\n--------------\n" );
        
        sb.append( "Low = " )
          .append( lowBound )
          .append( ", High = " )
          .append( highBound )
          .append( ", Current = " )
          .append( index )
          .append( '\n' )
          .append( "\n--------------\n" )
          .append( toString() );
         
        return sb.toString();
    }
    
    
    /**
     * @return string with a detailed (verbose) description of the current state
     * of this iterator
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
     * of this iterator between the specified indices
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
                 
        p_sb.append( "\n-----------------------\nByteIterator\n-----------------------\n" )
            .append( "\nLow = " )
            .append( lowBound )
            .append( ", High = " )
            .append( highBound )
            .append( ", Current = " )
            .append( index )
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
                
            if ( i == index )
            {
                p_sb.append( " = Current" );
            }
            
            p_sb.append( "\n" ); 
        }
    
        p_sb.append( "-----------------------\n" );
    }
  
    
    /**
     * @return string with a detailed (verbose) description of the current state
     * of this iterator
     */
    public final String toString()
    {
        return toString( lowBound, highBound ); 
    }
    
    /**
     * @param p_start The first index to display
     * @param p_end the Last index to display
     * @return string with a detailed (verbose) description of the current state
     * of this iterator between the specified indices
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
    
    
    /** @see com.cboe.lwt.byteUtil.ByteWriter#flush()
     * 
     *  no-op for this class
     */
    public void flush() 
    {
        // no op (here to satisfy ByteWriter interface)
    }
};

