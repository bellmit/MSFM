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
import com.cboe.util.*;
import com.cboe.interfaces.domain.Price;

import com.cboe.presentation.common.formatters.DisplayPriceFactory;

public class ExamplePriceStruct {
/**
 * ExamplePriceStruct constructor comment.
 */
public ExamplePriceStruct() {
    super();
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 *
 * @return com.cboe.interfaces.cmiUtil.PriceStruct
 */
public static PriceStruct getExamplePriceStruct() {
PriceStruct aPriceStruct;
Price aPrice;

    aPrice = DisplayPriceFactory.create(100.0);

    aPriceStruct = aPrice.toStruct();

    return aPriceStruct;
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 *
 * @return com.cboe.interfaces.cmiUtil.PriceStruct
 */
public static PriceStruct getExamplePriceStruct(double price) {
PriceStruct aPriceStruct;
Price aPrice;

    aPrice = DisplayPriceFactory.create(price);

    aPriceStruct = aPrice.toStruct();

    return aPriceStruct;
}
/**
 * This method was created in VisualAge.
 * @author Nick DePasquale
 *
 * @return com.cboe.interfaces.cmiUtil.PriceStruct
 */
public static PriceStruct getExamplePriceStruct100() {
PriceStruct aPriceStruct;
Price aPrice;

    aPrice = DisplayPriceFactory.create(100.0);

    aPriceStruct = aPrice.toStruct();

    return aPriceStruct;
}
}
