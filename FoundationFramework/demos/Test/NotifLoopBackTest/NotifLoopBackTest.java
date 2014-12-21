
// QuoteLoopbackTest.java

import IE.Iona.OrbixWeb._CORBA ; 

import org.omg.CosEventComm.*;
import org.omg.CosTypedEventComm.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.IntHolder;

import org.omg.CosNotification.EventType;


import com.cboe.ORBInfra.ORB.Orb;
import com.cboe.EventService.Test.EventServiceHelper;
import com.cboe.NotificationService.ConsumerSide.*;
import NotifDemo.*;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.eventService.EventServiceBaseImpl;


public class NotifLoopBackTest
{
  static private Quotes_i quotes_ = new Quotes_i();

  public static void main( String args[] )
  {
     //Runtime.getRuntime().traceMethodCalls( false );


       org.omg.CosNotifyFilter.ConstraintExp constrList[]= 
          new org.omg.CosNotifyFilter.ConstraintExp[1];

       EventType[] eventTypeSeq = new EventType[2];
       eventTypeSeq[0] = 
          new EventType( QuotesHelper.id(), 
                         "auto_quote");
       eventTypeSeq[1] = 
          new EventType( QuotesHelper.id(), 
                         "manual_quote");

       constrList[0] = 
          new org.omg.CosNotifyFilter.ConstraintExp( eventTypeSeq,
                                                     "$.auto_quote.data != 420");
                                                     //"");

       EventService eventsFacade = EventServiceBaseImpl.getInstance();

       Quotes quotes = null;
       try{
          eventsFacade.connectTypedNotifyChannelConsumer( 
             "LoopbackChannel",
             QuotesHelper.id(),
             quotes_ , constrList);


          System.out.println("Consumer side done ");

          // Supplier-side initialization

          quotes = QuotesHelper.narrow( 
                       eventsFacade.getTypedEventChannelSupplierStub( 
                                                          "LoopbackChannel",
                                                          QuotesHelper.id()) );
       }
       catch( Exception e){
          e.printStackTrace( System.out);

       }

    // Send some test messages.
    long start = System.currentTimeMillis();
    for( int i = 0; i < 5000; i++ )
    {
	quotes.auto_quote( i );
    }
    long end = System.currentTimeMillis();
    System.out.println("It took: " + (end-start) + " milliseconds for 5000." );
    try
    {
	Thread.sleep(2000);
    }
    catch( InterruptedException e )
    {
	;
    }
    
    System.exit(1);

  }

};

