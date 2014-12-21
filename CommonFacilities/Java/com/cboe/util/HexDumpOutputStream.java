package com.cboe.util;

import java.io.*;

/** 
 *  A utility class, useful for debugging purposes, will produce a
 *  traditionally formatted hex dump of a given byte array.
 *
 *  @author Steven Sinclair
 */
public class HexDumpOutputStream extends OutputStream
{
	/**
	 *  The default number of characters to print per line.
	 *  The number of actual characters will be four times 
	 *  this value: two hex chars, a space, and a literal char.
	 */
	public static final int DEFAULT_PRINT_WIDTH = 16;

	protected OutputStream underlying;
	protected int printWidth;
	protected boolean includePosition;
	protected int     position;

	protected StringBuffer currentLineHex;
	protected StringBuffer currentLineLiterals;

	/**
	 *  Create an instance of this output stream, writing to 
	 *  <code>underlying</code>, using the default line width.
	 *
	 *  @param underlying, the OutputStream to write to.
	 */
	public HexDumpOutputStream(OutputStream underlying)
	{
		this(underlying, DEFAULT_PRINT_WIDTH, true);
	}

	/**
	 *  Create an instance of this output stream, writing to 
	 *  <code>underlying</code>, using the given line width.
	 * 
	 *  @param underlying: the OutputStream to write to.
	 *  @param printWidth int: The number of actual characters will be four times 
	 *             this value: two hex chars, a space, and a literal char.
	 *  @param includePosition: if true, then a five-digit number will be written to
	 *  	       each line to indicate the position of the first byte in the line,
	 *			   relative to the first byte written via this stream.  If there are
	 *             more than 99999 line then "<<!>>" will be written
	 */
	public HexDumpOutputStream(OutputStream underlying, int printWidth, boolean includePosition)
	{
		this.underlying      = underlying;
		this.printWidth      = printWidth;
		this.includePosition = includePosition;
		position             = 0;
		currentLineHex       = new StringBuffer(printWidth*4);
		currentLineLiterals  = new StringBuffer(printWidth);
	}
	 
	/**
	 *  Append the given character to my internal representation of the
	 *  hex dump line.  If by adding this character the hex line represents
	 *  printWidth characters, then the line will be written to the underlying
	 *  stream.
	 *
	 *  @param ch the character to write.
	 */
	public void write(int ch) throws IOException
	{
		ch &= 0xff;
		if (ch < 0x10)
			currentLineHex.append('0');
		currentLineHex.append(Integer.toHexString(ch));
		currentLineHex.append(' ');
		if (ch >= 32 && ch < 127) 
			currentLineLiterals.append((char)ch);
		else 
			currentLineLiterals.append('.'); // unprintable chars

		if (currentLineLiterals.length() == printWidth)
		{
			writeCurrentLine();
		}
	}

	/**
	 * Write the currently represented line to the underlying output stream,
	 * filling in blanks for any missing characters.
	 */
	private void writeCurrentLine() throws IOException
	{
		if (currentLineHex.length() == 0)
			return;

		for (int i=currentLineLiterals.length(); i < printWidth; ++i)
		{
			currentLineHex.append("-- ");
			currentLineLiterals.append(' ');
		}
		currentLineHex.append(currentLineLiterals);
		currentLineHex.append('\n');
		if (includePosition)
		{
			if (position > 99999)
				underlying.write("<<!>>  ".getBytes());
			else 
			{
				int num=0;
				for (int p = position; p > 0; p /= 10, ++num)
				{
				}
				for (int i=num; i < 5; ++i)
				{
					underlying.write('0');
				}
				if (position > 0)
					underlying.write(Integer.toString(position).getBytes());
				underlying.write(' ');
				underlying.write(' ');
			}
		}
		underlying.write(currentLineHex.toString().getBytes());
		position += currentLineLiterals.length();
		currentLineHex.setLength(0);
		currentLineLiterals.setLength(0);
	}

	/**
	 *  Override the superclass's flush method.  Force the current line to
	 *  be printed even if it's incomplete, and flush the underlying.
	 */
	public void flush() throws IOException
	{
		writeCurrentLine();
		underlying.flush();
	}

	/**
	 *  Override the superclass's close method.  Call <code>flush()</code>, but
	 *  <b>don't</b> close the underlying.
	 */
	public void close() throws IOException
	{
		flush();
	}

	/**
	 *  Return the printWidth.  This value indicates the number of characters written
	 *  per line.  Each character will cause a two-digit hex number, a space, and a
	 *  single-character literal to be printed, so the <i>actual</i> number of characters
	 *  written per line will be 4 times this value.
	 *
	 *  @return int the printWidth value.
	 */
	public int getPrintWidth()
	{
		return printWidth;
	}

    /**
     *  Reset position to 0
     */
    public void resetPosition()
    {
        position = 0;
    }

	/**
	 *  A convenience method to write a hex dump of the given bytes to the OutputStream.
	 */
	public static void hexDump(byte[] bytes, int position, int length)
	{
		try
		{
			HexDumpOutputStream hexOut = new HexDumpOutputStream(System.out);
			hexOut.write(bytes, position, length);
			hexOut.close();
		}
		catch (IOException ex)
		{
			System.err.println("Error writing to System.out: " + ex);
		}
	}

	/**
	 *  A convenience method to write the given bytes to the given OutputStream in a hex dump format.
	 */
	public static void hexDump(byte[] bytes, int position, int length, OutputStream outputStream)
		throws IOException
	{
		HexDumpOutputStream hexOut = new HexDumpOutputStream(outputStream);
		hexOut.write(bytes, position, length);
		hexOut.close();
	}
    
    /**
     *  A convenience method to write the output to a String.
     */
    public static String hexDumpToString(byte[] bytes, int position, int length)
    throws IOException
{
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(512);    
    HexDumpOutputStream hexOut = new HexDumpOutputStream(outputStream);
    hexOut.write(bytes, position, length);
    hexOut.close();
    return outputStream.toString();
}
	 
    /**
	 * A test method.  Use the default printWidth and write the file passed to this method
	 * to System.out.
     */
    public static void main(String [] args)
    {
        try 
        {
            FileInputStream fin = new FileInputStream(args [0]);
            BufferedInputStream bin = new BufferedInputStream(fin);
            byte [] bytes = new byte [100 * DEFAULT_PRINT_WIDTH];
            int len;
            while((len = bin.read(bytes)) > -1)
			{
                hexDump(bytes, 0, len);
			}
        }
        catch (Exception e) { e.printStackTrace(); } 
    } 
}
