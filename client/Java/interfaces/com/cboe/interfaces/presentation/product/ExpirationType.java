//
// -----------------------------------------------------------------------------------
// Source file: ExpirationType.java
//
// PACKAGE: com.cboe.presentation.product
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ExpirationType
{
    STANDARD("Standard", 'S'),
    WEEKLY("Weekly", 'W'),
    QUATERLY("Quarterly",'Q'),
    MONTHLY("Monthly",'M'),
    CUSTOM("Custom",'C'),
    LEAP("Leap",'L');

    private String mExperationString;
    private char mExperationChar;

    private ExpirationType(String pExperationString, char pExperationChar){
        mExperationString = pExperationString;
        mExperationChar = pExperationChar;
    }

    public String toString(){
        return mExperationString;
    }

    public char toChar(){
        return mExperationChar;
    }

    public static ExpirationType findExperationType(char pExperationType){
        ExpirationType[] expTypes = ExpirationType.values();
        ExpirationType resultType = ExpirationType.CUSTOM;
        for(ExpirationType expType: expTypes){
            if(pExperationType == expType.toChar()){
                resultType = expType;
            }
        }
        return resultType;
    }
}
