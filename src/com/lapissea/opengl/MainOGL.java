package com.lapissea.opengl;

import java.lang.reflect.Field;

import org.json.JSONException;
import org.json.JSONObject;

import com.lapissea.opengl.abstr.opengl.IGLWindow;
import com.lapissea.opengl.abstr.opengl.ILWJGLCtx;
import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.core.Globals;
import com.lapissea.opengl.program.opengl.LWJGL2Ctx;
import com.lapissea.opengl.program.util.ConfigHandler;
import com.lapissea.opengl.program.util.LogUtil;
import com.lapissea.opengl.program.util.math.vec.Vec2i;

public class MainOGL{
	
	public static void main(String[] args) throws Exception{
		
		LogUtil.printWrapped("=====PRE_GAME_INIT=====");
		
		LogUtil.println("OS named \""+Globals.OS_NAME+"\" has been identified as", Globals.ACTIVE_OS);
		
		LogUtil.println("Using dank hax to set natives...");
		System.setProperty("java.library.path", "natives/"+Globals.ACTIVE_OS.toString().toLowerCase()+";"+System.getProperty("java.library.path"));
		try{
			Field fieldSysPath=ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
			LogUtil.println("Done haxing!");
		}catch(Exception e){
			LogUtil.println("Hax are not dank!");
			e.printStackTrace();
		}
		
		LogUtil.printWrapped("=======GAME_INIT=======");
		
		Thread.setDefaultUncaughtExceptionHandler((t, e)->{
			e.printStackTrace();
			System.exit(1);
		});
		
		try{
			JSONObject winCfg=ConfigHandler.getS("win_startup", ()->"{'pos':{'x':-1,'y':-1},'full-sc':false,'size':{'w':600,'h':400}}".replaceAll("'", "\""));
			Thread.currentThread().setName("Render");
			
			ILWJGLCtx glCtx=new LWJGL2Ctx();
			Game.createGame(glCtx);
			
			IGLWindow window=glCtx.getCtxWindow();
			try{
				
				window.setPos(new Vec2i(winCfg.getJSONObject("pos")));
				window.setSize(new Vec2i(winCfg.getJSONObject("size")));
				window.setTitle("LWJGL 2 game");
				window.setFullScreen(winCfg.getBoolean("full-sc"));
				glCtx.init();
				
			}catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			}
			Game.get().start();
			
			window.getSize().putWH(winCfg.getJSONObject("size"));
			window.getPos().putXY(winCfg.getJSONObject("pos"));
			
			glCtx.destroy();
			ConfigHandler.set("win_startup", winCfg);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
	}
}
