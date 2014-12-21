// Quotes_i.java 

import NotifDemo.POA_Quotes;


public class Quotes_i extends POA_Quotes
//extends com.cboe.EventService.Test._QuotesImplBase 
//implements org.omg.CosEventComm.PushConsumer
{
    private int numberAutoQuotes = 0;
    private int numberManualQuotes = 0;
    private long start;
    
  public void auto_quote( int data)
  {
    // Application handling code goes here.
    //.....
            
      if( this.numberAutoQuotes == 0 )
	  this.start = System.currentTimeMillis();
     
      if( data == 420)
	  System.out.println( "Received 420!!");

      if( data == 419 || data == 421)
	  System.out.println( "Received data : " + data);

      
      if( ++this.numberAutoQuotes % 500 == 0 )
      {
	  System.out.println( data );
	  System.out.println("Received " + this.numberAutoQuotes +
			     " auto_quote messages." );
	  System.out.println( "Received " + this.numberAutoQuotes +
			      " int " + 
	  		      (System.currentTimeMillis()-this.start) );
      }
      
  }

  public void manual_quote( )
  {
    // Application handling code goes here.
    //.....
      if( ++this.numberManualQuotes % 500 == 0 )
	  System.out.println("Received " + this.numberManualQuotes +
			     " manual_quote messages." );
  }

  public void push( org.omg.CORBA.Any any ) 
  {
    // Untyped events are not supported.
    //throw new org.omg.CosTypedEventChannelAdmin.InterfaceNotSupported();
  }

  public void disconnect_push_consumer( /*org.omg.CosEventComm.PushSupplier push_supplier*/ )
  {
  }

   public org.omg.CORBA.Object get_typed_consumer(){
      return null;
   }
};
