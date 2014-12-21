package com.cboe.domain.util;

import java.io.*;
import java.text.*;
import java.util.Date;
import junit.framework.*;
import com.cboe.util.*;
import com.cboe.interfaces.domain.Price;

/**
 *  This class extends DataInputStream to provide better generic support
 *  for parsing data packets, where ASCII integer representation may run
 *  into each other (ex, two 3-byte fields representing 123 and 456 may
 *  be presented as "123456" which will not be parsed properly by 
 *  Integer.parseInt()), or where the length of a string is encoded as a
 *  prefix to the string.
 *  <p>
 *  <b>Note:</b> this class is not threadsafe.  Only one thread should access
 * 		a given instance of this class unless such access is synchronized (ie, treat
 *		instances of this class as shared resources).
 */
public class CustomInputStream extends java.io.DataInputStream implements CustomInputStreamConstants
{

	/**
	 * Formatting object for parsing dates.
	 */
	private SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);


	private ByteArrayPool bytePool = new ByteArrayPool();
	
	/**
	 * Inner class for support of automated unit testing.
	 */
	static public class Test extends TestCase
	{
		private ReplaceableByteArrayInputStream bytesIn = new ReplaceableByteArrayInputStream(new byte[0]);
		private CustomInputStream customIn = new CustomInputStream(bytesIn);
		
		public static TestSuite suite()
		{
			TestSuite result = new TestSuite();
			result.addTest(new Test("testReadDate"));
			result.addTest(new Test("testReadIntAsString"));
			result.addTest(new Test("testReadLong"));
			result.addTest(new Test("testReadString"));
			result.addTest(new Test("testReadEighths"));
			result.addTest(new Test("testReadDecimal"));

			// Price reading is broken due to errors in the com....util.Price code.
			//
			result.addTest(new Test("testReadShortPrice"));
			result.addTest(new Test("testReadLongPrice"));
			return result;
		}
		
		/**
		 * Build & return a bytes array by cat'ing together the strings in the given array.
		 * The offsets array should have a length >= strings, and is treated as an "out"
		 * parameter: values will written to it's elements (ie, the offset of the corresponding
		 * strings).
		 */
		private static byte[] getBytes(String[] strings, int[] offsets)
		{
			String byteStr = ""; // should be about right
			for (int i=0; i < strings.length; ++i)
			{
				if (offsets != null)
					offsets[i] = byteStr.length();
				byteStr += strings[i];
			}
			return byteStr.getBytes();
		}

		public void testReadDate()
		{
			String[]  dates  = { "02021999XXX", "12122001", "01/4/98 ", "notADate",  "99/99/9999 ", "02.04.98" };
			String[]  format = { "MMddyyyy",    "MMddyyyy", "MM/dd/yy", "MMddyy",    "MM/dd/yyyy",  "xM.dx.yx" };
			boolean[] valid  = { true,          true,       true,       false,       true,         false };
			SimpleDateFormat myDateFormat = new SimpleDateFormat();

			int[] offsets = new int[dates.length];
			byte[] bytes = getBytes(dates, offsets);
			bytesIn.setBytes(bytes, 0, bytes.length);
			
			for (int i=0; i < dates.length; ++i)
			{
				String errorMsg = "Date parsing: string \"" + dates[i] + "\" using format \"" + format[i] + "\" is expected to " + (valid[i]?"pass":"fail") + " ";
				boolean pass = true;

				try
				{
					myDateFormat.applyPattern(format[i]);
					Date expect = myDateFormat.parse(dates[i]);
					customIn.moveToOffset(offsets[i]);
					Date date = customIn.readDate(format[i]);

					if (valid[i])
						assertTrue(errorMsg + "Parsed date " + date + " wasn't the expected date", expect.equals(date));
					else
						assertTrue(errorMsg + "Parsed date " + date + " was the expected date", !expect.equals(date));
				}
				catch (ParseException ex)
				{
					errorMsg += ex.getMessage();
					pass = false;
				}
				catch (IOException ex)
				{
					errorMsg += ex.getMessage();
					pass = false;
					assertTrue(errorMsg + ex, false);
				}

				assertTrue(errorMsg, (pass && valid[i]) || (!pass && !valid[i]));
			}
		}

		public void testReadIntAsString()
		{
			String[]  integers = { "-1", "00", "199", " 123", "-123", "500000000", "1234567", "-123crap", "crap" };
			int[]     lengths  = { 2,    2,    1,      4,     4,      9,           5,          8,         4  };
			int[]     expect   = { -1,   0,    1,      123,   -123,   500000000,   12345,     -1,         -1 };
			boolean[] valid    = { true, true, true, true,   true,    true,        true,      false,      false};

			int[] offsets = new int[integers.length];
			byte[] bytes = getBytes(integers, offsets);
			bytesIn.setBytes(bytes, 0, bytes.length);

			for (int i=0; i < integers.length; ++i)
			{
				String errorMsg = "Int parsing: string \"" + integers[i] + "\" value " + expect[i] + " is expected to " + (valid[i]?"pass":"fail") + " ";
				try
				{
					customIn.moveToOffset(offsets[i]);
					int x = customIn.readIntAsString(lengths[i]);

					if (valid[i])
						assertTrue(errorMsg + "Parsed int " + x + " wasn't the expected int", x == expect[i]);
					else
						assertTrue(errorMsg + "Parsed int " + x + " was the expected int",    x != expect[i]);
				}
				catch (ParseException ex)
				{
					assertTrue(errorMsg + ex, !valid[i]);
					
				}
				catch (IOException ex)
				{
					errorMsg += ex.getMessage();
					assertTrue(errorMsg, false);
				}
			}
		}

		public void testReadLong()
		{
			// If length is -1 then all length 1, 2, 4, 6, and 8 are attempted.
			//
			long[]    longs   = { 1,   0,    -1,    123,  -123, 50000000000L, -5000000000L, 12345678901L, 123,   1234  };
			int[]     lengths = { -1, -1,    -1,    -1,   -1,   8,            8,            6,            -1,    -1    };
			long[]    expect  = { 1,   0,    -1,    123,  -123, 50000000000L, -5000000000L, 12345678901L, 345,   -1234 };
			boolean[] valid   = { true, true, true, true, true, true,         true,         true,         false, false };
			int[]     offsets = new int[longs.length];

			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			DataOutputStream dataOut = new DataOutputStream(bytesOut);

			try
			{
				for (int pos=0,i=0; i < longs.length; ++i)
				{
					offsets[i] = pos;
					if (lengths[i] > -1)
						pos += lengths[i];
					switch (lengths[i])
					{
						case -1: // use all lengths 1,2,4,6, and 8
						    dataOut.writeByte((byte)longs[i]);
						    dataOut.writeShort((short)longs[i]);
						    dataOut.writeInt((int)longs[i]);
							dataOut.writeInt((int)((longs[i] & 0xffffffff0000L) >> 16)); 
							    dataOut.writeShort((short)((longs[i]) & 0xffff));
							dataOut.writeLong(longs[i]);
							pos += 21;
							break;
						case 1: dataOut.writeByte((byte)longs[i]);       break;
						case 2: dataOut.writeShort((short)longs[i]);     break;
						case 4: dataOut.writeInt((int)longs[i]);         break;
						case 6: dataOut.writeInt((int)((longs[i] & 0xffffffff0000L) >> 16)); 
							    dataOut.writeShort((short)(longs[i] & 0xffff)); break;
						case 8: dataOut.writeLong(longs[i]);             break;
						default:
							assertTrue("testReadLong: illegal length defined in test data: only length 1,2,4,6, and 8 are supported by this test.", false);
					}
				}
				dataOut.flush();
				bytesOut.flush();
				dataOut.close();
				bytesOut.close();
			}
			catch (Exception ex)
			{
				assertTrue("testReadLong: error preprocessing test data: " + ex.getMessage(), false);
			}

			byte[] bytes = bytesOut.toByteArray();
			bytesIn.setBytes(bytes, 0, bytes.length);

			for (int i=0; i < longs.length; ++i)
			{
				String errorMsg = "Long parsing: long " + longs[i] + " (length " + lengths[i] + ") expected to be value " + expect[i] + " is expected to " + (valid[i]?"pass":"fail") + " ";

				try
				{
					customIn.moveToOffset(offsets[i]);
					long x;
					if (lengths[i] < 0)
					{
						int lens[] = { 1, 2, 4, 6, 8 };
						for (int j=0; j < lens.length; ++j)
						{
							x = customIn.readLong(lens[j], false/*not unsigned*/);
							if (valid[i])
								assertTrue(errorMsg + "Parsed long " + x + " wasn't the expected value", x == expect[i]);
							else
								assertTrue(errorMsg + "Parsed long " + x + " was the expected value",    x != expect[i]);
						}
					}
					else
					{
						x = customIn.readLong(lengths[i], false/*not unsigned*/);
						if (valid[i])
							assertTrue(errorMsg + "Parsed long " + x + " wasn't the expected value", x == expect[i]);
						else
							assertTrue(errorMsg + "Parsed long " + x + " was the expected value",    x != expect[i]);
					}
				}
				catch (ParseException ex)
				{
					assertTrue(errorMsg + ex, !valid[i]);
				}
				catch (IOException ex)
				{
					assertTrue(errorMsg + ex, false);
				}
			}
		}

		public void testReadString()
		{
			String[]  strings = { "", "text", "more textXXX", "\000\013elevenCharsXXX", "\007byteEnc", "04four", "null-terminated\0" };
			String[]  expect  = { "", "text", "more text",    "elevenChars",            "byteEnc",     "four",   "null-terminated" };
			int[]     value   = { 0,  4,      9,              0,                        0,             2,        0 };
			int[]     policy  = 
			{ 
				FIXED_LENGTH, 
				FIXED_LENGTH,
				FIXED_LENGTH,
				SHORT_ENCODED_LENGTH,
				BYTE_ENCODED_LENGTH,
				TEXTUALLY_ENCODED_LENGTH,
				NULL_TERM
			};

			int[] offsets = new int[strings.length];
			byte[] bytes = getBytes(strings, offsets);
			bytesIn.setBytes(bytes, 0, bytes.length);

			for (int i=0; i < strings.length; ++i)
			{
				try
				{
					customIn.moveToOffset(offsets[i]);
					String s = customIn.readString(policy[i], value[i]);
					if (s == null && expect[i] != null)
						assertTrue("String \"" + strings[i] + "\" parsed to null, which as unexpected.", false);
					else
						assertTrue("String \"" + s + "\" is not equals to the the expected \"" + expect[i] + "\".", s.equals(expect[i]));
				}
				catch (IOException ex)
				{
					assertTrue("Unexpeted IO error reading byte array: " + ex, false);
				}
			}
		}

		public void testReadEighths()
		{
			String data   = "-abcdefghijklZYXWABCDEFGHIJKL1234 ";
			String expect = "f1234567fffffffffffffffffffffffff0";
			byte[] bytes =new byte[data.length()];
			for (int i=0; i < data.length(); ++i)
				bytes[i] = (byte)data.charAt(i);
			bytesIn.setBytes(bytes, 0, bytes.length);

			for (int i=0; i < bytes.length; ++i)
			{
				try
				{
					int x = customIn.readEighthsChar();
					assertTrue("Expected " + expect.charAt(i) + ", got " + x, 
						   x < 10 && x > -1 && expect.charAt(i) == (""+x).charAt(0));
				}
				catch (IOException ex)
				{
					assertTrue("Unexpected IO error while testing eighths: " + ex, false);
				}
				catch (ParseException ex)
				{
					assertTrue("Unexpected failure for char '" + data.charAt(i) + "': " + ex, expect.charAt(i)=='f');
				}
			}
		}

		public void testReadDecimal()
		{
			String[]  data   = { ".",   ".0", ".123000", ".000123", "99999", "876543212345", "-123", "xxx", "0.0" };
			double[]  expect = { 0,     0.0,  0.123,     0.000123,  0.99999, 0.876543212345, 0,      0,     0,    };
			boolean[] valid  = { false, true, true,      true,      true,    true,           false,  false, false };

			byte[] bytes = getBytes(data, null);
			bytesIn.setBytes(bytes, 0, bytes.length);

			for (int i=0; i < data.length; ++i)
			{
				try
				{
					double x = customIn.readDecimal(data[i].length());
					assertTrue("Expected " + expect[i] + ", got " + x, !valid[i] || x == expect[i]);
				}
				catch (IOException ex)
				{
					assertTrue("Unexpected IO error: " + ex, false);
				}
				catch (ParseException ex)
				{
					assertTrue("Unexpected parse exception reading expected decimal " + expect[i],
						   !valid[i]);
				}
			}
		}

		/**
		 * a=0.125, b=0.250, c=0.375, d=0.500, e=0.625, f=0.750, g=0.875, 'space'=0.000
		 */
		public void testReadShortPrice()
		{
			String[]  data    = { "a",  "1b",  "987654321g",    "12i ", "12", "crap",  " d ", " 12f ", "1b", "0h",  "12 f", };
			int[]     len     = { 1,    2,     10,               4,     2,    4,       3 ,    5,       2,    2,     4,      };
			boolean[] valid   = { true, true,  true,            false,  true, false,   true,  true,    true, false, false,  }; 
			double[]  expect  = { 0.125,  1.250, 987654321.875, 0,      12.0, 0,       0.500, 12.750,  1.25, 0,     0,      };
			int[]     offsets = new int[data.length];
			byte[]    bytes   = getBytes(data, offsets);
			bytesIn.setBytes(bytes, 0, bytes.length);

			for (int i=0; i < data.length; ++i)
			{
				try
				{
					Price price = customIn.readPriceUsingEighths(len[i]);
					assertTrue("Expected failure for string \"" + data[i] + "\" but it succeeded in creating "+ price,
						   valid[i]);
					assertTrue("Expected, for \"" + data[i] + "\", price value " + expect[i] + ", but got " + price,
						   Math.abs(price.toDouble() - expect[i]) < 0.01);  // Price object rounds off to the nearest cent
				}
				catch (IOException ex)
				{
					assertTrue("Unexpected IO exception trying to deal with \"" + data[i] + "\".", false);
				}
				catch (ParseException ex)
				{
					assertTrue("Unexpected parse exception for price string \"" + data[i] + "\" " + ex, !valid[i]);
				}
			}
			
		}

		public void testReadLongPrice()
		{
			// Note that for this data set, the last element shoul dbe the "-1.-1" case, since it causes a parse exception before
			// all of the bytes are read.
			//
			final double inf = Double.POSITIVE_INFINITY;
			String[]  data      = { "0.0", "1 1", "-1.0", "-1 0", "12345.5000", "12345 128", "7654321.1234", "cra", "MKT  .025", "-1.-1" };
			int[]     intLen    = { 1,     1,     2,      2,      5,            5,           7,              3,      5,           2,      };
			int[]     remLen    = { 1,     1,     1,      1,      4,            3,           4,              1,      3,           2,      };
			long[]    scale     = { 0,     5,     0,      10,     0,            256,         0,              10,     -1,          10,     };
			boolean[] valid     = { true,  true,  true,  true,    true,         true,        true,           false,  true,        false   };
			double[]  expect    = { 0.0,   1.2,   -1.0,  -1.0,    12345.5,      12345.500,   7654321.1234,   0,      inf,         0 };
			int[]     offsets   = new int[data.length];
			byte[]    bytes     = getBytes(data, offsets);

			bytesIn.setBytes(bytes, 0, bytes.length);
			for (int i=0; i < data.length; ++i)
			{
				try
				{
					Price price = customIn.readStandardPrice(intLen[i], remLen[i], scale[i]);
					assertTrue("Expected failure for string \"" + data[i] + "\" but it succeeded in creating "+ price,
						   valid[i]);
					
					if (!(expect[i] == Double.POSITIVE_INFINITY && price.isMarketPrice()))
						assertTrue("Expected price value " + expect[i] + ", but got " + price, 
							   Math.abs(price.toDouble() - expect[i]) < 0.00000001);
				}
				catch (IOException ex)
				{
					assertTrue("Unexpected IO exception trying to deal with \"" + data[i] + "\".", false);
				}
				catch (ParseException ex)
				{
					System.out.println("Was this parse exception expected? idx " + i + "" + (valid[i] ? "no" : "yes"));
					assertTrue("Unexpected parse exception " + ex, !valid[i]);
				}
			}
		}
		
		public Test(String methodName)
		{
			super(methodName);
		}
		public static void main(String args[])
		{
			System.out.println("Unit testing CustomInputStream");
			junit.textui.TestRunner.run(suite());
		}
	}
/**
 * If offsets are going to be used, then then the stream should be markable and
 * be have been marked at the desired zeroth point. (Offsets are managed by calling
 * InputStream.reset() then InputStream.skip(offsetValue)).
 * @param in java.io.InputStream the input stream from which data is to be read.
 */
public CustomInputStream(InputStream in)
{
	super(in);
	
	// Pre-allocate commonly used byte array lengths.
	//
	bytePool.addToPool(4, 4);
	bytePool.addToPool(2, 8);
}
/**
 * Return the current value of the position marker of the underlying ReplaceableByteArrayInputStream, if that
 * is, in fact, the underlying stream type: -1 will be returned otherwise.
 * 
 * @return int 
 */
public int getPos()
{
	int pos = -1;
	if (ReplaceableByteArrayInputStream.class.isInstance(this.in))
	{
		ReplaceableByteArrayInputStream bytesIn = (ReplaceableByteArrayInputStream)in;
		pos = bytesIn.getPos();
	}
	return pos;
}
	static public void main(String args[])
	{
		Test.main(args);
	}
	/**
	 *  Make the current position in the stream to be "offset" from the last mark.
	 *  An IOException will be thrown if the underlying stream is not markable or
	 *  if the given offset is >= the number of bytes available to be read.
	 */
	public void moveToOffset(int offset) throws IOException
	{
		in.reset();
		in.skip(offset);
	}
	/**
	 * This simplistic parsing method assumes that the date to be parsed has a length equal to
	 * the pattern being applied.  This is sufficient for most simple parsing information.
	 * The pattern is one which is legal according to the rules defined for
	 * <code>java.text.SimpleDateFormat</code>.  Note that this method can be used to parse a
	 * a time value as well (ex, the pattern "hh:mm:ss").
	 * <p>
	 * For example, the string "MMddyyyy" will properly parse a two-byte month (ASCII integer)
	 * followed by a two byte day (ASCII integer) followed by a four byte year (ASCII integer).
	 *
	 * @see #java.text.SimpleDateFormat
	 * 
	 * @author Steven Sinclair
	 * @param pattern the pattern to use to recognize the date.  If null is passed, then
	 *  	the format string "MMddyyyy" is used.
	 * @return java.util.Date the date that was parsed.
	 */
	public java.util.Date readDate(String pattern)
		throws IOException, ParseException
	{
		dateFormat.applyPattern(pattern);
		byte[] bytes = bytePool.borrowArray(pattern.length());
		try
		{
			if (this.read(bytes) < pattern.length())
				throw new IOException("Could not read enough bytes to match date pattern \"" + pattern + "\" (pos " + getPos() +  ")");
			return dateFormat.parse(new String(bytes));
		}
		finally
		{
			bytePool.recycleArray(bytes);
		}
	}
/**
 * Read <code>decimalDigits</code> bytes.  They are expected to be in the
 * format "[.]{0-9}*", ex ".0123" or "01234".  If a decimal place does not
 * exist then it is assumed to be implicit.  The double value returned will
 * be the value represented by the characters, guaranteed to be >= 0 and < 1.
 * 
 * @author Steven Sinclair
 * @return double the decimal value [0..1) represented by the bytes read.
 * @param decimalDigits the number of bytes to inspect
 */
public double readDecimal(int decimalDigits)
	throws IOException, ParseException
{
	if (decimalDigits == 0)
		return 0.0;
	byte[] bytes = bytePool.borrowArray(decimalDigits);
	try
	{
		readFully(bytes);
		double result = 0.0;
		int first = (bytes[0]=='.' ? 1 : 0);
		for (int i=first; i < bytes.length; ++i)
		{
			if (!Character.isDigit((char)bytes[i]))
				throw new ParseException("Parsing decimal value failed: non-integral byte " + bytes[i], getPos());
			result = (result*10) + (int)((char)bytes[i] - '0');
		}
		return result / Math.pow(10, decimalDigits-first);
	}
	finally
	{
		bytePool.recycleArray(bytes);
	}
}
/**
 *  Read the next byte.  Expect a character in the range a..h and return the number of eighths
 *  that this character represents using the rule a==1/8, b==2/8, c==2/8, ..., g==7/8.  0/8 is 
 *  represented by a space (' '). This method is case sensitive (ie, won't recognizes A..H).  
 *  A ParseException will be thrown if an unrecognizable byte is read.
 * 
 * @author Steven Sinclair
 * @return int a value in the range 0..7 representing the number of eights translated from the byte
 *		read from the underlying input stream.
 * @param integerDigits int
 */
public int readEighthsChar()
	throws IOException, ParseException
{
	char c = (char)readByte();
	if ((c < 'a' || c > 'g') && c != ' ' )
		throw new ParseException("Error reading eighths character: not in range a..h", getPos());
	return c==' ' ? 0 : ((int)(c-'a')+1);
}
/**
 * 
 * @author Steven Sinclair
 * @return int the integer read.
 * @param length the number of digits representing the integer
 * @param unsigned If true, then reads the bytes as unsigned.  Note, however, that since a short is
 * 		returned by this method, only values <= 2^31 will be properly returned by this method.
 */
public int readInt(int length, boolean unsigned) throws IOException, ParseException
{
	return (int)readLong(length, unsigned);
}
/**
 * 
 * @author Steven Sinclair
 * @return int
 * @param length int
 * @exception java.text.ParseException The exception description.
 * @exception java.io.IOException The exception description.
 */
public int readIntAsString(int length) throws ParseException, IOException
{
	return (int)readLongAsString(length);
}
	/**
	 *  Read a long value frm the given number of bytes
	 *  ' ' and '\0' characters will be ignored.  The value passed back is a long, so
	 *  any valid string representation of a long will be returned (approx. +/- 9.2e18)
	 *
	 *  @param length the number of bytes to read in.  Must be in the inclusive range [0..8] 
	 * @param unsigned If true, then reads the bytes as unsigned.  Note, however, that since a short is
	 * 		returned by this method, only values <= 2^63 will be properly returned by this method.
	 *  @return long the integer read from the stream
	 */
	public long readLong(int length, boolean unsigned)
		throws IOException, ParseException
	{
		byte[] bytes = bytePool.borrowArray(length);
		long value = 0;
		try
		{
			this.readFully(bytes);
			if (length > 8)
				throw new IllegalArgumentException("Parsing long value: can't currently handle a length of " + length + " (pos " + getPos() + ")");
			boolean isNegative = false;
			for (int i=0; i < length; ++i)
			{
				byte b = bytes[i];
				if (!unsigned && i==0 && (b & 0x80) != 0) // If we received a 2's complement negative integer.
				{
					isNegative = true;
				}
				if (isNegative)
				{
					b = (byte)(~b);
				}
				value = (value<<8) | (b&0xff);
			}
			if (isNegative)
				value = -(value+1);
		}
		finally
		{
			bytePool.recycleArray(bytes);
		}
		return value;
	}
	/**
	 *  Read a string of numeric characters and parse out the integer value.  Leading
	 *  ' ' and '\0' characters will be ignored.  The value passed back is a long, so
	 *  any valid string representation of a long will be returned (approx. +/- 9.2e18)
	 *
	 *  @param length the number of bytes to read in.
	 *  @return long the integer read from the stream
	 */
	public long readLongAsString(int length)
		throws IOException, ParseException
	{
		if (length == 0)
			return 0;
		byte[] bytes = bytePool.borrowArray(length);
		try
		{
			this.readFully(bytes);
			try
			{
				return Long.parseLong(new String(bytes).trim());
			}
			catch (NumberFormatException ex)
			{
				throw new ParseException("Error parsing long value from bytes \"" + (new String(bytes)) + "\": " + ex.getMessage(), getPos());
			}
		}
		finally
		{
			bytePool.recycleArray(bytes);
		}
	}
/**
 * Read an integer, maybe followed by a character a..h representing 1/8 through 7/8.
 * For example, "123f", "123 ", " 123a", and even "  d " are all valid, given numBytes==4.
 * 
 * 
 * @author Steven Sinclair
 * @return com.cboe.domainObjects.util.Price
 * @param integerDigits int
 */
public Price readPriceUsingEighths(int numBytes)
	throws IOException, ParseException
{
	if (numBytes < 1)
		throw new ParseException("Can't parse a price from less than 1 byte!", getPos());
	String str = readString(FIXED_LENGTH, numBytes).trim();
	char lastChar = str.charAt(str.length()-1);
	double eighths = 0;
	if (lastChar >= 'a' && lastChar <= 'g')
	{
		str = str.substring(0, str.length()-1);
		eighths = ((double)(lastChar - 'a') + 1) * 0.125;
	}
	try
	{
		long price = (str.length() > 0) ? Long.parseLong(str) : 0;
		return PriceFactory.create(price + eighths);
	}
	catch (NumberFormatException ex)
	{
		throw new ParseException("Price's integer portion not a valid integer string: \"" + str + "\"", getPos());
	}
}
/**
 * 
 * @author Steven Sinclair
 * @return short
 * @param length int
 * @param unsigned If true, then reads the bytes as unsigned.  Note, however, that since a short is
 * 		returned by this method, only values <= 2^15 will be properly returned by this method.
 */
public short readShort(int length, boolean unsigned) throws IOException, ParseException
{
	return (short)readLong(length, unsigned);
}
/**
 * 
 * @author Steven Sinclair
 * @return short
 * @param length int
 */
public short readShortAsString(int length) throws IOException, ParseException
{
	return (short)readLongAsString(length);
}
/**
 * If scale is < 1 then 256 is assumed.  Read integerDigits chars and interpret
 * them as the integer portion of hte price value.  Read a single character:
 * if it's a space then read the remainder as a fraction, applying the scale
 * value to it.  If the character's a '.' then interpret the remainder as a
 * decimal portion.  Any other character value will cause a ParseException.
 * Any other non-digital characters will cause a ParseException.
 * 
 * @author Steven Sinclair
 * @return com.cboe.util.Price
 * @param integerDigits int
 * @param remainderDigits int
 * @param scale int
 */
public Price readStandardPrice(int integerDigits, int remainderDigits, long scale)
	throws IOException, ParseException
{
	// Since the integer portion can be "M" or "MKT" (instead of a numeric string),
	// we need to recognize this case before proceeding.
	//
	long highestDigit = -1;
	boolean neg = false;
	if (integerDigits > 0)
	{
		char firstChar = (char)readByte();
		if (firstChar == 'M')
		{
			readString(FIXED_LENGTH, integerDigits + remainderDigits);
			return PriceFactory.create(Price.MARKET_STRING);
		}
		--integerDigits;
		if (Character.isDigit(firstChar) && firstChar != '0')
		{
			highestDigit = firstChar - '0';
		}
		neg = (firstChar == '-');
	}
	
	long price = readLongAsString(integerDigits);
	if (neg)
	{
		price *= -1;
	}
	else if (highestDigit > 0)
	{
		long priceDigits = Math.abs(price);
		while (priceDigits > 0)
		{
			highestDigit *= 10;
			priceDigits  /= 10;
		}
		price += highestDigit;
	}
	switch ((char)readByte())
	{
		case '.':
			return PriceFactory.create(price + readDecimal(remainderDigits));
		case ' ': return PriceFactory.create(price + readLongAsString(remainderDigits)/(double)scale);
		default:  throw new ParseException("Error reading price: expected '.' or ' '.", getPos());
	}
}
	/**
	 *  Read a string based on the given policy, which may be paramterized by the int policyValue.
	 *  @param policy  Any of the following are acceptable policies:
	 *		<lu>
	 *          <li> <b>NULL_TERM</b> the string is terminated by a null character ('\0').  policyValue is ignored.
	 *			<li> <b>FIXED_LENGTH</b> the string length is assumed to be exactly the value of polivyValue.
	 *          <li> <b>FIXED_LENGTH_OR_NULL</b> like fixced length, but will stop rreading if '\0' is encountered.
	 *			<li> <b>TEXTUALLY_ENCODED_LENGTH</b> the next 'policyValue' characters are expected to be a numeric string,
	 *					which will be parsed to determine the number of characters to read to make up the string.
	 *			<li> <b>SHORT_ENCODED_LENGTH</b> the next two bytes are assumed to be an unsigned short value representing the string length.
	 *			<li> <b>BYTE_ENCODED_LENGTH</b> the next byte is treated as an unsigned 8-bit integer representing the string length.
	 *      </lu>
	 *	@param policyValue the integer value associated with the selected policy.  It's value is defined in the list accompanying
	 *		the <code>policy</code> parameter.
	 *  @param buf The StringBuilder to which the string will be appended
	 */
	public String readString(int policy, int policyValue)
		throws IOException
	{
		int len = -1;
		switch (policy)
		{
			case NULL_TERM:
			{
				StringBuilder buf = new StringBuilder();
				while (true)
				{
					char ch = (char)readByte();
					if (ch == '\0')
						break;
					buf.append(ch);
				}
				return buf.toString();
			}
			case TEXTUALLY_ENCODED_LENGTH:
			{
				byte[] bytes = bytePool.borrowArray(policyValue);
				try
				{
					if (in.read(bytes) < policyValue)
						throw new IOException("Couldn't read " + policyValue + " bytes to determine string length. (pos " + getPos() + ")");
					try
					{ 
						len = Integer.parseInt(new String(bytes));
					}
					catch (NumberFormatException ex)
					{
						throw new IOException("Couldn't read string length value: " + ex + " (pos " + getPos() + ")");
					}
				}
				finally
				{
					bytePool.recycleArray(bytes);
				}
				break;
			}
			case FIXED_LENGTH:
				len = policyValue;
				break;
			case SHORT_ENCODED_LENGTH:
				len = this.readUnsignedShort();
				break;
			case BYTE_ENCODED_LENGTH:
				len = this.readUnsignedByte();
				break;
			default:
				throw new IllegalArgumentException("Invalid string policy " + policy + " at pos " + getPos());
		}

		byte[] bytes = bytePool.borrowArray(len);
		try
		{
			this.readFully(bytes);
			if ((policy & NULL_TERM) != 0)
			{
				for (int i=0; i < len; ++i)
				{
					if (bytes[i] == '\0')
						return new String(bytes, 0, i);
				}
			}
			return new String(bytes);
		}
		finally
		{
			bytePool.recycleArray(bytes);
		}
	}
/**
 * 
 * @author Steven Sinclair
 * @return long
 */
public long readUnsignedInt() throws IOException
{
	long ch1 = read();
	long ch2 = read();
	long ch3 = read();
	long ch4 = read();
	if ((ch1 | ch2 | ch3 | ch4) < 0)
	     throw new EOFException();
	return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
}
}
