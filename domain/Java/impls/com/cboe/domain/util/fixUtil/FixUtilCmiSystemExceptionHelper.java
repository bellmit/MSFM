package com.cboe.domain.util.fixUtil;
/**
 * Properly format a SystemException so the message is consistent
 * with the error message that is to be displayed in error logs
 * <br><br>
 * Copyright © 2000 by the Chicago Board Options Exchange ("CBOE"), as an unpublished work.
 * The information contained in this software program constitutes confidential and/or trade
 * secret information belonging to CBOE. This software program is made available to
 * CBOE members and member firms to enable them to develop software applications using
 * the CBOE Market Interface (CMi), and its use is subject to the terms and conditions
 * of a Software License Agreement that governs its use. This document is provided "AS IS"
 * with all faults and without warranty of any kind, either express or implied.
 *
 */
import com.cboe.exceptions.SystemException;
import com.cboe.util.ExceptionBuilder;

public class FixUtilCmiSystemExceptionHelper {

  public FixUtilCmiSystemExceptionHelper() {
  }
  /**
   * Return DataValidationException with message in the format to be returned in a FIX message
   * This version is for use with invalidData of type String
   */
  public static SystemException create(String cmiFieldName, String tagName, int tagNumber,
                                       String reason, String invalidData, int errorCode)
  {
	   StringBuilder se = new StringBuilder(200);
	   se.append("Invalid Data or unexpected data returned from CAS ")
	   .append(cmiFieldName)
	   .append("=")
	   .append(invalidData)
	   .append(" Cannot map to FIX=")
	   .append(tagName)
	   .append("(")
	   .append(tagNumber)
	   .append(") [")
	   .append(reason)
	   .append("]");   
       return ExceptionBuilder.systemException(se.toString(),errorCode);
  }

  /**
   * Return SystemException with message in the format to be returned in a FIX message
   * This version is for use with invalidData of type int
   */
  public static  SystemException create(String cmiFieldName,String tagName, int tagNumber,
                                        String reason, int invalidData, int errorCode){
       return create(cmiFieldName,tagName,tagNumber,reason,""+invalidData,errorCode);
  }
  /**
   * Return SystemException with message in the format to be returned in a FIX message
   * This version is for use with invalidData of type double
   */
  public static  SystemException create(String cmiFieldName,String tagName, int tagNumber,
                                        String reason, double invalidData, int errorCode){
       return create(cmiFieldName,tagName,tagNumber,reason,""+invalidData,errorCode);
  }
  /**
   * Return SystemException with message in the format to be returned in a FIX message
   * This version is for use with invalidData of type char
   */
  public  static SystemException create(String cmiFieldName,String tagName, int tagNumber,
                                        String reason, char invalidData, int errorCode){
       return create(cmiFieldName,tagName,tagNumber,reason,""+invalidData,errorCode);
  }

  /**
   * Return SystemException with message in the format to be returned in a FIX message -
   * without the FIX TagName and TagNumber
   * This version is for use with invalidData of type char
   */
  public static SystemException create(String cmiFieldName, String reason, String invalidData, int errorCode){
       return ExceptionBuilder.systemException("Invalid Data or unexpected data returned from CAS "+
         cmiFieldName+"="+invalidData+ " Cannot map to FIX [" + reason + "]",errorCode);
  }

  /**
   * Return SystemException with message in the format to be returned in a FIX message -
   * without the FIX TagName and TagNumber
   * This version is for use with invalidData of type char
   */
  public static SystemException create(String cmiFieldName, String reason, char invalidData, int errorCode){
       return create(cmiFieldName, reason, ""+invalidData, errorCode);
  }

  /**
   * Return SystemException with message in the format to be returned in a FIX message -
   * without the FIX TagName and TagNumber
   * This version is for use with invalidData of type int
   */
  public static SystemException create(String cmiFieldName, String reason, int invalidData, int errorCode){
       return create(cmiFieldName, reason, ""+invalidData, errorCode);
  }

  /**
   * Return SystemException with message in the format to be returned in a FIX message -
   * without the FIX TagName and TagNumber
   * This version is for use with invalidData of type short
   */
  public static SystemException create(String cmiFieldName, String reason, short invalidData, int errorCode){
       return create(cmiFieldName, reason, ""+invalidData, errorCode);
  }

  /**
   * Return SystemException with message in the format to be returned in a FIX message -
   * without the FIX TagName and TagNumber
   * This version is for use with invalidData of type double
   */
  public static SystemException create(String cmiFieldName, String reason, double invalidData, int errorCode){
       return create(cmiFieldName, reason, ""+invalidData, errorCode);
  }


}
