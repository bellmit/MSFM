package com.cboe.domain.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

public class InputOutputStream
extends InputStream
{
    public
    InputOutputStream ()
    {
        mBuffer = new byte[64];
        mBufferLen = 0;
        mBufferLock = new Object();
    }

    public int
    available ()
    {
        synchronized (mBufferLock) {
            return mBufferLen;
        }
    }

    public int
    read ()
    throws IOException
    {
        byte[] b = new byte[1];
        if (read(b, 0, 1) < 1) {
            return -1;
        }
        return (int) b[0] & 0xff;
    }

    public int
    read (byte[] buf, int off, int len)
    throws IOException
    {

        synchronized (mBufferLock) {
            if (mBufferLen == 0) {
                while (mBufferLen == 0) {
                    synchronized (mLock) {
                        if (mClosed) {
                            break;
                        }
                    }

                    try {
                        mBufferLock.wait();
                    } catch (InterruptedException x) { }
                }

                if (mBufferLen == 0) {
                    synchronized (mLock) {
                        if (mClosed) {
                            throw new IOException("Stream closed.");
                        }
                    }
                }
            }

            // something in the buffer
            if (mBufferLen <= len) {
                System.arraycopy(mBuffer, 0, buf, off, mBufferLen);
                len = mBufferLen;
                mBufferLen = 0;
            } else {
                System.arraycopy(mBuffer, 0, buf, off, len);
                System.arraycopy(mBuffer, len, mBuffer, 0, mBufferLen - len);
                mBufferLen -= len;
            }

            return len;
        }
    }

    public void feed (byte[] data, int len)
    {
        synchronized (mBufferLock) {
            if (mBufferLen + len > mBuffer.length) {
                int newlen = 4 * mBuffer.length;
                while (mBufferLen + len > newlen) {
                    newlen *= 4;
                }
                byte[] newbuf = new byte[newlen];
                System.arraycopy(mBuffer, 0, newbuf, 0, mBufferLen);
                mBuffer = newbuf;
            }
            System.arraycopy(data, 0, mBuffer, mBufferLen, len);
            mBufferLen += len;
            mBufferLock.notifyAll();
        }
    }

    public void
    close ()
    {
        mClosed = true;
    }


    private byte[] mBuffer;
    private int mBufferLen;
    private Object mBufferLock = new Object();
    private Object mLock = new Object();
    private boolean mClosed;

}
