package com.cboe.domain.util;

public class DumpStruct
{
	public static void main(String args[])
	{
		if (args.length == 0 || args[0].equals("-?"))
		{
			System.out.println("Expected args: className [prefix]");
			return;
		}
		String prefix = "struct";
		if (args.length > 1)
			prefix = args[1];
		try
		{
			Class c = Class.forName(args[0]);
			Object obj = ReflectiveStructBuilder.newStruct(c);
			ReflectiveStructBuilder.printStruct(obj, prefix);
		}
		catch (Exception ex)
		{
			System.err.println("Error: " + ex);
		}
	}
}
