/* Generated By:JJTree: Do not edit this line. ASTString.java */

package com.cboe.directoryService.parser;

public class ASTString extends SimpleNode {
private String value;  
public ASTString(int id) {
    super(id);
  }

  public ASTString(DirectoryServiceParser p, int id) {
    super(p, id);
  }
 
 
 public void setValue(String n) {
    value = n.substring(1, n.length()-1);
  }

    public String getValue() {
	return value;
    }

  public String toString(String prefix) {
    return prefix + "String: " + value;
  }

    public boolean equals(Object obj) {
	if(!(obj instanceof ASTString)) {
	    return false;
	}
	if(value.equals(((ASTString)obj).getValue())) {
	    return true;
	} else {
	    return false;
	}
    }
	

  /** Accept the visitor. **/
  public Object jjtAccept(DirectoryServiceParserVisitor visitor, Object data) throws Exception {
    return visitor.visit(this, data);
  }
}
