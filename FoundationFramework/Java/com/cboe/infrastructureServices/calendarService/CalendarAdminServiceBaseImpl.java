package com.cboe.infrastructureServices.calendarService;

import java.util.Vector;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.infraUtil.DateTypeStruct;
import com.cboe.infrastructureServices.foundationFramework.FrameworkComponentImpl;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;

public abstract class CalendarAdminServiceBaseImpl extends FrameworkComponentImpl
implements CalendarAdminService
{

        protected static String serviceImplClassName = "com.cboe.infrastructureServices.calendarService.CalendarAdminServiceNullImpl";
        private static CalendarAdminService instance = null;


	public abstract void addDay(DateTypeStruct[] dataTypeSeq) throws DataValidationException, AuthorizationException, CommunicationException, SystemException;
	public abstract void removeDay(DateTypeStruct[] dataTypeSeq) throws DataValidationException, AuthorizationException, CommunicationException, SystemException;
	public abstract Vector getDaysList();
	public abstract Vector getHolidaysList();
	public abstract Vector getNonSettlementDaysList();
	public abstract Vector getWeekendDaysList();
	public abstract Vector getUpdatedDaysList() throws AuthorizationException, CommunicationException, SystemException;
	public abstract BusinessCalendar getBusinessCalendar() throws NotFoundException;
        public static CalendarAdminService getInstance()
        {
                if ( instance == null )
                {
                        try
                        {
                                Class c = Class.forName(serviceImplClassName);
                                instance = (CalendarAdminService)c.newInstance();
                        }
                        catch ( ClassNotFoundException cnfe )
                        {
                                new CBOELoggableException( cnfe, MsgPriority.high ).printStackTrace();
                                return null;
                        }
                        catch ( InstantiationException ie )
                        {
                                new CBOELoggableException( ie, MsgPriority.high).printStackTrace();
                             return null;
                        }
                        catch ( IllegalAccessException iae )
                        {
                                new CBOELoggableException( iae, MsgPriority.high).printStackTrace();
                                return null;
                        }
                }
                return instance;
        }



	public boolean initialize(ConfigurationService configService){
		return true;
	}

	public static String getServiceImplClassName()
        {
                return serviceImplClassName;
        }

	public static void setServiceImplClassName(String className)
        {
                 serviceImplClassName = className;
        }
}
