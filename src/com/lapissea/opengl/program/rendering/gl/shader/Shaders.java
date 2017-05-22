package com.lapissea.opengl.program.rendering.gl.shader;

import com.lapissea.opengl.program.rendering.ModelTransformed;
import com.lapissea.opengl.program.rendering.gl.shader.shaders.GuiShader;
import com.lapissea.opengl.program.rendering.gl.shader.shaders.SkyboxShader;
import com.lapissea.opengl.program.rendering.gl.shader.shaders.TerrainShader;

public class Shaders{
	
	public static final float[] VERTEX_BOX=new float[]{
			-1,+1,-1,
			-1,-1,-1,
			+1,-1,-1,
			+1,-1,-1,
			+1,+1,-1,
			-1,+1,-1,
			
			-1,-1,+1,
			-1,-1,-1,
			-1,+1,-1,
			-1,+1,-1,
			-1,+1,+1,
			-1,-1,+1,
			
			+1,-1,-1,
			+1,-1,+1,
			+1,+1,+1,
			+1,+1,+1,
			+1,+1,-1,
			+1,-1,-1,
			
			-1,-1,+1,
			-1,+1,+1,
			+1,+1,+1,
			+1,+1,+1,
			+1,-1,+1,
			-1,-1,+1,
			
			-1,+1,-1,
			+1,+1,-1,
			+1,+1,+1,
			+1,+1,+1,
			-1,+1,+1,
			-1,+1,-1,
			
			-1,-1,-1,
			-1,-1,+1,
			+1,-1,-1,
			+1,-1,-1,
			-1,-1,+1,
			+1,-1,+1
	};
	
	public static ShaderRenderer<ModelTransformed>	ENTITY;
	public static TerrainShader						TERRAIN;
	public static ShaderRenderer<ModelTransformed>	LINE;
	public static SkyboxShader						SKYBOX;
	public static GuiShader							GUI;
	public static ShaderRenderer<ModelTransformed>	POST;
	
	public static void load(){
		ENTITY=new ShaderRenderer.Basic3D<>("entity");
		TERRAIN=new TerrainShader();
		LINE=new ShaderRenderer.Basic3D<>("line"); 
		POST=new ShaderRenderer.Basic3D<>("post"); 
		SKYBOX=new SkyboxShader();
		GUI=new GuiShader();
	}
	
}
