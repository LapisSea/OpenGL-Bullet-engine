package com.lapissea.opengl.resources.model;

import static com.lapissea.util.UtilL.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.List;
import java.util.function.Consumer;

import com.lapissea.opengl.resources.texture.TextureLoader;
import com.lapissea.opengl.window.assets.IMaterial;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ITexture;
import com.lapissea.opengl.window.assets.ModelAttribute;
import com.lapissea.opengl.window.impl.assets.Model;

import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntList;

public class ModelBuilder{
	
	public static final float[] AUTO_FLOAT=new float[0];
	
	public float[]			vertices	=AUTO_FLOAT;
	public ModelAttribute	vertexType	=ModelAttribute.VERTEX_ATTR_3D;
	
	public float[]		uvs;
	public ITexture[]	textures;
	
	public float[]	normals;
	public boolean	generateNormals;
	public boolean	killSmooth	=false;
	
	public int[]		materials;
	public IMaterial[]	materialDefs;
	
	public float[] vertexColors;
	
	public int[] indices;
	
	public String	name	="NO_NAME_MODEL";
	public boolean	culface	=true;
	
	public Class<? extends IModel>		type=Model.class;
	public Consumer<? extends IModel>	onLoad;
	
	public int format=GL_TRIANGLES;
	
	public ModelBuilder withType(Class<? extends IModel> type){
		this.type=type;
		return this;
	}
	
	public ModelBuilder withLoad(Runnable onLoad){
		this.onLoad=m->onLoad.run();
		return this;
	}
	
	public ModelBuilder withLoad(Consumer<? extends IModel> onLoad){
		this.onLoad=onLoad;
		return this;
	}
	
	public ModelBuilder withFormat(int format){
		this.format=format;
		return this;
	}
	
	public ModelBuilder withName(CharSequence name){
		return withName(name.toString());
	}
	
	public ModelBuilder withName(String name){
		this.name=name;
		return this;
	}
	
	public ModelBuilder withCulface(boolean culface){
		this.culface=culface;
		return this;
	}
	
	public ModelBuilder withAutoVertecies(){
		return withVertecies(AUTO_FLOAT);
	}
	
	public ModelBuilder withVertecies(FloatList vertices){
		return withVertecies(vertices.toFloatArray());
	}
	
	public ModelBuilder withVertecies(float...vertices){
		this.vertices=vertices;
		return this;
	}
	
	public ModelBuilder withVertexType(ModelAttribute vertexType){
		this.vertexType=vertexType;
		return this;
	}
	
	public ModelBuilder withVertexType(int dimensions){
		switch(dimensions){
		case 1:
			return withVertexType(ModelAttribute.VERTEX_ATTR_1D);
		case 2:
			return withVertexType(ModelAttribute.VERTEX_ATTR_2D);
		case 3:
			return withVertexType(ModelAttribute.VERTEX_ATTR_3D);
		}
		throw new IllegalArgumentException("Vertex dimension of "+dimensions+" is not supported!");
	}
	
	public ModelBuilder withAutoUvs(){
		return withUvs(AUTO_FLOAT);
	}
	
	public ModelBuilder withUvs(FloatList uvs){
		return withUvs(uvs.toFloatArray());
	}
	
	public ModelBuilder withUvs(float...uvs){
		this.uvs=uvs;
		return this;
	}
	
	public ModelBuilder withNormals(FloatList normals){
		return withNormals(normals.toFloatArray());
	}
	
	public ModelBuilder withNormals(float...normals){
		this.normals=normals;
		return this;
	}
	
	public ModelBuilder withMaterials(IntList materials){
		return withMaterials(materials.toIntArray());
	}
	
	public ModelBuilder withMaterials(int...materials){
		this.materials=materials;
		return this;
	}
	
	public ModelBuilder withVertexColors(FloatList vertexColors){
		return withVertexColors(vertexColors.toFloatArray());
	}
	
	public ModelBuilder withAutoVertexColors(){
		return withVertexColors(AUTO_FLOAT);
	}
	
	public ModelBuilder withVertexColors(float...vertexColors){
		this.vertexColors=vertexColors;
		return this;
	}
	
	public ModelBuilder withIndices(IntList indices){
		return withIndices(indices.toIntArray());
	}
	
	public ModelBuilder withIndices(int...indices){
		this.indices=indices;
		return this;
	}
	
	public ModelBuilder generateNormals(boolean generateNormals){
		this.generateNormals=generateNormals;
		return this;
	}
	
	public ModelBuilder killSmooth(boolean killSmooth){
		this.killSmooth=killSmooth;
		return this;
	}
	
	public ModelBuilder withTextures(String...texturePaths){
		return withTextures(convert(texturePaths, ITexture.class, TextureLoader::loadTexture));
	}
	
	public ModelBuilder withTextures(List<ITexture> textures){
		return withTextures(array(textures));
	}
	
	public ModelBuilder withTextures(ITexture...textures){
		this.textures=textures;
		return this;
	}
	
	public ModelBuilder withMaterialDefs(List<IMaterial> materialDefs){
		return withMaterialDefs(array(materialDefs));
	}
	
	public ModelBuilder withMaterialDefs(IMaterial...materialDefs){
		this.materialDefs=materialDefs;
		return this;
	}
	
}
