package com.lapissea.opengl.program.game.world;

import static com.bulletphysics.linearmath.DebugDrawModes.*;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import org.lwjgl.input.Mouse;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.IDebugDraw;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.lapissea.opengl.abstr.opengl.assets.ITexture;
import com.lapissea.opengl.abstr.opengl.events.Updateable;
import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.game.Camera;
import com.lapissea.opengl.program.game.entity.Entity;
import com.lapissea.opengl.program.game.entity.EntityUpd;
import com.lapissea.opengl.program.game.entity.entitys.EntityCrazyCube;
import com.lapissea.opengl.program.game.entity.entitys.EntityTree;
import com.lapissea.opengl.program.game.terrain.Terrain;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModuleLight;
import com.lapissea.opengl.program.util.LogUtil;
import com.lapissea.opengl.program.util.RandUtil;
import com.lapissea.opengl.program.util.color.IColorM;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class World{
	
	public DynamicsWorld	bulletWorld;
	private List<Entity>	entitys		=new ArrayList<>();
	private List<EntityUpd>	entitysUpd	=new ArrayList<>();
	private boolean			checkDead;
	private long			ticksPassed;
	private double			dayDuration	=200;
	
	public final List<Terrain> terrains=new ArrayList<>();
	
	public World(){
		setUpPhysics();
		setUpEntity();
	}
	
	private void setUpPhysics(){
		BroadphaseInterface broadphase=new DbvtBroadphase();
		CollisionConfiguration collisionConfiguration=new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher=new CollisionDispatcher(collisionConfiguration);
		ConstraintSolver constraintSolver=new SequentialImpulseConstraintSolver();
		
		bulletWorld=new DiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfiguration);
		bulletWorld.setGravity(new Vector3f(0, -9.81f, 0));
		bulletWorld.setDebugDrawer(new IDebugDraw(){
			
			@Override
			public void setDebugMode(int debugMode){
			
			}
			
			@Override
			public void reportErrorWarning(String warningString){
				LogUtil.printlnEr(warningString);
			}
			
			@Override
			public int getDebugMode(){
				boolean draw=false;
				return draw?DRAW_WIREFRAME|DRAW_CONTACT_POINTS|MAX_DEBUG_DRAW_MODE|ENABLE_CCD:0;
			}
			
			@Override
			public void drawLine(Vector3f from, Vector3f to, Vector3f color){
				Game.get().renderer.drawLine(from, to, color);
			}
			
			@Override
			public void drawContactPoint(Vector3f PointOnB, Vector3f normalOnB, float distance, int lifeTime, Vector3f color){
				Vector3f p2=new Vector3f(PointOnB);
				p2.add(normalOnB);
				Game.get().renderer.drawLine(PointOnB, p2, color);
			}
			
			@Override
			public void draw3dText(Vector3f location, String textString){
				
			}
		});
		//		CollisionShape ground=new StaticPlaneShape(new Vector3f(0, 1, 0), 0);
		//		MotionState groundMotionState=new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, 0, 0), 1)));
		//		RigidBodyConstructionInfo groundBodyConstructionInfo=new RigidBodyConstructionInfo(0, groundMotionState, ground, new Vector3f());
		//		groundBodyConstructionInfo.restitution=0.3F;
		//		dynamicsWorld.addRigidBody(new RigidBody(groundBodyConstructionInfo));
		
	}
	
	private void setUpEntity(){
		
		ITexture texture=null;
		int tileNum=20;
		for(int x=0;x<tileNum;x++){
			for(int z=0;z<tileNum;z++){
				Terrain t=new Terrain(x-tileNum/2, z-tileNum/2, texture);
				terrains.add(t);
				bulletWorld.addRigidBody(t.chunkBody);
			}
		}
		
		int worldSize=100;
		
		List<EntityTree> t=new ArrayList<>(100);
		for(int i=0, j=1;i<j;i++){
			t.add(new EntityTree(this, new Vec3f(RandUtil.CRF(worldSize*1.5), 0, RandUtil.CRF(worldSize*1.5))));
		}
		
		for(int i=0, j=20;i<j;i++){
			EntityCrazyCube c;
			spawn(c=new EntityCrazyCube(this, new Vec3f(RandUtil.CRF(100), 20+RandUtil.CRF(10), RandUtil.CRF(100))));
			if(i<ShaderModuleLight.MAX_POINT_LIGHT) c.lightColor=IColorM.randomRGB();
		}
		
		t.forEach(this::spawn);
		
		//		for(int i=0, j=10;i<j;i++){
		//			spawn(new EntityLight(this, new Vec3f(RandUtil.CRF(worldSize*1.5), 2, RandUtil.CRF(worldSize*1.5)), IColorM.randomRGB()));
		//		}
		//spawn(new EntityPlayer(this, new Vec3f(0, 0, 0)));
	}
	
	public void spawn(Entity e){
		if(e instanceof EntityUpd){
			EntityUpd eu=(EntityUpd)e;
			RigidBody b=eu.getPhysicsBody();
			
			entitysUpd.add(eu);
			
			if(b!=null) bulletWorld.addRigidBody(b);
		}
		entitys.add(e);
	}
	
	public void update(){
		bulletWorld.stepSimulation(1F/Game.get().timer.getUps());
		bulletWorld.debugDrawWorld();
		Game.get().renderer.lines.clearIfEmpty();
		
		entitysUpd.stream().forEach(Updateable::update);
		if(Mouse.isButtonDown(1)){
			for(EntityUpd c:entitysUpd){
				if(!(c instanceof EntityCrazyCube)) return;
				//				if(((EntityCrazyCube)c).lightColor==null)return;
				
				RigidBody controlBall=c.getPhysicsBody();
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
			
			ClosestRayResultCallback v=new ClosestRayResultCallback(new Vector3f(c.pos.x, c.pos.y, c.pos.z), new Vector3f(c.pos.x+vc.x, c.pos.y+vc.y, c.pos.z+vc.z));
			bulletWorld.rayTest(v.rayFromWorld, v.rayToWorld, v);
			if(v.hasHit()){
				if(v.collisionObject!=null&&v.collisionObject.getUserPointer() instanceof EntityCrazyCube){
					v.collisionObject.activate();
					float siz=1;
					((RigidBody)v.collisionObject).applyCentralForce(new Vector3f(vc.x*siz, vc.y*siz, vc.z*siz));
				}
			}
		}
		
		if(checkDead){
			entitysUpd.removeIf(Entity::isDead);
			checkDead=false;
		}
		ticksPassed++;
		
	}
	
	public List<Entity> getAll(){
		return entitys;
	}
	
	public long time(){
		return ticksPassed;
	}
	
	public void markDeadDirty(){
		checkDead=true;
	}
	
	public double getSunPos(){
		return getSunPos(0);
	}
	
	public double getSunPos(double pt){
		return ((time()+pt)/dayDuration)%1;
//		return 0.75;
//		return 0.25;
	}
	
	public double getSunBrightness(){
		return getSunBrightness(0);
	}
	
	public double getSunBrightness(float pt){
		double pos=getSunPos(pt),gradientSize=0.1;
		
		if(pos<0.5){
			if(pos<gradientSize) return 0.5+(pos/gradientSize)/2;
			pos=0.5-pos;
			if(pos<gradientSize) return 0.5+(pos/gradientSize)/2;
			return 1;
		}
		else{
			pos-=0.5;
			
			if(pos<gradientSize) return 0.5-(pos/gradientSize)/2;
			pos=0.5-pos;
			if(pos<gradientSize) return 0.5-(pos/gradientSize)/2;
			return 0;
		}
		
	}
	
}
