package com.lapissea.opengl.program.util;

import java.util.ArrayDeque;
import java.util.function.Supplier;

public final class Pool<T>{
	
	private final ArrayDeque<T>	objects	=new ArrayDeque<>();
	private final Supplier<T>	factory;
	
	public Pool(Supplier<T> factory){
		this.factory=factory;
	}
	
	public synchronized T borrow(){
		T obj=objects.poll();
		if(obj==null) return factory.get();
		return obj;
	}
	
	public synchronized void giveBack(T object){
		objects.push(object);
	}
}
