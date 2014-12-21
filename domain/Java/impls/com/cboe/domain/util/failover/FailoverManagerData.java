package com.cboe.domain.util.failover;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import com.cboe.domain.util.InputOutputStream;
import com.cboe.domain.util.StreamLogger;
import com.cboe.domain.util.adminRequest.ArContext;
import com.cboe.domain.util.adminRequest.FastAdminInvoker;
import com.cboe.domain.util.remoteShell.Transport;
import com.cboe.domain.util.remoteShell.TransportFactory;

public class FailoverManagerData
{
    public String masterHost;
    public String masterUserId;
    public String masterPassword;
    public String slaveHost;
    public String slaveUserId;
    public String slavePassword;
    public Transport masterTransport;
    public Transport slaveTransport;
    public ArrayList<ProcessInfo> piAlSlave;
    public ArrayList<ProcessInfo> piAlMaster;
    public String[] processNames;
    public String pKeyFile;
    public ExecutorService tpe;
    public ExecutorService statusTpe;
    public PrintStream outputStream;
    public TransportFactory transportFactory;
    public InputOutputStream errorInputStream;
    public InputOutputStream infoInputStream;
    public StreamLogger errorLog;
    public StreamLogger infoLog;
    public boolean initialized;
    public ArContext slaveArContext;
    public ArContext masterArContext;

    public FailoverManagerData(ArrayList<ProcessInfo> p_piAlSlave,
            ArrayList<ProcessInfo> p_piAlMaster,
            TransportFactory p_transportFactory, InputOutputStream p_errorInputStream,
            InputOutputStream p_infoInputStream, boolean p_initialized)
    {
        piAlSlave = p_piAlSlave;
        piAlMaster = p_piAlMaster;
        transportFactory = p_transportFactory;
        errorInputStream = p_errorInputStream;
        infoInputStream = p_infoInputStream;
        errorLog = new StreamLogger(errorInputStream);
        infoLog = new StreamLogger(infoInputStream);        
        initialized = p_initialized;
        slaveArContext = new ArContext();
        masterArContext = new ArContext();
    }
}