package com.cboe.domain.util.adminRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;

import com.cboe.LocalTransport.LocalProfile;
import com.cboe.ORBInfra.IIOPImpl.IIOPProfileImpl;
import com.cboe.ORBInfra.IOPImpl.IORImpl;
import com.cboe.ORBInfra.IOPImpl.ProfileNotPresent;
import com.cboe.ORBInfra.ORB.Orb;
import com.cboe.ORBInfra.PortableServer.POAObjectKey;
import com.cboe.ORBInfra.TIOP.TIOPProfileImpl;
import com.cboe.infrastructureServices.interfaces.adminService.Admin;
import com.cboe.infrastructureServices.interfaces.adminService.AdminHelper;
import com.cboe.infrastructureServices.interfaces.adminService.Command;
import com.cboe.infrastructureServices.interfaces.adminService.CommandHolder;
import com.cboe.infrastructureServices.interfaces.adminService.DataItem;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidParameter;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.UnsupportedCommand;

/**
 * 
 * @author baranski
 *
 */
public class FastAdminInvoker
{

    
    protected ORB orb;
    protected Admin admin;
    protected String hostName;
    protected int portNum;
    protected String orbName;
    protected Map<String,CommandHolder> cmdHolderMap;
    protected Map<String, String> unsupportedCmdMap;
    boolean timeout = false;
/**
 *     
 * @param p_hostName
 * @param p_portNum
 * @param p_orbName
 */
    public FastAdminInvoker(String p_hostName, int p_portNum, String p_orbName){
        hostName = p_hostName;
        portNum = p_portNum;
        orbName = p_orbName;
        cmdHolderMap = new ConcurrentHashMap();
        unsupportedCmdMap = new ConcurrentHashMap();
    }
   /**
    *  
    * @return
    */
    public String getOrbName()
    {
        return orbName;
    }
    /**
     * 
     * @param p_orbName
     */
    public void setOrbName(String p_orbName)
    {
        orbName = p_orbName;
    }
    /**
     * 
     * @throws Exception
     */
    public void initialize() throws Exception{
     
        orb = (com.cboe.ORBInfra.ORB.Orb)Orb.init();
        IORImpl ior = createIOR(hostName, portNum);
        String iorStr = ior.stringify();
        admin = AdminHelper.narrow(orb.string_to_object( iorStr ));
        
    }
    /**
     * Indicates if the last command execution resulted in a timeout
     * @return
     */
    public boolean isTimeout()
    {
        return timeout;
    }
    /**
     * 
     * @param p_timeout
     */
    public void setTimeout(boolean p_timeout)
    {
        timeout = p_timeout;
    }
    /**
     * 
     * @param cmdName
     * @param args
     * @throws UnsupportedCommand
     */
    public void addCommandHolder(String cmdName, String[] args) throws UnsupportedCommand{
        Command cmd = null;
        timeout = false;
        String key = createKey(cmdName, args);
        if(cmdHolderMap.get(key) != null){
          return; // already have this command set up  
        }
        try
        {
            cmd =  admin.getCommand(cmdName) ;
        }
        catch (UnsupportedCommand ex)
        {
            // Just the suffix of the command may have been provided:
            //
            Command[] cmds = admin.getAllCommands();
            String suffix = "." + cmdName;
            
            for (int i=0; i < cmds.length; i++)
            {
                if (cmds[i].name.endsWith(suffix))
                {
                    cmd = cmds[i] ;
                }
            }
        }
        if(cmd == null){
         throw new  UnsupportedCommand("Command: " + cmdName + " is not found in the admin service");  
        }
         
        cmd.args = new DataItem[cmd.args.length];
        for (int i=0; i < cmd.args.length; i++)
        {
            cmd.args[i] = new DataItem();
            cmd.args[i].name = "";
            cmd.args[i].type = "java.lang.String";
            cmd.args[i].value = (i<args.length) ? args[i] : "";
            cmd.args[i].description = "";
        }
        CommandHolder cmdHolder = new CommandHolder(cmd);
        this.cmdHolderMap.put(key,cmdHolder);
    }
    /**
     * @param cmdName
     * @param args
     * @return
     */
    private String createKey(String cmdName, String[] args)
    {
        String key = null;
        StringBuffer keyBuffer = new StringBuffer(cmdName.trim());
        for(int cnt = 0; cnt<args.length; cnt++){
          keyBuffer.append(args[cnt]);
        }
        key = keyBuffer.toString();
        return key;
    }
   
    /**
     * 
     * @param p_p_args 
     * @param p_p_cmd 
     * @return
     */
    public CommandHolder getCmdHolder(String p_cmd, String[] p_args)
    {
        return cmdHolderMap.get(createKey(p_cmd, p_args));
    }
    /**
     * 
     * @return
     * @throws InvalidParameter
     * @throws UnsupportedCommand 
     */
    public String invoke(String p_cmd, String[] p_args) throws InvalidParameter, UnsupportedCommand{
        timeout = false;
        CommandHolder cmdHolder = getCmdHolder(p_cmd, p_args);
        if(cmdHolder == null){
            addCommandHolder(p_cmd, p_args);
            cmdHolder = getCmdHolder(p_cmd, p_args);
        }
        boolean success = admin.executeCommand(cmdHolder);   
      
      if (!success)
      {
          throw new FastAdminInvokerException("Command: " + cmdHolder.value.name + " failed to execute");
      }
     StringBuffer sb = new StringBuffer();
     switch (cmdHolder.value.retValues.length)
    
      {
          case 0: break;
          case 1: sb.append(cmdHolder.value.retValues[0].value); break;
          default:
              for (int i=0; i < cmdHolder.value.retValues.length; i++)
              {
                  sb.append(cmdHolder.value.retValues[i].name + " = "
                      + cmdHolder.value.retValues[i].value);
              }
      }
     return sb.toString();
    }
    /**
     * 
     * @param hostName
     * @param portNum
     * @return
     * @throws Exception
     */
    private  IORImpl createIOR(String hostName, int portNum) throws Exception {
        IORImpl ior = null;
        try {
            org.omg.CORBA.Object myCC = orb.resolve_initial_references("CommandConsole");
            ior = ( (com.cboe.ORBInfra.ORB.DelegateImpl) ((org.omg.CORBA.portable.ObjectImpl) myCC)._get_delegate() ).getIOR().copy();
            ior.removeProfile( LocalProfile.tag );
            IIOPProfileImpl iProfile = (IIOPProfileImpl)ior.getProfile( new Integer(org.omg.IOP.TAG_INTERNET_IOP.value) );
            iProfile.setHost( hostName );
            iProfile.setPort( (short)portNum );
            POAObjectKey key = POAObjectKey.createObjectKey( new String[] {"AdminServicePOA"}, "AdminServiceObject".getBytes(), new byte[] {} );
            synchronized(key){
                iProfile.setObjectKey( key );
            }
            
            try {
                ior.removeProfile( TIOPProfileImpl.tag );
            } catch( ProfileNotPresent e ) {
                // Don't worry about this.
            }
        }
        catch ( Exception e ) {
            System.out.println( "Exception getting command console IOR: " +
                    "HostName(" + hostName + ") " +
                    "PortNum(" + portNum + ").  ");         
            e.printStackTrace(System.out);
            throw e;
        }
        catch (ProfileNotPresent e)
        {
            throw new Exception(e.getMessage());
        }
        ior.setTypeId("IDL:adminService/Admin:1.0");
        return ior;
    }
    /**
     * 
     * @param processName
     * @return
     */
    protected Admin findAdminService(String processName)
    {
      return admin;   
    }
    /**
     * 
     * @author baranski
     *
     */
    public static class FastAdminInvokerException extends RuntimeException
    {
        public FastAdminInvokerException(String msg)
        {
            super(msg);
        }
    }
    
    /**
     * 
     * @param p_cmd
     * @param p_arguments
     */
    public void setUnsupportedCommand(String p_cmd, String[] p_arguments)
    {
        String key = createKey(p_cmd, p_arguments);
        unsupportedCmdMap.put(key, key);
    }
    
    public boolean isKnownUnsupportedCommand(String p_cmd, String[] p_arguments){
        String key = createKey(p_cmd, p_arguments);
        return (unsupportedCmdMap.get(key) != null);
    }
    
    public static void main(String[] args){
        if(args.length < 4){
            System.out.println("Needs at least 4 arguments");
            System.exit(1);
        }
        
        ExecutorService tpe = Executors.newFixedThreadPool(1);
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        int timeout = Integer.parseInt(args[2]);
        final String command = args[3];
        String arArguments = "";

        String[] tempArgArray = new String[args.length - 4];  
        for (int i =4; i < args.length; i++){
            if(arArguments.length() > 0){
                arArguments = arArguments + "," + args[i];
            }else{
                arArguments = args[i];
            }
            tempArgArray[i - 4] = args[i];
        }
    final String[] argArray = tempArgArray;        
        System.out.println("Executing ar command: " + command + "(" +  arArguments + ")" + " on " + host + ":" + port);
        final FastAdminInvoker fai = new FastAdminInvoker(host, port, "foo");
        try
        {
            fai.initialize();
            Future fut = tpe.submit(new Runnable() {
                public void run()
                {
                    try
                    {
                        System.out.println(fai.invoke(command, argArray));
                    }
                    catch (SystemException se){
                        System.out.println("PROCESS IS DOWN");  
                    }
                    catch (UnsupportedCommand uc){
                        System.out.println("COMMAND: " + command + " IS NOT SUPPORTED BY THIS PROCESS");
                    }
                    catch (Exception e){
                        System.out.println("Exception occured: " + e.getMessage());
                        StackTraceElement[] ste = e.getStackTrace();
                        for(StackTraceElement st:ste){
                          String element = "at " + st.toString() + "\n";
                          System.out.print(element);
                        }
                    }
                }
            });
            if(timeout > 0){
              fut.get(timeout, TimeUnit.SECONDS);
            }else{
                fut.get();   
            }
            System.exit(0);
        }
        catch (TimeoutException te){
          System.out.println("TIMEOUT");
        }
       
        catch (Exception e){
            System.out.println("Exception occured: " + e.getMessage());
            StackTraceElement[] ste = e.getStackTrace();
            for(StackTraceElement st:ste){
              String element = "at " + st.toString() + "\n";
              System.out.print(element);
            }
        }
        System.exit(1);
    }
  }
