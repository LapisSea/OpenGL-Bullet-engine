package com.lapissea.opengl.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import org.lwjgl.util.vector.Matrix4f;

import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.lapissea.opengl.core.Globals;
import com.lapissea.opengl.util.function.Predicates;
import com.lapissea.opengl.window.api.util.MathUtil;
import com.lapissea.util.UtilL;

public class UtilM extends UtilL{
	
	static{
		__REGISTER_CUSTOM_TO_STRING(Matrix4f.class, mat->{
			String m00=Float.toString(mat.m00),m01=Float.toString(mat.m01),m02=Float.toString(mat.m02),m03=Float.toString(mat.m03);
			String m10=Float.toString(mat.m10),m11=Float.toString(mat.m11),m12=Float.toString(mat.m12),m13=Float.toString(mat.m13);
			String m20=Float.toString(mat.m20),m21=Float.toString(mat.m21),m22=Float.toString(mat.m22),m23=Float.toString(mat.m23);
			String m30=Float.toString(mat.m30),m31=Float.toString(mat.m31),m32=Float.toString(mat.m32),m33=Float.toString(mat.m33);
			int m0=MathUtil.max(m00.length(), m10.length(), m20.length(), m30.length());
			int m1=MathUtil.max(m01.length(), m11.length(), m21.length(), m31.length());
			int m2=MathUtil.max(m02.length(), m12.length(), m22.length(), m32.length());
			int m3=0;
			
			StringBuilder result=new StringBuilder();
			
			BiConsumer<String,Integer> add=(s, l)->{
				boolean neg=s.startsWith("-");
				if(!neg) result.append(' ');
				result.append(s);
				for(int i=0, j=l+(neg?2:1)-s.length();i<j;i++){
					result.append(' ');
				}
			};
			result.append('/');
			add.accept(m00, m0);
			add.accept(m01, m1);
			add.accept(m02, m2);
			add.accept(m03, m3);
			result.append("\\\n|");
			add.accept(m10, m0);
			add.accept(m11, m1);
			add.accept(m12, m2);
			add.accept(m13, m3);
			result.append("|\n|");
			add.accept(m20, m0);
			add.accept(m21, m1);
			add.accept(m22, m2);
			add.accept(m23, m3);
			result.append("|\n\\");
			add.accept(m30, m0);
			add.accept(m31, m1);
			add.accept(m32, m2);
			add.accept(m33, m3);
			result.append("/");
			
			return result.toString();
		});
	}
	
	public static InputStream getResource(String...names){
		for(String name:names){
			InputStream s=getResource(name);
			if(s!=null) return s;
		}
		return null;
	}
	
	public static String getTxtResource(String name){
		try(InputStream is=getResource(name)){
			
			if(is==null) return null;
			return new String(readAll(is));
			
		}catch(IOException e1){
			return null;
		}
	}
	
	public static InputStream getResource(String name){
		if(Globals.DEV_ENV){
			name="res/"+name;
			try{
				return Files.newInputStream(new File(name).toPath());
			}catch(IOException e){
				return null;
			}
		}
		
		return UtilL.class.getResourceAsStream("/"+name);
	}
	
	private static final Map<String,String[]>	FOLDER_CASH	=new HashMap<>();
	private static final Set<String>			FOLDER_NULL	=new HashSet<>();
	
	public static List<String> getResourceFolderContentList(String name){
		List<String> names=new ArrayList<>();
		if(getResourceFolderContent(name, (Consumer<String>)names::add)==-1) return null;
		return names;
	}
	
	public static List<String> getResourceFolderContentList(String name, Predicate<String> filter){
		List<String> names=new ArrayList<>();
		if(getResourceFolderContent(name, filter, names::add)==-1) return null;
		return names;
	}
	
	public static int getResourceFolderContent(String name, Consumer<String> consumer){
		return getResourceFolderContent(name, Predicates.TRUE(), consumer);
	}
	
	public static Stream<String> getResourceFolderContentStream(String name){
		String[] ar=getResourceFolderContent0(name);
		return ar==null?null:Arrays.stream(ar);
	}
	
	public static int getResourceFolderContent(String name, Predicate<String> filter, Consumer<String> consumer){
		String[] ar=getResourceFolderContent0(name);
		if(ar==null) return -1;
		int count=0;
		for(String nam:ar){
			if(filter.test(nam)){
				consumer.accept(nam);
				count++;
			}
		}
		return count;
	}
	
	private static String[] getResourceFolderContent0(String name){
		String name0=(name+"/").replaceAll("[\\\\/ ]+$|[\\\\/]+", "/"),
				namesCash[]=FOLDER_CASH.get(name0);
		
		if(namesCash!=null) return namesCash;
		if(FOLDER_NULL.contains(name0)) return null;
		
		String[] names;
		
		if(Globals.DEV_ENV){
			names=new File("res/"+name0).list();
			if(names==null){
				FOLDER_NULL.add(name0);
				return null;
			}
		}else{
			JarFile jar=Globals.getJarFile();
			names=stream(jar.entries()).filter(e->e.getName().startsWith(name0)).toArray(String[]::new);
			if(names.length==0){
				FOLDER_NULL.add(name0);
				return null;
			}
			closeSilenty(jar);
		}
		
		FOLDER_CASH.put(name0, names);
		return names;
	}
	
	private static final List<Class<?>> WRAPPER_TYPES=new ArrayList<>();
	static{
		WRAPPER_TYPES.add(Boolean.class);
		WRAPPER_TYPES.add(Character.class);
		WRAPPER_TYPES.add(Byte.class);
		WRAPPER_TYPES.add(Short.class);
		WRAPPER_TYPES.add(Integer.class);
		WRAPPER_TYPES.add(Long.class);
		WRAPPER_TYPES.add(Float.class);
		WRAPPER_TYPES.add(Double.class);
		WRAPPER_TYPES.add(Void.class);
	}
	
	public static boolean isWrapperType(Object type){
		return isWrapperType(type.getClass());
	}
	
	public static boolean isWrapperType(Class<?> type){
		return WRAPPER_TYPES.contains(type);
	}
	
	public static TriangleIndexVertexArray verticesToPhysicsMesh(float[] vts){
		int[] ids=new int[vts.length/3];
		for(int i=0;i<ids.length;i++){
			ids[i]=i;
		}
		
		return verticesToPhysicsMesh(vts, ids);
	}
	
	public static TriangleIndexVertexArray verticesToPhysicsMesh(float[] vts, int[] ids){
		
		IndexedMesh indexedMesh=new IndexedMesh();
		indexedMesh.numTriangles=ids.length/3;
		indexedMesh.triangleIndexBase=ByteBuffer.allocateDirect(ids.length*4).order(ByteOrder.nativeOrder());
		indexedMesh.triangleIndexBase.asIntBuffer().put(ids);
		indexedMesh.triangleIndexStride=3*4;
		indexedMesh.numVertices=vts.length;
		indexedMesh.vertexBase=ByteBuffer.allocateDirect(vts.length*4).order(ByteOrder.nativeOrder());
		indexedMesh.vertexBase.asFloatBuffer().put(vts);
		indexedMesh.vertexStride=3*4;
		
		TriangleIndexVertexArray vertArray=new TriangleIndexVertexArray();
		vertArray.addIndexedMesh(indexedMesh);
		return vertArray;
	}
}
