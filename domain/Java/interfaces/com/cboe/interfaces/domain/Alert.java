package com.cboe.interfaces.domain;


import com.cboe.idl.alert.AlertSearchCriteriaStruct;
import com.cboe.idl.cmiIntermarketMessages.AlertStruct;
import com.cboe.idl.cmiIntermarketMessages.AlertStructV2;
import com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.exceptions.SystemException;


/**
 * Alert
 *
 * @author baranski
 * date Oct 11, 2002
 */

public interface Alert {


    /**
     * Creates new alert from passed values.
     *
     * @param alertStruct struct containing values for new alert
     * @exception DataValidationException if validation checks fail
     */
    public void create(AlertStruct alertStruct) throws DataValidationException, TransactionFailedException, SystemException;

    /**
     * Creates new satisfaction alert from passed values.
     *
     * @param alertStruct struct containing values for new alert
     * @exception DataValidationException if validation checks fail
     */
    public void createSatisfactionAlert(SatisfactionAlertStruct alertStruct) throws DataValidationException, TransactionFailedException, SystemException;

    /**
     * Updates the alert.
     *
     * @param comments
     * @param resolution
     * @param tflUserId
     * @exception DataValidationException if validation checks fail
     */
    public void update(String tflUserId, String resolution, String comments) throws DataValidationException;


    /**
     *  Returns a AlertStruct populated based on the Alert fields
     * @return an AlertStruct
     * @throws IllegalStateException
     * @throws TransactionFailedException
     * @throws SystemException
     */

    public com.cboe.idl.cmiIntermarketMessages.AlertStruct toStruct() throws IllegalStateException, TransactionFailedException, SystemException;



     /**
     * Populates the Alert instance that will be used for searching alerts
     * @param searchCriteria
     */
    public void setSearchCriteria(AlertSearchCriteriaStruct searchCriteria);


     /**
     * Returns the creation time of the alert as long
     * @return
     */
    public long getAlertCreationTimeInMillis();
    /**
     * Creates new alert from passed values.
     *
     * @param alertStruct struct containing values for new alert
     * @exception DataValidationException if validation checks fail
     */
    public void createV2(AlertStructV2 p_alertStructV2) throws DataValidationException, TransactionFailedException, SystemException;

}
