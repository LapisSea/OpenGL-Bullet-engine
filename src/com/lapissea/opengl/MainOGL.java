package com.lapissea.opengl;

import java.lang.reflect.Field;

import com.lapissea.opengl.abstr.opengl.IGLWindow;
import com.lapissea.opengl.abstr.opengl.ILWJGLCtx;
import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.core.Globals;
import com.lapissea.opengl.program.opengl.LWJGL2Ctx;
import com.lapissea.opengl.program.util.LogUtil;
import com.lapissea.opengl.program.util.config.Config;
import com.lapissea.opengl.program.util.config.configs.WindowConfig;

public class MainOGL{
	
	public static void main(String[] args) throws Exception{
		LogUtil.__.INJECT_EXTERNAL_PRINT("dev_log");
		LogUtil.__.INJECT_DEBUG_PRINT(true);
		
		//		while(UtilM.TRUE()){
		//			UtilM.sleep(500);
		//			LogUtil.clear();
		//			LogUtil.println("asd26");
		//			LogUtil.println("asd27");
		//			LogUtil.printlnEr("asd28");
		//			LogUtil.println("29\n\n");
		//		}
		
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
		
		// ()->"{'pos':{'x':-1,'y':-1},'full-sc':false, 'v-sync':true,'size':{'w':600,'h':400}}".replaceAll("'", "\"")
		Thread.currentThread().setName("Render");
		
		ILWJGLCtx glCtx=new LWJGL2Ctx();
		Game.createGame(glCtx);
		
		IGLWindow window=glCtx.getCtxWindow();
		WindowConfig winCfg=Config.getConfig(WindowConfig.class, "win_startup");
		try{
			window.setPos(winCfg.position);
			LogUtil.println(window.getPos());
			window.setSize(winCfg.size);
			LogUtil.println(window.getSize());
			
			window.setTitle("LWJGL 2 game");
			window.setFullScreen(false);
			window.setResizable(true);
			window.setVSync(true);
			glCtx.init();
			
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		Game.get().start();
		
		winCfg.size.set(window.getSize());
		winCfg.position.set(window.getPos());
		
		glCtx.destroy();
		
		winCfg.save();
		
	}
}
