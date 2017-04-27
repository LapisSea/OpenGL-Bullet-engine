package com.lapissea.opengl.program.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class MapOfLists<K,V>extends HashMap<K,List<V>>{
	
	public boolean add(K key, V value){
		List<V> l=get(key);
		if(l==null) put(key, l=new ArrayList<>());
		
		return l.add(value);
	}
	
	public void forEach(K key, Consumer<V> action){
		List<V> l=get(key);
		if(l==null) return;
		l.stream().forEach(action);
	}
	
}
