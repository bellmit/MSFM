package com.cboe.util;

import java.io.*;
import junit.framework.*;

/**
 *  This class is identical to the ByteArrayInputStream, except that
 *  it allows the user to replace the underlying byte array at any point.
 *  This can be useful when we wish to minimize the number of streaming
 *  objects created.
 */
public class ReplaceableByteArrayInputStream extends ByteArrayInputStream
{
	/**
	 * Inner class for support of automated unit testing.
	 */
	static public class Test extends TestCase
	{
		public static TestSuite suite()
		{
			TestSuite result = new TestSuite();
			result.addTest(new Test("testStream"));
			return result;
		}

		public void testStream()
		{
			byte[][] testBytes =
			{
				{ 10, 100, -100 },
				{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 },
				{ 123, },
				{ 96 },
				{ 12, 12, 12 },
			};

			ReplaceableByteArrayInputStream bytesIn = new ReplaceableByteArrayInputStream(testBytes[0]);

			for (int i=0; i < testBytes.length; ++i)
			{
				int len = testBytes[i].length;
				if (i > 0)
					bytesIn.setBytes(testBytes[i], 0, testBytes[i].length);
				try
				{
					byte[] bytes = new byte[len];
					int num = bytesIn.read(bytes);
					assertTrue("Expected to read "+ len + " byte(s), got " + num, num==len);
					for (int j=0; j < bytes.length; ++j)
					{
						assertTrue("Didn't read the expected array for test array #" + i, bytes[j] == testBytes[i][j]);
					}
					num = bytesIn.read(bytes);
					assertTrue("Expected no more bytes to be available, not somehow read " + num + "byte(s).", num<0);
				}
				catch (IOException ex)
				{
					assertTrue("IOException while reading testBytes[" + i + "]: " + ex, false);
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
 * ReplaceableByteArrayInputStream constructor comment.
 * @param buf byte[]
 */
public ReplaceableByteArrayInputStream(byte[] buf)
{
	super(buf);
}
/**
 * ReplaceableByteArrayInputStream constructor comment.
 * @param buf byte[]
 * @param offset int
 * @param length int
 */
public ReplaceableByteArrayInputStream(byte[] buf, int offset, int length)
{
	super(buf, offset, length);
}
/**
 * Return the index of the next byte to be read.
 * 
 * @return int
 */
public int getPos()
{
	return this.pos;
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
/**
 *  Set the byte array that this input stream is managing to be something else.  This is a handy
 *  mechanism if you are mostly dealing with byte arrays, but want to manage it via a input stream.
 *  This way you don't have to create a new ByteArrayOutputStream and whatever other stream may wrap it:
 *  the setBytes() method can jsut be called with the new byte array: the ByteArrayInputStream's protected
 *  fields will be set accordingly.
 *
 *  @param bytes    The byte array containing the new bytes to use as the stream's source.
 *  @param offset   The index of the first byte in <code>bytes</code> to read.
 *  @param length   The number of bytes in  <code>bytes</code> to read.
 */
public void setBytes(byte[] bytes, int offset, int length)
{
	if (offset + length > bytes.length)
		throw new IllegalArgumentException("offset & length (" + offset + " & " + length + ") exceed byte array limits [0.." + bytes.length + "].");
	buf   = bytes;
	count = offset + length;
	mark  = offset;
	pos   = offset;
}
}
