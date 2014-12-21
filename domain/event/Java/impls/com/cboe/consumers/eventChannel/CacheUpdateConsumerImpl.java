package com.cboe.consumers.eventChannel;

/**
 * @author William Wei
 */
import com.cboe.idl.cmiUser.UserStruct;
import com.cboe.idl.user.UserDefinitionStruct;
import com.cboe.idl.user.UserEnablementStruct;
import com.cboe.idl.user.UserSummaryStruct;
import com.cboe.idl.firm.FirmStruct;
import com.cboe.interfaces.events.*;
import com.cboe.idl.user.SessionProfileUserDefinitionStruct;
import com.cboe.idl.user.UserFirmAffiliationStruct;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;

public class CacheUpdateConsumerImpl extends com.cboe.idl.events.POA_CacheUpdateEventConsumer implements CacheUpdateConsumer {
    private CacheUpdateConsumer delegate;
    /**
     * constructor comment.
     */
    public CacheUpdateConsumerImpl(CacheUpdateConsumer cacheUpdateEventConsumer) {
        super();
        delegate = cacheUpdateEventConsumer;
    }

    //public void acceptUserUpdate(UserStruct userStruct, UserDefinitionStruct userDefinitionStrcut) {
        //delegate.acceptUserUpdate(userStruct, userDefinitionStrcut);
    //}

    public void acceptUserUpdate(UserStruct userStruct,
                                 UserDefinitionStruct userDefinitionStruct,
                                 UserEnablementStruct userEnablementStruct) {
        // delegate.acceptUserUpdate(userStruct, userDefinitionStruct, userEnablementStruct);
    }
    public void acceptSessionProfileUserUpdate(SessionProfileUserStruct updatedUser, SessionProfileUserDefinitionStruct updatedUserDefinition, UserEnablementStruct updatedUserEnablement)
    {
        delegate.acceptSessionProfileUserUpdate(updatedUser, updatedUserDefinition, updatedUserEnablement);
    }

    public void acceptUserFirmAffiliationDelete(UserFirmAffiliationStruct userFirmAffiliationStruct)
    {
        delegate.acceptUserFirmAffiliationDelete(userFirmAffiliationStruct);
    }

    public void acceptUserFirmAffiliationUpdate(UserFirmAffiliationStruct userFirmAffiliationStruct)
    {
        delegate.acceptUserFirmAffiliationUpdate(userFirmAffiliationStruct);
    }
    public void acceptUserDeletion(UserSummaryStruct userSummary) {
        delegate.acceptUserDeletion(userSummary);
    }

    public void acceptFirmUpdate(FirmStruct firmStruct) {
        delegate.acceptFirmUpdate(firmStruct);
    }

    public void acceptFirmDeletion(FirmStruct firmStruct) {
        delegate.acceptFirmDeletion(firmStruct);
    }

    /**
     * @author Jeff Illian
     */
    public org.omg.CORBA.Object get_typed_consumer() {
        return null;
    }

    public void push(org.omg.CORBA.Any data)
    throws org.omg.CosEventComm.Disconnected {
    }

    public void disconnect_push_consumer() {
    }
}
