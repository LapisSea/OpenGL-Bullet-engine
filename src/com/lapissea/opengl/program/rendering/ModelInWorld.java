package com.lapissea.opengl.program.rendering;

import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.rendering.gl.model.Model;
import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.program.util.math.Maths;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public interface ModelInWorld{
	
	Matrix4f mat=new Matrix4f();
	
	Model getModel();
	
	Vec3f getModelScale();
	
	Quat4M getModelRot();
	
	Vec3f getModelPos();
	
	default Matrix4f getTransform(){
		mat.setIdentity();
		return Maths.createTransformMat(mat, getModelPos(), getModelRot(), getModelScale());
	}
	
	static ModelInWorld singleUse(Matrix4f worldTransform, Model model){
		return GLUtil.singleUse0(worldTransform, model);
	}
	
}
