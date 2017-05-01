package com.lapissea.opengl.program.core;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;

import com.lapissea.opengl.MainOGL;
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
	
	public static final String	OS_NAME		=System.getProperty("os.name").toLowerCase(Locale.ROOT);
	public static final EnumOS	ACTIVE_OS	=Arrays.stream(EnumOS.values()).filter(os->os.detector.apply(OS_NAME)).findFirst().orElseThrow(()->new UnsupportedOperationException("Sorry, os with name "+OS_NAME+" is not supported or could not be identified. :("));
	public static final boolean	DEV_ENV;
	
	static{
		
		File path=null;
		try{
			path=new File(URLDecoder.decode(MainOGL.class.getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-16"));
		}catch(UnsupportedEncodingException e){}
		
		if(path==null){
			DEV_ENV=false;
			LogUtil.println("Source path failed to be processed! Assuming user environment...");
		}
		else{
			if(DEV_ENV=!path.isFile()){
				LogUtil.println("Clearing old compiled shaders...");
				File f=new File("res/shaders/compiled output");
				if(!f.exists()||!f.isDirectory()) f.mkdirs();
				for(File fil:f.listFiles()){
					fil.delete();
				}
				LogUtil.println("Done clearing!");
			}
			LogUtil.println("Running "+(DEV_ENV?"development":"user")+" environment!");
		}
		
		if(!DEV_ENV&&path!=null){
			String pth=path.getParentFile().toString();
			if(!pth.equals(System.getProperty("user.dir"))) System.setProperty("user.dir", pth);
		}
	}
	
	public static void load(){}
}
