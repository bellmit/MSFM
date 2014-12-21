package com.cboe.interfaces.mdxUtil;

import java.util.SortedSet;

public interface MDXSubGroupConfigurationService {

	public void getClassesFromPCS() throws Exception;
	public SortedSet<Integer> configureOneMdxSubGroup();
	public SortedSet<Integer> configureTwoMdxSubGroups();
	public SortedSet<Integer> initializeSubGroupsDistributeEvenly() throws Exception;
	public SortedSet<Integer> balanceSubGroups() throws Exception;
	public SortedSet<Integer> addNewClassesToLeastPopulatedSubGroup() throws Exception;
	public SortedSet<Integer> checkForRemovedClasses();
	public void verifyMDXSubGroups() throws Exception;
	public SortedSet<Integer> removeAllClassesFromSubGroups();
	public void printGroups();
	public void createSubGroups() throws Exception;
}
