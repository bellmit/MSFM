package com.cboe.infrastructureServices.calendarService;

import java.util.Vector;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.infraUtil.DateTypeStruct;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;

public class CalendarAdminServiceNullImpl extends CalendarAdminServiceBaseImpl{

	
	/**
	 * addDay -- adds a day of the defined types like holiday / non -settlement day to the global service 
	 * @param DateTypeStruct[] dateTypeSeq -- the date and type to be entered
	 * @author murali
	 */	
	public void addDay(DateTypeStruct[] dateTypeSeq) throws DataValidationException, AuthorizationException, CommunicationException, SystemException{
	}

	/**
	 * removeDay -- removes a day of the defined types like holiday / non -settlement day from the global service 
	 * @param DateTypeStruct[] dateTypeSeq -- the date and type to be removed
	 * @author murali
	 */	
	public void removeDay(DateTypeStruct[] dateTypeSeq) throws DataValidationException , AuthorizationException, CommunicationException, SystemException{
	}

	/**
	 * getDaysList -- get the current days list from the local cache
	 * @returns an array of DateTypeStructs
	 * @author murali
	 */
	public Vector getDaysList(){
		return null;
	}

	public Vector getHolidaysList(){
		return null;
	}

	public Vector getNonSettlementDaysList(){
		return null;
	}

	public Vector getWeekendDaysList(){
		return null;
	}

	/**
	 * getDaysList -- get the current days list from the global server. in the process also updates the local cache
	 * @retunes an array of DateTypeStructs
	 * @author murali
	 */
	public Vector getUpdatedDaysList() throws AuthorizationException, CommunicationException, SystemException{
		return null;
	}

	/**
	 * getCalendarByName -- returns the BusinessCaledar for a particular name
	 * @param String name -- the name 
	 * @returns the BusinessCalendar or throws CalendarUnavailable
	 * @author murali
	 */
	public BusinessCalendar getBusinessCalendar() throws NotFoundException{
		return null;
	}

	public boolean initialize(ConfigurationService configService){
		return false;
	}
}
