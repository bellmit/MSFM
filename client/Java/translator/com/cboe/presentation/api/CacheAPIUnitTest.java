package com.cboe.presentation.api;

import test.framework.TestCase;
import test.framework.TestSuite;

import com.cboe.application.cas.TestCallback;
import com.cboe.application.cas.TestClient;
import com.cboe.application.cas.TestUserAccessV9Factory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.UnitTestHelper;
import com.cboe.delegates.callback.UserSessionAdminConsumerDelegate;
import com.cboe.domain.util.CurrentMarketProductContainerImpl;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiCallback.CMIUserSessionAdminHelper;
import com.cboe.idl.cmiConstants.LoginSessionModes;
import com.cboe.idl.cmiConstants.LoginSessionTypes;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiSession.TradingSessionStateStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.cmiUser.PreferenceStruct;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiV9.UserAccessV9;
import com.cboe.idl.cmiV9.UserSessionManagerV9;
import com.cboe.interfaces.domain.CurrentMarketProductContainer;
import com.cboe.interfaces.presentation.api.MarketMakerAPI;
import com.cboe.interfaces.presentation.marketData.PersonalBestBook;
import com.cboe.interfaces.presentation.marketData.UserMarketDataStruct;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.user.UserStructModel;
import com.cboe.presentation.marketData.PersonalBestBookImpl;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

 /**
 * This class provides means of testing of some CAS caching functionalities
 * @author Mike Pyatesky
  * @version 11/4/1999
 */
public class CacheAPIUnitTest extends TestCase implements EventChannelListener
{

    private static MarketMakerAPI marketMakerAPI;
    private static UserSessionManagerV9 session;
    private static UserAccessV9 userAccessV7;
    private static UserLogonStruct logonStruct;
    private static CMIUserSessionAdmin userListener;
    private static TestCASCallback clientListener;
    private static final String SESSION_NAME = "W_AM1";


    private EventChannelAdapter eventChannel = null;

  /**
   *Constructor
   *
   * @param aTestName String name of the test method
   */
  public CacheAPIUnitTest( String aTestName )
  {
    super(  aTestName );
  }

  /**
   * This method is implementation of EventChannelListener interface,
   * which allows <code>this</code> object to catch and process the events
   * it subscribed for
   */
    public void channelUpdate(ChannelEvent event)
    {
        System.out.println("!!!New Event Recieved!!!" );

        UserMarketDataStruct  userMarketDataStructs;
        PersonalBestBook personalBestBook;
    int channelType = ((ChannelKey)event.getChannel()).channelType;
        Integer key = (Integer)((ChannelKey)event.getChannel()).key;
    Object eventData = event.getEventData();
    switch(channelType)
    {
               case ChannelType.CB_USER_MARKET_DATA:
                        userMarketDataStructs = (UserMarketDataStruct)eventData;
                        System.out.println("!!!UserMarketData event recieved from UserMarketDataCacheProxy!!!" +
                                            "ChannelType = " + channelType + " classKey = " + key.intValue() );
                        debug(userMarketDataStructs);
            break;

               case ChannelType.CB_PERSONAL_BEST_BOOK:
                        personalBestBook = (PersonalBestBook)eventData;
                        System.out.println("!!!PersonalBestBook event recieved from TrdersOrderBook!!!" +
                                            "ProductKey = " + personalBestBook.getProductKey() +
                                            " bid price = " + personalBestBook.getBidPrice() +
                                            " ask price = " + personalBestBook.getAskPrice() );
            break;
               case ChannelType.CB_TRADING_SESSION_STATE:
                        TradingSessionStateStruct tradingSessionState = (TradingSessionStateStruct)eventData;
                        System.out.println("!!!Recieved CB_TRADING_SESSION_STATE event!!!" +
                                            " Session Key = " + tradingSessionState.sessionName +
                                            " Session State = " + tradingSessionState.sessionState);
                        testUnsubscribeTradingSessionStatus();
            break;

        default:
    }
    }

  /**
   * This a convinienece method to subscribe client listener for event channel events
   *
   * @param key Primitive key(product,class,..)
   * @param channelType Primitive event channel type
   * @param userListener Object inmlementation of EventChannelListener interface
   */
    public void subscribeForEvent(int key,int channelType, EventChannelListener userListener)
    {
      try
      {
        eventChannel.addChannelListener(this, userListener, new ChannelKey(channelType, new Integer(key)));
      } catch (Throwable t)
      {
          t.printStackTrace();
      }

    }

  /**
   * This a convinienece method to generate  an array of
   * UserPreferencesStruct objects
   *
   *@return PreferenceStruct[] array of preference structs
   */
    public PreferenceStruct[] getPreferencesStructs()
    {
      PreferenceStruct[] prefStructs = new PreferenceStruct[4];
      prefStructs[0] = new PreferenceStruct("USER1.KEY1.VALUE.VALUE1","VALUE1");
      prefStructs[1] = new PreferenceStruct("USER1.KEY2.VALUE.VALUE2","VALUE2");
      prefStructs[2] = new PreferenceStruct("PREF1.KEY1.VALUE.VALUE3","VALUE3");
      prefStructs[3] = new PreferenceStruct("PREF1.KEY2.VALUE.VALUE4","VALUE4");

      return prefStructs;
    }

  /**
   * This a convinienece method to generate  event channel events
   *
   * @param keys Primitive key(product,class,..)
   * @param channelType Primitive event channel type
   * @param eventData Object event data
   */
    protected void generateEvent(int keys, int channelType, Object eventData)
    {
      ChannelKey key = new ChannelKey(channelType, new Integer(keys));
      ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, eventData);
      EventChannelAdapterFactory.find().dispatch(event);
    }

  /**
  *
  */
  public void testGetUserMarketDataInternal()
  {
      RecapStruct recap1 = UnitTestHelper.createRecapStruct( SESSION_NAME, new ProductKeysStruct( 1,100,(short)1,1 ) , null );
      CurrentMarketStruct bestMarket = UnitTestHelper.createNewMarketStruct(SESSION_NAME, new ProductKeysStruct(1,100,(short)1,1) );
      CurrentMarketStruct bestPublicMarketAtTop  = UnitTestHelper.createNewMarketStruct(SESSION_NAME, new ProductKeysStruct(1,100,(short)1,1) );
      CurrentMarketProductContainer  currentMarket = new CurrentMarketProductContainerImpl(bestMarket, bestPublicMarketAtTop);
      PersonalBestBook personalBestBook = new PersonalBestBookImpl("WAM1", 1,new PriceStruct(),
                                                                new MarketVolumeStruct[2],
                                                                new PriceStruct(),
                                                                new MarketVolumeStruct[2]);
      generateEvent( 1, ChannelType.CB_RECAP_BY_PRODUCT, recap1 );
      generateEvent( 1, ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V3, currentMarket );
      generateEvent( 1, ChannelType.CB_PERSONAL_BEST_BOOK, personalBestBook );
//      generateEvent( 1, ChannelType.CB_PRODUCT_UPDATE_BY_CLASS, productStruct );

      generateEvent( 2, ChannelType.CB_RECAP_BY_PRODUCT, recap1 );
      generateEvent( 2, ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V3, currentMarket );
      generateEvent( 2, ChannelType.CB_PERSONAL_BEST_BOOK, personalBestBook );
//      generateEvent( 2, ChannelType.CB_PRODUCT_UPDATE_BY_CLASS, productStruct );

      RecapStruct recap2 = UnitTestHelper.createRecapStruct(SESSION_NAME,  new ProductKeysStruct( 1,100,(short)1,1 ) , null );

      generateEvent( 1, ChannelType.CB_RECAP_BY_PRODUCT, recap2 );

      System.out.println("Event generated to be processed and cached by UserMarketDataCacheProxy");
  }

    /**
    * test get user market data
    */
    public void testGetUserMarketData()
    {
      try
      {
          SessionProductClass[] classStruct = marketMakerAPI.getProductClassesForSession(SESSION_NAME, ProductTypes.OPTION,this);
          for( int i = 0; i < classStruct.length; i++ )
          {
                System.out.println("Class key = " + classStruct[i].getClassKey());
                SessionProduct[] products = marketMakerAPI.getProductsForSession(SESSION_NAME, classStruct[i].getClassKey(),this);
                for(int j = 0; j < products.length; j++)
                {
                    System.out.println("Product key = " + products[j].getProductKey());
                }
                UserMarketDataStruct [] userMarketData = marketMakerAPI.getUserMarketData(SESSION_NAME, classStruct[i].getClassKey(), this );
                for(int k = 0; k < userMarketData.length; k++)
                {
                    debug(userMarketData[k]);
                }
          }
          marketMakerAPI.getUserMarketData(SESSION_NAME, classStruct[1].getClassKey(), this );

      } catch (Exception e)
      {
          System.out.println("Exception in testGetUserMarketData()");
          e.printStackTrace();
      }
    }

    /**
    * test get personal best book
    */
    public void testGetPersonalBestBook()
    {
      try
      {
          SessionProductClass[] classStruct = marketMakerAPI.getProductClassesForSession(SESSION_NAME, ProductTypes.OPTION,this);
          for( int i = 0; i < classStruct.length; i++ )
          {
                System.out.println("Class key = " + classStruct[i].getClassKey());
                SessionProduct [] products = marketMakerAPI.getProductsForSession(SESSION_NAME, classStruct[i].getClassKey(),this);
                for(int j = 0; j < products.length; j++)
                {
                    marketMakerAPI.getPersonalBestBookByProduct(SESSION_NAME, products[j].getProductKey(), this );
                    System.out.println("**GetersonalBestBookByProduct Product key = " + products[j].getProductKey());
                }
                marketMakerAPI.getPersonalBestBookByClass(SESSION_NAME, classStruct[i].getClassKey(), this );

          }

      } catch (Exception e)
      {
          System.out.println("Exception in testGetUserMarketData()");
          e.printStackTrace();
      }
    }

    /**
    * test get current tradding sessions
    */
    public void testGetCurrentTradingSessions()
    {
      try
      {
          TradingSessionStruct []tradingSessions =marketMakerAPI.getCurrentTradingSessions(this);
          for( int i = 0; i < tradingSessions.length; i++ )
          {
              System.out.println( "Recieved TRADING SESSION STRUCTS event" +
                                  " Session Name = " + tradingSessions[i].sessionName );
          }
      } catch (Exception e)
      {
          System.out.println("Exception in testGetCurrentTradingSessions()");
          e.printStackTrace();
      }
    }

    /**
    * test unsubscribe trading session status
    */
    public void testUnsubscribeTradingSessionStatus()
    {
        try
        {
            marketMakerAPI.unsubscribeTradingSessionStatus(this);
            System.out.println("**Unsubscribed Trading Session Status**");

        } catch (Exception e)
        {
            System.out.println("Exception in testGetClassesForTradingSession()");
            e.printStackTrace();
      }
    }

    /**
    * test set user preferences
    */
    public void testSetUserPreferences()
    {
      PreferenceStruct[] prefs = getPreferencesStructs();
      try
      {

          marketMakerAPI.setUserPreferences( prefs );
          System.out.println( "********** User Preferences Set ************** ");
          for( int i = 0; i < prefs.length; i++ )
          {
              System.out.println( " name -> " + prefs[i].name );
          }
      } catch (Throwable t)
      {
          t.printStackTrace();
      }
    }

    /**
     * Test removing the user preferences contained within the given sequence of
     * preference structs.
     *
     */
    public void testRemoveUserPreference()
    {
      try
      {
          marketMakerAPI.removeUserPreference(getPreferencesStructs());
          System.out.println( "**********User Preferences Removed ************** " );

      } catch (Throwable t)
      {
          t.printStackTrace();

      }
    }

    /**
     * Test getting all user preferences for this user.
     *
     */
    public void testGetAllUserPreferences()
    {
      try
      {
         PreferenceStruct[] prefs = marketMakerAPI.getAllUserPreferences();
         System.out.println( "**********All User Preferences Recived ******************* " );

         for(int i = 0; i < prefs.length; i++)
         {
            System.out.println( prefs[i].name + " -> " + prefs[i].value );
         }

      } catch (Throwable t)
      {
          t.printStackTrace();

      }

    }

    /**
     * Test getting all user preferences for this user that begin with the given prefix.
     *
     */
    public void  testGetUserPreferencesByPrefix()
    {
      try
      {
          String []prefix = {"PREF1","PREF1.KEY1","USER1","USER1.KEY2"};
          for( int j = 0; j < prefix.length;j++ )
          {
              PreferenceStruct[] prefs = marketMakerAPI.getUserPreferencesByPrefix( prefix[j] );
              System.out.println( "**********User Preferences by Prefix = " + prefix[j]);

              for(int i = 0; i < prefs.length; i++)
              {
                  System.out.println( prefs[i].name + " -> " + prefs[i].value );
              }
        }
      } catch (Throwable t)
      {
          t.printStackTrace();

      }

    }

    /**
     * Test removing all user preferences for this user that begin with the given prefix.
     *
     */
    public void testRemoveUserPreferencesByPrefix()
    {
      try
      {
          String []prefix = {"PREF1","KEY2"};
          for( int j = 0; j < prefix.length;j++ )
          {
              marketMakerAPI.removeUserPreferencesByPrefix( prefix[j] );
              System.out.println( "**********User Preferences Removed by Prefix = " + prefix[j]);
          }
      } catch (Throwable t)
      {
          t.printStackTrace();

      }
    }

    /**
     * Test getting all system preferences for this user.
     *
     */
    public void testGetAllSystemPreferences()
    {
      try
      {
         PreferenceStruct[] prefs = marketMakerAPI.getAllSystemPreferences();
         System.out.println( "**********All System Preferences Recived ******************* " );

         for(int i = 0; i < prefs.length; i++)
         {
            System.out.println( prefs[i].name + " -> " + prefs[i].value );
         }

      } catch (Throwable t)
      {
          t.printStackTrace();

      }

    }

    /**
     * Test getting all system preferences for this user that begin with the given prefix.
     *
     */
    public void testGetSystemPreferencesByPrefix()
    {
      try
      {
          String []prefix = {"com","com.cboe.sbt1","com.cboe.sbt2"};
          for( int j = 0; j < prefix.length;j++ )
          {
              PreferenceStruct[] prefs = marketMakerAPI.getSystemPreferencesByPrefix( prefix[j] );
              System.out.println( "**********System Preferences Retrieved by Prefix = " + prefix[j]);

              for(int i = 0; i < prefs.length; i++)
              {
                  System.out.println( prefs[i].name + " -> " + prefs[i].value );
              }
        }
      } catch (Throwable t)
      {
          t.printStackTrace();

      }

    }

    /**
     * Test getting logged out.
     */
    public void testLogOut()
    {
        try
        {
            marketMakerAPI.logout();
            System.out.println( "**********User Logged Out *****************" );

        } catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    /**
     * Test getting loged on.
     */
    public void testLogon()
    {
        try
        {
            session = userAccessV7.logon(logonStruct, LoginSessionTypes.PRIMARY, userListener, true);

            marketMakerAPI = MarketMakerAPIFactory.create(session, userListener, clientListener, true);
            System.out.println( "**********User Logon OK *****************" );

        } catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    /**
     * Test getting valid user.
     *
     */
    public void testGetValidUser()
    {
      try
      {
          UserStructModel user = marketMakerAPI.getValidUser();
          System.out.println( "**********Recieved Valid User *****************\n" +
                              " User Name -> " + user.getFullName() + "\n User ID -> " + user.getUserId() );
      } catch (Throwable t)
      {
          t.printStackTrace();

      }
    }
  /**
  * Starts the application and runs the test suite.
  * @param args arguments
  */
  public static void main(java.lang.String[] args)
  {

      try
      {
            ////////// MUST BE CALLED /////////
        TestClient.initORBConnection(args);
//        TestClient.initFFEnv();
        if ( session == null )
        {
            userAccessV7 =  TestUserAccessV9Factory.find();
            logonStruct = new UserLogonStruct("sbtUs", "", "", LoginSessionModes.STAND_ALONE_TEST);
//            clientListener = new TestCASCallback("CacheAPIUnitTest");
            UserSessionAdminConsumerDelegate delegate = new UserSessionAdminConsumerDelegate(new TestCallback());
            org.omg.CORBA.Object corbaObject = ServicesHelper.connectToOrb (delegate);
            CMIUserSessionAdmin userSessionCallback = CMIUserSessionAdminHelper.narrow (corbaObject);
            session = userAccessV7.logon(logonStruct, LoginSessionTypes.PRIMARY, userListener, true);
        }

        marketMakerAPI = MarketMakerAPIFactory.create(session, userListener, clientListener, true);


//            traderCacheAPI = TraderCacheAPIFactory.create(session, userListener);
        } catch(Throwable t)
        {
            t.printStackTrace();
        }

//      String[] testArgs = {CacheAPIUnitTest.class.getName()};
//      test.ui.TestRunner.main(testArgs);
    test.textui.TestRunner.run(suite());
  }


  public void setUp()
  {
//      initUserMarketDataCache();
  }

  /**
   * This is a convinience method to System out the test results
   * @param object Object which members will be getting printed
   */
  private void debug(Object object)
  {
      UserMarketDataStruct userMarketDataStructs = (UserMarketDataStruct)object;


      System.out.println("UserMarketDataStruc classKey = " + userMarketDataStructs.productKeys);
      System.out.println("UserMarketDataStruct RecapStruct = " + userMarketDataStructs.recap);
      System.out.println("UserMarketDataStruct CurrentMarketStruct currentMarket.bidPrice.whole = " + userMarketDataStructs.currentMarket.bidPrice.whole);
      System.out.println("UserMarketDataStruct CurrentMarketStruct currentMarketPublic" + userMarketDataStructs.currentMarketPublic == null ? " = null" : (".bidIsMarketBest = " + userMarketDataStructs.currentMarketPublic.bidIsMarketBest));
      System.out.println("UserMarketDataStruct CurrentMarketStruct.recap.productInformation.productSymbol = " + userMarketDataStructs.recap.productInformation.productSymbol);
      System.out.println("UserMarketDataStruct PersonalBookStruct = " + userMarketDataStructs.personalBestBook);

  }

  /**
  *
  * @return TestSuit
  */
  public static TestSuite suite()
  {
        TestSuite suite = new TestSuite();
        suite.addTest(new CacheAPIUnitTest("testGetUserMarketData"));
        suite.addTest(new CacheAPIUnitTest("testGetPersonalBestBook"));
        suite.addTest(new CacheAPIUnitTest("testGetCurrentTradingSessions"));
        suite.addTest(new CacheAPIUnitTest("testGetClassesForTradingSession"));
//        suite.addTest(new CacheAPIUnitTest("testUnsubscribeTradingSessionStatus"));

        suite.addTest(new CacheAPIUnitTest("testGetValidUser"));

        suite.addTest(new CacheAPIUnitTest("testGetAllUserPreferences"));

        suite.addTest(new CacheAPIUnitTest("testSetUserPreferences"));
        suite.addTest(new CacheAPIUnitTest("testGetAllUserPreferences"));

        suite.addTest(new CacheAPIUnitTest("testRemoveUserPreference"));
        suite.addTest(new CacheAPIUnitTest("testGetAllUserPreferences"));

        suite.addTest(new CacheAPIUnitTest("testSetUserPreferences"));
        suite.addTest(new CacheAPIUnitTest("testGetUserPreferencesByPrefix"));
        suite.addTest(new CacheAPIUnitTest("testRemoveUserPreferencesByPrefix"));
        suite.addTest(new CacheAPIUnitTest("testGetAllUserPreferences"));

        suite.addTest(new CacheAPIUnitTest("testGetAllSystemPreferences"));
        suite.addTest(new CacheAPIUnitTest("testGetSystemPreferencesByPrefix"));

        suite.addTest(new CacheAPIUnitTest("testLogOut"));
        suite.addTest(new CacheAPIUnitTest("testLogon"));
        suite.addTest(new CacheAPIUnitTest("testGetAllUserPreferences"));
        suite.addTest(new CacheAPIUnitTest("testGetUserMarketData"));
        suite.addTest(new CacheAPIUnitTest("testGetPersonalBestBook"));
        suite.addTest(new CacheAPIUnitTest("testGetCurrentTradingSessions"));

//        suite.addTest(new CacheAPIUnitTest("testLogOut"));

      return suite;
  }
}
