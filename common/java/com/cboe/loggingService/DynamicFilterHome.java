package com.cboe.loggingService;
import java.util.*;

public class DynamicFilterHome
{
   synchronized public static DynamicFilterHome getHome()
   {
      if ( null == home )
      {
         home = new DynamicFilterHome();
      }
      return home;
   }
   // There will be a single filter instance for a name. The description provided by the first call to 
   // register a name will be the description used. In other words, if a later call uses a different
   // description, the later description will be ignored.
   // Newly created filters will be inactive.
   synchronized public DynamicFilter registerCategory( String name, String description )
   {
      DynamicFilter retVal = ( DynamicFilter ) filters.get( name );
      if ( null == retVal )
      {
         retVal = new DynamicFilter( name, description );
         filters.put( name, retVal );
      }
      return retVal;
   }
   synchronized public DynamicFilter[] getFilters()
   {
      DynamicFilter[] retVal = null;
      int count = filters.size();
      if ( 0 < count )
      {
         retVal = new DynamicFilter[ count ];
         Iterator iterator = filters.entrySet().iterator();
         int i = 0;
         while ( i < count && iterator.hasNext() )
         {
            retVal[ i++ ] = (DynamicFilter) ( (Map.Entry) iterator.next() ).getValue();
         }
         /* assert: i == count */
      }
      else
      {
         retVal = new DynamicFilter[ 0 ];
      }
      return retVal;
   }
   synchronized public void setAllFiltersInactive()
   {
      if ( 0 < filters.size() )
      {
         Iterator iterator = filters.entrySet().iterator();
         while ( iterator.hasNext() )
         {
            ( (DynamicFilter) ( (Map.Entry) iterator.next() ).getValue() ).setInactive();
         }
      }
   }
   synchronized public void setAllFiltersActive()
   {
      if ( 0 < filters.size() )
      {
         Iterator iterator = filters.entrySet().iterator();
         while ( iterator.hasNext() )
         {
            ( (DynamicFilter) ( (Map.Entry) iterator.next() ).getValue() ).setActive();
         }
      }
   }
   synchronized public void setFilterInactive( String name ) throws DynamicFilterNotFoundException
   {
      DynamicFilter filter = (DynamicFilter) filters.get( name );
      if ( null != filter )
      {
         filter.setInactive();
      }
      else
      {
         throw new DynamicFilterNotFoundException( name );
      }
   }
   synchronized public void setFilterActive( String name ) throws DynamicFilterNotFoundException
   {
      DynamicFilter filter = (DynamicFilter) filters.get( name );
      if ( null != filter )
      {
         filter.setActive();
      }
      else
      {
         throw new DynamicFilterNotFoundException( name );
      }
   }
   private DynamicFilterHome()
   {
      filters = new HashMap();
   }
   private static DynamicFilterHome home;
   private HashMap filters;
}
