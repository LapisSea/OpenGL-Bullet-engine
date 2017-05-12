package com.lapissea.opengl.program.core;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;
import java.util.jar.JarFile;

import com.lapissea.opengl.program.util.LogUtil;

public class Globals{
	
	public static enum EnumOS{
		WINDOWS(s->s.contains("win")),
		LINUX(s->s.contains("linux")||s.contains("unix")),
		MACOSX(s->s.contains("mac")),
		SOLARIS(s->s.contains("solaris")||s.contains("sunos")),
		FREEBDS(s->s.contains("freebsd")),
		OPENBDS(s->s.contains("openbds"));
		
		public final Function<String,Boolean> detector;
		
		private EnumOS(Function<String,Boolean> detector){
			this.detector=detector;
		}
		
	}
	
	public static final String	OS_NAME			=System.getProperty("os.name").toLowerCase(Locale.ROOT);
	public static final EnumOS	ACTIVE_OS		=Arrays.stream(EnumOS.values()).filter(os->os.detector.apply(OS_NAME)).findFirst().orElseThrow(()->new UnsupportedOperationException("Sorry, os with name "+OS_NAME+" is not supported or could not be identified. :("));
	public static final File	SOURCE_LOCATION	=new File(Globals.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	public static final boolean	DEV_ENV			=!SOURCE_LOCATION.isFile();
	
	static{
		
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
