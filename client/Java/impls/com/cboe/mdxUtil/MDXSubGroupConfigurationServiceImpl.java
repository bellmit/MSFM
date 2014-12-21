package com.cboe.mdxUtil;

import java.util.*;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.idl.product.GroupTypeStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.internalBusinessServices.ClientProductConfigurationService;
import com.cboe.interfaces.mdxUtil.MDXSubGroupConfigurationService;

public class MDXSubGroupConfigurationServiceImpl extends BObject implements
		MDXSubGroupConfigurationService {
	
	List<String> configurationGroups;
	List<String> configurationSubGroups;
	String mdxGroup;
	Map<String, SortedSet<Integer>> groupsToClassKeys;
	Map<String, Integer> subGroupSizes;
	ClientProductConfigurationService pcs;
	static final int unevenGroupSizeDifference = 5;
	static final String mdxGroupType = "MDXGroupType";

	public MDXSubGroupConfigurationServiceImpl(String mdxGroup, List<String> configurationGroups, List<String> configurationSubGroups) throws Exception
	{
		this.configurationGroups = configurationGroups;
		this.configurationSubGroups = configurationSubGroups;
		this.mdxGroup = mdxGroup;
		groupsToClassKeys = new HashMap<String, SortedSet<Integer>>();
		subGroupSizes = new HashMap<String, Integer>();
		pcs = ServicesHelper.getProductConfigurationService();
		
		Log.information(this, "MDXSubGroupConfigurationService started for " + mdxGroup );
	}
	
	public synchronized void getClassesFromPCS() throws Exception
	{
		for (String group : configurationGroups)
		{
			try
			{
				int[] keys = pcs.getProductClassesForGroup(group);
				SortedSet<Integer> classKeys = arrayToSet(keys);
				groupsToClassKeys.put(group, classKeys);
			}
			catch(Exception e)
			{
				Log.exception(e);
				Log.alarm("Could not get classes from PCS for MDX Group: " + group + ".  Aborting configuration for group.");
				throw new Exception("Could not get classes from PCS for MDX Group: " + group + ".  Aborting configuration for group.");
			}
		}
		for (String group : configurationSubGroups)
		{
			try
			{
				int[] keys = pcs.getProductClassesForGroup(group);
				SortedSet<Integer> classKeys = arrayToSet(keys);
				groupsToClassKeys.put(group, classKeys);
				subGroupSizes.put(group, classKeys.size());
			}
			catch(Exception e)
			{
				Log.exception(e);
				Log.alarm("Could not get classes from PCS for MDX SubGroup: " + group + ".  Aborting configuration for group.");
				throw new Exception("Could not get classes from PCS for MDX SubGroup: " + group + ".  Aborting configuration for group.");
			}
		}
	}
	
	/***
	 * Configure one mdx sub group.  This operation simply removes all existing classes from the subgroup, and then
	 * copies all classes from the super group into the subgroup.
	 * @return SortedSet containing all classKeys that failed during a pcs add/remove operation
	 */
	public synchronized SortedSet<Integer> configureOneMdxSubGroup()
	{
		Log.information("Configuring one MDX SubGroup.");
		String subGroup = configurationSubGroups.get(0);
		SortedSet<Integer> subgroupClasses = groupsToClassKeys.get(subGroup);
		SortedSet<Integer> failedClasses = new TreeSet<Integer>();
		
		Log.information(this, "Remove all classes from subgroup: " + subGroup);
		Set<Integer> removedClasses = new TreeSet<Integer>();
		for(Integer key : subgroupClasses)
	    {
			try
			{
				Log.information(this, "Removing class: " + key + " from MDX SubGroup: " + subGroup);
		    	pcs.removeProductClassFromGroup(key, subGroup);
		    	removedClasses.add(key);
			}
			catch(Exception e)
			{
				Log.exception(this, e);	
				Log.alarm(this, "Failed to remove classKey: " + key + " from MDX SubGroup: " + subGroup);
				failedClasses.add(key);
			}
	    }		
		subgroupClasses.removeAll(removedClasses);
	    
		Log.information(this, "Add all classes in super group into the subgroup: " + subGroup);
	    for(String group : configurationGroups)
	    {
	    	SortedSet<Integer> mainGroupClasses = groupsToClassKeys.get(group);	
	    	for(Integer key : mainGroupClasses)
	    	{
	    		try
	    		{
		    		Log.information(this, "Adding class:" + key + " to MDX SubGroup:" + subGroup);
		    		pcs.addProductClassToGroup(key, subGroup);
		    		subgroupClasses.add(key);
	    		}
	    		catch(Exception e)
				{
					Log.exception(this, e);	
					Log.alarm(this, "Failed to add classKey: " + key + " to MDX SubGroup: " + subGroup);
					failedClasses.add(key);
				}	    		
	    	}
	    }

	    if(failedClasses.size() == 0)
	    {
	    	Log.information(this, "Completed configuration of MDX SubGroups for " + mdxGroup + " with no errors");
	    }
	    else
	    {
	    	Log.alarm(this, "Errors occured in the following classkeys while configuring MDX SubGroups for " + mdxGroup + ": " + failedClasses.toString());	    	
	    }
	    return failedClasses;
	}
		
	/***
	 * Distribute classes between two subgroups.  If no classes have been added/removed from the 
	 * previous day, the subgroups will remain the same as the previous day after this operation.  Any
	 * new classes added since the previous day, will be inserted into the second subgroup.  The
	 * algorithm has the following steps:  1.  Remove all classes from subgroup2.  2.  Add any classes 
	 * from the super group that are not contained in subgroup1 into subgroup2.  3. Remove all classes from 
	 * subgroup1.  4. Add any classes from the super group that are not contained in subgroup2 into subgroup1. 
	 * @return SortedSet containing all classes that failed during a pcs add/remove operation
	 *
	***/
	public synchronized SortedSet<Integer> configureTwoMdxSubGroups()
	{
		Log.information("Configuring two MDX SubGroups.");
		String subGroupOne = configurationSubGroups.get(0);
		String subGroupTwo = configurationSubGroups.get(1);		
	    SortedSet<Integer> subGroupOneClasses = groupsToClassKeys.get(subGroupOne); 
	    SortedSet<Integer> subGroupTwoClasses = groupsToClassKeys.get(subGroupTwo);
	    SortedSet<Integer> failedClasses = new TreeSet<Integer>();
	        
	    Log.information(this, "Removing all classes from second subgroup: " + subGroupTwo);
	    Set<Integer> removedClasses = new TreeSet<Integer>();
	    for(Integer key : subGroupTwoClasses)
	    {
	    	try
	    	{
		    	pcs.removeProductClassFromGroup(key, subGroupTwo);
		    	removedClasses.add(key);
	    	}
	    	catch(Exception e)
			{
				Log.exception(this, e);	
				Log.alarm(this, "Failed to remove classKey: " + key + " from MDX SubGroup: " + subGroupTwo);
				failedClasses.add(key);
			}
	    }
	    subGroupTwoClasses.removeAll(removedClasses);
	    removedClasses.clear();
	    
	    // Find subset of super group that is not contained in Group 1
	    SortedSet<Integer> subsetOfMainGroupExcludingSubGroup1 = new TreeSet<Integer>();;
	    for(String group : configurationGroups)
	    {
	    	SortedSet<Integer> superGroupClasses = groupsToClassKeys.get(group);
	    	subsetOfMainGroupExcludingSubGroup1.addAll(getSubsetOfXThatDoesNotContainY(superGroupClasses, subGroupOneClasses));
	    }
	    
	    Log.information(this, "Adding subset of super group that is not contained in " + subGroupOne + " to " + subGroupTwo);
	    for(Integer key : subsetOfMainGroupExcludingSubGroup1)
	    {
	    	try
	    	{
		    	Log.information(this, "Adding class:" + key + " to MDX SubGroup:" + subGroupTwo);
		    	pcs.addProductClassToGroup(key, subGroupTwo);
		    	subGroupTwoClasses.add(key);
	    	}
	    	catch(Exception e)
			{
				Log.exception(this, e);	
				Log.alarm(this, "Failed to add classKey: " + key + " to MDX SubGroup: " + subGroupTwo);
				failedClasses.add(key);
			}
	    }
	    
	    Log.information(this, "Removing all classes from first subgroup: " + subGroupOne);
	    for(Integer key : subGroupOneClasses)
	    {
	    	try
	    	{
		    	Log.information(this, "Removing class: " + key + " from MDX SubGroup: " + subGroupOne);
		    	pcs.removeProductClassFromGroup(key, subGroupOne);
		    	removedClasses.add(key);
	    	}
	    	catch(Exception e)
			{
				Log.exception(this, e);	
				Log.alarm(this, "Failed to remove classKey: " + key + " from MDX SubGroup: " + subGroupOne);
				failedClasses.add(key);
			}
	    }
	    subGroupOneClasses.removeAll(removedClasses);
	    
	    // Find subset of super group that is not contained in Group 2
	    SortedSet<Integer> subsetOfMainGroupExcludingSubGroup2 = new TreeSet<Integer>();
	    for(String group : configurationGroups)
	    {
	    	SortedSet<Integer> mainGroupClasses = groupsToClassKeys.get(group);	
	    	subsetOfMainGroupExcludingSubGroup2.addAll(getSubsetOfXThatDoesNotContainY(mainGroupClasses, subGroupTwoClasses));
	    }
	    
	    Log.information(this, "Adding subset of super group that is not contained in " + subGroupTwo + " to " + subGroupOne);
	    for(Integer key : subsetOfMainGroupExcludingSubGroup2)
	    {
	    	try
	    	{
		    	Log.information(this, "Adding class:" + key + " to MDX SubGroup:" + subGroupOne);
		    	pcs.addProductClassToGroup(key, subGroupOne);
		    	subGroupOneClasses.add(key);
	    	}
	    	catch(Exception e)
			{
				Log.exception(this, e);	
				Log.alarm(this, "Failed to add classKey: " + key + " to MDX SubGroup: " + subGroupOne);
				failedClasses.add(key);
			}
	    }
	    
	    if(failedClasses.size() == 0)
	    {
	    	Log.information(this, "Completed configuration of MDX SubGroups for " + mdxGroup + " with no errors");
	    }
	    else
	    {
	    	Log.alarm(this, "Errors occured in the following classkeys while configuring MDX SubGroups for " + mdxGroup + ": " + failedClasses.toString());	    	
	    }
	    return failedClasses;
	}
	
	/***
	 * Attempts to create subgroups as defined in MDXSet.properties
	 */
	public synchronized void createSubGroups() throws Exception
	{
		Log.information(this, "Creating groups for " + mdxGroup);
		try
		{
			GroupTypeStruct[] groupTypes = pcs.getGroupTypes();	
			int groupType = -99999;
		    for(GroupTypeStruct groupTypeStruct : groupTypes)
		    {
		    	if(groupTypeStruct.groupTypeDescription.equals(mdxGroupType))
		    	{
		    	     groupType = groupTypeStruct.groupType;	
		    	     break;
		    	}
		    }
		    if(groupType == -99999)
		    {
		    	throw new Exception("GroupType: " + mdxGroupType + " not found");
		    }
			for(String group : configurationSubGroups)
			{
				Log.information(this, "Creating group: " + group);
			    pcs.createGroup(group , groupType);
			}
		}
		catch(Exception e)
		{
			Log.exception(this, e);
			throw e;	
		}
	}
	
	/***
	 * This method will assign classes from the super group into subgroups.  All subgroups must be empty in order to call
	 * this method.  Classes sharing the same underlying symbol will be placed in the same subgroup.
	 * @return SortedSet containing all classKeys that failed pcs add operation
	 */
	public synchronized SortedSet<Integer> initializeSubGroupsDistributeEvenly() throws Exception
	{
		Log.information(this, "Initializing SubGroups for " + mdxGroup);
		SortedSet<Integer> failedClasses = new TreeSet<Integer>();
		try
		{
			ClassKeyHelper classKeyHelper = new ClassKeyHelper();
			SortedSet<Integer> alreadyAddedClasses = new TreeSet<Integer>();
			Collection<Integer> sizes = subGroupSizes.values();
			for(Integer size : sizes)
			{
				if(size > 0)
				{
					throw new Exception("All SubGroups for " + mdxGroup + " are not empty.  initializeSubGroups can only be called when all SubGroups are empty.");
				}
			}
			
		    for(String groupKey : configurationGroups)
		    {
		    	
		    	SortedSet<Integer> classKeys = groupsToClassKeys.get(groupKey);
		    	for(Integer classKey : classKeys)
		    	{
		    		if(!alreadyAddedClasses.contains(classKey))
		    		{
			    		String leastPopulatedGroup = getLeastPopulatedSubGroup();
			    		SortedSet<Integer> leastPopulatedSubGroupClassKeys = groupsToClassKeys.get(leastPopulatedGroup);
			    		int size = subGroupSizes.get(leastPopulatedGroup);
			    		SortedSet<Integer> classKeyFamily = classKeyHelper.getClassFamilyForClassKey(classKey);
			    		for(Integer key : classKeyFamily)
			    		{
			    			if(classKeys.contains(key))
			    			{
			    				try
			    				{
			    					Log.information(this, "Adding class:" + key + " to MDX SubGroup:" + leastPopulatedGroup);
					    		    pcs.addProductClassToGroup(key, leastPopulatedGroup);
					    		    leastPopulatedSubGroupClassKeys.add(key);
						    		size++;
						    		alreadyAddedClasses.add(key);
			    				}
			    				catch(Exception e)
			    				{
			    					Log.alarm(this, "Exception occurred while adding classKey:" + key + " to SubGroup:" + leastPopulatedGroup);
			    					Log.exception(e);
			    					failedClasses.add(key);
			    				}
			    			}
			    		}
			    		subGroupSizes.put(leastPopulatedGroup, size);
		    		}
		    	}	    	
		    }
		}
		catch(Exception e)
		{
			Log.exception(this, e);
			throw e;
		}
		
		if(failedClasses.size() == 0)
	    {
	    	Log.information(this, "Completed initialization of MDX SubGroups for " + mdxGroup + " with no errors");
	    }
	    else
	    {
	    	Log.alarm(this, "Errors occured in the following classkeys while initializing MDX SubGroups for " + mdxGroup + ": " + failedClasses.toString());	    	
	    }
	    return failedClasses;
	}
	
	/***
	 * Moves classes between subgroups, from a more populated group to a lesser populated group.  Ensures that classes with
	 * classes with the same underlying symbol will be in the same subgroup.
	 * @return SortedSet containing all classKeys that failed pcs add operation
	 */
	public synchronized SortedSet<Integer> balanceSubGroups() throws Exception
	{
		Log.information(this, "Balancing SubGroups for " + mdxGroup);
		SortedSet<Integer> failedClasses = new TreeSet<Integer>();
		try
		{
			if(configurationSubGroups.size() < 2)
			{
				throw new Exception("Excepiton occurred while attempting to balance " + mdxGroup + ". There must be at least two SubGroups in order to call balanceSubGroups().");
			}
			if(!unevenGroups())
			{
				Log.information("Skipping balance for MDX Group " + mdxGroup + ". The difference in group sizes must be greater than " + unevenGroupSizeDifference + " in order for balancing to occur");
				return failedClasses;
			}
			ClassKeyHelper classKeyHelper = new ClassKeyHelper();
			while(unevenGroups())
			{
				String leastPopulatedGroup = getLeastPopulatedSubGroup();
				SortedSet<Integer> leastPopulatedGroupClassKeys = groupsToClassKeys.get(leastPopulatedGroup);
				String mostPopulatedGroup = getMostPopulatedSubGroup();
				SortedSet<Integer> mostPopulatedGroupClassKeys = groupsToClassKeys.get(mostPopulatedGroup);
				int leastPopulatedGroupSize = subGroupSizes.get(leastPopulatedGroup);
				int mostPopulatedGroupSize = subGroupSizes.get(mostPopulatedGroup);
				Integer classToMove = getRandomClassFromList(mostPopulatedGroupClassKeys);
				SortedSet<Integer> classKeyFamily = classKeyHelper.getClassFamilyForClassKey(classToMove);
				for(Integer key : classKeyFamily)
	    		{
	    			if(mostPopulatedGroupClassKeys.contains(key))
	    			{
	    				try
	    				{
	    					Log.information(this, "Moving classKey:" + key + " from MDX SubGroup:" + mostPopulatedGroup + " to MDX SubGroup:" + leastPopulatedGroup);
						    pcs.removeProductClassFromGroup(key, mostPopulatedGroup);
						    pcs.addProductClassToGroup(key, leastPopulatedGroup);
						    leastPopulatedGroupClassKeys.add(key);
							mostPopulatedGroupClassKeys.remove(key);
							leastPopulatedGroupSize++;
				    		mostPopulatedGroupSize--;
	    				}
	    				catch(Exception e)
	    				{
	    					Log.alarm(this, "Exception occurred while moving classKey:" + key + " from SubGroup:" + mostPopulatedGroup + " to SubGroup:" + leastPopulatedGroup);
	    					Log.exception(e);
	    					failedClasses.add(key);
	    				}
	    			}
	    		}
				subGroupSizes.put(leastPopulatedGroup, leastPopulatedGroupSize);
				subGroupSizes.put(mostPopulatedGroup, mostPopulatedGroupSize);
			}
		}
		catch(Exception e)
		{
			Log.exception(this, e);
			throw e;
		}
		
		if(failedClasses.size() == 0)
	    {
	    	Log.information(this, "Completed balancing of MDX SubGroups for " + mdxGroup + " with no errors");
	    }
	    else
	    {
	    	Log.alarm(this, "Errors occured in the following classkeys while balancing MDX SubGroups for " + mdxGroup + ": " + failedClasses.toString());	    	
	    }
	    return failedClasses;
	}
	
	/***
	 * Assigns classes that are defined in the super group but have not yet been assigned to a subgroup,
	 * to the least populated subgroup.  Ensures that classes with the same underlying symbol will be in 
	 * the same subgroup.
	 * @return SortedSet containing all classKeys that failed pcs add operation
	 */
	public synchronized SortedSet<Integer> addNewClassesToLeastPopulatedSubGroup() throws Exception
	{
		Log.information(this, "Adding new classes to least populated subgroup for " + mdxGroup);
		SortedSet<Integer> failedClasses = new TreeSet<Integer>();
		try
		{
			Log.information(this, "Checking for new classes in " + mdxGroup);
			ClassKeyHelper classKeyHelper = new ClassKeyHelper();
			for (String group : configurationGroups)
			{
				SortedSet<Integer> classKeys = groupsToClassKeys.get(group); 
				
				for(Integer classKey : classKeys)
				{
					boolean isNewClass = true;
					for(String subGroup: configurationSubGroups)
					{
						SortedSet<Integer> subGroupClassKeys = groupsToClassKeys.get(subGroup);
						if(subGroupClassKeys.contains(classKey))
						{
						    isNewClass = false;
						    break;
						}
					}
					// If it has been determined that this is a new class, assign it to the least
					// populated subgroup
					if(isNewClass)
					{
						//Get all classes that share the underlying symbol
						SortedSet<Integer> classKeyFamily = classKeyHelper.getClassFamilyForClassKey(classKey);
						boolean doesSiblingClassExistInSubGroup = false;
						String targetGroup = getLeastPopulatedSubGroup();
						for(Integer siblingKey : classKeyFamily)
						{
							for(String subGroup: configurationSubGroups)
							{
								SortedSet<Integer> subGroupClassKeys = groupsToClassKeys.get(subGroup);
								//If a class with the same underlying has already been assigned to a subgroup
								//we need to assign this class to that same subgroup.
								if(subGroupClassKeys.contains(siblingKey))
								{
								    doesSiblingClassExistInSubGroup = true;
								    targetGroup = subGroup;
								    break;
								}
							}
							if(doesSiblingClassExistInSubGroup)
							{
								break;
							}
						}
						
						SortedSet<Integer> classKeysForTargetGroup = groupsToClassKeys.get(targetGroup);
						int size = subGroupSizes.get(targetGroup);
						for(Integer key : classKeyFamily)
						{
							if(classKeys.contains(key) && !classKeysForTargetGroup.contains(key))
							{
								try
			    				{
									Log.information(this, "Adding class:" + key + " to MDX SubGroup:" + targetGroup);
					    		    pcs.addProductClassToGroup(key, targetGroup);
					    		    classKeysForTargetGroup.add(key);
					    		    size++;
			    				}
			    				catch(Exception e)
			    				{
			    					Log.alarm(this, "Exception occurred while adding classKey:" + key + " to SubGroup:" + targetGroup);
			    					Log.exception(e);
			    					failedClasses.add(key);
			    				}
							}
						}
			    		subGroupSizes.put(targetGroup, size);
					}
				}
			}
			Log.information(this, "Completed check for new classes in " + mdxGroup);
		}
		catch(Exception e)
		{
			Log.exception(this, e);
			throw e;
		}
		
		if(failedClasses.size() == 0)
	    {
	    	Log.information(this, "Completed adding new classes to MDX SubGroups for " + mdxGroup + " with no errors");
	    }
	    else
	    {
	    	Log.alarm(this, "Errors occured in the following classkeys while adding new classes to MDX SubGroups for " + mdxGroup + ": " + failedClasses.toString());	    	
	    }
	    return failedClasses;
	}
	
	/***
	 * Finds classes that have been removed from the super group, and removes them from the subgroups.
	 * @return SortedSet containing all classKeys that failed pcs add operation
	 */
	public synchronized SortedSet<Integer> checkForRemovedClasses()
	{
		Log.information(this, "checking for removed classes in " + mdxGroup);
		SortedSet<Integer> failedClasses = new TreeSet<Integer>();

		for (String subGroup : configurationSubGroups)
		{
			SortedSet<Integer> subGroupClassKeys = groupsToClassKeys.get(subGroup);
			int size = subGroupSizes.get(subGroup);
			List<Integer> removedClasses = new ArrayList<Integer>();
			for(Integer subGroupClassKey : subGroupClassKeys)
			{				
				boolean isRemovedClass = true;
				for(String group: configurationGroups)
				{
					SortedSet<Integer> mainGroupClassKeys = groupsToClassKeys.get(group);
					if(mainGroupClassKeys.contains(subGroupClassKey))
					{
					    isRemovedClass = false;
					    break;
					}
				}
				if(isRemovedClass)
				{
					try
					{
						Log.information(this, "Removing class:" + subGroupClassKey + " from MDX SubGroup:" + subGroup);
					    pcs.removeProductClassFromGroup(subGroupClassKey, subGroup);
					    removedClasses.add(subGroupClassKey);
					    size--;
					}
					catch(Exception e)
    				{
    					Log.alarm(this, "Exception occurred while removing classKey:" + subGroupClassKey + " from SubGroup:" + subGroup);
    					Log.exception(e);
    					failedClasses.add(subGroupClassKey);
    				}
				}
			}
			for(Integer classToRemove : removedClasses)
			{
				subGroupClassKeys.remove(classToRemove);
			}
		    subGroupSizes.put(subGroup, size);
		}
		
		if(failedClasses.size() == 0)
	    {
	    	Log.information(this, "Completed check for removed classes for " + mdxGroup + " with no errors");
	    }
	    else
	    {
	    	Log.alarm(this, "Errors occured in the following classkeys while checking for removed classes for " + mdxGroup + ": " + failedClasses.toString());	    	
	    }
	    return failedClasses;
	}
	
	/***
	 * Verifies that there are no duplicate classes across subgroups, and that the sum of all subgroup classes
	 * is equal to the supergroup
	 * @throws Exception if verification fails
	 */
	public synchronized void verifyMDXSubGroups() throws Exception
	{
		int totalMainGroupSize = 0;
		int totalSubGroupSize = 0;
		
		for(String group : configurationGroups)
		{
			SortedSet<Integer> classes = groupsToClassKeys.get(group);
			totalMainGroupSize = totalMainGroupSize + classes.size();
		}
		
		for(String subGroup : configurationSubGroups)
		{
			SortedSet<Integer> classes = groupsToClassKeys.get(subGroup);
			totalSubGroupSize = totalSubGroupSize + classes.size();
		}
		
		List<Integer> duplicateClasses = checkDuplicates();
		if(duplicateClasses.size() != 0)
		{
			Log.alarm(this, "MDXGroup Duplicate Class Check Failed for " + mdxGroup + ".  The following classes were found in multiple MDX PCS SubGroups: "  + duplicateClasses.toString());
			throw new Exception("MDXGroup Duplicate Class Check Failed for " + mdxGroup + ".  The following classes were found in multiple MDX PCS SubGroups: " + duplicateClasses.toString());
		}
		if(totalMainGroupSize != totalSubGroupSize)
		{
			Log.alarm(this, "Group Size Verification Failed for " + mdxGroup + ".  Run check for new and removed classes if you have not already done so.");
			throw new Exception("Group Size Verification Failed for " + mdxGroup + ".  Run check for new and removed classes if you have not already done so.");
		}

		Log.information(this, "MDX SubGroup Verification Successful for " + mdxGroup);
	}
		
	/***
	 * Removes all classes from subgroups
	 * @return SortedSet containing all classKeys for which the pcs remove operation failed.
	 */
	public synchronized SortedSet<Integer> removeAllClassesFromSubGroups()
	{
		Log.information(this, "Removing all classes from MDX SubGroups for " + mdxGroup);
		SortedSet<Integer> failedClasses = new TreeSet<Integer>();
 
		for(String subgroup : configurationSubGroups)
		{
			SortedSet<Integer> classes = groupsToClassKeys.get(subgroup);
			SortedSet<Integer> removedClasses = new TreeSet<Integer>();
			
			for(Integer key : classes)
			{
				try
				{
					Log.information(this, "Removing class:" + key + " from SubGroup:" + subgroup);
				    pcs.removeProductClassFromGroup(key, subgroup);
				    removedClasses.add(key);
				}
				catch(Exception e)
				{
					Log.alarm(this, "Exception occurred while removing classKey:" + key + " from SubGroup:" + subgroup);
					Log.exception(e);
					failedClasses.add(key);					
				}
			}
			classes.removeAll(removedClasses);
		}
		
		if(failedClasses.size() == 0)
	    {
	    	Log.information(this, "Completed removal of all classes from subgroups for " + mdxGroup + " with no errors");
	    }
	    else
	    {
	    	Log.alarm(this, "Errors occured in the following classkeys while attempting to remove all classes from subgroups for " + mdxGroup + ": " + failedClasses.toString());	    	
	    }
	    return failedClasses;
	}
		
	public synchronized void printGroups()
	{
		Log.information(this, "Printing all group assignments for " + mdxGroup);
		for(String group : configurationGroups)
		{
			SortedSet<Integer> classes = groupsToClassKeys.get(group);
			Log.information(this, "All classes in MDX Group " + group + ": " + classes.toString());
		}
		
		for(String group : configurationSubGroups)
		{
			SortedSet<Integer> classes = groupsToClassKeys.get(group);			
			Log.information(this, "All classes in MDX SubGroup " + group + ": " + classes.toString());
		}
	}
	
	SortedSet<Integer> getSubsetOfXThatDoesNotContainY(SortedSet<Integer> X, SortedSet<Integer> Y)
	{
		SortedSet<Integer> subset = new TreeSet<Integer>();
		for(Integer key : X)
		{
			if(!Y.contains(key))
			{
				subset.add(key);
			}
		}		
	    return subset;	
	}
	
	List<Integer> checkDuplicates()
	{
		List<String> subGroupsList = new ArrayList<String>(configurationSubGroups);
		List<Integer> duplicateClasses = new ArrayList<Integer>();
		
		for(int i=0; i<subGroupsList.size() - 1; i++)
	    {
			SortedSet<Integer> firstSubGroupClasses = groupsToClassKeys.get(subGroupsList.get(i));
	        for(int j=i+1; j < subGroupsList.size();  j++)
	        {
	            SortedSet<Integer> secondSubGroupClasses = groupsToClassKeys.get(subGroupsList.get(j));
	            for(Integer classKey : firstSubGroupClasses)
	            {
	            	if(secondSubGroupClasses.contains(classKey))
	            	{
	            		duplicateClasses.add(classKey);
	            	}
	            }
	        }
	    }
		return duplicateClasses;
	}
		
	String getLeastPopulatedSubGroup()
	{
		String leastPopulatedGroup = configurationSubGroups.iterator().next();
		int lowAmount = subGroupSizes.get(leastPopulatedGroup);

		for(String subGroup : configurationSubGroups)
		{
			int size = subGroupSizes.get(subGroup);
		    if(size == 0)
		    {
		    	return subGroup;
		    }
		    else
		    {
		    	if(size < lowAmount)
		    	{
		    		leastPopulatedGroup = subGroup;
		    		lowAmount = size;
		    	}
		    }		    	
		}
		return leastPopulatedGroup;		
	}
		
	String getMostPopulatedSubGroup()
	{
		String mostPopulatedGroup = configurationSubGroups.iterator().next();;
		int highAmount = subGroupSizes.get(mostPopulatedGroup);

		for(String subGroup : configurationSubGroups)
		{
			int size = subGroupSizes.get(subGroup);
	    	if(size > highAmount)
	    	{
	    		mostPopulatedGroup = subGroup;
	    		highAmount = size;
	    	}		    	
		}
		return mostPopulatedGroup;
	}
	
	boolean unevenGroups()
	{
		Collection<Integer> sizes = subGroupSizes.values();
		List<Integer> groupSizes = new ArrayList<Integer>(sizes);
	    for(int i=0; i<groupSizes.size() - 1; i++)
	    {
	        for(int j=i+1; j < groupSizes.size();  j++)
	        {
	        	if(Math.abs(groupSizes.get(i) - groupSizes.get(j)) > unevenGroupSizeDifference)
	        	{
	        		return true;
	        	}
	        }
	    }
		return false;      	
	}
	
	SortedSet<Integer> arrayToSet(int[] array)
	{
		SortedSet<Integer> set = new TreeSet<Integer>();
		if(array != null || array.length != 0)
		{
			for(int i=0; i<array.length; i++)
			{
				set.add(array[i]);
			}
		}
		return set;
	}
	
	Integer getRandomClassFromList(SortedSet<Integer> set)
	{
		int returnVal = set.first();
		int randomNumber = (int)(Math.random() * set.size());
		int i = 0;
        for(Integer key : set)
        {
            if(randomNumber == i)
            {
                returnVal = key;
                break;
            }
            i++;
        }
        return returnVal;
	}
}
