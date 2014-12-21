package com.cboe.mdxUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationServiceFileImpl;
import com.cboe.interfaces.mdxUtil.MDXSubGroupConfigurationService;
import com.cboe.interfaces.mdxUtil.MDXSubGroupConfigurationServiceHome;

public class MDXSubGroupConfigurationMain {
	
	ConfigurationService configurationService;
    public static final String RUN_MODE = "RunMode";
    public static final String MDXGROUP_PROP_FILE = "MDXGroupPropFile";
    private String runMode;
    private String mdxGroupPropFile;
    Properties mdxGroupProperties;
    private boolean mailResults;
    private static final String MAIL_RESULTS = "MailResults";
    private String mailOutput;
    private static final String MAIL_OUTPUT = "MailOutput";
    private String outputFile;
    private static final String OUTPUT_FILE = "OutputFile";

	private void initialize(String[] args) throws Exception
    {
        FoundationFramework ff = FoundationFramework.getInstance();
        configurationService = new ConfigurationServiceFileImpl();
        configurationService.initialize(args, 0);
        ff.initialize("MDXSubGroupConfiguration", configurationService);
        runMode = System.getProperty(RUN_MODE);
        mdxGroupPropFile = System.getProperty(MDXGROUP_PROP_FILE);
        mailResults = Boolean.parseBoolean(System.getProperty(MAIL_RESULTS));
        outputFile = System.getProperty(OUTPUT_FILE);
        if(mailResults)
        {
        	mailOutput = System.getProperty(MAIL_OUTPUT);	
        }
        mdxGroupProperties = getMDXGroupProperties(mdxGroupPropFile);       
    }
	
	private Properties getMDXGroupProperties(String fileName)
    {
        Properties properties = new Properties();
        InputStream stream = getInputStream(fileName);
        if(stream == null)
        {
            System.out.println("Specified property file " + fileName + " not found.");
        }
        else
        {
            try
            {
                properties.load(stream);
                stream.close();
            }
            catch (Exception ex)
            {
                System.out.println("Exception in specified property file " + fileName);
                ex.printStackTrace();
            }
        }
        return properties;
    }

    private InputStream getInputStream(String fileName)
    {
        InputStream stream = null;
        File f = new File(fileName);
        if(f.exists())
        {
            try
            {
                stream = new FileInputStream(f);
            }
            catch (Throwable t) 
            {
            	t.printStackTrace();
            }
        }
        return stream;
    }
	
    public void start()
	{
		String[] mdxGroups = mdxGroupProperties.getProperty("MDXGroups").split(",");
		HashMap<String, SortedSet<Integer>> failedGroupToClassKeys = new HashMap<String, SortedSet<Integer>>();
		HashMap<String, String> failedGroupToErrorMessage = new HashMap<String, String>();
		
		MDXSubGroupConfigurationServiceHome mdxSubGroupConfigServiceHome;
		try
        {
		    mdxSubGroupConfigServiceHome = (MDXSubGroupConfigurationServiceHome) HomeFactory.getInstance().findHome(MDXSubGroupConfigurationServiceHome.HOME_NAME);
        }
		catch(CBOELoggableException e)
        {
            e.printStackTrace();
            throw new NullPointerException("Could not find MDXSubGroupConfigurationServiceHome.");
        }

		List<String> configurationGroups;
		List<String> configurationSubGroups;
		for(int i=0; i<mdxGroups.length; i++)
		{
			SortedSet<Integer> failedClassKeys = new TreeSet<Integer>();
			try
			{
				String[] groupNames = mdxGroupProperties.getProperty(mdxGroups[i] + "ConfigurationGroups").split(",");
				String[] subGroupNames = mdxGroupProperties.getProperty(mdxGroups[i] + "ConfigurationSubGroups").split(",");
				configurationGroups = new ArrayList<String>();
				configurationSubGroups = new ArrayList<String>();
				for(int j = 0; j < groupNames.length; j++)
		        {
		            configurationGroups.add(groupNames[j]);
		        }        
		        for(int j = 0; j < subGroupNames.length; j++)
		        {
		            configurationSubGroups.add(subGroupNames[j]);
		        }
		        
		        MDXSubGroupConfigurationService mdxSubGroupConfigService = mdxSubGroupConfigServiceHome.create(mdxGroups[i], configurationGroups, configurationSubGroups);
		        
		        if(!runMode.equalsIgnoreCase("createSubGroups"))
		        {
		        	mdxSubGroupConfigService.getClassesFromPCS();
		        }
		        else
		        {
		        	mailResults = false;
		        	mdxSubGroupConfigService.createSubGroups();	
		        }
		       
				if(runMode.equalsIgnoreCase("initializeSubGroupsDistributeEvenly"))
				{
					mailResults = false;
					failedClassKeys = mdxSubGroupConfigService.initializeSubGroupsDistributeEvenly();	
					mdxSubGroupConfigService.printGroups();
				}
				else if(runMode.equalsIgnoreCase("balanceSubGroups"))
				{
					mailResults = false;
					failedClassKeys = mdxSubGroupConfigService.balanceSubGroups();
					mdxSubGroupConfigService.printGroups();
				}
				else if(runMode.equalsIgnoreCase("addNewClassesToLeastPopulatedSubGroup"))
				{
					mailResults = false;
					failedClassKeys = mdxSubGroupConfigService.addNewClassesToLeastPopulatedSubGroup();
					mdxSubGroupConfigService.printGroups();
				}
				else if(runMode.equalsIgnoreCase("checkForRemovedClasses"))
				{
					mailResults = false;
					failedClassKeys = mdxSubGroupConfigService.checkForRemovedClasses();
					mdxSubGroupConfigService.printGroups();
				}
				else if(runMode.equalsIgnoreCase("verifyMDXSubGroups"))
				{
					mailResults = false;
					mdxSubGroupConfigService.verifyMDXSubGroups();
				}
				else if(runMode.equalsIgnoreCase("removeAllClasses"))
				{
					mailResults = false;
					failedClassKeys = mdxSubGroupConfigService.removeAllClassesFromSubGroups();
					mdxSubGroupConfigService.printGroups();
				}
				else if(runMode.equalsIgnoreCase("printGroups"))
				{
					mailResults = false;
					mdxSubGroupConfigService.printGroups();	
				}
				else if(runMode.equalsIgnoreCase("dailyCheck"))
				{
					if(configurationSubGroups.size() == 1)
					{
						failedClassKeys = mdxSubGroupConfigService.configureOneMdxSubGroup();	
					}
					else if(configurationSubGroups.size() == 2)
					{
						failedClassKeys = mdxSubGroupConfigService.configureTwoMdxSubGroups();	
					}
					else if(configurationSubGroups.size() == 0)
					{
						throw new Exception("Error in MDXSet properties file.  There must be at least one subgroup to configure");
					}
					else if(configurationSubGroups.size() > 2)
					{
						throw new Exception("MDXSubGroupConfigurationService cannot configure more than two subgroups.");	
					}
					
					mdxSubGroupConfigService.printGroups();
					mdxSubGroupConfigService.verifyMDXSubGroups();
				}
				if (failedClassKeys.size() > 0)
				{
					failedGroupToClassKeys.put(mdxGroups[i], failedClassKeys);						
				}
			}
			catch(Exception e)
			{
				System.out.println(mdxGroups[i] + ": " + e.getMessage());
				failedGroupToClassKeys.put(mdxGroups[i], failedClassKeys);
				failedGroupToErrorMessage.put(mdxGroups[i], e.getMessage());
			}
		}
		try
		{
			BufferedWriter output = null;
			if(failedGroupToClassKeys.size() == 0 && failedGroupToErrorMessage.size() == 0)
			{	
				System.out.println("Completed MDX Group Configuration.  All Groups Configured Successfully.");
				if(mailResults)
				{
					output = new BufferedWriter(new FileWriter(mailOutput));
					output.write("All MDX Groups Were Configured Successfully.\n");	
					output.flush();
					output.close();
				}
			}
			else
			{
				System.out.println("ALARM:  MDX GROUP CONFIGURATION FAILED\n");
				
				if(mailResults)
				{
					output = new BufferedWriter(new FileWriter(mailOutput));
					output.write("ALARM:  MDX GROUP CONFIGURATION FAILED\n");
					output.write("Output file is located at prdgc01a: /sbt/prod/tradeeng/run_dir/log/" + outputFile + "\n\n");					
				}
				
				Set<String> failedGroups = failedGroupToClassKeys.keySet();
				for(String group : failedGroups)
				{
					System.out.println("Errors occurred while configuring MDX Group: " +  group);
					SortedSet<Integer> failedClassKeys = failedGroupToClassKeys.get(group);
					if(failedClassKeys != null)
					{
						System.out.println("The following classKeys in group " + group + " may not have been configured: " + failedClassKeys.toString());	
					}
					String errorMessage = failedGroupToErrorMessage.get(group);
					if(errorMessage != null)
					{
						System.out.println("The following error message was received when attempting to configure group " + group + ": " + errorMessage + "\n");						
					}
					System.out.println();
					if(mailResults)
					{
						output.write("Errors occurred while configuring MDX Group: " +  group + "\n");
						if(failedClassKeys != null)
						{
							output.write("The following classKeys in group " + group + " may not have been configured: " + failedClassKeys.toString() + "\n");	
						}
						if(errorMessage != null)
						{
							output.write("The following error message was received when attempting to configure group " + group + ": " + errorMessage + "\n");						
						}
						output.write("\n");
						output.flush();
					}
				}
				if(output != null)
				{
					output.close();
				}
			}
		}
		catch(IOException e)
		{
			System.out.println("An error occured while attempting to write to the mail output file");
			e.printStackTrace();
		}
	}
   
	public static void main(String[] args)
	{
	    MDXSubGroupConfigurationMain obj = new MDXSubGroupConfigurationMain();
	    try
	    {
	        obj.initialize(args);
	        obj.start();
	        System.exit(0);
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    	System.exit(0);
	    }
	}
}
