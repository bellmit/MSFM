//
// -----------------------------------------------------------------------------------
// Source file: AlarmCalculationFactory.java
//
// PACKAGE: com.cboe.presentation.alarms
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms;

import com.cboe.idl.alarm.AlarmCalculationStruct;
import com.cboe.interfaces.instrumentation.alarms.AlarmCalculation;
import com.cboe.interfaces.instrumentation.alarms.AlarmCalculationMutable;

/**
 * @author Thomas Morrow
 * @since Sep 16, 2008
 */
public class AlarmCalculationFactory
{
    private AlarmCalculationFactory()
    {
    }

    public static AlarmCalculation createAlarmCalculation(AlarmCalculationStruct struct)
    {
        return new AlarmCalculationImpl(struct);
    }

    public static AlarmCalculationMutable createMutableAlarmCalculation(AlarmCalculationStruct struct)
    {
        return new AlarmCalculationImpl(struct);
    }

    public static AlarmCalculationMutable createMutableAlarmCalculation(AlarmCalculation calculation)
    {
        return (AlarmCalculationMutable)copyAlarmCalculation(calculation);
    }

    public static AlarmCalculationMutable createNewMutableAlarmCalculation()
    {
        return new AlarmCalculationImpl();
    }

    public static AlarmCalculation copyAlarmCalculation(AlarmCalculation calculation)
    {
        AlarmCalculationStruct calculationStruct = calculation.getStruct();
        AlarmCalculationStruct newCalculationStruct = new AlarmCalculationStruct();
        newCalculationStruct.calculationId = calculationStruct.calculationId;
        newCalculationStruct.name = calculationStruct.name;
        newCalculationStruct.contextType = calculationStruct.contextType;
        newCalculationStruct.expression = calculationStruct.expression;
        return new AlarmCalculationImpl(newCalculationStruct);
    }
}
