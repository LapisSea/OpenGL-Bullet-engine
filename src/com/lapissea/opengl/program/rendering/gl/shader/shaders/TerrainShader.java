package com.lapissea.opengl.program.rendering.gl.shader.shaders;

import java.util.List;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.game.terrain.Terrain;
import com.lapissea.opengl.program.rendering.gl.Renderer;
import com.lapissea.opengl.program.rendering.gl.shader.ShaderRenderer;

public class TerrainShader extends ShaderRenderer.Basic3D<Terrain>{
	
	public TerrainShader(){
		super("terrain");
	}
	
	//	@Override
	//	protected String getFsSrc(){
	//		return UtilM.getTxtResource("shaders/entity.fs");
	//	}
	//	@Override
	//	protected String getVsSrc(){
	//		return UtilM.getTxtResource("shaders/entity.vs");
	//	}
	
	@Override
	public void renderBatched(Terrain entity){}
	
	@Override
	public void render(){
		onRendered();
		List<Terrain> terrains=Game.get().world.terrains;
		if(terrains.isEmpty()) return;
		
		prepareGlobal();
		terrains.forEach(this::renderChunk);
		unbind();
	}
	
	private void renderChunk(Terrain ter){
		if(!ter.model.isLoaded()) return;
		Renderer r=getRenderer();
		r.notifyEntityRender();
		if(!r.frustrum.sphere(ter.x, -2, ter.z, ter.model.rad())) return;
		r.notifyEntityActualRender();
		
		prepareModel(ter.model);
		prepareInstance(ter);
		ter.model.drawCall();
		unbindModel(ter.model);
	}
}
