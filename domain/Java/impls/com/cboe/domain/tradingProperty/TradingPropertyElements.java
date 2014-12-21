//
// -----------------------------------------------------------------------------------
// Source file: TradingPropertyElements.java
//
// PACKAGE: com.cboe.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import java.util.*;

import com.cboe.domain.property.BasicPropertyParser;

/**
 * Provides a container for each element of a potential Trading Property as defined by the persistence.
 */
public class TradingPropertyElements
{
    private int integer1;
    private int integer2;
    private int integer3;
    private double double1;
    private double double2;
    private double double3;
    private int sequenceNumber;

    /**
     * Constructs the object with all primitive default values.
     */
    public TradingPropertyElements()
    {
    }

    /**
     * Constructs with all fields initialized
     * @param integer1 field value
     * @param integer2 field value
     * @param integer3 field value
     * @param double1 field value
     * @param double2 field value
     * @param double3 field value
     * @param sequenceNumber field value
     */
    public TradingPropertyElements(int integer1, int integer2, int integer3,
                                   double double1, double double2, double double3,
                                   int sequenceNumber)
    {
        this();
        this.double1 = double1;
        this.double2 = double2;
        this.double3 = double3;
        this.integer1 = integer1;
        this.integer2 = integer2;
        this.integer3 = integer3;
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Constructs with the field values being parsed from the passed value
     * @param value String to parse field values from
     */
    public TradingPropertyElements(String value)
    {
        this();
        decodeValue(value);
    }

    /**
     * Builds a compound String from all the passed values. Uses BasicPropertyParser for the compound String building.
     * The order of the encoded String elements is integer1, integer2, integer3, double1, double2, double3,
     * sequenceNumber.
     * @param integer1 value to encode
     * @param integer2 value to encode
     * @param integer3 value to encode
     * @param double1 value to encode
     * @param double2 value to encode
     * @param double3 value to encode
     * @param sequenceNumber value to encode
     * @return encoded String of all values
     */
    public static String encodeValues(int integer1, int integer2, int integer3,
                                      double double1, double double2, double double3,
                                      int sequenceNumber)
    {
        Object[] elements = new Object[7];
        elements[0] = new Integer(integer1);
        elements[1] = new Integer(integer2);
        elements[2] = new Integer(integer3);
        elements[3] = new Double(double1);
        elements[4] = new Double(double2);
        elements[5] = new Double(double3);
        elements[6] = new Integer(sequenceNumber);

        String encodedValue = BasicPropertyParser.buildCompoundString(elements);
        return encodedValue;
    }

    /**
     * Overriden to return the getEncodedValue()
     */
    public String toString()
    {
        return getEncodedValue();
    }

    /**
     * Determines equality based on field values.
     * @param otherObject to compare
     * @return True if all field values are equal, false otherwise.
     */
    public boolean equals(Object otherObject)
    {
        boolean isEqual = super.equals(otherObject);
        if(!isEqual)
        {
            if(otherObject instanceof TradingPropertyElements)
            {
                TradingPropertyElements castedTPE = (TradingPropertyElements) otherObject;

                isEqual = ( getInteger1() == castedTPE.getInteger1() &&
                            getInteger2() == castedTPE.getInteger2() &&
                            getInteger3() == castedTPE.getInteger3() &&
                            getDouble1() == castedTPE.getDouble1() &&
                            getDouble2() == castedTPE.getDouble2() &&
                            getDouble3() == castedTPE.getDouble3() &&
                            getSequenceNumber() == castedTPE.getSequenceNumber() );
            }
        }

        return isEqual;
    }

    /**
     * Builds a compound String from all the field values. Uses BasicPropertyParser for the compound String building.
     * The order of the encoded String elements is Integer1, Integer2, Integer3, Double1, Double2, Double3,
     * SequenceNumber.
     * @return encoded String of all values
     */
    public String getEncodedValue()
    {
        return encodeValues(getInteger1(), getInteger2(), getInteger3(), getDouble1(), getDouble2(),
                            getDouble3(), getSequenceNumber());
    }

    /**
     * Builds a lists where each element is the String representation of a field value. The order of the encoded String
     * elements is Integer1, Integer2, Integer3, Double1, Double2, Double3, SequenceNumber.
     * @return elements as String representations
     */
    public List getEncodedValuesAsStringList()
    {
        String[] elements = new String[7];
        elements[0] = Integer.toString(getInteger1());
        elements[1] = Integer.toString(getInteger2());
        elements[2] = Integer.toString(getInteger3());
        elements[3] = Double.toString(getDouble1());
        elements[4] = Double.toString(getDouble2());
        elements[5] = Double.toString(getDouble3());
        elements[6] = Integer.toString(getSequenceNumber());

        return Arrays.asList(elements);
    }

    public double getDouble1()
    {
        return double1;
    }

    public void setDouble1(double double1)
    {
        this.double1 = double1;
    }

    public double getDouble2()
    {
        return double2;
    }

    public void setDouble2(double double2)
    {
        this.double2 = double2;
    }

    public double getDouble3()
    {
        return double3;
    }

    public void setDouble3(double double3)
    {
        this.double3 = double3;
    }

    public int getInteger1()
    {
        return integer1;
    }

    public void setInteger1(int integer1)
    {
        this.integer1 = integer1;
    }

    public int getInteger2()
    {
        return integer2;
    }

    public void setInteger2(int integer2)
    {
        this.integer2 = integer2;
    }

    public int getInteger3()
    {
        return integer3;
    }

    public void setInteger3(int integer3)
    {
        this.integer3 = integer3;
    }

    public int getSequenceNumber()
    {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber)
    {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Parses the field values from the passed String
     * @param value to parse
     */
    protected void decodeValue(String value)
    {
        if(value != null && value.length() > 0)
        {
            String[] elements = BasicPropertyParser.parseArray(value);
            if(elements.length > 0)
            {
                int intValue = Integer.parseInt(elements[0]);
                setInteger1(intValue);
            }
            if(elements.length > 1)
            {
                int intValue = Integer.parseInt(elements[1]);
                setInteger2(intValue);
            }
            if(elements.length > 2)
            {
                int intValue = Integer.parseInt(elements[2]);
                setInteger3(intValue);
            }
            if(elements.length > 3)
            {
                double doubleValue = Double.parseDouble(elements[3]);
                setDouble1(doubleValue);
            }
            if(elements.length > 4)
            {
                double doubleValue = Double.parseDouble(elements[4]);
                setDouble2(doubleValue);
            }
            if(elements.length > 5)
            {
                double doubleValue = Double.parseDouble(elements[5]);
                setDouble3(doubleValue);
            }
            if(elements.length > 6)
            {
                int intValue = Integer.parseInt(elements[6]);
                setSequenceNumber(intValue);
            }
        }
    }
}
