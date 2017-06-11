package com.lapissea.opengl.program.util.math.vec;

import com.lapissea.opengl.program.util.math.PartialTick;

public class FloatSmooth{
	
	private float value,prevValue;
	
	public FloatSmooth(){
		
	}
	
	public FloatSmooth(float value){
		setForce(value);
	}
	
	public void setForce(float value){
		setValue(value);
		update();
	}
	
	public void setValue(float value){
		this.value=value;
	}
	
	public float get(){
		return PartialTick.calc(prevValue, getValue());
	}
	
	public void update(){
		prevValue=getValue();
	}
	
	public float getValue(){
		return value;
	}
}
