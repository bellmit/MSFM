package com.cboe.interfaces.domain;

import com.cboe.idl.cmiIntermarketMessages.AlertStruct;
import com.cboe.idl.cmiIntermarketMessages.AlertStructV2;
import com.cboe.idl.alert.AlertSearchCriteriaStruct;
import com.cboe.idl.alert.AlertHistoryStruct;
import com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.exceptions.*;


/**
 * AlertHome
 *
 * @author baranski
 * @date Oct 11, 2002
 */

public interface AlertHome {

    public final static String HOME_NAME = "AlertHome";

    /**
     * Creates new satisfaction alert from passed values.
     *
     * @param AlertStruct CORBA struct containing values for new alert
     * @return void
     * @exception  TransactionFailedException if alert cannot be created
     * @exception DataValidationException if validation checks fail
     */
    public void createSatisfactionAlert(SatisfactionAlertStruct aAlertStruct)
            throws TransactionFailedException, DataValidationException, SystemException;

    /**
     * Creates new alert from passed values.
     *
     * @param AlertStruct CORBA struct containing values for new alert
     * @return void
     * @exception  TransactionFailedException if alert cannot be created
     * @exception DataValidationException if validation checks fail
     */

    public void create(AlertStruct aAlertStruct)
            throws TransactionFailedException, DataValidationException, SystemException;
    
    /**
     * Creates new alert from passed values.
     *
     * @param AlertStructV2 CORBA struct containing values for new (Trade througth Par)
     * @return void
     * @exception  TransactionFailedException if alert cannot be created
     * @exception DataValidationException if validation checks fail
     */

    public void createV2(AlertStructV2 aAlertStruct)
            throws TransactionFailedException, DataValidationException, SystemException;
    
    /**
     * Searches for alert by orsId.
     *
     * @param orsId of desired alert
     * @return found alert
     * @exception NotFoundException if search fails
     */
    public Alert[] findByORSID(String  orsId) throws NotFoundException, TransactionFailedException, DataValidationException;


    /**
     * Searches for alert by key.
     *
     * @param alertKey key of desired alert
     * @return found alert
     * @exception NotFoundException if search fails
     */
    public Alert findByKey(CboeIdStruct alertId) throws NotFoundException, TransactionFailedException, DataValidationException;
    /**
     * Updates the alert.
     *
     * @param updateable parameters
     * @return void
     * @exception  DataValidationException if invalid data
     * @exception TransactionFailedException if alert cannot be updated
     */
    public AlertStruct update(CboeIdStruct alertId, String tflUserId, String resolution, String comments)
           throws  TransactionFailedException, DataValidationException, NotFoundException, SystemException;

    public AlertHistoryStruct find(AlertSearchCriteriaStruct criteria)
           throws DataValidationException, TransactionFailedException, SystemException;

    public  SatisfactionAlertStruct getSatisfactionAlertById(CboeIdStruct alertId)
            throws  TransactionFailedException, SystemException, NotFoundException, DataValidationException;



}



