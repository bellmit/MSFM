package com.cboe.directoryService.parser;

public class OpHandlerFactory
implements DirectoryServiceParserConstants
{
	public static OpHandler create(int opCode)
	{
		if ( opCode == -1 ) {
			return new CompareObject();
		}
		else if ( opCode == DirectoryServiceParserConstants.AND ) {
			return new AndObject();
		}
		else if ( opCode == DirectoryServiceParserConstants.OR ) {
			return new OrObject();
		}
		else if ( opCode == DirectoryServiceParserConstants.NOT ) {
			return new NotObject();
		}
		else {
			throw new RuntimeException("don't know how to create opcode " + opCode);
		}
	}
}
