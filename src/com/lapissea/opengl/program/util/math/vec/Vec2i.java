package com.lapissea.opengl.program.util.math.vec;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;
import java.util.function.BiConsumer;

import com.lapissea.opengl.window.api.util.Calculateable;
import com.lapissea.opengl.window.api.util.IVec2i;
import com.lapissea.opengl.window.api.util.MathUtil;

public class Vec2i implements Calculateable<Vec2i>,Serializable,IVec2i{
	
	private static final long serialVersionUID=7737581116406153679L;
	
	private int	x;
	private int	y;
	
	public Vec2i(IVec2i vec2){
		this(vec2.x(), vec2.y());
	}
	public Vec2i(Point point){
		this(point.x, point.y);
	}
	
	public Vec2i(Dimension dimension){
		this(dimension.width, dimension.height);
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
	
	@Override
	public int x(){
		return x;
	}
	
	@Override
	public Vec2i x(int x){
		this.x=x;
		return this;
	}
	
	@Override
	public int y(){
		return y;
	}
	
	@Override
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
		return x((int)(x()*f)).y((int)(y()*f));
	}
	
	@Override
	public Vec2i set(Vec2i src){
		return set(src.x(), src.y());
	}
	
	public Vec2i set(IVec2i src){
		return set(src.x(), src.y());
	}
	public double length() {
		return MathUtil.length(x(), y());
	}
}
