// -----------------------------------------------------------------------------------
// Source file: VolumeTypeImpl.java
//
// PACKAGE: com.cboe.interfaces.presentation.bookDepth;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData;

import com.cboe.interfaces.presentation.common.formatters.VolumeFormatStrategy;
import com.cboe.interfaces.presentation.marketData.VolumeType;
import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.idl.cmiConstants.VolumeTypes;

public class VolumeTypeImpl implements VolumeType
{
    private static final VolumeFormatStrategy formatter = FormatFactory.getVolumeFormatStrategy();
    public static final VolumeType LIMIT = new VolumeTypeImpl(VolumeTypes.LIMIT, formatter.format(VolumeTypes.LIMIT, VolumeFormatStrategy.STANDARD_VOLUME_NAME));
    public static final VolumeType AON = new VolumeTypeImpl(VolumeTypes.AON, formatter.format(VolumeTypes.AON, VolumeFormatStrategy.STANDARD_VOLUME_NAME));
    public static final VolumeType FOK = new VolumeTypeImpl(VolumeTypes.FOK, formatter.format(VolumeTypes.FOK, VolumeFormatStrategy.STANDARD_VOLUME_NAME));
    public static final VolumeType IOC = new VolumeTypeImpl(VolumeTypes.IOC, formatter.format(VolumeTypes.IOC, VolumeFormatStrategy.STANDARD_VOLUME_NAME));
    public static final VolumeType ODD_LOT = new VolumeTypeImpl(VolumeTypes.ODD_LOT, formatter.format(VolumeTypes.ODD_LOT, VolumeFormatStrategy.STANDARD_VOLUME_NAME));
    public static final VolumeType NO_CONTINGENCY = new VolumeTypeImpl(VolumeTypes.NO_CONTINGENCY, formatter.format(VolumeTypes.NO_CONTINGENCY, VolumeFormatStrategy.STANDARD_VOLUME_NAME));
    public static final VolumeType CUSTOMER_ORDER = new VolumeTypeImpl(VolumeTypes.CUSTOMER_ORDER, formatter.format(VolumeTypes.CUSTOMER_ORDER, VolumeFormatStrategy.STANDARD_VOLUME_NAME));
    public static final VolumeType PROFESSIONAL_ORDER = new VolumeTypeImpl(VolumeTypes.PROFESSIONAL_ORDER, formatter.format(VolumeTypes.PROFESSIONAL_ORDER, VolumeFormatStrategy.STANDARD_VOLUME_NAME));
    public static final VolumeType QUOTES = new VolumeTypeImpl(VolumeTypes.QUOTES, formatter.format(VolumeTypes.QUOTES, VolumeFormatStrategy.STANDARD_VOLUME_NAME));

    private static final VolumeType[] typeList = {
        LIMIT,
        AON,
        FOK,
        IOC,
        ODD_LOT,
        NO_CONTINGENCY,
        CUSTOMER_ORDER,
        PROFESSIONAL_ORDER,
        QUOTES,

    };
    public static VolumeType getByName(String name)
    {
        for (int i = 0; i < typeList.length; i++)
        {
            if (typeList[i].getName().equals(name))
            {
                return typeList[i];
            }
        }
        return null;
    }

    public static VolumeType getByKey(int key)
    {
        for (int i = 0; i < typeList.length; i++)
        {
            if (typeList[i].getKey() == key)
            {
                return typeList[i];
            }
        }
        return null;
    }

    public static VolumeType[] getVolumeTypes()
    {
        return typeList;
    }

    public static boolean isContingencyVolumeType(VolumeType type)
    {
        boolean result = false;

        if (type == VolumeTypeImpl.AON ||
            type == VolumeTypeImpl.FOK ||
            type == VolumeTypeImpl.IOC ||
            type == VolumeTypeImpl.LIMIT)
        {
            result = true;
        }

        return result;
    }

    private int volumeTypeKey;
    private String volumeTypeName;

    private VolumeTypeImpl(int key, String name)
    {
        this();
        this.volumeTypeKey = key;
        this.volumeTypeName = name;
    }

    private VolumeTypeImpl()
    {
        super();
    }

    public String getName()
    {
        return this.volumeTypeName;
    }

    public int getKey()
    {
        return this.volumeTypeKey;
    }

    public boolean equals(Object obj)
    {
        boolean result = false;
        if (obj instanceof VolumeType)
        {
            VolumeType otherType = (VolumeType) obj;
            result = (this.getKey() == otherType.getKey());
        }
        return result;
    }

    public String toString()
    {
        return getName();
    }

}
