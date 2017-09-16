package com.lapissea.opengl.resources.model;

import static com.lapissea.opengl.window.assets.ModelAttribute.*;
import static com.lapissea.util.UtilL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.lapissea.opengl.core.Game;
import com.lapissea.opengl.rendering.frustrum.FrustrumBool;
import com.lapissea.opengl.rendering.frustrum.FrustrumCube;
import com.lapissea.opengl.resources.model.parsers.WavefrontParser;
import com.lapissea.opengl.util.PairM;
import com.lapissea.opengl.util.UtilM;
import com.lapissea.opengl.util.function.Predicates;
import com.lapissea.opengl.util.math.vec.Vec3f;
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
import com.lapissea.util.UtilL.InputStreamSilent;

@SuppressWarnings("unchecked")
public class ModelLoader{
	
	private static final Vec3f V0=new Vec3f(),V1=new Vec3f(),V2=new Vec3f(),V01=new Vec3f(),V12=new Vec3f(),NORMAL=new Vec3f();
	
	private static final List<IModel>		MODELS			=new ArrayList<>();
	private static final List<ModelParser>	MODEL_PARSERS	=new ArrayList<>();
	
	private static final Predicate<String> ANY_SUPPORTED=s->{
		int pos=s.lastIndexOf('.');
		return pos!=-1&&getLoaderByExtension(s.substring(pos+1))!=null;
	};
	
	static{
		registerModelParser(new WavefrontParser());
	}
	
	public static final IModel EMPTY_MODEL=new EmptyModel();
	
	public static void registerModelParser(ModelParser parser){
		if(MODEL_PARSERS.contains(parser)) throw new IllegalArgumentException(parser+" parser already exists!");
		MODEL_PARSERS.add(parser);
	}
	
	private static <T extends IModel> T createNew(Class<T> type, String name){
		
		try{
			Constructor<T> ctr=type.getConstructor(String.class);
			ctr.setAccessible(true);
			return ctr.newInstance(Objects.requireNonNull(name));
		}catch(NoSuchMethodException e){
			throw new RuntimeException("Missing "+type.getName()+".<init>(String)", e);
		}catch(Exception e){
			throw UtilL.uncheckedThrow(e);
		}
	}
	
	public static IModel loadAndBuild(String location){
		return loadAndBuild(Model.class, location);
	}
	
	public static <T extends IModel> T loadAndBuild(Class<T> type, String location){
		T t=createNew(type, location);
		
		load(location, data->{
			data.withType(type);
			buildModel(data, t);
		});
		
		return t;
	}
	
	public static IModel[] loadFolderAndBuild(String location){
		return loadFolderAndBuild(Model.class, location);
	}
	
	public static <T extends IModel> T[] loadFolderAndBuild(Class<T> type, String location){
		ModelBuilder[] data=loadFolder(location);
		for(ModelBuilder builder:data){
			builder.withType(type);
		}
		return buildModels(data);
	}
	
	private static ModelBuilder load(String location, ModelParser parser){
		LogUtil.println("Loading model:", location);
		try(InputStreamSilent modelData=UtilL.silentClose(UtilM.getResource("models/"+location))){
			if(modelData==null) throw new IllegalArgumentException("Model \""+location+"\" does not exist!");
			try{
				return parser.load(location, modelData);
			}catch(IOException e){
				throw uncheckedThrow(e);
			}
		}
	}
	
	public static void load(String location, Consumer<ModelBuilder> onLoad){
		Game.load(()->onLoad.accept(load(location)));
	}
	
	public static ModelBuilder load(String location){
		int pos=location.lastIndexOf('.');
		if(pos==-1){
			//assume extension
			
			String name,path;
			int pos0=location.lastIndexOf('/');
			if(pos0==-1){
				name=location;
				path="";
			}else{
				name=location.substring(pos0+1);
				path=location.substring(0, pos0);
			}
			
			
			return UtilM.getResourceFolderContentList("models/"+path).stream()
					.filter(fileName->name.equals(before(fileName, '.')))
					.map(fileName->new PairM<>(fileName, getLoaderByExtension(after(fileName, '.'))))
					.filter(pair->pair.obj2!=null)
					.findAny()
					.map(pair->load(pair.obj1, pair.obj2))
					.orElseThrow(()->new IllegalArgumentException("Model \""+location+"\" does not exist!"));
			
		}
		
		String extension=location.substring(pos+1);
		ModelParser parser=getLoaderByExtension(extension);
		if(parser==null) throw new IllegalArgumentException("Model \""+location+"\" has extension \""+extension+"\" that is not supported!");
		
		return load(location, parser);
	}
	
	public static ModelParser getLoaderByExtension(String extension){
		
		for(ModelParser parser:MODEL_PARSERS){
			if(parser.extensionSupported(extension)) return parser;
		}
		
		return null;
	}
	
	public static ModelBuilder[] loadFolder(String location){
		return loadFolder(location, Predicates.TRUE());
	}
	
	public static ModelBuilder[] loadFolder(String location, Predicate<String> fileFilter){
		Stream<String> s=UtilM.getResourceFolderContentStream("models/"+location);
		if(s==null) throw new IllegalArgumentException("Folder "+location+" does not exist!");
		
		return s.filter(fileFilter.and(ANY_SUPPORTED)).map(fname->load(location+"/"+fname)).toArray(ModelBuilder[]::new);
	}
	
	public static <T extends IModel> T[] buildModels(ModelBuilder...modelsData){
		return (T[])convert(modelsData, modelsData[0].type, ModelLoader::buildModel);
	}
	
	public static <T extends IModel> T buildModel(ModelBuilder builder){
		return (T)buildModel(builder, createNew(builder.type, builder.name));
	}
	
	public static <T extends IModel> T buildModel(ModelBuilder builder, T destintation){
		int faceSize=builder.format==GL_LINES?2:builder.format==GL_QUADS?4:3;
		
		if(builder.vertexColors==ModelBuilder.AUTO_FLOAT) builder.withVertexColors(new float[faceSize*4]);
		if(builder.vertices==ModelBuilder.AUTO_FLOAT) builder.withVertecies(new float[faceSize*builder.vertexType.size]);
		if(builder.uvs==ModelBuilder.AUTO_FLOAT) builder.withUvs(new float[faceSize*2]);
		
		//PROCESS DATA
		int[] indices=builder.indices;
		boolean hasIds=indices!=null;
		
		float[] vert=builder.vertices;
		
		boolean killSmooth=hasIds&&builder.killSmooth;
		
		boolean genNormal=builder.generateNormals;
		if(!killSmooth&&genNormal) builder.withNormals(hasIds?generateNormals(vert, indices):generateNormals(vert));
		
		float[] uvs=builder.uvs;
		float[] normals=builder.normals;
		float[] vertexColors=builder.vertexColors;
		int[] materialIds=builder.materials;
		
		if(killSmooth){
			float[] vert0=vert,uvs0=uvs,normals0=normals,vertexColors0=vertexColors;
			int[] materialIds0=materialIds;
			vert=new float[indices.length*3];
			if(uvs!=null) uvs=new float[indices.length*2];
			if(normals!=null) normals=new float[indices.length*3];
			if(materialIds!=null) materialIds=new int[indices.length];
			if(vertexColors!=null) vertexColors=new float[indices.length*4];
			
			if(builder.format==GL_QUADS){
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
				if(vertexColors!=null){
					counter=0;
					for(int i=0;i<indices.length;i+=4){
						vertexColors[counter++]=vertexColors0[indices[i+0]*4+0];
						vertexColors[counter++]=vertexColors0[indices[i+0]*4+1];
						vertexColors[counter++]=vertexColors0[indices[i+0]*4+2];
						vertexColors[counter++]=vertexColors0[indices[i+0]*4+3];
						
						vertexColors[counter++]=vertexColors0[indices[i+1]*4+0];
						vertexColors[counter++]=vertexColors0[indices[i+1]*4+1];
						vertexColors[counter++]=vertexColors0[indices[i+1]*4+2];
						vertexColors[counter++]=vertexColors0[indices[i+1]*4+3];
						
						vertexColors[counter++]=vertexColors0[indices[i+2]*4+0];
						vertexColors[counter++]=vertexColors0[indices[i+2]*4+1];
						vertexColors[counter++]=vertexColors0[indices[i+2]*4+2];
						vertexColors[counter++]=vertexColors0[indices[i+2]*4+3];
						
						vertexColors[counter++]=vertexColors0[indices[i+3]*4+0];
						vertexColors[counter++]=vertexColors0[indices[i+3]*4+1];
						vertexColors[counter++]=vertexColors0[indices[i+3]*4+2];
						vertexColors[counter++]=vertexColors0[indices[i+3]*4+3];
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
				if(vertexColors!=null){
					counter=0;
					for(int i=0;i<indices.length;i+=3){
						vertexColors[counter++]=vertexColors0[indices[i+0]*4+0];
						vertexColors[counter++]=vertexColors0[indices[i+0]*4+1];
						vertexColors[counter++]=vertexColors0[indices[i+0]*4+2];
						vertexColors[counter++]=vertexColors0[indices[i+0]*4+3];
						
						vertexColors[counter++]=vertexColors0[indices[i+1]*4+0];
						vertexColors[counter++]=vertexColors0[indices[i+1]*4+1];
						vertexColors[counter++]=vertexColors0[indices[i+1]*4+2];
						vertexColors[counter++]=vertexColors0[indices[i+1]*4+3];
						
						vertexColors[counter++]=vertexColors0[indices[i+2]*4+0];
						vertexColors[counter++]=vertexColors0[indices[i+2]*4+1];
						vertexColors[counter++]=vertexColors0[indices[i+2]*4+2];
						vertexColors[counter++]=vertexColors0[indices[i+2]*4+3];
					}
				}
			}
			hasIds=false;
			indices=null;
			
			if(genNormal) normals=generateNormals(vert);
		}
		
		if(vert.length%builder.vertexType.size!=0) throw new IllegalArgumentException(vert.length+" is not a valid vertex count for dimensions of "+builder.vertexType.size+" in model "+builder.name);
		
		init(destintation, builder.name, builder.format, hasIds?indices:null, builder.vertexType, vert, new Object[]{uvs,normals,materialIds,vertexColors}, UV_ATTR, NORMAL_ATTR, MATERIAL_ID_ATTR, COLOR_ATTR);
		
		//INJECT TEXTURE
		if(builder.textures!=null){
			for(ITexture t:builder.textures){
				destintation.addTexture(t);
			}
		}
		
		UtilL.iterate(builder.materialDefs, IMaterial.class, destintation::addMaterial);
		if(destintation.getMaterialCount()==0) destintation.createMaterial();
		
		destintation.culface(builder.culface);
		
		return destintation;
	}
	
	public static <T extends IModel> T init(T model, String name, int format, int[] indices, float[] vertex, Object[] data, ModelAttribute...attrs){
		return init(model, name, format, indices, vertex, true, data, attrs);
	}
	
	public static <T extends IModel> T init(T model, String name, int format, int[] indices, float[] vertex, boolean print, Object[] data, ModelAttribute...attrs){
		return init(model, name, format, indices, ModelAttribute.VERTEX_ATTR_3D, vertex, data, attrs);
	}
	
	public static <T extends IModel> T init(T model, String name, int format, int[] indices, ModelAttribute vertexType, float[] vertex, Object[] data, ModelAttribute...attrs){
		return init(model, name, format, indices, vertexType, vertex, true, data, attrs);
	}
	
	public static <T extends IModel> T init(T model, String name, int format, int[] indices, ModelAttribute vertexType, float[] vertex, boolean print, Object[] data, ModelAttribute...attrs){
		if(attrs.length!=data.length) throw new RuntimeException("Attributes not equal size as data!");
		
		model.notifyLoading();
		IFrustrumShape shape=calcShape(vertex, vertexType.size);
		Game.glCtx(()->{
			synchronized(model){
				List<Vbo> vbos=new ArrayList<>();
				List<ModelAttribute> attributes=new ArrayList<>();
				
				boolean hasIds=indices!=null;
				
				int vao=glGenVertexArrays();
				glBindVertexArray(vao);
				int indicesId=hasIds?bindIndices(indices):-1;
				
				putAttribute(vbos, attributes, vertexType, vertex);
				for(int i=0;i<data.length;i++){
					Object dataPart=data[i];
					if(dataPart==null) continue;
					if(dataPart.getClass().equals(float[].class)) putAttribute(vbos, attributes, attrs[i], (float[])dataPart);
					else putAttribute(vbos, attributes, attrs[i], (int[])dataPart);
				}
				
				unbindVao();
				model.load(vao, hasIds?indices.length:vertex.length, indicesId, format, UtilL.array(vbos), vertexType, attributes.toArray(new ModelAttribute[attributes.size()]), shape);
				if(print&&!name.startsWith("Gen_")) LogUtil.println("Loaded:", model);
			}
		});
		return model;
	}
	
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
	
	private static int bindIndices(int[] indicies){
		int vbo=glGenBuffers();
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, BufferUtil.store(indicies), GL_DYNAMIC_DRAW);
		return vbo;
	}
	
	private static void unbindVao(){
		glBindVertexArray(0);
	}
	
	public static void deleteAll(){
		UtilL.doAndClear(MODELS, IModel::delete);
	}
	
}
