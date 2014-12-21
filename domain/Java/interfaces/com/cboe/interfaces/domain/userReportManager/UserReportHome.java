package com.cboe.interfaces.domain.userReportManager;

import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.order.OrderAcknowledgeStruct;
import com.cboe.idl.quote.QuoteAcknowledgeStruct;
import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.order.OrderAcknowledgeStruct;
import com.cboe.idl.order.OrderAcknowledgeStructV3;
import com.cboe.idl.quote.QuoteAcknowledgeStruct;
import com.cboe.idl.quote.QuoteAcknowledgeStructV3;

import java.io.Serializable;
import java.util.Date;

/**
 *  Defines the interface for a "user report" home.  Describes the query mechanism, etc.
 *
 *  @author Steven Sinclair
 */
public interface UserReportHome
{
    public static final String HOME_NAME = "UserReportHome";

    public static final int ALL_CLASS_KEY = 0;
    /**
     * Used by reportType parameters to specify that the given entity is to be related to order reports.
     */
    public static final int ORDER_REPORT_TYPE = 1;

    /**
     * Used by reportType parameters to specify that the given entity is to be related to quote reports.
     */
    public static final int QUOTE_REPORT_TYPE = 2;

    /**
     *  Return all registered users for all report types.
     *
     *  @return RegisteredUser[] - all registered users.
     *  @exception SystemException - thrown if there is a persistence error.
     */
    RegisteredUser[] findAllRegisteredUsers()
        throws SystemException;

    /**
     *  Return all registered users for the given report type.
     *
     *  @param reportType - one of the XXX_REPORT_TYPE constants declared in this interface.
     *  @return RegisteredUser[] = all users registered for the given report type.
     *  @exception SystemException - thrown if there is a persistence error.
     */
    RegisteredUser[] findRegisteredUsersByType(int reportType)
        throws DataValidationException, SystemException;

    /**
     *  Return all user reports for the given user.
     *
     *  @param userId - the user to find the report for.
     *  @param classKey - the classKey to find the report for.  0 - indicates all reports ignore classKey.
     *  @param reportType - one of the XXX_REPORT_TYPE constants declared in this interface.
     *  @param onlyNotAck - If true, only returns reports for which there have not been an ack.
     *      Otherwise return all reports for userId&reportType.
     *  @return UserReport[] - all user reports which match the given criteria.
     *  @exception SystemException - thrown if there is a persistence error.
     */
    UserReport[] findReportsForUser(String userId, int classKey, int reportType, boolean onlyNotAck, long userReportDbId)
        throws DataValidationException, SystemException;


    /**
     *  Return all reports.
     *
     *  @return UserReport[] - all user reports
     *  @exception SystemException - thrown if there is a persistence error.
     */
    UserReport[] findAllReports()
        throws SystemException;

    /**
     *  Create a user report for a quote.
     *
     *  @param userId - the user for wom the report if being created
     *  @param firm - the user's firm (ExchangeFirmStruct)
     *  @param eventType - the event type (one of the XXX_EVENT_TYPE constants declared by this intereface).
     *  @param transSeqNum - the transaction sequence number
     *  @param productKey - the product of this quote report
     *  @param Serializable - any serializable data to associate with this report.
     *  @return UserReport - the report created.
     */
    UserReport createUserQuoteReport(String userId, ExchangeFirmStruct firm, int classKey, int eventType, int transSeqNum, int productKey, Serializable data, boolean createPersistentReport)
        throws SystemException, AlreadyExistsException, TransactionFailedException, DataValidationException;

    /**
     *  Create a user report for a order.
     *
     *  @param userId - the user for wom the report if being created
     *  @param firm - the user's firm (ExchangeFirmStruct)
     *  @param eventType - the event type (one of the XXX_EVENT_TYPE constants declared by this intereface).
     *  @param transSeqNum - the transaction sequence number
     *  @param productKey - the product of this order report
     *  @param orderId - the order id struct
     *  @param Serializable - any serializable data to associate with this report.
     *  @return UserReport - the report created.
     */
    UserReport createUserOrderReport(String userId, ExchangeFirmStruct firm, int classKey, int eventType, int transSeqNum, int productKey, OrderIdStruct orderId, Serializable data, boolean createPersistentReport)
        throws SystemException, AlreadyExistsException, TransactionFailedException, DataValidationException;

    /**
     *  Create an acknowledge of a quote report.
     *
     *  @param orderAck - the acknowleg struct used to find the related UserReport and create the ack.
     *  @return UserAcknowledge - the ack created.
     *  @exception NotFoundException - thrown if the related UserReport cannot be found.
     *  @exception AlreadyExistsExeption - thrown if the object to create aleady exists.
     *  @exception TransactionFailedException - thrown if there was a persistence problem.
     */
    UserAcknowledge createUserAck(QuoteAcknowledgeStructV3 quoteAck)
        throws NotFoundException, AlreadyExistsException, TransactionFailedException, SystemException;

    /**
     *  Create an acknowledge of an order report.
     *
     *  @param orderAck - the acknowleg struct used to find the related UserReport and create the ack.
     *  @return UserAcknowledge - the ack created.
     *  @exception NotFoundException - thrown if the related UserReport cannot be found.
     *  @exception AlreadyExistsExeption - thrown if the object to create aleady exists.
     *  @exception TransactionFailedException - thrown if there was a persistence problem.
     */
    UserAcknowledge createUserAck(OrderAcknowledgeStructV3 orderAck)
        throws NotFoundException, AlreadyExistsException, TransactionFailedException, SystemException;

    /**
     *  Register a user for a report type.
     *
     *  @param userId - the user to register
     *  @param reportType - one of the XXX_REPORT_TYPE constants declared in this interface.
     *  @return RegisteredUser - the object created
     *  @exception AlreadyExistsExeption - thrown if the object to create aleady exists.
     *  @exception TransactionFailedException - thrown if there was a persistence problem.
     */
    RegisteredUser createRegisteredUser(String userId, int classKey, int reportType)
        throws DataValidationException, AlreadyExistsException, TransactionFailedException;

    /**
     *  Purge reports and associated acknowledges for reports older than the given date.
     *
     *  @param terminationDate - remove all user reports and associated acknowledges for reports older than the given date.
     *  @param reportType - one of the XXX_REPORT_TYPE constants declared in this interface.
     *  @exception TransactionFailedException - thrown if there was a persistence problem.
     */
    void purgeReportsOlderThan(Date terminationDate, int reportType)
        throws TransactionFailedException, DataValidationException;

    /**
     *  Remove the given user registration.
     *
     *  @param userId - the user name to remove the registration for.
     *  @param reportType - the report type to remove the registration for.
     *  @exception TransactionFailedException - thrown if there is a persistence problem removing the registration
     *  @exception DataValidationException - thrown if the report type is invalid.
     *  @exception NotFoundException - thrown if the givne registration entry is not found.
     */
    void removeRegisteredUser(String userId, int classKey, int reportType)
        throws TransactionFailedException, DataValidationException, NotFoundException;

    boolean isDropCopyRole(String userId);
}
