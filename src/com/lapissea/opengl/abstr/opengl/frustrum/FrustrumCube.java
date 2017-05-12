package com.lapissea.opengl.abstr.opengl.frustrum;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.gl.Renderer;
import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.program.util.color.IColorM;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class FrustrumCube implements IFrustrumShape{
	
	private final Vec3f		size	=new Vec3f();
	private final Vec3f		offset	=new Vec3f();
	private final Vec3f		scale	=new Vec3f(1, 1, 1);
	private final Quat4M	rotation=new Quat4M();
	
	private final Vec3f	effectiveSize	=new Vec3f();
	private final Vec3f	effectivePos	=new Vec3f();
	private final Vec3f	dummyPos		=new Vec3f();
	
	
	public FrustrumCube(Vec3f start, Vec3f end){
		rotation.set(new Vec3f());
		size.set(end).sub(start).mul(0.5F);
		offset.set(start).add(end).mul(0.5F);
	}
	
	
	private void draw(boolean hasNoRot){
		
		
		Renderer r=Game.get().renderer;
		Vec3f start=Vec3f.POOL.borrow();
		Vec3f end=Vec3f.POOL.borrow();
		float x=effectivePos.x();
		float y=effectivePos.y();
		float z=effectivePos.z();
		
		float sizeX=size.x()*scale.x();
		float sizeY=size.y()*scale.y();
		float sizeZ=size.z()*scale.z();
		if(!hasNoRot){
			r.drawLine(start.setThis(x, y, z), rotation.rotate(end.setThis(sizeX, sizeY, sizeZ)).add(x, y, z), IColorM.WHITE);
			r.drawLine(start.setThis(x, y, z), rotation.rotate(end.setThis(-sizeX, sizeY, sizeZ)).add(x, y, z), IColorM.RED);
			r.drawLine(start.setThis(x, y, z), rotation.rotate(end.setThis(sizeX, -sizeY, sizeZ)).add(x, y, z), IColorM.GREEN);
			r.drawLine(start.setThis(x, y, z), rotation.rotate(end.setThis(sizeX, sizeY, -sizeZ)).add(x, y, z), IColorM.BLUE);
			
			r.drawLine(rotation.rotate(start.setThis(sizeX, sizeY, sizeZ)).add(x, y, z), rotation.rotate(end.setThis(sizeX, -sizeY, sizeZ)).add(x, y, z), IColorM.GREEN);
			r.drawLine(rotation.rotate(start.setThis(sizeX, sizeY, sizeZ)).add(x, y, z), rotation.rotate(end.setThis(-sizeX, sizeY, sizeZ)).add(x, y, z), IColorM.RED);
			r.drawLine(rotation.rotate(start.setThis(sizeX, sizeY, sizeZ)).add(x, y, z), rotation.rotate(end.setThis(sizeX, sizeY, -sizeZ)).add(x, y, z), IColorM.BLUE);
			
			r.drawLine(rotation.rotate(start.setThis(-sizeX, sizeY, sizeZ)).add(x, y, z), rotation.rotate(end.setThis(-sizeX, -sizeY, sizeZ)).add(x, y, z), IColorM.CYAN);
			r.drawLine(rotation.rotate(start.setThis(-sizeX, sizeY, -sizeZ)).add(x, y, z), rotation.rotate(end.setThis(-sizeX, -sizeY, -sizeZ)).add(x, y, z), IColorM.CYAN);
			r.drawLine(rotation.rotate(start.setThis(sizeX, sizeY, -sizeZ)).add(x, y, z), rotation.rotate(end.setThis(sizeX, -sizeY, -sizeZ)).add(x, y, z), IColorM.CYAN);
			
			r.drawLine(rotation.rotate(start.setThis(sizeX, -sizeY, sizeZ)).add(x, y, z), rotation.rotate(end.setThis(-sizeX, -sizeY, sizeZ)).add(x, y, z), IColorM.CYAN);
			r.drawLine(rotation.rotate(start.setThis(sizeX, -sizeY, -sizeZ)).add(x, y, z), rotation.rotate(end.setThis(-sizeX, -sizeY, -sizeZ)).add(x, y, z), IColorM.CYAN);
			r.drawLine(rotation.rotate(start.setThis(sizeX, sizeY, -sizeZ)).add(x, y, z), rotation.rotate(end.setThis(-sizeX, sizeY, -sizeZ)).add(x, y, z), IColorM.CYAN);
			
			r.drawLine(rotation.rotate(start.setThis(-sizeX, sizeY, sizeZ)).add(x, y, z), rotation.rotate(end.setThis(-sizeX, sizeY, -sizeZ)).add(x, y, z), IColorM.CYAN);
			r.drawLine(rotation.rotate(start.setThis(-sizeX, -sizeY, sizeZ)).add(x, y, z), rotation.rotate(end.setThis(-sizeX, -sizeY, -sizeZ)).add(x, y, z), IColorM.CYAN);
			r.drawLine(rotation.rotate(start.setThis(sizeX, -sizeY, sizeZ)).add(x, y, z), rotation.rotate(end.setThis(sizeX, -sizeY, -sizeZ)).add(x, y, z), IColorM.CYAN);
		}
		
		
		sizeX=effectiveSize.x();
		sizeY=effectiveSize.y();
		sizeZ=effectiveSize.z();
		
		r.drawLine(start.setThis(x+sizeX, y+sizeY, z+sizeZ), end.setThis(x+sizeX, y-sizeY, z+sizeZ), IColorM.CYAN);
		r.drawLine(start.setThis(x+sizeX, y+sizeY, z+sizeZ), end.setThis(x-sizeX, y+sizeY, z+sizeZ), IColorM.CYAN);
		r.drawLine(start.setThis(x+sizeX, y+sizeY, z+sizeZ), end.setThis(x+sizeX, y+sizeY, z-sizeZ), IColorM.CYAN);
		
		r.drawLine(start.setThis(x-sizeX, y+sizeY, z+sizeZ), end.setThis(x-sizeX, y-sizeY, z+sizeZ), IColorM.CYAN);
		r.drawLine(start.setThis(x-sizeX, y+sizeY, z-sizeZ), end.setThis(x-sizeX, y-sizeY, z-sizeZ), IColorM.CYAN);
		r.drawLine(start.setThis(x+sizeX, y+sizeY, z-sizeZ), end.setThis(x+sizeX, y-sizeY, z-sizeZ), IColorM.CYAN);
		
		r.drawLine(start.setThis(x+sizeX, y-sizeY, z+sizeZ), end.setThis(x-sizeX, y-sizeY, z+sizeZ), IColorM.CYAN);
		r.drawLine(start.setThis(x+sizeX, y-sizeY, z-sizeZ), end.setThis(x-sizeX, y-sizeY, z-sizeZ), IColorM.CYAN);
		r.drawLine(start.setThis(x+sizeX, y+sizeY, z-sizeZ), end.setThis(x-sizeX, y+sizeY, z-sizeZ), IColorM.CYAN);
		
		r.drawLine(start.setThis(x-sizeX, y+sizeY, z+sizeZ), end.setThis(x-sizeX, y+sizeY, z-sizeZ), IColorM.CYAN);
		r.drawLine(start.setThis(x-sizeX, y-sizeY, z+sizeZ), end.setThis(x-sizeX, y-sizeY, z-sizeZ), IColorM.CYAN);
		r.drawLine(start.setThis(x+sizeX, y-sizeY, z+sizeZ), end.setThis(x+sizeX, y-sizeY, z-sizeZ), IColorM.CYAN);
		
		Vec3f.POOL.giveBack(start);
		Vec3f.POOL.giveBack(end);
	}
	
	
	@Override
	public boolean isVisibleAt(Vec3f pos, Frustum frustrum){
		boolean hasNoRot=rotation==null||rotation.x==0&&rotation.y==0&&rotation.z==0&&rotation.w==1;
		
		if(hasNoRot){
			effectiveSize.set(size).mul(scale);
			effectivePos.set(offset).mul(scale).add(pos);
		}
		else{
			Vec3f vec=Vec3f.POOL.borrow();
			Vec3f scaledSize=Vec3f.POOL.borrow();
			
			vec.set(offset).mul(scale);
			rotation.rotate(vec);
			effectivePos.set(pos).add(vec);
			
			scaledSize.set(this.size).mul(scale).abs();
			
			rotation.rotate(effectiveSize.set(scaledSize)).abs();
			
			effectiveSize.setMax(rotation.rotate(vec.set(scaledSize).mulX(-1)).abs());
			effectiveSize.setMax(rotation.rotate(vec.set(scaledSize).mulY(-1)).abs());
			effectiveSize.setMax(rotation.rotate(vec.set(scaledSize).mulZ(-1)).abs());
			
			Vec3f.POOL.giveBack(vec);
			Vec3f.POOL.giveBack(scaledSize);
		}
		
		if(Renderer.RENDER_FRUSTRUM) draw(hasNoRot);
		
		
		return frustrum.cube(effectivePos, effectiveSize);
	}
	
	@Override
	public boolean isVisibleAt(float x, float y, float z, Frustum frustrum){
		return isVisibleAt(dummyPos.setThis(x, y, z), frustrum);
	}
	
	@Override
	public IFrustrumShape withTransform(float x, float y, float z, Quat4M quat){
		scale.set(x, y, z);
		this.rotation.set(quat);
		return this;
	}
	
	@Override
	public String toString(){
		return "Cube{size="+size+"}";
	}
	
}
