package com.lapissea.opengl.program.game.entity.entitys;

import com.lapissea.opengl.program.game.entity.EntityUpd;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.opengl.assets.Model;
import com.lapissea.opengl.program.rendering.gl.model.ModelLoader;
import com.lapissea.opengl.program.rendering.gl.shader.light.PointLight;
import com.lapissea.opengl.program.util.RandUtil;
import com.lapissea.opengl.program.util.color.ColorM;
import com.lapissea.opengl.program.util.math.PartialTick;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class EntityLight extends EntityUpd{
	
	public static Model MODEL;
	
	private static Model getModel0(){
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
			MODEL=ModelLoader.buildModel("LileCube", false, "vertices", vertices, "uvs", uvs, "indices", indices, "genNormals", true, "textures", new String[]{"WCveg256"});
		}
		return MODEL;
	}
	
	ColorM		color;
	PointLight	light;
	Vec3f		origin;
	double		random	=RandUtil.RD()*Math.PI*2,speed=0.5+RandUtil.RD();
	
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
		light.attenuation.x=0.9F+RandUtil.RF(0.1);
		pos.y=origin.y+(float)Math.sin((world.time()/(10*speed)+random)%(Math.PI*2))*3;
	}
}
