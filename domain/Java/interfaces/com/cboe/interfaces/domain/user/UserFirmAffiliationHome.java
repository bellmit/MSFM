package com.cboe.interfaces.domain.user;

import com.cboe.idl.user.UserFirmAffiliationStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;

/**
 * Created by IntelliJ IDEA.
 * User: EbrahimR
 * Date: Oct 27, 2004
 * Time: 3:24:52 PM
 * To change this template use Options | File Templates.
 */
public interface UserFirmAffiliationHome {

    public final static String HOME_NAME = "UserFirmAffiliationHome";
    com.cboe.interfaces.domain.user.UserFirmAffiliation create(UserFirmAffiliationStruct newUserFirmAffiliationStruct) throws DataValidationException;

    UserFirmAffiliation[] findAll();

    UserFirmAffiliation[] findByAffiliatedFirm(String affiliatedFirm) throws NotFoundException;

    UserFirmAffiliation findByUserExchange(ExchangeAcronymStruct exchangeAcronymStruct) throws NotFoundException;

    UserFirmAffiliationStruct toUserFirmAffiliationStruct(com.cboe.interfaces.domain.user.UserFirmAffiliation userFirmAffiliation);

    void deleteUserFirmAffiliation(UserFirmAffiliationStruct newUserFirmAffiliationStruct)throws DataValidationException, SystemException;
}
