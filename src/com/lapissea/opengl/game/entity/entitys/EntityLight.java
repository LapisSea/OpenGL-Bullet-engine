package com.lapissea.opengl.game.entity.entitys;

import com.lapissea.opengl.game.entity.EntityUpd;
import com.lapissea.opengl.game.world.World;
import com.lapissea.opengl.rendering.shader.light.PointLight;
import com.lapissea.opengl.resources.model.ModelBuilder;
import com.lapissea.opengl.resources.model.ModelLoader;
import com.lapissea.opengl.util.Rand;
import com.lapissea.opengl.util.math.PartialTick;
import com.lapissea.opengl.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.util.color.ColorM;
import com.lapissea.opengl.window.assets.IModel;

public class EntityLight extends EntityUpd{
	
	public static IModel MODEL;
	
	private static IModel getModel0(){
		if(MODEL==null){
			
			float[] vertices={
					-0.5f,0.5f,-0.5f,
					-0.5f,-0.5f,-0.5f,
					0.5f,-0.5f,-0.5f,
					0.5f,0.5f,-0.5f,
					
					-0.5f,0.5f,0.5f,
					-0.5f,-0.5f,0.5f,
					0.5f,-0.5f,0.5f,
					0.5f,0.5f,0.5f,
					
					0.5f,0.5f,-0.5f,
					0.5f,-0.5f,-0.5f,
					0.5f,-0.5f,0.5f,
					0.5f,0.5f,0.5f,
					
					-0.5f,0.5f,-0.5f,
					-0.5f,-0.5f,-0.5f,
					-0.5f,-0.5f,0.5f,
					-0.5f,0.5f,0.5f,
					
					-0.5f,0.5f,0.5f,
					-0.5f,0.5f,-0.5f,
					0.5f,0.5f,-0.5f,
					0.5f,0.5f,0.5f,
					
					-0.5f,-0.5f,0.5f,
					-0.5f,-0.5f,-0.5f,
					0.5f,-0.5f,-0.5f,
					0.5f,-0.5f,0.5f
					
			};
			
			float[] uvs={
					
					0,0,
					0,1,
					1,1,
					1,0,
					0,0,
					0,1,
					1,1,
					1,0,
					0,0,
					0,1,
					1,1,
					1,0,
					0,0,
					0,1,
					1,1,
					1,0,
					0,0,
					0,1,
					1,1,
					1,0,
					0,0,
					0,1,
					1,1,
					1,0
					
			};
			
			int[] indices={
					3,1,0,
					2,1,3,
					
					4,5,7,
					7,5,6,
					
					11,9,8,
					10,9,11,
					
					12,13,15,
					15,13,14,
					
					19,17,16,
					18,17,19,
					
					20,21,23,
					23,21,22
			};
			for(int i=0;i<vertices.length;i++){
				vertices[i]/=10;
			}
			MODEL=ModelLoader.buildModel(new ModelBuilder().withName("LileCube").withVertecies(vertices).withUvs(uvs).withIndices(indices).generateNormals(true).withTextures("WCveg256"));
		}
		return MODEL;
	}
	
	ColorM		color;
	PointLight	light;
	Vec3f		origin;
	double		random	=Rand.d()*Math.PI*2,speed=0.5+Rand.d();
	
	public EntityLight(World world, Vec3f pos, ColorM color){
		super(world, getModel0(), pos);
		this.color=color;
		origin=pos.clone();
		light=new PointLight(pos.clone(), color, new Vec3f(1, 0, 0.003F));
		
		model.getMaterial(0).setLightTroughput(1);
	}
	
	@Override
	public void preRender(){
		PartialTick.calc(light.pos, prevPos, pos);
		light.color=color;
		
		getRenderer().pointLights.add(light);
	}
	
	@Override
	public void update(){
		updatePrevs();
		light.attenuation.x=0.9F+Rand.f(0.1);
		pos.y=origin.y+(float)Math.sin((world.time()/(10*speed)+random)%(Math.PI*2))*3;
	}
}
