package com.cboe.domain.util;

/**
 * A helper class that uses an ExtensionsHelper to store and retrieve fields from a
 * the UserAssignedCancelID field. The user can store a user assigned cancel ID and
 * an ExecID in the UserAssignedCancelID field.
 *
 * This class wraps the <code>com.cboe.domain.util.ExtensionsHelper</code> class.
 *
 * Copyright:    Copyright (c) 2002 Chicago Board Options Exchange
 * @author Northey, LTG INC
 */

import junit.framework.*;

public class UserAssignedCancelIDHelper {

    public static final String USER_ASSIGNED_CANCEL_ID_KEY="USR_ASGND_CXL_ID";
    public static final String EXEC_ID_KEY="CANCEL_EXEC_ID";

    private ExtensionsHelper userAssignedCancel;

   /**
    * Default Construct a UserAssignedCancelIDHelper
    *
    */
    public UserAssignedCancelIDHelper() {
        userAssignedCancel = new ExtensionsHelper();
    }

   /**
    * Construct a UserAssignedCancelIDHelper from a UserAssignedCancelID value and an ExecID value
    *
    * @param userAssignedCancelIDvalue <code>String</code> user assigned cancel id to be stored in the UserAssignedCancelIDfield
    * @param execIDValue <code>String</code> execution id to be stored in the UserAssignedCancelIDfield
    *
    * @throws java.text.ParseException
    */
    public UserAssignedCancelIDHelper(String userAssignedCancelIDValue, String execIDValue) throws java.text.ParseException {
         this();
         setUserAssignedCancelID(userAssignedCancelIDValue);
         setExecID(execIDValue);
    }

   /**
    * Construct a UserAssignedCancelIDHelper from a UserAssignedCancelIDField that has encoded key=value
    * fields stored within it.
    *
    * @param UserAssignedCancelIDField <code>String</code> from CancelReport message
    *
    * @throws java.text.ParseException
    */
    public UserAssignedCancelIDHelper(String userAssignedCancelIDField) throws java.text.ParseException {

        userAssignedCancel = new ExtensionsHelper(userAssignedCancelIDField);
    }

    /**
     * Set the UserAssignedCancelIDValue into the UserAssignedCancelID
     *
     * @param execIDValue <code>String</code> execid value to be set in the UserAssignedCancelID
     *
     * @throws java.text.ParseException
     */
    public void setUserAssignedCancelID(String userAssignedCancelIDvalue) throws java.text.ParseException {
       userAssignedCancel.setValue(USER_ASSIGNED_CANCEL_ID_KEY,userAssignedCancelIDvalue);
    }

    /**
     * Set the ExecID into the UserAssignedCancelID
     *
     * @param execIDValue <code>String</code> execid value to be set in the UserAssignedCancelID
     *
     * @throws java.text.ParseException
     */
    public void setExecID(String execIDvalue) throws java.text.ParseException {
       userAssignedCancel.setValue(EXEC_ID_KEY, execIDvalue);
    }

    /**
     * return the UserAssignedCancelID or blank if one was not set previously
     *
     * @return <code>String</code> UserAssignedCancelID value previously set or blank
     */
    public String getUserAssignedCancelID() {
       return userAssignedCancel.getValue(USER_ASSIGNED_CANCEL_ID_KEY,"");
    }

    /**
     * return the ExecID or blank if one was not set previously
     *
     * @return <code>String</code> value for ExecID or blank if not previously stored
     */
    public String getExecID() {
       return userAssignedCancel.getValue(EXEC_ID_KEY,"");
    }

    /**
     * Return the user assigned cancel id fields as an encoded string for storage
     * into the userAssignedCancelID field
     *
     * @return <code>String</code> containing the encoded user assigned cancel
     * id and exec id fields - if previously set by the user.
     */
    public String toString() {
       return userAssignedCancel.toString();
    }

    public static class UnitTestUserAssignedCancelIDHelper extends TestCase {

    public UnitTestUserAssignedCancelIDHelper(String testName) {
        super(testName);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {

        TestSuite suite = new TestSuite();
        suite.addTest(new UnitTestUserAssignedCancelIDHelper("testDefaultConstruction"));
        suite.addTest(new UnitTestUserAssignedCancelIDHelper("testConstructionFromField"));
        suite.addTest(new UnitTestUserAssignedCancelIDHelper("testConstructionFromNullField"));
        suite.addTest(new UnitTestUserAssignedCancelIDHelper("testConstructionFromEmptyField"));
        suite.addTest(new UnitTestUserAssignedCancelIDHelper("testConstructionFromInvalidField"));
        suite.addTest(new UnitTestUserAssignedCancelIDHelper("testConstructionFromValues"));
        suite.addTest(new UnitTestUserAssignedCancelIDHelper("testConstructionFromValuesWithNulls"));
        return suite;

    }

    public void setUp() {

        System.out.println("Setup for test: "+getName());

    }

    /**
     * Test user supplying null string
     */
    public void testDefaultConstruction() throws Exception {

        UserAssignedCancelIDHelper helper = new UserAssignedCancelIDHelper();

        //
        // This test looks at the private ExtensionsHelper within the UserAssignedCancelIDHelper to make sure it was properly constructed
        //

        assertEquals("Make sure the ExtensionsHelper was created and there are no values in the helper",0,helper.userAssignedCancel.size());

        //
        // Make sure we can add something to a default constructed object
        //

        String execIdTestValue = "EXECIDTESTVALUE";
        helper.setExecID(execIdTestValue);
        assertEquals("Make sure we can add something to it",execIdTestValue,helper.getExecID());
        assertEquals("Make sure the proper number of values were stored",1,helper.userAssignedCancel.size());

        //
        // Add a user assigned cancel id
        //

        String userAssignedCancelIdTestValue = "0690AAA000120020405";
        helper.setUserAssignedCancelID(userAssignedCancelIdTestValue);
        assertEquals("Make sure we can add something to it",userAssignedCancelIdTestValue,helper.getUserAssignedCancelID());
        assertEquals("Make sure the proper number of values were stored",2,helper.userAssignedCancel.size());        
   

    }

    /**
     * Construct an UserAssignedCancelIDHelper from an encoded field
     */
    public void testConstructionFromField() throws Exception {

       String userAssignedCancelIdTestValue =  "0427ABC999920020919" ;
       String execIdTestValue = "0427ABC999900000001";
       String userCancelIDString = UserAssignedCancelIDHelper.USER_ASSIGNED_CANCEL_ID_KEY + ExtensionsHelper.DEFAULT_TAG_DELIMITER +
                                    userAssignedCancelIdTestValue + 
                                   ExtensionsHelper.DEFAULT_FIELD_DELIMITER +
                                    UserAssignedCancelIDHelper.EXEC_ID_KEY + ExtensionsHelper.DEFAULT_TAG_DELIMITER +
                                   execIdTestValue;
       
        UserAssignedCancelIDHelper helper = new UserAssignedCancelIDHelper(userCancelIDString);

        //
        // This test looks at the private ExtensionsHelper within the UserAssignedCancelIDHelper to make sure it was properly constructed
        //

        assertEquals("Make sure the ExtensionsHelper was created and there are no values in the helper",2,helper.userAssignedCancel.size());

        //
        // Check to see if values were returned properly
        //
        assertEquals("Make sure execID value was stored",execIdTestValue,helper.getExecID());

        assertEquals("Make sure user assigned canceld ID value was stored",userAssignedCancelIdTestValue,helper.getUserAssignedCancelID()); 

        assertTrue("Make sure a string is returned",helper.toString().length() > 0);
       
    }

    /**
     * Test the case when a null string is passed in as a field
     */
    public void testConstructionFromNullField() throws Exception {

        try {

            String nullstring = null;
            UserAssignedCancelIDHelper helper = new UserAssignedCancelIDHelper(nullstring);

        } catch (Exception e) {

            System.out.println("Caught Expected Exception when trying to construct using a null field "+e); 

       }

    }

    /**
     * Make sure that the UserAssignedCancelIDHelper can be initialized from an empty field
     */
    public void testConstructionFromEmptyField() throws Exception {

        try {

            String emptystring = "";
            UserAssignedCancelIDHelper helper = new UserAssignedCancelIDHelper(emptystring);
            assertEquals("Make sure the ExtensionsHelper was created and there are no values in the helper",0,helper.userAssignedCancel.size());

        } catch (Exception e) {

            System.out.println("Unexpected exception Caught when trying to construct using a null field "+e); 
            assertTrue("Exception should not have occurred",false);
       }

    }

    /**
     * Test building UserAssignedCancelIDHelper using an invalid field, in this case a tag delimiter without a key
     */
    public void testConstructionFromInvalidField() throws Exception {

       String userCancelIDString =  ExtensionsHelper.DEFAULT_TAG_DELIMITER;

        try {

             UserAssignedCancelIDHelper helper = new UserAssignedCancelIDHelper(userCancelIDString);
             assertEquals("Make sure the ExtensionsHelper was created and there are no values in the helper",0,helper.userAssignedCancel.size());

        } catch (Exception e) {

             System.out.println("Caught Expected Exception when trying to construct using a null field "+e); 

       }
       
    }

    /**
     * Test constructing a UserAssignedCancelIDHelper from values for user assigned cancel id and execid
     */
    public void testConstructionFromValues() throws Exception {

        String userAssignedCancelIdTestValue =  "0427ABC999920020919" ;
        String execIdTestValue = "0427ABC999900000001";

        UserAssignedCancelIDHelper helper = new UserAssignedCancelIDHelper(userAssignedCancelIdTestValue,execIdTestValue);

        assertEquals("Make sure the ExtensionsHelper was created and there are no values in the helper",2,helper.userAssignedCancel.size());    
        assertEquals("Make sure execID value was stored",execIdTestValue,helper.getExecID());
        assertEquals("Make sure user assigned cancel id value was stored",userAssignedCancelIdTestValue,helper.getUserAssignedCancelID());
        assertTrue("Make sure a string is returned",helper.toString().length() > 0);
    }

   /**
    *
    */
    public void testConstructionFromValuesWithNulls() throws Exception {

        String userAssignedCancelIdTestValue =  "0427ABC999920020919" ;
        String execIdTestValue = "0427ABC999900000001";
        String nullString = null;

        try {

            UserAssignedCancelIDHelper helper = new UserAssignedCancelIDHelper(userAssignedCancelIdTestValue,nullString);
            assertTrue("Should not have properly initialized with second value null",false);
            assertTrue("Make sure a string is returned",helper.toString().length() > 0);
            System.out.println(helper.toString());        

        } catch (Exception e) {

            System.out.println("Caught Expected exception when trying to construct with second value null"+e); 
        }


        try {

            UserAssignedCancelIDHelper helper = new UserAssignedCancelIDHelper(nullString,execIdTestValue);
            assertTrue("Should not have properly initialized with first value null",false);
            assertTrue("Make sure a string is returned",helper.toString().length() > 0);

        } catch (Exception e) {

            System.out.println("Caught Expected exception when trying to construct with first value null"+e); 
        }


        try {

            UserAssignedCancelIDHelper helper = new UserAssignedCancelIDHelper(nullString,nullString);
            assertTrue("Should not have properly initialized with both values null",false);
            assertTrue("Make sure a string is returned",helper.toString().length() > 0);

        } catch (Exception e) {

            System.out.println("Caught Expected exception when trying to construct with both values null"+e); 
        }


    }

    public void tearDown() {

    }
}
  
}

