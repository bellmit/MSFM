package com.cboe.domain.routingProperty.validation;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.*;

import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.MutableBasePropertyKey;

import com.cboe.domain.routingProperty.key.AbstractBasePropertyKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

// -----------------------------------------------------------------------------------
// Source file: Validator
//
// PACKAGE: com.cboe.internalPresentation.routingProperties.validation
// 
// Created: Mar 17, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public class ValidateDefaultKeyCombination extends AbstracBasePropertyGroupValidator
{
    public ValidateDefaultKeyCombination()
    {
    }

    public boolean isValid(BasePropertyGroup basePropertyGroup, StringBuffer validationReport)
    {
        BasePropertyType       basePropertyType       = basePropertyGroup.getType();
        MutableBasePropertyKey mutableBasePropertyKey = (MutableBasePropertyKey) basePropertyGroup.getPropertyKey();

        boolean isValid = true;
        int[][] masks   = basePropertyType.getMasks();

        boolean isAnyDefaultsSet = false;
        Map<String, Boolean> isFieldDefaultedMap = new HashMap<String, Boolean>(5);
        if(masks.length > 0 && masks[0].length > 0)
        {
            // check to see if the combination of default key components, if any, matches at least one mask
            try
            {
                for (PropertyDescriptor propertyDescriptor : mutableBasePropertyKey.getPropertyDescriptors())
                {
                    boolean isDefaulted = mutableBasePropertyKey.isFieldDefaultValue(propertyDescriptor);
                    if(isDefaulted)
                    {
                        isAnyDefaultsSet = true;
                    }
                    isFieldDefaultedMap.put(propertyDescriptor.getName(), isDefaulted);
                }
            }
            catch(IntrospectionException e)
            {
                Log.exception("Can't validate default key combination due to exception", e);
            }

            if(isAnyDefaultsSet)
            {
                int  maskSize      = masks[0].length;
                long maskToLookFor = 0;
               // boolean[] isDefaultSet = new boolean[maskSize];  // for debugging only

                // StringBuffer maskBitsForDebug = new StringBuffer();
                for(int index = 0; index < maskSize; ++index)
                {
                    String  keyCompName = ((AbstractBasePropertyKey) mutableBasePropertyKey).getKeyComponentName(index);
                    Boolean keyCompDefaultSetting  = isFieldDefaultedMap.get(keyCompName);
                    boolean isCompAtIndexDefaulted = keyCompDefaultSetting == null ? false : keyCompDefaultSetting;
                    if (isCompAtIndexDefaulted)
                    {
                        maskToLookFor |= 0x1 << index;
                        // maskBitsForDebug.append("1");
                    }
                    else
                    {
                        // maskBitsForDebug.append("0");
                    }
                    // isDefaultSet[index] = keyCompDefaultSetting == null ? false : keyCompDefaultSetting;  // for debugging
                }
                // System.out.println("ValidateDefaultKeyCombination.isValid: maskToLookFor=[" + maskBitsForDebug + "]");

                // there must be a mask that has a 1 bit at every index where isDefaultSet is true, otherwise the
                // combination of defaults is invalid

                isValid = false;
                for(int maskNumber = 0; maskNumber < masks.length; ++maskNumber)
                {
                    // maskBitsForDebug = new StringBuffer();

                    long maskToCheck = 0;
                    for(int index = 0; index < maskSize; ++index)
                    {
                        if (masks[maskNumber][index] == 1)
                        {
                            maskToCheck |= 0x1 << index;
                            // maskBitsForDebug.append("1");
                        }
                        else
                        {
                            // maskBitsForDebug.append("0");
                        }
                    }
                    // System.out.println("ValidateDefaultKeyCombination.isValid: maskToCheck #" + maskNumber + "=[" + maskBitsForDebug + "]");
                    if (maskToCheck == maskToLookFor)
                    {
                        // System.out.println("ValidateDefaultKeyCombination.isValid: found a match at " + maskNumber);
                        isValid = true;
                        break;
                    }
                }
                // if (! isValid) System.out.println("ValidateDefaultKeyCombination.isValid: did not find a match");

                // for debugging only
                // if (isValid)
                // {
                //     System.out.print(",  potentialMaskNumbers=");
                //     for (Integer x : potentialMaskNumbers)
                //     {
                //         System.out.print(x + ", ");
                //     }
                // }
                // System.out.println("");

                if(!isValid)
                {
                    StringBuffer msg = new StringBuffer(1000);
                    for(int mask[] : masks)
                    {
                        Set<String> validCombinations = new HashSet<String>(3);
                        for(int index = 0; index < maskSize && index < mask.length; ++index)
                        {
                            if(mask[index] == 1)
                            {
                                String keyCompName = ((AbstractBasePropertyKey) mutableBasePropertyKey).getKeyComponentName(index);
                                validCombinations.add(keyCompName);
                            }
                        }
                        if(validCombinations.size() > 0)
                        {
                            msg.append(" Valid combinations are:\n");
                            StringBuffer subMsg = new StringBuffer(100);
                            for (String keyComp : validCombinations)
                            {
                                subMsg.append(keyComp).append("   ");
                            }
                            if(msg.length() > 0)
                            {
                                msg.append("OR   ");
                            }
                            else
                            {
                                msg.append("        ");
                            }
                            if(validCombinations.size() > 1)
                            {
                                msg.append("All of these:  ");
                            }
                            msg.append(subMsg).append("\n");
                        }
                        else
                        {
                            msg.append("\nNone of the key components can be defaulted");
                        }
                    } // end for
                    
                    if(msg.length() > 0)   // it better be > 0 !!
                    {
                        validationReport.append("Invalid combination of Default keys.").append(msg).append('\n');
                    }
                } // end if !isValid

                // for debugging only
                // System.out.println("ValidateDefaultKeyCombination.isValid: " +  createDebugAnalysisString(masks, isDefaultSet));
            } // end if isAnyDefaultsSet
        } // end if masks.length

        return isValid;
    }

//    // for debugging only
//    private String createDebugAnalysisString(int[][] masks, boolean[] isDefaultSet)
//    {
//        StringBuffer str = new StringBuffer(1000);
//
//        int maskSize = masks.length > 0 ? masks[0].length : 0;
//        int maskCnt  = 0;
//
//        for(int mask[] : masks)
//        {
//            str.append("for mask number " + maskCnt + " ... ");
//            for(int index = 0; index < maskSize && index < mask.length; ++index)
//            {
//                str.append("keyCompName=[").append(((AbstractBasePropertyKey) mutableBasePropertyKey).getKeyComponentName(index)).append("] ... ");
//                if(mask[index] == 0)
//                {
//                    str.append("is not defaultable ");
//                    if(isDefaultSet[index])
//                    {
//                        str.append("but default is set\n");
//                        break;
//                    }
//                    else
//                    {
//                        str.append("and default is not set\n");
//                    }
//                }
//                else
//                {
//                    str.append("is defaultable ");
//                    if(isDefaultSet[index])
//                    {
//                        str.append("and default is set\n");
//                    }
//                    else
//                    {
//                        str.append("but default is not set\n");
//                    }
//                }
//            } // end for index
//            ++maskCnt;
//        } // end for masks
//
//        return str.toString();
//    }
}
