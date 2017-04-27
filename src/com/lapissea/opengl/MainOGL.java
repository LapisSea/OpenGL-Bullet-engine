package com.lapissea.opengl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.function.Function;

import org.json.JSONException;
import org.json.JSONObject;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.gl.Window;
import com.lapissea.opengl.program.rendering.gl.model.ModelLoader;
import com.lapissea.opengl.program.util.ConfigHandler;
import com.lapissea.opengl.program.util.LogUtil;
import com.lapissea.opengl.program.util.math.vec.Vec2i;

public class MainOGL{
	
	public static enum OS{
		WINDOWS(s->s.contains("win")),
		UNIX(s->s.contains("nix")||s.contains("nux")||s.contains("aix")),
		MACOSX(s->s.contains("mac")),
		SOLARIS(s->s.contains("sunos"));
		
		public final Function<String,Boolean> detector;
		
		private OS(Function<String,Boolean> detector){
			this.detector=detector;
		}
		
	}
	
	public static final boolean	DEV_ENV;
	public static final OS		ACTIVE_OS;
	
	static{
		LogUtil.printWrapped("=====PRE_GAME_INIT=====");
		File path=null;
		try{
			path=new File(URLDecoder.decode(MainOGL.class.getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-16"));
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		if(path==null){
			DEV_ENV=false;
			LogUtil.println("Source path failed to be processed! Assuming user environment...");
		}
		else{
			DEV_ENV=!path.isFile();
			LogUtil.println("Running "+(DEV_ENV?"development":"user")+" environment!");
		}
		
		if(DEV_ENV){
			LogUtil.println("Clearing old compiled shaders...");
			File f=new File("res/shaders/compiled output");
			for(File fil:f.listFiles()){
				fil.delete();
			}
			LogUtil.println("Done!");
		}
		else if(path!=null){
			path=path.getParentFile();
			String pth=path.toString(),dir=System.getProperty("user.dir");
			if(!pth.equals(dir)){
				LogUtil.println("Changing root folder from:\n"+dir+"\nto\n"+pth);
				System.setProperty("user.dir", pth);
			}
		}
		String osName=System.getProperty("os.name").toLowerCase();
		
		LogUtil.println("Detecting supported OS...");
		ACTIVE_OS=Arrays.stream(OS.values())
				.filter(os->os.detector.apply(osName))
				.findFirst()
				.orElseThrow(()->new UnsupportedOperationException("Sorry, os with name "+osName+" is not supported or could not be identified. :("));
		LogUtil.println("OS named \""+osName+"\" has been identified as", ACTIVE_OS, "\nDone!");
		
		System.setProperty("java.library.path", "natives/"+ACTIVE_OS.toString().toLowerCase()+";"+System.getProperty("java.library.path"));
		try{
			Field fieldSysPath=ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		}catch(Exception e){
			e.printStackTrace();
		}
		LogUtil.printWrapped("=======GAME_INIT=======");
	}
	
	public static void main(String[] args){
		
		Thread.setDefaultUncaughtExceptionHandler((t, e)->{
			e.printStackTrace();
			System.exit(1);
		});
		
		try{
			JSONObject winCfg=ConfigHandler.getS("win_startup", ()->"{'pos':{'x':-1,'y':-1},'full-sc':false,'size':{'w':600,'h':400}}".replaceAll("'", "\""));
			Thread.currentThread().setName("Render");
			try{
				Window.init(new Vec2i(winCfg.getJSONObject("pos")), new Vec2i(winCfg.getJSONObject("size")), "LWJGL 2 game", winCfg.getBoolean("full-sc"));
			}catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			}
			Game.get().start();
			
			ModelLoader.deleteAll();
			
			Window.SIZE.putWH(winCfg.getJSONObject("size"));
			Window.POS.putXY(winCfg.getJSONObject("pos"));
			
			Window.closeWindow();
			
			LogUtil.println(winCfg);
			ConfigHandler.set("win_startup", winCfg);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
	}
}
