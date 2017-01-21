package com.dputils.lang;

public class StringUtils
{
	public static final String EMPTY = "";

	public StringUtils()
	{
		// TODO Auto-generated constructor stub
	}

	public static boolean isEmpty(final CharSequence cs)
	{
		return cs == null || cs.length() == 0;
	}

	public static boolean isNotEmpty(final CharSequence cs)
	{
		return !isEmpty(cs);
	}

	public static boolean isBlank(final CharSequence cs)
	{
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0)
		{
			return true;
		}
		for (int i = 0; i < strLen; i++)
		{
			if (Character.isWhitespace(cs.charAt(i)) == false)
			{
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(final CharSequence cs)
	{
		return !isBlank(cs);
	}

	public static String trim(final String str)
	{
		return str == null ? null : str.trim();
	}

	public static String trimToNull(final String str)
	{
		final String ts = trim(str);
		return isEmpty(ts) ? null : ts;
	}

	public static String trimToEmpty(final String str)
	{
		return str == null ? EMPTY : str.trim();
	}

	public static String truncate(final String str, int offset, int maxWidth)
	{
		if (offset < 0)
		{
			throw new IllegalArgumentException("offset cannot be negative");
		}
		if (maxWidth < 0)
		{
			throw new IllegalArgumentException("maxWith cannot be negative");
		}
		if (str == null)
		{
			return null;
		}
		if (offset > str.length())
		{
			return EMPTY;
		}
		if (str.length() > maxWidth)
		{
			int ix = offset + maxWidth > str.length() ? str.length() : offset + maxWidth;
			return str.substring(offset, ix);
		}
		return str.substring(offset);
	}
}
