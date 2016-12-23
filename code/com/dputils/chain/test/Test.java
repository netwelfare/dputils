package com.dputils.chain.test;

import com.dputils.chain.impl.CatalogBase;
import com.dputils.chain.impl.ChainBase;

public class Test
{

	public static void main(String[] args)
	{
		CatalogBase catalogBase = new CatalogBase();
		ChainBase chainBase = new ChainBase();
		catalogBase.addCommand("", chainBase);
	}

}
