package com.cboe.interfaces.domain;
import com.cboe.idl.property.PropertyGroupStruct;
import com.cboe.idl.property.PropertyStruct;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.exceptions.SystemException;

/**
 * Created by IntelliJ IDEA.
 * User: EbrahimR
 * Date: Jun 20, 2003
 * Time: 5:11:33 PM
 * To change this template use Options | File Templates.
 */
public interface PropertyGroup
{
    /**
     * returns property category
     * @return
     */
    String getCategory();
    /**
     * returns property key. e.g user id
     * @return
     */
    String getPropertyKey();

    /**
     * returns the version number for last update
     * @return
     */
    int getVersion();
    /**
     * returns property values associated with this group
     * @return
     */
    PropertyStruct[] getProperties();

    long getId();

    public void setPropertyGroup(PropertyGroupStruct propertyGroupStruct) throws DataValidationException, TransactionFailedException, SystemException;





}

