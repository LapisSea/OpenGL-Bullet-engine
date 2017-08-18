package com.lapissea.opengl.program.util;

import java.util.Iterator;

public class SegmentedIterator<T> implements Iterator<T>{
	
	private int	pos,step;
	private T[]	data;
	
	public SegmentedIterator(T[] data, int start, int step){
		pos=start;
		this.step=step;
		this.data=data;
	}
	
	@Override
	public boolean hasNext(){
		return data.length>pos+step;
	}
	
	@Override
	public T next(){
		return data[pos+=step];
	}
	
};