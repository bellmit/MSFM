package com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses;

import java.util.EventObject;

public class TreeChangeEvent extends EventObject {
	
	public TreeChangeEvent(MBean composite, MBean component) {
		super(composite);
		this.component = component;
	}
	
  public MBean getComposite() {
    return (MBean)getSource();
  }
  
  public MBean getComponent() {
    return component;
  }  
 
  private MBean component;
  
}