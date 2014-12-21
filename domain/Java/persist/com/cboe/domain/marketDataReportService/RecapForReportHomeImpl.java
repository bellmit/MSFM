package com.cboe.domain.marketDataReportService;

import java.util.Vector;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiErrorCodes.AlreadyExistCodes;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.marketDataReportService.RecapForReport;
import com.cboe.interfaces.domain.marketDataReportService.RecapForReportHome;
import com.cboe.util.ExceptionBuilder;

/**
 * An implementation of <code>RecapForReportHome</code> that manages Recap information using
 * JavaGrinder persistence mapping.
 * 
 * @author Cognizant Technology Solutions.
 */
public class RecapForReportHomeImpl extends BOHome implements RecapForReportHome
{
    /**
     * Creates an instance.
     */
    public RecapForReportHomeImpl()
    {
        super();
    }

    /**
     * Searches for <code>RecapForReport </code> by Class Key and Product key.
     * 
     * @param classKey
     * @param productKey
     * @return <code>RecapForReport</code>
     */
    private RecapForReport queryByKeys(int classKey, int productKey)
    {
        RecapForReport result = null;
        RecapForReportImpl example = new RecapForReportImpl();
        addToContainer(example);
        ObjectQuery query = new ObjectQuery(example);
        try
        {
            example.setClassKey(classKey);
            example.setProductKey(productKey);
            result = (RecapForReport) query.findUnique();
        }
        catch (PersistenceException e)
        {
            // ignore error - return null result
        }
        return result;
    }

    /**
     * Searches for all <code>RecapForReport</code> belonging to the passed Session Name
     * 
     * @param sessionName
     * @return array of found <code>RecapForReport</code>'s. Array will contain zero elements if
     * search fails.
     */
    public RecapForReport[] findAllRecapBySession(String sessionName)
    {
        RecapForReport[] result = null;
        RecapForReportImpl example = new RecapForReportImpl();
        addToContainer(example);
        ObjectQuery query = new ObjectQuery(example);
        try
        {
            example.setSessionName(sessionName);
            Vector queryResult = query.find();
            result = new RecapForReport[queryResult.size()];
            queryResult.copyInto(result);
        }
        catch (PersistenceException e)
        {
            result = new RecapForReport[0];
        }
        return result;
    }

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
    public RecapForReportImpl createRecapEntry(String sessionName, int classKey, int productKey,
            int categoryType, short productType, char optionType, Price closingPrice, int openInterest) throws 
            DataValidationException, SystemException
    {
        RecapForReportImpl newInstance = new RecapForReportImpl();
        newInstance.create(sessionName, classKey, categoryType, productKey, productType, optionType, closingPrice, openInterest);
        addToContainer(newInstance);
        return newInstance;
    }
}
