package com.lapissea.opengl.program.game.physics.jbullet;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;

class TransformM extends Transform{
	
	Quat4f q=new Quat4f();
	
	final PhysicsObjJBullet parent;
	
	public TransformM(PhysicsObjJBullet parent){
		super();
		this.parent=parent;
	}
	
	void setHook(){
		if(parent.pos!=null){
			parent.pos.x(origin.x);
			parent.pos.y(origin.y);
			parent.pos.z(origin.z);
		}
		if(parent.rot!=null){
			MatrixUtil.getRotation(basis, q);
			parent.rot.set(q.x, q.y, q.z, q.w);
		}
	}
	
	@Override
	public void setFromOpenGLMatrix(float[] m){
		super.setFromOpenGLMatrix(m);
		setHook();
	}
	
	@Override
	public void set(Transform tr){
		super.set(tr);
		setHook();
	}
	
	@Override
	public void set(Matrix3f mat){
		super.set(mat);
		setHook();
	}
	
	@Override
	public void set(Matrix4f mat){
		super.set(mat);
		setHook();
	}
	
	@Override
	public void setIdentity(){
		super.setIdentity();
		setHook();
	}
	
	@Override
	public void mul(Transform tr){
		super.mul(tr);
		setHook();
	}
	
	@Override
	public void mul(Transform tr1, Transform tr2){
		super.mul(tr1, tr2);
		setHook();
	}
	
}
