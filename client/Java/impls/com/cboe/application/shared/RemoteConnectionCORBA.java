package com.cboe.application.shared;

import java.io.*;
import com.cboe.loggingService.Log;
import com.cboe.ORBInfra.IOPImpl.IORImpl;

public abstract class RemoteConnectionCORBA implements RemoteConnection
{
    protected static String DEFAULT_IOR_PATH  = "";
    protected static String DEFAULT_IOR_FILE  = "ior.txt";
    protected static String DEFAULT_V2_IOR_FILE = "iorV2.txt";
    protected static String DEFAULT_V3_IOR_FILE = "iorV3.txt";
    protected static String DEFAULT_V4_IOR_FILE = "iorV4.txt";
    protected static String DEFAULT_V5_IOR_FILE = "iorV5.txt";
    protected static String DEFAULT_V6_IOR_FILE = "iorV6.txt";
    protected static String DEFAULT_V7_IOR_FILE = "iorV7.txt";
    protected static String DEFAULT_V8_IOR_FILE = "iorV8.txt";
    protected static String DEFAULT_V9_IOR_FILE = "iorV9.txt";
    protected static String DEFAULT_PCQS_IOR_FILE = "iorPCQS.txt";
    protected static String DEFAULT_FLOOR_IOR_FILE = "iorFloor.txt";
    protected static String DEFAULT_OMT_IOR_FILE = "iorOMT.txt";
    protected static String DEFAULT_INTERMARKET_IOR_FILE = "intermarketIor.txt";

    protected static String IOR_PATH = "IOR_PATH";
    protected static String IOR_FILE = "IOR_FILE";
    protected static String V2_IOR_FILE = "V2_IOR_FILE";
    protected static String V3_IOR_FILE = "V3_IOR_FILE";
    protected static String V4_IOR_FILE = "V4_IOR_FILE";
    protected static String V5_IOR_FILE = "V5_IOR_FILE";
    protected static String V6_IOR_FILE = "V6_IOR_FILE";
    protected static String V7_IOR_FILE = "V7_IOR_FILE";
    protected static String V8_IOR_FILE = "V8_IOR_FILE";
    protected static String V9_IOR_FILE = "V9_IOR_FILE";
    protected static String PCQS_IOR_FILE = "PCQS_IOR_FILE";
    protected static String FLOOR_IOR_FILE = "FLOOR_IOR_FILE";
    protected static String OMT_IOR_FILE = "OMT_IOR_FILE";
    protected static String INTERMARKET_IOR_FILE = "INTERMARKET_IOR_FILE";





    protected org.omg.CORBA.ORB orb;

    /**
     * find_initial_object
     *
     * This method reads the IOR from a file to establish the CORBA connection
     *
     * @author  Keith A. Korecky
     *
     * @param   pathName -  String containing path to IOR text file
     * @param   fileName -  String containing file name of text file WITHOUT PATH
     *
     */
    public Object find_initial_object( String pathName, String fileName )
    {
       try
       {
            FileInputStream fs = new FileInputStream(new File(pathName, fileName));
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            String ior = br.readLine();
            fs.close();
            return orb.string_to_object(ior);
        } catch (Exception e) {
            Log.debugException(this, e);
            throw new org.omg.CORBA.OBJECT_NOT_EXIST("Unable to locate CAS IOR : " + e.toString());
        }
    }

    /**
     * find_initial_object
     *
     * This method reads the IOR from a file to establish the CORBA connection
     * uses DEFAULT_IOR_PATH for file path
     *
     * @author  Keith A. Korecky
     *
     * @param   fileName -  String containing file name of text file WITHOUT PATH
     *
     */
    public Object find_initial_object( String fileName )
    {
         String pathName = getSystemProperty(IOR_PATH);
         if ( pathName == null )
         {
            pathName = getDefaultPath();
         }

        return( find_initial_object( pathName, fileName ) );
    }

    /**
     * find_initial_object
     *
     * This method reads the IOR from a file to establish the CORBA connection
     * uses DEFAULT_IOR_PATH for file path
     *
     * @author  Keith A. Korecky
     *
     *
     */
    public Object find_initial_intermarket_object()
    {
        String fileName = getSystemProperty(INTERMARKET_IOR_FILE);
        if (fileName == null)
        {
            fileName = getDefaultIntermarketName();
        }
        return( find_initial_object( fileName ) );
    }


    /**
     * find_initial_object
     *
     * This method reads the IOR from a file to establish the CORBA connection.
     * uses DEFAULT_IOR_FILE for fileName
     *
     * @author  Keith A. Korecky
     *
     */
    public Object find_initial_object()
    {
        String pathName = getSystemProperty(IOR_PATH);
        String fileName = getSystemProperty(IOR_FILE);

        if ( pathName == null )
        {
            pathName = getDefaultPath();
        }

        if ( fileName == null )
        {
            fileName = getDefaultName();
        }

       return( find_initial_object( pathName, fileName ) );
    }

    /**
     * Obtains the UserAccessV2 interface at the V2_IOR_FILE IOR.
     * @return UserAccessV2 object
     */
    public Object find_initial_V2_object()
    {
        String fileName = getSystemProperty(V2_IOR_FILE);
        if(fileName == null)
        {
            fileName = getDefaultV2Name();
        }
        return (find_initial_object(fileName));
    }

    /**
     * Obtains the UserAccessV2 interface at the V2_IOR_FILE IOR.
     * @return UserAccessV3 object
     */
    public Object find_initial_V3_object()
    {
        String fileName = getSystemProperty(V3_IOR_FILE);
        if(fileName == null)
        {
            fileName = getDefaultV3Name();
        }
        return (find_initial_object(fileName));
    }

    public Object find_initial_V4_object()
    {
        String fileName = getSystemProperty(V4_IOR_FILE);
        if(fileName == null)
        {
            fileName = getDefaultV4Name();
        }
        return find_initial_object(fileName);
    }

    public Object find_initial_V5_object()
    {
        String fileName = getSystemProperty(V5_IOR_FILE);
        if(fileName == null)
        {
            fileName = getDefaultV5Name();
        }
        return find_initial_object(fileName);
    }

    public Object find_initial_PCQS_object()
    {
        String fileName = getSystemProperty(PCQS_IOR_FILE);
        if(fileName == null)
        {
            fileName = getDefaultPCQSName();
        }
        return find_initial_object(fileName);
    }

    public Object find_initial_Floor_object()
    {
        String fileName = getSystemProperty(FLOOR_IOR_FILE);
        if(fileName == null)
        {
            fileName = getDefaultFloorName();
        }
        return find_initial_object(fileName);
    }

    public Object find_initial_OMT_object()
    {
        String fileName = getSystemProperty(OMT_IOR_FILE);
        if(fileName == null)
        {
            fileName = getDefaultOMTName();
        }
        return find_initial_object(fileName);
    }

    public Object find_initial_V6_object()
    {
        String fileName = getSystemProperty(V6_IOR_FILE);
        if(fileName == null)
        {
            fileName = getDefaultV6Name();
        }
        return find_initial_object(fileName);
    }

    public Object find_initial_V7_object()
    {
        String fileName = getSystemProperty(V7_IOR_FILE);
        if(fileName == null)
        {
            fileName = getDefaultV7Name();
        }
        return find_initial_object(fileName);
    }

    public Object find_initial_V8_object()
    {
        String fileName = getSystemProperty(V8_IOR_FILE);
        if(fileName == null)
        {
            fileName = getDefaultV8Name();
        }
        return find_initial_object(fileName);
    }

    public Object find_initial_V9_object()
    {
        String fileName = getSystemProperty(V9_IOR_FILE);
        if(fileName == null)
        {
            fileName = getDefaultV9Name();
        }
        return find_initial_object(fileName);
    }

    /**
     *
     * @author Jeff Illian
     *
     */
    public String object_to_string(Object object)
    {
        return orb.object_to_string((org.omg.CORBA.Object) object);
    }

    public Object register_object(Object obj)
    {
        try {
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object) obj;
            orb.connect(corbaObject);
            if (Log.isDebugOn())
            {
                Log.debug(this, obj + "-> " + object_to_string(corbaObject));
            }

            return corbaObject;
        } catch (Exception e) {
            Log.debugException(this, e);
        }
        return null;
    }

    public Object register_object(Object obj, String poaName)
    {
        return register_object(obj);
    }

    public void unregister_object(Object obj)
    {
        try {
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object) obj;
            orb.disconnect(corbaObject);
        } catch (Exception e) {
            Log.debugException(this, e);
        }
    }

     /**
     * This method was created in VisualAge.
     * @return java.lang.Object
     * @param ior java.lang.String
     */
    public Object string_to_object(String ior)
    {
        return orb.string_to_object(ior);
    }

    public void cleanupConnection(Object obj)
    {
    }

    private String getSystemProperty(String propertyName)
    {
        String theProperty = System.getProperty(propertyName);

        return theProperty;
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

    protected String getDefaultOMTName()
    {
        return DEFAULT_OMT_IOR_FILE;
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
    public String getHostname(Object obj)
    {
        String hostname = "unknown";
        return hostname;
    }

    public String getPort(Object obj)
    {
        String port = ":unknown";
        return port;
    }

    public Object setRoundTripTimeout(Object obj, int timeout)
    {
        return obj;
        // to be implemented per orb
    }


    public String getTypeId(Object obj)
    {
        IORImpl ior = ( (com.cboe.ORBInfra.ORB.DelegateImpl) ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate() ).getIOR();
        return ior.getTypeId();
    }

    public String getTypeId(String ior)
    {
        Object obj = RemoteConnectionFactory.find().string_to_object(ior);
        return this.getTypeId(obj);
    }
}

