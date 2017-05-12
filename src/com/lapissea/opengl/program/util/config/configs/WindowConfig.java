package com.lapissea.opengl.program.util.config.configs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lapissea.opengl.program.util.config.Config;
import com.lapissea.opengl.program.util.math.vec.Vec2i;

public class WindowConfig extends Config{
	
	public Vec2i	position=new Vec2i();
	public Vec2i	size	=new Vec2i(800, 600);
	
	public WindowConfig(String name){
		super(name);
	}
	
	@JsonCreator
	public WindowConfig(@JsonProperty("name") String name, @JsonProperty("position") Vec2i position, @JsonProperty("size") Vec2i size){
		super(name);
		this.position=position;
		this.size=size;
	}
}
