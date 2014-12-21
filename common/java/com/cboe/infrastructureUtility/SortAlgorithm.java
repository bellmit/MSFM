// Source file: com/cboe/util/SortAlgorithm.java

package com.cboe.infrastructureUtility;

import java.util.*;


/**
   @(#)SortAlgorithm.java	1.6f 95/01/31 James Gosling
   Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
   Permission to use, copy, modify, and distribute this software
   and its documentation for NON-COMMERCIAL or
   COMMERCIAL purposes and without fee is hereby granted. 
   Please refer to the file http://java.sun.com/copy_trademarks.html
   for further important copyright and trademark information and
   to http://java.sun.com/licensing.html for further important
   licensing information for the Java (tm) Technology.
   SUN MAKES NO REPRESENTATIONS OR
   WARRANTIES ABOUT THE SUITABILITY OF
   THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
   PARTICULAR PURPOSE, OR NON-INFRINGEMENT.
   SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
   SUFFERED BY LICENSEE AS A RESULT OF USING,
   MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
   ITS DERIVATIVES.
   THIS SOFTWARE IS NOT DESIGNED OR INTENDED
   FOR USE OR RESALE AS ON-LINE CONTROL
   EQUIPMENT IN HAZARDOUS ENVIRONMENTS
   REQUIRING FAIL-SAFE PERFORMANCE, SUCH AS IN
   THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
   NAVIGATION OR COMMUNICATION SYSTEMS, AIR
   TRAFFIC CONTROL, DIRECT LIFE SUPPORT
   MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE
   FAILURE OF THE SOFTWARE COULD LEAD DIRECTLY
   TO DEATH, PERSONAL INJURY, OR SEVERE PHYSICAL
   OR ENVIRONMENTAL DAMAGE ("HIGH RISK
   ACTIVITIES").  SUN SPECIFICALLY DISCLAIMS ANY
   EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
   HIGH RISK ACTIVITIES.
   @(#SortAlgorithm.java, Thu Oct 27 10:32:35 1994
   Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
   @author James Gosling
   @modified by Renzo Zanelli
 */
public interface SortAlgorithm {
    public static final int SORT_ASCENDING_ORDER = 0;
    public static final int SORT_DESCENDING_ORDER = 1;
    
    /**
       Sort an item set
       @param itemSet  set of items to sort
       @exception      SortAlgorithmException
       if the set to be sorted is not a sortable object
       collection or the sort order specified is not
       SORT_ASCENDING_ORDER or SORT_DESCENDING_ORDER
       @roseuid 36CDD93101A2
     */
    public void sort(Object[] group) throws SortAlgorithmException;
    
    /**
       Sort an item set in specified direction
       @param itemSet  set of items to sort
       @param order    direction of sort
       @exception      SortAlgorithmException
       if the set to be sorted is not a sortable object
       collection or the sort order specified is not
       SORT_ASCENDING_ORDER or SORT_DESCENDING_ORDER
       @roseuid 36CDD9D603BC
     */
    public void sort(Object[] group, int order) throws SortAlgorithmException;
    
    /**
       Sort an item set
       @param itemSet  set of items to sort
       @exception      SortAlgorithmException
       if the set to be sorted is not a sortable object
       collection or the sort order specified is not
       SORT_ASCENDING_ORDER or SORT_DESCENDING_ORDER
       @roseuid 36CDD9DA015F
     */
    public void sort(Vector group) throws SortAlgorithmException;
    
    /**
       Sort an item set in specified direction
       @param itemSet  set of items to sort
       @param order    direction of sort
       @exception      SortAlgorithmException
       if the set to be sorted is not a sortable object
       collection or the sort order specified is not
       SORT_ASCENDING_ORDER or SORT_DESCENDING_ORDER
       @roseuid 36CDD9DD0019
     */
    public void sort(Vector group, int order) throws SortAlgorithmException;
}
