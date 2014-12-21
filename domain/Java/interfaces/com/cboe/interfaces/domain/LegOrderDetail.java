package com.cboe.interfaces.domain;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiOrder.BustReportStruct;
import com.cboe.idl.cmiOrder.CancelReportStruct;
import com.cboe.idl.cmiOrder.ContraPartyStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiOrder.LegOrderDetailStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;

public interface LegOrderDetail
{
    int getProductKey();

    Order getOrder();

    Price getMustUsePrice();

    ExchangeFirmStruct getClearingFirm() throws SystemException;

    char getCoverage();

    char getPositionEffect();

    char getSideValue();
    
    Side getSide();

    int getOriginalQuantity();

    int getTradedQuantity();

    int getCancelledQuantity();

    int getLeavesQuantity();

    // Update method - note that it takes a LegOrderDetailStruct.
    // This really should be LegOrderEntryStruct, but we kludged this
    // so the broker would not need modifications to add that struct to its code.
    void update(LegOrderDetailStruct updatedValues) throws NotFoundException, SystemException;
    FilledReportStruct fill(int productKey, long tradeId, int totalQuantity, Price tradePrice, ContraPartyStruct[] contraParties, boolean updateTrable, TradableSnapShot captuerdFillData) throws DataValidationException;
    CancelReportStruct cancel(int quantity, String userAssignedCancelId) throws DataValidationException;
    void bust(BustReportStruct bustInfo) throws DataValidationException;
    void reinstate(int quantity) throws DataValidationException;
    
    //in case of remote leg fill, we will get the fill report from the remote TE
    //   and set the leg to be filled upon its fill report
    void setFill(FilledReportStruct fillReport) throws DataValidationException;
    public String getOrderIdString();
    public void setTradedQuantity(int aQuantity);
}
