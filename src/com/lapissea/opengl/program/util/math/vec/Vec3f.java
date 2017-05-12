package com.lapissea.opengl.program.util.math.vec;

import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;

import com.lapissea.opengl.program.interfaces.Calculateable;
import com.lapissea.opengl.program.util.Pool;

public class Vec3f extends Vector3f implements Calculateable<Vec3f>{
	
	private static final long serialVersionUID=8084946802516068121L;
	
	public static final Pool<Vec3f> POOL=new Pool<>(Vec3f::new);
	
	public Vec3f(float[] data){
		this(data[0], data[1], data[2]);
	}
	
	public Vec3f(){
		this(0);
	}
	
	public Vec3f(float x){
		this(x, 0);
	}
	
	public Vec3f(float x, float y){
		this(x, y, 0);
	}
	
	public Vec3f(javax.vecmath.Vector3f src){
		this(src.x, src.y, src.z);
	}
	
	public Vec3f(Vec3f src){
		this(src.x(), src.y(), src.z());
	}
	
	public Vec3f(float x, float y, float z){
		set(x, y, z);
		
		//		LogUtil.printStackTrace("");
	}
	
	public Vec3f add(float x, float y, float z){
		this.x+=x;
		this.y+=y;
		this.z+=z;
		return this;
	}
	
	public Vec3f x(float x){
		this.x=x;
		return this;
	}
	
	public Vec3f y(float y){
		this.y=y;
		return this;
	}
	
	public Vec3f z(float z){
		this.z=z;
		return this;
	}
	
	public float x(){
		return x;
	}
	
	public float y(){
		return y;
	}
	
	public float z(){
		return z;
	}
	
	@Override
	public Vec3f add(Vec3f c){
		return addX(c.x()).addY(c.y()).addZ(c.z());
	}
	
	@Override
	public Vec3f sub(Vec3f c){
		x(x()-c.x());
		y(y()-c.y());
		z(z()-c.z());
		return this;
	}
	
	@Override
	public Vec3f subRev(Vec3f c){
		x(c.x()-x());
		y(c.y()-y());
		z(c.z()-z());
		return this;
	}
	
	@Override
	public Vec3f mul(float f){
		x(x()*f);
		y(y()*f);
		z(z()*f);
		return this;
	}
	
	public Vec3f mul(float x, float y, float z){
		x(x()*x);
		y(y()*y);
		z(z()*z);
		return this;
	}
	
	@Override
	public Vec3f mul(Vec3f c){
		x(x()*c.x());
		y(y()*c.y());
		z(z()*c.z());
		return this;
	}
	
	@Override
	public Vec3f div(Vec3f c){
		x(x()/c.x());
		y(y()/c.y());
		z(z()/c.z());
		return this;
	}
	
	public Vec3f div(float x, float y, float z){
		x(x()/x);
		y(y()/y);
		z(z()/z);
		return this;
	}
	
	@Override
	public Vec3f abs(){
		x(Math.abs(x()));
		y(Math.abs(y()));
		z(Math.abs(z()));
		return this;
	}
	
	@Override
	public Vec3f sqrt(){
		x((float)Math.sqrt(x()));
		y((float)Math.sqrt(y()));
		z((float)Math.sqrt(z()));
		return this;
	}
	
	@Override
	public Vec3f sq(){
		x(x()*x());
		y(y()*y());
		z(z()*z());
		return this;
	}
	
	public Vec3f addX(float x){
		x(x()+x);
		return this;
	}
	
	public Vec3f addY(float y){
		y(y()+y);
		return this;
	}
	
	public Vec3f addZ(float z){
		z(z()+z);
		return this;
	}
	
	@Override
	public Vec3f set(ReadableVector3f src){
		x=src.getX();
		y=src.getY();
		z=src.getZ();
		return this;
	}
	
	@Override
	public Vec3f clone(){
		return new Vec3f(x(), y(), z());
	}
	
	public Vec3f crossProduct(Vec3f vec, Vec3f dest){
		dest.set(y()*vec.z()-z()*vec.y(), z()*vec.x()-x()*vec.z(), x()*vec.y()-y()*vec.x());
		return dest;
	}
	
	public float max(){
		if(x>y){
			if(x>z) return x;
			else return z;
		}
		if(y>z) return y;
		else return z;
	}
	
	@Override
	public Vec3f set(Vec3f src){
		x(src.x());
		y(src.y());
		z(src.z());
		return this;
	}
	
	public Vec3f set(javax.vecmath.Vector3f src){
		x(src.x);
		y(src.y);
		z(src.z);
		return this;
	}
	
	public Vec3f setThis(float x, float y, float z){
		super.set(x, y, z);
		return this;
	}
	
	public Vec3f setMax(Vec3f vec){
		if(x()<vec.x()) x(vec.x());
		if(y()<vec.y()) y(vec.y());
		if(z()<vec.z()) z(vec.z());
		return this;
	}

	public Vec3f mulX(int x){
		return x(x()*x);
	}
	public Vec3f mulY(int y){
		return y(y()*y);
	}
	public Vec3f mulZ(int z){
		return z(z()*z);
	}
	
}
