package com.cboe.domain.marketData.mdhQueueEntry;

import java.io.IOException;
import java.io.StringWriter;

public class MDHStructFormatter
{
	public static String formatStruct(Object object, String message)
	{
		StringWriter writer = new StringWriter();
		try
		{
			com.cboe.util.ReflectiveObjectWriter.writeObject(object, "\t\t" + message, writer);
			writer.write("\n");
		}
		catch (IOException e)
		{
			writer.write(message + " - exception " + e.toString());
		}

		return writer.toString();
	}
}
