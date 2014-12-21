package com.cboe.directoryService.example;


import  com.cboe.directoryService.parser.*;
import java.io.*;


public class DParseTest {
    public static void main(String args[]) {
    try {
        // Various tests follow:
	    // String inputString = "aString in channelid";
	    String inputString = " (supervisor < frank) and (aString == channelid  or eventname == 6)";
	    //String inputString = "objectclass==bob";
	    //String inputString = "objectclass<= bob";
	    //String inputString = "exist svc";
	    long initTime = System.currentTimeMillis();
	    DirectoryParser parser = new DirectoryParser(inputString);
	    ASTconstraint n = parser.constraint();
	    long endTime = System.currentTimeMillis();

	    System.out.println("Time for Tree Construction " + (endTime - initTime));

        long initEvalTime = System.currentTimeMillis();
	    System.out.println("inputString = " +inputString);
	    DirectoryConstraintNodeVisitor visitor = new DirectoryConstraintNodeVisitor();
	    String val = (String)n.jjtAccept(visitor, null);
	    System.out.println("New LDAP Constraint = " + val);
	    long endEvalTime = System.currentTimeMillis();
	    System.out.println("Time for Constraint Eval " +(endEvalTime - initEvalTime));
	   
	
	    System.out.println("Thank you.");
	} catch (Exception e) {
	    System.out.println("Oops.");
	    System.out.println(e.getMessage());
	e.printStackTrace();
	}
	
	 
    }
    
  
}
