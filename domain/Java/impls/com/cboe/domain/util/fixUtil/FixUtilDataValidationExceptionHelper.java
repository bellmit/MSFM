package com.cboe.domain.util.fixUtil;
/**
 * Properly format a DataValidationException so the message is consistent
 * with the error message that will be provided in the the Text(58) tag
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
import com.cboe.exceptions.DataValidationException;
import com.cboe.util.ExceptionBuilder;

public class FixUtilDataValidationExceptionHelper {

  public FixUtilDataValidationExceptionHelper() {
  }
  /**
   * Return DataValidationException with message in the format to be returned in a FIX message
   * This version is for use with invalidData of type String
   */
  public static DataValidationException create(String tagName, int tagNumber, String reason,
                                               String invalidData, int errorCode)
  {
       return ExceptionBuilder.dataValidationException("Invalid Data - "+tagName +"("+tagNumber+")= "+
                                                        invalidData + "[" + reason +"]",errorCode);
  }

  /**
   * Return DataValidationException with message in the format to be returned in a FIX message
   * This version is for use with invalidData of type int
   */
  public static  DataValidationException create(String tagName, int tagNumber, String reason,
                                                int invalidData, int errorCode)
  {
       return create(tagName,tagNumber,reason,""+invalidData,errorCode);
  }
  /**
   * Return DataValidationException with message in the format to be returned in a FIX message
   * This version is for use with invalidData of type double
   */
  public static  DataValidationException create(String tagName, int tagNumber, String reason,
                                                double invalidData, int errorCode)
  {
       return create(tagName,tagNumber,reason,""+invalidData,errorCode);
  }
  /**
   * Return DataValidationException with message in the format to be returned in a FIX message
   * This version is for use with invalidData of type char
   */
  public  static DataValidationException create(String tagName, int tagNumber, String reason,
                                                char invalidData, int errorCode)
  {
       return create(tagName,tagNumber,reason,""+invalidData,errorCode);
  }

}
