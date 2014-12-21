package com.cboe.cfix.cas.casLogin;

import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumerPOA;
import com.cboe.idl.cmiCallbackV4.CMIRecapConsumerPOA;
import com.cboe.idl.cmiCallbackV4.CMITickerConsumerPOA;
import com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumerPOA;
import com.cboe.idl.cmiCallback.CMINBBOConsumerPOA;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.interfaces.cfix.CfixCasLoginHome;
import com.cboe.interfaces.cfix.CfixCasLogin;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import org.omg.PortableServer.POA;

/**
 * Created by IntelliJ IDEA.
 * User: lip
 * Date: Jun 16, 2010
 * Time: 10:52:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class CfixCasLoginSubscriptionTest {
    //public static void main(String [ ] args)
    public static void TestSubscription(POA rootPOA)
    {
        try{
            CfixCasLoginHome home = (CfixCasLoginHome) HomeFactory.getInstance().findHome(CfixCasLoginHome.HOME_NAME);
            CfixCasLogin myCfixCasLogin = home.find();

            int myClassKey = 69206019; // A (option)
            int myProductKey = 667421478;  // A (option)  A July-18-09 12.50 Call
            String mySessionName = "W_MAIN";
            short myQueueActions = QueueActions.NO_ACTION;

            CurrentMarketConsumerCallback myMarketConsumerCallback = new CurrentMarketConsumerCallback();
            rootPOA.activate_object(myMarketConsumerCallback);
            myCfixCasLogin.subscribeCurrentMarket(myClassKey, myMarketConsumerCallback._this(), myQueueActions);

            ExpectedOpeningPriceConsumerCallback myExpectedOpeningPriceConsumerCallback = new ExpectedOpeningPriceConsumerCallback();
            rootPOA.activate_object(myExpectedOpeningPriceConsumerCallback);
            myCfixCasLogin.subscribeExpectedOpeningPrice(mySessionName, myClassKey, myExpectedOpeningPriceConsumerCallback._this());

            RecapConsumerCallback myRecapConsumerCallback = new RecapConsumerCallback();
            rootPOA.activate_object(myRecapConsumerCallback);
            myCfixCasLogin.subscribeRecap(myClassKey, myRecapConsumerCallback._this() , myQueueActions);

            TickerConsumerCallback myTickerConsumerCallback = new TickerConsumerCallback();
            rootPOA.activate_object(myTickerConsumerCallback);
            myCfixCasLogin.subscribeTicker(myClassKey, myTickerConsumerCallback._this(), myQueueActions);

            NBBOConsumerCallback myNBBOConsumerCallback = new NBBOConsumerCallback();
            rootPOA.activate_object(myNBBOConsumerCallback);
            myCfixCasLogin.subscribeNBBOForClass(mySessionName, myClassKey, myNBBOConsumerCallback._this());
//            myCfixCasLogin.subscribeNBBOForProduct(mySessionName, myProductKey, myNBBOConsumerCallback._this());
        }catch(Exception e){
            Log.exception(e);
        }
    }
     public static class CurrentMarketConsumerCallback extends CMICurrentMarketConsumerPOA
     {
        public CurrentMarketConsumerCallback(){

        }

        public void acceptCurrentMarket(CurrentMarketStructV4[] currentMarketStructV4s, CurrentMarketStructV4[] currentMarketStructV4s1, int i, int i1, short i2) {
           Log.debug("Received currentMarket data.");
           Log.debug("ClassKey="+currentMarketStructV4s[0].classKey);
           Log.debug("bid="+currentMarketStructV4s[0].bidPrice);
           Log.debug("ask="+currentMarketStructV4s[0].askPrice);
        }
     }

    public static class RecapConsumerCallback extends CMIRecapConsumerPOA
    {
        public RecapConsumerCallback(){}

        public void acceptRecap(RecapStructV4[] recapStructV4s, int i, int i1, short i2) {
            Log.debug("Received Recap data.");
            Log.debug("exchange="+recapStructV4s[0].exchange);
            Log.debug("classKey="+recapStructV4s[0].classKey);
            Log.debug("productKey="+recapStructV4s[0].productKey);
            Log.debug("highPrice="+recapStructV4s[0].highPrice);
            Log.debug("lowPrice="+recapStructV4s[0].lowPrice);
            Log.debug("openPrice="+recapStructV4s[0].openPrice);
        }

        public void acceptLastSale(LastSaleStructV4[] lastSaleStructV4s, int i, int i1, short i2) {
            Log.debug("Received LastSale data.");
            Log.debug("exchange="+lastSaleStructV4s[0].exchange);
            Log.debug("classKey="+lastSaleStructV4s[0].classKey);
            Log.debug("productKey="+lastSaleStructV4s[0].productKey);
            Log.debug("lastSalePrice="+lastSaleStructV4s[0].lastSalePrice);
            Log.debug("lastSaleVolume="+lastSaleStructV4s[0].lastSaleVolume);
            Log.debug("lastSaleTime="+lastSaleStructV4s[0].lastSaleTime);
            Log.debug("netPriceChange="+lastSaleStructV4s[0].netPriceChange);
        }
    }

    public static class TickerConsumerCallback extends CMITickerConsumerPOA
    {
       public TickerConsumerCallback(){}

       public void acceptTicker(TickerStructV4[] tickerStructV4s, int i, int i1, short i2) {
           Log.debug("Received Ticker data.");
           Log.debug("exchange="+tickerStructV4s[0].exchange);
           Log.debug("classKey="+tickerStructV4s[0].classKey);
           Log.debug("productKey="+tickerStructV4s[0].productKey);
           Log.debug("tradePrice="+tickerStructV4s[0].tradePrice);
           Log.debug("tradeVolume="+tickerStructV4s[0].tradeVolume);
       }
    }

    public static class NBBOConsumerCallback extends CMINBBOConsumerPOA
    {
        public NBBOConsumerCallback(){}
        
        public void acceptNBBO(NBBOStruct[] nbboStructs) {
           Log.debug("Received NBBO data.");
           Log.debug("exchange="+nbboStructs[0].sessionName);
           Log.debug("productKey="+nbboStructs[0].productKeys.productKey);
           Log.debug("tradePrice="+nbboStructs[0].bidPrice);
           Log.debug("tradeVolume="+nbboStructs[0].bidExchangeVolume);
           Log.debug("classKey="+nbboStructs[0].askPrice);
           Log.debug("productKey="+nbboStructs[0].askExchangeVolume);
        }
    }

    public static class ExpectedOpeningPriceConsumerCallback extends CMIExpectedOpeningPriceConsumerPOA
    {
       public ExpectedOpeningPriceConsumerCallback(){

       }
       public void acceptExpectedOpeningPrice(ExpectedOpeningPriceStruct expectedOpeningPriceStruct) {
            Log.debug("Received ExpectedOpeningPrice data.");
            Log.debug("sessionName="+expectedOpeningPriceStruct.sessionName);
            Log.debug("productKeys="+expectedOpeningPriceStruct.productKeys);
            Log.debug("imbalanceQuantity="+expectedOpeningPriceStruct.imbalanceQuantity);
            Log.debug("expectedOpeningPrice.whole="+expectedOpeningPriceStruct.expectedOpeningPrice.whole);
            Log.debug("expectedOpeningPrice.fraction="+expectedOpeningPriceStruct.expectedOpeningPrice.fraction);
       }
    }

}
