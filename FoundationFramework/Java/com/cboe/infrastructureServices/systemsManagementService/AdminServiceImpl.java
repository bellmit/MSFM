package com.cboe.infrastructureServices.systemsManagementService;

import java.util.Enumeration;
import java.util.Vector;

import junit.framework.TestCase;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.interfaces.adminService.Command;

import static com.cboe.infrastructureServices.foundationFramework.utilities.Log.information;
import static com.cboe.infrastructureServices.foundationFramework.utilities.Log.alarm;

/**
 * Implementation of the Admin Operations.
 * This is the server side implementation. An instance of this class will communicate directly with 
 * the CommandCallbackService to implement the behavior.
 * Client side references to the Admin service should use the AdminServiceClientImpl.
 * @author Dave Hoag
 * @version 1.1
 */
public class AdminServiceImpl extends AdminServiceBaseImpl
{
	private String MSG_HEAD = "AdminServiceImpl >> ";
	
	public AdminServiceImpl()
	{
		setSmaType("GlobalAdminService.AdminServiceImpl");
	}
	/**
	 * Delegate the request to the CommandCallbackService
	 * @see com.cboe.infrastructureServices.systemsManagementService.CommandCallbackService
	 */
	public void defineProperties(com.cboe.infrastructureServices.interfaces.adminService.Property[] nproperties)
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.MultipleExceptions
	{
		CommandCallbackService.getInstance().defineProperties(nproperties);
	}
	/**
	 * Delegate the request to the CommandCallbackService
	 * @see com.cboe.infrastructureServices.systemsManagementService.CommandCallbackService
	 */
	public void defineProperty(com.cboe.infrastructureServices.interfaces.adminService.Property property)
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidPropertyName, com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidPropertyValue, com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.UnsupportedProperty
	{
		CommandCallbackService.getInstance().defineProperty(property);
	}
	/**
	 * Delegate the request to the CommandCallbackService
	 * @see com.cboe.infrastructureServices.systemsManagementService.CommandCallbackService
	 */
	public boolean executeCommand(com.cboe.infrastructureServices.interfaces.adminService.CommandHolder command) 
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidParameter
	{
		information(MSG_HEAD + "executeCommand: " + command.value.name + ". " + command.value.description);
		
		return CommandCallbackService.getInstance().executeCommand(command.value);
	}
	/**
	 * Get the callback commands from the CommandCallbackService.
	 * @see com.cboe.infrastructureServices.systemsManagementService.CommandCallbackService
	 */
	public com.cboe.infrastructureServices.interfaces.adminService.Command[] getAllCommands()
	{
		Vector v = new Vector();
		Enumeration e = CommandCallbackService.getInstance().getCallbackNames();
		while(e.hasMoreElements())
		{
			String name = (String)e.nextElement();
			try
			{ 
				Command c = CommandCallbackService.getInstance().getCommand(name);
				v.addElement(c);
			}
			catch (Exception ex)
			{
				//The Command for which we are requesting is simply not part of the result set.
				//This is not a fatal exception
				alarm(MSG_HEAD + "AdminServiceImpl.getAllCommands. Failed to find callback command: " + name);
			}
		} 
		Command [] result = new Command [ v.size() ];
		v.copyInto(result);
		return result;
	}
	/**
	 * Get the command with the provided name.
	 * Delegate the request to the CommandCallbackService
	 * @see com.cboe.infrastructureServices.systemsManagementService.CommandCallbackService
	 */
	public com.cboe.infrastructureServices.interfaces.adminService.Command getCommand(String commandName)
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.UnsupportedCommand
	{
		return CommandCallbackService.getInstance().getCommand(commandName);
	}
	/**
	 * Get all of the properties registered with the CommandCallback service.
	 */
	public com.cboe.infrastructureServices.interfaces.adminService.Property[] getProperties()
	{
		Vector v = new Vector();
		Enumeration e = CommandCallbackService.getInstance().getPropertyNames();
		while(e.hasMoreElements())
		{
			String name = (String)e.nextElement();
			try
			{ 
				com.cboe.infrastructureServices.interfaces.adminService.Property c = CommandCallbackService.getInstance().getProperty(name);
				v.addElement(c);
			}
			catch (Exception ex)
			{
				//The Property for which we are requesting is simply not part of the result set.
				//This is not a fatal exception
				alarm(MSG_HEAD + "Failed to find property " + name);
			}
		} 
		com.cboe.infrastructureServices.interfaces.adminService.Property [] result = new com.cboe.infrastructureServices.interfaces.adminService.Property [ v.size() ];
		v.copyInto(result);
		return result;
	}
	/**
	 * Get the String value of the selected property.
	 */
	public String getPropertyValue(String propertyName)
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.PropertyNotFound, com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidPropertyName
	{
		com.cboe.infrastructureServices.interfaces.adminService.Property prop = CommandCallbackService.getInstance().getProperty(propertyName);
		return prop.value;
	}
    /**
     * Exercise the functionality of this class
     */
    public static class UnitTest extends TestCase
    {
        boolean verbose = System.getProperty("verbose", "false").equalsIgnoreCase("true") ;
        boolean invoked;
        public UnitTest(String methodName)
        {
            super(methodName);
        }

        public static void main(String [] args)
        {
            junit.textui.TestRunner.run(UnitTest.class);
        }
        public void invokeFromClient()
        {
            invoked = true;
        }
        public void testExportAndExec() throws Exception
        {
            invoked = false;
            System.getProperties().put("ORB.PortNum","4444");
            System.getProperties().put("ORB.OrbName","testAdminServiceImpl");
            System.getProperties().put("ORB.InitRefURL","initrefs.ior");

            java.util.Properties props = new java.util.Properties();
            props.put("n.adminServiceImpl", "com.cboe.infrastructureServices.systemsManagementService.AdminServiceImpl");
            props.put("n.traderServiceImpl", "com.cboe.infrastructureServices.traderService.TraderServiceImpl");
            props.put("n.orbServiceImpl", "com.cboe.infrastructureServices.orbService.OrbServicePOAImpl");
            props.put("n.logServiceImpl", "com.cboe.infrastructureServices.loggingService.LogServiceNullImpl");
            props.put("n.autoStart", "true");
            props.put("n.isMaster", "true");
            FoundationFramework.initializeForTest("n", null, props);
            CommandCallbackService svc = FoundationFramework.getInstance().getCommandCallbackService();
            svc.registerForCommandCallback(this, "adminServiceClientTest", "invokeFromClient", "", new String [0], new String [0] );

            //new com.cboe.infrastructureServices.systemsManagementService.AdminServiceClientImpl.UnitTest().clientTest();
            int retVal = launchClient().waitFor();
            if(retVal != 0) assertTrue("Client failed ", false);
            assertTrue("Method never invoked", invoked);

            FoundationFramework.getInstance().shutdownProcess();
        }
        private final Process launchClient() throws Exception
        {
            String [] clientCmd = { "java" ,"-mx200m", "-Dverbose=", "com.cboe.infrastructureServices.systemsManagementService.AdminServiceClientImpl$UnitTest",  "clientTest" };
            clientCmd [ 2 ] = "-Dverbose=" + verbose;

            Process process = Runtime.getRuntime().exec(clientCmd);
            java.io.InputStream in = process.getInputStream();
            java.io.DataInputStream din = new java.io.DataInputStream(in);
            new Thread("stdoutClient").start();
            java.io.InputStream errin = process.getErrorStream();
            java.io.DataInputStream errdin = new java.io.DataInputStream(errin);
            new Thread("stderrClient").start();
            return process;
        }
        /**
         * Read from the datainput stream and print the output with the provided prefix.
         */
        public static Runnable printStream(final java.io.DataInputStream din, final String prefix )
        {
            return new Runnable()
            {
                public void run()
                {
                    try
                    {
                        for(String line = din.readLine(); line != null; line = din.readLine())
                        {
                            System.out.println(prefix + line);
                        }
                    }
                    catch(Exception ex)
                    {
                        System.out.println(prefix + ex);
                        ex.printStackTrace();
                    }
                }
            };
        } 

    }
}

