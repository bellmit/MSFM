package com.cboe.interfaces.presentation.rfq;

import com.cboe.util.event.EventChannelListener;
import com.cboe.exceptions.NotFoundException;
import com.cboe.idl.cmiQuote.RFQStruct;

public interface RFQCache extends EventChannelListener {
    public void addRFQ(RFQStruct rfq);

    public void addRFQs(RFQStruct[] rfqs);

    public void removeRFQ(RFQ rfq);

    public void removeRFQs(RFQ[] rfqs);

    public RFQ[] getRFQsForClass(int classKey);

    public RFQ getRFQForProduct(int productKey) throws NotFoundException;

    public RFQ[] getAllRFQs();

    public boolean doRFQsExist();

    public int getRFQCount();
}
