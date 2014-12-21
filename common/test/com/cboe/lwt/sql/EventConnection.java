package com.cboe.lwt.sql;


import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.driver.OracleDriver;


public final class EventConnection
{
    private static String           dbConnect = null;
    private static String           dbId      = null;
    private static String           dbPwd     = null;
    
    private static OracleConnection con       = null;
    

    public static synchronized OracleConnection getConnection()
        throws java.sql.SQLException
    {
        if ( con == null )
        {
            // Load properties
            
        
                dbConnect = System.getProperty( "DbConnect", "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=dotyl-nt)(PORT=1521)))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=LeeLocal.world)))" );  // oracle really needs to fix tnsnames.ora
                dbId      = System.getProperty( "DbId",      "dotyl" );  // TBD- remove defaults  
                dbPwd     = System.getProperty( "DbPwd",     "dotyl" );  // TBD- remove defaults    
        
            //  load jdbc driver
            
                java.sql.DriverManager.registerDriver( new OracleDriver() );
            
            // get a connection and configure it to commit immediately after execution
                con = (OracleConnection)java.sql.DriverManager.getConnection( dbConnect,   
                                                                              dbId, 
                                                                              dbPwd );
                                                                                
                con.setAutoCommit( true );
        }
        
        return con;
    }


}  // PrintConnection


