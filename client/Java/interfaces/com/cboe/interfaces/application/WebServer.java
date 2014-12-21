package com.cboe.interfaces.application;

import java.net.*;
import java.util.*;
/**
 * This extends the CORBA Interface into a CBOE Common standard
 * @author Jeff Illian
 */
public interface WebServer extends Runnable {

    public void setSocket(Socket s);

}
