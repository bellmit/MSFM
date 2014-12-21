package com.cboe.application.cas;

/**
 *
 * A very simple Web Server used to server up the IOR for UserAccess.
 * Down the road, it can be modified to server up simple web pages
 * such as IDL documentation or CAS statistics as well.
 *
 * @author Jeff Illian
 */

import java.io.*;
import java.net.*;
import java.util.*;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.application.*;
import com.cboe.util.*;

public class WebServerHomeImpl extends ClientBOHome implements WebServerHome, Runnable {
    // Main listener thread for the Web Server
    private Thread listener;
    public final static String THREAD_NAME    = "CAS Web Server worker Thread";
    public final static String CAS_WEB_SERVER = "CAS Web Server";
    public final static String TCP_PORT       = "tcp_port";

    private String tcp_port;

    public WebServerHomeImpl() {
        super();

    };

    public WebServer create(int tcp_port) {
        WebServerImpl bo = new WebServerImpl(tcp_port);
        //Every bo object must be added to the container.
        addToContainer(bo);

        bo.create(String.valueOf(bo.hashCode()));

        bo.initialize();

        return bo;
    }

    public void clientInitialize()
        throws Exception
    {
        tcp_port = getProperty(TCP_PORT);
    }

    public void clientStart() {
        listener = new Thread(this, CAS_WEB_SERVER);
        listener.start();

        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
    }

    public void run() {
        try {
            ServerSocket ss = new ServerSocket(Integer.parseInt(tcp_port));

            while (true) {
                Socket s = ss.accept();
                WebServer ws = create(Integer.parseInt(tcp_port));
                ws.setSocket(s);
                (new Thread(ws, THREAD_NAME)).start();
                ws = null;
            }

        } catch (java.io.IOException ioex) {
            Log.exception(this, ioex);
        };
    };
};
