package com.lapissea.opengl.program.util;

public class IntTree<T>{
	
	private class Node{
		
		int		key;
		T		value;
		Node	less,more;
		
		public Node(int key, T value){
			this.key=key;
			this.value=value;
		}
		
		public T get(int key){
			if(this.key==key) return value;
			
			Node child=key>this.key?more:less;
			if(child==null) return null;
			
			return child.get(key);
		}
		
		public void set(int key, T value){
			if(this.key==key) this.value=value;
			else if(key>this.key){
				if(more==null) more=new Node(key, value);
				else more.set(key, value);
			}
			else{
				if(less==null) less=new Node(key, value);
				else less.set(key, value);
			}
			
		}
		
	}
	
	private Node root;
	
	public void set(int key, T value){
		if(root==null) root=new Node(key, value);
		else root.set(key, value);
	}
	
	public T get(int key){
		if(root==null) return null;
		return root.get(key);
	}
	
	public void clear(){
		root=null;
	}
}
