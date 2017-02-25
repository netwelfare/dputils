package com.dputils.hash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHash<T>
{

	/**
	 * ��ϣ����
	 */
	private final HashFunction hashFunction;

	/**
	 * ����ڵ��� �� Խ��ֲ�Խ���⣬��Խ���ڳ�ʼ���ͱ����ʱ��Ч�ʲ�һ�㡣 �����У�����200�����;����ˡ�
	 */
	private final int numberOfReplicas;

	/**
	 * ����Hash�ռ�
	 */
	private final SortedMap<Integer, T> circle = new TreeMap<Integer, T>();

	/**
	 * @param hashFunction
	 *            ����ϣ����
	 * @param numberOfReplicas
	 *            �����������ϵ��
	 * @param nodes
	 *            ���������ڵ�
	 */
	public ConsistentHash(HashFunction hashFunction, int numberOfReplicas, Collection<T> nodes)
	{
		this.hashFunction = hashFunction;
		this.numberOfReplicas = numberOfReplicas;

		for (T node : nodes)
		{
			this.addNode(node);
		}
	}

	/**
	 * �������ڵ㣬ÿ��node �����numberOfReplicas������ڵ㣬��Щ����ڵ��Ӧ��ʵ�ʽڵ���node
	 */
	public void addNode(T node)
	{
		for (int i = 0; i < numberOfReplicas; i++)
		{
			int hashValue = hashFunction.hash(node.toString() + i);
			circle.put(hashValue, node);
		}
	}

	/**�Ƴ�����ڵ㣬��node������numberOfReplicas������ڵ�ȫ���Ƴ�
	 * @param node
	 */
	public void removeNode(T node)
	{
		for (int i = 0; i < numberOfReplicas; i++)
		{
			int hashValue = hashFunction.hash(node.toString() + i);
			circle.remove(hashValue);
		}
	}

	/**
	 * �õ�ӳ�������ڵ�
	 * 
	 * @param key
	 * @return
	 */
	public T getNode(Object key)
	{
		if (circle.isEmpty())
		{
			return null;
		}
		int hashValue = hashFunction.hash(key);
		//		System.out.println("key---" + key + " : hash---" + hash);
		if (!circle.containsKey(hashValue))
		{
			// ���ؼ����ڻ����hash��node�����ػ���˳ʱ���ҵ�һ������ڵ�
			SortedMap<Integer, T> tailMap = circle.tailMap(hashValue);
			// System.out.println(tailMap);
			// System.out.println(circle.firstKey());
			hashValue = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
		}
		//		System.out.println("hash---: " + hash);
		return circle.get(hashValue);
	}

	static class HashFunction
	{
		/**
		 * MurMurHash�㷨���ǷǼ���HASH�㷨�����ܸܺߣ�
		 * �ȴ�ͳ��CRC32,MD5��SHA-1���������㷨���Ǽ���HASH�㷨�����Ӷȱ���ͺܸߣ������������ϵ���Ҳ���ɱ��⣩
		 * ��HASH�㷨Ҫ��ܶ࣬���Ҿ�˵����㷨����ײ�ʺܵ�. http://murmurhash.googlepages.com/
		 */
		int hash(Object key)
		{
			ByteBuffer buf = ByteBuffer.wrap(key.toString().getBytes());
			int seed = 0x1234ABCD;

			ByteOrder byteOrder = buf.order();
			buf.order(ByteOrder.LITTLE_ENDIAN);

			long m = 0xc6a4a7935bd1e995L;
			int r = 47;

			long h = seed ^ (buf.remaining() * m);

			long k;
			while (buf.remaining() >= 8)
			{
				k = buf.getLong();

				k *= m;
				k ^= k >>> r;
				k *= m;

				h ^= k;
				h *= m;
			}

			if (buf.remaining() > 0)
			{
				ByteBuffer finish = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
				finish.put(buf).rewind();
				h ^= finish.getLong();
				h *= m;
			}

			h ^= h >>> r;
			h *= m;
			h ^= h >>> r;
			buf.order(byteOrder);
			return (int) h;
		}
	}
}
