// QuoteSupplier.java

import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.IntHolder;
import NotifDemo.*;
import com.cboe.infrastructureServices.eventService.EventServiceBaseImpl;
import com.cboe.infrastructureServices.eventService.EventService;


public class QuoteSupplier
{
  static private Quotes quotes = null;
    
  public static void main( String args[])
  {
    try {
       EventService eventsFacade = EventServiceBaseImpl.getInstance();
       
       quotes = QuotesHelper.narrow( 
                       eventsFacade.getTypedEventChannelSupplierStub( 
                                                          "TalarianChannel",
                                                          QuotesHelper.id()) );
    }                                               
    catch( Exception e){
           e.printStackTrace( System.out);
    }

    long start = System.currentTimeMillis();
    QuoteStruct[] quoteSeq = new QuoteStruct[3];
    quoteSeq[0] = new QuoteStruct( "IBM", (float)2.7);
    quoteSeq[1] = new QuoteStruct( "NEC", (float)3.4);
    quoteSeq[2] = new QuoteStruct( "DGI", (float)2.9);

    quotes.auto_quote( quoteSeq);
    quotes.manual_quote( new QuoteStruct( "XYZ", (float)1.0));
    quotes.manual_quote( new QuoteStruct( "ABC", (float)4.0));
    quotes.manual_quote( new QuoteStruct( "ION", (float)10.0));

    long end = System.currentTimeMillis();
    System.out.println("It took: " + (end-start) + " milliseconds ");

    try
    {
	Thread.sleep(2000);
    }
    catch( InterruptedException e )
    {
	;
    }
    

    //......
      
   // proxyPushConsumer.disconnect_push_consumer();

    System.out.println( "Loop Timeout - Normal Exit " );

      //return 0;
  }

};
