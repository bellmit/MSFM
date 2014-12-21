package com.cboe.mdxUtil;

import java.io.*;
import java.util.*;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class ClassKeyHelper {

static final String USE_CLASSKEYS_SCRIPT = "UseClassKeysScript";
	
	/***
	 * This method uses the "classKeys" script on prdgc01a to find all classKeys that share the same underlying
	 * symbol
	 * @param classKey
	 * @return SortedSet containing all classKeys that share the same underlying as classKey
	 */
	public SortedSet<Integer> getClassFamilyForClassKey(Integer classKey)
	{
		boolean useClassKeysScript = Boolean.parseBoolean(System.getProperty(USE_CLASSKEYS_SCRIPT, "false"));
		SortedSet<Integer> classKeys = new TreeSet<Integer>();
		if(useClassKeysScript)
		{
			try {
			    String line;
	            // First get the class symbol from the classKey
			    ProcessBuilder processBuilder = new ProcessBuilder();
			    List<String> command = new ArrayList<String>();
			    command.add("ksh");
			    command.add("classKeys -k " + classKey);
			    processBuilder.command(command);
			    processBuilder.directory(new File(System.getenv("RUN_DIR")));
			    final Process classKeyProcess = processBuilder.start();
			    BufferedReader classKeyInput = new BufferedReader(new InputStreamReader(classKeyProcess.getInputStream()));
			    BufferedReader classKeyError = new BufferedReader(new InputStreamReader(classKeyProcess.getErrorStream()));
			    while ((line = classKeyError.readLine()) != null)
			    {
			        System.out.println(line);	
			    }
			    line = classKeyInput.readLine();
			    if(line != null)
			    {
			    	StringTokenizer tokens = new StringTokenizer(line);
			    	if(tokens.hasMoreTokens())
			    	{
			    		//The class symbol should be the second token in the output
			            tokens.nextToken();
			    	}
			    	if(tokens.hasMoreTokens())
			    	{
				        String classSymbol = tokens.nextToken();
				        // Then get all the classKeys for the class symbol
				        processBuilder = new ProcessBuilder();
				        command = new ArrayList<String>();
				        command.add("ksh");
				        command.add("classKeys -s " + classSymbol);
				        processBuilder.command(command);
				        processBuilder.directory(new File(System.getenv("RUN_DIR")));
				        final Process classSymbolProcess = processBuilder.start();
				        BufferedReader classSymbolInput = new BufferedReader(new InputStreamReader(classSymbolProcess.getInputStream()));
				        BufferedReader classSymbolError = new BufferedReader(new InputStreamReader(classSymbolProcess.getErrorStream()));
					    while ((line = classSymbolError.readLine()) != null)
					    {
					        System.out.println(line);	
					    }
					    while ((line = classSymbolInput.readLine()) != null) 
					    {
					        tokens = new StringTokenizer(line);
					        if(tokens.hasMoreTokens())
					        {
					        	// ClassKey is the first token of the output
					            classKeys.add(Integer.parseInt(tokens.nextToken().trim()));
					        }
					    }
					    classSymbolProcess.destroy();
					    classSymbolError.close();
					    classSymbolInput.close();
			    	}
			    }
			    classKeyProcess.destroy();
			    classKeyError.close();
			    classKeyInput.close();
			}
			catch (Exception e) {
			    Log.exception(e);
			}
		}
		if(classKeys == null || classKeys.size() == 0)
		{
			classKeys.add(classKey);
		}
	    return classKeys;	
	}
}
