package com.cboe.domain.util;

/**
 * @author Steven Sinclair
 */
public interface CustomInputStreamConstants
{
	/**
	 * When parsing variable-length data (ex, String), define the policy to be
	 * "read byte until a '\0' character is read".  Return up to but not including the
	 * '\0' character.
	 */
	public static final int NULL_TERM = 0x01;
	
	/**
	 * When parsing variable-length data (ex, String), define the policy to be
	 * "read exactly the given number of bytes".
	 */
	public static final int FIXED_LENGTH = 0x02;
	
	/**
	 * An 'or'ing or NULL_TERM and FIXED_LENGTH: read until a null byte is encountered
	 * or FIXED_LENGTH bytes have been read, whichever comes first.
	 */
	public static final int FIXED_LENGTH_OR_NULL = FIXED_LENGTH | NULL_TERM;
	
	/**
	 * When parsing variable-length data (ex, String), define the policy to be
	 * "read the next n characters, assume they represent a numeric string, 
	 * parse the value, and use that value as the string length (read the next n chars).
	 */
	public static final int TEXTUALLY_ENCODED_LENGTH = 0x04;
	
	/**
	 * When parsing variable-length data (ex, String), define the policy to be
	 * "read the next two bytes as a NBO-encoded short.  Use that value to read the
	 * next n bytes."
	 */
	public static final int SHORT_ENCODED_LENGTH = 0x08;
	
	/**
	 * When parsing variable-length data (ex, String), define the policy to be
	 * "read the next byte.  Its unsigned value is the length of the string (read the
	 * next n bytes).
	 */
	public static final int BYTE_ENCODED_LENGTH = 0x10;

	/**
	 *  If null is provided as the date format string in the parseDate() method,
	 *  then this format will be used ("MMddyyyy").
	 */
	public static final String DEFAULT_DATE_FORMAT = "MMddyyyy";
}
