package com.lapissea.opengl.program.util.math.vec;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;
import java.util.function.BiConsumer;

import com.lapissea.opengl.window.api.util.vec.IVec2iR;

public class Vec2iFinal implements Serializable,IVec2iR{
	
	private static final long serialVersionUID=7737581116406153679L;
	
	private final int	x;
	private final int	y;
	
	public Vec2iFinal(IVec2iR vec2){
		this(vec2.x(), vec2.y());
	}
	
	public Vec2iFinal(Point point){
		this(point.x, point.y);
	}
	
	public Vec2iFinal(Dimension dimension){
		this(dimension.width, dimension.height);
	}
	
	public Vec2iFinal(){
		this(0, 0);
	}
	
	public Vec2iFinal(int x){
		this(x, 0);
	}
	
	public Vec2iFinal(int x, int y){
		this.x=x;
		this.y=y;
	}
	
	@Override
	public int x(){
		return x;
	}
	
	@Override
	public int y(){
		return y;
	}
	
	@SuppressWarnings("boxing")
	public void putXY(BiConsumer<String,Integer> put){
		put.accept("x", x());
		put.accept("y", y());
	}
	
	@SuppressWarnings("boxing")
	public void putWH(BiConsumer<String,Integer> put){
		put.accept("w", x());
		put.accept("h", y());
	}
	
	@Override
	public String toString(){
		return "Vec2i{x="+x()+", y="+y()+"}";
	}
	
	@Override
	public Vec2iFinal clone(){
		return new Vec2iFinal(x(), y());
	}
}
