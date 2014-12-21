package com.cboe.presentation.exampleStructs;

/**
 * This type was created in VisualAge.
 * @author Nick DePasquale
 */
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiOrder.*;
import java.util.*;
import com.cboe.domain.util.DateWrapper;
import java.text.*;

public class ExampleTimeStruct {
/**
 * ExampleDateTimeStruct constructor comment.
 */
public ExampleTimeStruct() {
    super();
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 *
 * @return TimeStruct
 */
public static TimeStruct getExampleCurrentTimeStruct() {
TimeStruct time = null;
long millis;
DateWrapper aDateWrapper;

    millis = System.currentTimeMillis();

    aDateWrapper = new DateWrapper(millis);

    time = aDateWrapper.toTimeStruct();

    return time;
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 *
 * @return TimeStruct
 */
public static TimeStruct getExampleTimeStruct(String str) {
int x = 0;
byte value[] = {0,0,0,0};
Byte aByte;

    // str in hh:mm:ss:ff format
    java.util.StringTokenizer aStringTokenizer;
    aStringTokenizer = new StringTokenizer(str,":");


    while(aStringTokenizer.hasMoreTokens())
    {
        aByte = new Byte(aStringTokenizer.nextToken());
        value[x] = aByte.byteValue();
        x++;
    }

    TimeStruct time = new TimeStruct(value[0],value[1],value[2],value[3]);


    /*
    DateWrapper aDateWrapper ;

    aDateWrapper = new DateWrapper(time);
    SimpleDateFormat aSimpleDateFormat;
    aSimpleDateFormat = new SimpleDateFormat("hh:mm:ss:SS");

    System.out.println(aSimpleDateFormat.format(aDateWrapper.getDate()));

    */
    return time;
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 *
 * @return com.cboe.interfaces.cmiUtil.DateTimeStruct
 */
public static TimeStruct getExampleTimeStruct01010101() {

TimeStruct time = new TimeStruct((byte)1,(byte)1,(byte)1,(byte)1);

    return time;
}
}
