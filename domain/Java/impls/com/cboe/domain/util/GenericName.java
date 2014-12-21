package com.cboe.domain.util;




/*******************************************************************************
 * a Utility class to extract GenericName from Interface helper ID
 *
 * @author Mei Wu
 * @date 12/18/2001
 */
public class GenericName
{

   /**
    * Get the generic service name,
    * return null if the input name is generic name; otherwise extract the generic name and return it
    * @param name String
    * @param ch int
    * @return String
    */
   public static String getGenericName ( String genericTypeName, int ch)  //added as a private method
   {
       String result = null;
       int start = genericTypeName.indexOf(ch);
       int end = -1;
       if (-1 != start)
       {
           end =  genericTypeName.indexOf(ch, start+1);
       }
       if ((-1 != start) && ( -1 != end) && (start < end))
       {
           try
           {
               result =  genericTypeName.substring(start+1,end);
           }
           catch(StringIndexOutOfBoundsException e)
           {
               result = null;
           }
       }
       return result;
   }

//*** Private Methods

   /****************************************************************************
    * Hide the default constructor from the public interface
    */
   private GenericName( )
   {
   }

}
