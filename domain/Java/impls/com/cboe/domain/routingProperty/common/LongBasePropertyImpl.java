package com.cboe.domain.routingProperty.common;

import java.util.ArrayList;
import java.util.List;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.routingProperty.AbstractBaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.common.LongBaseProperty;
import com.cboe.interfaces.domain.routingProperty.Validator;

public class LongBasePropertyImpl extends AbstractBaseProperty implements
		LongBaseProperty, Comparable {

	public static final String LONG_CHANGE_EVENT = "LongValue";
    private long longValue = 0;

	 public LongBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                   BasePropertyType type)
    {
        super(propertyCategory, propertyName, key, type);
    }

	public LongBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                   BasePropertyType type, List<Validator> validators)
    {
        super(propertyCategory, propertyName, key, type, validators);
    }

	 public LongBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                   BasePropertyType type, long longtValue)
    {
        this(propertyCategory, propertyName, key, type);
        setLongValue(longtValue);
    }

	 public LongBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                   BasePropertyType type, long longValue, List<Validator> validators)
    {
        this(propertyCategory, propertyName, key, type, validators);
        setLongValue(longValue);
    }

	public long getLongValue()
    {
        return longValue;
    }

	public void setLongValue(long lngValue)
    {
        long oldValue = longValue;
        longValue = lngValue;
        firePropertyChange(LONG_CHANGE_EVENT, oldValue, lngValue);
    }


	 protected List getEncodedValuesAsStringList()
    {
        List valueList = new ArrayList(1);
        valueList.add(new Long(longValue));

        return valueList;
    }

	 protected void decodeValue(String value)
    {
        if (value != null && value.length() > 0)
        {
            String[] elements = BasicPropertyParser.parseArray(value);
            if (elements.length > 0)
            {
                this.longValue = Long.parseLong(elements[0]);
            }
        }
    }

	public String toString()
    {
        StringBuffer buffer = new StringBuffer(55);
        buffer.append(getPropertyName()).append("=");
        buffer.append(getLongValue());

        return buffer.toString();
    }

	@Override
	public int compareTo(Object object)
	{
		int result;
        double otherValue = ((LongBaseProperty) object).getLongValue();
        result = (getLongValue() < otherValue ? -1 : (getLongValue() == otherValue ? 0 : 1));
        return result;
	}

}
