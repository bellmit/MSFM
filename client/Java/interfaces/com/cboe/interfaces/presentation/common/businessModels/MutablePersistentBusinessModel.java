package com.cboe.interfaces.presentation.common.businessModels;

import java.beans.*;
import org.omg.CORBA.UserException;

/**
 * Describes the contract that a SBT GUI Business object will support.
 */
public interface MutablePersistentBusinessModel extends MutableBusinessModel
{

    /**
     *  Saves any mods made to the business model
     */
    public void saveChanges() throws UserException;

    /**
     *  Reloads data from the server
     */
    public void refreshData() throws UserException;
    
}