package com.lapissea.opengl.program.game.particle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import org.lwjgl.opengl.GL11;

import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.GLUtil.BlendFunc;
import com.lapissea.opengl.program.rendering.gl.shader.Shaders;
import com.lapissea.opengl.program.util.NanoTimer;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.assets.IModel;

@SuppressWarnings("unchecked")
public class ParticleHandler<T extends Particle<T>>{
	
	private final List<T>			particles	=new ArrayList<>();
//	public final Class<T>				type;
	private boolean						deadDirty;
	private final ParticleFactory<T>	factory;
	
	public final List<IModel>	models	=new ArrayList<>();
	private Queue<Particle<?>>[]	toRender=new Queue[0];
	
	NanoTimer upd=new NanoTimer(),rend=new NanoTimer();
	
	public interface ParticleFactory<T extends Particle<T>>{
		
		T create(ParticleHandler<T> parent, Vec3f pos);
	}
	
	public ParticleHandler(ParticleFactory<T> factory){
		this.factory=factory;
	}
	
	public T spawn(Vec3f pos){
		T n6w=factory.create(this, pos);
		particles.add(n6w);
		return n6w;
	}
	
	public void update(){
		upd.start();
		if(deadDirty){
			particles.removeIf(p->{
				if(p.isDead()) return true;
				p.update();
				return false;
			});
			deadDirty=false;
		}
		else particles.forEach(Particle::update);
		upd.end();
//		LogUtil.println(upd.msAvrg100(),rend.msAvrg100());
	}
	
	public void render(){
		if(models.size()!=toRender.length){
			toRender=new Queue[models.size()];
			Arrays.fill(toRender, new ArrayDeque<>());
		}
		
		rend.start();
		particles.forEach(p->toRender[p.getModelIndex()].add(p));
		GLUtil.BLEND.set(true);
		GLUtil.BLEND_FUNC.set(BlendFunc.ADD);
		GL11.glDepthMask(false);
		for(Queue<Particle<?>> batch:toRender){
			Shaders.ENTITY.renderBatch(batch);
			batch.clear();
		}
		rend.end();
		GLUtil.BLEND_FUNC.set(BlendFunc.NORMAL);
		GL11.glDepthMask(true);
	}
	
	public void notifyDeath(){
		deadDirty=true;
	}
	
	public int getCount(){
		return particles.size();
	}
	
}
