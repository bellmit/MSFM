package com.cboe.domain.util;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Helper to parse and build extensions fields. The ExtensionsHelper can be constructed
 * from a String that has been formatted as a series of zero or more key=value fields
 * using the FIELD_DELIMITER and TAG_DELIMITER defined within this class.
 *
 * Fields are not stored in any order. Order of entry is not guaranteed on return.
 *
 * Methods are available to setValues() and getValues()
 *
 * Key values can be any combination of alphanumeric characters.
 *
 * The ExtensionsHelper interface supports entry of key fields as integers or Strings.
 *
 * Values can be string, int, or double.
 *
 * Values are always returned as strings - the user must perform the data type conversion.
 * (NOTE: This was done because this container permits blank values to be stored. The user
 * must then decide how to convert a blank into a numeric datatype. )
 *
 * The user of this class can supply an optional validator routine that is
 * used to validate the key value. If no validator is provided a <code>DefaultExtensionsValidator</code>
 * is provided for use.
 *
 * @see com.cboe.domain.util.ExtensionsValidator
 *
 * Note on changing values for Field Delimiter and Tag Delimiters.
 *
 * There are methods that are provided to set the Field Delimiter and Tag Delimiter.
 * To change the delimiters the class just need be extended to a specialized ExtensionsHelper
 * that uses different delimiters. For instance a JavelinExtensionsHelper
 * could be created that uses a Field delimiter of "|". Care should be taken when changing
 * delimiters
 *
 * <br><br>
 * Copyright © 1999-2002 by the Chicago Board Options Exchange ("CBOE"), as an unpublished work.
 * The information contained in this software program constitutes confidential and/or trade
 * secret information belonging to CBOE. This software program is made available to
 * CBOE members and member firms to enable them to develop software applications using
 * the CBOE Market Interface (CMi), and its use is subject to the terms and conditions
 * of a Software License Agreement that governs its use. This document is provided "AS IS"
 * with all faults and without warranty of any kind, either express or implied.
 *
 * @author Jim Northey
 */
public class ExtensionsHelper {

  /**
    * For this version of ExtensionsHelper it was felt that the
    * delimiters should be hardcoded, thus avoiding any confusion
    * or configuration issues.
    * The Field delimiter for Extensions strings is the same as for
    * FIX, the <SOH> (0x01) character. The tag delimiter is the same
    * as FIX, the equals sign.
    */
  public static final String DEFAULT_FIELD_DELIMITER = "\u0001";
  public static final String DEFAULT_TAG_DELIMITER = "=";
  public static final String SECONDARY_ORDER_ID = "198";

  /*
   * Field delimiter value - set to default value - can be
   * overriden by the user
   */
  private String fieldDelimiter;

  /*
   * Tag delimiter value - set to default value - can be
   * overriden by the user
   */
  private String tagDelimiter;

  /*
   * Current implementation of the container to store the is a HashMap
   * to contain the extensions key, values
   */
  private HashMap extensionContainer = new HashMap();

  /*
   * A validator will always be required to use this class.
   * A null validator is provided by default if the user does
   * not provide one.
   */
  private ExtensionsValidator validator=null;



  /**
   * Constructor that takes an ExtensionsValidator, extensions, and delimiters that will
   * be used to validate any keys and values added to the
   * ExtensionsHelper.
   *
   * @param validator <code>ExtensionsValidator</code> to be used to validate
   *                  key value pairs.
   * @param extensions <code>String</code> to be parsed for extensions fields
   * @param fieldDelimiter <code>String</code> field delimiter to be used to parse extensions fields
   * @param tagDelimiter <code>String</code> tag delimiter to be used to parse extensions fields
   *
   * @throws java.text.ParseException if extensions field is invalid
   */
  public ExtensionsHelper(ExtensionsValidator validator, String extensions, String fieldDelimiter, String tagDelimiter) throws java.text.ParseException {

      setFieldDelimiter(fieldDelimiter);

      setTagDelimiter(tagDelimiter);

      setValidator(validator);

      initContainer();

      setExtensions(extensions);
  }

  /**
   * Constructor that takes an ExtensionsValidator that will
   * be used to validate any keys and values added to the
   * ExtensionsHelper. Default values for delimiters are used.
   *
   * @param validator <code>ExtensionsValidator</code> to be used to validate
   *                  key value pairs.
   *
   */
  public ExtensionsHelper(ExtensionsValidator validator) {

      setFieldDelimiter(DEFAULT_FIELD_DELIMITER);

      setTagDelimiter(DEFAULT_TAG_DELIMITER);

      setValidator(validator);

      initContainer();

  }

  /**
   * Constructor that accepts a tag delimited string of fields.
   * The fields will be placed into the extensionsContainer by the parsing
   * method. Default values for delimiters and the <code>DefaultExtensionValidator</code>
   * is used.
   *
   * @param extensions <code>String</code> containing fields in a tag delimited format
   *
   * @throws java.text.ParseException if extensions field is invalid
   */
  public ExtensionsHelper(String extensions) throws java.text.ParseException {

      setFieldDelimiter(DEFAULT_FIELD_DELIMITER);

      setTagDelimiter(DEFAULT_TAG_DELIMITER);

      setValidator((ExtensionsValidator) new DefaultExtensionsValidator() );

      initContainer();

      setExtensions(extensions);
  }

  /**
   * Constructor that accepts a validator and tag delimited string.
   * The validator will be used to parse the string and to validate
   * subsequent key value pairs added to the extensions container.
   *
   * @param validator <code>ExtensionsValidator</code> to be used to validate
   *                  key value pairs.
   * @param extensions <code>String</code> containing fields in a tag delimited format
   *
   * @throws java.text.ParseException if extensions field is invalid
   */
  public ExtensionsHelper(ExtensionsValidator validator, String extensions) throws java.text.ParseException {

      setFieldDelimiter(DEFAULT_FIELD_DELIMITER);

      setTagDelimiter(DEFAULT_TAG_DELIMITER);

      setValidator(validator);

      initContainer();

      setExtensions(extensions);
 }

  /**
   * Constructor that takes an ExtensionsValidator that will
   * be used to validate any keys and values added to the
   * ExtensionsHelper. Default values for delimiters and the <code>DefaultExtensionValidator</code>
   * is used.
   *
   * @param extensions <code>String</code> to be parsed for extensions fields
   * @param fieldDelimiter <code>String</code> field delimiter to be used to parse extensions fields
   * @param tagDelimiter <code>String</code> tag delimiter to be used to parse extensions fields
   *
   * @throws java.text.ParseException if extensions field is invalid
   */
  public ExtensionsHelper(String extensions, String fieldDelimiter, String tagDelimiter) throws java.text.ParseException {

      setFieldDelimiter(fieldDelimiter);

      setTagDelimiter(tagDelimiter);

      setValidator((ExtensionsValidator) new DefaultExtensionsValidator() );

      initContainer();

      setExtensions(extensions);
  }

  /**
   * Default constructor - will create an empty extensions container
   * and use the DefaultExtensionsValidator
   */
  public ExtensionsHelper() {

      setFieldDelimiter(DEFAULT_FIELD_DELIMITER);

      setTagDelimiter(DEFAULT_TAG_DELIMITER);

      setValidator((ExtensionsValidator) new DefaultExtensionsValidator() );

      initContainer();

  }

 /**
  * Initialize ExtensionsHelper member variables, primarily the
  * extensions container
  *
  * @return void
  */
  protected void initContainer() {

      this.extensionContainer.clear();
  }


  /**
   * setter for the validator object
   *
   * The user is given the option of setting their own validator or changing their
   * validator at any time. The validator cannot be null.
   *
   * @param validator <code>ExtensionsValidator</code> used to validate fields
   * @return <code>void</code>
   * @throws <code>java.lang.IllegalArgumentException</code>
   */
   public void setValidator(ExtensionsValidator validator) {

        if ( validator == null ) {

         throw new java.lang.IllegalArgumentException("null validator provided");
        }

        this.validator = validator;
   }


  /**
   * setter for the field delimiter
   *
   * This is provided as a protected method - so that only inheritors
   * can access this method. It was felt that changing the delimiters
   * should not be part of the public interface because the consequences
   * of a programmer mistakenly changing the delimiters are fairly onerous.
   *
   * @param fieldDelimiter <code>String</code> that is used as the field delimiter
   * @return <code>void</code>
   * @throws <code>java.lang.IllegalArgumentException</code>
   */
  public void setFieldDelimiter(String fieldDelimiter) {

      if (fieldDelimiter == null ) {
          throw new java.lang.IllegalArgumentException("a null field delimiter not permitted");
      }

      if (fieldDelimiter.length() == 0) {
          throw new java.lang.IllegalArgumentException("a zero length field delimiter not permitted");
      }

      this.fieldDelimiter = fieldDelimiter;
  }

  /**
   * accessor for the field delimiter
   *
   * @return <code>final String</code> the field delimiter string
   */
  public final String getFieldDelimiter() {

      return this.fieldDelimiter;
  }


  /**
   * setter for the tag delimiter
   *
   * @param tagDelimiter <code>String</code> the tag delimiter value to be set
   * @return void
   * @throws <code>java.lang.IllegalArgumentException</code>
   */
  public void setTagDelimiter(String tagDelimiter) {

      if (tagDelimiter == null ) {
          throw new java.lang.IllegalArgumentException("null tag delimiter provided");
      }

      if (tagDelimiter.length() == 0) {
          throw new java.lang.IllegalArgumentException("a zero length tag delimiter not permitted");
      }

      this.tagDelimiter = tagDelimiter;
  }

  /**
   * accessor for the tag delimiter
   *
   * @return <code>final String</code> the tag delimiter string
   */
  public final String getTagDelimiter() {

      return this.tagDelimiter;
  }

  /**
   * Return a value associated with a field key
   *
   * Will return null is key is not found in extensions
   *
   * @param key <code>String</code> key of field for which a value is requested
   * @return value <code>String</code> value associated with key or blank if not found
   */
  public String getValue(String key) {

      Object val = extensionContainer.get(key);
 
      return (String)val;

  }

  /**
   * Return a value associated with a field key, return the default value
   * supplied by the user on the call if the key is not found.
   *
   * @param key <code>String</code> key of field for which a value is requested
   * @param defaultValue <code>String</code> If key is not found, then return defaultValue
   * @return value <code>String</code> value associated with key or blank if not found
   */
  public String getValue(String key, String defaultValue) {

      String val = getValue(key);

      if ( val == null ) {

         return defaultValue;

      }
     
      return val;

 }

  /**
   * Return a value associated with a field key
   * Returns a null is the key is not found.
   *
   * @param key - <code>int</code> The key of the field for which a value is requested.
   * @return value <code>String</code> value associated with key or blank if not found
   */
  public String getValue(int key) {

      return getValue(Integer.toString(key));
  }


  /**
   * Return a value associated with a field key
   * Returns the defaultValue if key is not found in extensions
   *
   * @param key - <code>int</code> The key of the field for which a value is requested.
   * @param defaultValue - <code>String</code> Default value returned if key is not found
   * @return value <code>String</code> value associated with key
   */
  public String getValue(int key, String defaultValue) {

      return getValue(Integer.toString(key),defaultValue);
  }

  /**
   * Set value in extensions container. If value is null do not set and simply return
   * ignoring the null (setValue() throws an exception if value is null)
   *
   * Caveat emptor - method is silent and simply does nothing if value is null
   * Key is not validated - if the key is null - setValue() will throw a ParseException
   *
   * @param key <code>String</code> key to be stored or replaced
   * @param value <code>String</code> value to be stored for this key
   * @throws <code>java.text.ParseException</code> if key is null or invalid
   *         not valid, as determined by the <code>ExtensionValidator</code> 
   */
  public void setValueIgnoreNullValue(String key, String value) throws java.text.ParseException {

     if ( value != null )
     {
         setValue(key,value);
     }

     // do nothing if value is null

  }

  /**
   * Set (or replace) a value into the extensions container
   *
   * Null or empty key values are not permitted and will result in a
   * <code>java.text.ParseException</code> being thrown.
   *
   * @param key <code>String</code> key to be stored or replaced
   * @param value <code>String</code> value to be stored for this key
   * @throws <code>java.text.ParseException</code> if key or values are
   *         not valid, as determined by the <code>ExtensionValidator</code>
   */
  public void setValue(String key, String value) throws java.text.ParseException {

      // Validate Key

      if (key == null) {
          throw new ParseException("Null Key provided",0);
      }

      if (key.length() == 0) {
          throw new ParseException("Empty key provided",0);
      }

      // Validate Value

      if (value == null) {
          throw new ParseException("Null value provided",0);
      }

      if (!validator.isValidKey(key) ) {
          throw new ParseException("Invalid Extensions Field Key="+key,0);
      }

      if (!validator.isValidValue(key,value) ) {
          throw new ParseException("Invalid Extensions Field Value for Key="+key+" value="+value,0);
      }

      extensionContainer.put(key,value);
  }

  /**
   * Set (or replace) an integer value into the extensions container
   *
   * @param key <code>String</code> key to be stored or replaced
   * @param value <code>int</code> value to be stored for this key
   * @throws <code>java.text.ParseException</code> if key or values are
   *         not valid, as determined by the <code>ExtensionValidator</code>
   */
  public void setValue(String key, int value) throws java.text.ParseException {

      setValue(key,Integer.toString(value));

  }

  /**
   * Set (or replace) a  double value into the extensions container
   *
   * @param key <code>String</code> key to be stored or replaced
   * @param value <code>double</code> value to be stored for this key
   * @throws <code>java.text.ParseException</code> if key or values are
   *         not valid, as determined by the <code>ExtensionValidator</code>
   */
  public void setValue(String key, double value) throws java.text.ParseException {

      setValue(key,Double.toString(value));
  }

   /**
   * Set (or replace) an String value into the extensions container
   *
   * @param key <code>int</code> key to be stored or replaced
   * @param value <code>double</code> value to be stored for this key
   * @throws <code>java.text.ParseException</code> if key or values are
   *         not valid, as determined by the <code>ExtensionValidator</code>
   */
  public void setValue(int key, String value) throws ParseException {

      setValue(Integer.toString(key), value);
  }

  /**
   * Set (or replace) an String value into the extensions container
   *
   * @param key <code>int</code> key to be stored or replaced
   * @param value <code>int</code> value to be stored for this key
   * @throws <code>java.text.ParseException</code> if key or values are
   *         not valid, as determined by the <code>ExtensionValidator</code>
   */
  public void setValue(int key, int value) throws ParseException {

      setValue(Integer.toString(key), Integer.toString(value));
  }


  /**
   * Set (or replace) a  double value into the extensions container
   *
   * @param key <code>int</code> key to be stored or replaced
   * @param value <code>double</code> value to be stored for this key
   * @throws <code>java.text.ParseException</code> if key or values are
   *         not valid, as determined by the <code>ExtensionValidator</code>
   */
  public void setValue(int key, double value) throws ParseException {

      setValue(Integer.toString(key), Double.toString(value) );
  }

  /**
   * Remove of key and its associated value from the container
   * returns defaultValue is the key was not found
   *
   * @param key<code>String</code> key to the kay value pair to be removed from the
   *                         extensions container.
   * @param defaultValue <code>String</code> default value to return if key is not found
   * @return String - returns previous value associated with key or the default value
   */
  public String removeKey(String key, String defaultValue) {

      Object retValue = extensionContainer.remove(key);

      if ( retValue == null) {
        
         return defaultValue;
      }

     return (String) retValue;
  }

  /**
   * Remove of key and its associated value from the container
   *
   * @param key<code>String</code> key to the kay value pair to be removed from the
   *                         extensions container.
   * @return String - returns previous value associated with key
   */
  public String removeKey(String key) {

      return (String) extensionContainer.remove(key);
  }

  /**
   * Remove of key and its associated value from the container
   *
   * @param key<code>int</code> key to the kay value pair to be removed from the
   *                         extensions container.
   * @return void
   */
  public String removeKey(int key) {

      return removeKey( Integer.toString(key) );
  }

  /**
   * Write extensionContainer to the extensions string
   *
   * @return tag delimited <code>String</code> containing extension fields
   */
  protected String writeExtensionContainerToString(){

      StringBuilder newextensions = new StringBuilder(100);

      for (Iterator iter=extensionContainer.entrySet().iterator(); iter.hasNext(); ) {

          Map.Entry e = (Map.Entry) iter.next();
          newextensions.append((String)e.getKey());
          newextensions.append( getTagDelimiter()); 
          newextensions.append((String)e.getValue());
          if (iter.hasNext()) {
              newextensions.append(getFieldDelimiter());
          }
      }

      return newextensions.toString();
  }

  /**
   * Override the toString method to return tag delimited extensions string
   *
   * @return tag delimited <code>String</code> containing extension fields
   */
  public String toString() {

    return writeExtensionContainerToString();
  }

  /**
   * Provide access to the number of elements in the table
   *
   * @return number of fields in extensions container as an <code>int</code>.
   */
  public int size() {

    return extensionContainer.size();
  }

  /**
   * Return a <code>java.util.Set</code> of keys for the extensions fields
   * currently stored.
   *
   * @return <code>java.util.Set</code> of keys
   */
  public Set getKeys() {

     return extensionContainer.keySet();

  }

  /**
   * Parse the extensions string into a extensions table. Called each time a new
   * extensions string is provided.
   *
   * @param extensions <code>String</code> containing zero or more tag delimited fields
   * @return void
   * @throws <code>java.text.ParseException</code>
   */
  private void parseExtensionsString(String extensions) throws ParseException {

      if (extensions == null) {
          throw new ParseException("Null extensions string encountered while parsing",0);
      }

      StringTokenizer parser = new StringTokenizer(extensions, getFieldDelimiter() );

      while(parser.hasMoreTokens()) {

          String field = parser.nextToken();

          int tagDelimiterPosition = field.indexOf( getTagDelimiter() );

         /*
          * See if the tag delimiter was found, if not set the key value
          * to the entire field and initialize it to a blank string
          */

          if ( tagDelimiterPosition == -1) {

              setValue(field,"");
          }
          else {

              /*
               * The key name is everything before the start of the tag delimiter
               */

              String key=field.substring(0,tagDelimiterPosition);

              /* Calculate the starting point of the value string - make sure to
               * account for the length of the delimiter which can be greater than
               * zero
               */

              int startOfValue = tagDelimiterPosition + getTagDelimiter().length();

              String value=field.substring(startOfValue);

              setValue(key,value);
          }
      }
    }

    /**
     * Append an extensions string to the current extensions maintained by
     * ExtensionsHelper
     *
     * @param extensions <code>String</code> string of extensions field to be appended
     * @return <code>void</code>
     * @throws <code>java.text.ParseException</code>
     */
    public void appendExtensions(String extensions) throws java.text.ParseException {

        parseExtensionsString(extensions);

    }

    /**
     * Replace the contents of the ExtensionsHelper with values from the extensions string
     *
     * @param extensions <code>String</code> extensions string to be replaced in the container
     * @return <code>void</code>
     * @throws <code>java.text.ParseException</code>
     */
     public void setExtensions(String extensions) throws java.text.ParseException {

         // reinitialize the extensions container

         initContainer();

         parseExtensionsString(extensions);
    }

  /**
   * Default Null extensions validator - always returns true
   *
   */
  public class DefaultExtensionsValidator implements ExtensionsValidator {

    /**
     * Validate the key - always returns true
     *
     * @param key <code>String</code> key to be validated
     * @return true
     */
    public boolean isValidKey(String key) {

      return true;
    }

    /**
     * Validate the value - always returns true
     *
     * @param key <code>String</code> key to be validated
     * @param value <code>String</code> value to be validated
     * @return true
     */
    public boolean isValidValue(String key, String value) {

      return true;
    }
 }


/**
 * UnitTest for ExtensionsHelper
 */

  public static class UnitTestExtensionsHelper extends TestCase {

    public UnitTestExtensionsHelper(String testName) {
        super(testName);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {

        TestSuite suite = new TestSuite();
        suite.addTest(new UnitTestExtensionsHelper("testDefaultConstruction"));
        suite.addTest(new UnitTestExtensionsHelper("testNullString"));
        suite.addTest(new UnitTestExtensionsHelper("testFieldDelimiterOnly"));
        suite.addTest(new UnitTestExtensionsHelper("testTagDelimiterOnly"));
        suite.addTest(new UnitTestExtensionsHelper("testNullValidator"));
        suite.addTest(new UnitTestExtensionsHelper("testBlankString"));
        suite.addTest(new UnitTestExtensionsHelper("testEmptyKeyString"));
        suite.addTest(new UnitTestExtensionsHelper("testEmptyValue"));
        suite.addTest(new UnitTestExtensionsHelper("testOneVariable"));
        suite.addTest(new UnitTestExtensionsHelper("testFieldWithEmbeddedTagDelimiter"));
        suite.addTest(new UnitTestExtensionsHelper("testNoTagDelimiterLastField"));
        suite.addTest(new UnitTestExtensionsHelper("testMultipleVariables"));
        suite.addTest(new UnitTestExtensionsHelper("testDanglingFieldDelimiter"));
        suite.addTest(new UnitTestExtensionsHelper("testLongString"));
        suite.addTest(new UnitTestExtensionsHelper("testWithOurOwnValidatorImpl"));
        suite.addTest(new UnitTestExtensionsHelper("testAppendExtensions"));
        suite.addTest(new UnitTestExtensionsHelper("testAppendExtensionsAsFirstCall"));
        suite.addTest(new UnitTestExtensionsHelper("testChangingFieldDelimiters"));
        suite.addTest(new UnitTestExtensionsHelper("testGetSetKeys"));
        suite.addTest(new UnitTestExtensionsHelper("testKeyNotFoundAndKeyEmpty"));
        return suite;

    }

    public void setUp() {

        System.out.println("Setup for test: "+name());

    }

  /**
   * Set of constants used for key values in unit tests
   *
   * Notice both integer and String Key types are supported
   */
  public static class UnitTestExtensionFields {

         public static final int TEST_KEY_1 = 10;
         public static final int TEST_KEY_2 = 20;
         public static final int TEST_KEY_3 = 3000;
         public static final String TEST_KEY_4 = "BARTID";
         public static final String  TEST_KEY_5 = "KEY5";
         public static final String TEST_KEY_6 = "AWAY_EXCHANGE";

  }

    /**
     * Test makes sure default constructor works - add a value to the empty
     * extensions container and make sure item is stored properly and that the
     * correct number of fields are stored
     */
    public void testDefaultConstruction() throws Exception {

        ExtensionsHelper exthelp = new ExtensionsHelper();
        assertEquals( "Make sure null string returns null extensions ", "", exthelp.toString() );
        assertEquals( "Make sure there are no items in the container", 0,exthelp.size() );
        //
        // Add an item to the empty container
        //
        int keyword1=ExtensionsHelper.UnitTestExtensionsHelper.UnitTestExtensionFields.TEST_KEY_2 ;
        String value1="TEST_VALUE1";
        exthelp.setValue(keyword1, value1);
        assertEquals( "Make sure there is one item",1,exthelp.size());
        assertEquals( "Make sure item can be retrieved",value1, exthelp.getValue(keyword1));

    }

    /**
     * Make sure that the ExtensionsHelper can be constructed using an empty string
     */
    public void testBlankString() throws Exception {

        ExtensionsHelper exthelp = new ExtensionsHelper("");
        assertEquals( "Make sure blank string returns blank extensions ", exthelp.toString(),"" );
        assertEquals( "Make sure there are no items in the container", 0,exthelp.size() );
    }


    /**
     * Test user supplying null string
     */
    public void testNullString() throws Exception {

        String nullStr = null;

        boolean results = false;

        try {
            ExtensionsHelper exthelp = new ExtensionsHelper(nullStr);
        } catch (java.text.ParseException e) {
            System.out.println("test: "+name()+"Caught expected ParseException for null string");
            results = true;
        }

        assertTrue( "Did not catch IllegalArgumentException as expected ", results );
    }

    /**
     * Test user supplying null string
     */
    public void testFieldDelimiterOnly() throws Exception {

        String nullStr = null;

        boolean results = true;

        ExtensionsHelper exthelp = new ExtensionsHelper();

        try {
            exthelp.setExtensions(exthelp.getFieldDelimiter());
        } catch (java.text.ParseException e) {
            results = false;
        }

        assertTrue( "Caught unexpected ParseException when extensions string = field delimiter only", results );
        assertEquals( "Number of fields should be zero when extensions = field delimiter only", 0, exthelp.size());
    }

    /**
     * Test user supplying null string
     */
    public void testTagDelimiterOnly() throws Exception {

        String nullStr = null;

        boolean results = false;

        ExtensionsHelper exthelp = new ExtensionsHelper();

        try {
            exthelp.setExtensions(exthelp.getTagDelimiter());
        } catch (java.text.ParseException e) {
            System.out.println("test: "+name()+"Caught expected ParseException for tag delimiter only");
            results = true;
        }

        assertTrue( "ParseException was not thrown as expected when extensions = tag delimiter only", results );
        assertEquals( "Number of fields should be zero when extensions field = tag delimiter only", 0, exthelp.size());
    }

    /**
     * Test user supplying null validator
     */
    public void testNullValidator() throws Exception {

        ExtensionsValidator nullValidator = null;

        boolean results = false;

        try {
            ExtensionsHelper exthelp = new ExtensionsHelper(nullValidator);
        } catch (java.lang.IllegalArgumentException e) {
            System.out.println("test: "+name()+"Caught expected IllegalArgumentException for null validator");
            results = true;
        }

        assertTrue( "Did not catch IllegalArgumentException as expected ", results );
    }

    /**
     * Make sure that the ExtensionsHelper can be constructed using an empty string
     */
    public void testEmptyKeyString() throws Exception {

        ExtensionsHelper exthelp = new ExtensionsHelper("");
        assertEquals( "Make sure null string returns null extensions ", exthelp.toString(),"" );
        assertEquals( "Make sure there are no items in the container", 0,exthelp.size() );

        try {

            exthelp.setValue("","");

        } catch (java.text.ParseException e) {

            System.out.println("test: "+name()+"() Caught expected ParseException: "+e.getMessage());
        }

        assertEquals( "Make sure null string returns null extensions ", exthelp.toString(),"" );
        assertEquals( "Make sure there are no items in the container", 0,exthelp.size() );

        //
        // Try the same thing with null instead of a blank string
        //
        try {

            exthelp.setValue(null,null);

        } catch (java.text.ParseException e) {

            System.out.println("test: "+name()+"() Caught expected ParseException: "+e.getMessage());
        }

        assertEquals( "Make sure null string returns null extensions ", exthelp.toString(),"" );
        assertEquals( "Make sure there are no items in the container", 0,exthelp.size() );
    }


    /**
     * Test case of one field in the extensions string passed to the constructor
     */
    public void testOneVariable() throws Exception {

        String keyword = ExtensionsHelper.UnitTestExtensionsHelper.UnitTestExtensionFields.TEST_KEY_6;
        String AwayExchange="ASE";
        String testString = keyword+"="+AwayExchange;

        ExtensionsHelper exthelp = new ExtensionsHelper(testString);
        assertEquals( "See if returned string matches input string", exthelp.toString(),testString );
        assertEquals( "Make sure there is one item",1,exthelp.size() );
        assertEquals( "Test Value 1 is returned for key "+keyword,AwayExchange,exthelp.getValue(keyword) );

        //
        //  Assign new String value to keyword
        //
        String differentValue="PSE";
        exthelp.setValue(keyword,differentValue);
        assertEquals( "Make sure there is still only one item",1,exthelp.size() );
        assertEquals( "Assign a new value to field "+keyword,differentValue,exthelp.getValue(keyword) );

        // Assign new String value to keyword A
        int value = 3;
        exthelp.setValue(keyword,value);
        assertEquals( "Make sure there is still only one item",1,exthelp.size() );
        assertEquals( "Assign a new integer value to field "+keyword,value,Integer.parseInt(exthelp.getValue(keyword)) );

        // Remove the tag value from the list
        exthelp.removeKey(keyword);
        assertEquals( "Make sure there are no entries remaining",0,exthelp.size());

        // Try and get a value from an empty list
        assertTrue( "Retrieving from an empty list should return a null",exthelp.getValue(keyword)==null);

    }

    /**
     * Test case of one field in the extensions string passed to the constructor
     */
    public void testFieldWithEmbeddedTagDelimiter() throws Exception {

        ExtensionsHelper exthelp = new ExtensionsHelper();
        String keyword = "TEST1";
        String value="5" + exthelp.getTagDelimiter()+ "ABC" + exthelp.getTagDelimiter() + "3";
        String testString = keyword + exthelp.getTagDelimiter() + value;

        exthelp.setExtensions(testString);
        assertEquals( "See if returned string matches input string", exthelp.toString(),testString );
        assertEquals( "Make sure there is one item",1,exthelp.size() );
        assertEquals( "Test Correct value is returned for key "+keyword+ " even if it has embedded tag delimiters",value,exthelp.getValue(keyword) );
    }


    /**
     * Test case of one field in the extensions string passed to the constructor
     */
    public void testNoTagDelimiterLastField() throws Exception {

        ExtensionsHelper exthelp = new ExtensionsHelper();
        String keyword = "TEST1";
        String firstKey = "A";
        String firstValue = "1";
        String secondKey = "BBB";
        String secondValue = "2";
        String lastKey = "C";
        String value="5" + exthelp.getTagDelimiter()+ "ABC" + exthelp.getTagDelimiter() + "3";
        String testString = firstKey + exthelp.getTagDelimiter() + firstValue + exthelp.getFieldDelimiter() +
                            secondKey + exthelp.getTagDelimiter() + secondValue + exthelp.getFieldDelimiter() +
                            lastKey;

        exthelp.setExtensions(testString);
        assertEquals( "Make sure there are three items",3,exthelp.size() );
        assertEquals( "Test Correct value is returned for key "+firstKey,firstValue,exthelp.getValue(firstKey) );
        assertEquals( "Test Correct value is returned for key "+secondKey,secondValue,exthelp.getValue(secondKey) );
        assertEquals( "Test Correct value is returned for key "+lastKey,"",exthelp.getValue(lastKey) );
    }

    /**
     * This is a test using multiple variables in the ExtensionsHelper
     */
    public void testMultipleVariables() throws Exception {

        String testString1 = "201=1";
        String keyword1= "201";
        String testValue1="1";
        ExtensionsHelper exthelp = new ExtensionsHelper(testString1);
        assertEquals( "Test Value 201=1 is returned by toString()", exthelp.toString(),testString1 );
        assertEquals( "Test Value 1 is returned for key "+keyword1,testValue1,exthelp.getValue(keyword1) );
        assertEquals( "Size of array after first entry should be 1",1,exthelp.size());
        //
        //  Add a second variable
        //
        int keyword2=13;
        double testValue2=1.13;
        exthelp.setValue(keyword2,testValue2);
        assertEquals( "Verify that there are two elements",2,exthelp.size());
        assertEquals( "Retrieve value for send item as a double", Double.toString(testValue2), exthelp.getValue(keyword2));
        assertEquals( "Make sure we can still retrieve the first item", testValue1, exthelp.getValue(keyword1) );

        //
        // Add a third item
        //

        int keyword3=ExtensionsHelper.UnitTestExtensionsHelper.UnitTestExtensionFields.TEST_KEY_2;
        int testValue3 = 32000;
        exthelp.setValue(keyword3,testValue3);
        assertEquals( "Verify that there are three elements",3,exthelp.size());
        assertEquals( "Retrieve third value",testValue3, Integer.parseInt(exthelp.getValue(keyword3)));
        assertEquals( "Make sure we can still retrieve the first item", testValue1, exthelp.getValue(keyword1) );
        assertEquals( "Make sure we can still retrieve second item", Double.toString(testValue2), exthelp.getValue(keyword2) );

        //
        // Add a blank item
        //
        String keyword4=ExtensionsHelper.UnitTestExtensionsHelper.UnitTestExtensionFields.TEST_KEY_4;
        exthelp.setValue(keyword4,"");
        assertEquals( "Verify that there are four elements",4,exthelp.size());
        assertEquals( "Retrieve fourth value","", exthelp.getValue(keyword4));
        assertEquals( "Make sure we can still retrieve the first item", testValue1, exthelp.getValue(keyword1) );
        assertEquals( "Make sure we can still retrieve second item", Double.toString(testValue2), exthelp.getValue(keyword2) );
        assertEquals( "Make sure we can still retrieve third item",testValue3, Integer.parseInt(exthelp.getValue(keyword3)));

        //
        // Remove one item
        //
        exthelp.removeKey(keyword1);
        assertEquals( "Make sure we can still retrieve second item", Double.toString(testValue2), exthelp.getValue(keyword2) );
        assertEquals( "Make sure we can still retrieve third item",testValue3, Integer.parseInt(exthelp.getValue(keyword3)) );
        assertEquals( "Make sure we can still retrieve fourth item","", exthelp.getValue(keyword4));
        assertEquals( "Verify that there are three elements remaining",3,exthelp.size());

        //
        // Remove another item
        //
        exthelp.removeKey(keyword4);
        assertEquals( "Make sure we can still retrieve second item", Double.toString(testValue2), exthelp.getValue(keyword2) );
        assertEquals( "Make sure we can still retrieve third item",testValue3, Integer.parseInt(exthelp.getValue(keyword3)) );
        assertEquals( "Verify that there are two elements remaining",2,exthelp.size());

        System.out.println("test: "+name());

    }

    /**
     * This is a test of various constructions with strings
     */
    public void testLongString() throws Exception {

        ExtensionsHelper exthelp = new ExtensionsHelper();

        String validString1 = "10=1" + exthelp.getFieldDelimiter() +
                              "20=2" + exthelp.getFieldDelimiter() +
                              "30=13" + exthelp.getFieldDelimiter() +
                              "15=37" + exthelp.getFieldDelimiter() +
                              "25=D" + exthelp.getFieldDelimiter() +
                              "35=0" + exthelp.getFieldDelimiter() +
                              "45=D" + exthelp.getFieldDelimiter() +
                              "ALPHABETIC_KEY=D" + exthelp.getFieldDelimiter() +
                              "LONG_KEY_NAME=D" + exthelp.getFieldDelimiter() +
                              "ANOTHER_LONG_KEY=Even longer value" + exthelp.getFieldDelimiter() +
                              "11=A 1" ;

        exthelp.setExtensions(validString1);
        assertEquals( "Make sure there are the correct number of entries", 11,exthelp.size() );
     }

    /**
     * This is a test of various constructions with strings
     */
    public void testDanglingFieldDelimiter() throws Exception {        //
        // Test condition if there is an extra delimiter at the end of the string
        //
        String validString2= "A=1";

        ExtensionsHelper exthelp2 = new ExtensionsHelper();

        String validString2WithFieldDelimiter = validString2 + exthelp2.getFieldDelimiter();

        exthelp2.setExtensions(validString2WithFieldDelimiter);

        assertEquals( "Make sure there are the correct number of entries", 1,exthelp2.size() );
        assertEquals( "Retrieve Value","1",exthelp2.getValue("A"));
        assertEquals( "Make sure the string is returned properly - the field delimiter should be removed",validString2,exthelp2.toString() );
    }



    /**
     * Test the append method to add fields from an additional extensions string
     */
    public void testAppendExtensions() throws Exception {        //

        ExtensionsHelper exthelp = new ExtensionsHelper();

        String initialFields= "A=1" + exthelp.getFieldDelimiter() +
                              "B=2" + exthelp.getFieldDelimiter() +
                              "C=3";

        String additionalFields = "D=5" + exthelp.getFieldDelimiter() +
                                  "E=6" + exthelp.getFieldDelimiter() +
                                  "F=6";

        exthelp.setExtensions(initialFields);
        assertEquals( "Make sure there are the correct number of entries initially", 3,exthelp.size() );

        exthelp.appendExtensions(additionalFields);
        assertEquals( "Make sure there are the correct number of entries after append", 6,exthelp.size() );
        assertEquals( "Make sure the last field has the correct value","6",exthelp.getValue("F") );
    }


    /**
     * Test the append method as an initial call
     */
    public void testAppendExtensionsAsFirstCall() throws Exception {        //

        ExtensionsHelper exthelp = new ExtensionsHelper();

        String initialFields= "A=1" + exthelp.getFieldDelimiter() +
                              "B=2" + exthelp.getFieldDelimiter() +
                              "C=3";

        String additionalFields = "D=5" + exthelp.getFieldDelimiter() +
                                  "E=6" + exthelp.getFieldDelimiter() +
                                  "F=6";

        exthelp.appendExtensions(initialFields);
        assertEquals( "Make sure there are the correct number of entries initially", 3,exthelp.size() );

        exthelp.appendExtensions(additionalFields);
        assertEquals( "Make sure there are the correct number of entries after append", 6,exthelp.size() );
        assertEquals( "Make sure the last field has the correct value","6",exthelp.getValue("F") );
    }

    /**
     * This with our own validator provided
     */
    public static class UnitTestExtensionsValidator implements ExtensionsValidator {

      /**
       * Creating a static array from existing constants.
       *
       * Because list is less than ten - using a simple array
       */
      public String validKeys[]  = {
        ""+UnitTestExtensionFields.TEST_KEY_1,
        ""+UnitTestExtensionFields.TEST_KEY_2,
        ""+UnitTestExtensionFields.TEST_KEY_3,
        UnitTestExtensionFields.TEST_KEY_4,
        UnitTestExtensionFields.TEST_KEY_5,
        UnitTestExtensionFields.TEST_KEY_6,
     };

      public String validValuesForKey1[] = {"A","SIMPLE","TEST","FOR","VALUES"};

      UnitTestExtensionsValidator() {

      }

      /**
       * See if the key is part of the list of valid keys
       */
      public boolean isValidKey(String key) {

         for (int i = 0; i < validKeys.length ; i++ ) {

              if ( key.equals(validKeys[i]) ) {

                 return true;
              }
         }

         return false;
      }

      /**
       * See if the values for a given key need to be validated - if so
       * then validate the values for that key.
       *
       * Notice that in this example only TEST_KEY_1 values are validated
       * All the rest are assumed correct.
       */
      public boolean isValidValue(String key, String value) {

         /**
          * For this test just validate values for TEST_KEY_1
          */
          if ( !key.equals(Integer.toString(UnitTestExtensionFields.TEST_KEY_1) ) ) {

              return true;
          }

          for ( int i = 0; i < validValuesForKey1.length ; i++ ) {

               if ( value.equals(validValuesForKey1[i]) ) {

                   return true;
               }
          }

          return false;
      }

    }

    public void testWithOurOwnValidatorImpl() throws Exception {

        UnitTestExtensionsValidator validator = new UnitTestExtensionsValidator();

        ExtensionsHelper exthelp = new ExtensionsHelper(validator);

        String validString1 = UnitTestExtensionFields.TEST_KEY_1+exthelp.getTagDelimiter()+
                                  validator.validValuesForKey1[0] + exthelp.getFieldDelimiter() +
                              UnitTestExtensionFields.TEST_KEY_2+exthelp.getTagDelimiter()+
                                  "AnyValue2"  + exthelp.getFieldDelimiter() +
                              UnitTestExtensionFields.TEST_KEY_3+exthelp.getTagDelimiter()+
                                  "25" + exthelp.getFieldDelimiter() +
                              UnitTestExtensionFields.TEST_KEY_4+exthelp.getTagDelimiter()+
                                  "Value3" + exthelp.getFieldDelimiter() +
                              UnitTestExtensionFields.TEST_KEY_5+exthelp.getTagDelimiter()+
                                  "YetAnotherValue" + exthelp.getFieldDelimiter() +
                              UnitTestExtensionFields.TEST_KEY_6+exthelp.getTagDelimiter() +
                                  "LastValue" + exthelp.getFieldDelimiter();

        exthelp.setExtensions(validString1);

        assertEquals( "Make sure there are the correct number of entries", 6,exthelp.size() );

        /**
         * Set a valid value for TEST_KEY_1
         */
        try {
            exthelp.setValue(UnitTestExtensionFields.TEST_KEY_1,validator.validValuesForKey1[1]);
        }
        catch (Exception e) {
            assertTrue("Valid Value could not be stored to TEST_KEY_1",false);
        }
        /**
         * Try setting an invalid value for TEST_KEY_1
         */
        try {
            exthelp.setValue(UnitTestExtensionFields.TEST_KEY_1,"ThisIsNotAValidValue");
            assertTrue("Invalid value was allowed to be stored to TEST_KEY_1",false);
        }
        catch (java.text.ParseException e) {
            System.out.println("test: "+name()+"() Caught expected ParseException: "+e.getMessage());
        }

    }

    public void testEmptyValue() throws Exception {

        ExtensionsHelper exthelp = new ExtensionsHelper();

        try {
            exthelp.setValue("TEST_STRING",null);
        }
        catch (Exception e) {
            System.out.println("Caught expected exception when null value is provided"+e);
        }


    }


    public void testChangingFieldDelimiters() throws Exception {

        String JavelinFieldDelimiter = "|";

        String testStr1 = "5201=10|5202=20020914-12:01:21.123|5203=5|5204=1.59|5208=20020914-12:01:21.123";

        ExtensionsHelper exthelp = new ExtensionsHelper(testStr1,JavelinFieldDelimiter,ExtensionsHelper.DEFAULT_TAG_DELIMITER);

        assertEquals( "Make sure there are the correct number of entries", 5,exthelp.size() );

        assertEquals( "Make sure proper field delimiter has been set",JavelinFieldDelimiter,exthelp.getFieldDelimiter());
        assertEquals( "Make sure proper tag delimiter has been set",ExtensionsHelper.DEFAULT_TAG_DELIMITER,exthelp.getTagDelimiter());
        String out1 = exthelp.toString();
        System.out.println("test: "+name()+" test string before delimiter change: "+out1);

        exthelp.setFieldDelimiter(ExtensionsHelper.DEFAULT_FIELD_DELIMITER);

        assertEquals( "Make sure proper field delimiter has been set",ExtensionsHelper.DEFAULT_FIELD_DELIMITER,exthelp.getFieldDelimiter());
        assertEquals( "Make sure proper tag delimiter has been set",ExtensionsHelper.DEFAULT_TAG_DELIMITER,exthelp.getTagDelimiter());
        String out2 = exthelp.toString();
        System.out.println("test: "+name()+" test string after delimiter change: "+out2);

        assertTrue("Make sure toString() is using a different field delimiter on output",! out1.equals(out2));

    }

    public void testKeyNotFoundAndKeyEmpty() throws Exception {

        String keyWithBlankValue = "BLANK_VALUE";
        String keyNotFound = "ThisKeyIsNotInExtensions";

        ExtensionsHelper exthelp = new ExtensionsHelper(keyWithBlankValue);

        String s = exthelp.getValue(keyNotFound);
        assertTrue("A Key that is not in the extensions should return null",s==null);

        String t = exthelp.getValue(keyWithBlankValue);
        assertEquals("A Key that is found but has no value should return an empty string - empty string is valid value",t,"");

        String defaultValue="ABCD";
        String u = exthelp.getValue(keyNotFound,defaultValue);
        assertEquals("Returned value should equal default value",exthelp.getValue(u,defaultValue),defaultValue);

    }


    public void testGetSetKeys() throws Exception {

        ExtensionsHelper exthelp = new ExtensionsHelper();

        String validString1 = UnitTestExtensionFields.TEST_KEY_1+exthelp.getTagDelimiter()+
                                  "FirstValue" + exthelp.getFieldDelimiter() +
                              UnitTestExtensionFields.TEST_KEY_2+exthelp.getTagDelimiter()+
                                  "AnyValue2"  + exthelp.getFieldDelimiter() +
                              UnitTestExtensionFields.TEST_KEY_3+exthelp.getTagDelimiter()+
                                  "25" + exthelp.getFieldDelimiter() +
                              UnitTestExtensionFields.TEST_KEY_4+exthelp.getTagDelimiter()+
                                  "Value3" + exthelp.getFieldDelimiter() +
                              UnitTestExtensionFields.TEST_KEY_5+exthelp.getTagDelimiter()+
                                  "YetAnotherValue" + exthelp.getFieldDelimiter() +
                              UnitTestExtensionFields.TEST_KEY_6+exthelp.getTagDelimiter() +
                                  "LastValue" + exthelp.getFieldDelimiter();
 
        exthelp.setExtensions(validString1);

        assertEquals( "Make sure there are the correct number of entries", 6,exthelp.size() );

        Set keySet = exthelp.getKeys();

        assertEquals("Make sure same number of elements are returned",exthelp.size(),keySet.size());

        String key = null;
        int i=0;
        for(Iterator iter = keySet.iterator(); iter.hasNext();i++) {
           key = (String)iter.next();
           assertTrue("key should not be null", !(key == null));
           assertTrue("Value should not be null for this test data in extensions",!(exthelp.getValue(key)==null));
        
        }
        assertEquals("Number iterated should equal number in extensions",i,exthelp.size());

     }

    public void tearDown() {

    }

    public String name()
    {
        return getName();
    }
    
  }

}
