package com.lapissea.opengl.program.rendering.gl.shader;

import com.lapissea.opengl.program.game.entity.Entity;
import com.lapissea.opengl.program.rendering.ModelInWorld;
import com.lapissea.opengl.program.rendering.gl.shader.ShaderRenderer.Basic3D;
import com.lapissea.opengl.program.rendering.gl.shader.shaders.TerrainShader;

public class Shaders{
	
	public static ShaderRenderer.Basic3D<Entity>	ENTITY;
	public static TerrainShader						TERRAIN;
	public static ShaderRenderer<ModelInWorld>		LINE;
	public static ShaderRenderer.Basic3D<?>			SKYBOX;
	
	public static void load(){
		
		ENTITY=new Basic3D<>("entity");
		TERRAIN=new TerrainShader();
		LINE=new ShaderRenderer.Basic3D<>("line");
		SKYBOX=new ShaderRenderer.Basic3D<>("skybox");
		
	}
	
}
