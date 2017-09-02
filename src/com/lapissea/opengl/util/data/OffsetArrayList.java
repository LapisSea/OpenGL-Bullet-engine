package com.lapissea.opengl.util.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Predicate;

import com.lapissea.util.LogUtil;
import com.lapissea.util.UtilL;

public class OffsetArrayList<T> implements List<T>{
	
	@SuppressWarnings("unchecked")
	private class LIter implements ListIterator<T>{
		
		int start,end,endOffset,cursor;
		
		public LIter(int start, int end){
			start-=offset;
			end-=offset;
			this.start=start;
			endOffset=end-data.length;
			end();
		}
		
		private void end(){
			end=endOffset+data.length;
		}
		
		@Override
		public boolean hasNext(){
			return cursor!=end;
		}
		
		@Override
		public T next(){
			return (T)data[cursor++];
		}
		
		@Override
		public void remove(){
			if(cursor<=0) throw new IllegalStateException();
			OffsetArrayList.this.remove(--cursor+offset);
			end();
		}
		
		@Override
		public boolean hasPrevious(){
			return cursor!=start;
		}
		
		@Override
		public T previous(){
			return (T)data[cursor++];
		}
		
		@Override
		public int nextIndex(){
			return cursor+offset;
		}
		
		@Override
		public int previousIndex(){
			return cursor+offset-2;
		}
		
		@Override
		public void set(T e){
			OffsetArrayList.this.set(cursor+offset-1, e);
			end();
		}
		
		@Override
		public void add(T e){
			OffsetArrayList.this.add(e);
			end();
		}
	}
	
	@SuppressWarnings("unchecked")
	private class Iter implements Iterator<T>{
		
		int	cursor;
		int	localMods	=mods;
		
		protected void protecc(int localMods){
			if(localMods!=mods) throw new ConcurrentModificationException();
		}
		
		@Override
		public boolean hasNext(){
			return cursor<size;
		}
		
		@Override
		public T next(){
			protecc(localMods);
			try{
				return (T)data[cursor++];
			}catch(Throwable e){
				LogUtil.println(size);
				throw UtilL.uncheckedThrow(e);
			}
		}
		
		@Override
		public void remove(){
			protecc(localMods);
			if(cursor<=0) throw new IllegalStateException();
			OffsetArrayList.this.remove(--cursor+offset);
			localMods=mods;
		}
	}
	
	private int			size;
	private int			offset;
	private int			mods;
	private Object[]	data;
	
	public OffsetArrayList(){
		this(16);
	}
	
	public OffsetArrayList(int initalSize){
		data=new Object[initalSize];
	}
	
	protected void protecc(int modNow){
		if(modNow!=mods) throw new ConcurrentModificationException();
	}
	
	private void normalize(){
		int tailRemove=0;
		
		while(tailRemove<size){
			if(data[tailRemove]!=null) break;
			tailRemove++;
		}
		
		if(tailRemove==size){
			while(size>0){
				data[--size]=null;
			}
			size=0;
			return;
		}
		if(tailRemove>0){
			int newSiz=size-tailRemove;
			System.arraycopy(data, tailRemove, data, 0, newSiz);
			for(int i=newSiz;i<size;i++){
				data[i]=null;
			}
			size=newSiz;
			offset+=tailRemove;
		}
		
		int headRemove=size;
		while(headRemove>=0){
			if(data[--headRemove]!=null){
				headRemove++;
				break;
			}
		}
		
		headRemove=size-headRemove;
		if(headRemove==size){
			while(size>0){
				data[--size]=null;
			}
			size=0;
			return;
		}
		size-=headRemove;
	}
	
	private void expandHead(int ammount){
		data=Arrays.copyOf(data, data.length+ammount);
		size=data.length;
	}
	
	private void expandTail(int ammount){
		offset-=ammount;
		
		if(size+ammount<=data.length){//if head space can contain expansion than just shit all data
			System.arraycopy(data, 0, data, ammount, size);
			size+=ammount;
			while(ammount>0){// give gc data
				data[--ammount]=null;
			}
		}else{//need to create new bigger array
			Object[] old=data;
			data=new Object[size+ammount];
			System.arraycopy(old, 0, data, ammount, size);
			size+=ammount;
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T set(int index, T element){
		
		if(element==null){
			index-=offset;
			if(index<0||index>=size) return null;
			
			int modNow=++mods;
			T replaced=(T)data[index];
			data[index]=null;
			protecc(modNow);
			return replaced;
		}
		
		int modNow=++mods;
		if(size==0) offset=index;
		
		index-=offset;
		
		if(element!=null){
			if(index<0){
				expandTail(-index);
				index=0;
			}else if(index>=size){
				if(index>=data.length){
					int ammount=index-data.length+1;
					expandHead(ammount);
				}else size=index+1;
			}
		}
		
		T replaced=(T)data[index];
		data[index]=element;
		
		normalize();
		protecc(modNow);
		return replaced;
	}
	
	@Override
	public boolean add(T element){
		if(element==null) return false;
		add(size+offset, element);
		return true;
	}
	
	@Override
	public void add(int index, T element){
		if(element==null) return;
		
		index-=offset;
		if(index<0||index>=size){
			set(index+offset, element);
			return;
		}
		int modNow=++mods;
		if(data[index]==null){
			data[index]=element;
			protecc(modNow);
			return;
		}
		boolean exp=size==data.length;
		if(exp) expandHead(1);
		
		System.arraycopy(data, index, data, index+1, size-index);
		data[index]=element;
		protecc(modNow);
		if(!exp) size++;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T remove(int index){
		int modNow=++mods;
		
		index-=offset;
		T t=(T)data[index];
		data[index]=null;
		if(index==0||index==size-1) normalize();
		
		protecc(modNow);
		return t;
	}
	
	@Override
	public boolean remove(Object o){
		int id=indexOf(o);
		if(id!=-1){
			remove(id);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeAll(Collection<?> c){
		int oldSize=size;
		for(Object o:c){
			remove(o);
		}
		return oldSize!=size;
	}
	
	@Override
	public boolean addAll(Collection<? extends T> c){
		int oldSize=size;
		for(T o:c){
			add(o);
		}
		return oldSize!=size;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends T> c){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean retainAll(Collection<?> c){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int size(){
		return size;
	}
	
	public boolean hasData(){
		return size()>0;
	}
	
	@Override
	public boolean isEmpty(){
		return size()==0;
	}
	
	@Override
	public boolean contains(Object o){
		if(o==null) return false;
		else{
			for(Object e:data){
				if(e!=null&&e.equals(o)) return true;
			}
		}
		return false;
	}
	
	@Override
	public Iterator<T> iterator(){
		return new Iter();
	}
	
	@Override
	public Object[] toArray(){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <L> L[] toArray(L[] a){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean containsAll(Collection<?> c){
		for(Object o:c){
			if(!contains(o)) return false;
		}
		return true;
	}
	
	@Override
	public void clear(){
		int modNow=++mods;
		while(size>0){
			data[--size]=null;
		}
		protecc(modNow);
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T get(int index){
		index-=offset;
		if(index<0||index>=size) return null;
		return (T)data[index];
	}
	
	@Override
	public int indexOf(Object o){
		if(o==null) return -1;
		
		for(int i=0;i<size;i++){
			if(data[i]!=null&&data[i].equals(o)) return i+offset;
		}
		return -1;
	}
	
	@Override
	public int lastIndexOf(Object o){
		if(o==null) return -1;
		
		for(int i=size-1;i>=0;i--){
			if(data[i]!=null&&data[i].equals(o)) return i+offset;
		}
		return -1;
	}
	
	@Override
	public ListIterator<T> listIterator(){
		return listIterator(offset);
	}
	
	@Override
	public ListIterator<T> listIterator(int index){
		return new LIter(index, size+offset);
	}
	
	@Override
	public List<T> subList(int fromIndex, int toIndex){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String toString(){
		StringBuilder b=new StringBuilder("OffsetArray{size=").append(size).append(", data=[ ");
		for(int i=0;i<size;i++){
			b.append(i+offset).append("=").append(UtilL.toString(data[i])).append(" ");
		}
		return b.append("]}").toString();
	}
	
	@Override
	public boolean removeIf(Predicate<? super T> filter){
		Objects.requireNonNull(filter);
		boolean removed=false;
		Iterator<T> i=iterator();
		while(i.hasNext()){
			T t=i.next();
			if(t!=null&&filter.test(t)){
				i.remove();
				removed=true;
			}
		}
		return removed;
	}
	
	public int getFirst() {
		return offset;
	}
}
