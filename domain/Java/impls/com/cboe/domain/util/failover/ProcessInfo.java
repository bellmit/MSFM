package com.cboe.domain.util.failover;

/**
 * @author baranski
 *
 */
public class ProcessInfo{
    /**
     * @param p_orbName
     * @param p_pid
     * @param p_port
     */
    public ProcessInfo(String p_orbName, String p_pid, String p_port)
    {
        super();
        orbName = p_orbName;
        pid = p_pid;
        port = p_port;
    }
    
   
    @Override
    public boolean equals(Object p_obj)
    {
        if(!(p_obj instanceof ProcessInfo)){
            return false;
        }
        return (orbName.equals(((ProcessInfo)p_obj).getOrbName()) && pid.equals(((ProcessInfo)p_obj).getPid()) && port.equals(((ProcessInfo)p_obj).getPort()));
    }


    /**
     * @return
     */
    public String getOrbName()
    {
        return orbName;
    }
    /**
     * @param p_orbName
     */
    public void setOrbName(String p_orbName)
    {
        orbName = p_orbName;
    }
    /**
     * 
     */
    public ProcessInfo()
    {

    }
    /**
     * @return
     */
    public String getPort()
    {
        return port;
    }
    /**
     * @param p_port
     */
    public void setPort(String p_port)
    {
        port = p_port;
    }
    /**
     * @return
     */
    public String getPid()
    {
        return pid;
    }
    /**
     * @param p_pid
     */
    public void setPid(String p_pid)
    {
        pid = p_pid;
    }
    private String orbName;
    private String port;
    private String pid;


}

