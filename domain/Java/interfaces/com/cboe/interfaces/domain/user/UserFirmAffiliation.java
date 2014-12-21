package com.cboe.interfaces.domain.user;

/**
 * Created by IntelliJ IDEA.
 * User: EbrahimR
 * Date: Oct 27, 2004
 * Time: 3:11:27 PM
 * To change this template use Options | File Templates.
 */
public interface UserFirmAffiliation {
    String getUserAcronym();

    String getExchangeAcronym();

    String getAffiliatedFirm();

    void setUserAcronym( String userAcronym );

    void setExchangeAcronym( String exchangeAcronym );

    void setAffiliatedFirm( String affiliatedFirm );
}
