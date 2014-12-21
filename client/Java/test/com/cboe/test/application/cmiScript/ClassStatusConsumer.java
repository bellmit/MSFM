package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallback.CMIClassStatusConsumerPOA;
import com.cboe.idl.cmiSession.ClassStateStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;

public class ClassStatusConsumer extends CMIClassStatusConsumerPOA
{
    public void updateProductClass(SessionClassStruct updatedClass)
    {
        Log.message("ClassStatusConsumer.updateProductClass "
                + Struct.toString(updatedClass));
    }
    
    public void acceptClassState(ClassStateStruct classState[])
    {
        Log.message("ClassStatusConsumer.acceptClassState "
                + Struct.toString(classState));
    }
}
