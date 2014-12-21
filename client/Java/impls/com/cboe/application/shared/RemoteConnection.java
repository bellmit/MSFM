package com.cboe.application.shared;

public interface RemoteConnection {
    public Object find_initial_object();

    public Object find_initial_intermarket_object();

    public Object find_initial_V2_object();

    public Object find_initial_V3_object();

    public Object find_initial_V4_object();

    public Object find_initial_V5_object();

    public Object find_initial_PCQS_object();

    public Object find_initial_Floor_object();

    public Object find_initial_OMT_object();

    public Object find_initial_object( String fileName );

    public Object find_initial_object( String pathName, String fileName );

    public String object_to_string(Object object);

    public Object string_to_object(String ior);

    public Object register_object(Object obj);

    public Object register_object(Object obj, String poaName);

    public void unregister_object(Object obj);

    public void cleanupConnection(Object obj);

    public String getHostname(Object obj);

    public String getPort(Object obj);

    public Object setRoundTripTimeout(Object obj, int timeout);

    public String getTypeId(Object obj);

    public String getTypeId(String ior);

    public Object find_initial_V6_object();

    public Object find_initial_V7_object();
    
    public Object find_initial_V9_object();
}
