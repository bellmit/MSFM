package com.cboe.infrastructureServices.calendarService;

import java.util.Vector;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.infraUtil.DateTypeStruct;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;



public interface CalendarAdminService{


	/**
	 * addDay -- adds a day of the defined types like holiday / non -settlement day to the global service 
	 * @param DateTypeStruct[] dataTypeSeq -- the date and type to be entered
	 * @author murali
	 */	
	public void addDay(DateTypeStruct[] dataTypeSeq) throws DataValidationException, AuthorizationException, CommunicationException, SystemException;

	/**
	 * removeDay -- removes a day of the defined types like holiday / non -settlement day from the global service 
	 * @param DateTypeStruct[] dataTypeSeq -- the date and type to be removed
	 * @author murali
	 */	
	public void removeDay(DateTypeStruct[] dataTypeSeq) throws DataValidationException, AuthorizationException, CommunicationException, SystemException;

	/**
	 * getDaysList -- get the current days list from the local cache
	 * @retunes an array of DateTypeStructs
	 * @author murali
	 */
	public Vector getDaysList();

	public Vector getHolidaysList();

	public Vector getNonSettlementDaysList();

	public Vector getWeekendDaysList();

	/**
	 * getDaysList -- get the current days list from the global server. in the process also updates the local cache
	 * @returns an array of DateTypeStructs
	 * @author murali
	 */
	public Vector getUpdatedDaysList() throws AuthorizationException, CommunicationException, SystemException;

	/**
	 * getBusinessCalendar -- returns the BusinessCaledar for a particular name
	 * @param String name -- the name 
	 * @returns the BusinessCalendar or throws NotFoundException
	 * @author murali
	 */
	public BusinessCalendar getBusinessCalendar() throws NotFoundException;

	public boolean initialize(ConfigurationService configService);

}
