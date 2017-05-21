package com.lapissea.opengl.program.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PrefixTree<T>{
	
	private class Node{
		
		final String			namePart;
		final ArrayList<Node>	children=new ArrayList<>(1);
		final int				l;
		final Node				parent;
		boolean					hasValue;
		
		T value;
		
		Node(Node parent, String namePart, T value){
			this(parent, namePart);
			setValue(value);
		}
		
		
		public void setValue(T value){
			hasValue=true;
			this.value=value;
		}
		
		Node(Node parent, String namePart){
			this.namePart=namePart;
			l=namePart.length();
			this.parent=parent;
		}
		
		T get(String name, int start){
			if(!match(name, start)) return null;
			if(name.length()-start-l==0) return value;//end reached
			
			return askCh(name, start);
		}
		
		T askCh(String name, int start){
			for(Node node:children){
				T result=node.get(name, start+l);
				if(result!=null) return result;
			}
			return null;
		}
		
		boolean match(String name, int start){
			return name.regionMatches(start, namePart, 0, l);
		}
		
		//returns false if it can not put value in itself of any of it's children
		public boolean put(String name, int start, T value){
			// check if part of string to be set equals at current element position (does "java." match at position 4 for string "com.java.fooobar"
			if(!match(name, start)){
				
				// check if requested string matches at start with element (com.foo.bar does not match com.foo.foobar but com.foo. do match)
				int count=0;
				for(int i=0, j=Math.min(start+l, name.length())-start;i<j;i++){
					if(name.charAt(i+start)!=namePart.charAt(i)) break;
					count++;
				}
				//nothing matching whatsoever? send false to signal that value has to be set in parent element
				if(count==0) return false;
				
				//split existing and value to be set in to 1 new parent element and 2 childs ("com.foo.bar"(existing) and "com.foo.foobar" to "com.foo." (as common parent) and "bar", "foobar")
				String match=namePart.substring(0, count);
				parent.children.remove(this);
				Node matchNode=new Node(parent, match);
				parent.children.add(matchNode);
				
				Node thisNode;//clone data from this node
				if(hasValue) thisNode=new Node(matchNode, namePart.substring(count), this.value);
				else thisNode=new Node(matchNode, namePart.substring(count));
				thisNode.children.addAll(children);
				matchNode.children.add(thisNode);
				matchNode.children.add(new Node(matchNode, name.substring(start+count), value));
				
				return true;
			}
			if(name.length()-start-l==0){//whole string was tested and an existing match was found
				setValue(value);
				return true;
			}
			
			for(Node node:children){
				if(node.put(name, start+l, value)) return true;
			}
			
			children.add(new Node(this, name.substring(start+l), value));
			return true;
		}
		
		private void prf(StringBuilder pad, String head, StringBuilder b, boolean lastElement){
			if(!namePart.isEmpty()){
				int pos;
				
				if(head.length()>0) head=(lastElement?"\\":"|")+head.substring(1);
				String padFinal=pad.substring(0, pos=(pad.length()-head.length()));
				b.append(padFinal);
				b.append(head).append(namePart);
				if(value!=null) b.append(" = ").append(value);
				b.append("\n");
				
				head="";
				if(l==1) head+=">";
				else{
					for(int i=0;i<l-2;i++){
						head+="-";
					}
					head+="> ";
				}
				
				if(lastElement) pad.setCharAt(pos, ' ');
				pad.append('|');
				for(int i=0;i<l-1;i++){
					pad.append(' ');
				}
			}
			for(int i=0;i<children.size();i++){
				children.get(i).prf(pad, head, b, children.size()>1&&i+1==children.size());
			}
			if(!namePart.isEmpty()) pad.setLength(pad.length()-l);
			
		}
		
		void getStartMatches(String name, int start, Consumer<T> hook){
			int len=name.length(),end=start+l;
			if(start>=len||(end>len&&name.regionMatches(start, namePart, 0, Math.min(end, len)-start))){
				if(hasValue) hook.accept(value);
			}
			else if(!match(name, start)) return;
			for(Node node:children){
				node.getStartMatches(name, end, hook);
			}
		}
		
		void getStartMatchesReverse(String name, int start, Consumer<T> hook){
			int end=start+l,len=name.length();
			boolean match=name.regionMatches(start, namePart, 0, Math.min(len, end)-start);
			System.out.println(UtilM.stringFill(start, ' ')+namePart+"\n"+name+" "+(len>end)+" "+match);
			
			if(len>end&&match){
				if(hasValue) hook.accept(value);
			}
			else if(!match) return;
			
			for(Node node:children){
				node.getStartMatchesReverse(name, end, hook);
			}
		}
		
	}
	
	private Node root=new Node(null, ""){
		
		@Override
		T get(String name, int start){
			if(name.length()==0) return value;
			return askCh(name, start);
		}
		
		@Override
		void getStartMatches(String name, int start, Consumer<T> hook){
			for(Node node:children){
				node.getStartMatches(name, start, hook);
			}
		}
		
		@Override
		void getStartMatchesReverse(String name, int start, Consumer<T> hook){
			for(Node node:children){
				node.getStartMatches(name, start, hook);
			}
		}
		
	};
	
	public List<T> getStartMatches(String name, List<T> dest){
		getStartMatches(name, dest::add);
		return dest;
	}
	
	public void getStartMatches(String name, Consumer<T> hook){
		root.getStartMatches(name, 0, hook);
	}
	
	public List<T> getStartMatchesReverse(String name, List<T> dest){
		getStartMatchesReverse(name, dest::add);
		return dest;
	}
	
	public void getStartMatchesReverse(String name, Consumer<T> hook){
		root.getStartMatchesReverse(name, 0, hook);
	}
	
	public T get(String name){
		return root.get(name, 0);
	}
	
	public void put(String name, T value){
		synchronized(this){
			root.put(name, 0, value);
		}
	}
	
	@Override
	public String toString(){
		StringBuilder b=new StringBuilder();
		StringBuilder head=new StringBuilder();
		head.append(" ");
		root.prf(head, "", b, false);
		return b.toString();
	}
}
