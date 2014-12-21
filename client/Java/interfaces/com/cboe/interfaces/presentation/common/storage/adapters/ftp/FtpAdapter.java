//
// ------------------------------------------------------------------------
// FILE: FtpAdapter.java
//
// PACKAGE: com.cboe.presentation.common.storage
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.presentation.common.storage.adapters.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FtpAdapter
{
    void ascii() throws IOException;

    void binary() throws IOException;

    void cdUp() throws IOException;

    void closeServer() throws IOException;

    void noop() throws IOException;

    String pwd() throws IOException;

    int issueCommand(String command) throws IOException;

    void cd(String path) throws IOException;

    void openServer(String host, int port) throws IOException;

    void login(String user, String password) throws IOException;

    void rename(String oldName, String newName) throws IOException;

    String[] list() throws IOException;

    String[] list(String path) throws IOException;

    void mkdir(String pathName) throws IOException;

    void mkdirs(String pathName) throws IOException;

    InputStream get(String name) throws IOException;

    OutputStream put(String name) throws IOException;

    void delete(String name) throws IOException;

    boolean exists(String name) throws IOException;

    boolean isConnected() throws IOException;
}
