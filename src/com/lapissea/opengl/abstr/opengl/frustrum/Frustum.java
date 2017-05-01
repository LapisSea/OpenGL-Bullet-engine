package com.lapissea.opengl.abstr.opengl.frustrum;

import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class Frustum{
	
	private float[][]		frustum			=new float[6][4];
	private final Matrix4f	projectionPrev	=new Matrix4f(),viewPrev=new Matrix4f();
	
	private float[] getMatrix(Matrix4f mat){
		return new float[]{
				mat.m00,
				mat.m01,
				mat.m02,
				mat.m03,
				mat.m10,
				mat.m11,
				mat.m12,
				mat.m13,
				mat.m20,
				mat.m21,
				mat.m22,
				mat.m23,
				mat.m30,
				mat.m31,
				mat.m32,
				mat.m33
		};
	}
	
	public void extractFrustum(Matrix4f projection, Matrix4f view){
		boolean projEqu=projection.equals(projectionPrev);
		boolean viewEqu=view.equals(viewPrev);
		
		if(projEqu&&viewEqu) return;
		
		if(projEqu) projectionPrev.load(projection);
		if(viewEqu) viewPrev.load(view);
		
		// Extracts The Current View Frustum Plane Equations
		float[] clip=new float[16]; // Result Of Concatenating PROJECTION and MODELVIEW
		float t; // Temporary Work Variable
		
		float[] proj=getMatrix(projection);
		float[] modl=getMatrix(view);
		
		// Concatenate (Multiply) The Two Matricies
		clip[0]=modl[0]*proj[0]+modl[1]*proj[4]+modl[2]*proj[8]+modl[3]*proj[12];
		clip[1]=modl[0]*proj[1]+modl[1]*proj[5]+modl[2]*proj[9]+modl[3]*proj[13];
		clip[2]=modl[0]*proj[2]+modl[1]*proj[6]+modl[2]*proj[10]+modl[3]*proj[14];
		clip[3]=modl[0]*proj[3]+modl[1]*proj[7]+modl[2]*proj[11]+modl[3]*proj[15];
		
		clip[4]=modl[4]*proj[0]+modl[5]*proj[4]+modl[6]*proj[8]+modl[7]*proj[12];
		clip[5]=modl[4]*proj[1]+modl[5]*proj[5]+modl[6]*proj[9]+modl[7]*proj[13];
		clip[6]=modl[4]*proj[2]+modl[5]*proj[6]+modl[6]*proj[10]+modl[7]*proj[14];
		clip[7]=modl[4]*proj[3]+modl[5]*proj[7]+modl[6]*proj[11]+modl[7]*proj[15];
		
		clip[8]=modl[8]*proj[0]+modl[9]*proj[4]+modl[10]*proj[8]+modl[11]*proj[12];
		clip[9]=modl[8]*proj[1]+modl[9]*proj[5]+modl[10]*proj[9]+modl[11]*proj[13];
		clip[10]=modl[8]*proj[2]+modl[9]*proj[6]+modl[10]*proj[10]+modl[11]*proj[14];
		clip[11]=modl[8]*proj[3]+modl[9]*proj[7]+modl[10]*proj[11]+modl[11]*proj[15];
		
		clip[12]=modl[12]*proj[0]+modl[13]*proj[4]+modl[14]*proj[8]+modl[15]*proj[12];
		clip[13]=modl[12]*proj[1]+modl[13]*proj[5]+modl[14]*proj[9]+modl[15]*proj[13];
		clip[14]=modl[12]*proj[2]+modl[13]*proj[6]+modl[14]*proj[10]+modl[15]*proj[14];
		clip[15]=modl[12]*proj[3]+modl[13]*proj[7]+modl[14]*proj[11]+modl[15]*proj[15];
		
		// Extract the RIGHT clipping plane
		frustum[0][0]=clip[3]-clip[0];
		frustum[0][1]=clip[7]-clip[4];
		frustum[0][2]=clip[11]-clip[8];
		frustum[0][3]=clip[15]-clip[12];
		
		// Normalize it
		t=(float)Math.sqrt(frustum[0][0]*frustum[0][0]+frustum[0][1]*frustum[0][1]+frustum[0][2]*frustum[0][2]);
		frustum[0][0]/=t;
		frustum[0][1]/=t;
		frustum[0][2]/=t;
		frustum[0][3]/=t;
		
		// Extract the LEFT clipping plane
		frustum[1][0]=clip[3]+clip[0];
		frustum[1][1]=clip[7]+clip[4];
		frustum[1][2]=clip[11]+clip[8];
		frustum[1][3]=clip[15]+clip[12];
		
		// Normalize it
		t=(float)Math.sqrt(frustum[1][0]*frustum[1][0]+frustum[1][1]*frustum[1][1]+frustum[1][2]*frustum[1][2]);
		frustum[1][0]/=t;
		frustum[1][1]/=t;
		frustum[1][2]/=t;
		frustum[1][3]/=t;
		
		// Extract the BOTTOM clipping plane
		frustum[2][0]=clip[3]+clip[1];
		frustum[2][1]=clip[7]+clip[5];
		frustum[2][2]=clip[11]+clip[9];
		frustum[2][3]=clip[15]+clip[13];
		// Normalize it
		t=(float)Math.sqrt(frustum[2][0]*frustum[2][0]+frustum[2][1]*frustum[2][1]+frustum[2][2]*frustum[2][2]);
		frustum[2][0]/=t;
		frustum[2][1]/=t;
		frustum[2][2]/=t;
		frustum[2][3]/=t;
		
		// Extract the TOP clipping plane
		frustum[3][0]=clip[3]-clip[1];
		frustum[3][1]=clip[7]-clip[5];
		frustum[3][2]=clip[11]-clip[9];
		frustum[3][3]=clip[15]-clip[13];
		
		// Normalize it
		t=(float)Math.sqrt(frustum[3][0]*frustum[3][0]+frustum[3][1]*frustum[3][1]+frustum[3][2]*frustum[3][2]);
		frustum[3][0]/=t;
		frustum[3][1]/=t;
		frustum[3][2]/=t;
		frustum[3][3]/=t;
		
		// Extract the FAR clipping plane
		frustum[4][0]=clip[3]-clip[2];
		frustum[4][1]=clip[7]-clip[6];
		frustum[4][2]=clip[11]-clip[10];
		frustum[4][3]=clip[15]-clip[14];
		
		// Normalize it
		t=(float)Math.sqrt(frustum[4][0]*frustum[4][0]+frustum[4][1]*frustum[4][1]+frustum[4][2]*frustum[4][2]);
		frustum[4][0]/=t;
		frustum[4][1]/=t;
		frustum[4][2]/=t;
		frustum[4][3]/=t;
		
		// Extract the NEAR clipping plane.  This is last on purpose (see pointinfrustum() for reason)
		frustum[5][0]=clip[3]+clip[2];
		frustum[5][1]=clip[7]+clip[6];
		frustum[5][2]=clip[11]+clip[10];
		frustum[5][3]=clip[15]+clip[14];
		
		// Normalize it
		t=(float)Math.sqrt(frustum[5][0]*frustum[5][0]+frustum[5][1]*frustum[5][1]+frustum[5][2]*frustum[5][2]);
		frustum[5][0]/=t;
		frustum[5][1]/=t;
		frustum[5][2]/=t;
		frustum[5][3]/=t;
	}
	
	// Test If A Point Is In The Frustum.
	public boolean point(Vec3f pos){
		return point(pos.x, pos.y, pos.z);
	}
	
	public boolean point(float x, float y, float z){
		for(int p=0;p<6;p++)
			if(frustum[p][0]*x+frustum[p][1]*y+frustum[p][2]*z+frustum[p][3]<=0)
				return false;
		return true;
	}
	
	public boolean sphere(Vec3f pos, float radius){
		return sphere(pos.x, pos.y, pos.z, radius);
	}
	
	// Test If A Sphere Is In The Frustum
	public boolean sphere(float x, float y, float z, float radius){
		if(radius<0.0001)return point(x, y, z);
		for(int p=0;p<6;p++)
			if(frustum[p][0]*x+frustum[p][1]*y+frustum[p][2]*z+frustum[p][3]<=-radius)
				return false;
		return true;
	}
	
	public boolean cube(Vec3f pos, float size){
		return cube(pos.x, pos.y, pos.z, size, size, size);
	}
	
	// Test If A Cube Is In The Frustum
	public boolean cube(float x, float y, float z, float size){
		return cube(x, y, z, size, size, size);
	}
	
	public boolean cube(Vec3f pos, float sizeX, float sizeY, float sizeZ){
		return cube(pos.x, pos.y, pos.z, sizeX, sizeY, sizeZ);
	}
	
	public boolean cube(float x, float y, float z, float sizeX, float sizeY, float sizeZ){
		for(int p=0;p<6;p++){
			if(frustum[p][0]*(x-sizeX)+frustum[p][1]*(y-sizeY)+frustum[p][2]*(z-sizeZ)+frustum[p][3]>0) continue;
			if(frustum[p][0]*(x+sizeX)+frustum[p][1]*(y-sizeY)+frustum[p][2]*(z-sizeZ)+frustum[p][3]>0) continue;
			if(frustum[p][0]*(x-sizeX)+frustum[p][1]*(y+sizeY)+frustum[p][2]*(z-sizeZ)+frustum[p][3]>0) continue;
			if(frustum[p][0]*(x+sizeX)+frustum[p][1]*(y+sizeY)+frustum[p][2]*(z-sizeZ)+frustum[p][3]>0) continue;
			if(frustum[p][0]*(x-sizeX)+frustum[p][1]*(y-sizeY)+frustum[p][2]*(z+sizeZ)+frustum[p][3]>0) continue;
			if(frustum[p][0]*(x+sizeX)+frustum[p][1]*(y-sizeY)+frustum[p][2]*(z+sizeZ)+frustum[p][3]>0) continue;
			if(frustum[p][0]*(x-sizeX)+frustum[p][1]*(y+sizeY)+frustum[p][2]*(z+sizeZ)+frustum[p][3]>0) continue;
			if(frustum[p][0]*(x+sizeX)+frustum[p][1]*(y+sizeY)+frustum[p][2]*(z+sizeZ)+frustum[p][3]>0) continue;
			return false;
		}
		return true;
	}
}
