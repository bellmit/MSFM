package com.cboe.domain.util;

// CBOE Imports
import  com.cboe.idl.cmiUtil.CboeIdStruct;
import  com.cboe.infrastructureServices.foundationFramework.utilities.UuidHolder;




/*******************************************************************************
 * Represents a CboeId and contains helper methods that perform various CboeId
 * related opertions
 *
 * @see    com.cboe.idl.cmiUtil.CboeIdStruct
 * @author Dan Mannisto
 */
public class CboeId
{

//*** Public Methods

   /****************************************************************************
    * Clones a CboeIdStruct.
    *
    * @param  cboeIdStruct struct to be cloned
    * @return cloned CboeIdstruct
    * @see    com.cboe.idl.cmiUtil.CboeIdStruct
    * @author Dan Mannisto
    */
   static public CboeIdStruct clone( CboeIdStruct cboeIdStruct )
   {
      if( cboeIdStruct != null )
      {
         CboeIdStruct result    = new CboeIdStruct( );
         result.highCboeId 		= cboeIdStruct.highCboeId;
         result.lowCboeId  		= cboeIdStruct.lowCboeId;
      	 return result;
	  }

      throw new NullPointerException( "CboeId.clone received a null CboeIdStruct!" );
   }


   /****************************************************************************
    * Creates a default CboeIdStruct.
    *
    * @return a default CboeIdStruct
    * @see    com.cboe.idl.cmiUtil.CboeIdStruct
    * @author Dan Mannisto
    */
   static CboeIdStruct createDefaultStruct( )
   {
      // the zero values of the default constructor will be fine
      return new CboeIdStruct( );
   }


   /****************************************************************************
    * Checks CboeIdStruct for default values.
    *
    * @param  cboeIdStruct CboeIdStruct to be checked
    * @return <code>true</code> if CboeIdStruct has default values
    * @see    com.cboe.idl.cmiUtil.CboeIdStruct
    * @author Dan Mannisto
    */
   static boolean isDefault( CboeIdStruct cboeIdStruct )
   {
      // assume that if Ids equal 0 then the struct is a default
      if( cboeIdStruct != null )
      {
         if( cboeIdStruct.highCboeId == 0 && cboeIdStruct.lowCboeId == 0 )
         {
            return true;
         }
      }
      throw new NullPointerException( "CboeId.isDefault received a null CboeIdStruct!" );
   }


   /****************************************************************************
    * Compares two CboeIdStructs for equality
    *
    * @param  firstCboeIdStruct  first CboeIdStruct to compare
    * @param  secondCboeIdStruct second CboeIdStruct to compare
    * @return <code>true</code> if the CboeIdStructs are equal;
    *         <code>false</code> if they are not equal
    * @see    com.cboe.idl.cmiUtil.CboeIdStruct
    * @author Dan Mannisto
    */
   static public boolean isEqual( CboeIdStruct firstCboeIdStruct,
                                  CboeIdStruct secondCboeIdStruct )
   {
      if( firstCboeIdStruct != null && secondCboeIdStruct != null )
      {
         if( firstCboeIdStruct.highCboeId == secondCboeIdStruct.highCboeId &&
             firstCboeIdStruct.lowCboeId == secondCboeIdStruct.lowCboeId )
         {
            return true;
         }
	 else
	 {
	    return false;
	 }
      }
      throw new NullPointerException( "CboeId.isEqual received a null CboeIdStruct!" );
   }


   /****************************************************************************
    * Converts a CboeIdStruct into a long value
    *
    * @param  cboeIdStruct CboeIdStruct to be converted
    * @return a new long value created from the cboeIdStruct
    * @see    com.cboe.idl.cmiUtil.CboeIdStruct
    * @author Dan Mannisto
    */
   static public long longValue( CboeIdStruct cboeIdStruct )
   {

      if( cboeIdStruct != null )
      {
         UuidHolder uuid = new UuidHolder( cboeIdStruct.highCboeId,
                                           cboeIdStruct.lowCboeId
                                         );
         return uuid.getLongValue();
      }

      throw new NullPointerException( "CboeId.longValue received a null CboeIdStruct!" );
   }


   /****************************************************************************
    * Returns a string representation of the CboeIdStruct.
    *
    * @param  cboeIdStruct cboeIdStruct to be converted
    * @return a string value for the cboeIdStruct
    * @see    com.cboe.idl.cmiUtil.CboeIdStruct
    * @author Dan Mannisto
    */
   static public String toString( CboeIdStruct cboeIdStruct )
   {
      if( cboeIdStruct != null )
      {
         return new String( String.valueOf( cboeIdStruct.highCboeId ) + ":" +
                            String.valueOf( cboeIdStruct.lowCboeId ));
      }
      throw new NullPointerException( "CboeId.toString received a null CboeIdStruct!" );
   }


   /****************************************************************************
    * Converts a long value into a CboeIdStruct
    *
    * @param  longValue long to be converted
    * @return a new CboeIdStruct created from the longValue
    * @see    com.cboe.idl.cmiUtil.CboeIdStruct
    * @author Dan Mannisto
    */
   static public CboeIdStruct toStruct( long longValue )
   {
      CboeIdStruct  result;

      result = new CboeIdStruct();

      UuidHolder uuid = new UuidHolder(longValue);

      result.highCboeId = uuid.getPrimaryBytes();
      result.lowCboeId  = uuid.getSecondaryBytes();

      return result;
   }


//*** Private Methods

   /****************************************************************************
    * Hide the default constructor from the public interface
    */
   private CboeId( )
   {
   }

}
