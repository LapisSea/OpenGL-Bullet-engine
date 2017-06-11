package com.lapissea.opengl.program.rendering.frustrum;

import com.lapissea.opengl.window.api.frustrum.Frustum;
import com.lapissea.opengl.window.api.frustrum.IFrustrumShape;
import com.lapissea.opengl.window.api.util.IRotation;
import com.lapissea.opengl.window.api.util.IVec3f;

public class FrustrumBool implements IFrustrumShape{
	
	public boolean value;
	
	public FrustrumBool(boolean value){
		this.value=value;
	}
	
	@Override
	public boolean isVisibleAt(IVec3f pos, Frustum frustrum){
		return value;
	}
	
	@Override
	public boolean isVisibleAt(float x, float y, float z, Frustum frustrum){
		return value;
	}
	
	@Override
	public IFrustrumShape withTransform(float x, float y, float z, IRotation q){
		return this;
	}
	
	@Override
	public String toString(){
		return "Bool{val="+value+"}";
	}
	
}
