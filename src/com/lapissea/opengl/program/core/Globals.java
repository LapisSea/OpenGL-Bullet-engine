package com.lapissea.opengl.program.core;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.jar.JarFile;

import com.lapissea.opengl.launch.GameStart;
import com.lapissea.opengl.program.util.LogUtil;

public class Globals{
	
	public static final File	SOURCE_LOCATION;
	public static final boolean	DEV_ENV;
	
	static{
		String path=GameStart.class.getResource(GameStart.class.getSimpleName()+".class").getFile();
		if(path.startsWith("/")) SOURCE_LOCATION=new File("").getAbsoluteFile();
		else{
			try{
				path=URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(path).getFile(), "UTF-8");
			}catch(UnsupportedEncodingException e){
				e.printStackTrace();
				System.exit(-1);
			}
			SOURCE_LOCATION=new File(path.substring(path.startsWith("file:")?6:0, path.lastIndexOf('!')));
		}
		
		DEV_ENV=!SOURCE_LOCATION.isFile();
		
		if(DEV_ENV){
			LogUtil.println("Clearing old compiled shaders...");
			File f=new File("res/shaders/compiled output");
			if(!f.exists()||!f.isDirectory()) f.mkdirs();
			for(File fil:f.listFiles()){
				fil.delete();
			}
			LogUtil.println("Done clearing!");
		}
		else{
			String pth=SOURCE_LOCATION.getParentFile().toString();
			if(!pth.equals(System.getProperty("user.dir"))) System.setProperty("user.dir", pth);
		}
		
		LogUtil.println("Running "+(DEV_ENV?"development":"user")+" environment!");
		
	}
	
	public static JarFile getJarFile(){
		try{
			return new JarFile(SOURCE_LOCATION);
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void load(){}
}
