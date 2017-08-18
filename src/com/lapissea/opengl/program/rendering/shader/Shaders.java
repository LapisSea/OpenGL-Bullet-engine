package com.lapissea.opengl.program.rendering.shader;

import com.lapissea.opengl.program.gui.GuiElement;
import com.lapissea.opengl.program.rendering.ModelTransformed;
import com.lapissea.opengl.program.rendering.shader.shaders.EntityShader;
import com.lapissea.opengl.program.rendering.shader.shaders.GuiRectShader;
import com.lapissea.opengl.program.rendering.shader.shaders.SkyboxShader;
import com.lapissea.opengl.program.rendering.shader.shaders.TerrainShader;

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
	public static ShaderRenderer<ModelTransformed>	POST_COPY;
	public static GuiRectShader						GUI_RECT;
	public static ShaderRenderer<GuiElement>		GUI_NORMAL;
	
	public static void load(){
		ENTITY=new EntityShader();
		
		TERRAIN=new TerrainShader();
		
		LINE=new ShaderRenderer.Basic3D<>("line");
		POST_COPY=new ShaderRenderer.Basic3D<>("post/copy");
		SKYBOX=new SkyboxShader();
		GUI_RECT=new GuiRectShader();
//		ENTITY=new ShaderRenderer.Basic3D<>("entity");
	}
	
}
