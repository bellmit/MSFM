package com.cboe.loggingService;

final public class DynamicFilter
{
   public boolean isActive()
   {
		return isActive;
   }
   public void setActive()
   {
		isActive = true;
   }
   public void setInactive()
   {
		isActive = false;
   }
   public String getName()
   {
      return name;
   }
   public String getDescription()
   {
      return description;
   }
   /* The constructor has package access on purpose, in order
      to restrict creation to the DynamicFilterHome */
   DynamicFilter( String name, String description )
   {
      this.name = name;
      this.description = description;
      isActive = false;
   }
   private final String name;
   private final String description;
   private volatile boolean isActive;
}
