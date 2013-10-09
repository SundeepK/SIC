package com.sun.imageloader.cache.api;

import java.util.Collection;

public interface MemoryCache<K, V> {

	/**
	 * 
	 * Insert a key value pair into the cache
	 * 
	 * @param key
	 * 			key to put into the cache
	 * @param value
	 * 			value associated to the key
	 * @return
	 * 			returns true if insertion was successful
	 */
	public	boolean put(K key, V value);
	
	/**
	 * 
	 * Get the value associated with the key
	 * 
	 * @param key
	 * 			key used to get the value associated with it
	 * @return
	 * 			the value associated with the key
	 */
	public V getValue(K key);
	
	
	/**
	 * 
	 * Remove the map entry from the cache
	 * 
	 * @param key
	 * 			The key associated with the value
	 * @param removeAllOccurences
	 * 			if all occurrences should be removed from the internal soft and hard cache
	 */
	public void remove(K key, boolean removeAllOccurences);

	/**
	 * 
	 * clear the internal caches
	 */
	public void clear();	
	
	/**
	 * 
	 * Get a Collection of keys from the internal caches
	 * 
	 * @return
	 * 		a collection of keys of the internal caches
	 */
	public Collection<K> getKeys();
	
	
}
