package com.lapissea.opengl.program.rendering.shader.shaders;

import static org.lwjgl.opengl.GL13.*;

import java.util.Collection;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.game.terrain.Chunk;
import com.lapissea.opengl.program.rendering.Renderer;
import com.lapissea.opengl.program.rendering.shader.ShaderRenderer;
import com.lapissea.opengl.program.rendering.shader.uniforms.UniformSampler2D;
import com.lapissea.opengl.program.util.data.OffsetArray;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.util.color.IColorM;
import com.lapissea.opengl.window.assets.IModel;

public class TerrainShader extends ShaderRenderer.Basic3D<Chunk>{
	
	UniformSampler2D skyBuffer;
	
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
	protected void setUpUniforms(){
		super.setUpUniforms();
		skyBuffer=getUniform("skyBuffer");
	}
	
	@Override
	public void prepareGlobal(){
		super.prepareGlobal();
		if(skyBuffer!=null){
			glActiveTexture(GL_TEXTURE0+1);
			getRenderer().skyFbo.getTexture().bind();
			skyBuffer.upload(1);
		}
	}
	
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
		
		IModel model=ter.getModel();
		Renderer r=getRenderer();
		r.drawLine(ter.spacePos, new Vec3f(ter.spacePos).addX(Chunk.SIZE), model.isLoaded()?IColorM.GREEN:IColorM.RED);
		r.drawLine(ter.spacePos, new Vec3f(ter.spacePos).addZ(Chunk.SIZE), model.isLoaded()?IColorM.GREEN:IColorM.RED);
		if(!model.isLoaded())return;
		
		r.notifyEntityRender();
		if(!model.getFrustrumShape().isVisibleAt(ter.spacePos, r.frustrum)) return;
		r.notifyEntityActualRender();
		
		renderSingleBare(ter);
		
	}
}
