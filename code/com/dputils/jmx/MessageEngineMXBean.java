package com.dputils.jmx;

public interface MessageEngineMXBean
{
	//��������
	public void stop();

	//�鿴�����Ƿ���ͣ��
	public boolean isPaused();

	//��ͣ������߼�������
	public void pause(boolean pause);

	public Message getMessage();

	//�޸�message
	public void changeMessage(Message m);
}
