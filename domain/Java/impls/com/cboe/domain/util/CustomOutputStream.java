package com.cboe.domain.util;

import java.io.*;
import java.text.*;
import java.util.Date;
import com.cboe.interfaces.domain.Price;
import junit.framework.*;

/**
 *  This extension of DataOutputStream is useful for writing strictly formatted
 *  data to the output stream.  Methods are provided for such thigns as precisely
 *  writing a long vlaue ot a given number of bytes, either as hex data or as readable digits.
 *  Date formatting is handled internally, and almost any format of Price object can be
 *  written.
 *  
 *  <p>
 *  <b>Note:</b> this class is not threadsafe.  Only one thread should access
 * 		a given instance of this class unless such access is synchronized (ie, treat
 *		instances of this class as shared resources).
 */
public class CustomOutputStream extends DataOutputStream
{
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

	private byte[] byteArray = new byte[8];
	
	/**
	 * Inner class for support of automated unit testing.
	 */
	static public class Test extends TestCase
	{
		private ByteArrayOutputStream bytesOut  = new ByteArrayOutputStream();
		private CustomOutputStream    customOut = new CustomOutputStream(bytesOut);
		
		public static TestSuite suite()
		{
			TestSuite result = new TestSuite();
			result.addTest(new Test("testWriteDate"));
			result.addTest(new Test("testWriteLong"));
			result.addTest(new Test("testWriteLongAsString"));
			result.addTest(new Test("testWritePrice"));
			result.addTest(new Test("testWritePrice2"));
			return result;
		}

		private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		
		private static Date parseDate(String dateStr)
		{
			try
			{
				return dateFormat.parse(dateStr);
			}
			catch (ParseException ex)
			{
				try
				{
					return timeFormat.parse(dateStr);
				}
				catch (ParseException ex2)
				{
					return null;
				}
			}
		}
		
		public void testWriteDate()
		{
			String[] dateStrs  = { "1/2/1999",   "03/02/1999", "6/1/1945",   "10:20:30", "16:00:00", "10:00:00", "01:02:03" };
			String[] format    = { "MM.dd.yyyy", "MMddyyyy",   "MM/dd/yyyy", "hh:mm:ss", "HH:mm",    "hh:mmaa",   "hhmmss"   };
			Date[]   dates     = new Date[dateStrs.length];
			bytesOut.reset();

			for (int i=0; i < dateStrs.length; ++i)
			{
				dates[i] = parseDate(dateStrs[i]);
				if (dates[i] == null)
					assertTrue("Preparing test: Unable to parse sample date from data string \"" + dateStrs[i] + "\"", false);
			}

			SimpleDateFormat formatter = new SimpleDateFormat();
			
			try
			{
				// Write the dates to the output stream
				//
				for (int i=0; i < dateStrs.length; ++i)
				{
					customOut.writeDate(dates[i], format[i]);
				}
				customOut.flush();
				bytesOut.flush();
				byte[] bytes = bytesOut.toByteArray();
				String output = new String(bytes);

				for (int pos=0,i=0; i < format.length; ++i)
				{
					formatter.applyPattern(format[i]);
					String dateStr = output.substring(pos, pos+format[i].length());
					pos += dateStr.length();
					try
					{
						Date date = formatter.parse(dateStr);
						assertTrue("Expected strings " + dateStr + " and " + dateStrs[i] + " to product equal dates: they weren't.", date.equals(dates[i]));
					}
					catch (ParseException ex)
					{
						assertTrue("Expected string to be a formatted date/time: \"" + dateStr + "\": " + ex, false);
					}
				}
			}
			catch (IOException ex)
			{
				assertTrue("Unexpected IO exception in testWriteDate: " + ex.getMessage(), false);
			}
		}

		public void testWriteLong()
		{
			long[] longs = { 1, 0, -1, 255, -255, 5000000000L, -5000000000L };

			bytesOut.reset();
			
			// Test these data lengths:
			//
			int[] lengths = { 1, 2, 3, 4, 5, 6, 7, 8 };
			long[] maxVal = { 0x7f, 0x7fff, 0x7fffff, 0x7fffffff, 0x7fffffffffL, 
				              0x7fffffffffffL, 0x7fffffffffffffL, 0x7fffffffffffffffL };
			
			try
			{
				for (int i=0; i < longs.length; ++i)
				{
					for (int j=0; j < lengths.length; ++j)
					{
						if (Math.abs(longs[i]) <= maxVal[j])
							customOut.writeLong(longs[i], lengths[j]);
					}
				}

				customOut.flush();
				bytesOut.flush();
				
				byte[] bytes = bytesOut.toByteArray();
				byte[] buf = new byte[8];

				DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(buf));

				int pos=0;
				for (int i=0; i < longs.length; ++i)
				{
					for (int j=0; j < lengths.length; ++j)
					{
						if (Math.abs(longs[i]) <= maxVal[j])
						{
							int rjust = 8 - lengths[j];
							System.arraycopy(bytes, pos, buf, rjust, lengths[j]);
							pos += lengths[j];
							int fill = ((buf[rjust] & 0x80) != 0) ? 0xff : 0x00;
							for (int k=0; k < rjust; ++k)
								buf[k] = (byte)fill;
							dataIn.reset();
							long x = dataIn.readLong();
							assertTrue("Expected " + longs[i] + " from length " + lengths[j] + ": got " + x, x==longs[i]);
						}
					}
				}
			}
			catch (IOException ex)
			{
				assertTrue("Unexpected IO exception in testWriteLong: " + ex.getMessage(), false);
			}
		}
		
		public void testWriteLongAsString()
		{
			long[] longs   = { -1, 0, 1, 255, -255, 5000000000L, -5000000000L };
			int[]  lengths = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
			long[] maxVal  = { 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, 
				               9999999999L, 99999999999L, 999999999999L };
			bytesOut.reset();
			
			try
			{
				for (int i=0; i < longs.length; ++i)
				{
					for (int j=0; j < lengths.length; ++j)
					{
						if (Math.abs(longs[i]) * (longs[i]<0 ? 10:1) <= maxVal[j])
						{
							customOut.writeLongAsString(longs[i], lengths[j], '0', false/*rightJustify*/);
						}
					}
				}

				customOut.flush();
				bytesOut.flush();
				
				byte[] bytes = bytesOut.toByteArray();
				String output = new String(bytes);

				int pos=0;
				for (int i=0; i < longs.length; ++i)
				{
					for (int j=0; j < lengths.length; ++j)
					{
						if (Math.abs(longs[i]) * (longs[i]<0 ? 10:1) <= maxVal[j])
						{
							String str = output.substring(pos, pos+lengths[j]);
							pos += lengths[j];
							long l = Long.valueOf(str).longValue();

							assertTrue("Value " + l + " != " + longs[i] + " using length " + lengths[j], l == longs[i]);
						}
					}
				}
			}
			catch (IOException ex)
			{
				assertTrue("Unexpected IO exception in testWriteLongAsString:" + ex.getMessage(), false);
			}
		}
		
		public void testWritePrice()
		{
			Price[]    prices  = { PriceFactory.create(123.5), PriceFactory.create(-0.126) };
			
			String[]   formats = { "%2i",       "%4i.%2d",             "%3i",          ".%4.3f",          "xx%c"        };
			String[][] expect  = { {null,"-0"}, {"0123.50","-000.13"}, {"123", "-00"}, {".5000",".1260"}, {"xxd","xxa"} };

			try
			{
				bytesOut.reset();
				for (int i=0; i < formats.length; ++i)
				{
					for (int j=0; j < prices.length; ++j)
					{
						try
						{
							customOut.writePrice(prices[j], formats[i]);
							assertTrue("expected writing price " + prices[j] + " using format \"" + formats[i] + "\" to fail: it didn't.", 
								   expect[i][j] != null);
							customOut.write(' ');
						}
						catch (NumberFormatException ex)
						{
							assertTrue("writing price " + prices[j] + " using format \"" + formats[i] + "\" failed: " + ex, 
								   expect[i][j] == null);
						}
					}
				}

				customOut.flush();
				bytesOut.flush();
				byte[] bytes = bytesOut.toByteArray();
				String output = new String(bytes);

				for (int pos=0,i=0; i < expect.length; ++i)
				{
					for (int j=0; j < expect[i].length; ++j)
					{
						if (expect[i][j] == null)
							continue;
						String substr = output.substring(pos, pos+expect[i][j].length());
						pos += substr.length() + 1;
						assertTrue("Expected string \"" + expect[i][j] + "\", got \"" + substr + "\"; format \"" + formats[i] + "\" applied to price " + prices[j],
							   expect[i][j].equals(substr));
					}
				}
			}
			catch (IOException ex)
			{
				assertTrue("Unexpected IO problem writing prices: " + ex, false);
			}
		}

		/**
		 *  An extra set of test to verify that teenies etc are written properly in the decimal (%D) format.
		 */
		public void testWritePrice2()
		{
			double[] prices  = { 0.0,   10.125,    12.0625,  999.5625,  0.8125,    123.375, 12345.0, 1234.0625, 123.8125 };
			int[]    lengths = { 3,     7,         6,        7,         7,         5,       6,       6,		    6        };
			String[] expect  = { "0  ", "00010 a", "012 1+", "0999 9+", "000 13+", "123 c", null,    null,      null     };
			
			try
			{
				bytesOut.reset();
				for (int i=0; i < prices.length; ++i)
				{
					try
					{
						customOut.writePrice(PriceFactory.create(prices[i]), "%" + lengths[i] + "D");
						customOut.write(' ');
						assertTrue("expected writing price " + prices[i] + " using format \"%" + lengths[i] + "D\" to fail: it didn't.", 
							   expect[i] != null);
					}
					catch (NumberFormatException ex)
					{
						assertTrue("unexpected format exception writing price " + prices[i] + " using format \"%" + lengths[i] + "D\": " + ex, expect[i]==null);
					}
				}
				customOut.flush();
				bytesOut.flush();
				byte[] bytes = bytesOut.toByteArray();
				String output = new String(bytes);
				for (int pos=0,i=0; i < expect.length; ++i)
				{
					if (expect[i] == null)
						continue;
					String substr = output.substring(pos, pos + expect[i].length());
					pos += substr.length() + 1;
					assertTrue("Expected string \"" + expect[i] + "\", for \"" + substr + "\": format \"%" + lengths[i] + "D\" applied to price " + prices[i], expect[i].equals(substr));
				}
			}
			catch (IOException ex)
			{
				assertTrue("Unexpected IO problem writing %D prices: " + ex, false);
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
 * CustomOutputStream constructor comment.
 * @param out java.io.OutputStream
 */
public CustomOutputStream(OutputStream out)
{
	super(out);
}
/**
 * 
 * @author Steven Sinclair
 * @return java.io.OutputStream
 */
public OutputStream getUnderlying()
{
	return this.out;
}
/**
 * A convenience method which returns the byte array of the underlying stream, assuming
 * that the underlying stream is a ByteArrayOutputStream.
 * Any other underlying OutputStream class will cause an <code>IllegalArgumentException</code>.
 * It should be noted, however, that the byte array returned is not guaranteed
 * to be inviolate: that is, the ByteArrayOutputStream may maintain a reference to it,
 * and continue writing to it, possibly writing over current data. Unless it is known
 * that the ByteArrayOutputStream willbe inactive for the duration of the processing
 * etc. of this byte array, a copy should be made (by passing in a value of true for
 * the <code>makeCopy</code> parameter.)
 * 
 * @author Steven Sinclair
 * @return byte[]
 * @param customOut com.cboe.util.CustomOutputStream
 */
public byte[] getUnderlyingBytes(boolean makeCopy)
	throws IOException 
{
	flush();
	OutputStream out = getUnderlying();
	out.flush();
	if (!ByteArrayOutputStream.class.isInstance(out))
		throw new IllegalArgumentException("Expected a ByteArrayOutputStream as the underlying stream for CustomInputStream.");
	ByteArrayOutputStream bytesOut = (ByteArrayOutputStream)out;
	byte[] bytes = bytesOut.toByteArray();
	if (makeCopy)
	{
		byte[] newBytes = new byte[bytes.length];
		System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
		return newBytes;
	}
	return bytes;
}
/**
 * 
 * @author Steven Sinclair
 * @param args java.lang.String[]
 */
public static void main(String args[])
{
	Test.main(args);
}
	public void writeDate(int year, int month, int day, int hour, int min, int sec, int frac, String format)
		throws IOException
	{
		java.util.Calendar cal = new java.util.GregorianCalendar(year, month, day, hour, min, sec);
		if (frac != 0)
			cal.set(java.util.Calendar.MILLISECOND, frac*10); // (fraction is centiseconds)
		writeDate(cal.getTime(), format);
	}
	public void writeDate(java.util.Date date, String format)
		throws IOException
	{
		if (format == null)
			format = "MM/dd/yyyy";
		dateFormat.applyPattern(format);
		writeBytes(dateFormat.format(date));
	}
	public void writeDateStruct(com.cboe.idl.cmiUtil.DateStruct struct, String format)
		throws IOException
	{
		writeDate(struct.year, struct.month - 1, struct.day, 0, 0, 0, 0, format);
	}
	public void writeDateTimeStruct(com.cboe.idl.cmiUtil.DateTimeStruct struct, String format)
		throws IOException
	{
		writeDate(struct.date.year, struct.date.month-1, struct.date.day,
			      struct.time.hour, struct.time.minute, struct.time.second, struct.time.fraction, format);
	}
	/**
	 * Write a long value to the output stream using the specified number of bytes.  If the
	 * number could not be written to the given number of bytes then an IllegalArgumentException
	 * is thrown. (ie, trying to write 987654 to two bytes will cause an exception).
	 * 
	 * <p>
	 * Note that any integral primitive (long, int, short, byte) can be passed to this method
	 * without any casting by the caller.
	 *
	 * @param longValue the value to write
	 * @param length the number of bytes to write.
	 */
	public void writeLong(long longValue, int length)
		throws IOException
	{
		if (length > byteArray.length)
		{
			byteArray = new byte[length];
		}
		long tmp = longValue;
		for (int i=0; i < length; ++i)
		{
			byteArray[length-i-1] = (byte)(tmp & 0xff);
			tmp >>= 8;
		}
		if ( (longValue >= 0 && tmp != 0) || (longValue < 0 && tmp != -1))
		{
			throw new IllegalArgumentException("value " + longValue + " is too large to write to " + length + " byte" + (length==1?"":"s"));
		}
		
		write(byteArray, 0, length);
	}
	/**
	 * Write a stringified long to the output stream using a fixed number of bytes.  If length
	 * is more room than is required, then 'padChar' are used to fill in the excess
	 * space, either befre or after the number, depending of the value of 'padPrefix'.
	 *
	 * @param longValue the value to write
	 * @param length the number of bytes to write.  If longValue cannot fit in length bytes,
	 *		a NumberFormatException is thrown.
	 * @param padChar the character to use for any padding that may be required.  If longValue
	 *		is negative, padChar is '0', and padPrefix is true, then the '0's will be placed
	 * 		between the '-' and the numeric characters.
	 * @param padPrefix if true, then any required padChars will be prepended to the number, otherwise
	 *		they will be appended to the number.
	 */
	public void writeLongAsString(long longValue, int length, char padChar, boolean leftJustify)
		throws IOException
	{
		String longStr = ""+Math.abs(longValue);
		int len = longStr.length() + (longValue<0 ? 1 : 0);
		if (len > length)
			throw new NumberFormatException("Cannot write value " + longValue + " in " + length + " bytes: insufficient space.");
		if (longValue < 0 && padChar == '0')
			write('-');
		if (!leftJustify)
		{
			for (int i=0; i < length-len; ++i)
				write(padChar);
		}
		if (longValue < 0 && padChar != '0')
			write('-');
		writeBytes(longStr);
		if (leftJustify)
		{
			for (int i=0; i < length-len; ++i)
				write(padChar);
		}
	}
	/**
	 * Write the price in the format defined by the given pattern.  The pattern is any string, which will be
	 * written character-for-character to the output stream, unless a '%' is encountered: if this is the case,
	 * then one of the format substitution strings given below should be used.
	 *
	 *  <lu>
	 *   <li> %4i integer portion    (NumberFormatException thrown if the integer won't fit in the given # of chars)
	 *   <li> %7.5f fractional portion given in fractions, using 10^n, where n is the value after the decimal, in this example 5.
	 *				If no value n is supplied (ex, "%7f"), then fractions of 1/256 will be used.
	 *   <li> %9d fractional portion given as a decimal (without the '.' & rounded if it won't fit in the given # of chars)
	 *   <li> %c  fractional portion in 1/8ths (0.125) (one of a..g representing 1/8 through 7/8, space for 0/8)
	 *   <li> %7D conform to TPF's decimal format (eighths characters, teenies as "##+", left justified.
	 *   <li> %%  write a '%' character.
	 * </lu>
	 *
	 * Note that "%i", "%f", and "%.5f" are equivalent to "%0i", "%0f", and "%0.5f": no default length is calculated.
	 *
	 * 125
	 *  ".%2.2f"  12500
	 * "anythingyouwant %4i %5i...."
	 * 
	 * <b>Examples:</b>
	 * <p> Applying 123.5 to the format string "%5i %c"    will yield "00123 d"
	 * <p> Applying 123.5 to the format string "%3if%3.1f" will yield "123f5" (5 tenths)
	 * <p> Applying 123.5 to the format string "%3i.%2d"   will yield "123.50"
	 * <p> Applying 123.5 to the format string "%7D"       will yield "123 f"
	 */
	public void writePrice(Price price, String pattern)
		throws IOException
	{
		double value  = price.toDouble();
		int    intVal = (int)value;
		double remVal = Math.abs(value) - Math.floor(Math.abs(value));

		// Count the # of digits in intValue
		//
		int intDigits = (intVal>0) ? 0 : 1;
		for (int tmp=Math.abs(intVal); tmp > 0; ++intDigits, tmp/=10)
		{
		}
		
		// Write pattern to the output stream.
		// 
		for (int i=0; i < pattern.length(); ++i)
		{
			char ch = pattern.charAt(i);
			if (ch == '%')
			{
				int param1=0, param2=-1;
				char c='\0';
				while (true)
				{
					c = pattern.charAt(++i);
					if (!Character.isDigit(c))
						break;
					param1 = param1*10 + ((int)c - (int)'0');
				}
				if (c == '.')
				{
					param2 = 0;
					while (true)
					{
						c = pattern.charAt(++i);
						if (!Character.isDigit(c))
							break;
						param2 = param2*10 + ((int)c - (int)'0');
					}
				}
				
				switch (c)
				{
					case 'c':
					{
						int frac = (int)(remVal*8);
						if (frac > 7)
						{
							throw new NumberFormatException("Price value invalid for a..g fractional formatting: " + value);
						}
						if (frac == 0)
							write(' ');
						else
							write((char)('a' + frac-1));
						break;
					}
					case '%':
						write('%');
						break;
					case 'i':	// write the integer portion
						if (param1 < intDigits)
							throw new NumberFormatException("Can't write int portion of " + price + " in " + param1 + " byte(s).");
						if (intVal==0 && value<0)
						{
							--param1;
							write('-');
						}
						writeLongAsString(intVal, param1, '0', false/*prefix-padding*/);
						break;
					case 'f':
					{
						double scale = (param2 < 0 ? 256 : Math.pow(10, param2));
						long fraction = Math.round(remVal * scale);

						// Write the fraction:
						//
						writeLongAsString(fraction, param1, '0', true/*suffix-padding*/);
						
						break;
					}
					case 'd':
					{
						double tmp = remVal;
						for (int j=0; j < param1; ++j)
						{
							int digit = (int)((tmp*=10) % 10);
							if (j == param1-1)
								write((char)('0' + ( ((tmp*10) %10)<5 ? digit : digit+1)));
							else
								write((char)('0' + digit));
						}
						break;
					}
					case 'D':
					{
						int sixteenths = (int)Math.round(remVal*16);
						
						if (sixteenths%2 != 0) // it's not an eighths value.
						{
							if (sixteenths < 10)
								writeLongAsString(intVal, param1 - 3, '0', false);
							else
								writeLongAsString(intVal, param1 - 4, '0', false);
							write(' ');
							writeBytes(""+sixteenths);
							write('+');
						}
						else	// it is an eighths value
						{
							writeLongAsString(intVal, param1 - 2, '0', false);
							write(' ');
							if (sixteenths==0)
								write(' ');
							else
								write('a' + (sixteenths/2)-1);
						}
						break;
					}
					default:
						throw new NumberFormatException("Cannot write price " + price + ": invalid pattern '%' command: \"" + pattern + "\"");
				}
			}
			else
			{
				write(ch);
			}
		}
	}
/**
 * Write string str to exactly len bytes, padding any excess space with padChar on the
 * right, or if <code>str.length() > len</code>, then write <code>str.substring(len)</code>.
 * 
 * @author Steven Sinclair
 * @param str java.lang.String
 * @param len int
 */
public void writeString(String str, int len)
	throws IOException
{
	writeString(str, len, ' ', true/*leftJustify*/);
}
/**
 * Write string str to exactly len bytes, padding any excess space with padChar
 * (on the right if leftJustify is true, otherwise on the left),
 * or if <code>str.length() > len</code>, then write <code>str.substring(len)</code>.
 * 
 * @author Steven Sinclair
 * @param str java.lang.String
 * @param len int
 * @param padChar char
 * @param leftJustify boolean
 */
public void writeString(String str, int len, char padChar, boolean leftJustify)
	throws IOException
{
	if (str.length() == len)
	{
		writeBytes(str);
	}
	else if (str.length() > len)
	{
		writeBytes(str.substring(0, len));
	}
	else
	{
		if (!leftJustify)
		{
			for (int i=0; i < len - str.length(); ++i)
				write(padChar);
		}
		writeBytes(str);
		if (leftJustify)
		{
			for (int i=0; i < len - str.length(); ++i)
				write(padChar);
		}
	}
}
	public void writeTimeStruct(com.cboe.idl.cmiUtil.TimeStruct struct, String format)
		throws IOException
	{
		writeDate(0, 0, 0, struct.hour, struct.minute, struct.second, struct.fraction, format);
	}
}
