package com.cboe.directoryService.persist; 
    
/*
     * Wrapper class for Large Strings 
     * to be Serialized 
     */
     public class StringWrapper implements java.io.Serializable {

         protected String message;

         public StringWrapper(String mes){
             message = mes;
         }

         public StringWrapper(){
               message = new String("");
         }
         public String getString(){
             return message;
         }
         
         public void setString(String mes) {
             message = mes; 
         }
     }