package com.cboe.application.shared;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


public class TransactionTimingConfiguration {
	private static String PUBLISH_TT = "Publish_TT";
	private static String TTE = "TTE";
	private static String ORDER_TTE = "Order_TTE";
	private static String QUOTE_TTE = "Quote_TTE";
    private static boolean publish_tt = false;
    private static boolean order_tte = false;
    private static boolean quote_tte = false;
    private static boolean is_local = true;


   static
   {
       	if (System.getProperty(ORDER_TTE)!=null)
		{
	        if (System.getProperty(ORDER_TTE).equalsIgnoreCase("TRUE"))
	        {
	            order_tte = true;
	        }
		}
       	if (System.getProperty(QUOTE_TTE)!=null)
		{
	        if (System.getProperty(QUOTE_TTE).equalsIgnoreCase("TRUE"))
	        {
	            quote_tte = true;
	        }
		}
       if (System.getProperty(PUBLISH_TT)!=null)
       {
           if (System.getProperty(PUBLISH_TT).equalsIgnoreCase("TRUE"))
           {
               publish_tt = true;
           }
       }
       	if (System.getProperty(TTE)!=null)
		{
	        if (System.getProperty(TTE).equalsIgnoreCase("REMOTE"))
	        {
	        	is_local= false;
	        }
		}
        Log.information("TTE Configuration IS_LOCAL:" +  is_local + " PUBLISH:"+ publish_tt + " QUOTE_TTE_ON:"+ quote_tte + " ORDER_TTE_ON:" + order_tte );
   }

    public static boolean publishTT()
    {
        return publish_tt;
    }
	
	public  static boolean isLocal()
	{
        return is_local;
    }

    public static boolean publishOrderTTE()
    {
        return order_tte;
    }
    
    public static boolean publishQuoteTTE()
    {
        return quote_tte;
    }   
	
}