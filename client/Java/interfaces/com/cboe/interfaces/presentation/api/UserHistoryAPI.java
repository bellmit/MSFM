package com.cboe.interfaces.presentation.api;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiTraderActivity.*;
import com.cboe.idl.cmiUtil.*;

/**
 * This interface represents the UserHistory API into the CAS,
 *
 *
 * @author Dean Grippo
 */

public interface UserHistoryAPI
{
    /**
     * Gets the activity history for the specified class, start time and direction.
     *
     * @author Dean Grippo
     *
     * @return com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct
     * @param  classKey  - the ID of the class
     * @param  startTime - get history starting from this time
     * @param  direction - and moving backward or forward from the start time
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
     public ActivityHistoryStruct getTraderClassActivityByTime(String sessionName, int classKey, DateTimeStruct startTime, short direction)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Gets the activity history for the specified product, start time and direction.
     *
     * @author Dean Grippo
     *
     * @return com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct
     * @param  productKey  - the ID of the product
     * @param  startTime - get history starting from this time
     * @param  direction - and moving backward or forward from the start time
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
     public ActivityHistoryStruct getTraderProductActivityByTime(String sessionName, int productKey, DateTimeStruct startTime, short direction)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;





     /**********************  END of User History methods ***********************************************/

}
