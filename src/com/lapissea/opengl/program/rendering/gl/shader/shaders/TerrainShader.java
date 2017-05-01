package com.lapissea.opengl.program.rendering.gl.shader.shaders;

import java.util.List;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.game.terrain.Terrain;
import com.lapissea.opengl.program.rendering.gl.Renderer;
import com.lapissea.opengl.program.rendering.gl.shader.ShaderRenderer;

public class TerrainShader extends ShaderRenderer.Basic3D<Terrain>{
	
	public TerrainShader(){
		super("entity");
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
	@Deprecated
	public void renderBatched(Terrain entity){}
	
	@Override
	@Deprecated
	public void renderSingle(Terrain renderable){}
	
	@Override
	@Deprecated
	public void renderBatch(List<? extends Terrain> entitysWithSameModel){}
	
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
		if(!ter.model.getFrustrumShape().isVisibleAt(ter.x, -2, ter.z, r.frustrum)) return;
		r.notifyEntityActualRender();
		
		prepareModel(ter.model);
		prepareInstance(ter);
		ter.model.drawCall();
		unbindModel(ter.model);
		
	}
}
