package com.cboe.presentation.api;

import com.cboe.idl.cmiQuote.RFQStruct;
import com.cboe.interfaces.presentation.rfq.RFQ;
import com.cboe.presentation.common.instruction.ScheduledInstructionSetFactory;
import java.util.Observer;
import java.util.Map;
import java.util.HashMap;
import com.cboe.exceptions.DataValidationException;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.domain.util.QuoteStructBuilder;

public class RFQFactory {

    protected static Map rfqPool;

    private static Map getRFQPool()
    {
        if (rfqPool == null)
        {
            rfqPool = new HashMap();
        }
        return rfqPool;
    }

    /**
       Creates the RFQ with the given struct. Creates each of the necessary instructions to manipulate the state of the RFQ and places the instructions on the RFQInstructionSet.
       @param RFQStruct struct
       @roseuid 3A9FCF4E01FC
     */
    public synchronized static RFQ find(Observer observer, RFQStruct rfqStruct)
    {
        Map rfqs = getRFQPool();
        Integer productKey = new Integer(rfqStruct.productKeys.productKey);
        RFQImpl rfq = (RFQImpl)rfqs.get(productKey);
        if (rfq == null)
        {
            rfq = new RFQImpl(rfqStruct);
            rfqs.put(productKey, rfq);
        }
        rfq.addObserver(observer);
        try {
            rfq.update(rfqStruct);
        } catch (DataValidationException e)
        {
            GUILoggerHome.find().exception("Exception in RFQFactory" , e);
        }

        long[] instructionTimes = rfq.getScheduledTimes(rfqStruct);

        for (int i = 0; i < instructionTimes.length; i++)
        {
            //Create an RFQInstruction and mark the last one to force the delete
            boolean isDelete = (i == (instructionTimes.length -1));
            RFQInstruction instruction = RFQInstructionFactory.create(rfq, instructionTimes[i], isDelete);
            ScheduledInstructionSetFactory.find().put(instruction);
        }
        return rfq;
    }

    /**
       Creates the RFQ with the given struct. Creates each of the necessary instructions to manipulate the state of the RFQ and places the instructions on the RFQInstructionSet.
       @param RFQStruct struct
       @roseuid 3A9FCF4E01FC
     */
    public synchronized static RFQ remove(int pKey)
    {
        Map rfqs = getRFQPool();
        Integer productKey = new Integer(pKey);
        RFQImpl rfq = (RFQImpl)rfqs.get(productKey);
        if (rfq != null)
        {
            RFQStruct rfqStruct = QuoteStructBuilder.cloneRFQStruct(rfq.getRFQStruct());
            rfqStruct.timeToLive = 0;

            try {
                rfq.update(rfqStruct);
            } catch (DataValidationException e)
            {
                GUILoggerHome.find().exception("Exception in RFQFactory" , e);
            }

            long[] instructionTimes = rfq.getScheduledTimes(rfqStruct);

            for (int i = 0; i < instructionTimes.length; i++)
            {
                boolean isDelete = (i == (instructionTimes.length -1));
                RFQInstruction instruction = RFQInstructionFactory.create(rfq, instructionTimes[i], isDelete);
                ScheduledInstructionSetFactory.find().put(instruction);
            }
        }
        return rfq;
    }

    public synchronized static void remove(RFQ rfq)
    {
        Integer productKey = rfq.getProductKey();

        RFQImpl cachedRFQ = (RFQImpl)getRFQPool().get(productKey);
        if (cachedRFQ.equals(rfq))
        {
            getRFQPool().remove(productKey);
        }
    }
}
