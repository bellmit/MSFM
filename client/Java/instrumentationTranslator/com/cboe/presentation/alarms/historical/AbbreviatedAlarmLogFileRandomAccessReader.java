//
// -----------------------------------------------------------------------------------
// Source file: AbbreviatedAlarmLogFileRandomAccessReader.java
//
// PACKAGE: com.cboe.presentation.alarms.historical
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms.historical;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class AbbreviatedAlarmLogFileRandomAccessReader
{
    private RandomAccessFile file;

    public AbbreviatedAlarmLogFileRandomAccessReader(RandomAccessFile file)
    {
        if(file == null)
        {
            throw new IllegalArgumentException("file may not be null.");
        }
        this.file = file;
    }

    public String readLine() throws IOException
    {
        String aLine = null;

        StringBuilder input = new StringBuilder(200);
        int character = -1;
        boolean eol = false;

        while(!eol)
        {
            switch(character = read())
            {
                case -1:
                case '\u0002':
                    eol = true;
                    long newPos = getFilePointer() + 1;
                    if(newPos <= length())
                    {
                        seek(newPos);
                    }
                    break;
                default:
                    input.append((char) character);
                    break;
            }
        }

        if(character != -1 && input.length() > 0)
        {
            aLine = input.toString();
        }
        return aLine;
    }

    public void close() throws IOException
    {file.close();}

    public FileChannel getChannel() {return file.getChannel();}

    public FileDescriptor getFD() throws IOException
    {return file.getFD();}

    public long getFilePointer() throws IOException
    {return file.getFilePointer();}

    public long length() throws IOException
    {return file.length();}

    public int read() throws IOException
    {return file.read();}

    public int read(byte[] bytes) throws IOException
    {return file.read(bytes);}

    public int read(byte[] bytes, int off, int len) throws IOException
    {return file.read(bytes, off, len);}

    @SuppressWarnings({"BooleanMethodNameMustStartWithQuestion"})
    public boolean readBoolean() throws IOException
    {return file.readBoolean();}

    public byte readByte() throws IOException
    {return file.readByte();}

    public char readChar() throws IOException
    {return file.readChar();}

    public double readDouble() throws IOException
    {return file.readDouble();}

    public float readFloat() throws IOException
    {return file.readFloat();}

    public void readFully(byte[] bytes) throws IOException
    {file.readFully(bytes);}

    public void readFully(byte[] bytes, int off, int len) throws IOException
    {file.readFully(bytes, off, len);}

    public int readInt() throws IOException
    {return file.readInt();}

    public long readLong() throws IOException
    {return file.readLong();}

    public short readShort() throws IOException
    {return file.readShort();}

    public int readUnsignedByte() throws IOException
    {return file.readUnsignedByte();}

    public int readUnsignedShort() throws IOException
    {return file.readUnsignedShort();}

    public String readUTF() throws IOException
    {return file.readUTF();}

    public void seek(long pos) throws IOException
    {file.seek(pos);}

    public void setLength(long newLength) throws IOException
    {file.setLength(newLength);}

    public int skipBytes(int value) throws IOException
    {return file.skipBytes(value);}
}
