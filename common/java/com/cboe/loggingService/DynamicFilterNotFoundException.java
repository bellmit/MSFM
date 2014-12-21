package com.cboe.loggingService;

public class DynamicFilterNotFoundException extends Exception
{
   public DynamicFilterNotFoundException()
   {
   }
   public DynamicFilterNotFoundException( String message )
   {
      super( message );
   }
   public DynamicFilterNotFoundException( String message, Throwable cause )
   {
      super( message, cause );
   }
   public DynamicFilterNotFoundException( Throwable cause )
   {
      super( cause );
   }
}
