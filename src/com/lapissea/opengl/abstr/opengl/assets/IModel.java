package com.lapissea.opengl.abstr.opengl.assets;

import java.util.List;
import java.util.function.Consumer;

import com.lapissea.opengl.abstr.opengl.frustrum.Frustum;
import com.lapissea.opengl.abstr.opengl.frustrum.IFrustrumShape;
import com.lapissea.opengl.program.interfaces.IntObjConsumer;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public interface IModel{
	
	String getName();
	
	IModel setOnLoad(Consumer<IModel> hook);
	
	default IModel setOnLoad(Runnable hook){
		return setOnLoad(hook::run);
	}
	
	IMaterial getMaterial(int i);
	
	IMaterial removeMaterial(int i);
	
	IMaterial createMaterial();
	
	IMaterial createMaterial(String name);
	
	IModel iterateMaterials(IntObjConsumer<IMaterial> consummer);
	
	int getMaterialCount();
	
	List<ITexture> getTextures();
	
	IModel load(int vao, int vertexCount, boolean usesIndicies, boolean usesQuads, int[] vbos, ModelAttribute[] attributeIds, IFrustrumShape shape);
	
	IModel enableAttributes();
	
	IModel disableAttributes();
	
	IModel drawCall();
	
	IFrustrumShape getFrustrumShape();
	
	default boolean isVisibleAt(Vec3f pos, Frustum frustum){
		return getFrustrumShape().isVisibleAt(pos, frustum);
	}
	
	boolean isLoaded();
	
	void delete();
	
	int getVertexCount();
	
	ModelAttribute getAttribute(int i);
	
	int getAttributeCount();
	
	IModel bindVao();
	
	void addMaterial(IMaterial material);
}
