//
// ------------------------------------------------------------------------
// FILE: AbstractFileStorage.java
//
// PACKAGE: com.cboe.presentation.common.storage
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.common.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;

/**
 * @author torresl@cboe.com
 */
abstract public class AbstractFileStorage extends AbstractStorage
{
    protected static final String PREFERENCES_PROPERTY_SECTION_NAME       = "Preferences";
    protected static final String SAVE_ALLOWED_PROPERTY_KEY               = "SaveAllowed";

    protected Boolean saveAllowed;
	
    public static final int MAX_READ = 10000;

    public AbstractFileStorage()
    {
        super();
        initialize();
    }

    private void initialize()
    {
    }

    protected String getSaveAllowedPropertyKey()
    {
        return SAVE_ALLOWED_PROPERTY_KEY;
    }

    protected String readStream(InputStream inputStream)
            throws IOException
    {
        StringBuffer buffer = new StringBuffer(MAX_READ);
        byte[] bytes = new byte[MAX_READ];

        int eofMarker = 0;
        while (eofMarker != -1)
        {
            eofMarker = inputStream.read(bytes);
            if (eofMarker != -1)
            {
                buffer.append(new String(bytes,0,eofMarker));
            }
        }

        inputStream.close();
        return buffer.toString();
    }

    protected void writeStream(OutputStream outputStream, byte[] bytes)
            throws IOException
    {
        outputStream.write(bytes);
        outputStream.close();
    }

    protected void writeObject(ObjectOutputStream outputStream, Serializable content)
            throws IOException
    {
        outputStream.writeObject(content);
        outputStream.close();
    }

    protected Object readObject(ObjectInputStream inputStream)
            throws IOException
    {
        try
        {
            Object obj = inputStream.readObject();
            inputStream.close();
            return obj;
        }
        catch (ClassNotFoundException e)
        {
            GUILoggerHome.find().exception(e);
            IOException ioe = new IOException("Cannot find class for retrieved object.");
            ioe.initCause(e);
            throw ioe;
        }

    }

    public boolean isSaveAllowed()
    {
        if (saveAllowed == null)
        {
            saveAllowed = Boolean.FALSE;
            if( AppPropertiesFileFactory.isAppPropertiesAvailable() )
            {
                String value =
                        AppPropertiesFileFactory.find().getValue(PREFERENCES_PROPERTY_SECTION_NAME,
                                                                 getSaveAllowedPropertyKey());
                if( value != null && value.length() > 0 )
                {
                    saveAllowed = new Boolean(value);
                }
            }
        }
        return saveAllowed.booleanValue();
    }
    


    /**
     * <p>
     * Fast copy one stream to another. This method uses a buffer to copy the contents.
     * </p>
     * <p>
     * Solution from: <a href="http://thomaswabner.wordpress.com/2007/10/09/fast-stream-copy-using-javanio-channels"
     * >here</a>
     * </p>
     * 
     * @param src
     *            Source stream
     * @param dest
     *            Destination stream
     * @throws IOException
     */
    public static void copyStreams(final InputStream sourceStream, final OutputStream destinationStream)
            throws IOException {

        if (sourceStream == null || destinationStream == null) {
            throw new IOException("Cannot transfer null strings");
        }
        try {
            // build the channels to read from the buffers
            final ReadableByteChannel src = Channels.newChannel(sourceStream);
            final WritableByteChannel dest = Channels.newChannel(destinationStream);

            final ByteBuffer buffer = ByteBuffer.allocateDirect(MAX_READ);
            while (src.read(buffer) != -1) {
                // prepare the buffer to be drained
                buffer.flip();
                // write to the channel, may block
                dest.write(buffer);
                // If partial transfer, shift remainder down
                // If buffer is empty, same as doing clear()
                buffer.compact();
            }
            // EOF will leave buffer in fill state
            buffer.flip();
            // make sure the buffer is fully drained.
            while (buffer.hasRemaining()) {
                dest.write(buffer);
            }
        } catch (Throwable t) {
            /*
             * Generically catch all throwables and recast them to our io exception. This is for API consistency
             */
            throw new IOException("Could not copy streams:" + t.getMessage());
        }
    }

}
