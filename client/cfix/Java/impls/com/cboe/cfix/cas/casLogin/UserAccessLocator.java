package com.cboe.cfix.cas.casLogin;

import java.net.*;
import java.io.*;

public class UserAccessLocator {

  String ipAddress;
  int tcpPortNumber;
  String iorFileName;

  final String IOR_REFERENCENAME = "/UserAccess.ior";
  final String DEFAULT_CAS_IP = "localhost";
  final int DEFAULT_CAS_TCP_PORT = 80;

  String httpResponse; // Contains the HTTP Header information returned
                       // by the Server

  /**
   *  Construct a UserAccessLocator Object
   *
   * @param ipAddress The IP Address for the CAS
   * @param tcpPortNumber The Port Number of the HTTP Server on the CAS
   * @param iorFileName The Requested IOR File name
   *
   */
  public UserAccessLocator(String ipAddress, int tcpPortNumber, String iorFileName) {
    this.ipAddress = ipAddress;
    this.tcpPortNumber = tcpPortNumber;
    this.iorFileName = iorFileName;
  }

  /**
   *  Construct a UserAccessLocator Object
   *
   * @param ipAddress The IP Address for the CAS
   * @param tcpPortNumber The Port Number of the HTTP Server on the CAS
   *
   */
  public UserAccessLocator(String ipAddress, int tcpPortNumber) {
    this.ipAddress = ipAddress;
    this.tcpPortNumber = tcpPortNumber;
    this.iorFileName = IOR_REFERENCENAME;

  }
  /**
   *  Construct a UserAccessLocator Object using a default
   *  IP Address of DEFAULT_CASIP and TCP Port number of DEFAULT_CASTPCPORT.
   *
   */
  public UserAccessLocator() {
         this.ipAddress = DEFAULT_CAS_IP;
         this.tcpPortNumber = DEFAULT_CAS_TCP_PORT;
         this.iorFileName = IOR_REFERENCENAME;
  }
  /**
   * Obtain the IOR for the UserAccess object of the CAS using the HTTP protocol
   *
   */
  public String obtainIOR() throws com.cboe.exceptions.CommunicationException {

      String ior;

      int numFields = 0;

      URLConnection conn;

      StringBuffer httpRespBfr = new StringBuffer();

      try {
        URL url = new URL("http",ipAddress,tcpPortNumber,iorFileName);

        conn = url.openConnection();

        //
        // Skip over the headers in the return message
        // Save the headers in the event the user wants
        // to access them
        //

        String s = null;
        for(numFields=0; ; numFields++) {
            s = conn.getHeaderField(numFields);
            if (s == null) break;
            httpRespBfr.append(s);
            httpRespBfr.append("\n");
        }

        httpResponse = httpRespBfr.toString();

        if (numFields == 0) {
           com.cboe.exceptions.CommunicationException e = new com.cboe.exceptions.CommunicationException();
           e.details = new com.cboe.exceptions.ExceptionDetails();
           e.details.message = "No CAS Found at IP Address: "+ipAddress+ " Port: "+tcpPortNumber;
           //e.details.dateTime =  DateTimeHelper.dateTimeStructToString(DateTimeHelper.makeDateTimeStruct(new Date()));
           e.details.error = 9000;
           e.details.severity = 1;
           throw e;
        }

        BufferedReader in  = new BufferedReader(
              new InputStreamReader((InputStream)conn.getContent()));

        ior = in.readLine();

      }
      catch(java.io.IOException e) {
           com.cboe.exceptions.CommunicationException cmiexception = new com.cboe.exceptions.CommunicationException();
           cmiexception.details = new com.cboe.exceptions.ExceptionDetails();
           cmiexception.details.message = e.getMessage();
           //cmiexception.details.dateTime = DateTimeHelper.dateTimeStructToString(DateTimeHelper.makeDateTimeStruct(new Date()));
           cmiexception.details.error = 9000;
           cmiexception.details.severity = 1;
           throw cmiexception;
      }


      return ior;
  }
  /**
   * Return the HTTP Response that was obtained from the CAS Server
   *
   */
  public String getHttpResponse() {
        return httpResponse;
  }

  /**
   * Provide a way for the user to query what IP address was used to access the CAS
   */
  public String getCASIPAddress() {
        return ipAddress;
  }
  /**
   * Provie a way for the user to query what TCP Port number was used to access the CAS
   */
  public int getTCPPortNumber() {
        return tcpPortNumber;
  }
}
