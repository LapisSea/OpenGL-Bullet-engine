package com.lapissea.opengl.program.rendering.gl.model;

import static com.lapissea.opengl.window.assets.ModelAttribute.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.lang.reflect.Constructor;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.frustrum.FrustrumBool;
import com.lapissea.opengl.program.rendering.frustrum.FrustrumCube;
import com.lapissea.opengl.program.rendering.gl.model.ObjModelLoader.ModelData;
import com.lapissea.opengl.program.rendering.gl.texture.TextureLoader;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.frustrum.IFrustrumShape;
import com.lapissea.opengl.window.api.util.BufferUtil;
import com.lapissea.opengl.window.assets.IMaterial;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ITexture;
import com.lapissea.opengl.window.assets.ModelAttribute;
import com.lapissea.opengl.window.assets.Vbo;
import com.lapissea.opengl.window.impl.assets.Model;
import com.lapissea.util.LogUtil;
import com.lapissea.util.UtilL;

public class ModelLoader{
	
	private static final List<IModel>			MODELS			=new ArrayList<>();
	private static final HashMap<String,Object>	MODEL_BUILD_DATA=new HashMap<>();
	
	public static final IModel EMPTY_MODEL=new Model("EMPTY_MODEL"){
		
		@Override
		public IModel load(int vao, int vertexCount, boolean usesIndicies, int format, Vbo[] vbos, ModelAttribute vertexType, ModelAttribute[] attributeIds, IFrustrumShape shape){
			throw new UnsupportedOperationException();
		}
		
		@Override
		public IModel drawCall(){
			throw new UnsupportedOperationException();
		}
		
		@Override
		public IModel enableAttributes(){
			throw new UnsupportedOperationException();
		}
		
		@Override
		public IModel disableAttributes(){
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean isLoaded(){
			return false;
		}
		
		@Override
		public IModel bindVao(){
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void addMaterial(IMaterial material){
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void addTexture(ITexture texture){
			throw new UnsupportedOperationException();
		}
		
		@Override
		public IMaterial createMaterial(){
			throw new UnsupportedOperationException();
		}
		
		@Override
		public IMaterial createMaterial(String name){
			throw new UnsupportedOperationException();
		}
		
	};
	
	private static final Vec3f V0=new Vec3f(),V1=new Vec3f(),V2=new Vec3f(),V01=new Vec3f(),V12=new Vec3f(),NORMAL=new Vec3f();
	
	public static IModel[] buildModels(ModelData...modelsData){
		IModel[] models=new IModel[modelsData.length];
		for(int i=0;i<modelsData.length;i++){
			models[i]=buildModel(modelsData[0]);
		}
		return models;
	}
	
	public static <T extends IModel> T[] buildModels(Class<T> type, ModelData...modelsData){
		T[] models=UtilL.array(type, modelsData.length);
		for(int i=0;i<modelsData.length;i++){
			models[i]=buildModel(type, modelsData[0]);
		}
		return models;
	}
	
	public static IModel buildModel(ModelData modelData){
		return buildModel(Model.class, modelData);
	}
	
	public static <T extends IModel> T buildModel(Class<T> type, ModelData modelData){
		return buildModel(type, modelData.name, modelData.format, "vertices", modelData.getVert(), "uvs", modelData.getUv(), "normals", modelData.getNorm(), "materialIds", modelData.getMat(), "materials", modelData.materials);
	}
	
	/**
	 * Use "0" on any array for minimal empty array<br>
	 * Values:<br>
	 * vertices = float[]
	 * <br>
	 * uvs = float[]
	 * <br>
	 * normals = float[]
	 * <br>
	 * materialIds = float[] (only round numbers)
	 * <br>
	 * indices = int[]
	 * <br>
	 * genNormals = boolean (defaults to yes if no normals are present)
	 * <br>
	 * killSmooth = boolean (defaults to yes)
	 * <br>
	 * textures = array or {@link Iterable} or single element of
	 * {@link ITexture} or String (texture name)
	 * <br>
	 * materials = array or {@link Iterable} or single element of
	 * {@link IMaterial}
	 * <br>
	 * primitiveColor = float[] (rgba)
	 * <br>
	 * vertexType = ModelAttribute
	 */
	public synchronized static IModel buildModel(String name, int format, Object...data){
		return buildModel(Model.class, name, format, data);
	}
	
	/**
	 * Use "0" on any array for minimal empty array<br>
	 * Values:<br>
	 * vertices = float[]
	 * <br>
	 * uvs = float[]
	 * <br>
	 * normals = float[]
	 * <br>
	 * materialIds = float[] (only round numbers)
	 * <br>
	 * indices = int[]
	 * <br>
	 * genNormals = boolean (defaults to yes if no normals are present)
	 * <br>
	 * killSmooth = boolean (defaults to yes)
	 * <br>
	 * textures = array or {@link Iterable} or single element of
	 * {@link ITexture} or String (texture name)
	 * <br>
	 * materials = array or {@link Iterable} or single element of
	 * {@link IMaterial}
	 * <br>
	 * primitiveColor = float[] (rgba)
	 * <br>
	 * vertexType = ModelAttribute
	 */
	public synchronized static <T extends IModel> T buildModel(Class<T> type, String name, int format, Object...data){
		if(data==null||data.length==0) return null;
		if(data.length%2!=0) throw new IllegalArgumentException("Bad data!");
		
		MODEL_BUILD_DATA.clear();
		
		for(int i=0;i<data.length;i+=2){
			MODEL_BUILD_DATA.put((String)data[i], data[i+1]);
		}
		T model=buildModel(type, name, format, MODEL_BUILD_DATA);
		MODEL_BUILD_DATA.clear();
		return model;
	}
	
	/**
	 * Use "0" on any array for minimal empty array<br>
	 * Values:<br>
	 * vertices = float[]
	 * <br>
	 * uvs = float[]
	 * <br>
	 * normals = float[]
	 * <br>
	 * materialIds = int[]
	 * <br>
	 * indices = int[]
	 * <br>
	 * genNormals = boolean (defaults to yes if no normals are present)
	 * <br>
	 * killSmooth = boolean (defaults to yes)
	 * <br>
	 * textures = array or {@link Iterable} or single element of
	 * {@link ITexture} or String (texture name)
	 * <br>
	 * materials = array or {@link Iterable} or single element of
	 * {@link IMaterial}
	 * <br>
	 * primitiveColor = float[] (rgba)
	 * <br>
	 * vertexType = ModelAttribute
	 */
	public static <T extends IModel> T buildModel(Class<T> type, String name, int format, Map<String,Object> data){
		
		data.keySet().stream().filter(key->data.get(key)==null).collect(Collectors.toList()).forEach(key->data.remove(key));
		
		//PROCESS DATA
		int[] indices=(int[])data.get("indices");
		boolean hasIds=indices!=null;
		
		ModelAttribute vertexType=(ModelAttribute)data.get("vertexType");
		if(vertexType==null) vertexType=ModelAttribute.VERTEX_ATTR_3D;
		
		Object vertObj=Objects.requireNonNull(data.get("vertices"));
		float[] vert=vertObj instanceof Number&&((Number)vertObj).intValue()==0?new float[(format==GL_QUADS?4:3)*vertexType.size]:(float[])vertObj;
		
		Object killSmoothObj=data.get("killSmooth");
		boolean killSmooth=hasIds&&(killSmoothObj==null||(boolean)killSmoothObj);
		
		Object genNormalObj=data.get("genNormals");
		boolean genNormal=genNormalObj==null?!data.containsKey("normals"):(boolean)genNormalObj;
		if(!killSmooth&&genNormal) data.put("normals", hasIds?generateNormals(vert, indices):generateNormals(vert));
		
		float[] uvs=(float[])data.get("uvs"),normals=(float[])data.get("normals"),primitiveColor=(float[])data.get("primitiveColor");
		int[] materialIds=(int[])data.get("materialIds");
		if(killSmooth){
			float[] vert0=vert,uvs0=uvs,normals0=normals,primitiveColor0=primitiveColor;
			int[] materialIds0=materialIds;
			vert=new float[indices.length*3];
			if(uvs!=null) uvs=new float[indices.length*2];
			if(normals!=null) normals=new float[indices.length*3];
			if(materialIds!=null) materialIds=new int[indices.length];
			if(primitiveColor!=null) primitiveColor=new float[indices.length*4];
			
			if(format==GL_QUADS){
				int counter=0;
				for(int i=0;i<indices.length;i+=4){
					vert[counter++]=vert0[indices[i+0]*3+0];
					vert[counter++]=vert0[indices[i+0]*3+1];
					vert[counter++]=vert0[indices[i+0]*3+2];
					
					vert[counter++]=vert0[indices[i+1]*3+0];
					vert[counter++]=vert0[indices[i+1]*3+1];
					vert[counter++]=vert0[indices[i+1]*3+2];
					
					vert[counter++]=vert0[indices[i+2]*3+0];
					vert[counter++]=vert0[indices[i+2]*3+1];
					vert[counter++]=vert0[indices[i+2]*3+2];
					
					vert[counter++]=vert0[indices[i+3]*3+0];
					vert[counter++]=vert0[indices[i+3]*3+1];
					vert[counter++]=vert0[indices[i+3]*3+2];
				}
				if(uvs!=null){
					counter=0;
					for(int i=0;i<indices.length;i+=4){
						uvs[counter++]=uvs0[indices[i+0]*2+0];
						uvs[counter++]=uvs0[indices[i+0]*2+1];
						
						uvs[counter++]=uvs0[indices[i+1]*2+0];
						uvs[counter++]=uvs0[indices[i+1]*2+1];
						
						uvs[counter++]=uvs0[indices[i+2]*2+0];
						uvs[counter++]=uvs0[indices[i+2]*2+1];
						
						uvs[counter++]=uvs0[indices[i+3]*2+0];
						uvs[counter++]=uvs0[indices[i+3]*2+1];
					}
				}
				if(normals!=null&&!genNormal){
					counter=0;
					for(int i=0;i<indices.length;i+=4){
						normals[counter++]=normals0[indices[i+0]*3+0];
						normals[counter++]=normals0[indices[i+0]*3+1];
						normals[counter++]=normals0[indices[i+0]*3+2];
						
						normals[counter++]=normals0[indices[i+1]*3+0];
						normals[counter++]=normals0[indices[i+1]*3+1];
						normals[counter++]=normals0[indices[i+1]*3+2];
						
						normals[counter++]=normals0[indices[i+2]*3+0];
						normals[counter++]=normals0[indices[i+2]*3+1];
						normals[counter++]=normals0[indices[i+2]*3+2];
						
						normals[counter++]=normals0[indices[i+3]*3+0];
						normals[counter++]=normals0[indices[i+3]*3+1];
						normals[counter++]=normals0[indices[i+3]*3+2];
					}
				}
				if(materialIds!=null){
					counter=0;
					for(int i=0;i<indices.length;i+=4){
						materialIds[counter++]=materialIds0[indices[i+0]];
						
						materialIds[counter++]=materialIds0[indices[i+1]];
						
						materialIds[counter++]=materialIds0[indices[i+2]];
						
						materialIds[counter++]=materialIds0[indices[i+3]];
					}
				}
				if(primitiveColor!=null){
					counter=0;
					for(int i=0;i<indices.length;i+=4){
						primitiveColor[counter++]=primitiveColor0[indices[i+0]*4+0];
						primitiveColor[counter++]=primitiveColor0[indices[i+0]*4+1];
						primitiveColor[counter++]=primitiveColor0[indices[i+0]*4+2];
						primitiveColor[counter++]=primitiveColor0[indices[i+0]*4+3];
						
						primitiveColor[counter++]=primitiveColor0[indices[i+1]*4+0];
						primitiveColor[counter++]=primitiveColor0[indices[i+1]*4+1];
						primitiveColor[counter++]=primitiveColor0[indices[i+1]*4+2];
						primitiveColor[counter++]=primitiveColor0[indices[i+1]*4+3];
						
						primitiveColor[counter++]=primitiveColor0[indices[i+2]*4+0];
						primitiveColor[counter++]=primitiveColor0[indices[i+2]*4+1];
						primitiveColor[counter++]=primitiveColor0[indices[i+2]*4+2];
						primitiveColor[counter++]=primitiveColor0[indices[i+2]*4+3];
						
						primitiveColor[counter++]=primitiveColor0[indices[i+3]*4+0];
						primitiveColor[counter++]=primitiveColor0[indices[i+3]*4+1];
						primitiveColor[counter++]=primitiveColor0[indices[i+3]*4+2];
						primitiveColor[counter++]=primitiveColor0[indices[i+3]*4+3];
					}
				}
			}else{
				int counter=0;
				for(int i=0;i<indices.length;i+=3){
					vert[counter++]=vert0[indices[i+0]*3+0];
					vert[counter++]=vert0[indices[i+0]*3+1];
					vert[counter++]=vert0[indices[i+0]*3+2];
					
					vert[counter++]=vert0[indices[i+1]*3+0];
					vert[counter++]=vert0[indices[i+1]*3+1];
					vert[counter++]=vert0[indices[i+1]*3+2];
					
					vert[counter++]=vert0[indices[i+2]*3+0];
					vert[counter++]=vert0[indices[i+2]*3+1];
					vert[counter++]=vert0[indices[i+2]*3+2];
				}
				if(uvs!=null){
					counter=0;
					for(int i=0;i<indices.length;i+=3){
						uvs[counter++]=uvs0[indices[i+0]*2+0];
						uvs[counter++]=uvs0[indices[i+0]*2+1];
						
						uvs[counter++]=uvs0[indices[i+1]*2+0];
						uvs[counter++]=uvs0[indices[i+1]*2+1];
						
						uvs[counter++]=uvs0[indices[i+2]*2+0];
						uvs[counter++]=uvs0[indices[i+2]*2+1];
					}
				}
				if(normals!=null){
					counter=0;
					for(int i=0;i<indices.length;i+=3){
						normals[counter++]=normals0[indices[i+0]*3+0];
						normals[counter++]=normals0[indices[i+0]*3+1];
						normals[counter++]=normals0[indices[i+0]*3+2];
						
						normals[counter++]=normals0[indices[i+1]*3+0];
						normals[counter++]=normals0[indices[i+1]*3+1];
						normals[counter++]=normals0[indices[i+1]*3+2];
						
						normals[counter++]=normals0[indices[i+2]*3+0];
						normals[counter++]=normals0[indices[i+2]*3+1];
						normals[counter++]=normals0[indices[i+2]*3+2];
					}
				}
				if(materialIds!=null){
					counter=0;
					for(int i=0;i<indices.length;i+=3){
						materialIds[counter++]=materialIds0[indices[i+0]];
						
						materialIds[counter++]=materialIds0[indices[i+1]];
						
						materialIds[counter++]=materialIds0[indices[i+2]];
					}
				}
				if(primitiveColor!=null){
					counter=0;
					for(int i=0;i<indices.length;i+=3){
						primitiveColor[counter++]=primitiveColor0[indices[i+0]*4+0];
						primitiveColor[counter++]=primitiveColor0[indices[i+0]*4+1];
						primitiveColor[counter++]=primitiveColor0[indices[i+0]*4+2];
						primitiveColor[counter++]=primitiveColor0[indices[i+0]*4+3];
						
						primitiveColor[counter++]=primitiveColor0[indices[i+1]*4+0];
						primitiveColor[counter++]=primitiveColor0[indices[i+1]*4+1];
						primitiveColor[counter++]=primitiveColor0[indices[i+1]*4+2];
						primitiveColor[counter++]=primitiveColor0[indices[i+1]*4+3];
						
						primitiveColor[counter++]=primitiveColor0[indices[i+2]*4+0];
						primitiveColor[counter++]=primitiveColor0[indices[i+2]*4+1];
						primitiveColor[counter++]=primitiveColor0[indices[i+2]*4+2];
						primitiveColor[counter++]=primitiveColor0[indices[i+2]*4+3];
					}
				}
			}
			hasIds=false;
			indices=null;
			
			if(genNormal) normals=generateNormals(vert);
		}
		
		if(vert.length%vertexType.size!=0) throw new IllegalArgumentException(vert.length+" is not a valid vertex count for dimensions of "+vertexType.size+" in model "+name);
		
		T model=create(type, name, format, hasIds?indices:null, vertexType, vert, new Object[]{uvs,normals,materialIds,primitiveColor}, UV_ATTR, NORMAL_ATTR, MATERIAL_ID_ATTR, COLOR_ATTR);
		
		//INJECT TEXTURE
		UtilL.iterate(data.get("textures"), obj->model.addTexture(obj instanceof ITexture?(ITexture)obj:TextureLoader.loadTexture((String)obj)));
		
		UtilL.iterate(data.get("materials"), IMaterial.class, model::addMaterial);
		if(model.getMaterialCount()==0) model.createMaterial();
		
		return model;
	}
	
	public static <T extends IModel> T create(Class<T> type, String name, int format, int[] indices, float[] vertex, Object[] data, ModelAttribute...attrs){
		return create(type, name, format, indices, vertex, true, data, attrs);
	}
	
	public static <T extends IModel> T create(Class<T> type, String name, int format, int[] indices, float[] vertex, boolean print, Object[] data, ModelAttribute...attrs){
		return create(type, name, format, indices, ModelAttribute.VERTEX_ATTR_3D, vertex, data, attrs);
	}
	
	public static <T extends IModel> T create(Class<T> type, String name, int format, int[] indices, ModelAttribute vertexType, float[] vertex, Object[] data, ModelAttribute...attrs){
		return create(type, name, format, indices, vertexType, vertex, true, data, attrs);
	}
	
	public static <T extends IModel> T create(Class<T> type, String name, int format, int[] indices, ModelAttribute vertexType, float[] vertex, boolean print, Object[] data, ModelAttribute...attrs){
		if(attrs.length!=data.length) throw new RuntimeException("Attributes not equal size as data!");
		
		T model;
		try{
			Constructor<T> ctr=type.getConstructor(String.class);
			ctr.setAccessible(true);
			model=ctr.newInstance(Objects.requireNonNull(name));
		}catch(NoSuchMethodException e){
			throw new RuntimeException("Missing "+type.getName()+".<init>(String)", e);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		
		IFrustrumShape shape=calcShape(vertex, vertexType.size);
		
		Game.glCtx(()->{
			int vao=createVao();
			List<Vbo> vbos=new ArrayList<>();
			List<ModelAttribute> attributes=new ArrayList<>();
			
			boolean hasIds=indices!=null;
			
			if(hasIds) bindIndices(indices);
			putAttribute(vbos, attributes, vertexType, vertex);
			for(int i=0;i<data.length;i++){
				Object dataPart=data[i];
				if(dataPart==null) continue;
				if(dataPart.getClass().equals(float[].class)) putAttribute(vbos, attributes, attrs[i], (float[])dataPart);
				else putAttribute(vbos, attributes, attrs[i], (int[])dataPart);
			}
			
			unbindVao();
			
			model.load(vao, hasIds?indices.length:vertex.length, hasIds, format, UtilL.array(vbos), vertexType, attributes.toArray(new ModelAttribute[attributes.size()]), shape);
			if(print&&!name.startsWith("Gen_")) LogUtil.println("Loaded:", model);
		});
		return model;
	}
	
	//	private static void putAttribute(IntList vbos, IntList attributes, int attrListId, int[] data, int partSize){
	//		if(data==null||data.length==0) return;
	//
	//		int vbo=createVbo();
	//
	//		glBindBuffer(GL_ARRAY_BUFFER, vbo);
	//
	//		glBufferData(GL_ARRAY_BUFFER, BufferUtil.store(data), GL_STATIC_DRAW);
	//		glVertexAttribPointer(attrListId, partSize, GL_INT, false, 0, 0);
	//
	//		glBindBuffer(GL_ARRAY_BUFFER, 0);
	//
	//		vbos.add(vbo);
	//		attributes.add(attrListId);
	//	}
	
	private static void putAttribute(List<Vbo> vbos, List<ModelAttribute> attributes, ModelAttribute attr, float[] data){
		if(data==null||data.length==0) return;
		Vbo vbo=Vbo.create(GL_ARRAY_BUFFER);
		
		vbo.bind();
		vbo.storeData(data);
		glVertexAttribPointer(attr.id, attr.size, GL_FLOAT, false, 0, 0);
		vbo.unbind();
		
		vbos.add(vbo);
		attributes.add(attr);
	}
	
	private static void putAttribute(List<Vbo> vbos, List<ModelAttribute> attributes, ModelAttribute attr, int[] data){
		if(data==null||data.length==0) return;
		Vbo vbo=Vbo.create(GL_ARRAY_BUFFER);
		vbo.bind();
		vbo.storeData(data);
		glVertexAttribIPointer(attr.id, attr.size, GL_INT, 0, 0);
		vbo.unbind();
		
		vbos.add(vbo);
		attributes.add(attr);
	}
	
	public static IFrustrumShape calcShape(FloatBuffer vert, int dimensions){
		if(vert.limit()==0) return new FrustrumBool(false);
		
		Vec3f start=new Vec3f(),end=new Vec3f();
		
		for(int i=0;i<vert.limit();i+=dimensions){
			float p1=vert.get(i+0);
			start.x(Math.min(start.x(), p1));
			end.x(Math.max(end.x(), p1));
			
			if(dimensions>1){
				float p2=vert.get(i+1);
				start.y(Math.min(start.y(), p2));
				end.y(Math.max(end.y(), p2));
				
				if(dimensions>2){
					float p3=vert.get(i+2);
					start.z(Math.min(start.z(), p3));
					end.z(Math.max(end.z(), p3));
				}
			}
		}
		
		return new FrustrumCube(start, end);
	}
	
	public static IFrustrumShape calcShape(float[] vert, int dimensions){
		if(vert.length==0) return new FrustrumBool(false);
		
		Vec3f start=new Vec3f(),end=new Vec3f();
		
		for(int i=0;i<vert.length;i+=dimensions){
			float p1=vert[i+0];
			start.x(Math.min(start.x(), p1));
			end.x(Math.max(end.x(), p1));
			
			if(dimensions>1){
				float p2=vert[i+1];
				start.y(Math.min(start.y(), p2));
				end.y(Math.max(end.y(), p2));
				
				if(dimensions>2){
					float p3=vert[i+2];
					start.z(Math.min(start.z(), p3));
					end.z(Math.max(end.z(), p3));
				}
			}
		}
		return new FrustrumCube(start, end);
	}
	
	private synchronized static float[] generateNormals(float[] vert){
		float[] normals=new float[vert.length];
		for(int i=0;i<vert.length;i+=9){
			int i0=i+2*3;
			int i1=i+1*3;
			int i2=i+0*3;
			V0.set(vert[i0], vert[i0+1], vert[i0+2]);
			V1.set(vert[i1], vert[i1+1], vert[i1+2]);
			V2.set(vert[i2], vert[i2+1], vert[i2+2]);
			V12.set(V2).subRev(V1).crossProduct(V01.set(V1).subRev(V0), NORMAL);
			
			normals[i0+0]=normals[i1+0]=normals[i2+0]=NORMAL.x();
			normals[i0+1]=normals[i1+1]=normals[i2+1]=NORMAL.y();
			normals[i0+2]=normals[i1+2]=normals[i2+2]=NORMAL.z();
		}
		
		return normals;
	}
	
	private synchronized static float[] generateNormals(float[] vert, int[] indicies){
		float[] normals=new float[vert.length];
		
		for(int i=0;i<indicies.length;i+=3){
			int i0=indicies[i+2]*3;
			int i1=indicies[i+1]*3;
			int i2=indicies[i+0]*3;
			
			V0.set(vert[i0], vert[i0+1], vert[i0+2]);
			V1.set(vert[i1], vert[i1+1], vert[i1+2]);
			V2.set(vert[i2], vert[i2+1], vert[i2+2]);
			V12.set(V2).subRev(V1).crossProduct(V01.set(V1).subRev(V0), NORMAL);
			
			normals[i0+0]=normals[i1+0]=normals[i2+0]=NORMAL.x();
			normals[i0+1]=normals[i1+1]=normals[i2+1]=NORMAL.y();
			normals[i0+2]=normals[i1+2]=normals[i2+2]=NORMAL.z();
		}
		
		return normals;
	}
	
	private static int createVao(){
		int vao=glGenVertexArrays();
		glBindVertexArray(vao);
		return vao;
	}
	
	private static void bindIndices(int[] indicies){
		int vbo=glGenBuffers();
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
		glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, BufferUtil.store(indicies));
		
	}
	
	private static void unbindVao(){
		glBindVertexArray(0);
	}
	
	public static void deleteAll(){
		UtilL.doAndClear(MODELS, IModel::delete);
	}
	
}
