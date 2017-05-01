package com.lapissea.opengl.abstr.opengl.assets;

import java.util.Arrays;
import java.util.stream.Stream;

import org.lwjgl.opengl.GL20;

public enum ModelAttribute{
	VERTEX_ATTR(0, 3, "pos"),
	UV_ATTR(1, 2, "uvIn"),
	NORMAL_ATTR(2, 3, "normalIn"),
	MAERIAL_ID_ATTR(3, 1, "materialIdIn"),
	PRIMITIVE_COLOR_ATTR(4, 4, "vtColorIn");
	
	public final int	id,size;
	public final String	defaultShaderName;
	
	private ModelAttribute(int id, int size, String defaultShaderName){
		this.id=id;
		this.size=size;
		this.defaultShaderName=defaultShaderName;
	}
	
	public void enable(){
		GL20.glEnableVertexAttribArray(id);
	}
	
	public void disable(){
		GL20.glDisableVertexAttribArray(id);
	}
	
	public static Stream<ModelAttribute> stream(){
		return Arrays.stream(values());
	}
	
}
