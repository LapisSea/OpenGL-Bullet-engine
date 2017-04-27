package com.lapissea.opengl.program.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfigHandler{
	
	private static final String CONFIG_ROOT="config";
	
	public static JSONObject getS(String name, Supplier<String> defaultData){
		return getO(name, ()->{
			try{
				return new JSONObject(defaultData.get());
			}catch(JSONException e){
				throw new RuntimeException(e);
			}
		});
	}
	
	public static JSONObject getO(String name, Supplier<JSONObject> defaultData){
		File file=file(name);
		JSONObject def=defaultData.get();
		if(!file.exists()||!file.isFile()){
			set(name, def);
			return def;
		}
		JSONObject conf;
		String org;
		try{
			org=new String(Files.readAllBytes(file.toPath()));
		}catch(IOException e3){
			e3.printStackTrace();
			org="{}";
		}
		try{
			conf=new JSONObject(org);
		}catch(JSONException e2){
			e2.printStackTrace();
			return def;
		}
		
		UtilM.forEach(conf, (key,value)->{
			try{
				def.get(key);
			}catch(JSONException e){
				conf.remove(key);
			}
		});
		UtilM.forEach(def, (key,value)->{
			try{
				conf.get(key);
			}catch(JSONException e){
				try{
					conf.put(key, value);
				}catch(JSONException e1){
					e1.printStackTrace();
				}
			}
		});
		try{
			String now=conf.toString(4);
			if(!now.equals(org))set(name, now);
		}catch(JSONException e){}
		
		return conf;
	}
	
	public static void set(String name, JSONObject data){
		try{
			set(name, data.toString(4));
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	public static void set(String name, String data){
		new File("config").mkdir();
		try{
			Files.write(path(name), data.getBytes());
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private static String string(String name){
		return CONFIG_ROOT+"/"+name+".cf";
	}
	
	private static File file(String name){
		return new File(string(name));
	}
	
	private static Path path(String name){
		return file(name).toPath();
	}
	
}
