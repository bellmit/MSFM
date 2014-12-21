package com.cboe.directoryService.parser;

public class DSCannotConvertToBooleanException extends Exception {


  public DSCannotConvertToBooleanException() {
  }

  public DSCannotConvertToBooleanException(String operName) {
    super(operName);
  }


};
