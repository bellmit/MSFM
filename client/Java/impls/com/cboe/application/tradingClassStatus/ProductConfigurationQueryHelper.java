package com.cboe.application.tradingClassStatus;

import java.util.ArrayList;
import java.util.HashMap;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiSession.SessionClassDetailStruct;
import com.cboe.idl.product.GroupStruct;
import com.cboe.idl.product.GroupTypeStruct;
import com.cboe.idl.session.TradingSessionStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ExceptionBuilder;

/**
 * @author Arun Ramachandran
 *    
 */
public class ProductConfigurationQueryHelper {

	private final String VALID_GROUP_TYPE = "ValidGroupType";
	private static ProductConfigurationQueryHelper singletonInstance = null;
	private ArrayList<GroupStruct> productGroups;
	private HashMap<String,Integer>groupStructIndex;
	private HashMap<String, GroupStruct> classKeyToGroup;
	private TradingSessionStruct[] tradingSessions;

	private ProductConfigurationQueryHelper() {
		String validGroupType = System.getProperty(VALID_GROUP_TYPE);
		if (validGroupType != null) {
			try {
				GroupTypeStruct[] groupTypes = ServicesHelper
						.getProductConfigurationService().getGroupTypes();
				tradingSessions = ServicesHelper.getTradingSessionService().getTradingSessions();
				for (int i = 0; i < groupTypes.length; i++) {
					GroupTypeStruct groupTypeStruct = groupTypes[i];
					if (groupTypeStruct.groupTypeDescription
							.equals(validGroupType)) {
						GroupStruct[] grpStruct = ServicesHelper
								.getProductConfigurationService()
								.getGroupsByType(groupTypeStruct.groupType);
						productGroups = new ArrayList<GroupStruct>(grpStruct.length);
						groupStructIndex = new HashMap<String, Integer>(grpStruct.length);
						classKeyToGroup = new HashMap<String, GroupStruct>();
						for (int j = 0; j < grpStruct.length; j++) {
							GroupStruct tempGS = grpStruct[j];
							productGroups.add(j, tempGS);
							groupStructIndex.put(tempGS.groupName, j);
							for (int k = 0; k < tradingSessions.length; k++) {
								SessionClassDetailStruct[] classes = ServicesHelper.getTradingSessionService().getClassesForSessionByGroup(tradingSessions[k].sessionName, tempGS.groupName);
								for (int l = 0; l < classes.length; l++) {
									int ck = classes[l].classDetail.classStruct.classKey;
									classKeyToGroup.put(classes[l].classDetail.sessionName+"."+ck, tempGS);
								}
							}
						}
					}
				}
				if(Log.isDebugOn()) {
					for (int i =0;i<productGroups.size();i++) {
						Log.debug("TradingClassStatus Cached Groups :"+groupStructToString(productGroups.get(i)));
					} 
				}
			} catch (AuthorizationException ae) {
				Log.exception("Error geting ProcessGroupNames", ae);
			} catch (DataValidationException dve) {
				Log.exception("Error geting ProcessGroupNames", dve);
			} catch (CommunicationException ce) {
				Log.exception("Error geting ProcessGroupNames", ce);
			} catch (SystemException se) {
				Log.exception("Error geting ProcessGroupNames", se);
			}
		}
	}

	private String groupStructToString(GroupStruct groupStruct) {
		return "<"+groupStruct.groupName+" | "+groupStruct.groupKey+" | "+groupStruct.groupType.groupType+">";
	}

	public static synchronized ProductConfigurationQueryHelper getInstance() {
		if (singletonInstance == null) {
			return singletonInstance = new ProductConfigurationQueryHelper();
		}
		return singletonInstance;
	}

	public String[] getProductGroups() {
		return (String[])groupStructIndex.keySet().toArray(new String[groupStructIndex.size()]);
	}

	public int[] getClassesForProductGroup(String productGroupName)
			throws DataValidationException, CommunicationException,
			AuthorizationException, SystemException {
		return ServicesHelper.getProductConfigurationService()
				.getProductClassesForGroup(productGroupName);
	}

	/**
	 * @param string
	 */
	public void isValidGroup(String groupName) throws DataValidationException{
		for (int i = 0; i < productGroups.size(); i++) {
			if(groupName.equals(productGroups.get(i).groupName)){
				return;
			}
		}
		throw ExceptionBuilder.dataValidationException("", 0);
	}

	public String getGroupNameForClass(int classKey, String tradingSession) {
		GroupStruct group = classKeyToGroup.get(tradingSession+"."+classKey);
		if(group!=null) {
			return group.groupName;
		}
		return null;
	}
	
	public String getGroupNameForGroupKey(int grpkey) {
		
		for (int i=0; i<productGroups.size(); i++) {
			GroupStruct grpStruct = productGroups.get(i);
			if(grpStruct.groupKey==grpkey) {
				return grpStruct.groupName;
			}
		}
		return null;
	}
	
	public boolean isValidSessionName(String sessionName) {
		for (int i = 0; i < tradingSessions.length; i++) {
			if(tradingSessions[i].sessionName.equals(sessionName)){
				return true;
			}
		}
		return false;
	}

	public TradingSessionStruct[] getTradingSessions() {
		return tradingSessions;
	}	
}
