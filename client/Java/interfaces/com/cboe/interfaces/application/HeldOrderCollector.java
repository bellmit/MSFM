
package com.cboe.interfaces.application;

import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiIntermarketMessages.*;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.interfaces.domain.session.SessionBasedCollector;

public interface HeldOrderCollector extends SessionBasedCollector {
    void acceptNewHeldOrder(String sessionName, int classKey, HeldOrderStruct heldOrder);

    void acceptCancelHeldOrderRequest(String sessionName, int classKey, ProductKeysStruct productKeys,  HeldOrderCancelRequestStruct cancelRequest );

    void acceptHeldOrderStatus(String sessionName, int classKey, HeldOrderStruct heldOrder);

    void acceptHeldOrders(String sessionName, int classKey, HeldOrderStruct[] heldOrders);

    void acceptHeldOrderCanceledReport(String sessionName, int classKey, HeldOrderStruct heldOrder, CboeIdStruct cancelId,  CancelReportStruct cancleReport );

    void acceptHeldOrderFilledReport(String sessionName, int classKey, HeldOrderStruct heldOrder, FilledReportStruct[] filledReport );

    void acceptFillRejectReport(String sessionName, int classKey, FillRejectStruct[] fillRejects);

}
