package com.lapissea.opengl.program.game.particle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.lapissea.opengl.program.rendering.gl.shader.Shaders;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.impl.assets.Model;

public class ParticleHandler<T extends Particle<T>>{
	
	private final LinkedList<T>			particles	=new LinkedList<>();
	public final Class<T>				type;
	private boolean						deadDirty;
	private final ParticleFactory<T>	factory;
	
	public final List<Model>				models	=new ArrayList<>();
	private final List<List<Particle<?>>>	toRender=new ArrayList<>();
	
	public interface ParticleFactory<T extends Particle<T>>{
		
		T create(ParticleHandler<T> parent, Vec3f pos);
	}
	
	@SuppressWarnings("unchecked")
	public ParticleHandler(ParticleFactory<T> factory){
		this.type=(Class<T>)factory.create(this, new Vec3f()).getClass();
		this.factory=factory;
	}
	
	public T spawn(Vec3f pos){
		T n6w=factory.create(this, pos);
		particles.add(n6w);
		return n6w;
	}
	
	public void update(){
		if(deadDirty){
			particles.removeIf(p->{
				if(p.isDead()) return true;
				p.update();
				return false;
			});
			deadDirty=false;
		}
		else particles.forEach(Particle::update);
	}
	
	public void render(){
		if(models.size()!=toRender.size()){
			while(models.size()>toRender.size()){
				toRender.add(new ArrayList<>());
			}
			while(models.size()<toRender.size()){
				toRender.remove(toRender.size()-1);
			}
		}
		
		particles.forEach(p->toRender.get(p.getModelIndex()).add(p));
		toRender.forEach(batch->{
			Shaders.ENTITY.renderBatch(batch);
			batch.clear();
		});
		
	}
	
	public void notifyDeath(){
		deadDirty=true;
	}
	
	public int getCount(){
		return particles.size();
	}
	
}
