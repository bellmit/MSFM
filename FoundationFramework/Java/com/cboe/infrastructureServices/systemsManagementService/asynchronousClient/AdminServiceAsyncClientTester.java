package com.cboe.infrastructureServices.systemsManagementService.asynchronousClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.TreeMap;
import org.omg.CORBA.UserException;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationServiceFileImpl;

public class AdminServiceAsyncClientTester {
	public static String END_OF_TEST = "end_of_test";
	public static char[] promptS = (new String("AdminServiceAsyncClientTester > ")).toCharArray();	
	
	private String commandFile;
	private boolean interactive = true;
	private TreeMap<String,Command> commandMap;
	private AdminServiceClientAsync service;

	public AdminServiceAsyncClientTester(boolean aInteractive, String aCommandFile)
	{		
		commandFile = aCommandFile;
		interactive = aInteractive;
		buildCommandMap();
	}
	
	/**
	 * Expect the followings:
	 * 1. if there is no argument, the tester will run in interactive mode.
	 * 2. args[0]: if run the tester in a non-interactive mode, the name of the file 
	 *    which contains the commands to be executed. 
	 */
	public static void main(String[] args) {
		AdminServiceAsyncClientTester tester;
		if (args.length == 1){
			tester = new AdminServiceAsyncClientTester(true, null); 
		}
		else {
			tester = new AdminServiceAsyncClientTester(false, args[1]);
		}
		tester.startTest(args);
	}
		
	public void startTest(String[] args)
	{
		initializeFF(args);
		if (interactive){
			doInteractiveTest();
		}
		else {
			doBatchTest();
		}
	}

	private void initializeFF(String[] args)
	{
	    FoundationFramework ff = FoundationFramework.getInstance();
		try
		{
			System.out.println("Initializing FF...");			
	        ConfigurationService configService = new ConfigurationServiceFileImpl();
	        configService.initialize(args, 0);
			ff.initialize("AdminServiceAsyncClientTester", configService);
	
			service = AsynchronousAdminClient.getInstance(); 
			
			System.out.println("Initialized FF");
		}
		catch(Exception e) {
			System.out.println("Exception when initializing FF. Shutdown...");
			e.printStackTrace();
		}
	}		

	private void doInteractiveTest(){
		while (true) {
			CommandPrompt cp = new CommandPrompt();
			String commandLine = cp.read();
			if (commandLine == END_OF_TEST) {
				commandMap.get("quit").execute();
			}
			else {
				try {
					Command command = validateCommand(commandLine);
					if (command != null ){
						command.execute();
					}
				}
				catch(IllegalArgumentException e1) {
					//do nothing here. Usage is already printed out by the commands.
				}
				catch(Exception e){
					e.printStackTrace();
					System.out.println("The exception happens when processing request. Try again");
				}
			}
		}
	}
	
	private void doBatchTest(){
		//TODO
	}
	
	private Command validateCommand(String commandLine){
		Command command = null;
		Command template = null;
		if (commandLine != null && commandLine.length() != 0) {			
			String[] cArray = commandLine.split(" ");
			if (cArray.length > 0) {
				template = commandMap.get(cArray[0]);
				if (template != null) {
					command = template.cloneCommand();
					command.initialize(cArray);
				}
				else {
					System.out.println("Command: " + cArray[0] + " is not a valid command");
					command = commandMap.get("help");						
				}				
			}
		}
		return command;
	}
	
	private String getString(String[] stringArray)
	{
		String str = "";
		if (stringArray == null) {
			return str;
		}
		for (String tmp: stringArray)
		{
			str = str + tmp + " ";
		}
		return str;
	}
	
	/**
	 * Build a map of all supported commands. The map will be used to validate
	 * the command execution request. 
	 */
	private void buildCommandMap(){
		
		commandMap = new TreeMap<String, Command>();
		
		//utility commands
		commandMap.put("help", new HelpCommand());
		commandMap.put("quit", new QuitCommand());	
		
		//admin service interfaces
		commandMap.put("getAllCommands", new GetAllCommandsCommand());
		commandMap.put("getProperty", new GetPropertyCommand());
	}

	/*********************************************************************
	 * 
	 * A Command Prompt which prompts the user and reads the user input
	 * 
	 *********************************************************************
	 */
	class CommandPrompt{
		
		InputStream inStream = System.in;
		
		public String read()
		{
			System.out.print(promptS);
			StringBuffer buffer = new StringBuffer();
			int input = -1;			
			try {
				input = inStream.read();
				while (input != -1 && (char) input != '\n') {
					buffer.append((char) input);
					input = inStream.read();
				}
			}
			catch (IOException e){
				e.printStackTrace();
			}
			if (input < 0) {
				return END_OF_TEST;
			}
			return buffer.toString();
		}	
	}
	

	/*********************************************************************
	 * 
	 * Abstract command class to execute the user command 
	 * 
	 *********************************************************************
	 */	
	abstract class Command {
		
		protected String usage;
		
		public void showUsage(){
			System.out.println(getUsage());
		}
		
		public abstract void execute();
		
		/**
		 * cArray[0]: the command itself
		 * cArray[1 - n]: whatever parameters needed for the execution of 
		 *             the command.
		 */
		public abstract void initialize(String[] cArray);		
		
		public abstract Command cloneCommand();
		
		public String getUsage(){
			return usage;
		}
	}
	
	/**********************************************************************
	 * Help Command
	 * ********************************************************************
	 */	
	class HelpCommand extends Command {
		
		public HelpCommand(){
			usage = "Usage: help";
		}
		
		public void execute(){
			Iterator itr = commandMap.values().iterator();
			while (itr.hasNext()) {
				((Command) itr.next()).showUsage();
			}
		}
		
		/**
		 * HelpCommand is a state-less command. There is no need to clone 
		 * HelpCommand. Just return self.
		 */
		public Command cloneCommand(){
			return this;
		}
		
		/**
		 */
		public void initialize(String[] cArray){
			//do nothing;
		}
	}
	
	/**********************************************************************
	 * Quit Command
	 * ********************************************************************
	 */		
	class QuitCommand extends Command {
		
		public QuitCommand(){
			usage = "Usage: quit";
		}
		
		public void execute(){
			System.out.println("Exiting the tester ...");
			System.exit(0);
		}
		
		/**
		 * HelpCommand is a state-less command. There is no need to clone 
		 * HelpCommand. Just return self.
		 */
		public Command cloneCommand(){
			return this;
		}
		
		/**
		 */
		public void initialize(String[] cArray){
			//do nothing;
		}
	}	
	
	/**********************************************************************
	 * getAllCommands
	 * ********************************************************************
	 */
	class GetAllCommandsCommand extends Command {
		
		private String destination;
		private int timeout;
		private GetAllCommandsCallback callback;
		
		public GetAllCommandsCommand(){
			usage = "Usage: getAllCommands <destination> <timeout>";			
		}
		
		public void initialize(String[] cArray){
			if (cArray.length != 3) {
				showUsage();
				throw new IllegalArgumentException(getUsage());
			}
			else {
				destination = cArray[1];
				timeout = Integer.parseInt(cArray[2]);
				createCallback();
			}
		}
		
		public void execute(){
			
			try{
				System.out.println("Executing: getAllCommands");
				service.getAllCommands(destination, timeout, callback);
				System.out.println("Completed executing: getAllCommands, please wait for results." );
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public Command cloneCommand()
		{
			return new GetAllCommandsCommand();
		}
		
		private void createCallback(){
			callback = new GetAllCommandsCallbackImpl();
		}
	}
	
	/**********************************************************************
	 * getProperty
	 * ********************************************************************
	 */
	class GetPropertyCommand extends Command {
		
		private String destination;
		private String propertyName;
		private int timeout;
		private GetPropertyValueCallback callback;
		
		public GetPropertyCommand(){
			usage = "Usage: getProperety <destination> <propertyName> <timeout>";			
		}
		
		public void initialize(String[] cArray){
			if (cArray.length != 4) {
				showUsage();
				throw new IllegalArgumentException(getUsage());
			}
			else {
				destination = cArray[1];
				propertyName = cArray[2];
				timeout = Integer.parseInt(cArray[3]);
				createCallback();
			}
		}
		
		public void execute(){
			
			try{
				System.out.println("Executing: getAllCommands");
				service.getPropertyValue(destination, timeout, propertyName,callback);
				System.out.println("Completed executing: getAllCommands, please wait for results." );
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public Command cloneCommand()
		{
			return new GetPropertyCommand();
		}
		
		private void createCallback(){
			callback = new GetPropertyValueCallbackImpl();
		}
	}	
	
	/**********************************************************************
	 * GetAllCommandsCallback
	 **********************************************************************
	 */
	public class GetAllCommandsCallbackImpl implements GetAllCommandsCallback {
		
	    public void catchException(RuntimeException e) {
	    	System.out.println("Got an exception: ");
	    	e.printStackTrace(System.out);
	    }
	    public void timedOut() {
	    	System.out.println("Timeout");
	    }
		
	    public void returned(com.cboe.infrastructureServices.interfaces.adminService.Command[] commands) {
	    	for (int i = 0; i < commands.length; i++) {
	    		System.out.println("Got command: " + commands[i].name );
	    	}
	    }
	}

	/**********************************************************************
	 * GetAllCommandsCallback
	 **********************************************************************
	 */
	public class GetPropertyValueCallbackImpl implements GetPropertyValueCallback {
		
	    public void catchException(UserException e) {
	    	System.out.println("Got an exception: ");
	    	e.printStackTrace(System.out);
	    }
	    
	    public void catchException(RuntimeException e) {
	    	System.out.println("Got an exception: ");
	    	e.printStackTrace(System.out);
	    }
	    
	    public void timedOut() {
	    	System.out.println("Timeout");
	    }
		
	    public void returned(String pValue) {
	    	System.out.println("Got property: " + pValue );
	    }
	}	
	
}
