package com.cboe.testDrive;

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.application.test.*;
import com.cboe.testDrive.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.interfaces.callback.CurrentMarketConsumer;

import java.io.*;
import java.util.*;
import java.text.*;

public class CurrentMarketCallbackR extends com.cboe.idl.cmiCallback.CMICurrentMarketConsumerPOA
{
    private PrintWriter writer = null;
    private MarketDataCounter counter;

    public CurrentMarketCallbackR()
    {
        try {
             writer =  new PrintWriter(new FileWriter("mdRecord.out"));
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        writer.println("time_receive                        time_sent    session     classkey    productkey      bside price     bside quantity      sside price     sside quantity");
        writer.flush();
    }

    public void addMessageCounter(MarketDataCounter counter)
    {
        this.counter = counter;
    }

    public void acceptCurrentMarket(CurrentMarketStruct[] currentMarket)
    {
        String timestamp = "";

        SimpleDateFormat formatter = new SimpleDateFormat ("EEE MMM dd yyyy HH:mm:ss:SSS");

        try {
            for (int i=0; i < currentMarket.length; i++)
            {
                timestamp =  formatter.format(new Date());

                int bidQuantity = 0;
                int askQuantity = 0;
                if (currentMarket[i].bidSizeSequence.length > 0)
                {
                    bidQuantity = currentMarket[i].bidSizeSequence[0].quantity;
                }

                if (currentMarket[i].askSizeSequence.length > 0)
                {
                    askQuantity = currentMarket[i].askSizeSequence[0].quantity;
                }

                if (counter != null)
                {
                    counter.acceptMessage(currentMarket[i].productKeys.classKey);
                }

                System.out.println("Receiving marketdata........... " + timestamp);
                writer.println( timestamp + ", " +
                currentMarket[i].sentTime.hour + ":" + currentMarket[i].sentTime.minute + ":" + currentMarket[i].sentTime.second + ":" + currentMarket[i].sentTime.fraction + ", " +
                currentMarket[i].sessionName + ", " +
                currentMarket[i].productKeys.classKey + ", " +
                currentMarket[i].productKeys.productKey + ", " + "$" +
                currentMarket[i].bidPrice.whole + "." + currentMarket[i].bidPrice.fraction + ", " +
                bidQuantity + ", " + "$" +
                currentMarket[i].askPrice.whole + "." + currentMarket[i].askPrice.fraction + ", " +
                askQuantity );
                writer.flush();

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}