/* Generated By:JJTree: Do not edit this line. ASTexpr_twiddle.java */

package com.cboe.directoryService.parser;

public class ASTexpr_twiddle extends SimpleNode {
  public ASTexpr_twiddle(int id) {
    super(id);
  }

  public ASTexpr_twiddle(DirectoryServiceParser p, int id) {
    super(p, id);
  }

  public boolean equals(Object obj) {
      if(!(obj instanceof ASTexpr_twiddle)) {
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


  /** Accept the visitor. **/
  public Object jjtAccept(DirectoryServiceParserVisitor visitor, Object data) throws Exception {
    return visitor.visit(this, data);
  }
}
