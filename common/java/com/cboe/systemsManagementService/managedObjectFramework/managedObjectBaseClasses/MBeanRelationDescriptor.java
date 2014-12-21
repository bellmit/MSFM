/* Copyright (c) 1999 The Chicago Board Options Exchange.  All Rights Reserved */

package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

/**
  Defines the operations supported by an MBean relation descriptor. The relation descriptor describes
  one side of a permissible relation between this MBean and other MBeans. An MBean may have more than 
  one kind of permissible relations with other MBeans.
  
*/
public interface MBeanRelationDescriptor extends MBeanFeatureDescriptor {
	
  /**
    Constant for denoting a <i>dependency</i> relation, whereby a <i>client</i> MBean 
    <i>uses</i> the services provided by zero or more <i>server</i> MBeans.
  */  
  public static String DEPENDENCY = "dependency";
  
  /**
    Constant for denoting the <i>client</i> role in a <i>dependency</i> relation.
  */
  public static String CLIENT = "client";
  
  /**
    Constant for denoting the <i>server</i> role in a <i>dependency</i> relation.
  */
  public static String SERVER = "server";
  
  /**
    Constant for denoting an <i>aggregation</i> relation, whereby an <i>aggregated</i> 
    MBean <i>aggregates</i> zero or more <i>aggregate</i> MBeans. An MBean may be 
    aggregated in more than one aggregate MBeans. Aggregate MBeans do not control the lifetime
    of their aggregated MBeans.
  */  
  public static String AGGREGATION = "aggregation";
  
  /**
    Constant for denoting the <i>aggregate</i> role in an <i>aggregation</i> relation.
  */
  public static String AGGREGATE = "aggregate";
  
  /**
    Constant for denoting the <i>aggregated</i> role in an <i>aggregation</i> relation.
  */
  public static String AGGREGATED = "aggregated";
  
  /**
    Constant for denoting a <i>composition</i> relation, whereby a <i>composite</i> MBean 
    <i>contains</i> zero or more <i>component</i> MBeans. Composition is 
    stronger relation than aggregation because the composite controls the lifecycle of its 
    component MBeans. A component MBean can belong to at most one composite 
    MBean.
  */  
  public static String COMPOSITION = "composition";
  
  /**
    Constant for denoting the <i>composite</i> role in a <i>composition</i> relation.
  */
  public static String COMPOSITE = "composite";
  
  /**
    Constant for denoting the <i>component</i> role in a <i>composition</i> relation.
  */
  public static String COMPONENT = "component";
  
  /**
    Constant denoting cardinality one.
  */  
  public static char EXACTLY_ONE = ' ';
  
  /**
    Constant denoting cardinality zero or more.
  */  
  public static char ZERO_OR_MORE = '*';

  /**
    Constant denoting cardinality one or more.
  */  
  public static char ONE_OR_MORE = '+';

  /**
    Constant denoting cardinality zero or one.
  */  
  public static char ZERO_OR_ONE = '?';
  
  /**
    * Returns the type of the relation.
    *
    * @post return != null
    * @post ( return.equals(DEPENDENCY) ||
    *        return.equals(AGGREGATION) ||
    *        return.equals(COMPOSITION) )
  */
  public String getRelationType();
  
  /**
    Returns the type of target MBeans that can be related to this MBean in the role given by 
    <code>getTargetRole()</code> or null if MBeans of any type can be related to this MBean.
    
  */
  public String getTargetMBeanType();

  /**
    * Returns the role of the target MBeans related to this MBean.
    * 
    * @post return != null
    * @post getRelationType().equals(DEPENDENCY) implies
    *            return.equals(CLIENT) || return.equals(SERVER)
    * @post getRelationType().equals(AGGREGATION) implies
    *            return.equals(AGGREGATE) || return.equals(AGGREGATED)
    * @post getRelationType().equals(COMPOSITION) implies
    *            return.equals(COMPOSITE) || return.equals(COMPONENT)
    *
  */
  public String getTargetRole();  
  	
  /**
    * Returns the permissible number of target MBeans related to this MBean
    *
    * @post return == EXACTLY_ONE ||
    *       return == ZERO_OR_MORE ||
    *       return == ONE_OR_MORE ||
    *       return == ZERO_OR_ONE
  */
  public char getCardinality();
  
  /**
    * Returns a flag indicating whether or not to load the target MBeans under this relation
    * in the same Agent as their parent.
    *
    */
  
}

