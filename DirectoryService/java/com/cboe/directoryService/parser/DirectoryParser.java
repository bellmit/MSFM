package com.cboe.directoryService.parser;

import java.io.ByteArrayInputStream;

public class DirectoryParser extends DirectoryServiceParser {
    private String myConstraint;
  
    public DirectoryParser(String inputConstraint) {
	super(new ByteArrayInputStream(inputConstraint.getBytes()));
	myConstraint = inputConstraint;
    }

    
    public void setConstraint(String aString) {
	myConstraint = aString;
    }
    public String getConstraint() {
	return myConstraint;
    }
    
  
}





