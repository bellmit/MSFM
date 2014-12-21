/* Generated By:JJTree: Do not edit this line. ASTexpr.java */

package com.cboe.directoryService.parser;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

public class ASTexpr extends SimpleNode {
  private List list;
  private String value;
    
  public ASTexpr(int id) {
    super(id);
    list = Collections.synchronizedList(new ArrayList());
  }

  public ASTexpr(DirectoryServiceParser p, int id) {
    super(p, id);
    list = Collections.synchronizedList(new ArrayList());
  }
 
  public void addOperator(int operator) {
      Integer operInteger = new Integer(operator);
      list.add(operInteger);
    }
    public int getNumOperator() {
    	return list.size();
    }
    
    public Object getOperator(int index) {
    	return list.get(index);
    }

    public boolean equals(Object obj) {
	if(!(obj instanceof ASTexpr)) {
	    return false;
	}
	//  System.out.println("lhs" + toString() + " rhs" + obj);
	if (getNumOperator() == ((ASTexpr)obj).getNumOperator()) {
	    for(int i = 0; i < getNumOperator(); i++) {
		if(((Integer)getOperator(i)).intValue() != ((Integer)((ASTexpr)obj).getOperator(i)).intValue()) {
		    return false;
		}
	    }
	} else {
	    return false;
	}
	
	if(jjtGetNumChildren() == ((Node)obj).jjtGetNumChildren()) {
	    for(int i = 0; i < jjtGetNumChildren(); i++) {
		if(jjtGetChild(i).equals(((Node)obj).jjtGetChild(i)) == false) {
		    return false;
		}
	    }
	} else {
	    return false;
	}
	return true;
    }
  /* Override this method if you want to customize how the node dumps
     out its children. */

    
  public void dump(String prefix) {
      if(children != null) {
	  if( children.length > 1) {
	      System.out.println(toString(prefix) + "***");
	  } else {
	      System.out.println(toString(prefix));
	  }
      } else {
	  System.out.println(toString(prefix));
      }
      if (children != null) {
    	  synchronized (list ) { 
			  Iterator e = list.iterator();
		
		      for (int i = 0; i < children.length; ++i) {
				  if(e.hasNext() ) {
					  System.out.println(prefix + e.next());
				  }
				  SimpleNode n = (SimpleNode)children[i];
				  if (n != null) {
					  n.dump(prefix + " ");
				  }
		      }
    	  }
      }
  }



  /** Set the value. **/
  public void setValue(String n) {
    value = n;
  }
  
  /** return  the value. **/ 
  public String getValue() {
    return value;
  }
  
  /** Accept the visitor. **/
  public Object jjtAccept(DirectoryServiceParserVisitor visitor, Object data) throws Exception {
    return visitor.visit(this, data);
  }
}
