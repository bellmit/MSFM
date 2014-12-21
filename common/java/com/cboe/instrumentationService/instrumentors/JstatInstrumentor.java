package com.cboe.instrumentationService.instrumentors;
/**
 * 
 * JstatInstrumentor
 * 
 * @author neher
 * 
 * Created: November 2 2006
 *
 * @version 1.0
 *
 */
public interface JstatInstrumentor extends Instrumentor {
    
    public static final String INSTRUMENTOR_TYPE_NAME = "JstatInstrumentor";

    public void setS0Capacity( double s0Capacity );
    public double getS0Capacity();

    public void setS0Utilization( double s0Utilization );
    public double getS0Utilization();
    
    public void setS1Capacity( double s1Capacity );
    public double getS1Capacity();

    public void setS1Utilization( double s1Utilization );
    public double getS1Utilization();
    
    public void setECapacity( double eCapacity );
    public double getECapacity();

    public void setEUtilization( double eUtilization );
    public double getEUtilization();

    public void setOCapacity( double oCapacity );
    public double getOCapacity();

    public void setOUtilization( double oUtilization );
    public double getOUtilization();

    public void setPCapacity( double pCapacity );
    public double getPCapacity();

    public void setPUtilization( double pUtilization );
    public double getPUtilization();

    public void setNbrYgGcs( long nbrYgGcs );
    public long getNbrYgGcs();

    public void setTimeYgGcs( double timeYgGcs );
    public double getTimeYgGcs();

    public void setNbrFullGcs( long nbrFullGcs );
    public long getNbrFullGcs();

    public void setTimeFullGcs( double timeFullGcs );
    public double getTimeFullGcs();

    public void setTimeYgFullGcs( double timeYgFullGcs );
    public double getTimeYgFullGcs();

	public void setTickFreq(long tickFreq);
	public long getTickFreq();

	public void setSafepointSyncTime(long safepointSyncTime);
	public long getSafepointSyncTime();

	public void setApplicationTime(long applicationTime);
	public long getApplicationTime();

	public void setSafepointTime(long safepointTime);
    public long getSafepointTime();

	public void setSafepoints(long safepoints);
    public long getSafepoints();
  
    public void get( JstatInstrumentor jstati );
}
