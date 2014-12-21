package com.cboe.infrastructureServices.instrumentationService;


/* This class provides utilities for performing bit manipulations 
	associated with the 64-bit entity ID. 

	This ID is a 64-bit integer and it is meant to be used as a 64-bit
	integer. This class will provide utilities to set or get individual
	fields of the bitmapped structure, but it will not return a class.
	I want to avoid the possibility of routinely allocating a class, due
	to the overhead of memory allocation and garbage collection.
*/

/*
  bits are 0 ... 63 with the low bit being 0
  62-63 = Source Type {00 = CAS, 01 = FIX, 10 = Other1, 11 = Other2 }
  48 - 61 = Source number, e.g. for cas0145 this is 145
  16 - 47 = Transaction identifier --- unique number provided by Client
	0 - 15 = Element identifier (e.g. individual quote in a quote block) provided by Server
*/

public class EntityID
{
	public static final short TT_CAS = 0;
	public static final short TT_FIX = 1;
	public static final short TT_EVENT = 2;
	public final static short TT_OTHER2 = 3;
	private static final int SOURCE_MAX = TT_EVENT;

	// transactionNumber is an unsigned 32-bit integer. Since Java only has
	// signed values, I am making it a long. However, values greater
	// than or equal to 2^32 will be rejected.
        public static long createID( short sourceType, short sourceNumber, long transactionNumber )
        {
		if ( sourceType < 0 || SOURCE_MAX < sourceType )
		{
			 throw new IllegalArgumentException( "Illegal transaction source type: " + sourceType );
		}
		if ( sourceNumber < 0 || NUMBER_MAX < sourceNumber )
		{
			 throw new IllegalArgumentException( "Illegal transaction source number: " + sourceNumber );
		}
		if ( transactionNumber < 0 || TRANS_MAX < transactionNumber )
		{
			 throw new IllegalArgumentException( "Illegal transaction number: " + transactionNumber );
		}
		// Note that since we have validated all the numbers, it is not necessary at this point to 
		// mask the numbers
		return ( ( (long) sourceType ) << SOURCE_SHIFT ) |
				 ( ( (long) sourceNumber ) << NUMBER_SHIFT ) |
				 ( transactionNumber << TRANS_SHIFT );
	}


    
       public static long createID( short sourceType, long transactionNumber )
       {
		short sourceNumber = 9999;
		return createID(sourceType, sourceNumber, transactionNumber);
	}




	// elementIdentifier is an unsigned 16-bit integer. Since Java only has
	// signed values, I am making it an int. However, values greater than
	// or equal to 2^16 will be rejected
	public static long setElementID( long entityID, int elementIdentifier )
	{
		if ( elementIdentifier < 0 || ELEMENT_MAX < elementIdentifier )
		{
			 throw new IllegalArgumentException( "Illegal element identifier: " + elementIdentifier );
		}
		// Note that since we have validated the elementidentifer, it isn't necessary to mask it.
		// Also, since the elementIdentifier occupies the lowest bits,  no shift is necessary.
        // Since this routine could be called extremely frequently, I am
		// leaving out the shift. If things change later on, it will have to be added back in.
		// Compiler optimization might take out the shift, but I would rather not count on it.
		return entityID & (~ ( (long) ( ELEMENT_MASK) ) ) | ( (long) elementIdentifier );
	}
	public static short getSourceType( long entityID )
	{
		// Since the sourceType occupies the high bits, the mask should not be necessary.
		// However, since this method should not get called frequently, I am including
		// the mask in case something changes in the future.
		return (short) ( ( entityID >>> SOURCE_SHIFT ) & SOURCE_MASK );
	}


	public static int getSourceNumber( long entityID )
	{
		return (int) ( ( entityID >>> NUMBER_SHIFT ) & NUMBER_MASK );
	}
	public static long getTransactionNumber( long entityID )
	{
		return ( entityID >>> TRANS_SHIFT ) & TRANS_MASK;
	}
	public static int getElementID( long entityID )
	{
		// Since elementID occupies the low bits, the shift should not be necessary.
		// However, since this method should not get called fequently, I am including
		// the shift in case something changes in the future.
		return (int) ( ( entityID >>> ELEMENT_SHIFT ) & ELEMENT_MASK );
	}

	/* NOTE: SOURCE_MAX is defined at top of tile with the type constants */
	private static final int SOURCE_MASK = 0x03;
	private static final int SOURCE_SHIFT = 62;
	private static final int NUMBER_MAX = 16 * 1024 - 1;
	private static final int NUMBER_MASK = 0x3FFF;
	private static final int NUMBER_SHIFT = 48;
	private static final long TRANS_MAX = 4L * 10204 * 1024 * 1024 - 1;
	private static final long TRANS_MASK = 0xFFFFFFFFL;
	private static final int TRANS_SHIFT = 16;
	private static final int ELEMENT_MAX = 64 * 1024 * 1024 - 1;
	private static final int ELEMENT_MASK = 0xFFFF;
	private static final int ELEMENT_SHIFT = 0;

    
    private EntityID()
    {
       // We don't wan't to instantiate this
    }
    private static void testSettings( short casType, short casNumber, long tid )
    {
		 String casTypeName;
		 switch ( casType )
		 {
			 case TT_CAS: casTypeName = "CAS"; break;
			 case TT_FIX: casTypeName = "FIX"; break;
			 default:     casTypeName = "Unknown"; break;
		 }
       System.out.println( "Creating ID for " + casTypeName +
          " " +  casNumber + " with transaction " + tid );
       long entityID = createID( casType, casNumber, tid );
       System.out.println( "Initial ID = " + entityID );
       System.out.println( " CAS Type = " + getSourceType( entityID ) );
       System.out.println( " CAS Number = " + getSourceNumber( entityID ) );
       System.out.println( " Transaction Number = " + getTransactionNumber( entityID ) );
       System.out.println( " Element Number = " + getElementID( entityID ) );
       System.out.println( "Setting element number 25" );
       entityID = setElementID( entityID, 25 );
       System.out.println( "ID with element = " + entityID );
       System.out.println( " CAS Type = " + getSourceType( entityID ) );
       System.out.println( " CAS Number = " + getSourceNumber( entityID ) );
       System.out.println( " Transaction Number = " + getTransactionNumber( entityID ) );
       System.out.println( " Element Number = " + getElementID( entityID ) );
       System.out.println( "Setting element number 40" );
       entityID = setElementID( entityID, 40 );
       System.out.println( "ID with element = " + entityID );
       System.out.println( " CAS Type = " + getSourceType( entityID ) );
       System.out.println( " CAS Number = " + getSourceNumber( entityID ) );
       System.out.println( " Transaction Number = " + getTransactionNumber( entityID ) );
       System.out.println( " Element Number = " + getElementID( entityID ) );
		 System.out.println( "==============================================" );
    }
    public static void main( String args[] )
    {
       testSettings( TT_CAS, (short)225, 123456L );
       testSettings( TT_FIX, (short)420, 757L );
    }
}
