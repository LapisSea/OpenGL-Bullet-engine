package com.lapissea.opengl.program.util.config.configs;

import java.util.HashMap;
import java.util.Map;

import com.lapissea.opengl.program.util.config.Config;
import com.lapissea.opengl.program.util.math.vec.Vec2i;

public class WindowConfig extends Config{
	
	public Vec2i	position=new Vec2i();
	public Vec2i	size	=new Vec2i(1000, 600);
	public Map<String,Vec2i> pos=new HashMap<>();
	
	public WindowConfig(String name){
		super(name);
		pos.put("asdad", new Vec2i(230, 253));
		pos.put("asd1ad", new Vec2i(1230, 250));
	}
	
	public WindowConfig(String name, Vec2i position, Vec2i size){
		super(name);
		this.position=position;
		this.size=size;
	}
}
