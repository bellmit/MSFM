package com.cboe.infrastructureServices.loggingService;

import java.util.Calendar;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Serializable;
/**
   Encapsulates the information associated with a logging parameter that may be appended to a log message to provide context or supporting information.
 */
public class MsgParameter  implements Serializable {
    /**
       The name of the parameter.
     */
    String name;
    /**
       The value of the parameter.
     */
    String value;
    /**
       The serialization version of this class.
     */
    static final long serialVersionUID = -8201039899806160974L;
    
    /**
       Create a new instance of a parameter.
       @param name The string name of the parameter.
       @param value The string value of the parameter.
       @roseuid 365338E00006
     */
    public MsgParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    /**
       @return The name of the parameter.
       @roseuid 365338F900DF
     */
    public String getName() {
        return name;
    }
    
    /**
       @return The value of the parameter.
       @roseuid 36533901005E
     */
    public String getValue() {
        return value;
    }
    
    /**
       @return A string representation of the state of this instance of a parameter.
       @roseuid 365C78B6017C
     */
    public String toString() {
        return name + "=" + value;
    }
    
    /**
       @param calendar The calendar instance to represent as parameters.
       @return An array of parameters representing the date and time of the given calendar instance.
       @roseuid 366705CF03AA
     */
    public static MsgParameter[] fromCalendar(Calendar calendar) {
        String minuteString = Integer.toString( calendar.get( Calendar.MINUTE ) );
        if( minuteString.length() == 1 ) {
            minuteString = "0" + minuteString;
        }

        // Pad the second string if necessary.
        String secondString = Integer.toString( calendar.get( Calendar.SECOND ) );
        if( secondString.length() == 1 ) {
            secondString = "0" + secondString;
        }

        // Pad the hundredth of second string if necessary.
        String hundredthOfSecondString
            = Integer.toString( calendar.get( Calendar.MILLISECOND ) / 10 );
        if( hundredthOfSecondString.length() == 1 ) {
            hundredthOfSecondString = "0" + hundredthOfSecondString;
        }

        MsgParameter[] parameters = {
            new MsgParameter( "year", Integer.toString( calendar.get( Calendar.YEAR ) ) ),
            new MsgParameter( "month", Integer.toString( calendar.get( Calendar.MONTH ) + 1 ) ),
            new MsgParameter( "day", Integer.toString( calendar.get( Calendar.DAY_OF_MONTH ) ) ),
            new MsgParameter( "hour", Integer.toString( calendar.get( Calendar.HOUR_OF_DAY ) ) ),
            new MsgParameter( "minute", minuteString ),
            new MsgParameter( "second", secondString ),
            new MsgParameter( "hundredthOfSecond", hundredthOfSecondString )
        };

        return parameters;
    }
    
    /**
       @param exception The exception instance to represent as parameters.
       @return An array of parameters representing the contents of the given exception.
       @roseuid 36680008028F
     */
    public static MsgParameter[] fromException(Throwable exception) {
        StringWriter stackTraceStringWriter = new StringWriter();
        exception.printStackTrace( new PrintWriter( stackTraceStringWriter ) );
        MsgParameter[] parameters = {
            new MsgParameter( "exceptionType", exception.getClass().getName() ),
            new MsgParameter( "exceptionMessage", exception.getMessage() ),
            new MsgParameter( "exceptionStackTrace", stackTraceStringWriter.toString() )
        };
        return parameters;
    }
    
    /**
       @return True if the names and values of the parameters are the same.
       @roseuid 37445CFC03CC
     */
    public boolean equals(MsgParameter otherParameter) {
        return ( name.equals( otherParameter.getName() ) ) &&
               ( value.equals( otherParameter.getValue() ) );
    }


}
