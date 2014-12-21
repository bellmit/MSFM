package com.cboe.application.shared;

import java.io.*;

public class RemoteConnectionOrbix extends RemoteConnectionCORBAHTTP
{
     /**
     * RemoteConnectionOrbix constructor comment.
     */
    public RemoteConnectionOrbix(String[] args) {
        java.util.Properties props = new java.util.Properties();
        props.put("org.omg.CORBA.ORBClass",
                "com.sun.CORBA.iiop.ORB");
        orb = org.omg.CORBA.ORB.init(args, props);
    }
}

