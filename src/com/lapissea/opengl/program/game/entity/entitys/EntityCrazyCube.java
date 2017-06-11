package com.lapissea.opengl.program.game.entity.entitys;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.linearmath.Transform;
import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.game.entity.EntityUpd;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.rendering.gl.model.ObjModelLoader;
import com.lapissea.opengl.program.rendering.gl.shader.light.LineLight;
import com.lapissea.opengl.program.util.MotionStateM;
import com.lapissea.opengl.program.util.RigidBodyEntity;
import com.lapissea.opengl.program.util.math.PartialTick;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.util.color.ColorM;
import com.lapissea.opengl.window.assets.IModel;

public class EntityCrazyCube extends EntityUpd{
	
	public static IModel MODEL;
	//	private static float[] VERT={
	//			-0.5f,0.5f,-0.5f,
	//			-0.5f,-0.5f,-0.5f,
	//			0.5f,-0.5f,-0.5f,
	//			0.5f,0.5f,-0.5f,
	//			
	//			-0.5f,0.5f,0.5f,
	//			-0.5f,-0.5f,0.5f,
	//			0.5f,-0.5f,0.5f,
	//			0.5f,0.5f,0.5f,
	//			
	//			0.5f,0.5f,-0.5f,
	//			0.5f,-0.5f,-0.5f,
	//			0.5f,-0.5f,0.5f,
	//			0.5f,0.5f,0.5f,
	//			
	//			-0.5f,0.5f,-0.5f,
	//			-0.5f,-0.5f,-0.5f,
	//			-0.5f,-0.5f,0.5f,
	//			-0.5f,0.5f,0.5f,
	//			
	//			-0.5f,0.5f,0.5f,
	//			-0.5f,0.5f,-0.5f,
	//			0.5f,0.5f,-0.5f,
	//			0.5f,0.5f,0.5f,
	//			
	//			-0.5f,-0.5f,0.5f,
	//			-0.5f,-0.5f,-0.5f,
	//			0.5f,-0.5f,-0.5f,
	//			0.5f,-0.5f,0.5f
	//	
	//	};
	
	static int[] indices={
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
	
	private static IModel getModel0(){
		if(MODEL==null){
			MODEL=ObjModelLoader.loadAndBuild("FancyCube");
		}
		return MODEL;
	}
	
	public final Vec3f	rotSpeed=new Vec3f();
	final Transform		transform;
	public ColorM		lightColor;
	LineLight			light;
	
	public EntityCrazyCube(World world, Vec3f pos){
		super(world, getModel0(), pos);
		transform=new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(pos.x, pos.y, pos.z), 0.5F));
		
		scale.set(2, 2, 2);
		
		float massKg=0.5F*scale.x*scale.y*scale.z;
		
		
		physicsBody=new RigidBodyEntity(this, massKg, new MotionStateM(transform), new BoxShape(new Vector3f(scale.x/2, scale.y/2, scale.z/2)), null, 0.9F, 0).antiTunnel();
		physicsBody.setDamping(0.15F, 0.15F);
	}
	
	@Override
	protected Vec3f initPos(Vec3f pos){
		return pos;
	}
	
	@Override
	public void update(){
		model.getMaterial(2).getDiffuse().set(177/256F, 0, 177/256F, 1);
		model.getMaterial(1).getDiffuse().set(0x00C7E7);
		model.getMaterial(1).getAmbient().set(0x00C7E7).a(1);
		model.getMaterial(0).getDiffuse().set(0x0000FF);
		//physicsBody.setDamping(0.5F, 0.5F);
		
		updatePrevs();
		if(Float.isNaN(pos.x())){
			kill();
		}
		pos.set(transform.origin.x, transform.origin.y, transform.origin.z);
		transform.getRotation(rot);
	}
	
	@Override
	public void preRender(){
		if(lightColor!=null){
			MODEL.getMaterial(0).setLightTroughput(1F);
//			if(light==null) 
				light=new LineLight(new Vec3f(), new Vec3f(), lightColor, new Vec3f(1F, 0.01F, 0.01F/(scale.max()*scale.max())));
			PartialTick.calc(light.pos1, prevPos, pos);
			PartialTick.calc(light.pos2, prevPos, pos);
			double angle=((world.time()+Game.getPartialTicks())/10D+light.pos2.length()/2)%(Math.PI*2);
			light.pos2.x+=25*Math.sin(angle);
			light.pos2.z+=25*Math.cos(angle);
			light.pos2.y+=5;
			light.pos1.y+=5;
			getRenderer().drawLine(light.pos1, light.pos2, light.color);
			getRenderer().addLight(light);
		}
	}
	
	
}
