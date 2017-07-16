package com.lapissea.opengl.program.rendering.frustrum;

import javax.vecmath.Quat4f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.gl.Renderer;
import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.frustrum.Frustum;
import com.lapissea.opengl.window.api.frustrum.IFrustrumShape;
import com.lapissea.opengl.window.api.util.IRotation;
import com.lapissea.opengl.window.api.util.IVec3f;
import com.lapissea.opengl.window.api.util.color.IColorM;

public class FrustrumCube implements IFrustrumShape{
	
	private static final Vec3f START=new Vec3f(),END=new Vec3f(),VEC=new Vec3f(),SS=new Vec3f(),CP=new Vec3f();
	
	private final Vec3f		size	=new Vec3f();
	private final Vec3f		offset	=new Vec3f();
	private final Vec3f		scale	=new Vec3f(1, 1, 1);
	private final Quat4M	rotation=new Quat4M();
	
	private final Vec3f	effectiveSize	=new Vec3f();
	private final Vec3f	effectivePos	=new Vec3f();
	private final Vec3f	dummyPos		=new Vec3f();
	
	public FrustrumCube(Vec3f start, Vec3f end){
		size.set(end).sub(start).mul(0.5F);
		offset.set(start).add(end).mul(0.5F);
	}
	
	private void draw(boolean hasNoRot){
		
		Renderer r=Game.get().renderer;
		
		Vec3f start=START;
		
		float x=effectivePos.x();
		float y=effectivePos.y();
		float z=effectivePos.z();
		
		float sizeX=size.x()*scale.x();
		float sizeY=size.y()*scale.y();
		float sizeZ=size.z()*scale.z();
		if(!hasNoRot){
			r.drawLine(start.setThis(x, y, z), rotation.rotate(END.setThis(sizeX, sizeY, sizeZ)).add(x, y, z), IColorM.WHITE);
			r.drawLine(start.setThis(x, y, z), rotation.rotate(END.setThis(-sizeX, sizeY, sizeZ)).add(x, y, z), IColorM.RED);
			r.drawLine(start.setThis(x, y, z), rotation.rotate(END.setThis(sizeX, -sizeY, sizeZ)).add(x, y, z), IColorM.GREEN);
			r.drawLine(start.setThis(x, y, z), rotation.rotate(END.setThis(sizeX, sizeY, -sizeZ)).add(x, y, z), IColorM.BLUE);
			
			r.drawLine(rotation.rotate(start.setThis(sizeX, sizeY, sizeZ)).add(x, y, z), rotation.rotate(END.setThis(sizeX, -sizeY, sizeZ)).add(x, y, z), IColorM.GREEN);
			r.drawLine(rotation.rotate(start.setThis(sizeX, sizeY, sizeZ)).add(x, y, z), rotation.rotate(END.setThis(-sizeX, sizeY, sizeZ)).add(x, y, z), IColorM.RED);
			r.drawLine(rotation.rotate(start.setThis(sizeX, sizeY, sizeZ)).add(x, y, z), rotation.rotate(END.setThis(sizeX, sizeY, -sizeZ)).add(x, y, z), IColorM.BLUE);
			
			r.drawLine(rotation.rotate(start.setThis(-sizeX, sizeY, sizeZ)).add(x, y, z), rotation.rotate(END.setThis(-sizeX, -sizeY, sizeZ)).add(x, y, z), IColorM.CYAN);
			r.drawLine(rotation.rotate(start.setThis(-sizeX, sizeY, -sizeZ)).add(x, y, z), rotation.rotate(END.setThis(-sizeX, -sizeY, -sizeZ)).add(x, y, z), IColorM.CYAN);
			r.drawLine(rotation.rotate(start.setThis(sizeX, sizeY, -sizeZ)).add(x, y, z), rotation.rotate(END.setThis(sizeX, -sizeY, -sizeZ)).add(x, y, z), IColorM.CYAN);
			
			r.drawLine(rotation.rotate(start.setThis(sizeX, -sizeY, sizeZ)).add(x, y, z), rotation.rotate(END.setThis(-sizeX, -sizeY, sizeZ)).add(x, y, z), IColorM.CYAN);
			r.drawLine(rotation.rotate(start.setThis(sizeX, -sizeY, -sizeZ)).add(x, y, z), rotation.rotate(END.setThis(-sizeX, -sizeY, -sizeZ)).add(x, y, z), IColorM.CYAN);
			r.drawLine(rotation.rotate(start.setThis(sizeX, sizeY, -sizeZ)).add(x, y, z), rotation.rotate(END.setThis(-sizeX, sizeY, -sizeZ)).add(x, y, z), IColorM.CYAN);
			
			r.drawLine(rotation.rotate(start.setThis(-sizeX, sizeY, sizeZ)).add(x, y, z), rotation.rotate(END.setThis(-sizeX, sizeY, -sizeZ)).add(x, y, z), IColorM.CYAN);
			r.drawLine(rotation.rotate(start.setThis(-sizeX, -sizeY, sizeZ)).add(x, y, z), rotation.rotate(END.setThis(-sizeX, -sizeY, -sizeZ)).add(x, y, z), IColorM.CYAN);
			r.drawLine(rotation.rotate(start.setThis(sizeX, -sizeY, sizeZ)).add(x, y, z), rotation.rotate(END.setThis(sizeX, -sizeY, -sizeZ)).add(x, y, z), IColorM.CYAN);
		}
		
		sizeX=effectiveSize.x();
		sizeY=effectiveSize.y();
		sizeZ=effectiveSize.z();
		
		r.drawLine(start.setThis(x+sizeX, y+sizeY, z+sizeZ), END.setThis(x+sizeX, y-sizeY, z+sizeZ), IColorM.CYAN);
		r.drawLine(start.setThis(x+sizeX, y+sizeY, z+sizeZ), END.setThis(x-sizeX, y+sizeY, z+sizeZ), IColorM.CYAN);
		r.drawLine(start.setThis(x+sizeX, y+sizeY, z+sizeZ), END.setThis(x+sizeX, y+sizeY, z-sizeZ), IColorM.CYAN);
		
		r.drawLine(start.setThis(x-sizeX, y+sizeY, z+sizeZ), END.setThis(x-sizeX, y-sizeY, z+sizeZ), IColorM.CYAN);
		r.drawLine(start.setThis(x-sizeX, y+sizeY, z-sizeZ), END.setThis(x-sizeX, y-sizeY, z-sizeZ), IColorM.CYAN);
		r.drawLine(start.setThis(x+sizeX, y+sizeY, z-sizeZ), END.setThis(x+sizeX, y-sizeY, z-sizeZ), IColorM.CYAN);
		
		r.drawLine(start.setThis(x+sizeX, y-sizeY, z+sizeZ), END.setThis(x-sizeX, y-sizeY, z+sizeZ), IColorM.CYAN);
		r.drawLine(start.setThis(x+sizeX, y-sizeY, z-sizeZ), END.setThis(x-sizeX, y-sizeY, z-sizeZ), IColorM.CYAN);
		r.drawLine(start.setThis(x+sizeX, y+sizeY, z-sizeZ), END.setThis(x-sizeX, y+sizeY, z-sizeZ), IColorM.CYAN);
		
		r.drawLine(start.setThis(x-sizeX, y+sizeY, z+sizeZ), END.setThis(x-sizeX, y+sizeY, z-sizeZ), IColorM.CYAN);
		r.drawLine(start.setThis(x-sizeX, y-sizeY, z+sizeZ), END.setThis(x-sizeX, y-sizeY, z-sizeZ), IColorM.CYAN);
		r.drawLine(start.setThis(x+sizeX, y-sizeY, z+sizeZ), END.setThis(x+sizeX, y-sizeY, z-sizeZ), IColorM.CYAN);
		
	}
	
	@Override
	public boolean isVisibleAt(IVec3f pos, Frustum frustrum){
		
		Vec3f camPos=CP.set(Game.get().renderer.getCamera().pos);
		
		float distance=camPos.sub(pos).length();
		
		if(Game.get().world.fog.getMaxDistance()<=distance-30) return false;
		
		boolean hasNoRot=rotation==null||rotation.x==0&&rotation.y==0&&rotation.z==0&&rotation.w==1;
		
		if(hasNoRot){
			effectiveSize.set(size).mul(scale);
			effectivePos.set(offset).mul(scale).add(pos);
		}else{
			Vec3f vec=VEC;
			Vec3f scaledSize=SS.set(size).mul(scale);
			
			vec.set(offset).mul(scale);
			rotation.rotate(vec);
			effectivePos.set(pos).add(vec);
			
			rotation.rotate(effectiveSize.set(scaledSize)).abs();
			
			effectiveSize.setMax(rotation.rotate(vec.set(scaledSize).mulX(-1)).abs());
			effectiveSize.setMax(rotation.rotate(vec.set(scaledSize).mulY(-1)).abs());
			effectiveSize.setMax(rotation.rotate(vec.set(scaledSize).mulZ(-1)).abs());
			
		}
		
		if(Renderer.RENDER_FRUSTRUM) draw(hasNoRot);
		
		return frustrum.cube(effectivePos, effectiveSize);
	}
	
	@Override
	public boolean isVisibleAt(float x, float y, float z, Frustum frustrum){
		return isVisibleAt(dummyPos.setThis(x, y, z), frustrum);
	}
	
	@Override
	public IFrustrumShape withTransform(float x, float y, float z, IRotation quat){
		scale.set(x, y, z);
		rotation.set((Quat4f)quat);
		return this;
	}
	
	@Override
	public String toString(){
		return "Cube{size="+size+"}";
	}
	
	public float getSizeX(){
		return size.x();
	}
	
	public float getSizeY(){
		return size.y();
	}
	
	public float getSizeZ(){
		return size.z();
	}
	
}
