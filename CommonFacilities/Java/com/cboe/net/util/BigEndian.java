package com.cboe.net.util;

import junit.framework.*;

/**
 * Encode primitive types (and a couple of others) to big endian byte encoding.
 */
public class BigEndian
{
	/**
	 * Inner class for support of automated unit testing.
	 */
	static public class Test extends TestCase
	{
		public static TestSuite suite()
		{
			TestSuite result = new TestSuite();
			result.addTest(new Test("testTypes"));
			return result;
		}

		public void testTypes()
		{
			Object encode[] =
			{ "boolean", new Boolean(true),
			  "boolean", new Boolean(false),
			  "byte",    new Byte((byte)0),
			  "byte",    new Byte((byte)1),
			  "byte",    new Byte((byte)-1),
			  "byte",    new Byte(Byte.MAX_VALUE),
			  "byte",    new Byte(Byte.MIN_VALUE),
			  "byte",    new Byte((byte)(Byte.MAX_VALUE/2)),
			  "byte",    new Byte((byte)(Byte.MIN_VALUE/2)),
			  "short",   new Short((short)0),
			  "short",   new Short((short)1),
			  "short",   new Short((short)-1),
			  "short",   new Short(Short.MAX_VALUE),
			  "short",   new Short(Short.MIN_VALUE),
			  "short",   new Short((short)(Short.MAX_VALUE/2)),
			  "short",   new Short((short)(Short.MIN_VALUE/2)),
			  "int",     new Integer(0),
			  "int",     new Integer(1),
			  "int",     new Integer(-1),
			  "int",     new Integer(Integer.MAX_VALUE),
			  "int",     new Integer(Integer.MIN_VALUE),
			  "int",     new Integer(Integer.MAX_VALUE/2),
			  "int",     new Integer(Integer.MIN_VALUE/2),
			  "price",   new Double(0),
			  "price",   new Double(1),
			  "price",   new Double(-1),
			  "price",   new Double(123456.789),
			  "price",   new Double(-1234.5678),
			  "string",  "",
			  "string",  "hello world",
			  "time",    new java.util.Date(0),
			  "time",    new java.util.Date(/*now*/),
			  "time",    new java.util.Date(3600000L * 24 * 365 * 30 /*about Y2k*/),
			  "time#",   new Long(0),
			  "time#",   new Long(new java.util.Date().getTime()/*now*/),
			  "time#",   new Long(3600000L * 24 * 365 * 30 /*about Y2k*/)
			};
			
			byte bytes[] = new byte[4096];
			int pos=0;
			for (int i=0; i < encode.length; ++i)
			{
				String type = (String)encode[i++];
				if (type.equals("boolean"))
					pos += encodeBoolean(((Boolean)encode[i]).booleanValue(), bytes, pos);
				else if (type.equals("byte"))
					pos += encodeByte(((Byte)encode[i]).byteValue(), bytes, pos);
				else if (type.equals("short"))
					pos += encodeShort(((Short)encode[i]).shortValue(), bytes, pos);
				else if (type.equals("int"))
					pos += encodeInt(((Integer)encode[i]).intValue(), bytes, pos);
				else if (type.equals("price"))
					pos += encodePrice(((Double)encode[i]).doubleValue(), bytes, pos);
				else if (type.equals("string"))
					pos += encodeString((String)encode[i], bytes, pos);
				else if (type.equals("time"))
					pos += encodeTime((java.util.Date)encode[i], bytes, pos);
				else if (type.equals("time#"))
					pos += encodeTime(((Long)encode[i]).longValue(), bytes, pos);
				else
					System.out.println("Unknown type \"" + type + "\"");
			}

//			System.out.println("Encoded " + (encode.length/2) + " values into " + pos + " bytes.");

			java.io.ByteArrayInputStream bytesIn = new java.io.ByteArrayInputStream(bytes);
			java.io.DataInputStream dataIn = new java.io.DataInputStream(bytesIn);

			pos=0;
			for (int i=0; i < encode.length; ++i)
			{
				String type = (String)encode[i++];
				Object val = encode[i];
				try
				{
					if (type.equals("boolean"))
					{
						boolean b = (dataIn.readByte() != 0x00);
						assertTrue("Expected boolean " + val + ", got " + b, b == ((Boolean)val).booleanValue());
					}
					else if (type.equals("byte"))
					{
						byte b = dataIn.readByte();
						assertTrue("Expected byte " + val + ", got " + b, b == ((Byte)val).byteValue());
					}
					else if (type.equals("short"))
					{
						short s = dataIn.readShort();
						assertTrue("Expected short " + val + ", got " + s, s == ((Short)val).shortValue());
							
					}
					else if (type.equals("int"))
					{
						int ii = dataIn.readInt();
						assertTrue("Expected int " + val + ", got " + ii, ii == ((Integer)val).intValue());
					}
					else if (type.equals("price"))
					{
						// Decoding of price isn't tested here.  Scaled integers are weird.
						//
						dataIn.skipBytes(8);
					}
					else if (type.equals("string"))
					{
						short len = dataIn.readShort();
						byte strBytes[] = new byte[len];
						dataIn.read(strBytes);
						String s = new String(strBytes);
						assertTrue("Expected \"" + val + "\", got \"" + s + "\"", s.equals((String)val));
					}
					else if (type.equals("time"))
					{
						int seconds = dataIn.readInt();
						int expect = (int)(((java.util.Date)val).getTime() / 1000);
						assertTrue("Expected " + expect + " seconds, got " + seconds, seconds == expect);
					}
					else if (type.equals("time#"))
					{
						int seconds = dataIn.readInt();
						int expect = (int)(((Long)val).longValue()/1000);
						assertTrue("Expected " + expect + " seconds, got " + seconds, seconds == expect);
					}
					else
						assertTrue("Unknown type \"" + type + "\"", false);
				}
				catch (java.io.IOException ex)
				{
					assertTrue("IOException at pos " + pos + ", for type " + type + " expecting value " + val + ex, false);
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
	 * @assume bytes.length >= offset + 1
	 * @return int number of bytes written to the byte array.
	 */
	public static int encodeBoolean(boolean b, byte[] bytes, int offset)
	{
		bytes[offset] = (byte)(b ? 1 : 0);
		return 1;	
	}
	/**
	 * @assume bytes.length >= offset + 1
	 * @return int number of bytes written to the byte array.
	 */
	public static int encodeByte(byte c, byte[] bytes, int offset)
	{
		bytes[offset] = (byte)c;
		return 1;
	}
	/**
	 * @assume bytes.length >= offset + 4
	 * @return int number of bytes written to the byte array.
	 */
	public static int encodeInt(int i, byte[] bytes, int offset)
	{
		for (int ii=3; ii>=0; --ii)
		{
			bytes[offset+ii] = (byte)(0xff & i);
			i >>= 8;
		}
		return 4;
	}
	/**
	 * Use the most precision available.  Note that this method does
	 * not currently attempt to encode scaling by 2^8 (ie, we only look
	 * for an optimal shift of the decimal place)
	 *
	 * @assume bytes.length >= offset + 8
	 * @return int number of bytes written to the byte array.
	 */
	public static int encodePrice(double price, byte bytes[], int offset)
	{
		short   highOrder = 0;
		int     significant = 0;
		byte    scale = 0;
		boolean valid = false;
		
		long l=0;
		
		if (price > (1L << 48) - 1)
		{
			// log error: "price " + price + " is too large to fit in an 8-byte scaled integer."
		}
		else
		{
			l = (long)(price);
			byte order = 0;
			for (order=0; l > 0; ++order)
				l /= 10;
			scale = (byte)(14 - order);
			
			double num = Math.round(price * Math.pow(10, scale));
			if (((long)Math.abs(num) & 0xffff000000000000L) != 0)
			{
				// log error - "price " + price + " is too large to store using " + numDecimals + " decimals."
			}
			else
			{
				l = ((long)num) & 0x0000ffffffffffffffL;
				valid = true;
			}
		}

		significant = (int)(l & 0xffffffff);
		highOrder = (short)((l>>32) & 0xffff);

		// Byte-encode the results
		//
		int len = encodeShort(highOrder, bytes, offset);
		len += encodeInt(significant, bytes, offset+len);
		len += encodeByte(scale, bytes, offset+len);
		len += encodeBoolean(valid, bytes, offset+len);
		return len;
	}
	/**
	 * @assume bytes.length >= offset + 2
	 * @return int number of bytes written to the byte array.
	 */
	public static int encodeShort(short i, byte[] bytes, int offset)
	{
		bytes[offset]   = (byte)(0xff & (i >> 8));
		bytes[offset+1] = (byte)(0xff & i);
		return 2;
	}
	/**
	 * @assume bytes.length >= offset + s.length() + 2
	 * @return int number of bytes written to the byte array.
	 */
	public static int encodeString(String s, byte[] bytes, int offset)
	{
		int len = encodeShort((short)s.length(), bytes, offset);
		for (int i=0; i < s.length(); ++i)
			bytes[offset+len+i] = (byte)s.charAt(i);
		return len + s.length();
	}
	/**
	 * @assume bytes.length >= offset + 4
	 * @return int number of bytes written to the byte array.
	 */
	public static int encodeTime(long millisSinceJan1970, byte bytes[], int offset)
	{
		return encodeInt((int)(millisSinceJan1970/1000), bytes, offset);
	}
	/**
	 * @assume bytes.length >= offset + 4
	 * @return int number of bytes written to the byte array.
	 */
	public static int encodeTime(java.util.Date time, byte bytes[], int offset)
	{
		return encodeInt((int)(time.getTime()/1000), bytes, offset);
	}
/**
 * Test the varuous encoding methods.  This is done by encoding values in
 * various formats into a byte array, and then decoding the values using a
 * DataInputStream, which will use network byte ordering, and compare the
 * decoded values against those which were originally encoded, thereby
 * verifying that the encoding was correct.
 *
 * Note that currently the verification of the decoding of scaled integers,
 * i.e. the encoding of Price doubles, is not verified here since I don't
 * know if the algorithm I use to encode it is correct.
 * 
 * @author Steven Sinclair
 * @param args java.lang.String[]
 */
public static void main(String args[])
{
	Test.main(args);
}
}
