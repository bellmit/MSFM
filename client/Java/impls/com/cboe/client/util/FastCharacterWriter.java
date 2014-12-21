package com.cboe.client.util;

/**
 * FastCharacterWriter.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.io.*;

public class FastCharacterWriter extends Writer implements Cloneable
{
    protected char[] characters;
    protected int    size;

    public static final int DEFAULT_CAPACITY = 2048;
    private static final String INTEGER_MIN_STRING = Integer.toString(Integer.MIN_VALUE);

    public static final FastCharacterWriter EMPTY_FAST_CHARACTER_WRITER = new FastCharacterWriter()
    {
        public int size()
        {
            return 0;
        }
    };

    public FastCharacterWriter()
    {
        super();

        characters = new char[DEFAULT_CAPACITY];
    }

    public FastCharacterWriter(int capacity)
    {
        super();

        characters = new char[capacity];
    }

    public FastCharacterWriter(Object lock)
    {
        super(lock);

        characters = new char[DEFAULT_CAPACITY];
    }

    public FastCharacterWriter(FastCharacterWriter fastCharacterWriter)
    {
        characters = new char[fastCharacterWriter.size];
        size       = fastCharacterWriter.size;

        System.arraycopy(fastCharacterWriter.characters, 0, characters, 0, size);
    }

    public void clear()
    {
        size = 0;
    }

    public void flush()
    {

    }

    public void close()
    {

    }

    public int size()
    {
        return size;
    }

    public String toString()
    {
        return new String(characters, 0, size);
    }

    public char[] toCharArray()
    {
        return characters;
    }

    public void write(int c)
    {
        ensureCapacity(1);

        characters[size++] = (char) c;
    }

    public void write(char[] buf1)
    {
        ensureCapacity(buf1.length);

        for (int i = 0; i < buf1.length; i++)
        {
            characters[size++] = buf1[i];
        }
    }

    public void write(char[] buf1, char[] buf2)
    {
        ensureCapacity(buf1.length + buf2.length);

        for (int i = 0; i < buf1.length; i++)
        {
            characters[size++] = buf1[i];
        }

        for (int i = 0; i < buf2.length; i++)
        {
            characters[size++] = buf2[i];
        }
    }

    public void write(char[] buf1, char[] buf2, char[] buf3)
    {
        ensureCapacity(buf1.length + buf2.length + buf3.length);

        for (int i = 0; i < buf1.length; i++)
        {
            characters[size++] = buf1[i];
        }

        for (int i = 0; i < buf2.length; i++)
        {
            characters[size++] = buf2[i];
        }

        for (int i = 0; i < buf3.length; i++)
        {
            characters[size++] = buf3[i];
        }
    }

    public void write(char[] buf1, char[] buf2, char[] buf3, char[] buf4)
    {
        ensureCapacity(buf1.length + buf2.length + buf3.length + buf4.length);

        for (int i = 0; i < buf1.length; i++)
        {
            characters[size++] = buf1[i];
        }

        for (int i = 0; i < buf2.length; i++)
        {
            characters[size++] = buf2[i];
        }

        for (int i = 0; i < buf3.length; i++)
        {
            characters[size++] = buf3[i];
        }

        for (int i = 0; i < buf4.length; i++)
        {
            characters[size++] = buf4[i];
        }
    }

    public void write(char[] buf1, char[] buf2, char[] buf3, char[] buf4, char[] buf5)
    {
        ensureCapacity(buf1.length + buf2.length + buf3.length + buf4.length + buf5.length);

        for (int i = 0; i < buf1.length; i++)
        {
            characters[size++] = buf1[i];
        }

        for (int i = 0; i < buf2.length; i++)
        {
            characters[size++] = buf2[i];
        }

        for (int i = 0; i < buf3.length; i++)
        {
            characters[size++] = buf3[i];
        }

        for (int i = 0; i < buf4.length; i++)
        {
            characters[size++] = buf4[i];
        }

        for (int i = 0; i < buf5.length; i++)
        {
            characters[size++] = buf5[i];
        }
    }

    public void write(char[] buf1, char[] buf2, char[] buf3, char[] buf4, char[] buf5, char[] buf6)
    {
        ensureCapacity(buf1.length + buf2.length + buf3.length + buf4.length + buf5.length + buf6.length);

        for (int i = 0; i < buf1.length; i++)
        {
            characters[size++] = buf1[i];
        }

        for (int i = 0; i < buf2.length; i++)
        {
            characters[size++] = buf2[i];
        }

        for (int i = 0; i < buf3.length; i++)
        {
            characters[size++] = buf3[i];
        }

        for (int i = 0; i < buf4.length; i++)
        {
            characters[size++] = buf4[i];
        }

        for (int i = 0; i < buf5.length; i++)
        {
            characters[size++] = buf5[i];
        }

        for (int i = 0; i < buf6.length; i++)
        {
            characters[size++] = buf6[i];
        }
    }

    public void write(char[] buf1, char char1)
    {
        ensureCapacity(buf1.length + 1);

        for (int i = 0; i < buf1.length; i++)
        {
            characters[size++] = buf1[i];
        }

        characters[size++] = char1;
    }

    public void write(char buf[], int offset, int length)
    {
        ensureCapacity(length);

        System.arraycopy(buf, offset, characters, size, length);
        size += length;
    }

    public void write(char[] buf1, char char1, char[] buf2, char char2)
    {
        ensureCapacity(buf1.length + buf2.length + 2);

        for (int i = 0; i < buf1.length; i++)
        {
            characters[size++] = buf1[i];
        }

        characters[size++] = char1;

        for (int i = 0; i < buf2.length; i++)
        {
            characters[size++] = buf2[i];
        }

        characters[size++] = char2;
    }

    public void write(char[] buf1, char char1, char char2, char char3)
    {
        ensureCapacity(buf1.length + 3);

        for (int i = 0; i < buf1.length; i++)
        {
            characters[size++] = buf1[i];
        }

        characters[size++] = char1;
        characters[size++] = char2;
        characters[size++] = char3;
    }

    public void write(char[] buf1, char char1, String str2, char char2)
    {
        ensureCapacity(buf1.length + str2.length() + 2);

        for (int i = 0; i < buf1.length; i++)
        {
            characters[size++] = buf1[i];
        }

        characters[size++] = char1;

        size += StringHelper.copyStringToCharArray(characters, size, str2);

        characters[size++] = char2;
    }

    public void write(char[] buf1, char char1, int number, char char2)
    {
        ensureCapacity(buf1.length + IntegerHelper.LOG_10_INT_WHOLE_MIN_VALUE + 2);

        for (int i = 0; i < buf1.length; i++)
        {
            characters[size++] = buf1[i];
        }

        characters[size++] = char1;

        size += appendInt(size, number);

        characters[size++] = char2;
    }

    public void write(String str1, char char1)
    {
        ensureCapacity(str1.length() + 1);

        size += StringHelper.copyStringToCharArray(characters, size, str1);

        characters[size++] = char1;
    }

    public void write(String str1, char char1, char[] buf2, char char2)
    {
        ensureCapacity(str1.length() + buf2.length + 2);

        size += StringHelper.copyStringToCharArray(characters, size, str1);

        characters[size++] = char1;

        for (int i = 0; i < buf2.length; i++)
        {
            characters[size++] = buf2[i];
        }

        characters[size++] = char2;
    }

    public void write(String str1, char char1, String str2, char char2)
    {
        ensureCapacity(str1.length() + str2.length() + 2);

        size += StringHelper.copyStringToCharArray(characters, size, str1);

        characters[size++] = char1;

        size += StringHelper.copyStringToCharArray(characters, size, str2);

        characters[size++] = char2;
    }

    public void write(String str1, char char1, int number, char char2)
    {
        ensureCapacity(str1.length() + IntegerHelper.LOG_10_INT_WHOLE_MIN_VALUE + 2);

        size += StringHelper.copyStringToCharArray(characters, size, str1);

        characters[size++] = char1;

        size += appendInt(size, number);

        characters[size++] = char2;
    }

    public void write(String str)
    {
        ensureCapacity(str.length());

        size += StringHelper.copyStringToCharArray(characters, size, str);
    }

    public void write(String str, int offset, int length)
    {
        ensureCapacity(length);

        size += StringHelper.copyStringToCharArray(characters, size, str, offset, length);
    }

    public void write(String str1, String str2)
    {
        ensureCapacity(str1.length() + str2.length());

        size += StringHelper.copyStringToCharArray(characters, size, str1, str1.length());
        size += StringHelper.copyStringToCharArray(characters, size, str2, str2.length());
    }

    public void write(String str1, String str2, String str3)
    {
        ensureCapacity(str1.length() + str2.length() + str3.length());

        size += StringHelper.copyStringToCharArray(characters, size, str1);
        size += StringHelper.copyStringToCharArray(characters, size, str2);
        size += StringHelper.copyStringToCharArray(characters, size, str3);
    }

    public void write(String str1, String str2, String str3, String str4)
    {
        ensureCapacity(str1.length() + str2.length() + str3.length() + str4.length());

        size += StringHelper.copyStringToCharArray(characters, size, str1);
        size += StringHelper.copyStringToCharArray(characters, size, str2);
        size += StringHelper.copyStringToCharArray(characters, size, str3);
        size += StringHelper.copyStringToCharArray(characters, size, str4);
    }

    public void write(FastCharacterWriter fastCharacterWriter)
    {
        if (fastCharacterWriter == null || fastCharacterWriter.size == 0)
        {
            return;
        }

        ensureCapacity(fastCharacterWriter.size);

        System.arraycopy(fastCharacterWriter.characters, 0, characters, size, fastCharacterWriter.size);

        size += fastCharacterWriter.size;
    }

    public void writeInt(int number)
    {
        ensureCapacity(IntegerHelper.countDigits(number));

        size += appendInt(size, number);
    }

    public void insert(int offset, int number)
    {
        size += shiftCapacity(offset, IntegerHelper.countDigits(number));

        appendInt(offset, number);
    }

    public void insert(int offset, char c)
    {
        size += shiftCapacity(offset, 1);

        characters[offset] = c;
    }

    public void insert(int offset, char[] buf)
    {
        size += shiftCapacity(offset, buf.length);

        System.arraycopy(buf, 0, characters, offset, buf.length);
    }

    public void insert(int offset, char[] buf1, char[] buf2)
    {
        size += shiftCapacity(offset, buf1.length + buf2.length);

        int p = offset;

        System.arraycopy(buf1, 0, characters, p, buf1.length);
        p += buf1.length;

        System.arraycopy(buf2, 0, characters, p, buf2.length);
    }

    public void insert(int offset, char[] buf1, char[] buf2, char[] buf3)
    {
        size += shiftCapacity(offset, buf1.length + buf2.length + buf3.length);

        int p = offset;

        System.arraycopy(buf1, 0, characters, p, buf1.length);
        p += buf1.length;

        System.arraycopy(buf2, 0, characters, p, buf2.length);
        p += buf2.length;

        System.arraycopy(buf3, 0, characters, p, buf3.length);
    }

    public void insert(int offset, char[] buf1, char char1)
    {
        size += shiftCapacity(offset, buf1.length + 1);

        int p = offset;

        System.arraycopy(buf1, 0, characters, p, buf1.length);
        p += buf1.length;

        characters[p] = char1;
    }

    public void insert(int offset, char buf[], int bufOffset, int length)
    {
        size += shiftCapacity(offset, length);

        System.arraycopy(buf, bufOffset, characters, offset, length);
    }

    public void insert(int offset, char[] buf1, char char1, char[] buf2, char char2)
    {
        size += shiftCapacity(offset, buf1.length + buf2.length + 2);

        int p = offset;

        System.arraycopy(buf1, 0, characters, p, buf1.length);
        p += buf1.length;

        characters[p++] = char1;

        System.arraycopy(buf2, 0, characters, p, buf2.length);
        p += buf2.length;

        characters[p] = char2;
    }

    public void insert(int offset, char[] buf1, char char1, char char2, char char3)
    {
        size += shiftCapacity(offset, buf1.length + 3);

        int p = offset;

        System.arraycopy(buf1, 0, characters, p, buf1.length);
        p += buf1.length;

        characters[p++] = char1;
        characters[p++] = char2;
        characters[p]   = char3;
    }

    public void insert(int offset, char[] buf1, char char1, String str2, char char2)
    {
        size += shiftCapacity(offset, buf1.length + str2.length() + 2);

        int p = offset;

        System.arraycopy(buf1, 0, characters, p, buf1.length);
        p += buf1.length;

        characters[p++] = char1;

        p += StringHelper.copyStringToCharArray(characters, p, str2);

        characters[p] = char2;
    }

    public void insert(int offset, char[] buf1, char char1, int number, char char2)
    {
        size += shiftCapacity(offset, buf1.length + IntegerHelper.countDigits(number) + 2);

        int p = offset;

        System.arraycopy(buf1, 0, characters, p, buf1.length);
        p += buf1.length;

        characters[p++] = char1;

        p += appendInt(p, number);

        characters[p] = char2;
    }

    public void insert(int offset, String str1, char char1)
    {
        int length = str1.length();

        size += shiftCapacity(offset, length + 1);

        int p = offset;

        StringHelper.copyStringToCharArray(characters, p, str1);
        p += length;

        characters[p] = char1;
    }

    public void insert(int offset, String str1, char char1, char[] buf2, char char2)
    {
        size += shiftCapacity(offset, str1.length() + buf2.length + 2);

        int p = offset;

        p += StringHelper.copyStringToCharArray(characters, p, str1);

        characters[p++] = char1;

        System.arraycopy(buf2, 0, characters, p, buf2.length);
        p += buf2.length;

        characters[p] = char2;
    }

    public void insert(int offset, String str1, char char1, String str2, char char2)
    {
        size += shiftCapacity(offset, str1.length() + str2.length() + 2);

        int p = offset;

        p += StringHelper.copyStringToCharArray(characters, p, str1);

        characters[p++] = char1;

        p += StringHelper.copyStringToCharArray(characters, p, str2);

        characters[p] = char2;
    }

    public void insert(int offset, String str1, char char1, int number, char char2)
    {
        if (number < 0)
        size += shiftCapacity(offset, str1.length() + IntegerHelper.countDigits(number) + 2);

        int p = offset;

        p += StringHelper.copyStringToCharArray(characters, p, str1);

        characters[p++] = char1;

        p += appendInt(p, number);

        characters[p] = char2;
    }

    public void insert(int offset, String str)
    {
        size += shiftCapacity(offset, str.length());

        StringHelper.copyStringToCharArray(characters, offset, str);
    }

    public void insert(int offset, String str, int strOffset, int length)
    {
        size += shiftCapacity(offset, length);

        StringHelper.copyStringToCharArray(characters, offset, str, strOffset, length);
    }

    public void insert(int offset, String str1, String str2)
    {
        size += shiftCapacity(offset, str1.length() + str2.length());

        int p = offset;

        StringHelper.copyStringToCharArray(characters, p, str1, str1.length());
        p += str1.length();

        StringHelper.copyStringToCharArray(characters, p, str2, str2.length());
    }

    public void insert(int offset, String str1, String str2, String str3)
    {
        size += shiftCapacity(offset, str1.length() + str2.length() + str3.length());

        int p = offset;

        p += StringHelper.copyStringToCharArray(characters, p, str1);
        p += StringHelper.copyStringToCharArray(characters, p, str2);
        StringHelper.copyStringToCharArray(characters, p, str3);
    }

    public void insert(int offset, String str1, String str2, String str3, String str4)
    {
        size += shiftCapacity(offset, str1.length() + str2.length() + str3.length() + str4.length());

        int p = offset;

        p += StringHelper.copyStringToCharArray(characters, p, str1);
        p += StringHelper.copyStringToCharArray(characters, p, str2);
        p += StringHelper.copyStringToCharArray(characters, p, str3);
        StringHelper.copyStringToCharArray(characters, p, str4);
    }

    public void insert(int offset, FastCharacterWriter fastCharacterWriter)
    {
        if (fastCharacterWriter == null || fastCharacterWriter.size == 0)
        {
            return;
        }

        shiftCapacity(offset, fastCharacterWriter.size);

        System.arraycopy(fastCharacterWriter.characters, 0, characters, offset, fastCharacterWriter.size);

        size += fastCharacterWriter.size;
    }

    public void replace(int offset, int length, String with)
    {
        if (offset >= size)
        {
            throw new ArrayIndexOutOfBoundsException("replace() offset(" + offset + ") is greater than size (" + size + ")");
        }

        if (length > size)
        {
            throw new ArrayIndexOutOfBoundsException("replace() length(" + length + ") is greater than size (" + size + ")");
        }

        if (offset + length > size)
        {
            throw new ArrayIndexOutOfBoundsException("replace() offset+length(" + (offset+length) + ") is greater than size (" + size + ")");
        }

        if (length > with.length())
        {
            throw new ArrayIndexOutOfBoundsException("replace() passed in array is lesser size(" + with.length() + ") than requested to copy(" + length + ")");
        }

        StringHelper.copyStringToCharArray(characters, offset, with, length);
    }

    public void replace(int offset, int length, char[] with)
    {
        if (offset >= size)
        {
            throw new ArrayIndexOutOfBoundsException("replace() offset(" + offset + ") is greater than size (" + size + ")");
        }

        if (length > size)
        {
            throw new ArrayIndexOutOfBoundsException("replace() length(" + length + ") is greater than size (" + size + ")");
        }

        if (offset + length > size)
        {
            throw new ArrayIndexOutOfBoundsException("replace() offset+length(" + (offset+length) + ") is greater than size (" + size + ")");
        }

        if (length > with.length)
        {
            throw new ArrayIndexOutOfBoundsException("replace() passed in array is lesser size(" + with.length + ") than requested to copy(" + length + ")");
        }

        System.arraycopy(with, 0, characters, offset, length);
    }

    public void replace(int offset, int length, char[] with, int withOffset)
    {
        if (offset >= size)
        {
            throw new ArrayIndexOutOfBoundsException("replace() offset(" + offset + ") is greater than size (" + size + ")");
        }

        if (length > size)
        {
            throw new ArrayIndexOutOfBoundsException("replace() length(" + length + ") is greater than size (" + size + ")");
        }

        if (offset + length > size)
        {
            throw new ArrayIndexOutOfBoundsException("replace() offset+length(" + (offset+length) + ") is greater than size (" + size + ")");
        }

        if (length > with.length - withOffset)
        {
            throw new ArrayIndexOutOfBoundsException("replace() passed in array is lesser size(" + (with.length - withOffset) + ") than requested to copy(" + length + ")");
        }

        System.arraycopy(with, withOffset, characters, offset, length);
    }

    public void write(OutputStream ostream) throws Exception
    {
        for (int i = 0; i < size; i++)
        {
            ostream.write((int) characters[i]);
        }
    }

    public Object clone()
    {
        return new FastCharacterWriter(this);
    }

    private void ensureCapacity(int additional)
    {
        if (size + additional >= characters.length)
        {
            characters = CollectionHelper.arrayclone(characters, 0, size, Math.max(size + additional, size << 1));
        }
    }

    private int shiftCapacity(int offset, int additional)
    {
        if (size + additional >= characters.length)
        {
            char[] temp = new char[Math.max(size + additional, size << 1)];
            if (offset == 0)
            {
                System.arraycopy(characters, 0, temp, additional, size);
            }
            else
            {
                System.arraycopy(characters, 0,      temp, 0,                   offset);
                System.arraycopy(characters, offset, temp, offset + additional, size - offset);
            }

            characters = temp;
        }
        else
        {
            System.arraycopy(characters, offset, characters, offset + additional, size - offset);
        }

        return additional;
    }

    private int writeInto(int offset, char ch)
    {
        characters[offset] = ch;

        return offset + 1;
    }

    private int writeInto(int offset, char[] buf1)
    {
        System.arraycopy(buf1, 0, characters, offset, buf1.length);

        return offset + buf1.length;
    }

    private int writeInto(int offset, char[] buf1, char[] buf2)
    {
        System.arraycopy(buf1, 0, characters, offset, buf1.length);
        offset += buf1.length;
        System.arraycopy(buf2, 0, characters, offset, buf2.length);
        offset += buf2.length;

        return offset;
    }

    private int writeInto(int offset, char[] buf1, char[] buf2, char[] buf3)
    {
        System.arraycopy(buf1, 0, characters, offset, buf1.length);
        offset += buf1.length;
        System.arraycopy(buf2, 0, characters, offset, buf2.length);
        offset += buf2.length;
        System.arraycopy(buf3, 0, characters, offset, buf3.length);
        offset += buf3.length;

        return offset;
    }

    private int writeInto(int offset, char[] buf1, char[] buf2, char[] buf3, char[] buf4)
    {
        System.arraycopy(buf1, 0, characters, offset, buf1.length);
        offset += buf1.length;
        System.arraycopy(buf2, 0, characters, offset, buf2.length);
        offset += buf2.length;
        System.arraycopy(buf3, 0, characters, offset, buf3.length);
        offset += buf3.length;
        System.arraycopy(buf4, 0, characters, offset, buf4.length);
        offset += buf4.length;

        return offset;
    }

    private int appendInt(int offset, int number)
    {
        if (number == Integer.MIN_VALUE)
        {
            writeInto(offset, INTEGER_MIN_STRING.toCharArray());
            return INTEGER_MIN_STRING.length();
        }

        boolean minus = number < 0;
        int oldOffset = offset;

        if (minus) // if negative, just add a minus sign
        {
           number = -number;
           offset = writeInto(offset, '-');
        }

        // see if the number is already pre-defined in the lookup table
        if (number < StringHelper.numbersChars.length)
        {
            offset = writeInto(offset, StringHelper.numbersChars[number]);
        }
        else if (number < StringHelper.NUMBER_1_000_000) // 123,456
        {
            offset = writeInto(offset, StringHelper.trimNumberCharsThousand_x_xxx_000_xxx(number), StringHelper.zeroNumberCharsThousand_x_xxx_xxx_000(number));
        }
        else if (number < StringHelper.NUMBER_1_000_000_000) // 123,456,789
        {
            offset = writeInto(offset, StringHelper.trimNumberCharsThousand_x_000_xxx_xxx(number), StringHelper.zeroNumberCharsThousand_x_xxx_000_xxx(number), StringHelper.zeroNumberCharsThousand_x_xxx_xxx_000(number));
        }
        else // 1,234,567,890
        {
            offset = writeInto(offset, StringHelper.trimNumberCharsThousand_0_xxx_xxx_xxx(number), StringHelper.zeroNumberCharsThousand_x_000_xxx_xxx(number), StringHelper.zeroNumberCharsThousand_x_xxx_000_xxx(number), StringHelper.zeroNumberCharsThousand_x_xxx_xxx_000(number));
        }

        return offset - oldOffset;
    }
}