package com.cboe.domain.util;

import com.cboe.idl.groupElement.ElementStruct;
import com.cboe.idl.groupElement.ElementErrorResultStruct;

/**
 * Wrapper object for GroupElementEvent Channel, 
*/
public class GroupElementEventStructContainer
{
    private ElementErrorResultStruct[] elementErrorResults;
    private ElementStruct elementStruct;
    private long 		parentGroupElementKey;
    private boolean		isRemoveElement;

    public GroupElementEventStructContainer(long parentGroupElementKey,
                                                  ElementStruct elementStruct)
    {
    	this.parentGroupElementKey = parentGroupElementKey;
    	this.elementStruct = elementStruct;
        
    }

    public GroupElementEventStructContainer(long parentGroupElementKey,
                                            ElementStruct elementStruct,
											boolean isRemoveElement)
    {
    	this.parentGroupElementKey = parentGroupElementKey;
    	this.elementStruct = elementStruct;
		this.isRemoveElement = isRemoveElement;
    }

    public GroupElementEventStructContainer(ElementErrorResultStruct[] errorResults)
    {
    	this.elementErrorResults = errorResults;
    }

    public long getParentGroupElementKey()
    {
        return  parentGroupElementKey;
    }

    public ElementStruct getElementStruct()
    {
        return  elementStruct;
    }

    public boolean getIsRemoveElement(){
        return  isRemoveElement;
    }

    public ElementErrorResultStruct[] getElementErrorResultStructs()
    {
        return elementErrorResults;
    }
}
