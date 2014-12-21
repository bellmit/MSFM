package com.cboe.interfaces.application;

/**
 * This is the common interface for the User History Home
 * @author Dean Grippo
 */
public interface UserHistoryHome
{

   public final static String HOME_NAME = "UserHistoryHome";


  /**
   * Creates an instance of the User History service.
   *
   * @return reference to User History service
   * @author Dean Grippo
   */
  public UserHistory create(SessionManager session);

}
