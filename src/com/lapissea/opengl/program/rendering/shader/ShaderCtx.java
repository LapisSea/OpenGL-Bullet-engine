package com.lapissea.opengl.program.rendering.shader;

import java.util.ArrayList;
import java.util.List;

import com.lapissea.opengl.program.rendering.shader.modules.ShaderModule;

public class ShaderCtx{
	
	private List<ShaderModule> parts=new ArrayList<>();
	
	public void addPart(ShaderModule part){
		parts.add(part);
	}
	
	public void removePart(ShaderModule part){
		parts.remove(part);
	}
	
	public void replacePart(ShaderModule target, ShaderModule part){
		int pos=parts.indexOf(target);
		if(pos==-1) return;
		parts.set(pos, part);
	}
	
}
