package com.lapissea.opengl.program.game.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

import org.lwjgl.util.vector.Vector3f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.game.terrain.Chunk;
import com.lapissea.opengl.program.rendering.Camera;
import com.lapissea.opengl.program.util.math.vec.Vec2i;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class ChunkLoadingSorter implements Iterator<Vec2i>{
	
	protected class Node implements Comparable<Node>{
		
		final Vec2i	vec;
		double		distance=0;
		double		dot		=1;
		double		lazyness=0;
		Vec3f		rot		=new Vec3f();
		
		Node(Vec2i vec){
			this.vec=vec;
		}
		void calcDot(){
			dot=Math.pow(1+(1+Vector3f.dot(rot, camDir))/2, 2);
			lazyness=distance*dot;
		}
		
		void calc(){
			distance=vec.distanceTo(pos);
			rot.set(camPos).sub(vec.x()*Chunk.SIZE, 0, vec.y()*Chunk.SIZE);
			rot.scale(1/rot.length());
			
			calcDot();
		}
		
		@Override
		public int compareTo(Node o){
			return Double.compare(lazyness, o.lazyness);
		}
	}
	
	protected class Arr extends ArrayList<Node>{
		
		@Override
		public void removeRange(int fromIndex, int toIndex){
			super.removeRange(fromIndex, toIndex);
		}
	}
	
	protected Arr		queue	=new Arr();
	protected Vec2i		pos		=new Vec2i();
	protected Vec3f		camPos	=new Vec3f();
	protected Vec3f		camRot	=new Vec3f();
	protected Vec3f		camDir	=new Vec3f();
	protected boolean	dirty,moved,recalcDot;
	protected int		cursor;
	
	public synchronized void add(Vec2i pos){
		Node n=new Node(pos);
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
	
	protected void sort(){
		
		if(cursor>0){
			queue.removeRange(0, cursor);
			cursor=0;
		}
		
		if(moved) queue.forEach(Node::calc);
		else if(recalcDot) queue.forEach(Node::calcDot);
		
		queue.sort(null);
		
		moved=dirty=recalcDot=false;
	}
	
	@Override
	public synchronized boolean hasNext(){
		return remaining()>0;
	}
	
	@Override
	public synchronized Vec2i next(){
		if(dirty) sort();
		return queue.get(cursor++).vec;
	}
	
	public int remaining(){
		return queue.size()-cursor;
	}
	
	public void iterate(Consumer<Vec2i> cons){
		while(hasNext()){
			cons.accept(next());
		}
	}
	
}
