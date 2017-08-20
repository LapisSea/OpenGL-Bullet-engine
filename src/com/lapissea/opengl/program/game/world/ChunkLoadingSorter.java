package com.lapissea.opengl.program.game.world;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.lwjgl.util.vector.Vector3f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.game.terrain.Chunk;
import com.lapissea.opengl.program.rendering.Camera;
import com.lapissea.opengl.program.util.math.vec.Vec2i;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class ChunkLoadingSorter{
	
	protected class Node implements Comparable<Node>{
		
		final Chunk	vec;
		double		distance=0;
		double		dot		=1;
		double		lazyness=0;
		Vec3f		rot		=new Vec3f();
		
		Node(Chunk ch){
			vec=ch;
		}
		
		void calcDot(){
			dot=Math.pow(1+(1+Vector3f.dot(rot, camDir))/2, 2);
			lazyness=distance*dot;
		}
		
		void calc(){
			distance=vec.pos.distanceTo(pos);
			rot.set(camPos).sub(vec.spacePos);
			rot.scale(1/rot.length());
			
			calcDot();
		}
		
		@Override
		public int compareTo(Node o){
			return Double.compare(o.lazyness, lazyness);
		}
		
		@Override
		public boolean equals(Object obj){
			return vec.pos.equals(((Node)obj).vec.pos);
		}
	}
	
	protected class Arr extends ArrayList<Node>{
		
		@Override
		public void removeRange(int fromIndex, int toIndex){
			super.removeRange(fromIndex, toIndex);
		}
	}
	
	protected Arr queue=new Arr();
	
	protected Vec2i		pos		=new Vec2i();
	protected Vec3f		camPos	=new Vec3f();
	protected Vec3f		camRot	=new Vec3f();
	protected Vec3f		camDir	=new Vec3f();
	protected boolean	dirty,moved,recalcDot;
	
	public synchronized void add(Chunk ch){
		Node n=new Node(ch);
		if(queue.contains(n)) return;
		queue.add(n);
		n.calc();
		dirty=true;
	}
	
	public void update(){
		Camera cam=Game.get().renderer.getCamera();
		
		if(!camRot.equals(cam.rot)){
			camDir.set(camRot.set(cam.rot)).eulerToDirection();
			dirty=recalcDot=true;
		}
		
		if(!camPos.equals(cam.pos)){
			camPos.set(cam.pos);
			
			int x=(int)(cam.pos.x()/Chunk.SIZE);
			int z=(int)(cam.pos.z()/Chunk.SIZE);
			if(pos.x()!=x||pos.y()!=z){
				pos.set(x, z);
				moved=dirty=true;
			}
		}
		
	}
	
	protected synchronized void sort(){
		if(moved) queue.forEach(Node::calc);
		else if(recalcDot) queue.forEach(Node::calcDot);
		
		queue.sort(null);
		
		moved=dirty=recalcDot=false;
	}
	
	public synchronized boolean hasNext(){
		return remaining()>0;
	}
	
	private synchronized Chunk next(){
		if(dirty) sort();
		return queue.remove(queue.size()-1).vec;
	}
	
	public int remaining(){
		return queue.size();
	}
	
	public void iterate(Consumer<Chunk> cons){
		while(hasNext()){
			Chunk pos;
			synchronized(this){
				pos=next();
			}
			cons.accept(pos);
		}
	}
	
}
