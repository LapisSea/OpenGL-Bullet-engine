package com.lapissea.opengl.program.util;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
import javax.vecmath.Tuple4d;
import javax.vecmath.Tuple4f;

import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.util.IRotation;
import com.lapissea.opengl.window.api.util.IVec3f;

public class Quat4M extends Quat4f implements IRotation{
	
	private static final long serialVersionUID=1217013901730370836L;
	
	public Quat4M(){
		super(0, 0, 0, 1);
	}
	
	public Quat4M(Vec3f euler){
		super();
		set(euler);
	}
	
	public Quat4M(float arg0, float arg1, float arg2, float arg3){
		super(arg0, arg1, arg2, arg3);
	}
	
	public Quat4M(float[] arg0){
		super(arg0);
	}
	
	public Quat4M(Quat4d arg0){
		super(arg0);
	}
	
	public Quat4M(Quat4f arg0){
		super(arg0);
	}
	
	public Quat4M(Tuple4d arg0){
		super(arg0);
	}
	
	public Quat4M(Tuple4f arg0){
		super(arg0);
	}
	
	public Quat4M mul(float f){
		x*=f;
		y*=f;
		z*=f;
		w*=f;
		return this;
	}
	
	@Override
	public Quat4M clone(){
		return new Quat4M(this);
	}
	
	public Quat4M set(Quat4M src){
		x=src.x();
		y=src.y();
		z=src.z();
		w=src.w();
		return this;
	}
	
	public Matrix4f toRotationMatrix(Matrix4f dest){
		final float xy=x*y;
		final float xz=x*z;
		final float xw=x*w;
		final float yz=y*z;
		final float yw=y*w;
		final float zw=z*w;
		final float xSquared=x*x;
		final float ySquared=y*y;
		final float zSquared=z*z;
		dest.m00=1-2*(ySquared+zSquared);
		dest.m01=2*(xy-zw);
		dest.m02=2*(xz+yw);
		dest.m03=0;
		
		dest.m10=2*(xy+zw);
		dest.m11=1-2*(xSquared+zSquared);
		dest.m12=2*(yz-xw);
		dest.m13=0;
		
		dest.m20=2*(xz-yw);
		dest.m21=2*(yz+xw);
		dest.m22=1-2*(xSquared+ySquared);
		dest.m23=0;
		
		dest.m30=0;
		dest.m31=0;
		dest.m32=0;
		dest.m33=1;
		return dest;
	}
	
	public org.lwjgl.util.vector.Matrix4f quatToMatrix4f(org.lwjgl.util.vector.Matrix4f dest){
		dest.m00=1.0f-2.0f*(this.y*this.y+this.z*this.z);
		dest.m01=2.0f*(this.x*this.y+this.z*this.w);
		dest.m02=2.0f*(this.x*this.z-this.y*this.w);
		dest.m03=0.0f;
		
		// Second row
		dest.m10=2.0f*(this.x*this.y-this.z*this.w);
		dest.m11=1.0f-2.0f*(this.x*this.x+this.z*this.z);
		dest.m12=2.0f*(this.z*this.y+this.x*this.w);
		dest.m13=0.0f;
		
		// Third row
		dest.m20=2.0f*(this.x*this.z+this.y*this.w);
		dest.m21=2.0f*(this.y*this.z-this.x*this.w);
		dest.m22=1.0f-2.0f*(this.x*this.x+this.y*this.y);
		dest.m23=0.0f;
		
		// Fourth row
		dest.m30=0;
		dest.m31=0;
		dest.m32=0;
		dest.m33=1.0f;
		
		return dest;
	}
	
	public Quat4M interpolate(Quat4M a, Quat4M b, float blend){
		return interpolate(this, a, b, blend);
	}
	
	public static Quat4M interpolate(Quat4M dest, Quat4M a, Quat4M b, float blend){
		float dot=a.w*b.w+a.x*b.x+a.y*b.y+a.z*b.z;
		float blendI=1f-blend;
		if(dot<0){
			dest.w=blendI*a.w+blend*-b.w;
			dest.x=blendI*a.x+blend*-b.x;
			dest.y=blendI*a.y+blend*-b.y;
			dest.z=blendI*a.z+blend*-b.z;
		}
		else{
			dest.w=blendI*a.w+blend*b.w;
			dest.x=blendI*a.x+blend*b.x;
			dest.y=blendI*a.y+blend*b.y;
			dest.z=blendI*a.z+blend*b.z;
		}
		dest.normalize();
		return dest;
	}
	
	public void set(Vec3f euler){
		double x2=euler.x()/2;
		double y2=euler.y()/2;
		double z2=euler.z()/2;
		
		double c1=Math.cos(y2);
		double s1=Math.sin(y2);
		double c2=Math.cos(z2);
		double s2=Math.sin(z2);
		double c3=Math.cos(x2);
		double s3=Math.sin(x2);
		double c1c2=c1*c2;
		double s1s2=s1*s2;
		w=(float)(c1c2*c3-s1s2*s3);
		x=(float)(c1c2*s3+s1s2*c3);
		y=(float)(s1*c2*c3+c1*s2*s3);
		z=(float)(c1*s2*c3-s1*c2*s3);
		normalize();
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
	public float w(){
		return w;
	}
	
	@Override
	public <T extends IVec3f> T rotate(T src, T dest){
		float k0=w*w-0.5f;
		float k1;
		float rx,ry,rz;
		
		// k1 = Q.V
		k1=src.x()*x;
		k1+=src.y()*y;
		k1+=src.z()*z;
		
		// (qq-1/2)V+(Q.V)Q
		rx=src.x()*k0+x*k1;
		ry=src.y()*k0+y*k1;
		rz=src.z()*k0+z*k1;
		
		// (Q.V)Q+(qq-1/2)V+q(QxV)
		rx+=w*(y*src.z()-z*src.y());
		ry+=w*(z*src.x()-x*src.z());
		rz+=w*(x*src.y()-y*src.x());
		
		//  2((Q.V)Q+(qq-1/2)V+q(QxV))
		rx+=rx;
		ry+=ry;
		rz+=rz;
		dest.x(rx);
		dest.y(ry);
		dest.z(rz);
		
		return dest;
	}
	
	public Quat4M fromMatrix(org.lwjgl.util.vector.Matrix4f matrix){
		return fromMatrix(this, matrix);
	}
	public static Quat4M fromMatrix(Quat4M dest, org.lwjgl.util.vector.Matrix4f matrix){
		float w,x,y,z;
		float diagonal=matrix.m00+matrix.m11+matrix.m22;
		if(diagonal>0){
			float w4=(float)(Math.sqrt(diagonal+1f)*2f);
			w=w4/4f;
			x=(matrix.m21-matrix.m12)/w4;
			y=(matrix.m02-matrix.m20)/w4;
			z=(matrix.m10-matrix.m01)/w4;
		}
		else if((matrix.m00>matrix.m11)&&(matrix.m00>matrix.m22)){
			float x4=(float)(Math.sqrt(1f+matrix.m00-matrix.m11-matrix.m22)*2f);
			w=(matrix.m21-matrix.m12)/x4;
			x=x4/4f;
			y=(matrix.m01+matrix.m10)/x4;
			z=(matrix.m02+matrix.m20)/x4;
		}
		else if(matrix.m11>matrix.m22){
			float y4=(float)(Math.sqrt(1f+matrix.m11-matrix.m00-matrix.m22)*2f);
			w=(matrix.m02-matrix.m20)/y4;
			x=(matrix.m01+matrix.m10)/y4;
			y=y4/4f;
			z=(matrix.m12+matrix.m21)/y4;
		}
		else{
			float z4=(float)(Math.sqrt(1f+matrix.m22-matrix.m00-matrix.m11)*2f);
			w=(matrix.m10-matrix.m01)/z4;
			x=(matrix.m02+matrix.m20)/z4;
			y=(matrix.m12+matrix.m21)/z4;
			z=z4/4f;
		}
		dest.set(x, y, z, w);
		return dest;
	}
}
