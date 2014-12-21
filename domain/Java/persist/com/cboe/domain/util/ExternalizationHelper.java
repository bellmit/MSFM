package com.cboe.domain.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.Price;
import com.cboe.server.transactionTiming.EmitPoint;

public class ExternalizationHelper
{
	public static byte[] serializeObject(Externalizable m_obj)
	{
//		EmitPoint epSerial = new EmitPoint ("TT1_Serialization_" + m_obj.getClass().getCanonicalName());
		
		byte[] rval = null;

		try
		{
//			epSerial.enter();
			
			ByteArrayOutputStream os = new ByteArrayOutputStream(4000);

			ObjectOutputStream oos = new ObjectOutputStream(os);

			m_obj.writeExternal(oos);

			oos.flush();

			rval = os.toByteArray();

			oos.close();
		}
		catch (IOException e)
		{
			Log.exception(e);

			throw new RuntimeException(e);
		}
		finally
		{
//			epSerial.exit(false);
		}

		return rval;
	}

	public static Price readPrice(ObjectInput in) throws IOException
	{
		boolean b = in.readBoolean();

		if (b)
		{
			short type = in.readShort();
			int fraction = in.readInt();
			int whole = in.readInt();
			return PriceFactory.create(PriceFactory.createPriceStruct(type, whole, fraction));
		}

		return null;
	}
	
	public static PriceSqlType readPriceSqlType (ObjectInput in) throws IOException
	{
		Price rval = readPrice (in);
		
		if (rval != null)
		{
			return new PriceSqlType (rval.toStruct());
		}
		
		return null;
	}

	public static void writePrice(ObjectOutput out, Price price2) throws IOException
	{
		out.writeBoolean(price2 != null);

		if (price2 != null)
		{
			PriceStruct price = price2.toStruct();
			out.writeShort(price.type);
			out.writeInt(price.fraction);
			out.writeInt(price.whole);
		}
	}

	public static DateTimeStruct readDateTimeStruct(ObjectInput in) throws IOException
	{
		if (in.readBoolean())
		{
			DateTimeStruct rval = new DateTimeStruct();

			rval.date = readDateStruct(in);
			rval.time = readTimeStruct(in);

			return rval;
		}
		else
		{
			return null;
		}
	}

	public static TimeStruct readTimeStruct(ObjectInput in) throws IOException
	{
		if (in.readBoolean())
		{
			TimeStruct rval = new TimeStruct();

			rval.fraction = in.readByte();
			rval.hour = in.readByte();
			rval.minute = in.readByte();
			rval.second = in.readByte();

			return rval;
		}
		else
		{
			return null;
		}
	}

	public static String readString(ObjectInput in) throws IOException
	{
		int len = in.readInt();
		String rval = null;

		if (len != 0)
		{
			byte[] b = new byte[len];

			if (in.read(b) != len)
			{
				// throw exception
				throw new RuntimeException("Read string failed. Marshalling error");
			}

			rval = new String(b);
		}

		return rval;
	}

	public static DateStruct readDateStruct(ObjectInput in) throws IOException
	{
		boolean b = in.readBoolean();

		if (b)
		{
			DateStruct rval = new DateStruct();

			rval.day = in.readByte();
			rval.month = in.readByte();
			rval.year = in.readShort();

			return rval;
		}

		return null;
	}

	public static void writeString(ObjectOutput out, String prefix2) throws IOException
	{
		if (prefix2 != null)
		{
			out.writeInt(prefix2.length());
			out.writeBytes(prefix2);
		}
		else
		{
			out.writeInt(0);
		}
	}

	public static void write(ObjectOutput out, DateTimeStruct dts) throws IOException
	{
		out.writeBoolean(dts != null);

		if (dts != null)
		{
			write(out, dts.date);
			write(out, dts.time);
		}
	}

	public static void write(ObjectOutput out, DateStruct date) throws IOException
	{
		out.writeBoolean(date != null);

		if (date != null)
		{
			out.write(date.day);
			out.write(date.month);
			out.writeShort(date.year);
		}
	}

	public static void write(ObjectOutput out, TimeStruct time) throws IOException
	{
		out.writeBoolean(time != null);

		if (time != null)
		{
			out.write(time.fraction);
			out.write(time.hour);
			out.write(time.minute);
			out.write(time.second);
		}
	}

	public static void dump(byte[] bytes)
	{
		StringBuffer appendToBuf = new StringBuffer(1000);

		final int to = bytes.length;
		int hi;
		int lo;
		for (int i = 0; i < to; i++)
		{
			if (bytes[i] >= 32) // consider 32..127 as "printable"
			{
				appendToBuf.append((char) bytes[i]);
			}
			else
			{
				appendToBuf.append("[");
				hi = (0xf0 & bytes[i]) >> 4;
				lo = (0x0f & bytes[i]);
				appendToBuf.append((char) (hi > 9 ? 'a' + (hi - 10) : '0' + hi));
				appendToBuf.append((char) (lo > 9 ? 'a' + (lo - 10) : '0' + lo));
				appendToBuf.append(']');
			}
		}

		Log.information("Dump of packet:\n" + appendToBuf.toString());
	}

	public static String toString(Price price2)
	{
		if (price2 != null)
		{
			PriceStruct price = price2.toStruct();
			StringBuffer buf = new StringBuffer();

			buf.append("[");
			append(buf, "type", price.type);
			append(buf, "fraction", price.fraction);
			append(buf, "whole", price.whole);
			buf.append("]");

			return buf.toString();
		}
		else
		{
			return "null";
		}
	}

	private static void append(StringBuffer buf, String string, int type)
	{
		buf.append(string).append(" = ").append(type);
	}

	private static void append(StringBuffer buf, String string, short type)
	{
		buf.append(string).append(" = ").append(type);
	}

	public static String toString(DateStruct date)
	{
		if (date != null)
		{
			StringBuffer buf = new StringBuffer();

			buf.append("[");

			append(buf, "day", date.day);
			append(buf, "month", date.month);
			append(buf, "year", date.year);
			buf.append("]");

			return buf.toString();
		}
		else
		{
			return "null";
		}
	}

	public static Object toString(DateTimeStruct transactionTime)
	{
		if (transactionTime != null)
		{
			StringBuffer buf = new StringBuffer();

			buf.append("[");
			buf.append(toString(transactionTime.date));
			buf.append(toString(transactionTime.time));

			return null;
		}
		else
		{
			return "null";
		}
	}

	public static Object toString(TimeStruct time)
	{
		if (time != null)
		{
			StringBuffer buf = new StringBuffer();

			buf.append("[");

			append(buf, "fraction", time.fraction);
			append(buf, "hour", time.hour);
			append(buf, "minute", time.minute);
			append(buf, "second", time.second);
			buf.append("]");

			return buf.toString();
		}
		else
		{
			return "null";
		}
	}

	public static void readObject(Externalizable migratable, byte[] serialized_object)
	{
//		EmitPoint epSerial = new EmitPoint ("TT1_Deserialization_" + migratable.getClass().getCanonicalName());
		
		try
		{
//			epSerial.enter();
			
			ByteArrayInputStream os = new ByteArrayInputStream(serialized_object);

			ObjectInputStream oos = new ObjectInputStream(os);

			migratable.readExternal(oos);

			oos.close();
		}
		catch (Exception e)
		{
			Log.exception(e);

			throw new RuntimeException(e);
		}
		finally
		{
//			epSerial.exit(false);
		}
	}
}
