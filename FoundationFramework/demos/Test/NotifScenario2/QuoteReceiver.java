
// QuoteReceiver.java


import IE.Iona.OrbixWeb._CORBA ; 
import org.omg.CORBA.IntHolder;
import org.omg.CosNaming.NamingContextPackage.*;
import com.cboe.NotificationService.ConsumerSide.TypedProxyPushSupplierImpl;
import NotifDemo.*;
import org.omg.CosNotifyFilter.ConstraintExp;
import com.cboe.infrastructureServices.eventService.EventServiceBaseImpl;
import com.cboe.infrastructureServices.eventService.EventService;


class QuoteReceiver
{
    static private Quotes_i quotes_ = new Quotes_i();

    public static void main( String args[] )
    {
        
        try { 
             EventService eventsFacade = EventServiceBaseImpl.getInstance();
             eventsFacade.connectTypedNotifyChannelConsumer( 
             "TalarianChannel",
             QuotesHelper.id(),
             quotes_ , new ConstraintExp[0]);
             System.out.println("Consumer side done ");

        }
        catch( Exception e){
           e.printStackTrace( System.out);
        }

           try
           {
              _CORBA.Orbix.processEvents( 1000*3600 );
           }
           catch( org.omg.CORBA.SystemException e )
           {
              System.out.println( "Error occured calling process events : " + e.toString() );
              System.exit(1);
           }


          
    }

};
