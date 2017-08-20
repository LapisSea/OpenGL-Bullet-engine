package com.lapissea.opengl.program.rendering;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.util.math.MatrixUtil;
import com.lapissea.opengl.program.util.math.PartialTick;
import com.lapissea.opengl.program.util.math.vec.FloatSmooth;
import com.lapissea.opengl.program.util.math.vec.Quat4;
import com.lapissea.opengl.program.util.math.vec.Vec2f;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.events.MouseMoveEvent;
import com.lapissea.opengl.window.api.events.MouseMoveEvent.IMouseMoveEventListener;
import com.lapissea.opengl.window.api.events.MouseScrollEvent;

public class Camera implements IMouseMoveEventListener{
	
	private static final Vec3f EFF_POS=new Vec3f(),MOVE=new Vec3f();
	
	public final Vec3f			pos			=new Vec3f(),rot=new Vec3f(),prevPos=new Vec3f();
	public float				zoom		=1,farPlane=1000,nearPlane=0.1F,fov=1.39626F,lastRenderFow,viewDistanceWanted=0;
	public final FloatSmooth	viewDistance=new FloatSmooth(viewDistanceWanted);
	
	private final Matrix4f	projection		=new Matrix4f();
	protected boolean		projectionDirty	=true;
	public boolean			noMouseMode		=false;
	public Vec3f			activeRotVec	=new Vec3f();
	public Quat4			activeRotQuat	=new Quat4();
	
	public Camera(){}
	
	public Camera(Vec3f pos){
		this.pos.set(pos);
		prevPos.set(pos);
	}
	
	public void update(){
		viewDistance.update();
		prevPos.set(pos);
		
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
		MOVE.mul(100);
//		if(EntityCrazyCube.CAM!=null&&false){
//			pos.set(EntityCrazyCube.CAM.pos);
//		}else
		pos.add(MOVE);
		viewDistance.setValue((viewDistance.getValue()+viewDistanceWanted)/2);
	}
	
	public void onMouseScroll(MouseScrollEvent e){
		viewDistanceWanted-=e.absolute/120F*Math.sqrt(Math.abs(viewDistanceWanted));
	}
	
	@Override
	public void onMouseMove(MouseMoveEvent e){
		if(!Game.win().isFocused()||!Mouse.isGrabbed()) return;
		if(noMouseMode){
			if(Keyboard.isKeyDown(Keyboard.KEY_Q)) rot.y-=0.1;
			if(Keyboard.isKeyDown(Keyboard.KEY_E)) rot.y+=0.1;
			if(Keyboard.isKeyDown(Keyboard.KEY_R)) rot.x-=0.1;
			if(Keyboard.isKeyDown(Keyboard.KEY_F)) rot.x+=0.1;
		}else{
			rot.x-=e.yDelta/200F;
			rot.y+=e.xDelta/200F;
			Game.win().centerMouse();
		}
		
		if(rot.x>Math.PI/2) rot.x=(float)(Math.PI/2);
		else if(rot.x<-Math.PI/2) rot.x=(float)(-Math.PI/2);
	}
	
	public Matrix4f createView(Matrix4f dest){
		dest.setIdentity();
		//		MatrixUtil.rotateZXY(dest, PartialTick.calc(activeRotVec, prevRot, rot));
		MatrixUtil.rotateZXY(dest, activeRotVec.set(rot));
		
		Vec3f rotV=activeRotVec.clone().eulerToDirection().mul(-viewDistance.get());
		if(rotV.y()<0) rotV.y((float)-Math.sqrt(-rotV.y()));
		
		dest.translate(PartialTick.calc(EFF_POS, prevPos, pos).add(rotV).mul(-1F));
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
	
	private static final Vec2f SIZE_F=new Vec2f();
	
	public void calcProjection(){
		farPlane=(float)Game.get().world.fog.getMaxDistance();
		float aspectRatio=SIZE_F.set(Game.win().getSize()).divXy();
		float y_scale=(float)(1/Math.tan(fov/zoom/2));
		float x_scale=y_scale/aspectRatio;
		float frustumLength=farPlane-nearPlane;
		
		projection.setIdentity();
		projection.m00=x_scale;
		projection.m11=y_scale;
		projection.m22=-((farPlane+nearPlane)/frustumLength);
		projection.m23=-1;
		projection.m32=-(2*nearPlane*farPlane/frustumLength);
		projection.m33=0;
	}
	
	public void calcOrtho(){
		
		float right=Game.win().getSize().x();
		float left=0;
		float top=0;
		float bottom=Game.win().getSize().y();
		
		projection.m00=2/(right-left);
		projection.m01=0;
		projection.m02=0;
		projection.m03=0;
		
		projection.m10=0;
		projection.m11=2/(top-bottom);
		projection.m12=0;
		projection.m13=0;
		
		projection.m20=0;
		projection.m21=0;
		projection.m22=2/(farPlane-nearPlane);
		projection.m23=0;
		
		projection.m30=-(right+left)/(right-left);
		projection.m31=-(top+bottom)/(top-bottom);
		projection.m32=-(farPlane+nearPlane)/(farPlane-nearPlane);
		projection.m33=1;
	}
	
	private void injectProjection(Matrix4f dest){
		dest.load(projection);
	}
}
