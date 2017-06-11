package com.lapissea.opengl.program.util.math.vec;

import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;

import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.window.api.util.Calculateable;
import com.lapissea.opengl.window.api.util.IVec3f;
import com.lapissea.opengl.window.api.util.MathUtil;

public class Vec3f extends Vector3f implements Calculateable<Vec3f>,IVec3f{
	
	private static final long serialVersionUID=8084946802516068121L;
	
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
	}
	
	public Vec3f add(IVec3f vec){
		return add(vec.x(), vec.y(), vec.z());
	}
	
	public Vec3f add(float f){
		return add(f, f, f);
	}
	
	public Vec3f add(float x, float y, float z){
		this.x+=x;
		this.y+=y;
		this.z+=z;
		return this;
	}
	
	@Override
	public Vec3f x(float x){
		this.x=x;
		return this;
	}
	
	@Override
	public Vec3f y(float y){
		this.y=y;
		return this;
	}
	
	@Override
	public Vec3f z(float z){
		this.z=z;
		return this;
	}
	
	@Override
	public float x(){
		return x;
	}
	
	@Override
	public float y(){
		return y;
	}
	
	@Override
	public float z(){
		return z;
	}
	
	@Override
	public Vec3f add(Vec3f c){
		return addX(c.x()).addY(c.y()).addZ(c.z());
	}
	
	@Override
	public Vec3f sub(Vec3f c){
		return sub(c.x(), c.y(), c.z());
	}
	
	public Vec3f sub(IVec3f c){
		return sub(c.x(), c.y(), c.z());
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
	
	public Vec3f set(IVec3f src){
		x=src.x();
		y=src.y();
		z=src.z();
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
	
	public Vec3f sub(float x, float y, float z){
		return x(x()-x).y(y()-y).z(z()-z);
	}
	
	public static Vec3f interpolate(Vec3f dest, Vec3f v1, Vec3f v2, float percent){
		return dest.set(v1).add((v2.x()-v1.x())*percent, (v2.y()-v1.y())*percent, (v2.z()-v1.z())*percent);
	}
	
	public Vec3f set(Quat4M q1){
		double sqw=q1.w*q1.w;
		double sqx=q1.x*q1.x;
		double sqy=q1.y*q1.y;
		double sqz=q1.z*q1.z;
		double unit=sqx+sqy+sqz+sqw; // if normalised is one, otherwise is correction factor
		double test=q1.x*q1.y+q1.z*q1.w;
		if(test>0.499*unit){ // singularity at north pole
			y=(float)(2*Math.atan2(q1.x, q1.w));
			z=(float)Math.PI/2;
			x=0;
			return this;
		}
		if(test<-0.499*unit){ // singularity at south pole
			y=-2*(float)Math.atan2(q1.x, q1.w);
			z=(float)-Math.PI/2;
			x=0;
			return this;
		}
		y=(float)Math.atan2(2*q1.y*q1.w-2*q1.x*q1.z, sqx-sqy-sqz+sqw);
		z=(float)Math.asin(2*test/unit);
		x=(float)Math.atan2(2*q1.x*q1.w-2*q1.y*q1.z, -sqx+sqy-sqz+sqw);
		
		return this;
	}
	
	public Vec3f normalize(){
		super.normalise();
		return this;
	}
	
	public Vec3f toAngular(){
		float distanceX=-x(),distanceY=-y(),distanceZ=-z();
		x((float)-Math.atan2(distanceY, MathUtil.length(-distanceX, -distanceZ)));
		y((float)Math.atan2(distanceX, -distanceZ));
		
		return this;
	}
	
}
