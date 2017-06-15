package com.lapissea.opengl.program.util.config.configs;

import com.lapissea.opengl.program.util.config.Config;
import com.lapissea.opengl.program.util.math.vec.Vec2i;

public class WindowConfig extends Config{
	
	public Vec2i	position=new Vec2i(-1,-1);
	public Vec2i	size	=new Vec2i(1000, 600);
	
	public WindowConfig(String name){
		super(name);
	}
	
	public WindowConfig(String name, Vec2i position, Vec2i size){
		super(name);
		this.position=position;
		this.size=size;
	}
}
