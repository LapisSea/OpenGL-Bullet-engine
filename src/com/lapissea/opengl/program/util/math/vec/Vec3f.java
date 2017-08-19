package com.lapissea.opengl.program.util.math.vec;

import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;

import com.lapissea.opengl.window.api.util.Calculateable;
import com.lapissea.opengl.window.api.util.IRotation;
import com.lapissea.opengl.window.api.util.IVec3f;
import com.lapissea.opengl.window.api.util.Interpolateble;
import com.lapissea.opengl.window.api.util.MathUtil;
import com.lapissea.opengl.window.api.util.SimpleLoadable;

public class Vec3f extends Vector3f implements Calculateable<Vec3f>,IVec3f,Interpolateble<Vec3f>,IRotation,SimpleLoadable<Vec3f>{
	
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
	
	public Vec3f(Vec3f src){
		this(src.x(), src.y(), src.z());
	}
	
	public Vec3f(String string, int start){
		load(string, start);
	}
	
	public Vec3f(String string){
		load(string);
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
	public Vec3f load(String string){
		return load(string, 0);
	}
	
	@Override
	public Vec3f load(String string, int start){
		return load(string, start, string.length());
	}
	
	private void load(StringBuilder buff, int rgba){
		int pos=buff.indexOf("=");
		if(pos==-1){
			float num=Float.parseFloat(buff.toString());
			// @formatter:off
			switch(rgba){
			case 0:x(num);break;
			case 1:y(num);break;
			case 2:z(num);break;
			}
			// @formatter:on
		}else{
			float num=Float.parseFloat(buff.substring(pos+1).trim());
			// @formatter:off
			switch(buff.charAt(0)){
			case 'x':x(num);break;
			case 'y':y(num);break;
			case 'z':z(num);break;
			}
			// @formatter:on
		}
	}
	
	@Override
	public Vec3f load(String string, int start, int end){
		if(end>string.length()) throw new IllegalArgumentException("End "+end+" can not be larger than total length of "+string.length());
		if(start<0) throw new IllegalArgumentException("Start has to be positive!");
		if(start>=end) throw new IllegalArgumentException("Start has to be smaller than end!");
		
		boolean begin=true,lastSpace=false;
		StringBuilder buff=new StringBuilder();
		int rgba=0;
		for(int i=start;i<end;i++){
			char c=string.charAt(i);
			boolean space=c==','||Character.isWhitespace(c);
			if(begin){
				if(space) continue;
				else begin=false;
			}
			if(lastSpace&&space) continue;
			
			if(lastSpace=space){
				load(buff, rgba);
				rgba=(rgba+1)%3;
				buff.setLength(0);
			}else buff.append(c);
			
		}
		if(buff.length()>0) load(buff, rgba);
		
		return this;
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
	
	public Vec3f div(float f){
		x(x()/f);
		y(y()/f);
		z(z()/f);
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
		if(x()<0) x(-x());
		if(y()<0) y(-y());
		if(z()<0) z(-z());
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
		return MathUtil.max(x(), y(), z());
	}
	
	public float min(){
		return MathUtil.min(x(), y(), z());
	}
	
	@Override
	public Vec3f set(Vec3f src){
		x(src.x());
		y(src.y());
		z(src.z());
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
	
	public Vec3f normalize(){
		super.normalise();
		return this;
	}
	
	public void set(double x, double y, double z){
		set((float)x, (float)y, (float)z);
	}
	
	public Vec3f directionToEuler(){
		
		float distanceX=-x(),distanceY=-y(),distanceZ=-z();
		x((float)-Math.atan2(distanceY, MathUtil.length(-distanceX, -distanceZ)));
		y((float)Math.atan2(distanceX, -distanceZ));
		z(0);
		return this;
	}
	
	public Vec3f eulerToDirection(){
		double xCos=Math.cos(x());
		double xSin=Math.sin(x());
		
		double y=-y()+Math.PI/2;
		double yCos=Math.cos(y);
		double ySin=Math.sin(y);
		
		set(xCos*yCos, -xSin, -xCos*ySin);
		return this;
	}
	
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder(20);
		
		sb.append("Vec3f[");
		sb.append(x);
		sb.append(", ");
		sb.append(y);
		sb.append(", ");
		sb.append(z);
		sb.append(']');
		return sb.toString();
	}
	
	@Override
	public Vec3f interpolate(Vec3f second, float percent){
		return interpolate(this, this, second, percent);
	}
	
	@Override
	public Vec3f interpolate(Vec3f first, Vec3f second, float percent){
		return interpolate(this, first, second, percent);
	}
	
	public double distanceTo(Vec3f vec){
		double x=x()-vec.x();
		double y=y()-vec.y();
		double z=z()-vec.z();
		return Math.sqrt(x*x+y*y+z*z);
	}
	
	@Override
	public float w(){
		return 1;
	}
	
	@Override
	public <T extends IVec3f> T rotate(T src, T dest){
		dest.x(src.x());
		dest.y(src.y());
		dest.z(src.z());
		return src;
	}
	
	public static Vec3f single(float f){
		return new Vec3f(f, f, f);
	}
	
	@Override
	public int getValueCount(){
		return 3;
	}
	
	@Override
	public void loadValue(int id, float value){
		// @formatter:off
		switch(id){
		case 0:x(value);break;
		case 1:y(value);break;
		case 2:z(value);break;
		}
		// @formatter:on
	}
	
	@Override
	public void loadValue(char c, float value){
		// @formatter:off
		switch(c){
		case 'x':x(value);break;
		case 'y':y(value);break;
		case 'z':z(value);break;
		}
		// @formatter:on
	}
	
	@Override
	public Vec3f load(int offset, float[] data){
		return setThis(data[offset], data[offset+1], data[offset+2]);
	}
}
