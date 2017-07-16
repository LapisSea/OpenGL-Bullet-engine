package com.lapissea.opengl.program.util.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.json.JSONObject;

import com.lapissea.opengl.program.util.OperatingSystem;
import com.lapissea.util.LogUtil;

/**
 * Usage: folder1.folder2.filename:keyname.keyname = file at "config/folder1/folder2/filename.cfg" with value inside keyname.keyname
 */
public class Config{
	
	private static class Node{
		
		Object obj;
		
		@Override
		public String toString(){
			return Objects.toString(obj);
		}
	}
	
	private static class FileCfg{
		
		Map<String,Node> data=new HashMap<>();
		
		final String path;
		
		boolean dirty=true;
		
		public FileCfg(String path){
			this.path=path;
			path=OperatingSystem.APP_DATA+"/OpenGL engine/config/"+path;
			try{
				add("", new JSONObject(new String(Files.readAllBytes(new File(path).toPath()))));
			}catch(NoSuchFileException e){}catch(Exception e){
				e.printStackTrace();
			}
			
			
		}
		
		private boolean dirty(){
			return dirty;
		}
		
		private void add(String path, JSONObject jsonObject){
			for(String key:jsonObject.keySet()){
				
				String p=path.isEmpty()?key:path+"."+key;
				Object o=jsonObject.get(key);
				
				if(o instanceof JSONObject){
					add(p, (JSONObject)o);
					continue;
				}
				
				Node n=get(p);
				if(n==null) data.put(p, n=new Node());
				
				n.obj=o;
			}
		}
		
		Node get(String name){
			return data.get(name);
		}
		
	}
	
	private static Map<String,FileCfg> DATA=new HashMap<>();
	
	public static void set(String path, Object obj){
		int split=path.indexOf(":");
		if(split<1) throw new IllegalArgumentException("config path was not defined!");
		
		String f=path.substring(0, split)+".cfg";
		FileCfg file=DATA.get(f);
		if(file==null) DATA.put(f, file=new FileCfg(f));
		String name=path.substring(split+1);
		Node n=file.get(name);
		if(n==null) file.data.put(name, n=new Node());
		
		n.obj=obj;
		file.dirty=true;
	}
	
	public static boolean getBool(String path, boolean onNull){
		Object o=get(path);
		return o==null?onNull:(boolean)o;
	}
	
	public static int getInt(String path, int onNull){
		Object o=get(path);
		return o==null?onNull:(int)o;
	}
	
	public static float getFloat(String path, float onNull){
		Object o=get(path);
		return o==null?onNull:(float)o;
	}
	
	public static Supplier<Object> getDirect(String path){
		Node node=getN(path);
		return ()->node==null?null:node.obj;
	}
	
	public static Object get(String path){
		return get(path, Object.class);
	}
	
	public static <T> T get(String path, Class<T> type){
		return get(path, null, type);
	}
	
	public static Object get(String path, Object onNull){
		return get(path, onNull, Object.class);
	}
	
	public static <T> T get(String path, T onNull, Class<T> type){
		Node node=getN(path);
		if(node==null) return onNull;
		return (T)node.obj;
	}
	
	private static Node getN(String path){
		
		int split=path.indexOf(":");
		if(split<1) throw new IllegalArgumentException("config path was not defined!");
		
		String f=path.substring(0, split)+".cfg";
		FileCfg file=DATA.get(f);
		if(file==null) file=new FileCfg(f);
		
		return file.get(path.substring(split+1));
	}
	
	public static void save(){
		DATA.values().stream().filter(FileCfg::dirty).forEach(f->{
			f.dirty=false;
			JSONObject obj=new JSONObject();
			f.data.forEach((k, v)->{
				JSONObject target=obj;
				String[] parts=k.split(".");
				for(int i=0,j=parts.length-1;i<j;i++){
					String part=parts[i];
					LogUtil.println(part);
					if(target.has(part)){
						
					}
				}
				obj.put(k, v.obj);
			});
			try{
				File fil=new File(OperatingSystem.APP_DATA+"/OpenGL engine/config/"+f.path);
				if(!fil.getParentFile().exists()) fil.getParentFile().mkdirs();
				if(!fil.exists()) fil.createNewFile();
				Files.write(fil.toPath(), obj.toString(4).getBytes());
			}catch(Exception e){
				e.printStackTrace();
			}
		});
	}
}
