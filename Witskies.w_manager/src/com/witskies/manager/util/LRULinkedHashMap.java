package com.witskies.manager.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 类说明：利用LinkedHashMap实现简单的缓存， 必须实现removeEldestEntry方法
 * 
 * @param <K>
 * @param <V>
 */
public class LRULinkedHashMap<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 3686850826084638521L;

	private final int maxCapacity;

	private static final float DEFAULT_LOAD_FACTOR = 0.75f;

	private final Lock lock = new ReentrantLock();

	public LRULinkedHashMap(int maxCapacity) {
		super(maxCapacity, DEFAULT_LOAD_FACTOR, true);
		this.maxCapacity = maxCapacity;
	}

	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > maxCapacity;
	}

	public boolean containsKey(Object key) {
		try {
			lock.lock();
			return super.containsKey(key);
		} finally {
			lock.unlock();
		}
	}

	public V get(Object key) {
		try {
			lock.lock();
			return super.get(key);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 根据其索引值返回VALUE
	 * 
	 * @param index
	 * @return
	 */
	public V getValueByIndex(int index) {
		if (index >= this.size())
			return null;
		Iterator<Entry<K, V>> iter = this.entrySet().iterator();
		int i = 0;
		while (iter.hasNext()) {
			V value = iter.next().getValue();
			if (i++ == index) {
				return value;
			}
		}
		return null;
	}

	/**
	 * 把HASH转为LIST结构
	 * 
	 * @return
	 */
	public List<V> toList() {
		Iterator<Entry<K, V>> iter = this.entrySet().iterator();
		List<V> list = new ArrayList<V>();
		while (iter.hasNext()) {
			V value = iter.next().getValue();
			list.add(value);
		}
		return list;
	}

	public V put(K key, V value) {
		try {
			lock.lock();
			return super.put(key, value);
		} finally {
			lock.unlock();
		}
	}

	public int size() {
		try {
			lock.lock();
			return super.size();
		} finally {
			lock.unlock();
		}
	}

	public void clear() {
		try {
			lock.lock();
			super.clear();
		} finally {
			lock.unlock();
		}
	}

	public Collection<Map.Entry<K, V>> getAll() {
		try {
			lock.lock();
			return new ArrayList<Map.Entry<K, V>>(super.entrySet());
		} finally {
			lock.unlock();
		}
	}
}