package com.cboe.application.cas;

//Title:        WebServer Class that returns CAS IOR
//Version:
//Copyright:    Taken from Sun JavaSoft Example - About half the code was
//              rewritten - what wasn't should be.
//
//Author:       Jim Northey
//Company:      Chicago Board Options Exchange
//Description:  WebServer is a simple WebServer that is intended to
//              respond to an http request. It now returns the IOR for the
//              CAS. It is constructed with an IOR and a TCP Port number.
//
//              User must use a GET or HEAD request asking for a url of:
//              "/UserAccess.ior". The URL is not case sensitive.
//
//              There is a routine to send a file - it is not used at this
//              time. A better parser will be needed. The code from Sun
//              looks like "C" code. Someday RSN we should put a real http
//              message parser in here - possibly using the URLConnection
//              class.
//
//              This mini-server has been tested with a Web Browser and the
//              UserAccessLocator.java example that is provided to clients.
//
import com.cboe.idl.cmi.Version;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.interfaces.expressApplication.UserAccessV4Home;
import com.cboe.interfaces.application.*;
import com.cboe.interfaces.floorApplication.UserAccessFloorHome;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.StringTokenizer;

//
// WebServer is now constructed with an IOR String and a TCP Port Number
//

public class WebServerImpl extends BObject implements WebServer, Runnable, HttpConstants {
    private final static int BUF_SIZE = 1024;
    private final static String CAS_IOR_URL = "/UserAccess.ior";
    private final static String IM_CAS_IOR_URL = "/IntermarketUserAccess.ior";
    private final static String CAS_V2_IOR_URL = "/UserAccessV2.ior";
    private final static String CAS_V3_IOR_URL = "/UserAccessV3.ior";
    private final static String CAS_V4_IOR_URL = "/UserAccessV4.ior";
    private final static String PCQS_IOR_URL = "/UserAccessPCQS.ior";
    private final static String FLOOR_IOR_URL = "/UserAccessFloor.ior";
    private final static String OMT_IOR_URL = "/UserAccessOMT.ior";
    private final static String TMS_IOR_URL = "/UserAccessTMS.ior";
    private final static String CAS_V5_IOR_URL = "/UserAccessV5.ior";
    private final static String CAS_V6_IOR_URL = "/UserAccessV6.ior";
    private final static String CAS_V7_IOR_URL = "/UserAccessV7.ior";
    private final static String CAS_V8_IOR_URL = "/UserAccessV8.ior";
    private final static String CAS_V9_IOR_URL = "/UserAccessV9.ior";

    private final static String SIMULATOR_IOR_URL = "/simulator.ior";
    private final static String HTTP_HEADER = "HTTP/1.0 " + HTTP_OK + " OK";
    private final static String SERVER_TAG = "Server: ";
    private final static String SERVER_DATE = "date: ";
    private final static String CONTENT_TYPE = "Content-type: text/html";
    private final static String CONTENT_LENGTH = "Content-length: ";
    private final static String HTTP_ERROR = "HTTP/1.0 " + HTTP_NOT_FOUND;
    private final static int DEFAULT_TIMEOUT = 1000;


    private static final byte[] EOL = {(byte)'\r', (byte)'\n' };

    private int timeout;

      /* buffer to use for requests */
    byte[] buf;
      /* Socket to client we're handling */
    private Socket s;

    private int tcp_port;

    private String userAccessIor;

    private String imUserAccessIor;

    private String userAccessV2Ior;

    private String userAccessV3Ior;

    private String expressUserAccessIor;

    private String userAccessPCQSIor;

    private String userAccessFloorIor;
    private String userAccessOMTIor;
    private String userAccessTMSIor;

    private String userAccessV5Ior;
    private String userAccessV6Ior;
    private String userAccessV7Ior;
    private String userAccessV8Ior;
    private String userAccessV9Ior;

    private String serverTag;

    private boolean requestHandled;

    public WebServerImpl(int tcp_port) {
        super();
        this.tcp_port = tcp_port;
        buf = new byte[BUF_SIZE];
        s = null;
        userAccessIor = null;
        imUserAccessIor = null;
    };

    public void create(String name) {
        super.create(name);
    };

    public void initialize() {
        try {
            FoundationFramework ff = FoundationFramework.getInstance();

            ConfigurationService configService = ff.getConfigService();
            String fullName = getBOHome().getFullName();
            StringBuilder sb = new StringBuilder(fullName.length()+20);
            sb.append(fullName).append(".timeout");
            timeout = configService.getInt(sb.toString(), DEFAULT_TIMEOUT);

            sb.setLength(0);
            sb.append(SERVER_TAG).append(' ').append(fullName).append(' ').append(Version.CMI_VERSION);
            serverTag = sb.toString();

            UserAccessHome userAccessHome = (UserAccessHome)HomeFactory.getInstance().findHome(UserAccessHome.HOME_NAME);
            userAccessIor = userAccessHome.objectToString();

            UserAccessV2Home userAccessV2Home = (UserAccessV2Home)HomeFactory.getInstance().findHome(UserAccessV2Home.HOME_NAME);
            userAccessV2Ior = userAccessV2Home.objectToString();

            UserAccessV3Home userAccessV3Home = (UserAccessV3Home)HomeFactory.getInstance().findHome(UserAccessV3Home.HOME_NAME);
            userAccessV3Ior = userAccessV3Home.objectToString();

            UserAccessPCQSHome userAccessPCQSHome = (UserAccessPCQSHome)HomeFactory.getInstance().findHome((UserAccessPCQSHome.HOME_NAME));
            userAccessPCQSIor = userAccessPCQSHome.objectToString();
            
            UserAccessFloorHome userAccessFloorHome = (UserAccessFloorHome)HomeFactory.getInstance().findHome((UserAccessFloorHome.HOME_NAME));
            userAccessFloorIor = userAccessFloorHome.objectToString();

            UserAccessOMTHome userAccessOMTHome = (UserAccessOMTHome)HomeFactory.getInstance().findHome((UserAccessOMTHome.HOME_NAME));
            userAccessOMTIor = userAccessOMTHome.objectToString();

            UserAccessTMSHome userAccessTMSHome = (UserAccessTMSHome)HomeFactory.getInstance().findHome((UserAccessTMSHome.HOME_NAME));
            userAccessTMSIor = userAccessTMSHome.objectToString();

            UserAccessV5Home userAccessV5Home = (UserAccessV5Home)HomeFactory.getInstance().findHome((UserAccessV5Home.HOME_NAME));
            userAccessV5Ior = userAccessV5Home.objectToString();

            IntermarketUserAccessHome imUserAccessHome =
                    (IntermarketUserAccessHome)HomeFactory.getInstance().findHome(IntermarketUserAccessHome.HOME_NAME);
            imUserAccessIor = imUserAccessHome.objectToString();

            UserAccessV4Home userAccessV4Home = (UserAccessV4Home)HomeFactory.getInstance().findHome(UserAccessV4Home.HOME_NAME);
            expressUserAccessIor = userAccessV4Home.objectToString();
            
            UserAccessV6Home userAccessV6Home = (UserAccessV6Home)HomeFactory.getInstance().findHome((UserAccessV6Home.HOME_NAME));
            userAccessV6Ior = userAccessV6Home.objectToString();

            UserAccessV7Home userAccessV7Home = (UserAccessV7Home)HomeFactory.getInstance().findHome((UserAccessV7Home.HOME_NAME));
            userAccessV7Ior = userAccessV7Home.objectToString();
            
            UserAccessV8Home userAccessV8Home = (UserAccessV8Home)HomeFactory.getInstance().findHome((UserAccessV8Home.HOME_NAME));
            userAccessV8Ior = userAccessV8Home.objectToString();

            UserAccessV9Home userAccessV9Home = (UserAccessV9Home)HomeFactory.getInstance().findHome((UserAccessV9Home.HOME_NAME));
            userAccessV9Ior = userAccessV9Home.objectToString();

            Log.information (serverTag);

        } catch (Exception e) {
            Log.exception(this, e);
        };
    }

    public synchronized void setSocket(Socket s) {
        this.s = s;
        notify();
    };

    //
    //Stripped out all knowledge of the thread pool. Assume
    //Calling program will be in charge of managing the pool
    //of threads. See example program WebServerTest.java
    public synchronized void run() {
        requestHandled = false;
//        while(! requestHandled) {
//            if (s == null) {
//                /* nothing to do */
//                try {
//                    wait();
//                } catch (InterruptedException e) {
//                    /* should not happen */
//                    continue;
//                };
//            };
            try {
                handleClient();
                requestHandled = true;
            } catch (Exception e) {
                Log.exception(this, e);
            };
            /* go back in wait queue if there's fewer
            * than numHandler connections.
            */
            s = null;
//        };
    };

      //
      // method that handles the client request. Significant changes
      // from Sun's example. Did leave some of their parsing code (C stuff).
      //
    void handleClient() throws IOException {
        InputStream is = new BufferedInputStream(s.getInputStream());
        BufferedOutputStream os = new BufferedOutputStream(s.getOutputStream());

        /* we will only block in read for this many milliseconds
         * before we fail with java.io.InterruptedIOException,
         * at which point we will abandon the connection.
         */
        s.setSoTimeout(timeout);

        /* zero out the buffer from last time */
        for (int i = 0; i < BUF_SIZE ; i++) {
            buf[i] = 0;
        };

        try {
            /* We only support HTTP GET/HEAD, and don't
             * support any fancy HTTP options,
             * so we're only interested really in
             * the first line.
             */
            int nread = 0, r = 0;

        outerloop:
            while (nread < BUF_SIZE) {
                r = is.read(buf, nread, BUF_SIZE - nread);
                if (r == -1) {
                    /* EOF */
                    return;
                }
                int i = nread;
                nread += r;
                for (; i < nread; i++) {
                    if (buf[i] == (byte)'\n' || buf[i] == (byte)'\r') {
                        break outerloop;
                    }
                }
            }

            /* are we doing a GET or just a HEAD */
            boolean doingGet;
            /* beginning of file name */
            int index;
            if (buf[0] == (byte)'G' &&
                buf[1] == (byte)'E' &&
                buf[2] == (byte)'T' &&
                buf[3] == (byte)' ') {
                doingGet = true;
                index = 4;
            } else if (buf[0] == (byte)'H' &&
                buf[1] == (byte)'E' &&
                buf[2] == (byte)'A' &&
                buf[3] == (byte)'D' &&
                buf[4] == (byte)' ') {
                doingGet = false;
                index = 5;
            } else {
                /* we don't support this method */
                StringBuilder sb = new StringBuilder(45);
                sb.append("HTTP/1.0 ").append(HTTP_BAD_METHOD).append(" unsupported method type: ");
                String httpError = sb.toString();
                os.write(httpError.getBytes());
                os.write(buf, 0, 5);
                os.write(EOL);
                os.flush();
                s.close();
                return;
            };

            // Now we check if the URL is for the useraccess.ior
            String request = new String(buf,index,nread-index+1);

            request = (new StringTokenizer(request," \n\u0000",true)).nextToken();

            if (Log.isDebugOn())
            {
                Log.debug(this, "Request String = " + request);
            }

            byte[] ob = null;
            String ior = null;
            if (request.equalsIgnoreCase(CAS_IOR_URL)) {
                 ob = userAccessIor.getBytes();
                 ior = userAccessIor;
            } else if ( request.equalsIgnoreCase(IM_CAS_IOR_URL)) {
                     ob = imUserAccessIor.getBytes();
                     ior = imUserAccessIor;
            } else if ( request.equalsIgnoreCase(CAS_V2_IOR_URL)) {
                     ob = userAccessV2Ior.getBytes();
                     ior = userAccessV2Ior;
            } else if ( request.equalsIgnoreCase(CAS_V3_IOR_URL)) {
                     ob = userAccessV3Ior.getBytes();
                     ior = userAccessV3Ior;
            } else if ( request.equalsIgnoreCase(CAS_V4_IOR_URL)) {
                     ob = expressUserAccessIor.getBytes();
                     ior = expressUserAccessIor;
            } else if( request.equalsIgnoreCase(PCQS_IOR_URL)) {
                     ob = userAccessPCQSIor.getBytes();
                     ior = userAccessPCQSIor;
            } else if( request.equalsIgnoreCase(FLOOR_IOR_URL)) {
                     ob = userAccessFloorIor.getBytes();
                     ior = userAccessFloorIor;
            } else if( request.equalsIgnoreCase(OMT_IOR_URL)) {
                     ob = userAccessOMTIor.getBytes();
                     ior = userAccessOMTIor;
            } else if( request.equalsIgnoreCase(TMS_IOR_URL)) {
                ob = userAccessTMSIor.getBytes();
                ior = userAccessTMSIor;                  
            } else if( request.equalsIgnoreCase(CAS_V5_IOR_URL)) {
                ob = userAccessV5Ior.getBytes();
                ior = userAccessV5Ior;                  
            } else if( request.equalsIgnoreCase(CAS_V6_IOR_URL)) {
                ob = userAccessV6Ior.getBytes();
                ior = userAccessV6Ior;                  
            } else if( request.equalsIgnoreCase(CAS_V7_IOR_URL)) {
                ob = userAccessV7Ior.getBytes();
                ior = userAccessV7Ior;                  
            }else if( request.equalsIgnoreCase(CAS_V8_IOR_URL)) {
                ob = userAccessV8Ior.getBytes();
                ior = userAccessV8Ior;
            }else if( request.equalsIgnoreCase(CAS_V9_IOR_URL)) {
                ob = userAccessV9Ior.getBytes();
                ior = userAccessV9Ior;
            }else {
                /* we don't support this method */
                StringBuilder sb = new StringBuilder(HTTP_ERROR.length()+request.length()+1);
                sb.append(HTTP_ERROR).append(' ').append(request);
                String httpError = sb.toString();
                os.write(httpError.getBytes());
                os.write(EOL);
                os.flush();
                Log.medium(this, "Request Rejected:" + request + " From:" + s.getInetAddress());
                s.close();
                return;
            };

            os.write(HTTP_HEADER.getBytes());
            os.write(EOL);

            os.write(serverTag.getBytes());
            os.write(EOL);

            StringBuilder sb = new StringBuilder(50);
            sb.append(SERVER_DATE).append(new Date());
            String serverDate = sb.toString();
            os.write(serverDate.getBytes());
            os.write(EOL);

            String contentType = CONTENT_TYPE;
            os.write(contentType.getBytes());
            os.write(EOL);

            sb.setLength(0);
            sb.append(CONTENT_LENGTH).append(ior.length());
            String contentLength = sb.toString();
            os.write(contentLength.getBytes());
            os.write(EOL);
            os.write(EOL);

            // Writing the IOR Content

            os.write(ob);
//            os.write(EOL);
            os.flush();

            if (Log.isDebugOn())
            {
                Log.debug(this, "Request Handled. Sent:" + ior);
            }

        } finally {
            s.close();
        };
    };

    // Method not used at this time - unchanged from Sun Example

    void sendFile(File targ, PrintStream ps) throws IOException {
        InputStream is = null;
        ps.write(EOL);
        if (targ.isDirectory()) {
            /* here, we take advantage of the fact
             * that FileURLConnection will parse a directory
             * listing into HTML for us.
             */
            File ind = new File(targ, "index.html");
            if (ind.exists()) {
                is = new FileInputStream(ind);
            } else {
                URL u = new URL("file", "", targ.getAbsolutePath());
                is = u.openStream();
            }
        } else {
            is = new FileInputStream(targ.getAbsolutePath());
        }
        try {
            int n;
            while ((n = is.read(buf)) > 0) {
                ps.write(buf, 0, n);
            }
        } finally {
            is.close();
        }
    }

}

    // Unchanged from Sun Example

    interface HttpConstants {
        /** 2XX: generally "OK" */
        public static final int HTTP_OK = 200;
        public static final int HTTP_CREATED = 201;
        public static final int HTTP_ACCEPTED = 202;
        public static final int HTTP_NOT_AUTHORITATIVE = 203;
        public static final int HTTP_NO_CONTENT = 204;
        public static final int HTTP_RESET = 205;
        public static final int HTTP_PARTIAL = 206;

        /** 3XX: relocation/redirect */
        public static final int HTTP_MULT_CHOICE = 300;
        public static final int HTTP_MOVED_PERM = 301;
        public static final int HTTP_MOVED_TEMP = 302;
        public static final int HTTP_SEE_OTHER = 303;
        public static final int HTTP_NOT_MODIFIED = 304;
        public static final int HTTP_USE_PROXY = 305;

        /** 4XX: client error */
        public static final int HTTP_BAD_REQUEST = 400;
        public static final int HTTP_UNAUTHORIZED = 401;
        public static final int HTTP_PAYMENT_REQUIRED = 402;
        public static final int HTTP_FORBIDDEN = 403;
        public static final int HTTP_NOT_FOUND = 404;
        public static final int HTTP_BAD_METHOD = 405;
        public static final int HTTP_NOT_ACCEPTABLE = 406;
        public static final int HTTP_PROXY_AUTH = 407;
        public static final int HTTP_CLIENT_TIMEOUT = 408;
        public static final int HTTP_CONFLICT = 409;
        public static final int HTTP_GONE = 410;
        public static final int HTTP_LENGTH_REQUIRED = 411;
        public static final int HTTP_PRECON_FAILED = 412;
        public static final int HTTP_ENTITY_TOO_LARGE = 413;
        public static final int HTTP_REQ_TOO_LONG = 414;
        public static final int HTTP_UNSUPPORTED_TYPE = 415;

        /** 5XX: server error */
        public static final int HTTP_SERVER_ERROR = 500;
        public static final int HTTP_INTERNAL_ERROR = 501;
        public static final int HTTP_BAD_GATEWAY = 502;
        public static final int HTTP_UNAVAILABLE = 503;
        public static final int HTTP_GATEWAY_TIMEOUT = 504;
        public static final int HTTP_VERSION = 505;
    }

