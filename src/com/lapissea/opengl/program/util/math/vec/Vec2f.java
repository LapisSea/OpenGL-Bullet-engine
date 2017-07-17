package com.lapissea.opengl.program.util.math.vec;

import com.lapissea.opengl.window.api.util.Calculateable;
import com.lapissea.opengl.window.api.util.IVec2i;

public class Vec2f implements Calculateable<Vec2f>{
	
	public static final Vec2f ZERO=new Vec2f(){
		
		@Override
		public float x(){
			return 0;
		}
		
		@Override
		public float y(){
			return 0;
		}
	};
	
	private float x,y;
	
	public Vec2f(){
		this(0, 0);
	}
	
	public Vec2f(float x){
		this(x, 0);
	}
	
	public Vec2f(float x, float y){
		set(x, y);
	}
	
	public Vec2f set(float x, float y){
		return x(x).y(y);
	}
	
	public float x(){
		return x;
	}
	
	public Vec2f x(float x){
		this.x=x;
		return this;
	}
	
	public float y(){
		return y;
	}
	
	public Vec2f y(float y){
		this.y=y;
		return this;
	}
	
	public Vec2f add(float x, float y){
		x(x()+x);
		y(y()+y);
		return this;
	}
	
	@Override
	public Vec2f add(Vec2f c){
		return add(c.x(), c.y());
	}
	
	@Override
	public Vec2f sub(Vec2f c){
		x(x()-c.x());
		y(y()-c.y());
		return this;
	}
	
	@Override
	public Vec2f subRev(Vec2f c){
		x(c.x()-x());
		y(c.y()-y());
		return this;
	}
	
	@Override
	public Vec2f mul(Vec2f c){
		x(x()*c.x());
		y(y()*c.y());
		return this;
	}
	
	@Override
	public Vec2f div(Vec2f c){
		x(x()/c.x());
		y(y()/c.y());
		return this;
	}
	
	@Override
	public Vec2f abs(){
		x(Math.abs(x()));
		y(Math.abs(y()));
		return this;
	}
	
	@Override
	public Vec2f sqrt(){
		x((float)Math.sqrt(x()));
		y((float)Math.sqrt(y()));
		return this;
	}
	
	@Override
	public Vec2f sq(){
		x(x()*x());
		y(y()*y());
		return this;
	}
	
	@Override
	public Vec2f clone(){
		return new Vec2f(x(), y());
	}
	
	@Override
	public Vec2f mul(float f){
		x(x()*f);
		y(y()*f);
		return this;
	}
	
	@Override
	public Vec2f set(Vec2f src){
		x(src.x());
		y(src.y());
		return this;
	}
	
	@Override
	public String toString(){
		return "Vec2f{x="+x()+", y="+y()+"}";
	}
	
	public Vec2f set(IVec2i src){
		x(src.x());
		y(src.y());
		return this;
	}
}
