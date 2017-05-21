package com.lapissea.opengl.launch;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.util.LogUtil;
import com.lapissea.opengl.program.util.OperatingSystem;
import com.lapissea.opengl.program.util.SingleInstanceProgram;
import com.lapissea.opengl.program.util.config.Config;
import com.lapissea.opengl.program.util.config.configs.WindowConfig;
import com.lapissea.opengl.window.api.IGLWindow;
import com.lapissea.opengl.window.api.ILWJGLCtx;
import com.lapissea.opengl.window.impl.LWJGL2Ctx;

public class Launch{
	
	
	public static void main(String[] args){
		try{
			LogUtil.__.INJECT_FILE_LOG(OperatingSystem.APP_DATA+"/OpenGL engine/log.txt");
			SingleInstanceProgram.check();
			//LogUtil.__.INJECT_EXTERNAL_PRINT("dev_log");
			LogUtil.__.INJECT_DEBUG_PRINT(true);
			
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
			try{
				window.setPos(winCfg.position);
				window.setSize(winCfg.size);
				
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
		}catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
}
