//
// ------------------------------------------------------------------------
// FILE: AlarmDefinitionServiceSimulatedPublisher.java
// 
// PACKAGE: com.cboe.presentation.alarms.events;
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.alarms.events;

import java.util.*;

import com.cboe.idl.alarm.AlarmConditionStruct;
import com.cboe.idl.alarm.AlarmDefinitionStruct;
import com.cboe.idl.alarm.AlarmCalculationStruct;
import com.cboe.idl.alarmEvents.AlarmDefinitionEventService;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.events.AlarmDefinitionConsumer;
import com.cboe.interfaces.events.AlarmDefinitionEventDelegateServiceConsumer;
import com.cboe.interfaces.instrumentation.alarms.AlarmActivation;

import com.cboe.util.ExceptionBuilder;

import com.cboe.presentation.api.InstrumentationTranslatorFactory;
import com.cboe.presentation.api.InstrumentationTranslatorImpl;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.uuidService.IdService;
import com.cboe.instrumentationCollector.alarms.validation.AlarmFieldValidatorFactory;
import com.cboe.instrumentationCollector.alarms.validation.AlarmLogicalValidatorFactory;

public class AlarmDefinitionServiceSimulatedPublisher
        implements AlarmDefinitionEventDelegateServiceConsumer
{
    private IdService idService;

    private Map<Integer, AlarmConditionStruct> conditionsById = new HashMap<Integer, AlarmConditionStruct>(101);
    private Map<Integer, AlarmCalculationStruct> calculationsById = new HashMap<Integer, AlarmCalculationStruct>(101);
    private Map<Integer, AlarmDefinitionStruct> definitionsById = new HashMap<Integer, AlarmDefinitionStruct>(101);
    private Map<Integer, Map<Integer, AlarmDefinitionStruct>> definitionsByCondition =
            new HashMap<Integer, Map<Integer, AlarmDefinitionStruct>>(101);

    private static final AlarmDefinitionStruct[] EMPTY_ALARM_DEFINITION_STRUCT = new AlarmDefinitionStruct[0];
    private static final AlarmConditionStruct[] EMPTY_ALARM_CONDITION_STRUCT = new AlarmConditionStruct[0];

    public AlarmDefinitionServiceSimulatedPublisher()
    {
        idService = FoundationFramework.getInstance().getIdService();
    }

    public void setAlarmDefinitionEventServiceDelegate(AlarmDefinitionEventService eventChannelDelegate)
    {
        //not used, required for interface
    }

    public void publishConditionById(long requestId, int conditionID)
    {
        AlarmConditionStruct struct = conditionsById.get(conditionID);
        if(struct != null)
        {
            AlarmConditionStruct[] structs = {struct};
            getAlarmDefinitionConsumer().acceptConditions(requestId, structs);
        }
        else
        {
            NotFoundException notFoundException =
                    ExceptionBuilder.notFoundException("Condition for Id:" + conditionID + ", does not exist.",
                                                       NotFoundCodes.RESOURCE_DOESNT_EXIST);
            getAlarmDefinitionConsumer().acceptNotFoundException(requestId, notFoundException.details);
        }
    }

    public void publishAllConditions(long requestId)
    {
        AlarmConditionStruct[] structs = EMPTY_ALARM_CONDITION_STRUCT;
        structs = conditionsById.values().toArray(structs);
        getAlarmDefinitionConsumer().acceptConditions(requestId, structs);
    }

    public void createCondition(long requestId, AlarmConditionStruct condition)
    {
        AlarmConditionStruct otherEqualCondition = searchForEqualCondition(condition);
        if(otherEqualCondition != null)
        {
            AlreadyExistsException exception =
                    ExceptionBuilder.alreadyExistsException("Matching condition already exists. conditionId:" +
                                                            otherEqualCondition.conditionId, 0);
            getAlarmDefinitionConsumer().acceptAlreadyExistsException(requestId, exception.details);
        }
        else
        {
            try
            {
                condition.conditionId = idService.getNextID();

                AlarmFieldValidatorFactory.validateAlarmCondition(condition);
                AlarmLogicalValidatorFactory.getInstance().validateAlarmCondition(condition);

                Integer conditionKey = condition.conditionId;

                conditionsById.put(conditionKey, condition);

                getAlarmDefinitionConsumer().acceptNewCondition(requestId, condition);
            }
            catch(NotFoundException e)
            {
                getAlarmDefinitionConsumer().acceptNotFoundException(requestId, e.details);
            }
            catch(SystemException e)
            {
                getAlarmDefinitionConsumer().acceptSystemException(requestId, e.details);
            }
            catch(DataValidationException e)
            {
                getAlarmDefinitionConsumer().acceptDataValidationException(requestId, e.details);
            }
        }
    }

    @SuppressWarnings({"OverlyNestedMethod"})
    public void updateCondition(long requestId, AlarmConditionStruct condition)
    {
        Integer conditionKey = condition.conditionId;

        AlarmConditionStruct struct = conditionsById.get(conditionKey);
        if(struct != null)
        {
            AlarmConditionStruct otherEqualCondition = searchForEqualCondition(condition);
            if(otherEqualCondition != null && condition.conditionId != otherEqualCondition.conditionId)
            {
                AlreadyExistsException exception =
                        ExceptionBuilder.alreadyExistsException("Matching condition already exists. conditionId:" +
                                                                otherEqualCondition.conditionId, 0);
                getAlarmDefinitionConsumer().acceptAlreadyExistsException(requestId, exception.details);
            }
            else
            {
                try
                {
                    AlarmFieldValidatorFactory.validateAlarmCondition(condition);
                    AlarmLogicalValidatorFactory.getInstance().validateAlarmCondition(condition);

                    conditionsById.remove(conditionKey);
                    conditionsById.put(conditionKey, condition);
                    getAlarmDefinitionConsumer().acceptChangedCondition(requestId, condition);

                    Map<Integer, AlarmDefinitionStruct> definitionsMap = definitionsByCondition.get(conditionKey);
                    if(definitionsMap != null)
                    {
                        for(AlarmDefinitionStruct definitionStruct : definitionsMap.values())
                        {
                            for(int i = 0; i < definitionStruct.conditions.length; i++)
                            {
                                AlarmConditionStruct alarmConditionStruct = definitionStruct.conditions[i];
                                if(alarmConditionStruct.conditionId == condition.conditionId)
                                {
                                    definitionStruct.conditions[i] = condition;
                                    getAlarmDefinitionConsumer().acceptChangedDefinition(requestId, definitionStruct);
                                    break;
                                }
                            }
                        }
                    }
                }
                catch(DataValidationException e)
                {
                    getAlarmDefinitionConsumer().acceptDataValidationException(requestId, e.details);
                }
            }
        }
        else
        {
            NotFoundException notFoundException =
                    ExceptionBuilder.notFoundException("Condition for Id:" + condition.conditionId + ", does not exist.",
                                                       NotFoundCodes.RESOURCE_DOESNT_EXIST);
            getAlarmDefinitionConsumer().acceptNotFoundException(requestId, notFoundException.details);
        }
    }

    @SuppressWarnings({"OverlyNestedMethod"})
    public void deleteCondition(long requestId, AlarmConditionStruct condition)
    {
        Integer conditionKey = condition.conditionId;

        AlarmConditionStruct struct = conditionsById.remove(conditionKey);
        if(struct != null)
        {
            Map<Integer, AlarmDefinitionStruct> definitionsMap = definitionsByCondition.get(conditionKey);
            if(definitionsMap != null)
            {
                for(AlarmDefinitionStruct definitionStruct : definitionsMap.values())
                {
                    for(int i = 0; i < definitionStruct.conditions.length; i++)
                    {
                        AlarmConditionStruct alarmConditionStruct = definitionStruct.conditions[i];
                        if(alarmConditionStruct.conditionId == condition.conditionId)
                        {
                            AlarmConditionStruct[] smallerConditionArray =
                                    new AlarmConditionStruct[definitionStruct.conditions.length - 1];
                            if(smallerConditionArray.length > 0)
                            {
                                System.arraycopy(definitionStruct.conditions, 0, smallerConditionArray, 0, i);
                                System.arraycopy(definitionStruct.conditions, i + 1, smallerConditionArray, i,
                                                 definitionStruct.conditions.length - i - 1);
                            }
                            definitionStruct.conditions = smallerConditionArray;

                            getAlarmDefinitionConsumer().acceptChangedDefinition(requestId, definitionStruct);
                            break;
                        }
                    }
                }
            }

            getAlarmDefinitionConsumer().acceptDeleteCondition(requestId, struct);
        }
        else
        {
            NotFoundException notFoundException =
                    ExceptionBuilder.notFoundException("Condition for Id:" + condition.conditionId +", does not exist.",
                                                       NotFoundCodes.RESOURCE_DOESNT_EXIST);
            getAlarmDefinitionConsumer().acceptNotFoundException(requestId, notFoundException.details);
        }
    }

    public void createCalculation(long requestId, AlarmCalculationStruct calculation)
    {
        AlarmCalculationStruct otherEqualCalculation = searchForEqualCalculation(calculation);
        if(otherEqualCalculation != null)
        {
            AlreadyExistsException exception =
                    ExceptionBuilder.alreadyExistsException("Matching calculation already exists. calculationId:" +
                                                            otherEqualCalculation.calculationId, 0);
            getAlarmDefinitionConsumer().acceptAlreadyExistsException(requestId, exception.details);
        }
        else
        {
            try
            {
                calculation.calculationId = idService.getNextID();

                AlarmFieldValidatorFactory.validateAlarmCalculation(calculation);
                AlarmLogicalValidatorFactory.getInstance().validateAlarmCalculation(calculation);

                Integer calculationKey = calculation.calculationId;

                calculationsById.put(calculationKey, calculation);

                getAlarmDefinitionConsumer().acceptNewCalculation(requestId, calculation);
            }
            catch(NotFoundException e)
            {
                getAlarmDefinitionConsumer().acceptNotFoundException(requestId, e.details);
            }
            catch(SystemException e)
            {
                getAlarmDefinitionConsumer().acceptSystemException(requestId, e.details);
            }
            catch(DataValidationException e)
            {
                getAlarmDefinitionConsumer().acceptDataValidationException(requestId, e.details);
            }
        }
    }

    public void publishAllCalculations(long requestId)
    {
        Collection<AlarmCalculationStruct> structCollection = calculationsById.values();
        AlarmCalculationStruct[] structs = new AlarmCalculationStruct[structCollection.size()];
        structs = structCollection.toArray(structs);
        getAlarmDefinitionConsumer().acceptCalculations(requestId, structs);
    }

    public void publishCalculationById(long requestId, int calculationId)
    {
        AlarmCalculationStruct struct = calculationsById.get(calculationId);
        if(struct != null)
        {
            AlarmCalculationStruct[] structs = {struct};
            getAlarmDefinitionConsumer().acceptCalculations(requestId, structs);
        }
        else
        {
            NotFoundException notFoundException =
                    ExceptionBuilder.notFoundException("Calculation for Id:" + calculationId + ", does not exist.",
                                                       NotFoundCodes.RESOURCE_DOESNT_EXIST);
            getAlarmDefinitionConsumer().acceptNotFoundException(requestId, notFoundException.details);
        }
    }

    public void updateCalculation(long requestId, AlarmCalculationStruct calculation)
    {
        Integer calculationKey = calculation.calculationId;

        AlarmCalculationStruct struct = calculationsById.get(calculationKey);
        if (struct == null)
        {
            NotFoundException notFoundException =
                    ExceptionBuilder.notFoundException("Calculation for Id:" + calculation.calculationId + ", does not exist.",
                                                       NotFoundCodes.RESOURCE_DOESNT_EXIST);
            getAlarmDefinitionConsumer().acceptNotFoundException(requestId, notFoundException.details);
        }
        else
        {
            AlarmCalculationStruct otherEqualCalculation = searchForEqualCalculation(calculation);
            if(otherEqualCalculation != null && calculation.calculationId != otherEqualCalculation.calculationId)
            {
                AlreadyExistsException exception =
                        ExceptionBuilder.alreadyExistsException("Matching calculation already exists. calculationId:" +
                                                                otherEqualCalculation.calculationId, 0);
                getAlarmDefinitionConsumer().acceptAlreadyExistsException(requestId, exception.details);
            }
            else
            {
                try
                {
                    AlarmFieldValidatorFactory.validateAlarmCalculation(calculation);
                    AlarmLogicalValidatorFactory.getInstance().validateAlarmCalculation(calculation);

                    calculationsById.put(calculationKey, calculation);
                    getAlarmDefinitionConsumer().acceptChangedCalculation(requestId, calculation);

                    //TODO Fire Condition changed for conditions that contain the updated calculation
                }
                catch(DataValidationException e)
                {
                    getAlarmDefinitionConsumer().acceptDataValidationException(requestId, e.details);
                }
            }
        }
    }

    public void deleteCalculation(long requestId, AlarmCalculationStruct calculation)
    {
        Integer calculationKey = calculation.calculationId;

        AlarmCalculationStruct struct = calculationsById.remove(calculationKey);
        if(struct != null)
        {
            //TODO Check for Conditions using the deleted calculation and notify user

            getAlarmDefinitionConsumer().acceptDeleteCalculation(requestId, struct);
        }
        else
        {
            NotFoundException notFoundException =
                    ExceptionBuilder.notFoundException("Calculation for Id:" + calculation.calculationId +", does not exist.",
                                                       NotFoundCodes.RESOURCE_DOESNT_EXIST);
            getAlarmDefinitionConsumer().acceptNotFoundException(requestId, notFoundException.details);
        }
    }

    public void publishDefinitionById(long requestId, int definitionId)
    {
        AlarmDefinitionStruct struct = definitionsById.get(definitionId);
        if(struct != null)
        {
            AlarmDefinitionStruct[] structs = {struct};
            getAlarmDefinitionConsumer().acceptDefinitions(requestId, structs);
        }
        else
        {
            NotFoundException notFoundException =
                    ExceptionBuilder.notFoundException("Definition for Id:" + definitionId + ", does not exist.",
                            NotFoundCodes.RESOURCE_DOESNT_EXIST);
            getAlarmDefinitionConsumer().acceptNotFoundException(requestId, notFoundException.details);
        }
    }

    public void publishAllDefinitions(long requestId)
    {
        AlarmDefinitionStruct[] structs = EMPTY_ALARM_DEFINITION_STRUCT;
        structs = definitionsById.values().toArray(structs);
        getAlarmDefinitionConsumer().acceptDefinitions(requestId, structs);
    }

    @SuppressWarnings({"OverlyLongMethod", "MethodWithMultipleReturnPoints"})
    public void createDefinition(long requestId, AlarmDefinitionStruct definition)
    {
        AlarmConditionStruct[] conditions = definition.conditions;
        BitSet dupeConditionCheckBitset = new BitSet(conditions.length);

        for(AlarmConditionStruct condition : conditions)
        {
            Integer conditionKey = condition.conditionId;
            AlarmConditionStruct conditionStruct = conditionsById.get(conditionKey);
            if(conditionStruct == null)
            {
                NotFoundException notFoundException =
                        ExceptionBuilder.notFoundException("Condition for Id:" + condition.conditionId +
                                                           ", does not exist.",
                                                           NotFoundCodes.RESOURCE_DOESNT_EXIST);
                getAlarmDefinitionConsumer().acceptNotFoundException(requestId, notFoundException.details);
                return;
            }

            boolean alreadyChecked = dupeConditionCheckBitset.get(conditionKey);
            //noinspection IfStatementWithNegatedCondition
            if(!alreadyChecked)
            {
                dupeConditionCheckBitset.set(conditionKey);
            }
            else
            {
                DataValidationException exception =
                        ExceptionBuilder.dataValidationException("Condition for Id:" + condition.conditionId +
                                                                 ", was specified more than once.", 0);
                getAlarmDefinitionConsumer().acceptDataValidationException(requestId, exception.details);
                return;
            }
        }

        AlarmDefinitionStruct otherEqualDefinition = searchForEqualDefinition(definition);
        if(otherEqualDefinition != null)
        {
            AlreadyExistsException exception =
                    ExceptionBuilder.alreadyExistsException("Matching definition already exists. definitionId:" +
                                                            otherEqualDefinition.definitionId, 0);
            getAlarmDefinitionConsumer().acceptAlreadyExistsException(requestId, exception.details);
        }
        else
        {
            try
            {
                definition.definitionId = idService.getNextID();

                AlarmLogicalValidatorFactory.getInstance().validateAlarmDefinition(definition);

                Integer definitionKey = definition.definitionId;

                definitionsById.put(definitionKey, definition);

                for(AlarmConditionStruct condition : conditions)
                {
                    Integer conditionKey = condition.conditionId;
                    Map<Integer, AlarmDefinitionStruct> definitionMap = definitionsByCondition.get(conditionKey);
                    if(definitionMap == null)
                    {
                        definitionMap = new HashMap<Integer, AlarmDefinitionStruct>(101);
                    }
                    definitionMap.put(definitionKey, definition);
                    definitionsByCondition.put(conditionKey, definitionMap);
                }

                getAlarmDefinitionConsumer().acceptNewDefinition(requestId, definition);
            }
            catch(NotFoundException e)
            {
                getAlarmDefinitionConsumer().acceptNotFoundException(requestId, e.details);
            }
            catch(SystemException e)
            {
                getAlarmDefinitionConsumer().acceptSystemException(requestId, e.details);
            }
            catch(DataValidationException e)
            {
                getAlarmDefinitionConsumer().acceptDataValidationException(requestId, e.details);
            }
        }
    }

    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod", "MethodWithMultipleReturnPoints", "OverlyNestedMethod"})
    public void updateDefinition(long requestId, AlarmDefinitionStruct definition)
    {
        Integer definitionKey = definition.definitionId;

        AlarmDefinitionStruct struct = definitionsById.get(definitionKey);
        if(struct != null)
        {
            AlarmConditionStruct[] conditions = definition.conditions;
            BitSet dupeConditionCheckBitset = new BitSet(conditions.length);

            for(AlarmConditionStruct condition : conditions)
            {
                Integer conditionKey = condition.conditionId;
                AlarmConditionStruct conditionStruct = conditionsById.get(conditionKey);
                if(conditionStruct == null)
                {
                    NotFoundException notFoundException =
                            ExceptionBuilder.notFoundException("Condition for Id:" + condition.conditionId +
                                                               ", does not exist.",
                                                               NotFoundCodes.RESOURCE_DOESNT_EXIST);
                    getAlarmDefinitionConsumer().acceptNotFoundException(requestId, notFoundException.details);
                    return;
                }

                boolean alreadyChecked = dupeConditionCheckBitset.get(conditionKey);
                //noinspection IfStatementWithNegatedCondition
                if(!alreadyChecked)
                {
                    dupeConditionCheckBitset.set(conditionKey);
                }
                else
                {
                    DataValidationException exception =
                            ExceptionBuilder.dataValidationException("Condition for Id:" + condition.conditionId +
                                                                     ", was specified more than once.", 0);
                    getAlarmDefinitionConsumer().acceptDataValidationException(requestId, exception.details);
                    return;
                }
            }

            AlarmDefinitionStruct otherEqualDefinition = searchForEqualDefinition(definition);
            if(otherEqualDefinition != null && definition.definitionId != otherEqualDefinition.definitionId)
            {
                AlreadyExistsException exception =
                        ExceptionBuilder.alreadyExistsException("Matching definition already exists. definitionId:" +
                                                                otherEqualDefinition.definitionId, 0);
                getAlarmDefinitionConsumer().acceptAlreadyExistsException(requestId, exception.details);
            }
            else
            {
                try
                {
                    AlarmLogicalValidatorFactory.getInstance().validateAlarmDefinition(definition);

                    definitionsById.remove(definitionKey);
                    definitionsById.put(definitionKey, definition);


                    conditions = struct.conditions;
                    for(AlarmConditionStruct condition : conditions)
                    {
                        Integer conditionKey = condition.conditionId;
                        Map<Integer, AlarmDefinitionStruct> definitionMap = definitionsByCondition.get(conditionKey);
                        if(definitionMap != null)
                        {
                            definitionMap.remove(definitionKey);
                            if(definitionMap.isEmpty())
                            {
                                definitionsByCondition.remove(conditionKey);
                            }
                            else
                            {
                                definitionsByCondition.put(conditionKey, definitionMap);
                            }
                        }
                    }


                    conditions = definition.conditions;
                    for(AlarmConditionStruct condition : conditions)
                    {
                        Integer conditionKey = condition.conditionId;
                        Map<Integer, AlarmDefinitionStruct> definitionMap = definitionsByCondition.get(conditionKey);
                        if(definitionMap == null)
                        {
                            definitionMap = new HashMap<Integer, AlarmDefinitionStruct>(101);
                        }
                        definitionMap.put(definitionKey, definition);
                        definitionsByCondition.put(conditionKey, definitionMap);
                    }


                    getAlarmDefinitionConsumer().acceptChangedDefinition(requestId, definition);
                }
                catch(DataValidationException e)
                {
                    getAlarmDefinitionConsumer().acceptDataValidationException(requestId, e.details);
                }
            }
        }
        else
        {
            NotFoundException notFoundException =
                    ExceptionBuilder.notFoundException("Definition for Id:" + definition.definitionId +
                                                       ", does not exist.",
                                                       NotFoundCodes.RESOURCE_DOESNT_EXIST);
            getAlarmDefinitionConsumer().acceptNotFoundException(requestId, notFoundException.details);
        }
    }

    @SuppressWarnings({"MethodWithMultipleReturnPoints"})
    public void deleteDefinition(long requestId, AlarmDefinitionStruct definition)
    {
        Integer definitionKey = definition.definitionId;

        AlarmDefinitionStruct struct = definitionsById.get(definitionKey);
        if(struct != null)
        {
            AlarmActivation[] foundActivations =
                    InstrumentationTranslatorFactory.find().
                    getAlarmActivationsForDefinitionId(definitionKey);
            if(foundActivations != null && foundActivations.length > 0)
            {
                DataValidationException exception =
                        ExceptionBuilder.dataValidationException("Definition for Id:" + definition.definitionId +
                                                                 ", has activations. Activations must be deleted first.",
                                                                 0);
                getAlarmDefinitionConsumer().acceptDataValidationException(requestId, exception.details);
                return;
            }


            AlarmConditionStruct[] conditions = struct.conditions;
            for(AlarmConditionStruct condition : conditions)
            {
                Integer conditionKey = condition.conditionId;
                Map<Integer, AlarmDefinitionStruct> definitionMap = definitionsByCondition.get(conditionKey);
                if(definitionMap != null)
                {
                    definitionMap.remove(definitionKey);
                    if(definitionMap.isEmpty())
                    {
                        definitionsByCondition.remove(conditionKey);
                    }
                    else
                    {
                        definitionsByCondition.put(conditionKey, definitionMap);
                    }
                }
            }


            definitionsById.remove(definitionKey);
            getAlarmDefinitionConsumer().acceptDeleteDefinition(requestId, struct);
        }
        else
        {
            NotFoundException notFoundException =
                    ExceptionBuilder.notFoundException("Definition for Id:" + definition.definitionId +
                                                       ", does not exist.",
                                                       NotFoundCodes.RESOURCE_DOESNT_EXIST);
            getAlarmDefinitionConsumer().acceptNotFoundException(requestId, notFoundException.details);
        }
    }

    private AlarmDefinitionConsumer getAlarmDefinitionConsumer()
    {
        return ((InstrumentationTranslatorImpl) InstrumentationTranslatorFactory.find()).
                getAlarmConsumersHome().getAlarmDefinitionConsumer();
    }

    private AlarmConditionStruct searchForEqualCondition(AlarmConditionStruct condition)
    {
        AlarmConditionStruct foundCondition = null;

        for(AlarmConditionStruct alarmConditionStruct : conditionsById.values())
        {
            if(isConditionsEqual(condition, alarmConditionStruct))
            {
                foundCondition = alarmConditionStruct;
                break;
            }
        }

        return foundCondition;
    }

    private boolean isConditionsEqual(AlarmConditionStruct condition1, AlarmConditionStruct condition2)
    {
        boolean isEqual;

        isEqual = (condition1.conditionType == condition2.conditionType &&
                   condition1.contextName.equals(condition2.contextName) &&
                   condition1.contextType.equals(condition2.contextType) &&
                   condition1.fieldName.equals(condition2.fieldName) &&
                   condition1.fieldType.equals(condition2.fieldType) &&
                   condition1.operator.equals(condition2.operator) &&
                   condition1.subjectName.equals(condition2.subjectName) &&
                   condition1.threshold.equals(condition2.threshold));

        return isEqual;
    }

    private AlarmCalculationStruct searchForEqualCalculation(AlarmCalculationStruct calculation)
    {
        AlarmCalculationStruct foundCalculation = null;

        for(AlarmCalculationStruct alarmCalculationStruct : calculationsById.values())
        {
            if(isCalculationsEqual(calculation, alarmCalculationStruct))
            {
                foundCalculation = alarmCalculationStruct;
                break;
            }
        }

        return foundCalculation;
    }

    private boolean isCalculationsEqual(AlarmCalculationStruct calculation1, AlarmCalculationStruct calculation2)
    {
        return (calculation1.contextType.equals(calculation2.contextType) &&
                calculation1.expression.equals(calculation2.expression));
    }

    private AlarmDefinitionStruct searchForEqualDefinition(AlarmDefinitionStruct definition)
    {
        AlarmDefinitionStruct foundDefinition = null;

        for(AlarmDefinitionStruct alarmDefinitionStruct : definitionsById.values())
        {
            if(isDefinitionsEqual(definition, alarmDefinitionStruct))
            {
                foundDefinition = alarmDefinitionStruct;
                break;
            }
        }

        return foundDefinition;
    }

    private boolean isDefinitionsEqual(AlarmDefinitionStruct definition1, AlarmDefinitionStruct definition2)
    {
        boolean isEqual = false;

        AlarmConditionStruct[] def1Conditions = definition1.conditions;
        AlarmConditionStruct[] def2Conditions = definition2.conditions;

        if(def1Conditions.length == def2Conditions.length)
        {
            boolean arraysEqual = true;
            for(AlarmConditionStruct def1Condition : def1Conditions)
            {
                boolean foundMatch = false;
                for(AlarmConditionStruct def2Condition : def2Conditions)
                {
                    if(def1Condition.conditionId == def2Condition.conditionId)
                    {
                        foundMatch = true;
                        break;
                    }
                }
                if(!foundMatch)
                {
                    arraysEqual = false;
                    break;
                }
            }

            isEqual = arraysEqual;
        }

        if(isEqual)
        {
            isEqual = definition1.severity == definition2.severity;
        }

        return isEqual;
    }
}
