//-------------------------------------------------------------------------
//FILE: DirectoryConstraintNodeVisitor.java
//
// PACKAGE: com.cboe.directoryService.parser
//
//-----------------------------------------------------------------------
//
// Copyright (c) 1998 The Chicago Board Options Exchange. All Rights Reserved.
//
//------------------------------------------------------------------------
package com.cboe.directoryService.parser;

//import com.cboe.loggingService.Log;
import java.util.ArrayList;

import com.cboe.common.log.Logger;
import com.cboe.common.log.InfraLoggingRb;
import java.util.*;

/**
*  This is the class that implements the visitor behavior for parsing DirectoryService constraints.
*  This class implements a number of "visit" methods.  Each visit method corresponds to a particular node
*  on the parse tree.  A parse tree consists of a node and possibly children.  The visit methods have the 
*  opportunity to inspect the parse tree values and affect changes to return values.
*  See the DirectoryServiceParser.jjt file for the grammer definition for the nodes.
*
*  This visitor creates a constraint object that has the reverse polish notation format of
*  the nodes. That is, individual pieces of the equation are wrapped together in the
*  TraderConstraint object, and each piece implements the OpHandler interface.
*  A CompareObject (a constraint piece) further wraps a node that contains a property test.
*  For example, a node with "(property == 3)" will be contained in a CompareObject.
*
* @author             Judd Herman
*/
public class DirectoryConstraintNodeVisitor
implements DirectoryServiceParserVisitor
{
	private static String EMPTY_STRING = "";

	/** sequence of OpHandlers */
	private TraderConstraint theConstraint;

	private static boolean isRegistered = false;

	private static ResourceBundle rb = null;

	public DirectoryConstraintNodeVisitor()
	{
		initializeLogging();
		
		theConstraint = new TraderConstraint();
		
	}
	
	// new chanaka
	private  void initializeLogging() {
		try {
			rb = ResourceBundle.getBundle( InfraLoggingRb.class.getName() );
			
		} catch( Exception e ) {
			Logger.sysAlarm( Logger.createLogMessageId( Logger.getDefaultLoggerName(),
											    "Unable to set Logging ResourceBundle({0}).",
											    "DirectoryConstraintNodeVisitor", "" ),
						  new Object[] {InfraLoggingRb.class.getName()} );
		}

	}
	

	/**
	* This is the inherited node - it should never be visited
	*/
	public Object visit(SimpleNode node, Object data)
	throws Exception
	{
		throw new DSVisitNotImplementedError();
	}

	/**
	* Accessor for the TraderConstraint object
	* @return the TraderConstraint object
	*/
	public TraderConstraint getTraderConstraint()
	{
		return theConstraint;
	}

	/**
	* Visit the top level node 
	* @param node ASTConstraint node
	* @param data Place for additional data to be passed to the visitor
	* @return an empty string in case the node is empty
	* @exception java.lang.Exception 
	*/
	public Object visit(ASTconstraint node, Object data)
	throws Exception
	{
		if (node.jjtGetNumChildren() == 0) {       
			return EMPTY_STRING;
		}

		return node.jjtGetChild(0).jjtAccept(this, data);
	}

	/**
	* Visit the ASTBool node. At this node, the children can consist of 
	* multiple boolean expression or comparisons.  
	* @param node ASTbool node
	* @param data Place for additional data to be passed to the visitor
	* @return The string for this node
	* Return an empty string in case the node is empty.
	* @exception java.lang.Exception 
	*/
	public Object visit(ASTbool node, Object data)
	throws Exception
	{
		//Log.traceEntry(null, "ASTbool");

		int numChildren = node.jjtGetNumChildren();
		//Log.debug(null, "ASTbool: numchildren", numChildren);

		int numOps = node.getNumOperator();
		//Log.debug(null, "ASTbool: numoperator", numOps);

		java.lang.Object[] params = {"DirectoryConstraintNodeVisitor", "visit",
							    "Node", node,
							    "Object", data,
		                                                "# children", new Integer( numChildren ),
		                                                "# operator", new Integer( numOps )};
		
		if (Logger.isLoggable( rb, InfraLoggingRb.METHOD_ENTRY_4, Logger.TRACE ) ) {
			Logger.traceEntry( rb,
						    InfraLoggingRb.METHOD_ENTRY_4,
						    params);
		}

		String[] children = new String[numChildren];
		for (int i = 0; i < numChildren; i++) {  
			children[i] = ((String)(node.jjtGetChild(i).jjtAccept(this, data)).toString());
			if ( children[i] == null ) {
				children[i] = EMPTY_STRING;
			}
		}

		for (int i = 0; i < numOps; i++) {
			OpHandler aHandler = OpHandlerFactory.create( ((Integer)node.getOperator(i)).intValue() );
			theConstraint.addToList(aHandler);
		}

		String retVal = null;
		if ( node.getValue() == null ) {
			retVal = EMPTY_STRING;
		}
		else {
			retVal = node.getValue().toUpperCase();
		}

		//Log.traceExit(null, "ASTbool");
		if (Logger.isLoggable ( rb, InfraLoggingRb.METHOD_EXIT, Logger.TRACE ) ) {
			Logger.traceExit( rb, 
						   InfraLoggingRb.METHOD_EXIT,
						   params );
		}

		return retVal;
	} 

	/**
	* Visit the ASTbool_compare node.
	* @param node ASTbool_compare node
	* @param data Place for additional data to be passed to the visitor
	* @return string for this node
	* Return an empty string in case the node is empty.
	* @exception java.lang.Exception 
	*/
	public Object visit(ASTbool_compare node, Object data)
	throws Exception
	{
		//Log.traceEntry(null, "ASTbool_compare");

		int numChildren = node.jjtGetNumChildren();
		//Log.debug(null, "ASTbool_compare: #children", numChildren);

		java.lang.Object[] params = {"DirectoryConstraintNodeVisitor", "visit",
							    "Node", node,
							    "Object", data,
							    "# children", new Integer( numChildren ) };
		
		if (Logger.isLoggable( rb, InfraLoggingRb.METHOD_ENTRY_3, Logger.TRACE ) ) {
			Logger.traceEntry( rb,
						    InfraLoggingRb.METHOD_ENTRY_3,
						    params);
		}

		String[] children = new String[numChildren];
  
		for (int i = 0; i < numChildren;i++) {  
			children[i] = ((String)(node.jjtGetChild(i).jjtAccept(this, data)).toString());
			if ( children[i] == null ) {
				children[i] = EMPTY_STRING;
			}
		}
      
		// If there are two children then this node is a regular boolean statement
		if (children.length == 2) {
			//Log.trace(null, "ASTbool_compare: create comparison object: " + (String)children[0] + "," + (String)children[1] + "," + node.getOperator());  // chanaka
			CompareObject cObj = 
				new CompareObject((String)children[0], (String)children[1], node.getOperator());
			theConstraint.addToList(cObj);
		}
		
		//Log.traceExit(null, "ASTbool_compare");
		if (Logger.isLoggable ( rb, InfraLoggingRb.METHOD_EXIT, Logger.TRACE ) ) {
			Logger.traceExit( rb, 
						   InfraLoggingRb.METHOD_EXIT,
						   params );
		}

		return "OK ";

	}

	/**
	* Visit the ASTexpr_in node. If two children on this node exist, handles the "in" operator to find values 
	* within an attribute list. 
	* Swap left and right hand values
	* @param node ASTexpr_in node
	* @param data Place for additional data to be passed to the visitor
	* @return string for this node
	* Return an empty string in case the node is empty.
	* @exception java.lang.Exception 
	*/
	public Object visit(ASTexpr_in node, Object data)
	throws Exception
	{
		//Log.traceEntry(null, "ASTexpr_in"); // chanaka

		Object tempObject = "";
		if (node.jjtGetNumChildren() == 2) {
			String lhs = (String)node.jjtGetChild(0).jjtAccept(this, data);
			String rhs = (String)node.jjtGetChild(1).jjtAccept(this, data);
			//Log.trace(null, "ASTexpr_in: create comparison object: " + rhs + "," + lhs + ",IN");  // chanaka
			CompareObject cObj = new CompareObject(rhs, lhs, DirectoryServiceParserConstants.IN);
			theConstraint.addToList(cObj);
		}
		else {
			tempObject = node.jjtGetChild(0).jjtAccept(this, data);
			//Log.debug(null, "visit(ASTexpr_in): retVal", tempObject);  // chanaka
		}

		//Log.traceExit(null, "ASTexpr_in");   // chanaka
		return tempObject;
	}

	/**
	* Visit the ASTexpr_twiddle node.
	* @param node ASTexpr_twiddle node
	* @param data Place for additional data to be passed to the visitor
	* @return string for this node
	* Return an empty string in case the node is empty.
	* @exception java.lang.Exception 
	*/
	public Object visit(ASTexpr_twiddle node, Object data)
	throws Exception
	{
		//Log.traceEntry(null, "ASTexpr_twiddle"); // chanaka

		Object retVal = null;
		if (node.jjtGetNumChildren() == 1) {
			retVal = node.jjtGetChild(0).jjtAccept(this, data);
		}
		else {
			String lhs = (String)node.jjtGetChild(0).jjtAccept(this, data);
			String rhs = (String)node.jjtGetChild(1).jjtAccept(this, data);
			//Log.trace(null, "ASTexpr_twiddle: create comparison object: " + lhs + "," + rhs + ",TWIDDLE");  // chanaka
			CompareObject cObj = new CompareObject(lhs, rhs, DirectoryServiceParserConstants.TWIDDLE);
			theConstraint.addToList(cObj);
			retVal = lhs + rhs;
		}

		//Log.debug(null, "ASTexpr_twiddle: retVal", retVal);  // chanaka
		//Log.traceExit(null, "ASTexpr_twiddle");  // chanaka

		return retVal;
	}

	/**
	* Visit the ASTexpr node. Return the appropriate data types in case this node has plus or 
	* minus operators.
	* @param node ASTexpr node
	* @param data Place for additional data to be passed to the visitor
	* @return concatenated object strings.  
	* @exception java.lang.Exception 
	*/
	public Object visit(ASTexpr node, Object data)
	throws Exception
	{
		//Log.traceEntry(null, "ASTexpr");

		Object tempObject = null;
		if (node.getNumOperator() == 0) {
			tempObject = node.jjtGetChild(0).jjtAccept(this, data);
		}
		else {
			for (int i=0; i<node.getNumOperator(); i++) {
				switch ( ((Integer)node.getOperator(i)).intValue() )
				{
					case DirectoryServiceParserConstants.PLUS:
					if (i == 0) {
						Object lhs = node.jjtGetChild(0).jjtAccept(this, data);
						Object rhs = node.jjtGetChild(1).jjtAccept(this, data);
						tempObject = add(lhs, rhs);
					}
					else {
						Object rhs = node.jjtGetChild(i+1).jjtAccept(this, data);
						tempObject = add(tempObject, rhs);
					}
					break;

					case DirectoryServiceParserConstants.MINUS:
					if (i == 0) {
						Object lhs = node.jjtGetChild(0).jjtAccept(this, data);
						Object rhs = node.jjtGetChild(1).jjtAccept(this, data);
						tempObject = subtract(lhs, rhs);
					}
					else {
						Object rhs = node.jjtGetChild(i+1).jjtAccept(this, data);
						tempObject = subtract(tempObject, rhs);
					}
					break;
				}
			}
		}

		//Log.traceEntry(null, "ASTexpr");

		return tempObject;
	}

	/**
	* Visit the ASTterm node.  Check for plus or minus values.   
	* @param node ASTterm node
	* @param data Place for additional data to be passed to the visitor
	* @return concatenated object strings.  
	* @exception java.lang.Exception 
	*/
	public Object visit(ASTterm node, Object data)
	throws Exception
	{
		if (node.getNumOperator() == 0) {
			Object tempObject = node.jjtGetChild(0).jjtAccept(this, data);
			return tempObject;
		}
		else {
			Object tempObject = null;
			for (int i = 0; i < node.getNumOperator();i++) {
				switch( ((Integer)node.getOperator(i)).intValue() )
				{
					case DirectoryServiceParserConstants.MULT:
					if (i == 0) {
						Object lhs = node.jjtGetChild(0).jjtAccept(this, data);
						Object rhs = node.jjtGetChild(1).jjtAccept(this, data);
						tempObject = multiply(lhs, rhs);
					}
					else {
						Object rhs = node.jjtGetChild(i+1).jjtAccept(this, data);
						tempObject = multiply(tempObject, rhs);
					}
					break;

					case DirectoryServiceParserConstants.DIV:
					if (i == 0) {
						Object lhs = node.jjtGetChild(0).jjtAccept(this, data);
						Object rhs = node.jjtGetChild(1).jjtAccept(this, data);
						tempObject = divide(lhs, rhs);
					}
					else {
						Object rhs = node.jjtGetChild(i+1).jjtAccept(this, data);
						tempObject = divide(tempObject, rhs);
					}
					break;		    
				}
			}
			return tempObject;
		} 
	}

	/**
	* Visit the ASTfactor_not node.
	* Replace with the factor value in case a "not" is supplied.   
	* @param node ASTfactor_not node
	* @param data Place for additional data to be passed to the visitor
	* @return object string
	* @exception java.lang.Exception 
	*/
	public Object visit(ASTfactor_not node, Object data)
	throws Exception
	{
		//Log.traceEntry(null, "ASTfactor_not");

		Object factorVal = node.jjtGetChild(0).jjtAccept(this, data);

		int notCount = node.getNotCount();
		if ( (notCount % 2) > 0 ) {
			//Log.trace(null, "ASTfactor_not: create oper for: NOT");
			OpHandler aHandler = 
				OpHandlerFactory.create( DirectoryServiceParserConstants.NOT );
			theConstraint.addToList(aHandler);
		}

		//Log.traceExit(null, "ASTfactor_not");
		return factorVal;
	}

	/**
	* Visit the ASTfactor node.
	* If the node uses the "exists" operator then create a comparison object.
	* In addition, responds for inputs of TRUe, FALSE, PLUS, MINUS and backslash.
	* @param node ASTfactor node
	* @param data Place for additional data to be passed to the visitor
	* @return string conversion for a particular input operator.   
	* @exception java.lang.Exception 
	*/
	public Object visit(ASTfactor node, Object data)
	throws Exception
	{
		//Log.traceEntry(null, "ASTfactor");
		//Log.debug(null, "ASTfactor: flag", node.getFlag());

		Object tempObject = null;
		switch(node.getFlag())
		{
			case DirectoryServiceParserConstants.EXIST:
			tempObject = node.jjtGetChild(0).jjtAccept(this, data);
			CompareObject cObj = 
				new CompareObject((String)tempObject, "", DirectoryServiceParserConstants.EXIST);
			theConstraint.addToList(cObj);
			return (String)tempObject;

			case DirectoryServiceParserConstants.MINUS:
			tempObject = node.jjtGetChild(0).jjtAccept(this, data);
			if (tempObject instanceof Double) {
				return new Double(-((Double)tempObject).doubleValue());
			} 
			else if (tempObject instanceof Long) {
				return new Long(-((Long)tempObject).longValue());  
			}
			return tempObject;

			case DirectoryServiceParserConstants.PLUS:
			return node.jjtGetChild(0).jjtAccept(this, data);

			case DirectoryServiceParserConstants.BACKSLASH:
			throw new DSVisitNotImplementedError();

			case DirectoryServiceParserConstants.TRUE:
			return "TRUE";

			case DirectoryServiceParserConstants.FALSE:
			return "FALSE";

			default:
			return node.jjtGetChild(0).jjtAccept(this, data);
		} 
	}

	/**
	* Visit the ASTIdent node.  Return as-is.
	* @param node ASTIdent node
	* @param data Place for additional data to be passed to the visitor
	* @return prepended string.   
	* @exception java.lang.Exception 
	*/
	public Object visit(ASTIdent node, Object data)
	throws Exception
	{
		return node.getValue();
	}

	/**
	* Visit the ASTFloat node.  Return as-is.
	* @param node ASTFloat node
	* @param data Place for additional data to be passed to the visitor
	* @return float value   
	* @exception java.lang.Exception 
	*/
	public Object visit(ASTFloat node, Object data)
	throws Exception
	{
		return node.getValue();
	}

	/**
	* Visit the ASTInteger node.  Return as-is.
	* @param node ASTInteger node
	* @param data Place for additional data to be passed to the visitor
	* @return integer value   
	* @exception java.lang.Exception 
	*/
	public Object visit(ASTInteger node, Object data)
	throws Exception
	{
		return node.getValue();
	}

	/**
	* Visit the ASTString node.  Return as-is.
	* @param node ASTString node
	* @param data Place for additional data to be passed to the visitor
	* @return string value   
	* @exception java.lang.Exception 
	*/
	public Object visit(ASTString node, Object data)
	throws Exception
	{
		return node.getValue();
	}

	/**
	* The divide operation divides two input values
	* @param lhs Can be a Double or a Long 
	* @param rhs Can be a Double or a Long 
	* @return result as an object    
	* @exception DSOperandTypesNotSupportedException if either parameter is not Double or Long
	*/
	private Object divide(Object lhs, Object rhs)
	throws DSOperandTypesNotSupportedException
	{
		if (lhs instanceof Double && rhs instanceof Double) {
			return (new Double(((Double)lhs).doubleValue() / ((Double)rhs).doubleValue()));
		}
		else if (lhs instanceof Double && rhs instanceof Long) {
			return(new Double(((Double)lhs).doubleValue() / ((Long)rhs).longValue()));
		}
		else if (lhs instanceof Long && rhs instanceof Double) {
			return(new Double(((Long)lhs).longValue() / ((Double)rhs).doubleValue()));
		}
		else if (lhs instanceof Long && rhs instanceof Long) {
			return(new Long(((Long)lhs).longValue() / ((Long)rhs).longValue()));
		} 

		throw new DSOperandTypesNotSupportedException();
	}

	/**
	* The multiply operation multiplies two input values
	* @param lhs Can be a Double or a Long 
	* @param rhs Can be a Double or a Long 
	* @return result as an object    
	* @exception DSOperandTypesNotSupportedException if either parameter is not Double or Long
	*/
	private Object multiply(Object lhs, Object rhs)
	throws DSOperandTypesNotSupportedException
	{
		if (lhs instanceof Double && rhs instanceof Double) {
			return(new Double(((Double)lhs).doubleValue() * ((Double)rhs).doubleValue()));
		}
		else if (lhs instanceof Double && rhs instanceof Long) {
			return(new Double(((Double)lhs).doubleValue() * ((Long)rhs).longValue()));
		}
		else if (lhs instanceof Long && rhs instanceof Double) {
			return(new Double(((Long)lhs).longValue() * ((Double)rhs).doubleValue()));
		}
		else if(lhs instanceof Long && rhs instanceof Long) {
			return(new Long(((Long)lhs).longValue() * ((Long)rhs).longValue()));
		}	

		throw new DSOperandTypesNotSupportedException();
	}

	/**
	* The add operation adds two input values
	* @param lhs Can be a Double or a Long 
	* @param rhs Can be a Double or a Long 
	* @return result as an object    
	* @exception DSOperandTypesNotSupportedException if either parameter is not Double or Long
	*/
	private Object add(Object lhs, Object rhs)
	throws DSOperandTypesNotSupportedException
	{
		if (lhs instanceof Double && rhs instanceof Double) {
			return (new Double(((Double)lhs).doubleValue() + ((Double)rhs).doubleValue()));
		}
		else if (lhs instanceof Double && rhs instanceof Long) {
			return (new Double(((Double)lhs).doubleValue() + ((Long)rhs).longValue()));
		}
		else if (lhs instanceof Long && rhs instanceof Double) {
			return (new Double(((Long)lhs).longValue() + ((Double)rhs).doubleValue()));
		}
		else if (lhs instanceof Long && rhs instanceof Long) {
			return (new Long(((Long)lhs).longValue() + ((Long)rhs).longValue()));
		}	

		throw new DSOperandTypesNotSupportedException();
	}

	/**
	* The subtract operation subtracts two input values
	* @param lhs Can be a Double or a Long 
	* @param rhs Can be a Double or a Long 
	* @return result as an object    
	* @exception DSOperandTypesNotSupportedException if either parameter is not Double or Long
	*/
	private Object subtract(Object lhs, Object rhs)
	throws DSOperandTypesNotSupportedException
	{
		if (lhs instanceof Double && rhs instanceof Double) {
			return (new Double(((Double)lhs).doubleValue() - ((Double)rhs).doubleValue()));
		}
		else if (lhs instanceof Double && rhs instanceof Long) {
			return (new Double(((Double)lhs).doubleValue() - ((Long)rhs).longValue()));
		}
		else if (lhs instanceof Long && rhs instanceof Double) {
			return (new Double(((Long)lhs).longValue() - ((Double)rhs).doubleValue()));
		}
		else if (lhs instanceof Long && rhs instanceof Long) {
			return (new Long(((Long)lhs).longValue() - ((Long)rhs).longValue()));
		}

		throw new DSOperandTypesNotSupportedException();
	}	
}
