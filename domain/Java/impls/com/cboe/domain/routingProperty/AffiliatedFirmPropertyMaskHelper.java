package com.cboe.domain.routingProperty;

public class AffiliatedFirmPropertyMaskHelper 
{
	// Session, AffiliatedFirm, ClassKey [ all are required fields ]
	public static final int[][] DIRECTED_AIM_AFFILIATED_FIRM_REGISTRATION_MASK = {{0,0,0,0}}; 
	
	// Session, AffiliatedFirm [ all are required fields ]
	public static final int[][] DIRECTED_AIM_AFFILIATED_FIRM_REGISTRATION_OVERRIDE_MASK = {{0,0,0}};
    
	//  Session, AffiliatedFirm, ExecutingFirm
	public static final int[][] DIRECTED_AIM_AFFILIATED_FIRM_PARTNERSHIP_MASK = {{0,0,0,0,0,0,1},{0,0,0,0,0,1,0},{0,0,0,0,0,1,1},{0,0,0,1,1,0,0},{0,0,0,1,1,0,1},{0,0,0,1,1,1,0},{0,0,0,1,1,1,1}};
    
}