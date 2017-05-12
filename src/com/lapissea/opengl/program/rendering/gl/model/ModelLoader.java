package com.lapissea.opengl.program.rendering.gl.model;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.lapissea.opengl.abstr.opengl.assets.IMaterial;
import com.lapissea.opengl.abstr.opengl.assets.IModel;
import com.lapissea.opengl.abstr.opengl.assets.ITexture;
import com.lapissea.opengl.abstr.opengl.assets.ModelAttribute;
import com.lapissea.opengl.abstr.opengl.frustrum.FrustrumCube;
import com.lapissea.opengl.abstr.opengl.frustrum.IFrustrumShape;
import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.gl.model.ObjModelLoader.ModelData;
import com.lapissea.opengl.program.rendering.gl.texture.TextureLoader;
import com.lapissea.opengl.program.util.BufferUtil;
import com.lapissea.opengl.program.util.LogUtil;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class ModelLoader{
	
	private static final List<Model>			MODELS			=new ArrayList<>();
	private static final HashMap<String,Object>	MODEL_BUILD_DATA=new HashMap<>();
	
	public static final IModel EMPTY_MODEL=new Model("EMPTY_MODEL"){
		
		@Override
		public IModel load(int vao, int vertexCount, boolean usesIndicies, boolean usesQuads, int[] vbos, ModelAttribute[] attributeIds, IFrustrumShape shape){
			throw new UnsupportedOperationException();
		}
		
		@Override
		public IModel drawCall(){
			return this;
		}
		
		@Override
		public IModel enableAttributes(){
			return this;
		}
		
		@Override
		public IModel disableAttributes(){
			return this;
		}
	};
	
	private static final Vec3f V0=new Vec3f(),V1=new Vec3f(),V2=new Vec3f(),V01=new Vec3f(),V12=new Vec3f(),NORMAL=new Vec3f();
	
	public static Model[] buildModels(ModelData...modelsData){
		Model[] models=new Model[modelsData.length];
		for(int i=0;i<modelsData.length;i++){
			models[i]=buildModel(modelsData[0]);
		}
		return models;
	}
	
	public static <T extends Model> T[] buildModels(Class<T> type, ModelData...modelsData){
		T[] models=UtilM.array(type, modelsData.length);
		for(int i=0;i<modelsData.length;i++){
			models[i]=buildModel(type, modelsData[0]);
		}
		return models;
	}
	
	public static Model buildModel(ModelData modelData){
		return buildModel(Model.class, modelData);
	}
	
	public static <T extends Model> T buildModel(Class<T> type, ModelData modelData){
		return buildModel(type, modelData.name, modelData.usesQuads, "vertices", modelData.getVert(), "uvs", modelData.getUv(), "normals", modelData.getNorm(), "materialIds", modelData.getMat(), "materials", modelData.materials);
	}
	
	/**
	 * Use "0" on any array for minimal empty array<br>
	 * Values:<br>
	 * vertices = float[]
	 * <br>uvs = float[]
	 * <br>normals = float[]
	 * <br>materialIds = float[] (only round numbers)
	 * <br>indices = int[]
	 * <br>genNormals = boolean (defaults to yes if no normals are present)
	 * <br>killSmooth = boolean (defaults to yes)
	 * <br>textures = array or {@link Iterable} or single element of {@link ITexture} or String (texture name)
	 * <br>materials = array or {@link Iterable} or single element of {@link IMaterial}
	 * <br>primitiveColor = float[] (rgba)
	 */
	public synchronized static Model buildModel(String name, boolean usesQuads, Object...data){
		return buildModel(Model.class, name, usesQuads, data);
	}
	
	/**
	 * Use "0" on any array for minimal empty array<br>
	 * Values:<br>
	 * vertices = float[]
	 * <br>uvs = float[]
	 * <br>normals = float[]
	 * <br>materialIds = float[] (only round numbers)
	 * <br>indices = int[]
	 * <br>genNormals = boolean (defaults to yes if no normals are present)
	 * <br>killSmooth = boolean (defaults to yes)
	 * <br>textures = array or {@link Iterable} or single element of {@link ITexture} or String (texture name)
	 * <br>materials = array or {@link Iterable} or single element of {@link IMaterial}
	 * <br>primitiveColor = float[] (rgba)
	 */
	public synchronized static <T extends Model> T buildModel(Class<T> type, String name, boolean usesQuads, Object...data){
		if(data==null||data.length==0) return null;
		if(data.length%2!=0) throw new IllegalArgumentException("Bad data!");
		
		MODEL_BUILD_DATA.clear();
		
		for(int i=0;i<data.length;i+=2){
			MODEL_BUILD_DATA.put((String)data[i], data[i+1]);
		}
		T model=buildModel(type, name, usesQuads, MODEL_BUILD_DATA);
		MODEL_BUILD_DATA.clear();
		return model;
	}
	
	/**
	 * Use "0" on any array for minimal empty array<br>
	 * Values:<br>
	 * vertices = float[]
	 * <br>uvs = float[]
	 * <br>normals = float[]
	 * <br>materialIds = float[] (only round numbers)
	 * <br>indices = int[]
	 * <br>genNormals = boolean (defaults to yes if no normals are present)
	 * <br>killSmooth = boolean (defaults to yes)
	 * <br>textures = array or {@link Iterable} or single element of {@link ITexture} or String (texture name)
	 * <br>materials = array or {@link Iterable} or single element of {@link IMaterial}
	 * <br>primitiveColor = float[] (rgba)
	 */
	public static <T extends Model> T buildModel(Class<T> type, String name, boolean usesQuads, HashMap<String,Object> data){
		
		data.keySet().stream().filter(key->data.get(key)==null).collect(Collectors.toList()).forEach(key->data.remove(key));
		
		//PROCESS DATA
		int[] indices=(int[])data.get("indices");
		boolean hasIds=indices!=null;
		float[] vert=(float[])Objects.requireNonNull(data.get("vertices"));
		
		Object killSmoothObj=data.get("killSmooth");
		boolean killSmooth=hasIds&&(killSmoothObj==null||(boolean)killSmoothObj);
		
		Object genNormalObj=data.get("genNormals");
		boolean genNormal=genNormalObj==null?!data.containsKey("normals"):(boolean)genNormalObj;
		if(!killSmooth&&genNormal) data.put("normals", hasIds?generateNormals(vert, indices):generateNormals(vert));
		
		float[] uvs=(float[])data.get("uvs"),normals=(float[])data.get("normals"),materialIds=(float[])data.get("materialIds"),primitiveColor=(float[])data.get("primitiveColor");
		
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
		
		if(killSmooth){
			float[] vert0=vert,uvs0=uvs,normals0=normals,materialIds0=materialIds,primitiveColor0=primitiveColor;
			vert=new float[indices.length*3];
			if(uvs!=null) uvs=new float[indices.length*2];
			if(normals!=null) normals=new float[indices.length*3];
			if(materialIds!=null) materialIds=new float[indices.length];
			if(primitiveColor!=null) primitiveColor=new float[indices.length*4];
			
			if(usesQuads){
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
			}
			else{
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
		
		//INJECT TEXTURE
		Object textures=data.get("textures");
		if(textures!=null){
			if(textures instanceof ITexture) model.addTexture((ITexture)textures);
			if(textures instanceof String) model.addTexture(TextureLoader.loadTexture((String)textures));
			else if(textures instanceof Iterable){
				((Iterable<?>)textures).forEach(tx->model.addTexture(tx instanceof ITexture?(ITexture)tx:TextureLoader.loadTexture((String)tx)));
			}
			else if(textures.getClass().isArray()){
				Class<?> typeTx=textures.getClass().getComponentType();
				
				if(UtilM.instanceOf(typeTx, ITexture.class)){
					for(ITexture tx:(ITexture[])textures){
						model.addTexture(tx);
					}
				}
				else if(typeTx==String.class){
					for(String txName:(String[])textures){
						synchronized(TextureLoader.class){
							model.addTexture(TextureLoader.loadTexture(txName));
						}
					}
				}
			}
		}
		
		Object materials=data.get("materials");
		if(materials!=null){
			if(materials instanceof IMaterial) model.addMaterial((IMaterial)materials);
			else if(materials instanceof Iterable){
				((Iterable<?>)materials).forEach(mat->model.addMaterial((IMaterial)mat));
			}
			else if(materials.getClass().isArray()){
				Class<?> typeTx=materials.getClass().getComponentType();
				
				if(UtilM.instanceOf(typeTx, IMaterial.class)){
					for(IMaterial tx:(IMaterial[])materials){
						model.addMaterial(tx);
					}
				}
			}
		}
		if(model.getMaterialCount()==0) model.createMaterial();
		
		float[] vertF=vert,uvsF=uvs,normalsF=normals,materialIdsF=materialIds,primitiveColorF=primitiveColor;
		boolean hasIdsF=hasIds;
		int[] indicesF=indices;
		
		//CALL BUILD
		Game.glCtx(()->{
			int vao=createVao();
			IntList vbos=new IntArrayList();
			List<ModelAttribute> attributes=new ArrayList<>();
			if(hasIdsF) bindIndices(indicesF);
			
			putAttribute(vbos, attributes, ModelAttribute.VERTEX_ATTR, vertF);
			putAttribute(vbos, attributes, ModelAttribute.UV_ATTR, uvsF);
			putAttribute(vbos, attributes, ModelAttribute.NORMAL_ATTR, normalsF);
			putAttribute(vbos, attributes, ModelAttribute.MAERIAL_ID_ATTR, materialIdsF);
			putAttribute(vbos, attributes, ModelAttribute.PRIMITIVE_COLOR_ATTR, primitiveColorF);
			unbindVao();

			model.load(vao, hasIdsF?indicesF.length:vertF.length, hasIdsF, usesQuads, vbos.toIntArray(), attributes.toArray(new ModelAttribute[attributes.size()]), calcShape(vertF));
			if(!name.startsWith("Gen_")) LogUtil.println("Loaded:", model);
		});
		return model;
	}
	
	//	private static void putAttribute(IntList vbos, IntList attributes, int attrListId, int[] data, int partSize){
	//		if(data==null||data.length==0) return;
	//		
	//		int vbo=createVbo();
	//		
	//		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
	//		
	//		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, BufferUtil.store(data), GL15.GL_STATIC_DRAW);
	//		GL20.glVertexAttribPointer(attrListId, partSize, GL11.GL_INT, false, 0, 0);
	//		
	//		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	//		
	//		vbos.add(vbo);
	//		attributes.add(attrListId);
	//	}
	
	private static void putAttribute(IntList vbos, List<ModelAttribute> attributes, ModelAttribute attr, float[] data){
		if(data==null||data.length==0) return;
		int vbo=GL15.glGenBuffers();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, BufferUtil.store(data), GL15.GL_DYNAMIC_DRAW);
		GL20.glVertexAttribPointer(attr.id, attr.size, GL11.GL_FLOAT, false, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		vbos.add(vbo);
		attributes.add(attr);
	}
	
	//	public static BasicModel buildModel(String name, float[] vert, float[] uvs, float[] normals, int[] indices){
	//		checkData(vert, uvs);
	//		
	//		BasicModel model=new BasicModel(name);
	//		float rad=calcRad(vert);
	//		
	//		Game.runInRenderContext(()->{
	//			int vao=createVao();
	//			bindIndices(indices);
	//			int[] vbos=putData(vert, uvs, normals, new float[4]);
	//			unbindVao();
	//			model.load(vao, indices.length, false, vbos, rad, false);
	//		});
	//		
	//		return model;
	//	}
	
	private static IFrustrumShape calcShape(float[] vert){
		Vec3f start=new Vec3f(),end=new Vec3f();
		
		for(int i=0;i<vert.length;i+=3){
			float p1=vert[i+0],p2=vert[i+1],p3=vert[i+2];
			start.x(Math.min(start.x(), p1));
			start.y(Math.min(start.y(), p2));
			start.z(Math.min(start.z(), p3));
			end.x(Math.max(end.x(), p1));
			end.y(Math.max(end.y(), p2));
			end.z(Math.max(end.z(), p3));
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
		int vao=GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		return vao;
	}
	
	private static void bindIndices(int[] indicies){
		int vbo=GL15.glGenBuffers();
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
		
		GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, BufferUtil.store(indicies));
		
	}
	
	private static void unbindVao(){
		GL30.glBindVertexArray(0);
	}
	
	public static void deleteAll(){
		UtilM.doAndClear(MODELS, Model::delete);
	}
	
}
