package com.lapissea.opengl.game.entity.entitys;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.input.Mouse;

import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.lapissea.opengl.core.Game;
import com.lapissea.opengl.game.entity.EntityUpd;
import com.lapissea.opengl.game.physics.jbullet.PhysicsObjJBullet;
import com.lapissea.opengl.game.world.World;
import com.lapissea.opengl.rendering.Camera;
import com.lapissea.opengl.rendering.shader.light.PointLight;
import com.lapissea.opengl.resources.model.ModelLoader;
import com.lapissea.opengl.util.BlackBody;
import com.lapissea.opengl.util.Rand;
import com.lapissea.opengl.util.math.PartialTick;
import com.lapissea.opengl.util.math.SimplexNoise;
import com.lapissea.opengl.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.util.color.ColorM;
import com.lapissea.opengl.window.assets.IModel;

public class EntityCrazyCube extends EntityUpd{
	
	public static IModel MODEL;
	
	private static IModel getModel0(){
		if(MODEL==null){
//			MODEL=ModelLoader.loadAndBuild("Cubdroid.obj");
//			MODEL=ModelLoader.loadAndBuild("FancyCube");
			MODEL=ModelLoader.loadAndBuild("icosphere radius 0.5--triangulated.obj");
		}
		return MODEL;
	}
	
	public ColorM	lightColor;
	PointLight		light;
	
	public static EntityCrazyCube CAM;
	
	public EntityCrazyCube(World world, Vec3f pos){
		super(world, getModel0(), pos);
		float s=Rand.f()+1;
		scale.set(s, s, s);
		
//		model.getMaterial(2).getDiffuse().set(177/256F, 0, 177/256F, 1);
//		model.getMaterial(1).getDiffuse().set(0x00C7E7);
//		model.getMaterial(1).getAmbient().set(0x00C7E7).a(1);
//		model.getMaterial(0).getDiffuse().set(0x0000FF);
		
		float massKg=0.5F*scale.x*scale.y*scale.z;
		
		if(CAM==null) CAM=this;
		
		getPhysicsObj().init(massKg, new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(pos.x, pos.y, pos.z), 0.5F)), new SphereShape(scale.x/2), Vec3f.single(0.9F));
//		getPhysicsObj().init(massKg, new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(pos.x, pos.y, pos.z), 0.5F)), new BoxShape(new Vector3f(scale.x/2, scale.y/2, scale.z/2)), Vec3f.single(0.9F));
		getPhysicsObj().body.setDamping(0.15F, 0.15F);
		getPhysicsObj().hookPos(this.pos);
		getPhysicsObj().hookRot(rot);
	}
	
	@Override
	protected Vec3f initPos(Vec3f pos){
		return pos;
	}
	
	@Override
	public void update(){
		if(Float.isNaN(pos.x())) kill();
		updatePrevs();
//		LogUtil.println(model);
//		LogUtil.println(model.getMaterial(1));
//		LogUtil.println(pos);
//		pos.y-=0.2;
//		float h=world.getHeightAt(pos.x, pos.z)+scale.y/2;
//		if(pos.y<h) pos.y=h;
		
		if(this==CAM){
			if(Mouse.isButtonDown(1)){
				for(EntityUpd c:world.entitysUpd){
					if(!(c instanceof EntityCrazyCube)) return;
					//				if(((EntityCrazyCube)c).lightColor==null)return;
					
					RigidBody controlBall=c.getPhysicsObj().body;
					if(controlBall==null) continue;
					
					MotionState m=controlBall.getMotionState();
					Transform ts=m.getWorldTransform(new Transform());
					Vector3f p=ts.origin;
					Camera cm=Game.get().renderer.getCamera();
					Vector3f cmp=new Vector3f(cm.pos.x, cm.pos.y, cm.pos.z);
					Vector3f force=new Vector3f();
					
					force.sub(cmp, p);
					force.x*=1;
					force.y*=1;
					force.z*=1;
					controlBall.activate();
					controlBall.applyCentralForce(force);
				}
			}
			if(Mouse.isButtonDown(0)){
				Camera c=Game.get().renderer.getCamera();
				Vec3f rot=c.rot;
				double xCos=Math.cos(-rot.x);
				double xSin=Math.sin(-rot.x);
				double yCos=Math.cos(rot.y-Math.PI/2);
				double ySin=Math.sin(rot.y-Math.PI/2);
				Vector3f vc=new Vector3f((float)(yCos*xCos*100), (float)xSin*100, (float)(ySin*xCos)*100);
				PhysicsObjJBullet hit=world.rayTrace(new Vec3f(c.pos.x, c.pos.y, c.pos.z), new Vec3f(c.pos.x+vc.x, c.pos.y+vc.y, c.pos.z+vc.z),null);
				if(hit!=null){
					float siz=1;
					hit.applyForce(vc.x*siz, vc.y*siz, vc.z*siz);
				}
			}
		}
	}
	
	@Override
	public void preRender(){
//		LogUtil.println(MODEL.getVertexCount());
		if(lightColor!=null){
			if(light==null) light=new PointLight(new Vec3f(), lightColor, new Vec3f(1F, 0.01F, 0.01F/(scale.max()*scale.max())));
			BlackBody.fromKelvin(light.color, (float)(prevPos.distanceTo(pos)*2000+1000+50+50*SimplexNoise.noise(hashCode(), (world.time()+Game.getPartialTicks())/5D)));
			PartialTick.calc(light.pos, prevPos, pos);
			
			getRenderer().addLight(light);
		}
	}
	
}
