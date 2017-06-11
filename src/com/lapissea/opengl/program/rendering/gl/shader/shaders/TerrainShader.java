package com.lapissea.opengl.program.rendering.gl.shader.shaders;

import java.util.Collection;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.game.terrain.Terrain;
import com.lapissea.opengl.program.rendering.gl.Renderer;
import com.lapissea.opengl.program.rendering.gl.shader.ShaderRenderer;
import com.lapissea.opengl.program.util.data.OffsetArray;

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
	public void renderBatch(Collection<? extends Terrain> entitysWithSameModel){}
	
	@Override
	public void render(){
		onRendered();
		OffsetArray<OffsetArray<Terrain>> terrains=Game.get().world.terrains;
		if(terrains.isEmpty()) return;
		
		prepareGlobal();
		for(OffsetArray<Terrain> offsetArray:terrains){
			for(Terrain terrain:offsetArray){
				renderChunk(terrain);
			}
		}
		unbind();
	}
	
	private void renderChunk(Terrain ter){
		if(!ter.model.isLoaded()) return;
		Renderer r=getRenderer();
		r.notifyEntityRender();
		if(!ter.model.getFrustrumShape().isVisibleAt(ter.x*Terrain.SIZE, -2, ter.z*Terrain.SIZE, r.frustrum)) return;
		r.notifyEntityActualRender();
		
		prepareModel(ter.model);
		prepareInstance(ter);
		ter.model.drawCall();
		unbindModel(ter.model);
		
	}
}
