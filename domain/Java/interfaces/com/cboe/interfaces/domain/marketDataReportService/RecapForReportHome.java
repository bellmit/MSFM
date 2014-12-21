package com.cboe.interfaces.domain.marketDataReportService;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.domain.Price;

/**
 * A home class used to create or find <code>RecapForReport</code> information for Market Data Report Service.
 * 
 * @author Cognizant Technology Solutions.
 */
public interface RecapForReportHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "RecapForReportHome";

    /**
     * Creates a <code>RecapForReport</code> corresponding to the passed values.
     * 
     * @param sessionName
     * @param classKey
     * @param productKey
     * @param categoryType
     * @param productType
     * @param optionType
     * @param closingPrice
     * @param openInterest
     * @return Created RecapForReport object.
     * @throws DataValidationException
     * @throws SystemException
     */
    public RecapForReport createRecapEntry(String sessionName, int classKey, int productKey,
            int categoryType, short productType, char optionType, Price closingPrice, int openInterest) 
            throws DataValidationException, SystemException;

    /**
     * Searches for all <code>RecapForReport</code> belonging to the passed Session Name
     * 
     * @param sessionName
     * @return array of found <code>RecapForReport</code>'s. Array will contain zero elements if
     * search fails.
     */
    public RecapForReport[] findAllRecapBySession(String sessionName);
}