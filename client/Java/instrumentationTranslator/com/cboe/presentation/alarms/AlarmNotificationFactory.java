//
// -----------------------------------------------------------------------------------
// Source file: AlarmNotificationFactory.java
//
// PACKAGE: com.cboe.presentation.alarms
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms;

import com.cboe.idl.alarm.AlarmNotificationStruct;
import com.cboe.idl.alarm.AlarmNotificationConditionStruct;

import com.cboe.interfaces.instrumentation.alarms.AlarmNotification;
import com.cboe.interfaces.instrumentation.alarms.AlarmNotificationCondition;

public final class AlarmNotificationFactory
{
    private AlarmNotificationFactory(){};

    public static AlarmNotification createAlarmNotification(AlarmNotificationStruct struct, long receivedTime)
//            throws NotFoundException
    {
        AlarmNotification notification = new AlarmNotificationImpl(struct, receivedTime);
//        testNotification(notification);
        return notification;
    }

    static AlarmNotificationCondition createAlarmNotificationCondition(AlarmNotificationConditionStruct struct)
//            throws NotFoundException
    {
        AlarmNotificationCondition notifyCondition = new AlarmNotificationConditionImpl(struct);
//        testNotificationCondition(notifyCondition);
        return notifyCondition;
    }

    static AlarmNotificationCondition createAlarmNotificationCondition(AlarmNotificationConditionStruct struct,
                                                                       AlarmNotification notification)
//            throws NotFoundException
    {
        AlarmNotificationCondition notifyCondition = new AlarmNotificationConditionImpl(struct, notification);
//        testNotificationCondition(notifyCondition);
        return notifyCondition;
    }

    public static AlarmNotificationConditionHistoricalImpl createAlarmNotificationConditionHistorical(AlarmNotificationConditionStruct struct)
    //throws NotFoundException
    {
        AlarmNotificationConditionHistoricalImpl notifyConditionHist = new AlarmNotificationConditionHistoricalImpl(struct);
        //testNotificationCondition(notifyCondition);
        return notifyConditionHist;
    }

//    private static void testNotification(AlarmNotification notification)
//            throws NotFoundException
//    {
//        AlarmActivation activation = notification.getAlarmActivation();
//        if(activation != null)
//        {
//            notification.getMatchedConditions();
//        }
//        else
//        {
//            //throw exception
//            throw ExceptionBuilder.notFoundException("Alarm Activation or Alarm Definition could not be found for " +
//                                                     "Alarm Notification.", NotFoundCodes.RESOURCE_DOESNT_EXIST);
//        }
//    }
//
//    private static void testNotificationCondition(AlarmNotificationCondition notifyCondition)
//            throws NotFoundException
//    {
//        AlarmCondition condition = notifyCondition.getCondition();
//        if(condition == null)
//        {
//            //throw exception
//            throw ExceptionBuilder.notFoundException("Alarm Condition could not be found for Alarm Notification " +
//                                                     "Condition.", NotFoundCodes.RESOURCE_DOESNT_EXIST);
//        }
//    }
}
