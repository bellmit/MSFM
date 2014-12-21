package com.cboe.interfaces.domain;

// ------------------------------------------------------------------------
//  Source file: TradeReportSummaryHome.java
// 
//  A smaller version of the TradeReport interface which contains just key 
//  information for trade reports that match provided criteria
//
//  @author dowat
// ------------------------------------------------------------------------
//  Copyright (c) 2009 The Chicago Board Option
// ------------------------------------------------------------------------


import java.util.Vector;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiUtil.DateTimeStruct;

/**
 * This defines the TradeReportSummaryHome interface.  Implementations should be read only.
 *
 * @author dowat
 */

public interface TradeReportSummaryHome
{
	public static final String HOME_NAME = "TradeReportSummary"; // name used to retrieve TradeReportSummaryHome from HomeFactory

    
    /**
     * Method used to retrieve all the trade reports associated with the given trade ids.  Method expects that all ids pertain
     * to trade reports that executed on the trade server where this query executes.  Trade reports found that are not handled 
     * by the executing hybrid trade server will be ignored.
     *
     * @param tradeReportKeys (<code>TradeReportSummaryStruct</code>)
     *
     * @return RelatedTradeReportSummaryStruct Returns all trade report summaries that are associated with the provided trade id criteria and 
     *         executed on the Hybrid Trade server wher the query is running.  A trade retrieved from db but not handled by the executing 
     *         Hybrid Trade Server will be ignored and an empty result returned
     * 
     * @exception SystemException whenever the query fails to execute
     *
     * @author dowat
     * @throws NotFoundException 
     * @throws TransactionFailedException 
     */
    TradeReportSummary findByKey(long tradeid) throws TransactionFailedException, SystemException, NotFoundException, DataValidationException;
    
    /**
     * Method used to retrieve all the trade reports associated with the given trade ids.  Method expects that all ids pertain
     * to trade reports that executed on the trade server where this query executes.  Trade reports found that are not handled 
     * by the executing hybrid trade server will be ignored.
     *
     * @param tradeReportKeys (<code>TradeReportSummaryStruct</code>)
     *
     * @return TradeReport[] Returns all trade reports that correspond to the provided trade report key criteria and executed on the Hybrid Trade
     * Server where the trade occurred.  Trade retrieved from db but not handled by the executing Hybrid Trade Server will be ignored.
     * 
     * @exception SystemException whenever the query fails to execute
     *
     * @author dowat
     * @throws NotFoundException 
     * @throws SystemException 
     */
    TradeReport[] findByKeys(Vector tradeids) throws TransactionFailedException, NotFoundException, SystemException, DataValidationException;
	
    /**
     * Method used to retrieve the keys for all TradeReports whose time member is strictly between startTime(<code>DateTimeStruct</code>) and
     * endTime(<code>DateTimeStruct</code>.  Implementations should support multiple Hybrid Trade Server deployments where multiple instances are 
     * running on a single business cluster by adding class key criteria to the query to filter for the trade reports that executed on the same 
     * server where the query executes.  This is to prevent returning duplicate reports for a given business cluster.
     *
     * @param startTime (<code>long</code>)
     * @param endTime (<code>long</code>)
     * @param includeParentReports (<code>boolean</code>)
     *
     * @return TradeReportSummaryStruct[] Implementations should return either an empty or non-empty array containing trade report key data
     *         for trade that meet the given criteria.
     * @author Hemmant Thakkar
     * @throws TransactionFailedException TODO
     */
    public TradeReportSummary[] findTradeReportsBetween(DateTimeStruct startTime, DateTimeStruct endTime, boolean includeParentReports) throws TransactionFailedException, DataValidationException;
    /**
     * Method used to retrieve the keys for all TradeReports whose time member is strictly between startTime(<code>DateTimeStruct</code>) and
     * endTime(<code>DateTimeStruct</code> and involves one of the specified classes.  Implementations should support multiple 
     * Hybrid Trade Server deployments where multiple instances are running on a single business cluster by adding class key criteria to the 
     * query to filter for the trade reports that executed on the same server where the query executes.  This is to prevent returning duplicate 
     * reports for a given business cluster.
     *
     * @param startTime (<code>long</code>)
     * @param endTime (<code>long</code>)
     * @param classKeys (<code>int[]</code>)
     *
     * @return TradeReportSummaryStruct[] Implementations should return either an empty or non-empty array containing results that meet the
     *         given criteria
     * @exception SystemException whenever query fails to execute
     *
     * @author Hemant Thakkar
     */
    public TradeReportSummary[] findTradeReportsByClass(DateTimeStruct beginDateTime, DateTimeStruct endDateTime, int[] classKeys) throws SystemException, DataValidationException;

    /**
     * Method used to retrieve the keys for all TradeReports whose time member is strictly between startTime(<code>DateTimeStruct</code>) and
     * endTime(<code>DateTimeStruct</code> and whose underlying trades on one of the given exchanges.  Implementations should support multiple 
     * Hybrid Trade Server deployments where multiple instances are running on a single business cluster by adding class key criteria to the 
     * query to filter for the trade reports that executed on the same server where the query executes.  This is to prevent returning duplicate 
     * reports for a given business cluster.
     *
     * @param startTime (<code>long</code>)
     * @param endTime   (<code>long</code>)
     * @param String[]  (<code>String[]</code>) An aaray of primary exchanges
     *
     * @return TradeReportSummaryStruct[] Implementations should return either an empty or non-empty array containing the trade report keys for
     *         trade reports that meet the given criteria
     * @exception SystemException whenever query fails to run
     *
     * @author Hemant Thakkar
     */
    public TradeReportSummary[] findTradeReportsByExchange(DateTimeStruct startTime, DateTimeStruct endTime, String[] primaryExchList) throws SystemException, DataValidationException, NotFoundException;

    /**
     * Method used to retrieve the keys for all TradeReports whose time member is strictly between startTime(<code>DateTimeStruct</code>) and
     * endTime(<code>DateTimeStruct</code> and is for one of the given products.  Implementations should support multiple 
     * Hybrid Trade Server deployments where multiple instances are running on a single business cluster by adding class key criteria to the 
     * query to filter for the trade reports that executed on the same server where the query executes.  This prevents returning duplicate 
     * reports for a given business cluster.
     *
     * @param startTime (<code>long</code>)
     * @param endTime   (<code>long</code>)
     * @param String[]  (<code>String[]</code>) An array of user ids
     *
     * @return TradeReportSummaryStruct[] Implementations should return either an empty or non-empty array containing the trade report keys for
     *         trade reports that meet the given criteria
     * @exception SystemException whenever query fails to run
     *
     * @author dowat
     */
    public TradeReportSummary[] findTradeReportsByUserIds(DateTimeStruct startTime, DateTimeStruct endDateTime, String[] userids, boolean activeOnly) throws SystemException, DataValidationException;


    /**
     * Method used to retrieve the keys for all TradeReports whose time member is strictly between startTime(<code>DateTimeStruct</code>) and
     * endTime(<code>DateTimeStruct</code> and is for one of the given products.  Implementations should support multiple 
     * Hybrid Trade Server deployments where multiple instances are running on a single business cluster by adding class key criteria to the 
     * query to filter for the trade reports that executed on the same server where the query executes.  This prevents returning duplicate 
     * reports for a given business cluster.
     *
     * @param startTime (<code>long</code>)
     * @param endTime   (<code>long</code>)
     * @param String[]  (<code>String[]</code>)
     *
     * @return TradeReportSummaryStruct[] Implementations should return either an empty or non-empty array containing the trade report keys for
     *         trade reports that meet the given criteria
     * @exception SystemException whenever query fails to run
     *
     * @author Hemant Thakkar
     */
    public TradeReportSummary[] findTradeReportsByProduct(DateTimeStruct startTime, DateTimeStruct endDateTime, int[] productKeys) throws SystemException, DataValidationException;
}
