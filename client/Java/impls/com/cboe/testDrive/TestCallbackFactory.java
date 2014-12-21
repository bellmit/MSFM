package com.cboe.testDrive;

import com.cboe.testDrive.*;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.idl.cmiCallback.CMIQuoteStatusConsumerHelper;
import com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer;

public class TestCallbackFactory
{
    public static com.cboe.idl.cmiCallbackV2.CMIRecapConsumer getV2RecapConsumer(RecapCallbackDV2 recapClient)
    {
        org.omg.CORBA.Object callbackObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(recapClient);
        com.cboe.idl.cmiCallbackV2.CMIRecapConsumer consumer = com.cboe.idl.cmiCallbackV2.CMIRecapConsumerHelper.narrow(callbackObject);
        return consumer;
    }

    public static com.cboe.idl.cmiCallback.CMIRecapConsumer getV1RecapConsumer(RecapCallbackD recapClient)
    {
        org.omg.CORBA.Object callbackObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(recapClient);
        com.cboe.idl.cmiCallback.CMIRecapConsumer consumer = com.cboe.idl.cmiCallback.CMIRecapConsumerHelper.narrow(callbackObject);
        return consumer;
    }

    public static com.cboe.idl.cmiCallbackV2.CMITickerConsumer getV2TickerConsumer(TickerCallbackDV2 tickerClient)
    {
        org.omg.CORBA.Object callbackObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(tickerClient);
        com.cboe.idl.cmiCallbackV2.CMITickerConsumer consumer = com.cboe.idl.cmiCallbackV2.CMITickerConsumerHelper.narrow(callbackObject);
        return consumer;
    }

    public static com.cboe.idl.cmiCallback.CMITickerConsumer getV1TickerConsumer(TickerCallbackD tickerClient)
    {
        org.omg.CORBA.Object callbackObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(tickerClient);
        com.cboe.idl.cmiCallback.CMITickerConsumer consumer = com.cboe.idl.cmiCallback.CMITickerConsumerHelper.narrow(callbackObject);
        return consumer;
    }

    public static com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer getV2CurrentMarketConsumer(CurrentMarketCallbackDV2 currentMarketClient)
    {
        org.omg.CORBA.Object callbackObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(currentMarketClient);
        com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer consumer = com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumerHelper.narrow(callbackObject);
        return consumer;
    }

    public static com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer getV3CurrentMarketConsumer(CurrentMarketCallbackDV3 currentMarketClient)
    {
        org.omg.CORBA.Object callbackObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(currentMarketClient);
        com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer consumer = com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumerHelper.narrow(callbackObject);
        return consumer;
    }

    public static com.cboe.idl.cmiCallback.CMICurrentMarketConsumer getV1CurrentMarketConsumer(CurrentMarketCallbackD currentMarketClient)
    {
        org.omg.CORBA.Object callbackObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(currentMarketClient);
        com.cboe.idl.cmiCallback.CMICurrentMarketConsumer consumer = com.cboe.idl.cmiCallback.CMICurrentMarketConsumerHelper.narrow(callbackObject);
        return consumer;
    }

    public static com.cboe.idl.cmiCallback.CMICurrentMarketConsumer getV1CurrentMarketConsumerR(CurrentMarketCallbackR currentMarketClient)
    {
        org.omg.CORBA.Object callbackObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(currentMarketClient);
        com.cboe.idl.cmiCallback.CMICurrentMarketConsumer consumer = com.cboe.idl.cmiCallback.CMICurrentMarketConsumerHelper.narrow(callbackObject);
        return consumer;
    }

    public static com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer getV2CurrentMarketConsumerR(CurrentMarketCallbackRV2 currentMarketClient)
    {
        org.omg.CORBA.Object callbackObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(currentMarketClient);
        com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer consumer = com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumerHelper.narrow(callbackObject);
        return consumer;
    }

    public static com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer getV2BestBookConsumer(BestBookCallbackDV2 bestbookClient)
    {
        org.omg.CORBA.Object callbackObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(bestbookClient);
        com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer consumer = com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumerHelper.narrow(callbackObject);
        return consumer;
    }

    public static com.cboe.idl.cmiCallback.CMIOrderBookConsumer getV1BestBookConsumer(BestBookCallbackD bestbookClient)
    {
        org.omg.CORBA.Object callbackObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(bestbookClient);
        com.cboe.idl.cmiCallback.CMIOrderBookConsumer consumer = com.cboe.idl.cmiCallback.CMIOrderBookConsumerHelper.narrow(callbackObject);
        return consumer;
    }

    public static com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer getV1QuoteStatusConsumer(QuoteCallbackD quoteClient)
    {
        org.omg.CORBA.Object callbackObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(quoteClient);
        CMIQuoteStatusConsumer consumer = com.cboe.idl.cmiCallback.CMIQuoteStatusConsumerHelper.narrow(callbackObject);
        return consumer;
    }

    public static com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer getV2QuoteStatusConsumer(QuoteCallbackDV2 quoteClient)
    {
        org.omg.CORBA.Object callbackObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(quoteClient);
        com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer consumer = com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumerHelper.narrow(callbackObject);
        return consumer;
    }

    public static com.cboe.idl.cmiCallbackV2.CMINBBOConsumer getV2NBBOConsumer(NBBOCallbackDV2 quoteClient)
    {
        org.omg.CORBA.Object callbackObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(quoteClient);
        com.cboe.idl.cmiCallbackV2.CMINBBOConsumer consumer = com.cboe.idl.cmiCallbackV2.CMINBBOConsumerHelper.narrow(callbackObject);
        return consumer;
    }

    public static com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer getV2ExpectedOpeningPriceConsumer(ExpectedOpeningPriceCallbackDV2 quoteClient)
    {
        org.omg.CORBA.Object callbackObject = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(quoteClient);
        com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer consumer = com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumerHelper.narrow(callbackObject);
        return consumer;
    }

    public static com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumer getV4CurrentMarketConsumer(
            CurrentMarketCallbackDV4 currentMarketClient)
    {
        org.omg.CORBA.Object callbackObject =
                (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(currentMarketClient);
        return com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumerHelper.narrow(callbackObject);
    }

    public static com.cboe.idl.cmiCallbackV4.CMIRecapConsumer getV4RecapConsumer(RecapCallbackDV4 recapClient)
    {
        org.omg.CORBA.Object callbackObject =
                (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(recapClient);
        return com.cboe.idl.cmiCallbackV4.CMIRecapConsumerHelper.narrow(callbackObject);
    }

    public static com.cboe.idl.cmiCallbackV4.CMITickerConsumer getV4TickerConsumer(TickerCallbackDV4 tickerClient)
    {
        org.omg.CORBA.Object callbackObject =
                (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(tickerClient);
        return com.cboe.idl.cmiCallbackV4.CMITickerConsumerHelper.narrow(callbackObject);
    }
}
