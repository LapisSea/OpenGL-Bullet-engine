package com.lapissea.opengl.util.math.vec;

import com.lapissea.opengl.window.api.util.vec.IRotation;
import com.lapissea.opengl.window.api.util.vec.IVec3fR;
import com.lapissea.opengl.window.api.util.vec.IVec3fW;

public class Vec3fFinal implements IVec3fR,IRotation{
	
	private static final Quat4 ROTATION_QUAT=new Quat4();
	
	private final float x,y,z;
	
	public static Vec3fFinal single(float f){
		return new Vec3fFinal(f, f, f);
	}
	
	public Vec3fFinal(){
		this(0);
	}
	
	public Vec3fFinal(float x){
		this(x, 0);
	}
	
	public Vec3fFinal(float x, float y){
		this(x, y, 0);
	}
	
	public Vec3fFinal(float x, float y, float z){
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public Vec3fFinal(float[] data){
		this(data[0], data[1], data[2]);
	}
	
	public Vec3fFinal(Vec3f src){
		this(src.x(), src.y(), src.z());
	}
	
	public <T extends IVec3fW> T crossProduct(IVec3fR vec, T dest){
		dest.set(y()*vec.z()-z()*vec.y(), z()*vec.x()-x()*vec.z(), x()*vec.y()-y()*vec.x());
		return dest;
	}
	
	public void set(double x, double y, double z){
		set((float)x, (float)y, (float)z);
	}
	
	
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder(20);
		
		sb.append("Vec3fF[");
		sb.append(x);
		sb.append(", ");
		sb.append(y);
		sb.append(", ");
		sb.append(z);
		sb.append(']');
		return sb.toString();
	}
	
	@Override
	public float w(){
		return 1;
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
	public <T extends IVec3fR&IVec3fW> T rotate(T src, T dest){
		synchronized(ROTATION_QUAT){
			return ROTATION_QUAT.set(this).rotate(src, dest);
		}
	}
	@Override
	public boolean equals(Object obj){
		if(obj==null||!(obj instanceof IVec3fR))return false;
		IVec3fR o=(IVec3fR)obj;
		return x()==o.x()&&y()==o.y()&&y()==o.y();
	}
}
