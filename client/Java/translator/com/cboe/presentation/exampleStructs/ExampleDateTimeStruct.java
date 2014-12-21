package com.cboe.presentation.exampleStructs;

/**
 * This type was created in VisualAge.
 * @author Nick DePasquale
 */
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiOrder.*;
 
public class ExampleDateTimeStruct {
/**
 * ExampleDateTimeStruct constructor comment.
 */
public ExampleDateTimeStruct() {
    super();
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 * 
 * @return com.cboe.interfaces.cmiUtil.DateTimeStruct
 */
public static DateTimeStruct getExampleDateTimeStructJan11999() {
DateTimeStruct aDateTimeStruct;

DateStruct date = ExampleDateStruct.getExampleDateStructJan11999();
TimeStruct time = ExampleTimeStruct.getExampleTimeStruct01010101();

    aDateTimeStruct = new DateTimeStruct(date,time);
        
    return aDateTimeStruct;
}
}
