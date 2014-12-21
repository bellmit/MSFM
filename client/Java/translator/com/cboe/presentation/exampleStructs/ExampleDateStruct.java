package com.cboe.presentation.exampleStructs;

/**
 * This type was created in VisualAge.
 * @author Nick DePasquale
 */
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiOrder.*;
 
public class ExampleDateStruct {
/**
 * ExampleDateTimeStruct constructor comment.
 */
public ExampleDateStruct() {
    super();
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 * 
 * @return com.cboe.interfaces.cmiUtil.DateTimeStruct
 */
public static DateStruct getExampleDateStructJan11999() {
DateStruct date = new DateStruct((byte)1,(byte)1,(short)1999);
        
    return date;
}
}
