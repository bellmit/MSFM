package com.cboe.instrumentationService.instrumentors;

/**
 * 
 * OutlierInstrumentor
 * 
 * @author beaudry (cloned from JmxInstrumentor by neher)
 * 
 * Created: Wed Oct 18 2006
 *
 * @version 1.0
 *
 */
public interface OutlierInstrumentor extends Instrumentor {

    public static final String INSTRUMENTOR_TYPE_NAME = "OutlierInstrumentor";

    public void setMethodName( String methodName );
    public String getMethodName();
    
    public void setMachine( String machine );
    public String getMachine();
    
    public void setProcess( String processStr );
    public String getProcess();
    
    public void setTimeOutValue( String timeOutValue );
    public String getTimeOutValue();
    
    public void setActualDuration( long actualDuration );
    public long getActualDuration();
    
    public void setTimeStamp( long timeStamp );
    public long getTimeStamp();
    
    public void setClassKey( long classKey );
    public long getClassKey();
    
    public void setBlockSize( int blockSize );
    public int getBlockSize();
    
    public void setSessionNumber (byte sessionNumber);
    public byte getSessionNumber();
    
    public void get( OutlierInstrumentor outlieri );

}
