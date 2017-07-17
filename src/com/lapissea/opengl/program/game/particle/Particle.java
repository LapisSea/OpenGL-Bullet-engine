package com.lapissea.opengl.program.game.particle;

import javax.vecmath.Vector3f;

import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.ModelTransformed;
import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.program.util.math.MatrixUtil;
import com.lapissea.opengl.program.util.math.PartialTick;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.assets.IModel;

public class Particle<T extends Particle<T>> implements ModelTransformed{
	
	static final Matrix4f TRANS=new Matrix4f();
	
	protected static final Vec3f	POS			=new Vec3f(),SCAL=new Vec3f(),ACTELERATION=new Vec3f();
	protected static final Quat4M	ROT			=new Quat4M();
	protected static final Vector3f	GRAVITY_VEC	=new Vector3f();
	
	protected final ParticleHandler<T>	handler;
	public Vec3f						pos,prevPos,speed=new Vec3f(),scale,prevScale;
	public float						gravity;
	private boolean						dead;
	public int							age;
	
	public Particle(ParticleHandler<T> handler, Vec3f pos){
		this.handler=handler;
		this.pos=new Vec3f(pos);
		this.prevPos=new Vec3f(pos);
		
		this.scale=new Vec3f(1, 1, 1);
		this.prevScale=new Vec3f(1, 1, 1);
	}
	
	public void update(){
		updatePrevs();
		age++;
		
		if(gravity!=0) speed.addY(gravity);
		pos.add(speed);
	}
	
	public void updatePrevs(){
		prevPos.set(pos);
		prevScale.set(scale);
	}
	
	public void kill(){
		dead=true;
		handler.notifyDeath();
	}
	
	public boolean isDead(){
		return dead;
	}
	
	public Vec3f getModelPos(){
		return PartialTick.calc(POS, prevPos, pos);
	}
	
	public Vec3f getModelScale(){
		return PartialTick.calc(SCAL, prevScale, scale);
	}
	
	@Override
	public IModel getModel(){
		return handler.models.get(getModelIndex());
	}
	
	public int getModelIndex(){
		return 0;
	}
	
	@Override
	public Matrix4f getTransform(){
		TRANS.setIdentity();
		
		return MatrixUtil.createTransformMat(TRANS, getModelPos(), Game.get().renderer.getCamera().activeRotQuat, getModelScale());
	}
	
}
