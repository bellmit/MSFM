package com.cboe.application.test;
import com.cboe.exceptions.*;
import com.cboe.idl.cmi.UserSessionManager;
import com.cboe.idl.cmi.Version;
import com.cboe.idl.cmiV2.UserAccessV2;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.idl.cmiV2.UserSessionManagerV2;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.cmiV3.UserAccessV3;
import com.cboe.idl.cmiV3.UserSessionManagerV3;
import com.cboe.domain.util.*;
import com.cboe.application.cas.TestUserAccessV2Factory;
import com.cboe.application.cas.TestCallback;
import com.cboe.application.cas.TestUserAccessV3Factory;
import com.cboe.application.shared.RemoteConnection;
import com.cboe.application.shared.RemoteConnectionFactory;
//import com.cboe.delegates.callback.UserSessionAdminConsumerDelegate;
import com.cboe.delegates.callback.CurrentMarketConsumerDelegate;
import com.cboe.delegates.callback.CurrentMarketV2ConsumerDelegate;
import com.cboe.delegates.callback.CurrentMarketV3ConsumerDelegate;
import com.cboe.delegates.callback.BookDepthConsumerDelegate;
import com.cboe.delegates.callback.ExpectedOpeningPriceConsumerDelegate;
import com.cboe.delegates.callback.ExpectedOpeningPriceV2ConsumerDelegate;
import com.cboe.delegates.callback.NBBOConsumerDelegate;
import com.cboe.delegates.callback.NBBOV2ConsumerDelegate;
import com.cboe.delegates.callback.RecapConsumerDelegate;
import com.cboe.delegates.callback.RecapV2ConsumerDelegate;
import com.cboe.delegates.callback.TickerConsumerDelegate;
import com.cboe.delegates.callback.TickerV2ConsumerDelegate;
import com.cboe.delegates.callback.QuoteStatusConsumerDelegate;
import com.cboe.delegates.callback.RFQConsumerDelegate;
import com.cboe.util.event.EventChannelAdapterFactory;
import java.util.Properties;
import java.io.FileInputStream;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CORBA.ORB;

/**
 * @author Vaziranc
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MarketDataClient extends TestAPIClient {


    private static boolean loggedIn;
    private static String classSymbol;
    private static String sessionName;
    private static int classKey;
    private static int productKey;
    private static short productType;
    private static short queue_action;


    private static double currentPrice;
    private static com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer currentMarketV3Consumer;


    private static CMIRecapConsumer getRecapConsumer()
    {
        RecapConsumerDelegate delegate= new RecapConsumerDelegate(getCallback());
        org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
        CMIRecapConsumer theRecapConsumer = CMIRecapConsumerHelper.narrow (corbaObject);
        return  theRecapConsumer;
    }

    private static CMICurrentMarketConsumer getCurrentMarketConsumer()
    {
        CurrentMarketConsumerDelegate delegate= new CurrentMarketConsumerDelegate(getCallback());
        org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
        CMICurrentMarketConsumer theCurrentMarketConsumer = CMICurrentMarketConsumerHelper.narrow (corbaObject);
        return  theCurrentMarketConsumer;
    }


    /**
     * Starts the application.
     * @param args an array of command-line arguments
     */
    public static void main(String[] args)
    {

        Properties loadedProperties = getProperties();
        try
        {

            initORBConnection(args);
            initPOA();
            logon();

            sessionName = loadedProperties.getProperty(SESSION_NAME).toString();
            classSymbol = loadedProperties.getProperty(CLASS_SYMBOL).toString();
            productType = Short.parseShort(loadedProperties.getProperty(PRODUCT_TYPE).toString());
            //classKey = session.getProductQuery().getClassBySymbol(productType, classSymbol).classKey;
            //productKey = session.getProductQuery().getProductsByClass(classKey)[0].productKeys.productKey;
            queue_action = Short.parseShort(loadedProperties.getProperty(QUE_ACTION).toString());
            classKey = Integer.parseInt(loadedProperties.getProperty(CLASS_KEY).toString());
//            productKey = Integer.parseInt(loadedProperties.getProperty(PRODUCT_KEY).toString());


            //classKey = 196821;
            //classKey = 917647; //IBM
            //productKey = 106431946;
            //productKey = 918827; //IBM Jan07 55.00 call
            queue_action = QueueActions.OVERLAY_LAST;

            //QuoteEntryStruct quote;
            //QuoteEntryStruct[] blockQuotes;


            System.out.println("1)subscribeCurrentMarketForClassV3");
            System.out.println("2)unsubscribeCurrentMarketForClassV3");
            System.out.println("3)subscribeCurrentMarketForProductV3");
            System.out.println("4)unsubscribeCurrentMarketForProductV3");


            int choice = 0;
            while ( choice !=-1 )
            {
                int ch;
                choice = 0;
                while ((ch = System.in.read ()) != '\n')
                {
                   if (ch > '0' && ch <= '9')
                   {
                       choice *= 10;
                       choice += ch - '0';
                   }
                   else
                       break;
                    System.out.println("Entering your choice :");
                    System.out.println ("choice = " + choice);


                    switch (choice)
                    {
                        case 1:
                            subscribeCurrentMarketForClassV3(sessionName,classKey, getCurrentMarketV3Consumer(), queue_action);
                            break;
                        case 2:
                            unsubscribeCurrentMarketForClassV3(sessionName,classKey, getCurrentMarketV3Consumer());
                            break;
                        case 3:
                            subscribeCurrentMarketForProductV3(sessionName,productKey, getCurrentMarketV3Consumer(),queue_action);
                            break;
                        case 4:
                            unsubscribeCurrentMarketForProductV3(sessionName,productKey, getCurrentMarketV3Consumer());
                            break;
                    }
                }

            }//end of while (choice)..
        }
        catch (Exception e)
        {
            System.out.println("caught " + e);
            e.printStackTrace();
        }

        System.out.println("done");
        System.exit(1);

    }


    private static void subscribeCurrentMarketForClassV3(String sessionName,
                                                         int classKey,
                                                         com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener,
                                                         short actionOnQueue)

    {
        System.out.println("calling subscribeCurrentMarketForClassV3 action:"+actionOnQueue);
//        short action = QueueActions.OVERLAY_LAST;

        try
        {
            sessionV3.getMarketQueryV3().subscribeCurrentMarketForClassV3(sessionName, classKey, clientListener, (short)2);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("subscribeCurrentMarketForClassV3 done");

    }


    private static void unsubscribeCurrentMarketForClassV3(String sessionName,
                                                         int classKey,
                                                         com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener)

    {
        System.out.println("calling unsubscribeCurrentMarketForClassV3");
        short action = QueueActions.OVERLAY_LAST;

        try
        {
            sessionV3.getMarketQueryV3().unsubscribeCurrentMarketForClassV3(sessionName, classKey, clientListener);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("unsubscribeCurrentMarketForClassV3 done");

    }

    private static void subscribeCurrentMarketForProductV3(String sessionName,
                                                         int productKey,
                                                         com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener,
                                                         short actionOnQueue)

    {
        System.out.println("calling subscribeCurrentMarketForProductV3");
        short action = QueueActions.OVERLAY_LAST;

        try
        {
            sessionV3.getMarketQueryV3().subscribeCurrentMarketForProductV3(sessionName,productKey,clientListener,action);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("subscribeCurrentMarketForProductV3 done");

    }


    private static void unsubscribeCurrentMarketForProductV3(String sessionName,
                                                         int productKey,
                                                         com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener)

    {
        System.out.println("calling unsubscribeCurrentMarketForClassV3");
        short action = QueueActions.OVERLAY_LAST;

        try
        {
            sessionV3.getMarketQueryV3().unsubscribeCurrentMarketForProductV3(sessionName, productKey, clientListener);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("unsubscribeCurrentMarketForProductV3 done");

    }



    private static com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer getCurrentMarketV3Consumer()
    {
        if (currentMarketV3Consumer == null)
        {
            //com.cboe.idl.cmiCallbackV2.POA_CMIQuoteStatusConsumer_tie delegateV2= new com.cboe.idl.cmiCallbackV2.POA_CMIQuoteStatusConsumer_tie(getCallback());
            com.cboe.idl.cmiCallbackV3.POA_CMICurrentMarketConsumer_tie delegateV3 = new com.cboe.idl.cmiCallbackV3.POA_CMICurrentMarketConsumer_tie(getCallback());
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegateV3);
            currentMarketV3Consumer = com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumerHelper.narrow (corbaObject);

        }
        return currentMarketV3Consumer;
    }

}


