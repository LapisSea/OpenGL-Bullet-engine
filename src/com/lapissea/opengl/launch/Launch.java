package com.lapissea.opengl.launch;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.core.GameSettings;
import com.lapissea.opengl.program.util.OperatingSystem;
import com.lapissea.opengl.program.util.config.Config;
import com.lapissea.opengl.program.util.config.configs.WindowConfig;
import com.lapissea.opengl.window.api.IGLWindow;
import com.lapissea.opengl.window.api.ILWJGLCtx;
import com.lapissea.opengl.window.impl.LWJGL2Ctx;
import com.lapissea.splashscreen.SplashScreenHost;
import com.lapissea.util.LogUtil;

public class Launch{
	
	
	public static void start(String[] args) throws Exception{
		SplashScreenHost.open("Splash.jar", "LWJGL 2 game");
		Thread.sleep(1);
		SplashScreenHost.sendMsg("Starting...");
		NativeSetUp.haxNatives();
		SplashScreenHost.sendMsg("Injected natves!");
		SplashScreenHost.sendPercent(0.1F);
		
		//		try{
		//			new Test().lel();
		//		}catch(Throwable e){
		//			e.printStackTrace();
		//		}
		//		System.exit(0);
		
		try{
			LogUtil.__.INJECT_FILE_LOG(OperatingSystem.APP_DATA+"/OpenGL engine/log.txt");
			//LogUtil.__.INJECT_EXTERNAL_PRINT("dev_log");
			LogUtil.__.INJECT_DEBUG_PRINT(true);
			
			
			SplashScreenHost.sendMsg("Injected logger!");
			SplashScreenHost.sendPercent(0.3F);
			
			//			TransformerAsmPoll.register();
			//			try{
			//				new Test().lel();
			//			}catch(Exception e){
			//				e.printStackTrace();
			//			}
			//			System.exit(0);
			
			LogUtil.printWrapped("=====PRE_GAME_INIT=====");
			
			LogUtil.println("OS named \""+OperatingSystem.OS_NAME+"\" has been identified as", OperatingSystem.ACTIVE_OS);
			
			LogUtil.printWrapped("=======GAME_INIT=======");
			
			Thread.setDefaultUncaughtExceptionHandler((t, e)->{
				e.printStackTrace();
				System.exit(1);
			});
			
			Thread.currentThread().setName("Render");
			
			ILWJGLCtx glCtx=new LWJGL2Ctx();
			Game.createGame(glCtx);
			
			IGLWindow window=glCtx.getCtxWindow();
			WindowConfig winCfg=Config.getConfig(WindowConfig.class, "win_startup");
			GameSettings s=Config.getConfig(GameSettings.class, "game_settings");
			s.save();
			SplashScreenHost.sendMsg("Initalised game base!");
			SplashScreenHost.sendPercent(0.4F);
			try{
				SplashScreenHost.sendMsg("Creating window...");
				
				//window.setTitle("Genine and Lee");
				window.setPos(-10000, -10000).setSize(1, 1);
				window.setTitle("The abandoned");
				window.setFullScreen(false);
				window.setResizable(true);
				window.setVSync(true);
				window.setPos(winCfg.position);
				window.setSize(winCfg.size);
				glCtx.init();
				SplashScreenHost.sendMsg("Window created!");
				SplashScreenHost.sendPercent(0.7F);
				
			}catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			}
			
			Game.get().start();
			if(window.getPos().x()!=-10000){
				winCfg.size.set(window.getSize());
				winCfg.position.set(window.getPos());
			}
			glCtx.destroy();
			
			winCfg.save();
			s.save();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
}
