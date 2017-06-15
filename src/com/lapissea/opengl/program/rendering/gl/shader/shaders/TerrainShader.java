package com.lapissea.opengl.program.rendering.gl.shader.shaders;

import java.util.Collection;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.game.terrain.Chunk;
import com.lapissea.opengl.program.rendering.gl.Renderer;
import com.lapissea.opengl.program.rendering.gl.shader.ShaderRenderer;
import com.lapissea.opengl.program.util.data.OffsetArray;

public class TerrainShader extends ShaderRenderer.Basic3D<Chunk>{
	
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
	public void renderBatched(Chunk entity){}
	
	@Override
	@Deprecated
	public void renderSingle(Chunk renderable){}
	
	@Override
	@Deprecated
	public void renderBatch(Collection<? extends Chunk> entitysWithSameModel){}
	
	@Override
	public void render(){
		onRendered();
		OffsetArray<OffsetArray<Chunk>> terrains=Game.get().world.chunks;
		if(terrains.isEmpty()) return;
		
		prepareGlobal();
		for(OffsetArray<Chunk> offsetArray:terrains){
			for(Chunk terrain:offsetArray){
				renderChunk(terrain);
			}
		}
		unbind();
	}
	
	private void renderChunk(Chunk ter){
		if(!ter.model.isLoaded()) return;
		Renderer r=getRenderer();
		r.notifyEntityRender();
		if(!ter.model.getFrustrumShape().isVisibleAt(ter.x*Chunk.SIZE, -2, ter.z*Chunk.SIZE, r.frustrum)) return;
		r.notifyEntityActualRender();
		
		prepareModel(ter.model);
		prepareInstance(ter);
		ter.model.drawCall();
		unbindModel(ter.model);
		
	}
}
