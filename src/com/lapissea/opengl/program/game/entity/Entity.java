package com.lapissea.opengl.program.game.entity;

import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.game.events.Renderable;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.rendering.ModelTransformed;
import com.lapissea.opengl.program.rendering.gl.Renderer;
import com.lapissea.opengl.program.rendering.gl.shader.Shaders;
import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.program.util.math.MatrixUtil;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.assets.IModel;

public abstract class Entity implements Renderable,ModelTransformed{
	
	protected static final Vec3f	POS	=new Vec3f(),SCAL=new Vec3f();
	protected static final Quat4M	ROT	=new Quat4M();
	
	protected static final Matrix4f TRANS=new Matrix4f();
	
	public IModel		model;
	public final Vec3f	pos,scale;
	public final Quat4M	rot;
	private boolean		dead;
	public final World	world;
	
	public Entity(World world, IModel model, Vec3f pos){
		this.model=model;
		this.pos=initPos(pos);
		this.world=world;
		rot=initRot();
		scale=initScale();
	}
	
	protected Vec3f initPos(Vec3f pos){
		return pos;
	}
	
	protected Quat4M initRot(){
		return new Quat4M();
	}
	
	protected Vec3f initScale(){
		return new Vec3f(1, 1, 1);
	}
	
	public void kill(){
		dead=true;
		world.markDeadDirty();
	}
	
	public boolean isDead(){
		return dead;
	}
	
	public Quat4M getModelRot(){
		ROT.set(rot);
		return ROT;
	}
	
	public Vec3f getModelPos(){
		return POS.set(pos);
	}
	
	public Vec3f getModelScale(){
		return SCAL.set(scale);
	}
	
	@Override
	public IModel getModel(){
		return model;
	}
	
	@Override
	public void render(){
		Renderer r=getRenderer();
		r.notifyEntityRender();
		if(!model.isLoaded()||!model.getFrustrumShape().withTransform(getModelScale(), getModelRot()).isVisibleAt(getModelPos(), r.frustrum)) return;
		r.notifyEntityActualRender();
		
		Shaders.ENTITY.renderBatched(this);
	}
	
	@Override
	public Matrix4f getTransform(){
		TRANS.setIdentity();
		
		return MatrixUtil.createTransformMat(TRANS, getModelPos(), getModelRot(), getModelScale());
	}
}
