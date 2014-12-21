package com.cboe.domain.util;

import com.cboe.interfaces.domain.HistoryServiceIdGenerator;

public class HistoryServiceIdGeneratorConstantIdImpl implements HistoryServiceIdGenerator
{
	private final Long id = new Long (1);

	public Object getId()
	{
		return id;
	}

}
