package com.cboe.interfaces.domain;

// spreads-fixMe: Adding this because of the general pattern of
// addding homes to the interfaces if the object it is the home for is
// added to the interfaces; Needed to add LegOrderDetail interface
// so adding this one too. EJF 8/31/2001
public interface LegOrderDetailHome
{
   static final String HOME_NAME = "LegOrderDetailHome";
    
    // Create and find methods are on implementation - they 
    // use OrderImpl objects; I can't put them here unless I make
    // the method use the interface and then cast within the LegOrderDetail.
}