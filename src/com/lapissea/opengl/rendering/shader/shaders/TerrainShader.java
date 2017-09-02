package com.lapissea.opengl.rendering.shader.shaders;

import static org.lwjgl.opengl.GL13.*;

import java.util.Collection;

import com.lapissea.opengl.core.Game;
import com.lapissea.opengl.game.terrain.Chunk;
import com.lapissea.opengl.rendering.Renderer;
import com.lapissea.opengl.rendering.shader.ShaderRenderer;
import com.lapissea.opengl.rendering.shader.uniforms.UniformSampler2D;
import com.lapissea.opengl.util.data.OffsetArrayList;
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
		OffsetArrayList<OffsetArrayList<Chunk>> terrains=Game.get().world.chunks;
		if(terrains.isEmpty()) return;
		
		prepareGlobal();
		for(OffsetArrayList<Chunk> offsetArray:terrains){
			if(offsetArray==null)continue;
			for(Chunk terrain:offsetArray){
				if(terrain==null)continue;
				renderChunk(terrain);
			}
		}
		unbind();
	}
	
	private void renderChunk(Chunk ter){
		
		IModel model=ter.getModel();
		Renderer r=getRenderer();
//		ColorM c=new ColorM(model.isLoaded()?IColorM.GREEN:IColorM.RED).a(0.2F);
//		r.drawLine(ter.spacePos, new Vec3f(ter.spacePos).addX(Chunk.SIZE), c);
//		r.drawLine(ter.spacePos, new Vec3f(ter.spacePos).addZ(Chunk.SIZE), c);
		if(!model.isLoaded())return;
		
		r.notifyEntityRender();
		if(!model.getFrustrumShape().isVisibleAt(ter.spacePos, r.frustrum)) return;
		r.notifyEntityActualRender();
		
		renderSingleBare(ter);
		
	}
}
