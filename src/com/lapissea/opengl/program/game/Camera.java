package com.lapissea.opengl.program.game;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.program.util.math.MatrixUtil;
import com.lapissea.opengl.program.util.math.PartialTick;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class Camera{
	
	private static final Vec3f EFF_POS=new Vec3f(),MOVE=new Vec3f();
	
	public Vec3f	pos	=new Vec3f(),rot=new Vec3f(),prevPos=new Vec3f(),prevRot=new Vec3f();
	public float	zoom=1,farPlane=1000,nearPlane=0.1F,fov=80,lastRenderFow;
	
	private final Matrix4f	projection		=new Matrix4f();
	protected boolean		projectionDirty	=true;
	public boolean			noMouseMode		=false;
	public Vec3f			activeRotVec=new Vec3f();
	public Quat4M			activeRotQuat=new Quat4M();
	
	public void update(){
		
		prevPos.set(pos);
		prevRot.set(rot);
		
		MOVE.set(0, 0, 0);
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) MOVE.addX(1);
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) MOVE.addX(-1);
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) MOVE.addY(1);
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) MOVE.addY(-1);
		
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) MOVE.addZ(1);
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) MOVE.addZ(-1);
		
		Vector4f vec4=new Vector4f(MOVE.x, MOVE.y, MOVE.z, 1);
		Matrix4f mat=new Matrix4f();
		EFF_POS.set(rot).mul(-1);
		EFF_POS.x=0;
		MatrixUtil.rotate(mat, EFF_POS);
		
		Matrix4f.transform(mat, vec4, vec4);
		MOVE.x=vec4.x;
		MOVE.y=vec4.y;
		MOVE.z=vec4.z;
		
		pos.add(MOVE.mul(1F));
		
		if(Game.win().isFocused()){
			if(noMouseMode){
				if(Keyboard.isKeyDown(Keyboard.KEY_Q)) rot.y-=0.1;
				if(Keyboard.isKeyDown(Keyboard.KEY_E)) rot.y+=0.1;
				if(Keyboard.isKeyDown(Keyboard.KEY_R)) rot.x-=0.1;
				if(Keyboard.isKeyDown(Keyboard.KEY_F)) rot.x+=0.1;
			}
			else{
				float yAmmount=(Mouse.getX()-Display.getWidth()/2)/100F;
				rot.y+=yAmmount;
				rot.x-=(Mouse.getY()-Display.getHeight()/2)/100F;
				Game.win().centerMouse();
			}
		}
		
		if(rot.x>Math.PI/2) rot.x=(float)(Math.PI/2);
		else if(rot.x<-Math.PI/2) rot.x=(float)(-Math.PI/2);
		
	}
	
	public Matrix4f createView(Matrix4f dest){
		dest.setIdentity();
		MatrixUtil.rotateZXY(dest, PartialTick.calc(activeRotVec, prevRot, rot));
		dest.translate(PartialTick.calc(EFF_POS, prevPos, pos).mul(-1F));
		activeRotVec.mul(-1);
		activeRotQuat.set(activeRotVec);
		return dest;
	}
	
	public void createProjection(Matrix4f dest){
		//		float fov=this.fov/zoom;
		//		
		//		if(lastRenderFow==fov){
		//			injectProjection(dest);
		//			return;
		//		}
		//		lastRenderFow=fov;
		calcProjection();
		injectProjection(dest);
	}
	
	public void calcProjection(){
		
		float aspectRatio=(float)Display.getWidth()/(float)Display.getHeight();
		float y_scale=(float)((1f/Math.tan(Math.toRadians(this.fov/zoom/2f)))*aspectRatio);
		float x_scale=y_scale/aspectRatio;
		float frustumLength=farPlane-nearPlane;
		
		projection.setIdentity();
		projection.m00=x_scale;
		projection.m11=y_scale;
		projection.m22=-((farPlane+nearPlane)/frustumLength);
		projection.m23=-1;
		projection.m32=-((2*nearPlane*farPlane)/frustumLength);
		projection.m33=0;
	}
	
	private void injectProjection(Matrix4f dest){
		dest.m00=projection.m00;
		dest.m11=projection.m11;
		dest.m22=projection.m22;
		dest.m23=projection.m23;
		dest.m32=projection.m32;
		dest.m33=projection.m33;
	}
}
