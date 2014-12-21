package com.cboe.application.shared;

import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class CollectPerformanceMetrics
{
	
	public static final String[] SESSION_IDENTIFIER_LIST = new String[] {
		"*unknown*",
		"ONE_MAIN",
		"W_AM1",
		"W_MAIN",
		"CFE_MAIN",
		"W_STOCK",
        "COF_MAIN",
        "C2_MAIN"
	};

	/**
	* Generates a long hash value for use as an identifier for
	* an order ID for metrics taking.
	* <P>
	* The mapping of this value is as follows (from most
	* significant to least significant):
	* <UL>
	* <LI>0-20 - 21 bits - Uniquely identifying hash,
	*     used as filler.
	* <LI>21-35 - 15 bits - Branch id, 3 characters
	*     alpha compressed into 15 bits.
	* <LI>36-49 - 14 bits - Sequence number - 4 digits.
	* <LI>50-59 - 10 bits - Firm number - 3 digits.
	* <LI>60-63 - 4 bits - Session identifier -
	*     used as a lookup into the static array
	*     {@link #SESSION_IDENTIFIER_LIST SESSION_IDENTIFIER_LIST}
	*     or set to 0 if not found in this list.
	* </UL>
	* @param orderId The orderId struct for which to generate a hash value.
	* @param session The session name to encode in the metric.
	* @return A long hash value mapped as stated above.
	*
	*/
    public static long generateOrderMetricId(OrderIdStruct orderId, String session) {
	long hash = 0l;  
		// 'branch' is 3 characters alpha, 15 bits
		String hold = orderId.branch.toUpperCase();
		if (hold.length() > 3) {
			hold = hold.substring(0, 3);
		}
		long branch = 0L;
		try {
		branch = encodeAlpha(hold);
		} catch (IllegalArgumentException e) {
			// ignore, leave 0
		}

	// seq# is 4 digits, 14 bits
	long branchSequenceNumber = (long)orderId.branchSequenceNumber;

		// firm# is 3 digits, 10 bits
	long firmNumber = 0L;
	try {
			firmNumber = Long.parseLong(orderId.executingOrGiveUpFirm.firmNumber);
		} catch (NumberFormatException e) {
			// Ignore, leave 0
		}
	// session key, 4 bits
	long sessionKey = 0L;
	if (session != null) {
		// skip the *unknown* entry
		for (int x = 1 ; x < SESSION_IDENTIFIER_LIST.length ; x++) {
			if (session.equals(SESSION_IDENTIFIER_LIST[x])) {
				sessionKey = (long)x;
				break;
			}
		}
	    } // endif

	    // A string from which we'll create filler hash bits.
		String filler =
				orderId.executingOrGiveUpFirm.exchange +
				orderId.correspondentFirm +
				orderId.orderDate;

        long fillerValue = (long)filler.hashCode();


	hash = setBitsInLong(hash, 0, fillerValue, 21);
	hash = setBitsInLong(hash, 21, branch, 15);
	hash = setBitsInLong(hash, 36, branchSequenceNumber, 14);
	hash = setBitsInLong(hash, 50, firmNumber, 10);
	hash = setBitsInLong(hash, 60, sessionKey, 4);
	return hash;
    }


// Quote metrics methods - MH 05/2003
	 /**
	 * Generates a quote ID for metrics recording.
	 * 0-48  - 49 bits - First 32 for product, rest is not used
	 * 49-52 - 4 bits - Session identifier
	 * 53-63 - 11 bits - Number of quotes in block
	 *
	 */
	public static long generateQuoteMetricId(long productClass, String session, long quoteBlock)
	{
	     long populateLong = 0l;
	     long fillerVal = 0l;

	     // productKey# is 4 digits, 32 bits
	     // when call is for multiple quotes class will be passed
	     long productOrCLassKey = productClass;

	     // session key, 4 bits
	     long sessionKey = 0L;
	     if (session != null) {
		     // skip the *unknown* entry
		     for (int x = 1 ; x < SESSION_IDENTIFIER_LIST.length ; x++) {
			     if (session.equals(SESSION_IDENTIFIER_LIST[x])) {
				     sessionKey = (long)x;
				     break;
			     }
		     }
	     } // endif

	     populateLong = setBitsInLong(populateLong, 0, fillerVal, 17);
	     populateLong = setBitsInLong(populateLong, 17, productOrCLassKey, 32);
	     populateLong = setBitsInLong(populateLong, 49, sessionKey, 4);
	     populateLong = setBitsInLong(populateLong, 53, quoteBlock, 11);

	 return populateLong;
     }

     /**
     * Generates a ID for trade metrics recording.
     * 0-31  - 32 bits - First 32 for product,
     * 32- 35 - 4 bits - Session identifier
     * 36-63 - 28 bits - Number of traded quantity
     *
     */
   


	/**
	* Sets bits in a long to a specified value.
	* @param target The long value to set bits into.
	* @param destinationIndex The index w/in the target
	* long to begin setting bits (must be between 0 and 63);
	* <b>the index is numberd from the most significant bit
	* of the target to the least significant bit</b>.
	* @param bits The source for the bits to set into the
	* target value; the desired will be the <b>least
	* significant bits of this value</b>.
	* @param numBits The number of bits that will be set
	* from the 'bits' parameter into the target.
	* @return The updated long value.
	* @exception IllegalArgumentException If the combination of
	* parameters addresses values outside the boundries of the
	* target long.
	*
	*/
	private static long setBitsInLong(long target, int destinationIndex,
					  long bits, int numBits) {

		// First, some checks
		if ((destinationIndex < 0) || (destinationIndex > 63) ||
		    (numBits < 0) ||
		    (numBits + destinationIndex > 64)) {
			throw new IllegalArgumentException("Invalid parameters given");
		}

        long mask = (1L << numBits) - 1L;

		int shiftValue = 64 - numBits - destinationIndex;

		// Zero out the section of the target we're overwriting
		target &= ~(mask << shiftValue);

		// Mask the 'bits' value to make sure we're only
		// getting the sample we want.
		bits &= mask;

		// Set the bits
		target |= (bits << shiftValue);

		return target;
	}

	/**
	* Encodes a String composed all of uppercase alphabetic
	* characters into a long.
	* Encoding is optimized for all-caps only letters.
	* Decoding is done with {@link #decodeAlpha decodeAlpha()}
	* @param str The string to encode; maximum length is 13
	* characters.
	* @return The long value.
	* @exception IllegalArgumentException if str is too long
	* or contains a non-uppercase-letter character.
	*
	*/
	private static long encodeAlpha(String str) {
		// Checks
		if (str.length() > 13) {
			throw new IllegalArgumentException("String too long:" + str);
		}
		long ret = 0L;
	if (str.length() == 0 ){
	    return ret;
	}


		// A hack to get around the problem of a leading 'A'
		// (this would equate to a leading zero and would be dropped)
		// We'll promote a leading 'A' to a 'B' and make the return value
		// negative...
		boolean leadingA = false;
		if (str.charAt(0) == 'A') {
			str = "B" + str.substring(1);
			leadingA = true;
		}

		// Consider the string a base 26 number and compute value
		int len = str.length();
		char c = (char)0;
		for (int x = 0 ; x < len ; x++) {
			c = str.charAt(len - x - 1);
			if ((c < 'A') || (c > 'Z')) {
				throw new IllegalArgumentException("Invalid character '" + c + "' in string:" + str);
			}

			ret += (long)(c - 'A') * pow(26, x);
		}

		if (leadingA) {
			ret = -ret;
		}

		return ret;
	}


	/**
	* A power function for ints.
	* Only computes positive exponents.
	* @param base The base of the power function.
	* @param exponent The exponent.
	* @return base<super>exponent</super>.
	* @exception java.lang.IllegalArgumentException if
	* a negative exponent is supplied.
	* @see java.lang.Math#pow
	*
	*/
	public static long pow(int base, int exponent) {
		if (exponent < 0) {
			throw new IllegalArgumentException("Negative exponent");
		}

		if (exponent == 0) {
			return 1l;
		}

		long value = base;
		for (int x = 1 ; x < exponent ;  x++) {
			value *= base;
		}
		return value;
	} // end pow()

	
}
