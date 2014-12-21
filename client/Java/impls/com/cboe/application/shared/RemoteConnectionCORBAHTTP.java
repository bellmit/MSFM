package com.cboe.application.shared;

import java.io.*;
import java.net.*;

public abstract class RemoteConnectionCORBAHTTP extends RemoteConnectionCORBA
{
    protected static String DEFAULT_IOR_PATH  = "http://localhost:80";
    protected static String DEFAULT_IOR_FILE  = "/UserAccess.ior";
	protected static String DEFAULT_INTERMARKET_IOR_FILE = "/IntermarketUserAccess.ior";
    protected static String DEFAULT_V2_IOR_FILE = "/UserAccessV2.ior";
    protected static String DEFAULT_V3_IOR_FILE = "/UserAccessV3.ior";
    protected static String DEFAULT_V4_IOR_FILE = "/UserAccessV4.ior";
    protected static String DEFAULT_V5_IOR_FILE = "/UserAccessV5.ior";
    protected static String DEFAULT_PCQS_IOR_FILE = "/UserAccessPCQS.ior";
    protected static String DEFAULT_FLOOR_IOR_FILE = "/UserAccessFloor.ior";
    protected static String DEFAULT_V6_IOR_FILE = "/UserAccessV6.ior";
    protected static String DEFAULT_V7_IOR_FILE = "/UserAccessV7.ior";
    protected static String DEFAULT_V8_IOR_FILE = "/UserAccessV8.ior";
    protected static String DEFAULT_V9_IOR_FILE = "/UserAccessV9.ior";


    /**
    * This method was created in VisualAge.
    * @return java.lang.Object
    */
   public Object find_initial_object( String pathName, String fileName )
   {
        Object corbaObject = null;
        URL casURL = null;

        try
        {
            StringBuilder sb = new StringBuilder(pathName.length()+fileName.length());
            sb.append(pathName).append(fileName);
            casURL = new URL(sb.toString());
            URLConnection conn = casURL.openConnection();

            //
            // Skip over the headers in the return message
            //

            String s = null;
            int i = 0;
            for(i=0; ; i++) {
                s = conn.getHeaderField(i);
                sb.setLength(0);
                sb.append("HTTP Header Field: ").append(s);
                System.out.println(sb.toString());
                if (s == null) break;
            }

            if (i == 0) {
                sb.setLength(0);
                sb.append("No CAS Found at IP Address: ").append(casURL.getHost()).append(" Port: ").append(casURL.getPort());
                String noCasFound = sb.toString();
                System.out.println(noCasFound);
                throw new org.omg.CORBA.OBJECT_NOT_EXIST(noCasFound);
            }
            else {
                sb.setLength(0);
                sb.append("RemoteConnectionCORBAHTTP opened connection to CAS URL: ").append(casURL.toString());
                System.out.println(sb.toString());
            }

            BufferedReader in  = new BufferedReader(
                new InputStreamReader((InputStream)conn.getContent()));

            String ior = in.readLine();

            if (ior != null) {
                corbaObject = string_to_object(ior);
            }

        }

        catch (org.omg.CORBA.OBJECT_NOT_EXIST noobj)
        {
            throw noobj;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new org.omg.CORBA.OBJECT_NOT_EXIST( "Error obtaining CAS :: " + e.toString() );
        }

        return corbaObject;
    }

    protected String getDefaultPath()
    {
        return DEFAULT_IOR_PATH;
    }
    protected String getDefaultName()
    {
        return DEFAULT_IOR_FILE;
    }
    protected String getDefaultIntermarketName()
    {
        return DEFAULT_INTERMARKET_IOR_FILE;
	}
    protected String getDefaultV2Name()
    {
        return DEFAULT_V2_IOR_FILE;
    }
    protected String getDefaultV3Name()
    {
        return DEFAULT_V3_IOR_FILE;
    }
    protected String getDefaultV4Name()
    {
        return DEFAULT_V4_IOR_FILE;
    }
    protected String getDefaultV5Name()
    {
        return DEFAULT_V5_IOR_FILE;
    }
    protected String getDefaultPCQSName()
    {
        return DEFAULT_PCQS_IOR_FILE;
    }
    protected String getDefaultFloorName()
    {
        return DEFAULT_FLOOR_IOR_FILE;
    }
    protected String getDefaultV6Name()
    {
        return DEFAULT_V6_IOR_FILE;
    }
    protected String getDefaultV7Name()
    {
        return DEFAULT_V7_IOR_FILE;
    }
    protected String getDefaultV8Name()
    {
        return DEFAULT_V8_IOR_FILE;
    }
    protected String getDefaultV9Name()
    {
        return DEFAULT_V9_IOR_FILE;
    }
}