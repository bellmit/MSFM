package com.cboe.testDrive;

import com.cboe.testDrive.*;
import java.util.*;
import java.io.*;
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.delegates.application.*;
import com.cboe.delegates.callback.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.domain.util.ReflectiveStructBuilder;
import com.cboe.exceptions.*;


public class CASTestQuoteQueue extends CASTestQuote
{
    QuoteQueue qq;
    QuoteBlock bq;
    CASTestQuoteBlockInjector generator = null;

    public CASTestQuoteQueue(TestParameter parm, SessionManagerStructV2 sessionManagerStruct, CASMeter casMeter, int tid, QuoteQueue qq)
        throws Exception
    {
        super(parm, sessionManagerStruct, casMeter);
        this.qq = qq;
        this.generator = new CASTestQuoteBlockInjector(parm, casMeter, qq);
    }

    private void enterQuoteBlock(int classKey, QuoteEntryStruct[] quotes)
        throws DataValidationException, TransactionFailedException, CommunicationException, AuthorizationException, NotAcceptedException, SystemException
    {
        if (parm.callbackVersion.equals("V2"))
        {
            quoteV2.acceptQuotesForClassV2(classKey, quotes);
        } else
        {
            quoteV1.acceptQuotesForClass(classKey, quotes);
        }
    }

    public  void run()
    {
        generator.start();

        while (!parm.threadDone)
        {
            try
            {
                synchronized (qq)
                {
                    bq = qq.getNextQuoteBlock();
                }

                if (bq != null)
                {
                    int curID = Integer.parseInt(bq.qs[0].userAssignedId);
                    casMeter.setCallTime(curID);
                    casMeter.setMethodCalled(curID, 'Q');
                    this.enterQuoteBlock(bq.itsClassKey, bq.qs);
                    casMeter.setFinishTime(curID);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                casMeter.setFinishTime(Integer.parseInt(bq.qs[0].userAssignedId));
                casMeter.setEndTime(Integer.parseInt(bq.qs[0].userAssignedId));
            }
        }

        generator.setDone();

        System.out.println("Thread Done. Stop sending quotes.");
        try {
            Thread.currentThread().sleep(120000);
            casMeter.printData();
        } catch (Exception e){}

        while (true)
        {
        }
    }
}
