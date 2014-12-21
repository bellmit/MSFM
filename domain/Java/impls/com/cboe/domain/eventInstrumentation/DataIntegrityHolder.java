package com.cboe.domain.eventInstrumentation;

import com.cboe.interfaces.domain.eventInstrumentation.EventInstrumentation;

public class DataIntegrityHolder {
    
    private String consumerHomeClassName;
    private String consumerName;
	private EventInstrumentation[] instrumentors;

	public DataIntegrityHolder(String name)
	{
		consumerName = name;
	}
	public String getConsumerName() {
		return consumerName;
	}
    public String getConsumerHomeClassName()
    {
        return consumerHomeClassName;
    }
	public void setConsumerName(String consumerName) {
		this.consumerName = consumerName;
	}
    public void setConsumerClassName(String consumerHomeClassName)
    {
        this.consumerHomeClassName = consumerHomeClassName;
    }
	public EventInstrumentation[] getInstrumentor() {
		return instrumentors;
	}
	public void setInstrumentor(EventInstrumentation[] messageCount) {
		this.instrumentors = messageCount;
	}
	public long getTotalMessageCount()
	{
		long total =0;
		for (EventInstrumentation count:instrumentors)
		{
			total +=count.getCount();
		}
		
		return total;
	}
	public void increment(int methodIndex)
	{
		if (methodIndex >= instrumentors.length)
		{
			return;
		}
		instrumentors[methodIndex].increment();
	}
	public String getAllMessageCountInfo()
	{
	    StringBuffer messageBuffer = new StringBuffer();
        long total =0;
        messageBuffer.append("ConsumerName[").append(this.consumerName).append("] ClassName[").append(this.consumerHomeClassName).append("]");
        return messageBuffer.toString();
	}
}
