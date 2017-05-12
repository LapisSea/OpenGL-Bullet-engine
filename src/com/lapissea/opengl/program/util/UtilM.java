package com.lapissea.opengl.program.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.lwjgl.util.vector.Matrix4f;

import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.lapissea.opengl.program.core.Globals;

public class UtilM{
	
	public static final double	SQRT2D	=Math.sqrt(2);
	public static final float	SQRT2F	=(float)SQRT2D;
	
	public static String toString(Object...objs){
		StringBuilder print=new StringBuilder();
		
		if(objs!=null) for(int i=0;i<objs.length;i++){
			Object a=objs[i];
			if(isArray(a)) print.append(unknownArrayToString(a));
			else if(a instanceof FloatBuffer) print.append(floatBufferToString((FloatBuffer)a));
			else print.append(toString(a)+(i==objs.length-1?"":" "));
		}
		else print.append("null");
		
		return print.toString();
	}
	
	public static String toString(Object obj){
		
		if(obj instanceof Matrix4f){
			Matrix4f mat=(Matrix4f)obj;
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
			
		}
		
		StringBuilder print=new StringBuilder();
		
		if(obj!=null){
			if(isArray(obj)) print.append(unknownArrayToString(obj));
			else if(obj instanceof FloatBuffer) print.append(floatBufferToString((FloatBuffer)obj));
			else print.append(obj.toString());
		}
		else print.append("null");
		
		return print.toString();
	}
	
	public static boolean isArray(Object object){
		return object!=null&&object.getClass().isArray();
	}
	
	public static String floatBufferToString(FloatBuffer buff){
		StringBuilder print=new StringBuilder("Buffer{");
		
		buff=buff.duplicate();
		buff.limit(buff.capacity());
		if(buff.capacity()>0){
			int j=0;
			print.append(buff.get(j));
			for(j=1;j<buff.capacity();j++)
				print.append(", ").append(buff.get(j));
		}
		print.append('}');
		return print.toString();
	}
	
	private static String unknownArrayToString(Object arr){
		if(arr instanceof boolean[]) return Arrays.toString((boolean[])arr);
		if(arr instanceof float[]) return Arrays.toString((float[])arr);
		if(arr instanceof byte[]) return Arrays.toString((byte[])arr);
		if(arr instanceof int[]) return Arrays.toString((int[])arr);
		if(arr instanceof long[]) return Arrays.toString((long[])arr);
		if(arr instanceof short[]) return Arrays.toString((short[])arr);
		if(arr instanceof char[]) return Arrays.toString((char[])arr);
		if(arr instanceof double[]) return Arrays.toString((double[])arr);
		if(arr instanceof Object[]) return Arrays.toString((Object[])arr);
		return "ERR: "+arr;
	}
	
	public static boolean TRUE(){
		return true;
	}
	
	public static void sleep(long millis){
		try{
			Thread.sleep(millis);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void sleep(long millis, int nanos){
		try{
			Thread.sleep(millis, nanos);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String getTxtResource(String name){
		try(InputStream is=getResource(name)){
			
			if(is==null) return null;
			
			ByteArrayOutputStream buffer=new ByteArrayOutputStream();
			try{
				int nRead;
				byte[] data=new byte[16384];
				while((nRead=is.read(data, 0, data.length))!=-1){
					buffer.write(data, 0, nRead);
				}
				buffer.flush();
			}catch(IOException e){
				e.printStackTrace();
			}
			return new String(buffer.toByteArray());
		}catch(IOException e1){
			return null;
		}
		
	}
	
	public static InputStream getResource(String...names){
		for(String name:names){
			InputStream s=getResource(name);
			if(s!=null) return s;
		}
		return null;
	}
	
	private static final Map<String,String[]>	FOLDER_CASH	=new HashMap<>();
	private static final List<String>			FOLDER_NULL	=new ArrayList<>();
	
	public static String[] getResourceFolderContent(String name, Predicate<String> filter){
		String[] result=getResourceFolderContent(name);
		if(result==null) return null;
		return Arrays.stream(result).filter(filter).toArray(String[]::new);
	}
	
	public static String[] getResourceFolderContent(String name){
		String name0=name.replaceAll("[\\\\/ ]*$|[\\\\/]+", "/");
		Optional<Entry<String,String[]>> a=FOLDER_CASH.entrySet().stream().filter(e->e.getKey().equals(name0)).findFirst();
		if(a.isPresent()) return a.get().getValue();
		if(FOLDER_NULL.contains(name0)) return null;
		
		if(Globals.DEV_ENV) return new File("res/"+name0).list();
		
		JarFile jar=Globals.getJarFile();
		List<String> list=new ArrayList<>();
		stream(jar.entries())
				.filter(e->e.getName().length()>name0.length()&&e.getName().startsWith(name0))
				.forEach(e->list.add(e.getName().substring(name0.length())));
		closeSilenty(jar);
		
		if(list.isEmpty()){
			FOLDER_NULL.add(name0);
			return null;
		}
		String[] result=array(list);
		FOLDER_CASH.put(name0, result);
		
		return result;
	}
	
	public static <T> Stream<T> stream(Enumeration<T> e){
		return StreamSupport.stream(
				new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED){
					
					@Override
					public boolean tryAdvance(Consumer<? super T> action){
						if(e.hasMoreElements()){
							action.accept(e.nextElement());
							return true;
						}
						return false;
					}
					
					@Override
					public void forEachRemaining(Consumer<? super T> action){
						while(e.hasMoreElements()){
							action.accept(e.nextElement());
						}
					}
				}, false);
	}
	
	public static <T> T[] array(List<T> list){
		if(list.isEmpty()) return null;
		
		@SuppressWarnings("unchecked")
		T[] a=(T[])UtilM.array(list.get(0).getClass(), list.size());
		return list.toArray(a);
	}
	
	public static InputStream getResource(String name){
		if(Globals.DEV_ENV) name="res/"+name;
		
		InputStream src=UtilM.class.getResourceAsStream("/"+name);
		if(src==null) try{
			src=Files.newInputStream(new File(name).toPath());
		}catch(IOException e){}
		return src;
	}
	
	public static <K,V> void doAndClear(Map<K,V> collection, BiConsumer<K,V> toDo){
		if(collection.isEmpty()) return;
		collection.forEach(toDo);
		collection.clear();
	}
	
	public static <T> void doAndClear(Collection<T> collection, Consumer<T> toDo){
		if(collection.isEmpty()) return;
		collection.stream().forEach(toDo);
		collection.clear();
	}
	
	public static void startDaemonThread(Runnable run, String name){
		Thread t=new Thread(run, name);
		t.setDaemon(true);
		t.start();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] array(Class<T> componentType, int length){
		return (T[])Array.newInstance(componentType, length);
	}
	
	public static boolean instanceOf(Class<?> left, Class<?> right){
		if(left==right) return true;
		try{
			left.asSubclass(right);
			return true;
		}catch(Exception ignored){}
		return false;
	}
	
	public static boolean instanceOf(Object left, Class<?> right){
		return left!=null&&instanceOf(left.getClass(), right);
	}
	
	public static boolean instanceOf(Class<?> left, Object right){
		return instanceOf(left, right.getClass());
	}
	
	public static boolean instanceOf(Object left, Object right){
		return instanceOf(left.getClass(), right.getClass());
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
	
	public static Serializable fromString(String s){
		//		byte[] data=Base64.getDecoder().decode(s);
		byte[] data=s.getBytes();
		Object o=null;
		try{
			ObjectInputStream ois=new ObjectInputStream(new ByteArrayInputStream(data));
			try{
				o=ois.readObject();
			}catch(ClassNotFoundException e){
				e.printStackTrace();
			}
			ois.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return (Serializable)o;
	}
	
	public static String toString(Serializable o) throws IOException{
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		ObjectOutputStream oos=new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();
		//		return Base64.getEncoder().encodeToString(baos.toByteArray());
		return new String(baos.toByteArray());
	}
	
	public static void closeSilenty(Closeable closeable){
		try{
			closeable.close();
		}catch(IOException e){}
	}
}
