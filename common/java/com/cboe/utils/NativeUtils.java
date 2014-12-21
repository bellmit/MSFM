package com.cboe.utils;

import java.io.InputStream;
import java.io.OutputStream;

public class NativeUtils
{
	public static boolean isWindows = System.getProperty("os.name").startsWith("Windows");

	public static String getCtimeString() { throw new UnsupportedOperationException(); }
	public static String getDateTimeString() { throw new UnsupportedOperationException(); }
	public static int getServByName(String name, String protocol) { throw new UnsupportedOperationException(); }
	public static String getServByPort(int port, String protocol) { throw new UnsupportedOperationException(); }

	public static String getEnvVar(String name)
    {
       return System.getenv( name );
    }

	public static long getPid()
    {
//     FIXME
//     for Windows, just return the value 1
       if ( isWindows ) {
           return 1L;
       }

       InputStream input = null;
       InputStream errStream = null;
       OutputStream output = null;
       Process p = null;
       try {
          p = Runtime.getRuntime().exec( "/bin/ksh" );
          input = p.getInputStream();
          errStream = p.getErrorStream();
          output = p.getOutputStream();
          output.write( "echo $PPID\n".getBytes() );
          byte[] buffer = new byte[ 16 ];
          output.close();
          output = null;
          int count = input.read( buffer );
          input.close();
          input = null;
          p.waitFor();
          int exitValue = p.exitValue();
          p = null;
          String pidString = new String( buffer, 0, count - 1 );
          int pid = Integer.parseInt( pidString );
          return pid;
       }
       catch ( Throwable t ) {
          throw new RuntimeException( "Unable to get process ID: " + t.toString() );
       }
       finally {
          try {
             if ( null != input ) {
                input.close();
             }
             if ( null != errStream ) {
                errStream.close();
             }
             if ( null != output ) {
                 output.close();
             }
             if ( null != p ) {
                p.destroy();
                p.waitFor();
                int exitValue = p.exitValue();
             }
          }
          catch ( Throwable th ) { }
       }
    }
}
