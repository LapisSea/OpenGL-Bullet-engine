package com.lapissea.opengl.program.util.math;

import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class MatrixUtil{
	
	private static final Vec3f		X_AXIS	=new Vec3f(1, 0, 0),Y_AXIS=new Vec3f(0, 1, 0),Z_AXIS=new Vec3f(0, 0, 1);
	private static final Matrix4f	ROT_MAT	=new Matrix4f();
	
	public static Matrix4f createTransformMat(Vec3f translation, Quat4M rotation, Vec3f scale){
		return createTransformMat(new Matrix4f(), translation, rotation, scale);
	}
	
	public static Matrix4f createTransformMat(Vec3f translation, Vec3f scale){
		return createTransformMat(new Matrix4f(), translation, scale);
	}
	
	public static Matrix4f createTransformMat(Matrix4f src, Vec3f translation, Vec3f scale){
		return src.translate(translation).scale(scale);
	}
	
	public static Matrix4f createTransformMat(Matrix4f src, Vec3f translation, Vec3f rotation, Vec3f scale){
		return rotate(src.translate(translation), rotation).scale(scale);
	}
	
	public static Matrix4f createTransformMat(Matrix4f src, Vec3f translation, Quat4M rotation, Vec3f scale){
		return rotate(src.translate(translation), rotation).scale(scale);
	}
	
	public static synchronized Matrix4f rotate(Matrix4f mat, Quat4M rot){
		rot.quatToMatrix4f(ROT_MAT);
		return Matrix4f.mul(mat, ROT_MAT, mat);
		//return rotateXYZ(mat, new Vec3f().set(rot));
	}
	
	public static Matrix4f rotate(Matrix4f mat, Vec3f rot){
		return mat.rotate(rot.x(), X_AXIS).rotate(rot.y(), Y_AXIS).rotate(rot.z(), Z_AXIS);
	}
	
	public static Matrix4f rotateX(Matrix4f mat, float angle){
		return mat.rotate(angle, X_AXIS);
	}
	
	public static Matrix4f rotateY(Matrix4f mat, float angle){
		return mat.rotate(angle, Y_AXIS);
	}
	
	public static Matrix4f rotateZ(Matrix4f mat, float angle){
		return mat.rotate(angle, Z_AXIS);
	}
	
	public static Matrix4f rotateZXY(Matrix4f mat, Vec3f rot){
		return mat.rotate(rot.z(), Z_AXIS).rotate(rot.x(), X_AXIS).rotate(rot.y(), Y_AXIS);
	}
	
	public static boolean equals(Matrix4f mat1, Matrix4f mat2){
		if(mat1==null?mat2==null:mat1==mat2) return true;
		return mat1.m00==mat2.m00&&
				mat1.m01==mat2.m01&&
				mat1.m02==mat2.m02&&
				mat1.m03==mat2.m03&&
				
				mat1.m10==mat2.m10&&
				mat1.m11==mat2.m11&&
				mat1.m12==mat2.m12&&
				mat1.m13==mat2.m13&&
				
				mat1.m20==mat2.m20&&
				mat1.m21==mat2.m21&&
				mat1.m22==mat2.m22&&
				mat1.m23==mat2.m23&&
				
				mat1.m30==mat2.m30&&
				mat1.m31==mat2.m31&&
				mat1.m32==mat2.m32&&
				mat1.m33==mat2.m33;
	}
	
}
