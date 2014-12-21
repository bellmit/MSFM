//
// -----------------------------------------------------------------------------------
// Source file: ManualFillElement.java
//
// PACKAGE: com.cboe.interfaces.presentation.omt;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.omt;


/**
 * Interface for access to various parts of manual fill table entry
 */

public interface ManualFillElement
{
    String getClearingFirm();
    void setClearingFirm(String clearingFirm);
    String getContraBroker();
    void setContraBroker(String contraBroker);
    Integer getIndex();
    void setIndex(Integer index);
    int getVolume();
    void setVolume(int volume);
    boolean isEmpty();
    boolean isComplete();
    void clear();
}
