package com.cboe.presentation.common.formatters;

import com.cboe.idl.cmiOrder.OrderContingencyStruct;
import com.cboe.interfaces.domain.routingProperty.common.ContingencyType;
import com.cboe.interfaces.presentation.common.formatters.ContingencyFormatStrategy;

public class ContingencyFormatter extends Formatter implements ContingencyFormatStrategy
{

	 public ContingencyFormatter()
	    {
	        super();
	        addStyle(BRIEF, BRIEF_DESC);
	        addStyle(FULL,  FULL_DESC);

	        setDefaultStyle(BRIEF);
	    }

	    public String format(OrderContingencyStruct contingency)
	    {
	        return format(contingency, getDefaultStyle());
	    }
	    public String format(OrderContingencyStruct contingency, String styleName)
	    {
	        validateStyle(styleName);
	        String ret = "";
	        if (styleName.equals(FULL))
	        {
	            ret = ContingencyTypes.toString(contingency.type, ContingencyTypes.FULL_FORMAT);
	        } else
	        {
	            ret = ContingencyTypes.toString(contingency.type);
	        }
	        if(contingency.type == ContingencyTypes.MIN ||
	                contingency.type == ContingencyTypes.RESERVE)
	        {
	            ret = ret +" "+contingency.volume;
	        }
	        else if(contingency.type == ContingencyTypes.STP ||
	               (contingency.type == ContingencyTypes.STP_LOSS) ||
	               (contingency.type == ContingencyTypes.STP_LIMIT) ||
	               (contingency.type == ContingencyTypes.BID_PEG_CROSS) ||
	               (contingency.type == ContingencyTypes.OFFER_PEG_CROSS)
	               )
	        {
	            ret = ret +" "+DisplayPriceFactory.create(contingency.price);
	        }
	        return ret;

	    }
	    
	    public String format(ContingencyType contingency){
	    	return ContingencyTypes.toString(new Integer(contingency.contingencyType).shortValue()) ;
	    }
}
