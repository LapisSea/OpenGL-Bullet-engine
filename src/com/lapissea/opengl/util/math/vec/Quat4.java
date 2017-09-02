package com.lapissea.opengl.util.math.vec;

import javax.vecmath.Quat4f;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.window.api.util.Interpolateble;
import com.lapissea.opengl.window.api.util.MathUtil;
import com.lapissea.opengl.window.api.util.vec.IRotation;
import com.lapissea.opengl.window.api.util.vec.IVec3fR;
import com.lapissea.opengl.window.api.util.vec.IVec3fW;

public class Quat4 implements IRotation,Interpolateble<Quat4>{
	
	final static double EPS2=1.0e-30;
	
	private float x,y,z,w;
	
	public Quat4(){
		x=y=z=0;
		w=1;
	}
	
	public Quat4(float x, float y, float z, float w){
		set(x, y, z, w);
		normalize();
	}
	
	public Quat4(float[] q){
		this(q, 0);
	}
	
	public Quat4(float[] q, int offset){
		this(q[offset+0], q[offset+1], q[offset+2], q[offset+3]);
	}
	
	public Quat4(Quat4f q1){
		x=q1.x;
		y=q1.y;
		z=q1.z;
		w=q1.w;
	}
	
	public void normalize(){
		scale(1/MathUtil.length(x, y, z, w));
		
	}
	
	public void set(Matrix3f m1){
		float ww=0.25f*(m1.m00+m1.m11+m1.m22+1.0f);
		
		if(ww>=0){
			if(ww>=EPS2){
				w=(float)Math.sqrt(ww);
				ww=0.25f/w;
				x=(m1.m21-m1.m12)*ww;
				y=(m1.m02-m1.m20)*ww;
				z=(m1.m10-m1.m01)*ww;
				return;
			}
		}else{
			w=x=y=z=1;
			return;
		}
		
		w=0;
		ww=-0.5f*(m1.m11+m1.m22);
		if(ww>=0){
			if(ww>=EPS2){
				x=(float)Math.sqrt(ww);
				ww=0.5f/x;
				y=m1.m10*ww;
				z=m1.m20*ww;
				return;
			}
		}else{
			x=0;
			y=0;
			z=1;
			return;
		}
		
		x=0;
		ww=0.5f*(1.0f-m1.m22);
		if(ww>=EPS2){
			y=(float)Math.sqrt(ww);
			z=m1.m21/(2.0f*y);
			return;
		}
		
		y=0;
		z=1;
	}
	
	public void set(float m1[][]){
		float ww=0.25f*(m1[0][0]+m1[1][1]+m1[2][2]+1.0f);
		
		if(ww>=0){
			if(ww>=EPS2){
				w=(float)Math.sqrt(ww);
				ww=0.25f/w;
				x=(m1[2][1]-m1[1][2])*ww;
				y=(m1[0][2]-m1[2][0])*ww;
				z=(m1[1][0]-m1[0][1])*ww;
				return;
			}
		}else{
			w=0;
			x=0;
			y=0;
			z=1;
			return;
		}
		
		w=0;
		ww=-0.5f*(m1[1][1]+m1[2][2]);
		if(ww>=0){
			if(ww>=EPS2){
				x=(float)Math.sqrt(ww);
				ww=0.5f/x;
				y=m1[1][0]*ww;
				z=m1[2][0]*ww;
				return;
			}
		}else{
			x=0;
			y=0;
			z=1;
			return;
		}
		
		x=0;
		ww=0.5f*(1.0f-m1[2][2]);
		if(ww>=EPS2){
			y=(float)Math.sqrt(ww);
			z=m1[2][1]/(2.0f*y);
			return;
		}
		
		y=0;
		z=1;
	}
	
	public void scale(float s){
		x*=s;
		y*=s;
		z*=s;
		w*=s;
	}
	
	@Override
	public String toString(){
		return "Quat4["+x+", "+y+", "+z+", "+w+"]";
	}
	
	public Quat4(IVec3fR euler){
		set(euler);
	}
	
	public Quat4(Quat4 quat){
		this(quat.x(), quat.y(), quat.z(), quat.w());
	}
	
	public Quat4 mul(float f){
		x*=f;
		y*=f;
		z*=f;
		w*=f;
		return this;
	}
	
	@Override
	public Quat4 clone(){
		return new Quat4(this);
	}
	
	public Quat4 set(float x, float y, float z, float w){
		this.x=x;
		this.y=y;
		this.z=z;
		this.w=w;
		return this;
	}
	
	public Quat4 set(Quat4 src){
		x=src.x();
		y=src.y();
		z=src.z();
		w=src.w();
		return this;
	}
	
	public Matrix4f toRotationMatrix(Matrix4f dest){
		final float xy=x*y,xz=x*z,xw=x*w,yz=y*z,yw=y*w,zw=z*w,xSquared=x*x,ySquared=y*y,zSquared=z*z;
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
	
	public Matrix4f quatToMatrix4f(Matrix4f dest){
		dest.m00=1.0f-2.0f*(y*y+z*z);
		dest.m01=2.0f*(x*y+z*w);
		dest.m02=2.0f*(x*z-y*w);
		dest.m03=0.0f;
		
		// Second row
		dest.m10=2.0f*(x*y-z*w);
		dest.m11=1.0f-2.0f*(x*x+z*z);
		dest.m12=2.0f*(z*y+x*w);
		dest.m13=0.0f;
		
		// Third row
		dest.m20=2.0f*(x*z+y*w);
		dest.m21=2.0f*(y*z-x*w);
		dest.m22=1.0f-2.0f*(x*x+y*y);
		dest.m23=0.0f;
		
		// Fourth row
		dest.m30=0;
		dest.m31=0;
		dest.m32=0;
		dest.m33=1.0f;
		
		return dest;
	}
	
	public static Quat4 interpolate(Quat4 dest, Quat4 a, Quat4 b, float blend){
		float dot=a.w*b.w+a.x*b.x+a.y*b.y+a.z*b.z,blendI=1f-blend;
		if(dot<0){
			dest.w=blendI*a.w+blend*-b.w;
			dest.x=blendI*a.x+blend*-b.x;
			dest.y=blendI*a.y+blend*-b.y;
			dest.z=blendI*a.z+blend*-b.z;
		}else{
			dest.w=blendI*a.w+blend*b.w;
			dest.x=blendI*a.x+blend*b.x;
			dest.y=blendI*a.y+blend*b.y;
			dest.z=blendI*a.z+blend*b.z;
		}
		dest.normalize();
		return dest;
	}
	
	public Quat4 set(IVec3fR euler){
		double x2=euler.x()/2,y2=euler.y()/2,z2=euler.z()/2,c1=Math.cos(y2),s1=Math.sin(y2),c2=Math.cos(z2),s2=Math.sin(z2),c3=Math.cos(x2),s3=Math.sin(x2),c1c2=c1*c2,s1s2=s1*s2;
		
		w((float)(c1c2*c3-s1s2*s3));
		x((float)(c1c2*s3+s1s2*c3));
		y((float)(s1*c2*c3+c1*s2*s3));
		z((float)(c1*s2*c3-s1*c2*s3));
		normalize();
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
	public float w(){
		return w;
	}
	
	public Quat4 x(float x){
		this.x=x;
		return this;
	}
	
	public Quat4 y(float y){
		this.y=y;
		return this;
	}
	
	public Quat4 z(float z){
		this.z=z;
		return this;
	}
	
	public Quat4 w(float w){
		this.w=w;
		return this;
	}
	
	@Override
	public <T extends IVec3fR&IVec3fW> T rotate(T src, T dest){
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
	
	public Quat4 fromMatrix(Matrix4f matrix){
		return fromMatrix(this, matrix);
	}
	
	public static Quat4 fromMatrix(Quat4 dest, Matrix4f matrix){
		float w,x,y,z;
		float diagonal=matrix.m00+matrix.m11+matrix.m22;
		if(diagonal>0){
			float w4=(float)(Math.sqrt(diagonal+1f)*2f);
			w=w4/4f;
			x=(matrix.m21-matrix.m12)/w4;
			y=(matrix.m02-matrix.m20)/w4;
			z=(matrix.m10-matrix.m01)/w4;
		}else if(matrix.m00>matrix.m11&&matrix.m00>matrix.m22){
			float x4=(float)(Math.sqrt(1f+matrix.m00-matrix.m11-matrix.m22)*2f);
			w=(matrix.m21-matrix.m12)/x4;
			x=x4/4f;
			y=(matrix.m01+matrix.m10)/x4;
			z=(matrix.m02+matrix.m20)/x4;
		}else if(matrix.m11>matrix.m22){
			float y4=(float)(Math.sqrt(1f+matrix.m11-matrix.m00-matrix.m22)*2f);
			w=(matrix.m02-matrix.m20)/y4;
			x=(matrix.m01+matrix.m10)/y4;
			y=y4/4f;
			z=(matrix.m12+matrix.m21)/y4;
		}else{
			float z4=(float)(Math.sqrt(1f+matrix.m22-matrix.m00-matrix.m11)*2f);
			w=(matrix.m10-matrix.m01)/z4;
			x=(matrix.m02+matrix.m20)/z4;
			y=(matrix.m12+matrix.m21)/z4;
			z=z4/4f;
		}
		dest.set(x, y, z, w);
		return dest;
	}
	
	@Override
	public Quat4 interpolate(Quat4 second, float percent){
		return interpolate(this, this, second, percent);
	}
	
	@Override
	public Quat4 interpolate(Quat4 first, Quat4 second, float percent){
		return interpolate(this, first, second, percent);
	}
	
	public <T extends IVec3fW> T forward(T dest){
		dest.x(2*(x*z+w*y));
		dest.y(2*(y*z-w*x));
		dest.z(1-2*(x*x+y*y));
		return dest;
	}
	
	public <T extends IVec3fW> T up(T dest){
		dest.x(2*(x*y-w*z));
		dest.y(1-2*(x*x+z*z));
		dest.z(2*(y*z+w*x));
		return dest;
	}
	
	public <T extends IVec3fW> T left(T dest){
		dest.x(1-2*(y*y+z*z));
		dest.y(2*(x*y+w*z));
		dest.z(2*(x*z-w*y));
		return dest;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj==this) return true;
		if(!(obj instanceof Quat4)) return false;
		Quat4 q=(Quat4)obj;
		return x==q.x&&y==q.y&&z==q.z&&w==q.w;
	}
}
