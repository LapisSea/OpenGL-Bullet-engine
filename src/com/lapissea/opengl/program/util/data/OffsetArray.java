package com.lapissea.opengl.program.util.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.lapissea.util.UtilL;

@SuppressWarnings("unchecked")
public class OffsetArray<T> implements Iterable<T>{
	
	private List<T>	data=new ArrayList<>();
	private int		dataOffset;
	
	public T remove(int index){
		return set(index, null);
	}
	
	public T set(int index, T obj){
		if(data.isEmpty()){
			dataOffset=index;
			data.add(obj);
			return null;
		}
		if(index<dataOffset){
			if(obj==null) return null;
			expandTail(dataOffset-index, (Class<T>)obj.getClass());
		}
		
		index-=dataOffset;
		
		if(index>=data.size()){
			if(obj==null) return null;
			expandHead(index-data.size()+1, (Class<T>)obj.getClass());
		}
		
		T t=data.set(index, obj);
		
		trim();
		return t;
	}
	
	private void expandHead(int ammount, Class<T> t){
		if(ammount==1) data.add(null);
		else data.addAll(Arrays.asList(UtilL.array(t, ammount)));
	}
	
	private void expandTail(int ammount, Class<T> t){
		if(ammount==1) data.add(0, null);
		else data.addAll(0, Arrays.asList(UtilL.array(t, ammount)));
		dataOffset-=ammount;
	}
	
	private void trim(){
		if(data.isEmpty()) return;
		for(Iterator<T> i=data.iterator();i.hasNext();){
			if(i.next()==null) i.remove();
			else break;
		}
		if(data.isEmpty()) return;
		for(ListIterator<T> i=data.listIterator(data.size());i.hasPrevious();){
			if(i.previous()==null) i.remove();
			else break;
		}
	}
	
	public T get(int index){
		index-=dataOffset;
		return index>=data.size()||index<0?null:data.get(index);
	}
	
	@Override
	public String toString(){
		return data.toString();
	}
	
	private class Iter implements Iterator<T>{
		
		T	next;
		int	cursor;
		
		public Iter(){
			calcNext();
		}
		
		private void calcNext(){
			next=null;
			while(next==null&&data.size()!=cursor)
				next=data.get(cursor++);
		}
		
		@Override
		public boolean hasNext(){
			return next!=null;
		}
		
		@Override
		public T next(){
			T now=next;
			calcNext();
			return now;
		}
		
	};
	
	@Override
	public Iterator<T> iterator(){
		return new Iter();
	}
	
	public boolean isEmpty(){
		return data.isEmpty();
	}
}
