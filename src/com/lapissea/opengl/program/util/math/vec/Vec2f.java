package com.lapissea.opengl.program.util.math.vec;

import com.lapissea.opengl.window.api.util.Calculateable;

public class Vec2f implements Calculateable<Vec2f>{
	
	private static final Vec2f STATIC_SAFE=new Vec2f();
	
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
	
	@Override
	public Vec2f add(Vec2f c){
		x(x()+c.x());
		y(y()+c.y());
		return this;
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
		STATIC_SAFE.set(f, f);
		return mul(STATIC_SAFE);
	}

	@Override
	public Vec2f set(Vec2f src){
		x(src.x());
		y(src.y());
		return this;
	}
}
