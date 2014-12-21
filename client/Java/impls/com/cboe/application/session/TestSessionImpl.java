package com.cboe.application.session;

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiAdmin.*;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiAdmin.MessageStruct;

public class TestSessionImpl implements UserSessionManagerOperations{

  public MarketQuery getMarketQuery() {
    return null;
  }

  public OrderQuery getOrderQuery() {
    return null;
  }

  public ProductQuery getProductQuery() {
    return null;
  }

  public Quote getQuote() {
    return null;
  }

  public void getSystemQuery() {
  }

  public OrderEntry getOrderEntry() {
    return null;
  }

  public Administrator getAdministrator() {
    return null;
  }

  public UserHistory getUserHistory() {
    return null;
  }

  public ProductDefinition getProductDefinition() {
    return null;
  }
  public UserPreferenceQuery getUserPreferenceQuery() {
    return null;
  }

  public TradingSession getTradingSession() {
    return null;
  }

  public UserTradingParameters getUserTradingParameters() {
    return null;
  }


  public UserStruct getValidUser() {
    return null;
  }

  public SessionProfileUserStruct getValidSessionProfileUser() {
    return null;
  }

  public void logout()
  {
  }

  public String getVersion()
  {
    return "1.0";
  }

    public int sendMessage(MessageStruct message)
    {
        return 0;
    }

    public void publishMessages(String userId)
    {
    }


  public void authenticate(UserLogonStruct userLogon) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.AuthenticationException, com.cboe.exceptions.DataValidationException {
        //Implement this com.cboe.idl.cmi._UserSessionManagerOperations method;
  }

  public void changePassword(String parm1, String parm2) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException {
        //Implement this com.cboe.idl.cmi._UserSessionManagerOperations method;
  }

  public DateTimeStruct getSystemDateTime() throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException {
        //Implement this com.cboe.idl.cmi._UserSessionManagerOperations method;
        return null;
  }
}
