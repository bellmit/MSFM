package com.cboe.interfaces.domain.userReportManager;

import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.exceptions.*;
import java.io.Serializable;
import java.util.Date;

/**
 *  Defines the interface for a "user report": an object describing an order or a quote report for a user.
 *
 *  @author Steven Sinclair
 */
public interface UserReport
{
    String getUserId();
    String getFirmNum();
    String getFirmExchange();
    int getReportType();     // constant from UserReportHome
    int getEventType();      // constant from UserReportHome
    int getTransactionSequenceNumber();
    int getProductKey();
    int getClassKey();
    OrderIdStruct getOrderId();
    Serializable getData();
    Date getTimeStamp();
    boolean isPossibleResend(); // transient field
    boolean isDropCopy();     // transient field
    boolean isGroupDropCopy();     // transient field
    String getGroupUserId();      // transient field    

    void setUserId(String userId);
    void setFirm(ExchangeFirmStruct firm);
    void setFirmNum(String firmNum);
    void setFirmExchange(String firmExchange);
    void setReportType(int reportType);     // constant from UserReportHome
    void setEventType(int eventType);       // constant from UserReportHome
    void setTransactionSequenceNumber(int transSeqNum);
    void setProductKey(int productKey);
    void setClassKey(int classKey);
    void setOrderId(OrderIdStruct orderId);
    void setData(Serializable data);
    void setTimeStamp(Date date);
    void setIsPossibleResend(boolean isPossibleResend); // transient field
    void setIsDropCopy(boolean isPossibleResend);     // transient field
    void setIsGroupDropCopy(boolean isGroupDC); //JJC   // transient field
    void setGroupUserId(String groupUserId); //JJC   // transient field
}
