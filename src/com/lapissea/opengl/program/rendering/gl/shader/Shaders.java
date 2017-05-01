package com.lapissea.opengl.program.rendering.gl.shader;

import com.lapissea.opengl.program.rendering.ModelInWorld;
import com.lapissea.opengl.program.rendering.gl.shader.shaders.SkyboxShader;
import com.lapissea.opengl.program.rendering.gl.shader.shaders.TerrainShader;

public class Shaders{
	
	public static ShaderRenderer<ModelInWorld>	ENTITY;
	public static TerrainShader					TERRAIN;
	public static ShaderRenderer<ModelInWorld>	LINE;
	public static SkyboxShader					SKYBOX;
	
	public static void load(){
		ENTITY=new ShaderRenderer.Basic3D<>("entity");
		TERRAIN=new TerrainShader();
		LINE=new ShaderRenderer.Basic3D<>("line");
		SKYBOX=new SkyboxShader();
	}
	
}
