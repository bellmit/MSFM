// Quotes_i.java 

import NotifDemo.POA_Quotes;

public class Quotes_i extends POA_Quotes
{
    
    
  public void auto_quote(NotifDemo.QuoteStruct[] product)
  {
    // Application handling code goes here.
    //.....
      System.out.println("QuotesExample::auto_quote");
      for(int i = 0; i < product.length; i++) {
	  System.out.println("ProductId : " + product[i].product + " " + "Price : " + product[i].price);
      }
      
  }

  public void manual_quote( NotifDemo.QuoteStruct quote)
  {
    // Application handling code goes here.
    //.....
      System.out.println("QuotesExample::manual_quote");
      System.out.println("ProductId : " + quote.product + " " + "Price : " + quote.price);
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
