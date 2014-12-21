package com.cboe.presentation.exampleStructs;

/**
 * This type was created in VisualAge.
 * @author Nick DePasquale
 */
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.domain.util.*;
  
public class ExampleOrderContingencyStruct {
/**
 * ExampleContingencyStruct constructor comment.
 */
public ExampleOrderContingencyStruct() {
    super();
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 * 
 * @return ContingencyStruct
 */
public static OrderContingencyStruct getExampleOrderContingencyAON() {
OrderContingencyStruct anOrderContingencyStruct;

    anOrderContingencyStruct = new OrderContingencyStruct(ContingencyTypes.AON,ExamplePriceStruct.getExamplePriceStruct(0.0),0);
    
    return anOrderContingencyStruct;
}
}
