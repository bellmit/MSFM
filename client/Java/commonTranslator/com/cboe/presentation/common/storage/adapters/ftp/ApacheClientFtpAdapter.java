//
// ------------------------------------------------------------------------
// Source file: ApacheClientFtpAdapter.java
//
// PACKAGE: com.cboe.presentation.common.storage.adapters.ftp
//
// ------------------------------------------------------------------------
// Copyright (c) 2008 The Chicago Board Options Exchange. All Rights Reserved.
//
// ------------------------------------------------------------------------

package com.cboe.presentation.common.storage.adapters.ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter;

/**
 * <p>
 * This object bridges the Apache commons-net library FTP client implementation with our FtpAdapter interface.
 * </p>
 * 
 * @author Ryan Eccles
 * 
 */
public class ApacheClientFtpAdapter implements FtpAdapter {

    /**
     * This timeout value is based on the old Sun timeout value.
     */
    private static final int DEFAULT_TIMEOUT = 30000;

    /**
     * Local reference to the apache ftp client, delegate all commands to this guy
     */
    private FTPClient ftpClient;

    /**
     * Default constructor
     */
    public ApacheClientFtpAdapter() {
        super();
        ftpClient = new FTPClient();
        ftpClient.setDefaultTimeout(DEFAULT_TIMEOUT);

    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #ascii()
     */
    public void ascii() throws IOException {
        ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);
    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #binary()
     */
    public void binary() throws IOException {
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #cd(java.lang.String)
     */
    public void cd(String name) throws IOException {
        ftpClient.changeWorkingDirectory(name);
    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #cdUp()
     */
    public void cdUp() throws IOException {
        ftpClient.cdup();
    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #closeServer()
     */
    public void closeServer() throws IOException {
        ftpClient.disconnect();
    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #delete(java.lang.String)
     */
    public void delete(String name) throws IOException {
        ftpClient.deleteFile(name);
    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #exists(java.lang.String)
     */
    public boolean exists(String name) throws IOException {
        // if we can list without exception then the file exists
        try {

            // just be thorough with the return list
            String[] results = list(name);
            if (results == null || results.length == 0) {
                return false;
            }
            // otherwise
            return true;
        } catch (IOException ioe) {
            // default to false
        }
        return false;
    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #get(java.lang.String)
     */
    public InputStream get(String fileName) throws IOException {
        InputStream dest = ftpClient.retrieveFileStream(fileName);
        if (dest == null) {
            return null;
        }
        /*
         * we need to put this wrapper in so we ensure we complete the operation. This ensures the server gets a
         * termination flag, otherwise the server sits waiting for completion and will not accept new commands.
         */
        InputStream stream = new BufferedInputStream(dest) {
            boolean isClosed = false;

            @Override
            public void close() throws IOException {
                super.close();
                // We only can call this once per close so protect against double close() calls
                if (isClosed == false) {
                    isClosed = true;
                    ftpClient.completePendingCommand();
                }

            }
        };
        return stream;
    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #isConnected()
     */
    public boolean isConnected() throws IOException {
        return ftpClient.isConnected();
    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #issueCommand(java.lang.String)
     */
    public int issueCommand(String command) throws IOException {
        return ftpClient.sendCommand(command);
    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #list()
     */
    public String[] list() throws IOException {
        return list(".");
    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #list(java.lang.String)
     */
    public String[] list(String pathname) throws IOException {
        // we are not using the "getFiles" call because it does a whole lot more
        // work given that we just want the string values. This method is much
        // quicker.
        return ftpClient.listNames(pathname);
    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #login(java.lang.String,
     *      java.lang.String)
     */
    public void login(String user, String password) throws IOException {
        /*
         * The Apache client doesn't generate exceptions like the interface expects. Make it so.
         */
        if (ftpClient.login(user, password) == false) {
            throw new IOException("Not logged in");
        }

    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #mkdir(java.lang.String)
     */
    public void mkdir(String pathName) throws IOException {
        ftpClient.makeDirectory(pathName);
    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #mkdirs(java.lang.String)
     */
    public void mkdirs(String pathName) throws IOException {

        StringTokenizer tk = new StringTokenizer(pathName, "/", false);
        StringBuffer wholePath = new StringBuffer(pathName.length());
        String currentPwd = pwd();

        /*
         * walk down the tree making directories as we go. This is the way the ftp server expects the new dirs to be
         * built.
         */
        while (tk.hasMoreElements()) {
            String partialPath = (String) tk.nextElement();
            wholePath.append(partialPath);
            // optimistic build, much quicker then testing first, the server all ready tests
            mkdir(partialPath);
            cd(partialPath);
        }

        // go back to the start
        cd(currentPwd);
    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #noop()
     */
    public void noop() throws IOException {
        ftpClient.noop();
    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #openServer(java.lang.String, int)
     */
    public void openServer(String host, int port) throws IOException {
        ftpClient.connect(host, port);

        /*
         * Use active mode as the default mode to get around the Firewall aversion to passive mode. We need to place
         * this after the connect otherwise the mode will be reset.
         */
        ftpClient.enterLocalActiveMode();

    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #put(java.lang.String)
     */
    public OutputStream put(String name) throws IOException {

        OutputStream source = ftpClient.storeFileStream(name);
        if (source == null) {
            return null;
        }

        /*
         * we need to put this wrapper in so we ensure we complete the operation. This ensures the server gets a
         * termination flag, otherwise the server sits waiting for completion and will not accept new commands.
         */
        OutputStream stream = new BufferedOutputStream(source) {
            boolean isClosed = false;

            @Override
            public void close() throws IOException {
                super.close();
                // guard against double calls to close(), (too bad there wasn't an isClosed() method)
                if (isClosed == false) {
                    isClosed = true;
                    ftpClient.completePendingCommand();
                }
            }

        };
        return stream;
    }

    /**
     * returns the working directory
     */
    public String pwd() throws IOException {
        return ftpClient.printWorkingDirectory();
    }

    /**
     * @see com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter #rename(java.lang.String,
     *      java.lang.String)
     */
    public void rename(String oldName, String newName) throws IOException {
        // ensure we match the API expectations, throw exceptions on failed attempts
        if (ftpClient.rename(oldName, newName) == false) {
            throw new IOException("Could not rename file");
        }
    }

}

// EOF