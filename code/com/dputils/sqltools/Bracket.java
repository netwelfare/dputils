package com.dputils.sqltools;

public class Bracket extends SqlElement
{
	public Bracket(String type)
	{
		switch (type)
		{
			case "left":
				this.literal = "(";
				break;
			case "right":
				this.literal = ")";
				break;
		}

	}

}
