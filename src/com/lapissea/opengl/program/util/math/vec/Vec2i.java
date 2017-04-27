package com.lapissea.opengl.program.util.math.vec;

import java.util.function.BiConsumer;

import org.json.JSONException;
import org.json.JSONObject;

import com.lapissea.opengl.program.interfaces.Calculateable;

public class Vec2i implements Calculateable<Vec2i>{
	
	private static final Vec2i STATIC_SAFE=new Vec2i();
	private int x,y;
	
	public Vec2i(JSONObject json){
		try{
			set(json.getInt("x"), json.getInt("y"));
		}catch(JSONException e){
			try{
				set(json.getInt("w"), json.getInt("h"));
			}catch(JSONException e1){
				throw new IllegalArgumentException("Json does not contain x,y or w,h as int");
			}
		}
	}
	
	public Vec2i(){
		this(0, 0);
	}
	
	public Vec2i(int x){
		this(x, 0);
	}
	
	public Vec2i(int x, int y){
		set(x, y);
	}
	
	public Vec2i set(int x, int y){
		return x(x).y(y);
	}
	
	public int x(){
		return x;
	}
	
	public Vec2i x(int x){
		this.x=x;
		return this;
	}
	
	public int y(){
		return y;
	}
	
	public Vec2i y(int y){
		this.y=y;
		return this;
	}
	
	@Override
	public Vec2i add(Vec2i c){
		x(x()+c.x());
		y(y()+c.y());
		return this;
	}
	
	@Override
	public Vec2i sub(Vec2i c){
		x(x()-c.x());
		y(y()-c.y());
		return this;
	}
	
	@Override
	public Vec2i subRev(Vec2i c){
		x(c.x()-x());
		y(c.y()-y());
		return this;
	}
	
	@Override
	public Vec2i mul(Vec2i c){
		x(x()*c.x());
		y(y()*c.y());
		return this;
	}
	
	@Override
	public Vec2i div(Vec2i c){
		x(x()/c.x());
		y(y()/c.y());
		return this;
	}
	
	@Override
	public Vec2i abs(){
		x(Math.abs(x()));
		y(Math.abs(y()));
		return this;
	}
	
	@Override
	public Vec2i sqrt(){
		x((int)Math.sqrt(x()));
		y((int)Math.sqrt(y()));
		return this;
	}
	
	@Override
	public Vec2i sq(){
		x(x()*x());
		y(y()*y());
		return this;
	}
	public void putXY(JSONObject json){
		try{
			json.put("x", x());
			json.put("y", y());
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	public void putWH(JSONObject json){
		try{
			json.put("w", x());
			json.put("h", y());
		}catch(JSONException e){
			e.printStackTrace();
		}
	}

	public void putXY(BiConsumer<String,Integer> put){
		put.accept("x", x());
		put.accept("y", y());
	}
	public void putWH(BiConsumer<String,Integer> put){
		put.accept("w", x());
		put.accept("h", y());
	}
	@Override
	public String toString(){
		return "Vec2i{x="+x()+", y="+y()+"}";
	}
	@Override
	public Vec2i clone(){
		return new Vec2i(x(), y());
	}
	@Override
	public Vec2i mul(float f){
		STATIC_SAFE.set((int)f, (int)f);
		return mul(STATIC_SAFE);
	}
	@Override
	public Vec2i set(Vec2i src){
		return set(src.x(), src.y());
	}
}
