package com.cboe.domain.util.failover;





public class SynchStatus
{
    public static final int IN_SYNCH = 0;
    public static final int OUT_OF_SYNCH = 1;
    public static final int ERROR = -3;
    public static final int TIMEOUT = -2;
    public static final int UNKNOWN_STATE = -1;
    
    private String orbName = null;
    private int inSynchState = UNKNOWN_STATE;
    private String lastSynchTimestamp = "";
    private int nbrOfSynchPointsMissed = 0;
    private String mismatchDetail = "";
    
    private static int tab1 = 40;
    private static int tab2 = 20;
    private static int tab3 = 10;
    
    private static final String header1 = "Process";
    private static final String header2 = "State";
    private static final String header3 = "Missed";
    private static final String header4 = "Timestamp";
    
    
    public SynchStatus()
    {
    }
    
    public String getOrbName()
    {
        return orbName;
    }
    
    public String toStateString(int state){
        switch(getInSynchState()){
            case SynchStatus.ERROR: return "ERROR";
            case SynchStatus.IN_SYNCH: return "IN_SYNCH";
            case SynchStatus.OUT_OF_SYNCH: return "OUT_OF_SYNCH";
            case SynchStatus.TIMEOUT: return "TIMEOUT" ;
            case SynchStatus.UNKNOWN_STATE: return "UNKNOWN_STATE"; 
            default: return "" + getInSynchState();
        }       
        
}
    
    public static String getPrintHeader(){
        StringBuilder sb = new StringBuilder();
        sb.append(header1);
        sb.append(fillSpace(tab1 - header1.length()));
        sb.append(header2);
        sb.append(fillSpace(tab2 - header2.length()));
        sb.append(header3);
        sb.append(fillSpace(tab3 - header3.length()));
        sb.append(header4);
        return sb.toString();
    }
    public String toString(){
        
        StringBuilder sb = new StringBuilder();
        sb.append(getOrbName());
        sb.append(fillSpace(tab1 - getOrbName().length()));
        sb.append(toStateString(getInSynchState()));
        sb.append(fillSpace(tab2 - toStateString(getInSynchState()).length()));
        sb.append(getNbrOfSynchPointsMissed());
        sb.append(fillSpace(tab3 - (getNbrOfSynchPointsMissed() + "").length()));
        sb.append(getLastSynchTimestamp());
        if(getInSynchState() != IN_SYNCH){
         sb.append("\n Mismatch details: ");
         sb.append(getMismatchDetail());
        }
        return sb.toString();
    }

    private static String fillSpace(int size){
        if(size <=0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i =0; i < size; i++){
            sb.append(" ");
        }
        return sb.toString();
    }
    
    public void setOrbName(String p_orbName)
    {
        orbName = p_orbName;
    }

    public int getInSynchState()
    {
        return inSynchState;
    }
    public void setInSynchState(int p_inSynchState)
    {
        inSynchState = p_inSynchState;
    }
    public String getLastSynchTimestamp()
    {
        return lastSynchTimestamp;
    }
    public void setLastSynchTimestamp(String p_lastSynchTimestamp)
    {
        lastSynchTimestamp = p_lastSynchTimestamp;
    }
    public int getNbrOfSynchPointsMissed()
    {
        return nbrOfSynchPointsMissed;
    }
    public void setNbrOfSynchPointsMissed(int p_nbrOfSynchPointsMissed)
    {
        nbrOfSynchPointsMissed = p_nbrOfSynchPointsMissed;
    }
    public String getMismatchDetail()
    {
        return mismatchDetail;
    }
    public void setMismatchDetail(String p_mismatchDetail)
    {
        mismatchDetail = p_mismatchDetail;
    }

    public void appendMismatchDetail(String p_string)
    {
        if(mismatchDetail == null){
            mismatchDetail = p_string;
        }
        else{
          mismatchDetail = mismatchDetail + p_string;   
        }
    }
    
}
