package com.lapissea.opengl.program.rendering.gl.texture;

import com.lapissea.opengl.program.util.math.vec.Vec2f;

public class UvArea{
	
	public final Vec2f min=new Vec2f(),max=new Vec2f();
	
	public UvArea(){}
	
	public UvArea(Vec2f min, Vec2f max){
		set(min, max);
	}
	
	public UvArea set(Vec2f min, Vec2f max){
		this.min.set(min);
		this.max.set(max);
		return this;
	}
	
	public float topLeftX(){
		return min.x();
	}
	
	public float topLeftY(){
		return min.y();
	}
	
	public float topRightX(){
		return max.x();
	}
	
	public float topRightY(){
		return min.y();
	}
	
	public float bottomLeftX(){
		return min.x();
	}
	
	public float bottomLeftY(){
		return max.y();
	}
	
	public float bottomRightX(){
		return max.x();
	}
	
	public float bottomRightY(){
		return max.y();
	}
}
