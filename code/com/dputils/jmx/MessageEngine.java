package com.dputils.jmx;

public class MessageEngine implements MessageEngineMXBean
{
	private final Message message = Echo.msg;

	@Override
	public void stop()
	{
		Echo.running = false;
	}

	@Override
	public boolean isPaused()
	{
		return Echo.pause;
	}

	@Override
	public void pause(boolean pause)
	{
		Echo.pause = pause;
	}

	@Override
	public Message getMessage()
	{
		return this.message;
	}

	@Override
	public void changeMessage(Message m)
	{
		this.message.setBody(m.getBody());
		this.message.setTitle(m.getTitle());
		this.message.setBy(m.getBy());
	}
}
