package com.cboe.directoryService.parser;

public class DSOperandTypesNotSupportedException extends Exception {


  public DSOperandTypesNotSupportedException() {
  }

  public DSOperandTypesNotSupportedException(String operName) {
    super(operName);
  }


};
